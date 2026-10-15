package com.base.capability.sms_expense_notification

import com.base.db.FakeCandidateExpenseRepository
import com.base.notification.FakeExpenseNotificationManager
import com.base.sms.SmsBroadcastReceiver
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class SmsExpenseNotificationTest : BehaviorSpec() {
  init {
    Given("SmsBroadcastReceiver with repository and mock notification manager") {
      val repository = FakeCandidateExpenseRepository()
      val notificationManager = FakeExpenseNotificationManager()

      val receiver = SmsBroadcastReceiver()

      When("a valid bank transaction SMS is processed") {
        val body = "برداشت مبلغ 450000 ریال از حساب بانک ملت"
        val sender = "Bank Mellat"

        val resultId =
            receiver.processSms(
                sender = sender,
                body = body,
                repository = repository,
                notificationManager = notificationManager,
            )

        Then("candidate expense is created in repository") {
          resultId shouldBe 1L
          val candidate = repository.getCandidateById(1L)
          candidate?.sender shouldBe sender
          candidate?.amount shouldBe 450000L
        }

        Then("ExpenseNotificationManager is triggered with candidate details") {
          notificationManager.shownCandidateIds shouldBe listOf(1L)
          val call = notificationManager.shownNotifications.first()
          call.candidateId shouldBe 1L
          call.amount shouldBe 450000L
          call.sender shouldBe sender
        }
      }

      When("an SMS that is not a bank transaction is processed") {
        val nonBankRepository = FakeCandidateExpenseRepository()
        val nonBankNotificationManager = FakeExpenseNotificationManager()
        val body = "سلام، حال شما چطوره؟"
        val sender = "+989123456789"

        val resultId =
            receiver.processSms(
                sender = sender,
                body = body,
                repository = nonBankRepository,
                notificationManager = nonBankNotificationManager,
            )

        Then("no candidate is created and no notification is triggered") {
          resultId shouldBe null
          nonBankRepository.getCandidateById(1L) shouldBe null
          nonBankNotificationManager.shownNotifications shouldHaveSize 0
        }
      }
    }
  }
}
