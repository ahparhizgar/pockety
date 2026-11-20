package com.base.feature.money

import com.base.feature.settings.CurrencyType
import com.base.ui.money.amountText
import com.base.ui.money.groupMoneyDigits
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
  fun groupingPreservesFractionalDigitsAndSign() {
    assertEquals("1,234,567.8901", groupMoneyDigits("1234567.8901"))
    assertEquals("-1,234.5", groupMoneyDigits("-1234.5"))
  }
}
