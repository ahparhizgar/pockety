package com.base.feature.sms

import com.base.sms.SmsExpenseParser
import com.base.sms.SmsRemoteConfig
import com.base.sms.SmsRemoteRules
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class SmsRemoteRulesTest : BehaviorSpec() {
  init {
    Given("SmsExpenseParser with remote rules") {
      val debitSms = "بانک ملت\nبرداشت: 3,200,000 ریال\nخرید از فروشگاه کوروش"
      val otpSms = "رمز یکبار مصرف شما 123456 مبلغ 250,000 ریال"

      When("an exclude pattern matches a message the local logic accepts") {
        val rules = SmsRemoteRules(SmsRemoteConfig(exclude = listOf("کوروش")))

        Then("the message is vetoed") { SmsExpenseParser.parse(debitSms, rules) shouldBe null }
      }

      When("an include pattern matches a message the local logic rejects") {
        val rules = SmsRemoteRules(SmsRemoteConfig(include = listOf("رمز یکبار مصرف")))

        Then("the message is parsed as an expense") {
          SmsExpenseParser.parse(otpSms, rules)?.amount shouldBe 250_000L
        }
      }

      When("include and exclude patterns both match") {
        val rules =
            SmsRemoteRules(
                SmsRemoteConfig(include = listOf("برداشت"), exclude = listOf("کوروش"))
            )

        Then("the exclude veto wins") { SmsExpenseParser.parse(debitSms, rules) shouldBe null }
      }

      When("a pattern is not a valid regex") {
        val rules = SmsRemoteRules(SmsRemoteConfig(include = listOf("[unclosed")))

        Then("it is ignored and the local logic decides") {
          SmsExpenseParser.parse(debitSms, rules)?.amount shouldBe 3_200_000L
          SmsExpenseParser.parse(otpSms, rules) shouldBe null
        }
      }
    }
  }
}
