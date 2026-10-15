package com.base.sms

data class ParsedSmsExpense(
    val amount: Long,
)

object SmsExpenseParser {

  // Digits mapping for Persian (۰-۹) and Arabic-Indic (٠-٩)
  private fun normalizeDigits(input: String): String {
    val sb = StringBuilder(input.length)
    for (ch in input) {
      when (ch) {
        in '۰'..'۹' -> sb.append(ch - '۰')
        in '٠'..'٩' -> sb.append(ch - '٠')
        else -> sb.append(ch)
      }
    }
    return sb.toString()
  }

  // Regex patterns matching typical Persian bank SMS formats
  // Examples:
  // برداشت: 120,000 ریال
  // خرید: ۱,۴۵۰,۰۰۰ ریال
  // مبلغ: 50,000 ریال
  // انتقال: 2,000,000 ریال
  // کسر شد: 350,000 ریال
  // واریز (is deposit, ignore unless withdrawal/purchase/transfer)
  // Or "مبلغ 320,000 ریال از حساب شما برداشت شد"
  // Or "Withdrawal: 3,200,000 Rials"
  private const val TOMAN_TO_RIAL_FACTOR = 10L

  private val persianRegex =
      Regex(
          """(?:برداشت|خرید|مبلغ|انتقال|کسر|پرداخت)\s*(?::|-)?\s*""" +
              """([0-9\u06F0-\u06F9\u0660-\u0669,\u060C\.\s]+?)\s*(?:ریال|تومان|Rials?)""",
          RegexOption.IGNORE_CASE,
      )
  private val englishRegex =
      Regex(
          """(?:Withdrawal|Purchase|Transfer|Amount|Paid)\s*(?::|-)?\s*""" +
              """([0-9,\.\s]+?)\s*(?:Rials?|Tomans?|IRR)""",
          RegexOption.IGNORE_CASE,
      )
  private val fallbackRegex =
      Regex(
          """(?:برداشت|خرید|مبلغ)\s*(?::|-)?\s*([0-9\u06F0-\u06F9\u0660-\u0669,\u060C]+)""",
          RegexOption.IGNORE_CASE,
      )

  private val minusAmountRegex =
      Regex(
          """(?:^|\n|\r)\s*[-−]\s*([0-9\u06F0-\u06F9\u0660-\u0669,\u060C]+)\s*(?:ریال|تومان|Rials?)?(?=\s*(?:\n|\r|$|\s))""",
          RegexOption.IGNORE_CASE,
      )

  private val patterns = listOf(persianRegex, englishRegex, fallbackRegex, minusAmountRegex)

  private fun isEligibleExpenseText(text: String): Boolean {
    val lower = text.lowercase()
    val isDeposit = text.contains("واریز") || lower.contains("deposit")
    val hasWithdrawal =
        text.contains("برداشت") ||
            text.contains("خرید") ||
            text.contains("انتقال") ||
            text.contains("کسر") ||
            text.contains("پرداخت") ||
            lower.contains("withdrawal") ||
            lower.contains("purchase") ||
            lower.contains("transfer") ||
            lower.contains("paid") ||
            (text.contains("مانده") && (text.contains("-") || text.contains("−")))
    return !isDeposit || hasWithdrawal
  }

  private fun extractAmountFromMatch(match: MatchResult): Long? {
    val rawAmountGroup = match.groupValues[1]
    val normalized = normalizeDigits(rawAmountGroup)
    val cleanedDigits = normalized.replace(Regex("""[,\u060C\.\s]"""), "")
    val amount = cleanedDigits.toLongOrNull()?.takeIf { it > 0 } ?: return null

    val matchedSpan = match.value
    val isToman = matchedSpan.contains("تومان") || matchedSpan.lowercase().contains("toman")
    return if (isToman) amount * TOMAN_TO_RIAL_FACTOR else amount
  }

  fun parse(text: String): ParsedSmsExpense? {
    if (text.isBlank() || !isEligibleExpenseText(text)) {
      return null
    }

    val extractedAmount = patterns.firstNotNullOfOrNull { pattern ->
      val match = pattern.find(text)
      if (match != null) extractAmountFromMatch(match) else null
    }

    return extractedAmount?.let { ParsedSmsExpense(amount = it) }
  }
}
