package com.base.calendar

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

class GregorianCalendarTest {

  @Test
  fun `convert to and from epoch milliseconds in UTC`() {
    val date = CalendarDate(2026, 3, 21, CalendarType.GREGORIAN)
    val epochMillis = GregorianCalendar.toEpochMillis(date, TimeZone.UTC)
    val expectedInstant = Instant.parse("2026-03-21T00:00:00Z")

    epochMillis shouldBe expectedInstant.toEpochMilliseconds()

    val roundTripDate = GregorianCalendar.toCalendarDate(epochMillis, TimeZone.UTC)
    roundTripDate shouldBe date
  }

  @Test
  fun `leap year identification`() {
    GregorianCalendar.isLeapYear(2000) shouldBe true
    GregorianCalendar.isLeapYear(2024) shouldBe true
    GregorianCalendar.isLeapYear(2100) shouldBe false
    GregorianCalendar.isLeapYear(2025) shouldBe false
  }

  @Test
  fun `days in February`() {
    GregorianCalendar.getDaysInMonth(2024, 2) shouldBe 29
    GregorianCalendar.getDaysInMonth(2025, 2) shouldBe 28
  }

  @Test
  fun `reject invalid month or day`() {
    shouldThrow<IllegalArgumentException> { CalendarDate(2024, 13, 1, CalendarType.GREGORIAN) }

    shouldThrow<IllegalArgumentException> { CalendarDate(2024, 1, 32, CalendarType.GREGORIAN) }

    shouldThrow<IllegalArgumentException> { CalendarDate(2025, 2, 29, CalendarType.GREGORIAN) }
  }

  @Test
  fun `add months with day clamping`() {
    val date = CalendarDate(2024, 1, 31, CalendarType.GREGORIAN)
    val nextMonth = GregorianCalendar.addMonths(date, 1)

    nextMonth shouldBe CalendarDate(2024, 2, 29, CalendarType.GREGORIAN)
  }

  @Test
  fun `add days across month and year boundary`() {
    val date = CalendarDate(2023, 12, 30, CalendarType.GREGORIAN)
    val result = GregorianCalendar.addDays(date, 3)

    result shouldBe CalendarDate(2024, 1, 2, CalendarType.GREGORIAN)

    val subtractResult = GregorianCalendar.addDays(result, -3)
    subtractResult shouldBe date
  }

  @Test
  fun `add years with leap day clamping`() {
    val leapDay = CalendarDate(2024, 2, 29, CalendarType.GREGORIAN)
    val nextYear = GregorianCalendar.addYears(leapDay, 1)

    nextYear shouldBe CalendarDate(2025, 2, 28, CalendarType.GREGORIAN)
  }

  @Test
  fun `fake factory creates valid instances`() {
    val defaultDate = FakeCalendarDateFactory.create()
    defaultDate.year shouldBe 2026
    defaultDate.type shouldBe CalendarType.GREGORIAN

    val list = FakeCalendarDateFactory.createList(count = 3)
    list.size shouldBe 3
  }
}
