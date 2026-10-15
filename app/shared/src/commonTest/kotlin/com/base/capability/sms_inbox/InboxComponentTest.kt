package com.base.capability.sms_inbox

import com.base.component.TestCContext
import com.base.db.CandidateExpenseEntity
import com.base.db.CandidateExpenseRepository
import com.base.notification.ExpenseNotificationManager
import com.base.notification.FakeExpenseNotificationManager
import com.base.testValue
import com.base.util.FakeCallback
import com.base.util.FakeCallback1
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class InboxComponentTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("InboxComponent with repository") {
      val koin by integrationKoin()
      val repository by testValue { koin.get<CandidateExpenseRepository>() }
      val notificationManager by testValue { koin.get<ExpenseNotificationManager>() }
      val selectCallback by testValue { FakeCallback1<CandidateExpenseEntity>() }
      val backCallback by testValue { FakeCallback<Unit>() }

      val component: InboxComponent by testValue {
        DefaultInboxComponent(
            cContext = TestCContext(),
            candidateExpenseRepository = repository,
            expenseNotificationManager = notificationManager,
            onSelectCandidate = selectCallback,
            onBack = backCallback,
        )
      }

      When("no candidate expenses exist") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("state has empty candidates list") {
          val state = component.state.value
          state.candidates shouldHaveSize 0
          state.isLoading shouldBe false
        }
      }

      When("candidate expenses exist") {
        var id1: Long by testValue { 0 }
        var id2: Long by testValue { 0 }

        beforeEach {
          id1 = repository.addCandidate("Mellat", "Withdrawal 1000", 1000L)
          id2 = repository.addCandidate("Saman", "Withdrawal 2000", 2000L)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("component observes and lists pending candidates") {
          val state = component.state.value
          state.candidates shouldHaveSize 2
          state.candidates[0].amount shouldBe 2000L
          state.candidates[1].amount shouldBe 1000L
        }

        Then(
            "dismissing candidate removes it from pending list in state and cancels notification"
        ) {
          component.onDismiss(id2)
          testCoroutineScheduler.advanceUntilIdle()

          val state = component.state.value
          state.candidates shouldHaveSize 1
          state.candidates[0].id shouldBe id1
          (notificationManager as FakeExpenseNotificationManager).cancelledCandidateIds shouldBe
              listOf(id2)
        }

        Then("selecting candidate invokes onSelectCandidate callback") {
          val candidate = component.state.value.candidates[0]
          component.onSelectCandidate(candidate)

          selectCallback.shouldBeCalled()
        }

        Then("clicking back invokes onBack callback") {
          component.onBackClicked()
          backCallback.shouldBeCalled()
        }
      }
    }
  }
}
