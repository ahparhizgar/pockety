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
import com.base.db.CategoryRepository
import com.base.db.ExpenseEntity
import com.base.db.ExpenseRepository
import com.base.db.PocketItemModel
import com.base.db.PocketRepository
import com.base.notification.ExpenseNotificationManager
import com.base.usermessage.UiString
import kotlin.time.Clock
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.error_invalid_amount

data class AddExpenseState(
    val isEditMode: Boolean = false,
    val amountText: String = "",
    val descriptionText: String = "",
    val selectedCategory: CategoryEntity? = null,
    val selectedPocket: PocketItemModel? = null,
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

  fun onPocketSelected(pocket: PocketItemModel?)

  fun onPocketCleared()

  fun onSelectPocketClicked()

  fun onSaveClicked(): Job

  fun onDeleteClicked(): Job

  fun onCancelClicked()

  @Serializable
  data class Config(
      val initialAmount: String = "",
      val candidateExpenseId: Long? = null,
      val expenseId: Long? = null,
  )
}

interface AddExpenseComponentFactory {
  @Suppress("LongParameterList")
  fun create(
      cContext: CContext,
      config: AddExpenseComponent.Config = AddExpenseComponent.Config(),
      onSelectCategory: () -> Unit,
      onSelectPocket: () -> Unit,
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
    private val pocketRepository: PocketRepository,
    private val categoryRepository: CategoryRepository,
    private val config: AddExpenseComponent.Config = AddExpenseComponent.Config(),
    private val onSelectCategory: () -> Unit,
    private val onSelectPocket: () -> Unit,
    private val onCompleted: () -> Unit,
    private val onCancelled: () -> Unit,
) : AddExpenseComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state =
      MutableValue(
          AddExpenseState(
              isEditMode = config.expenseId != null,
              amountText = config.initialAmount,
          )
      )
  override val state: Value<AddExpenseState> = _state

  private var availablePockets: List<PocketItemModel> = emptyList()

