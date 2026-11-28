package com.base.ui.money

import com.base.feature.settings.AppLanguage
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

actual fun formatAmountForDisplay(amountText: String, language: AppLanguage): String {
  val format = NumberFormat.getNumberInstance(Locale.forLanguageTag(language.code))
  // BigDecimal keeps every digit exact; the raised cap stops the formatter
  // from rounding the 4 fraction digits of kilo-toman amounts.
  format.maximumFractionDigits = 340
  return format.format(BigDecimal(amountText))
}
