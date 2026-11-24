package com.base.feature.sms

import com.base.sms.SmsExpenseParser
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SmsExpenseParserTest : BehaviorSpec() {
  init {
    Given("SmsExpenseParser") {
      When("SMS contains standard Persian withdrawal format with English digits") {
        val sms = "بانک ملت\nبرداشت: 3,200,000 ریال\nخرید از فروشگاه کوروش"
        val parsed = SmsExpenseParser.parse(sms)

        Then("it extracts the amount in Rials") {
          parsed shouldNotBe null
          parsed?.amount shouldBe 3200000L
        }
      }

      When("SMS contains Persian digits with Persian comma separators") {
        val sms = "بانک سامان\nخرید: ۱،۴۵۰،۰۰۰ ریال\nمانده: ۲،۳۰۰،۰۰۰ ریال"
        val parsed = SmsExpenseParser.parse(sms)

        Then("it normalizes Persian digits and extracts amount in Rials") {
          parsed shouldNotBe null
          parsed?.amount shouldBe 1450000L
        }
      }

      When("SMS contains Arabic-Indic digits") {
        val sms = "برداشت مبلغ ٥٠٠,٠٠٠ ریال از حساب شما"
        val parsed = SmsExpenseParser.parse(sms)

        Then("it normalizes Arabic-Indic digits correctly") {
          parsed shouldNotBe null
          parsed?.amount shouldBe 500000L
        }
      }

      When("SMS contains English transaction format") {
        val sms = "Sender: +9820000 | Withdrawal: 3,200,000 Rials - Purchase: Kourosh Hypermarket"
        val parsed = SmsExpenseParser.parse(sms)

        Then("it extracts the Rial amount") {
          parsed shouldNotBe null
          parsed?.amount shouldBe 3200000L
        }
      }

      When("SMS specifies amount in Toman") {
        val sms = "مبلغ: 50,000 تومان کسر شد"
        val parsed = SmsExpenseParser.parse(sms)

        Then("it converts Toman to Rials (1 Toman = 10 Rials)") {
          parsed shouldNotBe null
          parsed?.amount shouldBe 500000L
        }
      }

      When("SMS is a promotional SnapPay advertisement") {
        val sms =
            "تا ۶۹۰٪ تخفیف خرید انواع کالا!\n" +
                "در بلک‌فرایدی اسنپ‌پی، کالای مورد نیازت رو تا ۶۹۰٪ ارزون‌تر بخر.\n" +
                "+ ۵۰۰هزار تومن تخفیف بیشتر با کد: PAY5NPA\n" +
                "حداقل خرید: ۲ میلیون تومن\n" +
                "خرید کن:\n" +
                "l.snpy.ir/himi1"
        val parsed = SmsExpenseParser.parse(sms)

        Then("it returns null instead of suggesting the minimum purchase as an expense") {
          parsed shouldBe null
        }
      }

      When("SMS is purely a deposit notice") {
        val sms = "بانک صادرات\nواریز: 10,000,000 ریال به حساب شما"
        val parsed = SmsExpenseParser.parse(sms)

        Then("it ignores deposit SMS") { parsed shouldBe null }
      }

      When("SMS does not match banking transaction pattern") {
        val sms = "سلام، چطوری؟ امشب ساعت ۹ قرار داریم."
        val parsed = SmsExpenseParser.parse(sms)

        Then("it returns null") { parsed shouldBe null }
      }

      When("SMS text is blank") {
        val parsed = SmsExpenseParser.parse("   ")

        Then("it returns null") { parsed shouldBe null }
      }

      When("SMS contains minus prefix amount with account and balance") {
        val sms =
            """
            777.888.26236491.1
            -5,150,000
            07/23_14:52
            مانده: 558,481,583
            """
                .trimIndent()
        val parsed = SmsExpenseParser.parse(sms)

        Then("it extracts the amount in Rials") {
          parsed shouldNotBe null
          parsed?.amount shouldBe 5150000L
        }
      }
    }
  }
}
