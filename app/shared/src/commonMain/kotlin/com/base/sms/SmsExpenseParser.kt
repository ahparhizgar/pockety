package com.base.sms

data class ParsedSmsExpense(val amount: Long)

object SmsExpenseParser {
  private const val TOMAN_TO_RIAL_FACTOR = 10L
  private const val CARD_NUMBER_LENGTH = 16
  private const val FEE_PRIORITY = 0
  private const val CURRENCY_PRIORITY = 1
  private const val LABEL_PRIORITY = 2
  private const val SIGNED_PRIORITY = 3
  private val tomanRegex = Regex("تومان|تومن|toman", RegexOption.IGNORE_CASE)
  private const val CURRENCY = "(?:ریال|تومان|تومن|rials?|tomans?|irr)"
  private const val EXPENSE =
      "(?:برداشت|خرید|پرداخت|انتقال(?:\\s*وجه)?|کارت\\s*به\\s*کارت|کسر(?:\\s*از)?|بدهکار|پرید|\\b(?:withdrawal|purchase|transfer|paid|debited?|pos)\\b)"
  private val expenseRegex = Regex(EXPENSE, RegexOption.IGNORE_CASE)
  private val feeRegex = Regex("کارمزد|\\bfee\\b", RegexOption.IGNORE_CASE)
  private val nonDebitTemplates =
      listOf(
              """شناسه\s+تا[یئ]ید\s+برداشت\s+از\s+حساب""",
              """درخواست\s+خرید\s+واحدهای\s+صندوق[^\r\n]*ثبت\s+شد""",
              """هزینه\s+پیامک[\s\S]*?برداشت[\s\u00ad]+می[\s\u00ad]*شود""",
              """کد\s+رهگیری\s+تجویز[\s\S]*?(?:توسط\s+سازمان\s+بیمه|سهم\s+سازمان)""",
              """موجودی\s+کیف\s+پول\s+شما[^\r\n]*افزایش\s+یافت""",
              """بابت\s+سود\s+طرح\s+حامی[\s\S]*?به\s+قدرت\s+خرید\s+شما\s+اضافه\s+شد""",
              """کارگزاری\s+مفید\s+برداشت\s+مبلغ[^\r\n]*\s+از\s+حساب\s+حامی\s+مانده""",
              """بابت\s+خرید\s+از\s+کیف\s+پول\s+اوانو\s+کسر\s+گردید""",
              """سرویس\s+اعتبار\s+اضطراری[\s\S]*?فعال\s*سازی\s+با\s+شماره\s*گیری""",
              """بازگشت\s+امتیاز[\s\S]*?شماره\s*گیری""",
              """(?:فعال\s*سازی\s+سرویس\s+تعرفه\s+آزاد\s+همراهی|بسته\s+مکالمه\s+همراهی)[\s\S]*?\*[\d*]+#""",
              """شارژ\s+همراهی\s+مخصوص\s+شما[\s\S]*?کد\s+دستوری""",
              """(?:شارژ\s+انجام\s+شد\.\s*)?اعتبار\s*شما\s*\d[\d,]*\s*ریال\.?\s+(?:اینترنت\s+همراهی|خرید\s+به\s+صرفه\s+از)""",
              """گزینه\s+مورد\s+نظر\s+را\s+ارسال\s+کنید[\s\S]*?هزینه\s+این\s+پیامک""",
              """\*خرید\s+از\s+داروکده\*\s+شماره\s+پیگیری""",
              """بلیط[^\r\n]*صادر\s+شد\.\s+شناسه\s+خرید:\s*\d+\s+safar724\.com""",
              """(?:خرید\s+از\s+طاقچه\s*\|\s*دانلود\s+کتاب|خرید\s+خرید\s+کتاب[^\r\n]*\(طاقچه\))[\s\S]*?شناسه:""",
              """خرید\s+شما\s+با\s+موفقیت\s+انجام\s+شد\.\s+کد\s+سفارش:[\s\S]*?پورتال\s+ایرانسل""",
          )
          .map(::Regex)
  private val nonExpenseRegex =
      Regex(
          """(?:^|\s)رمز|کد\s*(?:تایید|تأیید|فعال\s*سازی)|گذرواژه|\botp\b|password|verification\s+code|واریز|بستانکار|برگشت|عودت|استرداد|deposit|credited|received|refund|revers|ناموفق|موفق\s*نبود|انجام\s*نشد|عدم\s*موجودی|موجودی\s*کافی|لغو|failed|declined|unsuccessful|یادآوری|سررسید|لطفا|پرداخت\s*کنید|جایزه|reminder|please\s+pay""",
          RegexOption.IGNORE_CASE,
      )
  // Promotional messages are not bank transactions, even when they mention
  // purchases and amounts: discounts, percent-off figures and shopping links
  // never appear in real debit notifications.
  private val promotionalRegex =
      Regex(
          """تخفیف|discount|promo|٪|%|(?:https?://|www\.)\S+|\b[a-z0-9-]+\.(?:ir|com|net|org|io)/\S*""",
          RegexOption.IGNORE_CASE,
      )
  // A complete integer token: never parse a fragment of a date, account, decimal or bad grouping.
  private val numberRegex =
      Regex(
          """(?<![\d,./+-])(?<!\d:)([+-]?)[ \t]*(\d{1,3}(?:,\d{3})+|\d+)(?:[ \t]*([+-]))?(?![\d,/:-]|\.\d)"""
      )
  private val currencyBefore = Regex("$CURRENCY\\s*$", RegexOption.IGNORE_CASE)
  private val currencyAfter = Regex("^\\s*($CURRENCY)(?![a-z])", RegexOption.IGNORE_CASE)
  private val amountLabel =
      Regex(
          "(?:$EXPENSE|کارمزد|fee|مبلغ|amount)(?:\\s*(?:وجه|به|مبلغ|از|حساب|by))*\\s*(?:\\(\\s*($CURRENCY)\\s*\\))?\\s*[:：]?\\s*(?:$CURRENCY)?\\s*$",
          RegexOption.IGNORE_CASE,
      )
  private val metadataLabel =
      Regex(
          """(?:مانده(?:\s*حساب)?|موجودی(?:\s*قابل\s*برداشت)?|balance|شماره\s*(?:حساب|کارت|پیگیری)|حساب|کارت|شبا|تاریخ|ساعت|account|card|reference)\s*[:：]?\s*(?:$CURRENCY)?\s*$""",
          RegexOption.IGNORE_CASE,
      )
  private val bankContext =
      Regex("حساب|کارت|مانده|موجودی|بانک|account|balance|bank", RegexOption.IGNORE_CASE)

