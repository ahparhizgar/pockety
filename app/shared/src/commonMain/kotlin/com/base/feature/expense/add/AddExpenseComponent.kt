package com.base.feature.expense.add

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CandidateExpenseRepository
import com.base.db.CandidateExpenseStatus
import com.base.db.ExpenseRepository
import com.base.feature.root.TopLevelDestinationConfig
import com.base.usermessage.UiString
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.error_empty_description
import pockety.app.shared.generated.resources.error_invalid_amount

data class AddExpenseState(
    val amountText: String = "",
    val descriptionText: String = "",
    val amountError: UiString? = null,
    val descriptionError: UiString? = null,
    val isSaving: Boolean = false,
)

interface AddExpenseComponent : TopLevelDestinationConfig {
  val state: Value<AddExpenseState>

  fun onAmountChanged(amount: String)

  fun onDescriptionChanged(description: String)

  fun onSaveClicked(): Job

  fun onCancelClicked()

  @Serializable
  data class Config(
      val initialAmount: String = "",
      val candidateExpenseId: Long? = null,
  ) : TopLevelDestinationConfig
}

interface AddExpenseComponentFactory {
  fun create(
      cContext: CContext,
      config: AddExpenseComponent.Config = AddExpenseComponent.Config(),
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseComponent
}

class DefaultAddExpenseComponent(
    cContext: CContext,
    private val expenseRepository: ExpenseRepository,
    private val candidateExpenseRepository: CandidateExpenseRepository,
    private val config: AddExpenseComponent.Config = AddExpenseComponent.Config(),
    private val onCompleted: () -> Unit,
    private val onCancelled: () -> Unit,
) : AddExpenseComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state = MutableValue(AddExpenseState(amountText = config.initialAmount))
  override val state: Value<AddExpenseState> = _state

  override fun onAmountChanged(amount: String) {
    _state.update {
      it.copy(
          amountText = amount,
          amountError = null,
      )
    }
  }

  override fun onDescriptionChanged(description: String) {
    _state.update {
      it.copy(
          descriptionText = description,
          descriptionError = null,
      )
    }
  }

  override fun onSaveClicked(): Job = scope.launch {
    val currentState = _state.value
    val amountNumber = currentState.amountText.trim().replace(",", "").toLongOrNull()
    val isAmountValid = amountNumber != null && amountNumber > 0
    val isDescriptionValid = currentState.descriptionText.trim().isNotEmpty()

    if (!isAmountValid || !isDescriptionValid) {
      _state.update {
        it.copy(
            amountError =
                if (!isAmountValid) {
                  UiString(Res.string.error_invalid_amount)
                } else {
                  null
                },
            descriptionError =
                if (!isDescriptionValid) {
                  UiString(Res.string.error_empty_description)
                } else {
                  null
                },
        )
      }
      return@launch
    }

    _state.update { it.copy(isSaving = true) }
    try {
      expenseRepository.addExpense(
          amount = amountNumber,
          description = currentState.descriptionText.trim(),
      )
      config.candidateExpenseId?.let { id ->
        candidateExpenseRepository.updateStatus(id, CandidateExpenseStatus.ACCEPTED)
      }
      onCompleted()
    } finally {
      _state.update { it.copy(isSaving = false) }
    }
  }

  override fun onCancelClicked() {
    onCancelled()
  }
}

class DefaultAddExpenseComponentFactory(
    private val expenseRepository: ExpenseRepository,
    private val candidateExpenseRepository: CandidateExpenseRepository,
) : AddExpenseComponentFactory {
  override fun create(
      cContext: CContext,
      config: AddExpenseComponent.Config,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseComponent =
      DefaultAddExpenseComponent(
          cContext = cContext,
          expenseRepository = expenseRepository,
          candidateExpenseRepository = candidateExpenseRepository,
          config = config,
          onCompleted = onCompleted,
          onCancelled = onCancelled,
      )
}

@Fake
class FakeAddExpenseComponent(
    initialState: AddExpenseState = AddExpenseState(),
) : AddExpenseComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<AddExpenseState> = _state

  override fun onAmountChanged(amount: String) {
    _state.update { it.copy(amountText = amount) }
  }

  override fun onDescriptionChanged(description: String) {
    _state.update { it.copy(descriptionText = description) }
  }

  override fun onSaveClicked(): Job = Job().apply { complete() }

  override fun onCancelClicked() {}
}

@Fake
class FakeAddExpenseComponentFactory : AddExpenseComponentFactory {
  override fun create(
      cContext: CContext,
      config: AddExpenseComponent.Config,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseComponent =
      FakeAddExpenseComponent(AddExpenseState(amountText = config.initialAmount))
}
