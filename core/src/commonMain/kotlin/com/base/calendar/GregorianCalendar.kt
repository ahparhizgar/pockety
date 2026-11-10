package com.base.calendar

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@Suppress("MagicNumber")
object GregorianCalendar : Calendar {
  override val type: CalendarType = CalendarType.GREGORIAN
  override val firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY

  override fun getDayOfWeek(date: CalendarDate): DayOfWeek {
    require(date.type == CalendarType.GREGORIAN) { "Expected Gregorian date, got ${date.type}" }
    return LocalDate(date.year, date.month, date.day).dayOfWeek
  }

  override fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
  }

  override fun getDaysInMonth(year: Int, month: Int): Int {
    require(month in 1..12) { "Month must be between 1 and 12, got $month" }
    return when (month) {
      1,
      3,
      5,
      7,
      8,
      10,
      12 -> 31
      4,
      6,
      9,
      11 -> 30
      2 -> if (isLeapYear(year)) 29 else 28
      else -> error("Invalid month: $month")
    }
  }

  override fun toCalendarDate(epochMillis: Long, timeZone: TimeZone): CalendarDate {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = instant.toLocalDateTime(timeZone)
    return CalendarDate(
        year = localDateTime.year,
        month = localDateTime.month.ordinal + 1,
        day = localDateTime.day,
        type = CalendarType.GREGORIAN,
    )
  }

  override fun toEpochMillis(date: CalendarDate, timeZone: TimeZone): Long {
    require(date.type == CalendarType.GREGORIAN) { "Expected Gregorian date, got ${date.type}" }
    val localDate = LocalDate(date.year, date.month, date.day)
    val instant = localDate.atStartOfDayIn(timeZone)
    return instant.toEpochMilliseconds()
  }

  override fun addDays(date: CalendarDate, days: Int): CalendarDate {
    require(date.type == CalendarType.GREGORIAN) { "Expected Gregorian date, got ${date.type}" }
    var y = date.year
    var m = date.month
    var d = date.day + days

    while (d > getDaysInMonth(y, m)) {
      d -= getDaysInMonth(y, m)
      m++
      if (m > 12) {
        m = 1
        y++
      }
    }

    while (d < 1) {
      m--
      if (m < 1) {
        m = 12
        y--
      }
      d += getDaysInMonth(y, m)
    }

    return CalendarDate(year = y, month = m, day = d, type = CalendarType.GREGORIAN)
  }

  override fun addMonths(date: CalendarDate, months: Int): CalendarDate {
    require(date.type == CalendarType.GREGORIAN) { "Expected Gregorian date, got ${date.type}" }
    val totalMonths = (date.year * 12 + (date.month - 1)) + months
    val targetYear = totalMonths.floorDiv(12)
    val targetMonth = (totalMonths.mod(12)) + 1
    val maxDays = getDaysInMonth(targetYear, targetMonth)
    val targetDay = date.day.coerceAtMost(maxDays)
    return CalendarDate(
        year = targetYear,
        month = targetMonth,
        day = targetDay,
        type = CalendarType.GREGORIAN,
    )
  }

  override fun addYears(date: CalendarDate, years: Int): CalendarDate {
    require(date.type == CalendarType.GREGORIAN) { "Expected Gregorian date, got ${date.type}" }
    val targetYear = date.year + years
    val maxDays = getDaysInMonth(targetYear, date.month)
    val targetDay = date.day.coerceAtMost(maxDays)
    return CalendarDate(
        year = targetYear,
        month = date.month,
        day = targetDay,
        type = CalendarType.GREGORIAN,
    )
  }

  override fun toGregorian(date: CalendarDate): CalendarDate {
    require(date.type == CalendarType.GREGORIAN) { "Expected Gregorian date, got ${date.type}" }
    return date
  }

  override fun fromGregorian(date: CalendarDate): CalendarDate {
    require(date.type == CalendarType.GREGORIAN) { "Expected Gregorian date, got ${date.type}" }
    return date
  }
}
