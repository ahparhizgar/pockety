package com.base.sms

import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class SmsBroadcastProcessingTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("Sms broadcast processing logic") {
      val koin by integrationKoin()
      val repository by testValue { koin.get<com.base.db.CandidateExpenseRepository>() }

      When("processing incoming bank withdrawal SMS") {
        val sender = "Bank Mellat"
        val body = "بانک ملت\nبرداشت: 4,500,000 ریال\nخرید سوپرمارکت"

        val parsed = SmsExpenseParser.parse(body)

        Then("candidate expense is inserted and verified via repository") {
          parsed?.amount shouldBe 4500000L

          val id =
              repository.addCandidate(
                  sender = sender,
                  rawBody = body,
                  amount = parsed!!.amount,
              )
          testCoroutineScheduler.advanceUntilIdle()

          id shouldBe 1L
          val pending = repository.observePendingCandidates().first()
          pending shouldHaveSize 1
          pending[0].sender shouldBe "Bank Mellat"
          pending[0].amount shouldBe 4500000L
        }
      }

      When("processing non-financial SMS") {
        val body = "سلام دوست عزیز، کد ورود شما ۱۲۳۴ است"
        val parsed = SmsExpenseParser.parse(body)

        Then("no candidate expense is created") { parsed shouldBe null }
      }
    }
  }
}
