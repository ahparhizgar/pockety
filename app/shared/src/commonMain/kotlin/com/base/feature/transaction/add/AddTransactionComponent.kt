package com.base.feature.transaction.add

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.TransactionRepository
import com.base.feature.root.TopLevelDestinationConfig
import com.base.usermessage.UiString
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.error_empty_description
import pockety.app.shared.generated.resources.error_invalid_amount

data class AddTransactionState(
    val amountText: String = "",
    val descriptionText: String = "",
    val amountError: UiString? = null,
    val descriptionError: UiString? = null,
    val isSaving: Boolean = false,
)

interface AddTransactionComponent : TopLevelDestinationConfig {
  val state: Value<AddTransactionState>

  fun onAmountChanged(amount: String)

  fun onDescriptionChanged(description: String)

  fun onSaveClicked(): Job

  fun onCancelClicked()

  @Serializable data object Config : TopLevelDestinationConfig
}

interface AddTransactionComponentFactory {
  fun create(
      cContext: CContext,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddTransactionComponent
}

class DefaultAddTransactionComponent(
    cContext: CContext,
    private val transactionRepository: TransactionRepository,
    private val onCompleted: () -> Unit,
    private val onCancelled: () -> Unit,
) : AddTransactionComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state = MutableValue(AddTransactionState())
  override val state: Value<AddTransactionState> = _state

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
      transactionRepository.addTransaction(
          amount = amountNumber,
          description = currentState.descriptionText.trim(),
      )
      onCompleted()
    } finally {
      _state.update { it.copy(isSaving = false) }
    }
  }

  override fun onCancelClicked() {
    onCancelled()
  }
}

class DefaultAddTransactionComponentFactory(
    private val transactionRepository: TransactionRepository,
) : AddTransactionComponentFactory {
  override fun create(
      cContext: CContext,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddTransactionComponent =
      DefaultAddTransactionComponent(
          cContext = cContext,
          transactionRepository = transactionRepository,
          onCompleted = onCompleted,
          onCancelled = onCancelled,
      )
}

@Fake
class FakeAddTransactionComponent(
    initialState: AddTransactionState = AddTransactionState(),
) : AddTransactionComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<AddTransactionState> = _state

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
class FakeAddTransactionComponentFactory : AddTransactionComponentFactory {
  override fun create(
      cContext: CContext,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddTransactionComponent = FakeAddTransactionComponent()
}