  private fun normalize(input: String): String =
      buildString(input.length) {
        for (ch in input) {
          append(
              when (ch) {
                in '۰'..'۹' -> '0' + (ch - '۰')
                in '٠'..'٩' -> '0' + (ch - '٠')
                'ي',
                'ى' -> 'ی'
                'ك' -> 'ک'
                '٬',
                '،' -> ','
                '−' -> '-'
                '\u200c',
                '\u00a0' -> ' '
                '\u200e',
                '\u200f',
                in '\u202a'..'\u202e',
                in '\u2066'..'\u2069' -> continue
                else -> ch
              }
          )
        }
      }

  fun parse(text: String): ParsedSmsExpense? {
    val normalized = normalize(text)
    if (normalized.isBlank() || isNonExpense(normalized)) return null

    val hasOutgoing =
        expenseRegex.containsMatchIn(normalized) || feeRegex.containsMatchIn(normalized)
    val candidate = candidates(normalized, hasOutgoing).maxByOrNull { it.priority }
    return candidate?.let { ParsedSmsExpense(it.amount) }
  }

  private fun isNonExpense(text: String): Boolean =
      nonExpenseRegex.containsMatchIn(text) ||
          promotionalRegex.containsMatchIn(text) ||
          nonDebitTemplates.any { it.containsMatchIn(text) }

  private fun candidates(text: String, hasOutgoing: Boolean): Sequence<AmountCandidate> =
      numberRegex.findAll(text).mapNotNull { match ->
        val before = text.substring(0, match.range.first).trimEnd()
        val after = text.substring(match.range.last + 1)
        val label = amountLabel.find(before)
        if (isMetadata(before, label)) return@mapNotNull null
        val currency =
            currencyAfter.find(after)?.value
                ?: currencyBefore.find(before)?.value
                ?: label?.groups?.get(1)?.value
        val signs = listOf(match.groupValues[1], match.groupValues[3])
        if ("+" in signs) return@mapNotNull null
        // Unlabelled minuses need banking context, unlike arbitrary negative numbers.
        val signedTransaction = "-" in signs && bankContext.containsMatchIn(text)
        val labelledTransaction = hasOutgoing && (label != null || currency != null)
        if (!signedTransaction && !labelledTransaction) return@mapNotNull null
        val amount = amountInRials(match.groupValues[2], currency) ?: return@mapNotNull null
        AmountCandidate(amount, priority(label?.value, signedTransaction))
      }

  private fun isMetadata(before: String, label: MatchResult?): Boolean {
    val metadata = metadataLabel.find(before)
    return metadata != null && (label == null || metadata.range.first <= label.range.first)
  }

  private fun amountInRials(raw: String, currency: String?): Long? {
    val digits = raw.replace(",", "")
    // Unqualified long integers are usually card/account identifiers.
    val isIdentifier = currency == null && digits.length >= CARD_NUMBER_LENGTH
    val value = digits.toLongOrNull()?.takeIf { it > 0 && !isIdentifier }
    val factor =
        if (currency != null && tomanRegex.containsMatchIn(currency)) TOMAN_TO_RIAL_FACTOR else 1L
    return value?.takeIf { it <= Long.MAX_VALUE / factor }?.times(factor)
  }

  // Prefer the principal to a separate fee; preserve text order on ties.
  private fun priority(label: String?, signedTransaction: Boolean): Int =
      when {
        label != null && feeRegex.containsMatchIn(label) -> FEE_PRIORITY
        signedTransaction -> SIGNED_PRIORITY
        label != null -> LABEL_PRIORITY
        else -> CURRENCY_PRIORITY
      }

  private data class AmountCandidate(val amount: Long, val priority: Int)
}
