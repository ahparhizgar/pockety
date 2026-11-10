package com.base.calendar

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.datetime.DayOfWeek

class CalendarTest {

  @Test
  fun `gregorian calendar first day of week is monday`() {
    GregorianCalendar.firstDayOfWeek shouldBe DayOfWeek.MONDAY
  }

  @Test
  fun `gregorian calendar day of week for 2026-03-21 is saturday`() {
    val date = CalendarDate(2026, 3, 21, CalendarType.GREGORIAN)
    GregorianCalendar.getDayOfWeek(date) shouldBe DayOfWeek.SATURDAY
  }

  @Test
  fun `gregorian calendar month names`() {
    getMonthName(1, CalendarType.GREGORIAN) shouldBe "January"
    getMonthName(12, CalendarType.GREGORIAN) shouldBe "December"
  }

  @Test
  fun `persian calendar first day of week is saturday`() {
    PersianCalendar.firstDayOfWeek shouldBe DayOfWeek.SATURDAY
  }

  @Test
  fun `persian calendar day of week for 1403-01-01 is wednesday`() {
    val date = CalendarDate(1403, 1, 1, CalendarType.PERSIAN)
    PersianCalendar.getDayOfWeek(date) shouldBe DayOfWeek.WEDNESDAY
  }

  @Test
  fun `persian calendar month names`() {
    getMonthName(1, CalendarType.PERSIAN) shouldBe "Farvardin"
    getMonthName(7, CalendarType.PERSIAN) shouldBe "Mehr"
    getMonthName(12, CalendarType.PERSIAN) shouldBe "Esfand"
  }
}
