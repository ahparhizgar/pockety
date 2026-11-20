package com.base.feature.sms

import com.base.sms.latestBankExpense
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class LatestBankExpenseTest :
    BehaviorSpec({
      Given("a newest-first SMS stream with unrelated messages before two bank expenses") {
        val messages = sequence {
          yield(FakeReceivedSmsFactory.create("سلام"))
          yield(FakeReceivedSmsFactory.create("رمز پویا: 123456 خرید: 500000 ریال"))
          yield(FakeReceivedSmsFactory.create("برداشت: 500000 ریال"))
          error("Older messages should not be read once the latest match is found")
        }
        When("the latest bank expense is selected") {
          val selected = latestBankExpense(messages)
          Then("unrelated newer messages are skipped and only the latest expense is returned") {
            selected?.amount shouldBe 500000L
          }
        }
      }
    })
