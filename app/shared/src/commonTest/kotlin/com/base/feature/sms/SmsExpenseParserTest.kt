package com.base.feature.sms

import com.base.sms.SmsExpenseParser
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SmsExpenseParserTest : BehaviorSpec() {
  init {
    Given("SmsExpenseParser") {
      listOf(
              "18:08:20   مبلغ (ریال):  125000" to 125_000L,
              "18:10:56   مبلغ (ریال):  12500" to 12_500L,
              "18:10:56   مبلغ (تومان):  ۱۲۵۰۰" to 125_000L,
          )
          .forEach { (timeAndAmount, amount) ->
            When(
                "Resalat reports a purchase at ${timeAndAmount.substringBefore(' ')} for $amount Rials"
            ) {
              val sms =
                  "*بانک قرض الحسنه رسالت*   عملیات  خرید ساده   تاریخ:  1404/07/04  ساعت: $timeAndAmount"

              Then("it extracts the purchase amount in Rials") {
                SmsExpenseParser.parse(sms)?.amount shouldBe amount
              }
            }
          }

      When("SMS contains standard Persian withdrawal format with English digits") {
        val sms = "بانک ملت\nبرداشت: 3,200,000 ریال\nخرید از فروشگاه کوروش"
        val parsed = SmsExpenseParser.parse(sms)

        Then("it extracts the amount in Rials") {
          parsed shouldNotBe null
          parsed?.amount shouldBe 3200000L
        }
      }

      mapOf(
              "a Bank Melli withdrawal authorization" to
                  "بانک ملي(هشدار)\nشناسه تاييد برداشت از حساب شما:12345\nمبلغ:105000 ریال",
              "a pending fund order" to
                  "درخواست خرید واحدهای صندوق پیشرو به مبلغ 337,326 ریال با موفقیت ثبت شد.",
              "an upcoming bank fee" to
                  "بابت هزینه پیامک ارسالی، مبلغ 300 هزار ریال از حساب شما برداشت می\u00adشود.",
              "an insurer contribution" to
                  "کد رهگيري تجويز:12345\nمبلغ 9979551 ريال توسط سازمان بيمه پرداخت مي شود.",
              "a wallet credit" to
                  "موجودی کیف پول شما به میزان ۳۵٬۹۵۲ ریال با موفقیت افزایش یافت.\nدیجی پی - پرداخت",
              "brokerage interest" to
                  "مبلغ 11,339,874 ريال بابت سود طرح حامي به قدرت خريد شما اضافه شد.",
              "a Hami brokerage withdrawal" to
                  "کارگزاری مفید\nبرداشت مبلغ 922,000 ریال\nاز حساب حامی\nمانده: 992,030 ریال",
              "an Ovano wallet debit" to "مبلغ ۲۳۴,۰۰۰ ریال بابت خرید از کیف پول اوانو کسر گردید.",
              "an emergency airtime offer" to
                  "با استفاده از سرویس اعتبار اضطراری می‌تونی تا 5000 تومان شارژ بگیری و بعدا پرداخت کنی.\nمشاهده و فعالسازی با شماره گیری:\n*10*25#",
              "a rewards promotion" to
                  "بازگشت امتیاز\nاز طریق شماره گیری کد#1* وخرید یک شارژ 10000تومانی",
              "a data plan activation offer" to
                  "با فعالسازی سرویس تعرفه آزاد همراهی به مبلغ10000 تومان\nخرید و فعالسازی با شماره گیری:\n*100*622#",
              "a calling plan offer" to
                  "با پرداخت فقط 100000 ریال با بسته مکالمه همراهی\nهمین الان فعال کن:\n*1000*11#",
              "an airtime bonus offer" to
                  "شارژ همراهی مخصوص شما:\nپرداخت مبلغ 125 هزار تومان\nاز طریق شماره‌گیری کد دستوری\n*1000*31#",
              "an airtime balance with a recharge ad" to
                  "شارژ انجام شد.اعتبارشما 217917 ريال.\nاینترنت همراهی\n*100*621#\nخرید راحت شارژ از\n*1#",
              "an airtime balance with a shopping ad" to
                  "اعتبارشما 112039ریال\nخرید به صرفه از: #1*\nو سامانه shop.mci.ir",
              "a service menu with an SMS price" to
                  "گزينه مورد نظر را ارسال کنيد.\n1- نحوه استعلام کارت\nهزينه اين پيامک 350 تومان و برعهده مشترک مي باشد",
              "a Darukadeh receipt" to
                  "*خرید از داروکده*\nشماره پیگیری : 1234567\nمبلغ : 22,800,000 تومان",
              "a ticket confirmation" to
                  "بلیط از تهران برای 16:00-04/09/08 صادر شد.\nشناسه خرید: 1234567\nsafar724.com",
              "a Taaghche receipt" to
                  "خرید از طاقچه | دانلود کتاب صوتی و الکترونیک\nقیمت: ۱۳۰,۰۰۰ تومان\nشناسه: 12345678901234\nبازار",
              "a BazaarPay receipt" to
                  "خرید خرید کتاب نمونه از (طاقچه) به مبلغ ۶۳,۰۰۰ تومان.\nبازارپی\nشناسه: 12345678901234",
              "an Irancell order confirmation" to
                  "خرید شما با موفقیت انجام شد. کد سفارش: MTNi-12345678\nنام محصول: مودم TDLTE - TF-i60 B1 + سیم کارت FD + بسته 30 روزه\nشناسه رزرو: 12345678\nپورتال ایرانسل",
          )
          .forEach { (template, sms) ->
            When("SMS is $template") {
              Then("it produces no expense") { SmsExpenseParser.parse(sms) shouldBe null }
            }
          }

      mapOf(
              "a completed withdrawal" to
                  "بانک ملی\nبرداشت از حساب شما انجام شد. مبلغ: 105000 ریال",
              "a completed SMS fee" to "هزینه پیامک به مبلغ 105000 ریال از حساب شما برداشت شد.",
              "a bank debit for airtime" to
                  "بانک ملت\nخرید شارژ همراهی: 105000 ریال\nمانده: 900000 ریال",
              "a bank debit mentioning a merchant" to
                  "بانک ملت\nخرید از داروکده\nمبلغ: 105000 ریال",
              "an unrecognized wallet debit" to "مبلغ 105000 ریال بابت خرید از کیف پول کسر گردید.",
          )
          .forEach { (transaction, sms) ->
            When("SMS reports $transaction") {
              Then("it produces an expense for 105000 Rials") {
                SmsExpenseParser.parse(sms)?.amount shouldBe 105_000L
              }
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
