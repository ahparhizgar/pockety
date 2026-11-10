package com.base.calendar

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

class PersianCalendarTest {

  @Test
  fun `convert Nowruz 1403-01-01 to and from Gregorian 2024-03-20`() {
    val persianNowruz = CalendarDate(1403, 1, 1, CalendarType.PERSIAN)
    val gregorianDate = PersianCalendar.toGregorian(persianNowruz)

    gregorianDate shouldBe CalendarDate(2024, 3, 20, CalendarType.GREGORIAN)

    val backToPersian = PersianCalendar.fromGregorian(gregorianDate)
    backToPersian shouldBe persianNowruz
  }

  @Test
  fun `convert Persian date to and from epoch milliseconds in UTC`() {
    val persianNowruz = CalendarDate(1403, 1, 1, CalendarType.PERSIAN)
    val epochMillis = PersianCalendar.toEpochMillis(persianNowruz, TimeZone.UTC)
    val expectedInstant = Instant.parse("2024-03-20T00:00:00Z")

    epochMillis shouldBe expectedInstant.toEpochMilliseconds()

    val roundTripDate = PersianCalendar.toCalendarDate(epochMillis, TimeZone.UTC)
    roundTripDate shouldBe persianNowruz
  }

  @Test
  fun `leap year identification`() {
    PersianCalendar.isLeapYear(1399) shouldBe true
    PersianCalendar.isLeapYear(1403) shouldBe true
    PersianCalendar.isLeapYear(1408) shouldBe true
    PersianCalendar.isLeapYear(1404) shouldBe false
  }

  @Test
  fun `days in month 12 Esfand for leap and non-leap years`() {
    PersianCalendar.getDaysInMonth(1403, 12) shouldBe 30
    PersianCalendar.getDaysInMonth(1404, 12) shouldBe 29
  }

  @Test
  fun `reject invalid month or day in Persian date`() {
    shouldThrow<IllegalArgumentException> { CalendarDate(1403, 13, 1, CalendarType.PERSIAN) }

    shouldThrow<IllegalArgumentException> { CalendarDate(1403, 7, 31, CalendarType.PERSIAN) }

    shouldThrow<IllegalArgumentException> { CalendarDate(1404, 12, 30, CalendarType.PERSIAN) }
  }

  @Test
  fun `add days across Persian month boundary`() {
    val endOfShahrivar = CalendarDate(1403, 6, 31, CalendarType.PERSIAN)
    val nextDay = PersianCalendar.addDays(endOfShahrivar, 1)

    nextDay shouldBe CalendarDate(1403, 7, 1, CalendarType.PERSIAN)

    val fiveDaysLater = PersianCalendar.addDays(endOfShahrivar, 5)
    fiveDaysLater shouldBe CalendarDate(1403, 7, 5, CalendarType.PERSIAN)
  }

  @Test
  fun `add months with day clamping`() {
    val date = CalendarDate(1403, 6, 31, CalendarType.PERSIAN)
    val nextMonth = PersianCalendar.addMonths(date, 1)

    nextMonth shouldBe CalendarDate(1403, 7, 30, CalendarType.PERSIAN)
  }

  @Test
  fun `add years with leap day clamping`() {
    val leapDay = CalendarDate(1403, 12, 30, CalendarType.PERSIAN)
    val nextYear = PersianCalendar.addYears(leapDay, 1)

    nextYear shouldBe CalendarDate(1404, 12, 29, CalendarType.PERSIAN)
  }

  @Test
  fun `round trip conversions for historical and future dates`() {
    val benchmarks =
        listOf(
            CalendarDate(1357, 11, 22, CalendarType.PERSIAN) to
                CalendarDate(1979, 2, 11, CalendarType.GREGORIAN),
            CalendarDate(1400, 1, 1, CalendarType.PERSIAN) to
                CalendarDate(2021, 3, 21, CalendarType.GREGORIAN),
            CalendarDate(1402, 1, 1, CalendarType.PERSIAN) to
                CalendarDate(2023, 3, 21, CalendarType.GREGORIAN),
            CalendarDate(1403, 1, 1, CalendarType.PERSIAN) to
                CalendarDate(2024, 3, 20, CalendarType.GREGORIAN),
        )

    for ((persian, gregorian) in benchmarks) {
      PersianCalendar.toGregorian(persian) shouldBe gregorian
      PersianCalendar.fromGregorian(gregorian) shouldBe persian
    }
  }

  @Test
  fun `calendar date comparison across types`() {
    val persian = CalendarDate(1403, 1, 1, CalendarType.PERSIAN) // 2024-03-20
    val gregorianEarlier = CalendarDate(2024, 3, 19, CalendarType.GREGORIAN)
    val gregorianSame = CalendarDate(2024, 3, 20, CalendarType.GREGORIAN)
    val gregorianLater = CalendarDate(2024, 3, 21, CalendarType.GREGORIAN)

    (persian > gregorianEarlier) shouldBe true
    (persian.compareTo(gregorianSame) == 0) shouldBe true
    (persian < gregorianLater) shouldBe true
  }

  @Test
  fun `fake factory creates valid Persian instances`() {
    val persianDate = FakeCalendarDateFactory.createPersian()
    persianDate.year shouldBe 1405
    persianDate.type shouldBe CalendarType.PERSIAN
  }
}
