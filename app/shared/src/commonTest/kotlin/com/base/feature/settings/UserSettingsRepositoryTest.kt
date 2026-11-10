package com.base.feature.settings

import com.base.calendar.CalendarType
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class UserSettingsRepositoryTest {

  @Test
  fun `default calendar type is gregorian`() = runTest {
    val repository = FakeUserSettingsRepository()
    repository.calendarType.first() shouldBe CalendarType.GREGORIAN
  }

  @Test
  fun `setting calendar type to persian updates flow`() = runTest {
    val repository = FakeUserSettingsRepository()
    repository.setCalendarType(CalendarType.PERSIAN)
    repository.calendarType.first() shouldBe CalendarType.PERSIAN
  }
}
