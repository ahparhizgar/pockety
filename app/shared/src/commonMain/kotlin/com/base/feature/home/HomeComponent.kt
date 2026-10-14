package com.base.feature.home

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.ExpenseEntity
import com.base.db.ExpenseRepository
import com.base.db.FakeExpenseFactory
import com.base.feature.root.TopLevelDestinationConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable

data class HomeState(
    val expenses: List<ExpenseEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSmsPermissionGranted: Boolean = false,
)

interface HomeComponent : TopLevelDestinationConfig {
  val state: Value<HomeState>

  fun onAddExpenseClicked()

  fun onEnableSmsTrackingClicked()

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

  override fun onAddExpenseClicked() {
    onAddExpense()
  }

  override fun onEnableSmsTrackingClicked() {
    permissionController.requestReceiveSmsPermission()
  }
}

class DefaultHomeComponentFactory(
    private val expenseRepository: ExpenseRepository,
) : HomeComponentFactory {
  override fun create(
      cContext: CContext,
      onAddExpense: () -> Unit,
  ): HomeComponent =
      DefaultHomeComponent(
          cContext = cContext,
          expenseRepository = expenseRepository,
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

  override fun onEnableSmsTrackingClicked() {}
}

@Fake
class FakeHomeComponentFactory : HomeComponentFactory {
  override fun create(
      cContext: CContext,
      onAddExpense: () -> Unit,
  ): HomeComponent = FakeHomeComponent()
}
