package com.base.feature.expense.add

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CandidateExpenseRepository
import com.base.db.CandidateExpenseStatus
import com.base.db.CategoryEntity
import com.base.db.ExpenseRepository
import com.base.notification.ExpenseNotificationManager
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
    val selectedCategory: CategoryEntity? = null,
    val amountError: UiString? = null,
    val descriptionError: UiString? = null,
    val isSaving: Boolean = false,
)

interface AddExpenseComponent {
  val state: Value<AddExpenseState>

  fun onAmountChanged(amount: String)

  fun onDescriptionChanged(description: String)

  fun onCategorySelected(category: CategoryEntity?)

  fun onCategoryCleared()

  fun onSelectCategoryClicked()

  fun onSaveClicked(): Job

  fun onCancelClicked()

  @Serializable
  data class Config(
      val initialAmount: String = "",
      val candidateExpenseId: Long? = null,
  )
}

interface AddExpenseComponentFactory {
  fun create(
      cContext: CContext,
      config: AddExpenseComponent.Config = AddExpenseComponent.Config(),
      onSelectCategory: () -> Unit,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseComponent
}

@Suppress("LongParameterList")
class DefaultAddExpenseComponent(
    cContext: CContext,
    private val expenseRepository: ExpenseRepository,
    private val candidateExpenseRepository: CandidateExpenseRepository,
    private val expenseNotificationManager: ExpenseNotificationManager,
    private val config: AddExpenseComponent.Config = AddExpenseComponent.Config(),
    private val onSelectCategory: () -> Unit = {},
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

  override fun onCategorySelected(category: CategoryEntity?) {
    _state.update { it.copy(selectedCategory = category) }
  }

  override fun onCategoryCleared() {
    _state.update { it.copy(selectedCategory = null) }
  }

  override fun onSelectCategoryClicked() {
    onSelectCategory.invoke()
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
          categoryId = currentState.selectedCategory?.id,
      )
      config.candidateExpenseId?.let { id ->
        candidateExpenseRepository.updateStatus(id, CandidateExpenseStatus.ACCEPTED)
        expenseNotificationManager.cancelNotification(id)
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
    private val expenseNotificationManager: ExpenseNotificationManager,
) : AddExpenseComponentFactory {
  override fun create(
      cContext: CContext,
      config: AddExpenseComponent.Config,
      onSelectCategory: () -> Unit,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseComponent =
      DefaultAddExpenseComponent(
          cContext = cContext,
          expenseRepository = expenseRepository,
          candidateExpenseRepository = candidateExpenseRepository,
          expenseNotificationManager = expenseNotificationManager,
          config = config,
          onSelectCategory = onSelectCategory,
          onCompleted = onCompleted,
          onCancelled = onCancelled,
      )
}

object FakeAddExpenseStateFactory {
  @Suppress("LongParameterList")
  fun create(
      amountText: String = "",
      descriptionText: String = "",
      selectedCategory: CategoryEntity? = null,
      amountError: UiString? = null,
      descriptionError: UiString? = null,
      isSaving: Boolean = false,
  ): AddExpenseState =
      AddExpenseState(
          amountText = amountText,
          descriptionText = descriptionText,
          selectedCategory = selectedCategory,
          amountError = amountError,
          descriptionError = descriptionError,
          isSaving = isSaving,
      )
}

@Fake
class FakeAddExpenseComponent(
    initialState: AddExpenseState = FakeAddExpenseStateFactory.create(),
    private val onSelectCategory: () -> Unit = {},
) : AddExpenseComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<AddExpenseState> = _state

  override fun onAmountChanged(amount: String) {
    _state.update { it.copy(amountText = amount) }
  }

  override fun onDescriptionChanged(description: String) {
    _state.update { it.copy(descriptionText = description) }
  }

  override fun onCategorySelected(category: CategoryEntity?) {
    _state.update { it.copy(selectedCategory = category) }
  }

  override fun onCategoryCleared() {
    _state.update { it.copy(selectedCategory = null) }
  }

  override fun onSelectCategoryClicked() {
    onSelectCategory.invoke()
  }

  override fun onSaveClicked(): Job = Job().apply { complete() }

  override fun onCancelClicked() {}
}

@Fake
class FakeAddExpenseComponentFactory : AddExpenseComponentFactory {
  override fun create(
      cContext: CContext,
      config: AddExpenseComponent.Config,
      onSelectCategory: () -> Unit,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseComponent =
      FakeAddExpenseComponent(
          initialState = FakeAddExpenseStateFactory.create(amountText = config.initialAmount),
          onSelectCategory = onSelectCategory,
      )
}
