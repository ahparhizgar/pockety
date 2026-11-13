package com.base.feature.settings

import com.base.calendar.CalendarType
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class UserSettingsRepositoryTest {

  @Test
  fun `default preferences have expected values`() = runTest {
    val repository = FakeUserSettingsRepository()
    repository.calendarType.first() shouldBe CalendarType.GREGORIAN
    repository.currencyType.first() shouldBe CurrencyType.TOMANS
    repository.endOfDayReminderEnabled.first() shouldBe true
  }

  @Test
  fun `setting calendar type to persian updates flow`() = runTest {
    val repository = FakeUserSettingsRepository()
    repository.setCalendarType(CalendarType.PERSIAN)
    repository.calendarType.first() shouldBe CalendarType.PERSIAN
  }

  @Test
  fun `setting currency type to rials updates flow`() = runTest {
    val repository = FakeUserSettingsRepository()
    repository.setCurrencyType(CurrencyType.RIALS)
    repository.currencyType.first() shouldBe CurrencyType.RIALS
  }

  @Test
  fun `toggling end of day reminder updates flow`() = runTest {
    val repository = FakeUserSettingsRepository()
    repository.setEndOfDayReminderEnabled(false)
    repository.endOfDayReminderEnabled.first() shouldBe false
  }
}
