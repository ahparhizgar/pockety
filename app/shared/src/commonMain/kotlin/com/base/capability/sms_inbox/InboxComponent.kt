package com.base.capability.sms_inbox

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.TopLevelDestination
import com.base.component.componentScope
import com.base.db.CandidateExpenseEntity
import com.base.db.CandidateExpenseRepository
import com.base.db.CandidateExpenseStatus
import com.base.db.FakeCandidateExpenseEntityFactory
import com.base.notification.ExpenseNotificationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

data class InboxState(
    val candidates: List<CandidateExpenseEntity> = emptyList(),
    val isLoading: Boolean = false,
)

interface InboxComponent {
  val state: Value<InboxState>

  fun onDismiss(candidateId: Long): Job

  fun onSelectCandidate(candidate: CandidateExpenseEntity)

  fun onBackClicked()

  @Serializable data object Config : TopLevelDestination
}

interface InboxComponentFactory {
  fun create(
      cContext: CContext,
      onSelectCandidate: (CandidateExpenseEntity) -> Unit,
      onBack: () -> Unit,
  ): InboxComponent
}

class DefaultInboxComponent(
    cContext: CContext,
    private val candidateExpenseRepository: CandidateExpenseRepository,
    private val expenseNotificationManager: ExpenseNotificationManager,
    private val onSelectCandidate: (CandidateExpenseEntity) -> Unit,
    private val onBack: () -> Unit,
) : InboxComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state = MutableValue(InboxState(isLoading = true))
  override val state: Value<InboxState> = _state

  init {
    observeCandidates()
  }

  private fun observeCandidates(): Job =
      candidateExpenseRepository
          .observePendingCandidates()
          .onEach { items ->
            _state.update {
              it.copy(
                  candidates = items,
                  isLoading = false,
              )
            }
          }
          .launchIn(scope)

  override fun onDismiss(candidateId: Long): Job = scope.launch {
    candidateExpenseRepository.updateStatus(candidateId, CandidateExpenseStatus.REJECTED)
    expenseNotificationManager.cancelNotification(candidateId)
  }

  override fun onSelectCandidate(candidate: CandidateExpenseEntity) {
    onSelectCandidate.invoke(candidate)
  }

  override fun onBackClicked() {
    onBack.invoke()
  }
}

class DefaultInboxComponentFactory(
    private val candidateExpenseRepository: CandidateExpenseRepository,
    private val expenseNotificationManager: ExpenseNotificationManager,
) : InboxComponentFactory {
  override fun create(
      cContext: CContext,
      onSelectCandidate: (CandidateExpenseEntity) -> Unit,
      onBack: () -> Unit,
  ): InboxComponent =
      DefaultInboxComponent(
          cContext = cContext,
          candidateExpenseRepository = candidateExpenseRepository,
          expenseNotificationManager = expenseNotificationManager,
          onSelectCandidate = onSelectCandidate,
          onBack = onBack,
      )
}

@Fake
class FakeInboxComponent(
    initialState: InboxState =
        InboxState(candidates = FakeCandidateExpenseEntityFactory.createList(3)),
) : InboxComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<InboxState> = _state

  override fun onDismiss(candidateId: Long): Job = Job().apply { complete() }

  override fun onSelectCandidate(candidate: CandidateExpenseEntity) {}

  override fun onBackClicked() {}
}

@Fake
class FakeInboxComponentFactory : InboxComponentFactory {
  override fun create(
      cContext: CContext,
      onSelectCandidate: (CandidateExpenseEntity) -> Unit,
      onBack: () -> Unit,
  ): InboxComponent = FakeInboxComponent()
}
