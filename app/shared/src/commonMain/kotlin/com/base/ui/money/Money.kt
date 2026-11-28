package com.base.ui.money

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.base.LocalAppLanguage
import com.base.feature.settings.AppLanguage
import com.base.feature.settings.CurrencyType
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.analytics_currency_symbol_fa
import pockety.app.shared.generated.resources.money_kilo_toman_fa
import pockety.app.shared.generated.resources.money_rial_fa
import pockety.app.shared.generated.resources.settings_currency_kilo_tomans_symbol
import pockety.app.shared.generated.resources.settings_currency_rials_symbol
import pockety.app.shared.generated.resources.settings_currency_tomans_symbol

val LocalCurrency = staticCompositionLocalOf { CurrencyType.TOMANS }

private val CurrencyType.decimalPlaces: Int
  get() =
      when (this) {
        CurrencyType.RIALS -> 0
        CurrencyType.TOMANS -> 1
        CurrencyType.KILO_TOMANS -> 4
      }

/**
 * Converts without floating point arithmetic or rounding; null means invalid or overflowing input.
 */
fun CurrencyType.parseRials(input: String): Long? {
  val normalized = normalizeMoneyInput(input)
  val parts = normalized.split('.')
  if (parts.size > 2 || parts[0].isEmpty() || parts.any { part -> part.any { !it.isDigit() } })
      return null
  val fraction = parts.getOrElse(1) { "" }
  return if (fraction.length > decimalPlaces) null
  else (parts[0] + fraction.padEnd(decimalPlaces, '0')).toLongOrNull()
}

fun normalizeMoneyInput(input: String): String = buildString {
  input.forEach { character ->
    when {
      character == ',' || character == '٬' || character.isWhitespace() -> Unit
      character == '٫' -> append('.')
      character.isDigit() -> append(character.digitToInt())
      else -> append(character)
    }
  }
}

fun CurrencyType.amountText(rials: Long): String {
  val raw = rials.toString()
  val negative = raw.startsWith('-')
  val digits = raw.removePrefix("-").padStart(decimalPlaces + 1, '0')
  val amount =
      if (decimalPlaces == 0) digits
      else {
        val whole = digits.dropLast(decimalPlaces)
        val fraction = digits.takeLast(decimalPlaces).trimEnd('0')
        if (fraction.isEmpty()) whole else "$whole.$fraction"
      }
  return if (negative) "-$amount" else amount
}

fun groupMoneyDigits(input: String): String {
  val parts = input.split('.', limit = 2)
  val whole = parts[0].removePrefix("-").reversed().chunked(3).joinToString(",").reversed()
  return (if (input.startsWith('-')) "-" else "") +
      whole +
      (if (parts.size == 2) ".${parts[1]}" else "")
}

/**
 * Formats a plain amount text (digits with an optional '.' fraction, as produced by
 * [CurrencyType.amountText]) for display in [language], using the platform's locale
 * formatting: digit shapes, grouping and decimal separators follow the language.
 */
expect fun formatAmountForDisplay(amountText: String, language: AppLanguage): String

@Composable
fun moneyAmount(rials: Long): String =
    formatAmountForDisplay(LocalCurrency.current.amountText(rials), LocalAppLanguage.current)

@Composable
fun currencySymbol(): String =
    stringResource(
        when (LocalCurrency.current) {
          CurrencyType.RIALS ->
              if (LocalAppLanguage.current == AppLanguage.PERSIAN) Res.string.money_rial_fa
              else Res.string.settings_currency_rials_symbol
          CurrencyType.TOMANS ->
              if (LocalAppLanguage.current == AppLanguage.PERSIAN)
                  Res.string.analytics_currency_symbol_fa
              else Res.string.settings_currency_tomans_symbol
          CurrencyType.KILO_TOMANS ->
              if (LocalAppLanguage.current == AppLanguage.PERSIAN) Res.string.money_kilo_toman_fa
              else Res.string.settings_currency_kilo_tomans_symbol
        }
    )

@Composable fun moneyLabel(rials: Long): String = "${moneyAmount(rials)} ${currencySymbol()}"
