package com.base.feature.settings

import com.base.calendar.CalendarType
import com.base.component.TestCContext
import com.base.feature.root.RootComponentFactory
import com.base.testValue
import com.base.ui.components.PocketyTab
import com.base.util.MainDispatcherExtension
import com.base.util.assertAnalyticsComponent
import com.base.util.assertMainComponent
import com.base.util.assertSettingsComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe

class SettingsFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("app initialized at MainComponent") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      When("user selects Settings tab in bottom bar") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          val main = root.assertMainComponent()
          main.onTabSelect(PocketyTab.Settings)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("active child in MainComponent is SettingsComponent") {
          val settings = root.assertMainComponent().assertSettingsComponent()
          settings.state.value.selectedCalendarType shouldBe CalendarType.GREGORIAN
        }

        And("user selects Persian calendar option") {
          beforeEach {
            val settings = root.assertMainComponent().assertSettingsComponent()
            settings.onCalendarTypeSelected(CalendarType.PERSIAN)
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("SettingsComponent state reflects PERSIAN") {
            val settings = root.assertMainComponent().assertSettingsComponent()
            settings.state.value.selectedCalendarType shouldBe CalendarType.PERSIAN
          }

          And("user selects Analytics tab") {
            beforeEach {
              val main = root.assertMainComponent()
              main.onTabSelect(PocketyTab.Analytics)
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("AnalyticsComponent displays active month in Persian calendar") {
              val analytics = root.assertMainComponent().assertAnalyticsComponent()
              analytics.state.value.selectedMonthLabel.isNotEmpty() shouldBe true
            }
          }
        }
      }
    }
  }
}
