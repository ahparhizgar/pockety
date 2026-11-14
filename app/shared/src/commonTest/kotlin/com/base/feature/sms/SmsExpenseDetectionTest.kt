package com.base.feature.sms

import com.base.sms.SmsExpenseParser
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

// Representative, anonymized template fixtures, not recovered customer SMS.
// Competitor reports supply helper probes, not real bank messages.
class SmsExpenseDetectionTest :
    BehaviorSpec({
      Given("outgoing transaction SMS templates") {
        val expenses =
            listOf(
                "Arabic letters and thousands separator" to
                    ("بانك سامان\nخريد: ۱٬۲۳۴٬۵۶۷ ريال\nمانده: ۹٬۰۰۰٬۰۰۰" to 1234567L),
                "trailing minus" to
                    ("بانک ملی\nحساب 12345\n1,234,567-\nمانده: 8,000,000" to 1234567L),
                "spaced Unicode minus" to ("حساب 12345\n− ۵۰۰٬۰۰۰\nمانده: ۹۰۰٬۰۰۰" to 500000L),
                "label without whitespace" to ("خرید:500,000 ریال" to 500000L),
                "transfer plus fee template" to
                    ("انتقال+کارمزد: 1,007,200 ریال\nمانده: 9,000,000 ریال" to 1007200L),
                "debit label" to ("بانک ملت\nبدهکار: 250000\nمانده: 600000" to 250000L),
                "card to card" to
                    ("کارت‌به‌کارت مبلغ ۲۵۰٬۰۰۰ ریال از حساب شما به کارت 6037990012345678" to
                        250000L),
                "outgoing transfer" to
                    ("انتقال وجه به مبلغ 2,000,000 ریال از حساب شما انجام شد" to 2000000L),
                "withdrawal after amount" to ("مبلغ ۳۰۰۰۰۰ ریال از حساب شما برداشت شد" to 300000L),
                "compact deduction" to ("کسراز حساب: 350,000 ریال\nمانده: 900,000 ریال" to 350000L),
                "colloquial toman" to ("پرداخت: ۱۲٬۵۰۰ تومن\nمانده: ۱۰۰٬۰۰۰ تومن" to 125000L),
                "English debit" to
                    ("Account 1234 debited by IRR 450,000. Balance IRR 900,000" to 450000L),
                "balance before withdrawal" to
                    ("مانده: 9,000,000 ریال\nبرداشت: 500,000 ریال" to 500000L),
                "fee before principal" to
                    ("کارمزد: 7,200 ریال\nانتقال: 1,000,000 ریال" to 1000000L),
                "fee only" to ("کارمزد: 7,200 ریال از حساب شما کسر شد" to 7200L),
                "signed debit before currency balance" to
                    ("حساب 1234\n500,000-\nمانده: 9,000,000 ریال" to 500000L),
                "positive balance is not income" to
                    ("برداشت: 500,000 ریال\nمانده: +9,000,000 ریال" to 500000L),
                "toman minus with bidi marks" to
                    ("حساب 1234\n\u200e−۵۰٬۰۰۰ تومان\nمانده: ۹۰۰٬۰۰۰" to 500000L),
            )
        expenses.forEach { (name, example) ->
          When(name) {
            val parsed = SmsExpenseParser.parse(example.first)
            Then("it returns the outgoing amount in rials") {
              parsed?.amount shouldBe example.second
            }
          }
        }
      }
      Given("messages that do not confirm an expense") {
        val ignored =
            listOf(
                "bare amount" to "مبلغ: 500,000 ریال",
                "unrelated English words" to "Coffee proposal amount 500,000 IRR",
                "failed compact label" to "خرید:500,000 ریال ناموفق",
                "OTP purchase" to "رمز پویا: 123456\nخرید به مبلغ 500,000 ریال\nاعتبار ۲ دقیقه",
                "Persian verification code" to "کد تایید پرداخت: 123456\nمبلغ: 500,000 ریال",
                "English OTP" to "OTP: 123456 for Purchase Amount: 500,000 IRR",
                "failed purchase" to "خرید: 500,000 ریال\nتراکنش ناموفق\nمانده: 900,000 ریال",
                "insufficient funds" to "برداشت: 500,000 ریال به دلیل عدم موجودی کافی انجام نشد",
                "reversal" to "برگشت خرید مبلغ 500,000 ریال به حساب شما واریز شد",
                "deposit with transfer description" to
                    "واریز مبلغ 500,000 ریال بابت انتقال وجه به حساب شما",
                "incoming card transfer" to "کارت به کارت\nمبلغ: 500,000 ریال به حساب شما واریز شد",
                "positive signed purchase" to "خرید: +500,000 ریال",
                "positive signed amount" to "حساب 1234\n500,000+\nمانده: 900,000",
                "available balance" to "موجودی قابل برداشت: 500,000 ریال",
                "English signed balance" to "Balance: IRR -500,000",
                "balance only" to "مانده: -500,000 ریال",
                "balance on previous line" to "مانده حساب:\n-500,000 ریال",
                "bill reminder" to "یادآوری پرداخت قبض به مبلغ 500,000 ریال تا فردا",
                "payment request" to "لطفا مبلغ 500,000 ریال پرداخت کنید",
                "advertisement" to "با خرید مبلغ 500,000 ریال جایزه بگیرید",
                "English incoming" to
                    "Transfer received. Amount: 500,000 IRR credited to your account",
                "fee on deposit" to "واریز: 500,000 ریال\nکارمزد: 7,200 ریال",
                "account number" to "خرید از کارت 6037990012345678\nمانده: 900,000 ریال",
                "date is not signed amount" to "خرید\n1405-08-23\nمانده: 900,000 ریال",
                "fractional amount" to "خرید: 12.50 ریال",
                "invalid grouping" to "خرید: 12,34 ریال",
                "zero" to "برداشت: 0 ریال",
                "overflow" to "خرید: 922337203685477581 تومان",
            )
        ignored.forEach { (name, sms) ->
          When(name) {
            val parsed = SmsExpenseParser.parse(sms)
            Then("it creates no expense") { parsed shouldBe null }
          }
        }
      }
    })
