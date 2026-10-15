package com.base.capability.sms_expense_notification

import com.base.db.CandidateExpenseStatus
import com.base.db.FakeCandidateExpenseEntityFactory
import com.base.db.FakeCandidateExpenseRepository
import com.base.notification.DenyExpenseReceiver
import com.base.notification.FakeExpenseNotificationManager
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class DenyExpenseReceiverTest : BehaviorSpec() {
  init {
    Given("a pending candidate expense in repository") {
      val initialCandidate =
          FakeCandidateExpenseEntityFactory.create(
              id = 42L,
              status = CandidateExpenseStatus.PENDING,
          )
      val repository = FakeCandidateExpenseRepository(listOf(initialCandidate))
      val notificationManager = FakeExpenseNotificationManager()

      val receiver = DenyExpenseReceiver()

      When("processDeny is called") {
        receiver.processDeny(
            candidateId = 42L,
            repository = repository,
            notificationManager = notificationManager,
        )

        Then("repository updates candidate status to REJECTED") {
          repository.lastUpdatedId shouldBe 42L
          repository.lastUpdatedStatus shouldBe CandidateExpenseStatus.REJECTED
          repository.getCandidateById(42L)?.status shouldBe CandidateExpenseStatus.REJECTED
        }

        Then("notification is cancelled") {
          notificationManager.cancelledCandidateIds shouldBe listOf(42L)
        }
      }
    }
  }
}
