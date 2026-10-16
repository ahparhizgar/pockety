package com.base.capability.home

import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.capability.adding_expense.AddExpenseFlowComponent
import com.base.capability.root_navigation.TopLevelDestinationConfig
import com.base.capability.sms_inbox.InboxComponent
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CandidateExpenseRepository
import com.base.db.ExpenseEntity
import com.base.db.ExpenseRepository
import com.base.db.FakeExpenseFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

data class HomeState(
    val expenses: List<ExpenseEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSmsPermissionGranted: Boolean = false,
    val pendingCandidateCount: Int = 0,
    val pendingCandidateSum: Long = 0L,
)

interface HomeComponent : TopLevelDestinationConfig {
  val state: Value<HomeState>

  fun onAddExpenseClicked()

  fun onUndecidedExpensesClicked()

  fun onEnableSmsTrackingClicked(): Job

  @Serializable data object Config : TopLevelDestinationConfig
}

interface HomeComponentFactory {
  fun create(
      cContext: CContext,
      onAddExpense: () -> Unit,
  ): HomeComponent
}

class DefaultHomeComponent(
    cContext: CContext,
    private val expenseRepository: ExpenseRepository,
    private val candidateExpenseRepository: CandidateExpenseRepository,
    private val onAddExpense: () -> Unit,
) : HomeComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state =
      MutableValue(
          HomeState(
              isLoading = true,
              isSmsPermissionGranted = permissionController.isSmsPermissionGranted.value,
          )
      )
  override val state: Value<HomeState> = _state

  init {
    observeExpenses()
    observePermission()
    observePendingCandidates()
  }

  private fun observeExpenses(): Job =
      expenseRepository
          .observeExpenses()
          .onEach { items ->
            _state.update {
              it.copy(
                  expenses = items,
                  isLoading = false,
              )
            }
          }
          .launchIn(scope)

  private fun observePermission(): Job =
      permissionController.isSmsPermissionGranted
          .onEach { granted -> _state.update { it.copy(isSmsPermissionGranted = granted) } }
          .launchIn(scope)

  private fun observePendingCandidates(): Job =
      candidateExpenseRepository
          .observePendingCandidates()
          .onEach { candidates ->
            _state.update {
              it.copy(
                  pendingCandidateCount = candidates.size,
                  pendingCandidateSum = candidates.sumOf { candidate -> candidate.amount },
              )
            }
          }
          .launchIn(scope)

  override fun onAddExpenseClicked() {
    navigation.pushNew(AddExpenseFlowComponent.Config())
    onAddExpense()
  }

  override fun onUndecidedExpensesClicked() {
    navigation.pushNew(InboxComponent.Config)
  }

  override fun onEnableSmsTrackingClicked(): Job = scope.launch {
    val granted = permissionController.requestReceiveSmsPermission()
    if (granted) {
      // Future navigation or prompt to track bank SMS if needed
    }
  }
}

class DefaultHomeComponentFactory(
    private val expenseRepository: ExpenseRepository,
    private val candidateExpenseRepository: CandidateExpenseRepository,
) : HomeComponentFactory {
  override fun create(
      cContext: CContext,
      onAddExpense: () -> Unit,
  ): HomeComponent =
      DefaultHomeComponent(
          cContext = cContext,
          expenseRepository = expenseRepository,
          candidateExpenseRepository = candidateExpenseRepository,
          onAddExpense = onAddExpense,
      )
}

@Fake
class FakeHomeComponent(
    initialState: HomeState = HomeState(expenses = FakeExpenseFactory.createList(3)),
) : HomeComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<HomeState> = _state

  override fun onAddExpenseClicked() {}

  override fun onUndecidedExpensesClicked() {}

  override fun onEnableSmsTrackingClicked(): Job = Job().apply { complete() }
}

@Fake
class FakeHomeComponentFactory : HomeComponentFactory {
  override fun create(
      cContext: CContext,
      onAddExpense: () -> Unit,
  ): HomeComponent = FakeHomeComponent()
}
