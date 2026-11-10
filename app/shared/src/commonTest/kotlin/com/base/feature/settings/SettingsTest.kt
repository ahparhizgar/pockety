package com.base.feature.settings

import com.base.calendar.CalendarType
import com.base.component.TestCContext
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class SettingsTest : BehaviorSpec() {

  init {
    extensions(MainDispatcherExtension())

    Given("SettingsComponent initialized with default preferences") {
      val koin by integrationKoin()
      val repository by testValue { koin.get<UserSettingsRepository>() }
      val factory by testValue { koin.get<SettingsComponentFactory>() }
      val component by testValue { factory.create(TestCContext()) }

      When("state is observed") {
        Then("selectedCalendarType is GREGORIAN") {
          component.state.value.selectedCalendarType shouldBe CalendarType.GREGORIAN
        }
      }

      When("user selects Persian calendar") {
        beforeEach {
          component.onCalendarTypeSelected(CalendarType.PERSIAN)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("selectedCalendarType updates to PERSIAN and is persisted in UserSettingsRepository") {
          repository.calendarType.first() shouldBe CalendarType.PERSIAN
          component.state.value.selectedCalendarType shouldBe CalendarType.PERSIAN
        }
      }
    }
  }
}
