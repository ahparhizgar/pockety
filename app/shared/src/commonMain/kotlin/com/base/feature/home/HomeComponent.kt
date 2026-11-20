package com.base.feature.home

import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CandidateExpenseRepository
import com.base.feature.root.RootFlowDestination
import com.base.sms.InitialSmsImport
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class HomeState(
    val isSmsPermissionGranted: Boolean = false,
    val pendingCandidateCount: Int = 0,
    val pendingCandidateSum: Long = 0L,
)

interface HomeComponent {
  val state: Value<HomeState>

  fun onAddExpenseClicked()

  fun onUndecidedExpensesClicked()

  fun onEnableSmsTrackingClicked(): Job
}

interface HomeComponentFactory {
  fun create(
      cContext: CContext,
      onAddExpense: () -> Unit,
      onOpenInbox: () -> Unit = {},
  ): HomeComponent
}

class DefaultHomeComponent(
    cContext: CContext,
    private val candidateExpenseRepository: CandidateExpenseRepository,
    private val initialSmsImport: InitialSmsImport,
    private val onAddExpense: () -> Unit,
    private val onOpenInbox: () -> Unit = {},
) : HomeComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state =
      MutableValue(
          HomeState(
              isSmsPermissionGranted = permissionController.isSmsPermissionGranted.value,
          )
      )
  override val state: Value<HomeState> = _state

  init {
    observePermission()
    observePendingCandidates()
  }

  private fun observePermission(): Job =
      permissionController.isSmsPermissionGranted
          .onEach { granted ->
            _state.update { it.copy(isSmsPermissionGranted = granted) }
            if (granted) initialSmsImport.importOnce()
          }
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
    navigation.pushNew(RootFlowDestination.AddExpenseFlow())
    onAddExpense()
  }

  override fun onUndecidedExpensesClicked() {
    onOpenInbox()
    scope.launch { permissionController.requestNotificationPermission() }
  }

  override fun onEnableSmsTrackingClicked(): Job = scope.launch {
    permissionController.requestReceiveSmsPermission()
  }
}

class DefaultHomeComponentFactory(
    private val candidateExpenseRepository: CandidateExpenseRepository,
    private val initialSmsImport: InitialSmsImport,
) : HomeComponentFactory {
  override fun create(
      cContext: CContext,
      onAddExpense: () -> Unit,
      onOpenInbox: () -> Unit,
  ): HomeComponent =
      DefaultHomeComponent(
          cContext = cContext,
          candidateExpenseRepository = candidateExpenseRepository,
          initialSmsImport = initialSmsImport,
          onAddExpense = onAddExpense,
          onOpenInbox = onOpenInbox,
      )
}

@Fake
class FakeHomeComponent(
    initialState: HomeState = HomeState(),
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
      onOpenInbox: () -> Unit,
  ): HomeComponent = FakeHomeComponent()
}
