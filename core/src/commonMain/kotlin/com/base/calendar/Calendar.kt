package com.base.calendar

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone

enum class CalendarType {
  GREGORIAN,
  PERSIAN,
}

data class CalendarDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val type: CalendarType,
) : Comparable<CalendarDate> {

  init {
    @Suppress("MagicNumber")
    require(month in 1..12) { "Month must be between 1 and 12, got $month" }
    val maxDays =
        when (type) {
          CalendarType.GREGORIAN -> GregorianCalendar.getDaysInMonth(year, month)
          CalendarType.PERSIAN -> PersianCalendar.getDaysInMonth(year, month)
        }
    require(day in 1..maxDays) {
      "Day must be between 1 and $maxDays for $type year $year month $month, got $day"
    }
  }

  @Suppress("ReturnCount")
  override fun compareTo(other: CalendarDate): Int {
    val thisGregorian =
        when (type) {
          CalendarType.GREGORIAN -> this
          CalendarType.PERSIAN -> PersianCalendar.toGregorian(this)
        }
    val otherGregorian =
        when (other.type) {
          CalendarType.GREGORIAN -> other
          CalendarType.PERSIAN -> PersianCalendar.toGregorian(other)
        }

    val yearDiff = thisGregorian.year.compareTo(otherGregorian.year)
    if (yearDiff != 0) return yearDiff
    val monthDiff = thisGregorian.month.compareTo(otherGregorian.month)
    if (monthDiff != 0) return monthDiff
    return thisGregorian.day.compareTo(otherGregorian.day)
  }
}

interface Calendar {
  val type: CalendarType
  val firstDayOfWeek: DayOfWeek

  fun getDayOfWeek(date: CalendarDate): DayOfWeek

  fun isLeapYear(year: Int): Boolean

  fun getDaysInMonth(year: Int, month: Int): Int

  fun toCalendarDate(epochMillis: Long, timeZone: TimeZone = TimeZone.UTC): CalendarDate

  fun toEpochMillis(date: CalendarDate, timeZone: TimeZone = TimeZone.UTC): Long

  fun addDays(date: CalendarDate, days: Int): CalendarDate

  fun addMonths(date: CalendarDate, months: Int): CalendarDate

  fun addYears(date: CalendarDate, years: Int): CalendarDate

  fun toGregorian(date: CalendarDate): CalendarDate

  fun fromGregorian(date: CalendarDate): CalendarDate
}

private val gregorianMonthNames =
    listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December",
    )

private val persianMonthNames =
    listOf(
        "Farvardin",
        "Ordibehesht",
        "Khordad",
        "Tir",
        "Mordad",
        "Shahrivar",
        "Mehr",
        "Aban",
        "Azar",
        "Dey",
        "Bahman",
        "Esfand",
    )

@Suppress("MagicNumber")
fun getMonthName(month: Int, type: CalendarType): String {
  require(month in 1..12) { "Month must be between 1 and 12, got $month" }
  return when (type) {
    CalendarType.GREGORIAN -> gregorianMonthNames[month - 1]
    CalendarType.PERSIAN -> persianMonthNames[month - 1]
  }
}

object FakeCalendarDateFactory {
  @Suppress("MagicNumber")
  fun create(
      year: Int = 2026,
      month: Int = 3,
      day: Int = 21,
      type: CalendarType = CalendarType.GREGORIAN,
  ): CalendarDate =
      CalendarDate(
          year = year,
          month = month,
          day = day,
          type = type,
      )

  @Suppress("MagicNumber")
  fun createPersian(
      year: Int = 1405,
      month: Int = 1,
      day: Int = 1,
  ): CalendarDate =
      CalendarDate(
          year = year,
          month = month,
          day = day,
          type = CalendarType.PERSIAN,
      )

  @Suppress("MagicNumber")
  fun createList(
      count: Int = 3,
      startYear: Int = 2026,
      month: Int = 1,
      day: Int = 1,
      type: CalendarType = CalendarType.GREGORIAN,
  ): List<CalendarDate> =
      (0 until count).map { offset ->
        CalendarDate(
            year = startYear + offset,
            month = month,
            day = day,
            type = type,
        )
      }
}
