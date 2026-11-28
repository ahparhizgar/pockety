package com.base.feature.money

import com.base.feature.settings.AppLanguage
import com.base.feature.settings.CurrencyType
import com.base.ui.money.amountText
import com.base.ui.money.formatAmountForDisplay
import com.base.ui.money.groupMoneyDigits
import com.base.ui.money.localizeDigits
import com.base.ui.money.parseRials
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MoneyTest {
  @Test
  fun exactRialsSurviveEveryDisplayUnitIncludingLongBoundaries() {
    val amounts = listOf(0L, 1L, 15L, 123456789L, Long.MAX_VALUE)
    CurrencyType.entries.forEach { currency ->
      amounts.forEach { amount ->
        assertEquals(amount, currency.parseRials(groupMoneyDigits(currency.amountText(amount))))
      }
    }
    assertEquals("-922337203685477.5808", CurrencyType.KILO_TOMANS.amountText(Long.MIN_VALUE))
  }

  @Test
  fun userEnteredUnitsConvertToWholeRialsWithoutRounding() {
    assertEquals(123456L, CurrencyType.KILO_TOMANS.parseRials("۱۲٫۳۴۵۶"))
    assertEquals(123456L, CurrencyType.TOMANS.parseRials("12,345.6"))
    assertEquals(123456L, CurrencyType.RIALS.parseRials("۱۲۳٬۴۵۶"))
    assertNull(CurrencyType.KILO_TOMANS.parseRials("1.00001"))
    assertNull(CurrencyType.TOMANS.parseRials("1.01"))
    assertNull(CurrencyType.RIALS.parseRials("1.1"))
    assertNull(CurrencyType.TOMANS.parseRials(Long.MAX_VALUE.toString()))
    assertNull(CurrencyType.RIALS.parseRials("-1"))
    assertNull(CurrencyType.RIALS.parseRials("1.2.3"))
    assertNull(CurrencyType.RIALS.parseRials(""))
  }

  @Test
  fun displayFormattingLocalizesDigitsAndSeparators() {
    assertEquals("1,234,567.5", formatAmountForDisplay("1234567.5", AppLanguage.ENGLISH))
    assertEquals("۱٬۲۳۴٬۵۶۷٫۵", formatAmountForDisplay("1234567.5", AppLanguage.PERSIAN))
    assertEquals("۸۵٬۰۰۰", formatAmountForDisplay("85000", AppLanguage.PERSIAN))
    // Kilo-toman amounts carry 4 fraction digits; none may be rounded away.
    assertEquals("۱٫۲۳۴۵", formatAmountForDisplay("1.2345", AppLanguage.PERSIAN))
    assertEquals("1.2345", formatAmountForDisplay("1.2345", AppLanguage.ENGLISH))
    assertEquals("۰", formatAmountForDisplay("0", AppLanguage.PERSIAN))
  }

  @Test
  fun digitLocalizationKeepsSeparatorsPaddingAndNonDigits() {
    // Years must not gain a grouping separator.
    assertEquals("۱۴۰۴", localizeDigits("1404", AppLanguage.PERSIAN))
    // Zero padding and the colon of a time stay untouched.
    assertEquals("۱۴:۰۵", localizeDigits("14:05", AppLanguage.PERSIAN))
    assertEquals("۲۸ Nov, ۰۹:۳۰", localizeDigits("28 Nov, 09:30", AppLanguage.PERSIAN))
    assertEquals("14:05", localizeDigits("14:05", AppLanguage.ENGLISH))
  }

  @Test
  fun groupingPreservesFractionalDigitsAndSign() {
    assertEquals("1,234,567.8901", groupMoneyDigits("1234567.8901"))
    assertEquals("-1,234.5", groupMoneyDigits("-1234.5"))
  }
}
