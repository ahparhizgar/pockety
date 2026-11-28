package com.base.ui.money

import com.base.feature.settings.AppLanguage
import java.math.BigDecimal
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

// The formatter's fraction cap: high enough to never round a display amount
// (kilo-toman amounts carry 4 fraction digits) while BigDecimal stays exact.
private const val MAX_FRACTION_DIGITS = 340

actual fun formatAmountForDisplay(amountText: String, language: AppLanguage): String {
  val format = NumberFormat.getNumberInstance(Locale.forLanguageTag(language.code))
  format.maximumFractionDigits = MAX_FRACTION_DIGITS
  return format.format(BigDecimal(amountText))
}

actual fun localizeDigits(text: String, language: AppLanguage): String {
  val zeroDigit = DecimalFormatSymbols(Locale.forLanguageTag(language.code)).zeroDigit
  if (zeroDigit == '0') return text
  return buildString(text.length) {
    for (char in text) {
      append(if (char in '0'..'9') zeroDigit + (char - '0') else char)
    }
  }
}
