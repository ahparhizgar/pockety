package com.base.calendar

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@Suppress("MagicNumber", "LongMethod", "TooManyFunctions")
object PersianCalendar : Calendar {
  override val type: CalendarType = CalendarType.PERSIAN
  override val firstDayOfWeek: DayOfWeek = DayOfWeek.SATURDAY

  override fun getDayOfWeek(date: CalendarDate): DayOfWeek {
    require(date.type == CalendarType.PERSIAN) { "Expected Persian date, got ${date.type}" }
    val gregorianDate = toGregorian(date)
    return GregorianCalendar.getDayOfWeek(gregorianDate)
  }

  /**
   * Determines if a Persian year is a leap year (kabiseh).
   *
   * Algorithm from Saman Zamani PersianDate (33-year cycle algorithm with reference year 1375):
   */
  override fun isLeapYear(year: Int): Boolean {
    val referenceYear = 1375.0
    val yearRes = year - referenceYear
    if (yearRes == 0.0 || (yearRes % 33 == 0.0)) {
      return true
    }

    val startYear: Double =
        if (yearRes > 0) {
          if (yearRes > 33) {
            val numb = yearRes / 33.0
            referenceYear + (floor(numb) * 33)
          } else {
            referenceYear
          }
        } else {
          if (yearRes > -33) {
            referenceYear - 33
          } else {
            val numb = abs(yearRes) / 33.0
            referenceYear - (ceil(numb) * 33)
          }
        }

    val leapYears =
        listOf(
            startYear.toInt(),
            (startYear + 4).toInt(),
            (startYear + 8).toInt(),
            (startYear + 12).toInt(),
            (startYear + 16).toInt(),
            (startYear + 20).toInt(),
            (startYear + 24).toInt(),
            (startYear + 28).toInt(),
            (startYear + 33).toInt(),
        )

    return leapYears.contains(year)
  }

  override fun getDaysInMonth(year: Int, month: Int): Int {
    require(month in 1..12) { "Month must be between 1 and 12, got $month" }
    return when {
      month in 1..6 -> 31
      month in 7..11 -> 30
      month == 12 -> if (isLeapYear(year)) 30 else 29
      else -> error("Invalid month: $month")
    }
  }

  /**
   * Converts Gregorian date to Jalali date [year, month, day] Algorithm from Saman Zamani
   * PersianDate (gregorian_to_jalali).
   */
  fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
    val g0 = if (gm > 2) (gy + 1) else gy
    val gdm = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
    var d =
        355666 + (365 * gy) + ((g0 + 3) / 4) - ((g0 + 99) / 100) +
            ((g0 + 399) / 400) +
            gd +
            gdm[gm - 1]

    var y = -1595 + (33 * (d / 12053))
    d %= 12053
    y += 4 * (d / 1461)
    d %= 1461
    if (d > 365) {
      y += (d - 1) / 365
      d = (d - 1) % 365
    }

    val m: Int
    val day: Int
    if (d < 186) {
      m = 1 + (d / 31)
      day = 1 + (d % 31)
    } else {
      m = 7 + ((d - 186) / 30)
      day = 1 + ((d - 186) % 30)
    }

    return Triple(y, m, day)
  }

  /**
   * Converts Jalali date to Gregorian date [year, month, day] Algorithm from Saman Zamani
   * PersianDate (jalali_to_gregorian).
   */
  fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
    val adjustedJy = jy + 1595
    var d =
        -355668 +
            (365 * adjustedJy) +
            ((adjustedJy / 33) * 8) +
            (((adjustedJy % 33) + 3) / 4) +
            jd +
            (if (jm < 7) (jm - 1) * 31 else ((jm - 7) * 30) + 186)

    var y = 400 * (d / 146097)
    d %= 146097
    if (d > 36524) {
      d--
      y += 100 * (d / 36524)
      d %= 36524
      if (d >= 365) {
        d++
      }
    }
    y += 4 * (d / 1461)
    d %= 1461
    if (d > 365) {
      y += (d - 1) / 365
      d = (d - 1) % 365
    }

    val isGregorianLeap = (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)
    val salA =
        intArrayOf(0, 31, if (isGregorianLeap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    d++
    var m = 0
    while (m < 13 && d > salA[m]) {
      d -= salA[m]
      m++
    }

    return Triple(y, m, d)
  }

  override fun toGregorian(date: CalendarDate): CalendarDate {
    require(date.type == CalendarType.PERSIAN) { "Expected Persian date, got ${date.type}" }
    val (gy, gm, gd) = jalaliToGregorian(date.year, date.month, date.day)
    return CalendarDate(year = gy, month = gm, day = gd, type = CalendarType.GREGORIAN)
  }

  override fun fromGregorian(date: CalendarDate): CalendarDate {
    require(date.type == CalendarType.GREGORIAN) { "Expected Gregorian date, got ${date.type}" }
    val (py, pm, pd) = gregorianToJalali(date.year, date.month, date.day)
    return CalendarDate(year = py, month = pm, day = pd, type = CalendarType.PERSIAN)
  }

  override fun toCalendarDate(epochMillis: Long, timeZone: TimeZone): CalendarDate {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = instant.toLocalDateTime(timeZone)
    val gregorianDate =
        CalendarDate(
            year = localDateTime.year,
            month = localDateTime.month.ordinal + 1,
            day = localDateTime.day,
            type = CalendarType.GREGORIAN,
        )
    return fromGregorian(gregorianDate)
  }

  override fun toEpochMillis(date: CalendarDate, timeZone: TimeZone): Long {
    require(date.type == CalendarType.PERSIAN) { "Expected Persian date, got ${date.type}" }
    val gregorianDate = toGregorian(date)
    val localDate = LocalDate(gregorianDate.year, gregorianDate.month, gregorianDate.day)
    val instant = localDate.atStartOfDayIn(timeZone)
    return instant.toEpochMilliseconds()
  }

  override fun addDays(date: CalendarDate, days: Int): CalendarDate {
    require(date.type == CalendarType.PERSIAN) { "Expected Persian date, got ${date.type}" }
    val (gy, gm, gd) = jalaliToGregorian(date.year, date.month, date.day)
    val gregorianDate = CalendarDate(year = gy, month = gm, day = gd, type = CalendarType.GREGORIAN)
    val movedGregorian = GregorianCalendar.addDays(gregorianDate, days)
    return fromGregorian(movedGregorian)
  }

  override fun addMonths(date: CalendarDate, months: Int): CalendarDate {
    require(date.type == CalendarType.PERSIAN) { "Expected Persian date, got ${date.type}" }
    val totalMonths = (date.year * 12 + (date.month - 1)) + months
    val targetYear = totalMonths.floorDiv(12)
    val targetMonth = (totalMonths.mod(12)) + 1
    val maxDays = getDaysInMonth(targetYear, targetMonth)
    val targetDay = date.day.coerceAtMost(maxDays)
    return CalendarDate(
        year = targetYear,
        month = targetMonth,
        day = targetDay,
        type = CalendarType.PERSIAN,
    )
  }

  override fun addYears(date: CalendarDate, years: Int): CalendarDate {
    require(date.type == CalendarType.PERSIAN) { "Expected Persian date, got ${date.type}" }
    val targetYear = date.year + years
    val maxDays = getDaysInMonth(targetYear, date.month)
    val targetDay = date.day.coerceAtMost(maxDays)
    return CalendarDate(
        year = targetYear,
        month = date.month,
        day = targetDay,
        type = CalendarType.PERSIAN,
    )
  }
}