  init {
    pocketRepository
        .observePockets()
        .onEach { pockets ->
          val previousPockets = availablePockets
          availablePockets = pockets
          _state.update { currentState ->
            val currentSelectedPocket = currentState.selectedPocket
            if (currentSelectedPocket != null) {
              val updatedPocket = pockets.find { it.id == currentSelectedPocket.id }
              currentState.copy(selectedPocket = updatedPocket)
            } else if (
                !currentState.isEditMode &&
                    previousPockets.isEmpty() &&
                    currentState.selectedCategory != null
            ) {
              val matchingPocket = pockets.firstOrNull {
                currentState.selectedCategory.id in it.categoryIds
              }
              currentState.copy(selectedPocket = matchingPocket)
            } else {
              currentState
            }
          }
        }
        .launchIn(scope)

    if (config.expenseId != null) {
      scope.launch {
        val expense = expenseRepository.getExpenseById(config.expenseId)
        if (expense != null) {
          val categories = categoryRepository.observeCategories().first()
          val pockets =
              if (availablePockets.isNotEmpty()) availablePockets
              else pocketRepository.observePockets().first()
          val category = categories.firstOrNull { it.id == expense.categoryId }
          val pocket = pockets.firstOrNull { it.id == expense.pocketId }
          _state.update {
            it.copy(
                amountText = expense.amount.toString(),
                descriptionText = expense.description,
                selectedCategory = category,
                selectedPocket = pocket,
            )
          }
        }
      }
    }
  }

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
    val matchingPocket =
        if (category != null) {
          availablePockets.firstOrNull { category.id in it.categoryIds }
        } else {
          null
        }
    _state.update {
      it.copy(
          selectedCategory = category,
          selectedPocket = matchingPocket,
      )
    }
  }

  override fun onCategoryCleared() {
    _state.update { it.copy(selectedCategory = null) }
  }

  override fun onSelectCategoryClicked() {
    onSelectCategory.invoke()
  }

  override fun onPocketSelected(pocket: PocketItemModel?) {
    _state.update { it.copy(selectedPocket = pocket) }
  }

  override fun onPocketCleared() {
    _state.update { it.copy(selectedPocket = null) }
  }

  override fun onSelectPocketClicked() {
    onSelectPocket.invoke()
  }

  override fun onSaveClicked(): Job = scope.launch {
    val currentState = _state.value
    val amountNumber = currentState.amountText.trim().replace(",", "").toLongOrNull()
    val isAmountValid = amountNumber != null && amountNumber > 0

    if (!isAmountValid) {
      applyValidationErrors(isAmountValid)
      return@launch
    }

    _state.update { it.copy(isSaving = true) }
    try {
      saveOrUpdateExpense(amountNumber, currentState)
      onCompleted()
    } finally {
      _state.update { it.copy(isSaving = false) }
    }
  }

  private fun applyValidationErrors(isAmountValid: Boolean) {
    _state.update {
      it.copy(
          amountError =
              if (!isAmountValid) {
                UiString(Res.string.error_invalid_amount)
              } else {
                null
              },
          descriptionError = null,
      )
    }
  }

  private suspend fun saveOrUpdateExpense(amountNumber: Long, currentState: AddExpenseState) {
    val expenseId = config.expenseId
    if (expenseId != null) {
      val existing = expenseRepository.getExpenseById(expenseId)
      val expenseToUpdate =
          existing?.copy(
              amount = amountNumber,
              description = currentState.descriptionText.trim(),
              categoryId = currentState.selectedCategory?.id,
              pocketId = currentState.selectedPocket?.id,
          )
              ?: ExpenseEntity(
                  id = expenseId,
                  amount = amountNumber,
                  description = currentState.descriptionText.trim(),
                  createdAt = Clock.System.now(),
                  categoryId = currentState.selectedCategory?.id,
                  pocketId = currentState.selectedPocket?.id,
              )
      expenseRepository.updateExpense(expenseToUpdate)
    } else {
      expenseRepository.addExpense(
          amount = amountNumber,
          description = currentState.descriptionText.trim(),
          categoryId = currentState.selectedCategory?.id,
          pocketId = currentState.selectedPocket?.id,
      )
      config.candidateExpenseId?.let { id ->
        candidateExpenseRepository.updateStatus(id, CandidateExpenseStatus.ACCEPTED)
        expenseNotificationManager.cancelNotification(id)
      }
    }
  }

  override fun onDeleteClicked(): Job = scope.launch {
    val expenseId = config.expenseId ?: return@launch
    _state.update { it.copy(isSaving = true) }
    try {
      expenseRepository.deleteExpense(expenseId)
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
    private val pocketRepository: PocketRepository,
    private val categoryRepository: CategoryRepository,
) : AddExpenseComponentFactory {
  @Suppress("LongParameterList")
  override fun create(
      cContext: CContext,
      config: AddExpenseComponent.Config,
      onSelectCategory: () -> Unit,
      onSelectPocket: () -> Unit,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseComponent =
      DefaultAddExpenseComponent(
          cContext = cContext,
          expenseRepository = expenseRepository,
          candidateExpenseRepository = candidateExpenseRepository,
          expenseNotificationManager = expenseNotificationManager,
          pocketRepository = pocketRepository,
          categoryRepository = categoryRepository,
          config = config,
          onSelectCategory = onSelectCategory,
          onSelectPocket = onSelectPocket,
          onCompleted = onCompleted,
          onCancelled = onCancelled,
      )
}

object FakeAddExpenseStateFactory {
  @Suppress("LongParameterList")
  fun create(
      isEditMode: Boolean = false,
      amountText: String = "",
      descriptionText: String = "",
      selectedCategory: CategoryEntity? = null,
      selectedPocket: PocketItemModel? = null,
      amountError: UiString? = null,
      descriptionError: UiString? = null,
      isSaving: Boolean = false,
  ): AddExpenseState =
      AddExpenseState(
          isEditMode = isEditMode,
          amountText = amountText,
          descriptionText = descriptionText,
          selectedCategory = selectedCategory,
          selectedPocket = selectedPocket,
          amountError = amountError,
          descriptionError = descriptionError,
          isSaving = isSaving,
      )
}

@Fake
class FakeAddExpenseComponent(
    initialState: AddExpenseState = FakeAddExpenseStateFactory.create(),
    private val onSelectCategory: () -> Unit = {},
    private val onSelectPocket: () -> Unit = {},
    private val onCompleted: () -> Unit = {},
    private val onCancelled: () -> Unit = {},
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

  override fun onPocketSelected(pocket: PocketItemModel?) {
    _state.update { it.copy(selectedPocket = pocket) }
  }

  override fun onPocketCleared() {
    _state.update { it.copy(selectedPocket = null) }
  }

  override fun onSelectPocketClicked() {
    onSelectPocket.invoke()
  }

  override fun onSaveClicked(): Job = Job().apply { complete() }

  override fun onDeleteClicked(): Job = Job().apply { complete() }

  override fun onCancelClicked() {
    onCancelled()
  }
}

@Fake
class FakeAddExpenseComponentFactory : AddExpenseComponentFactory {
  @Suppress("LongParameterList")
  override fun create(
      cContext: CContext,
      config: AddExpenseComponent.Config,
      onSelectCategory: () -> Unit,
      onSelectPocket: () -> Unit,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseComponent =
      FakeAddExpenseComponent(
          initialState =
              FakeAddExpenseStateFactory.create(
                  isEditMode = config.expenseId != null,
                  amountText = config.initialAmount,
              ),
          onSelectCategory = onSelectCategory,
          onSelectPocket = onSelectPocket,
          onCompleted = onCompleted,
          onCancelled = onCancelled,
      )
}
