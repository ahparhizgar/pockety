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
      val component by testValue {
        factory.create(
            cContext = TestCContext(),
            onCategoriesClick = {},
        )
      }

      When("state is observed") {
        Then(
            "selectedCalendarType is GREGORIAN, selectedCurrency is TOMANS, reminder is enabled, and selectedLanguage is ENGLISH"
        ) {
          component.state.value.selectedCalendarType shouldBe CalendarType.GREGORIAN
          component.state.value.selectedCurrency shouldBe CurrencyType.TOMANS
          component.state.value.endOfDayReminderEnabled shouldBe true
          component.state.value.selectedLanguage shouldBe AppLanguage.ENGLISH
          component.state.value.isLanguageDialogVisible shouldBe false
        }
      }

      When("user selects Persian language") {
        beforeEach {
          component.onLanguageSelected(AppLanguage.PERSIAN)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("selectedLanguage updates to PERSIAN and is persisted in UserSettingsRepository") {
          repository.appLanguage.first() shouldBe AppLanguage.PERSIAN
          component.state.value.selectedLanguage shouldBe AppLanguage.PERSIAN
          component.state.value.isLanguageDialogVisible shouldBe false
        }
      }

      When("user clicks language row on device without system locale settings") {
        beforeEach {
          component.onLanguageClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("isLanguageDialogVisible becomes true") {
          component.state.value.isLanguageDialogVisible shouldBe true
        }
      }

      When("user dismisses language dialog") {
        beforeEach {
          component.onDismissLanguageDialog()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("isLanguageDialogVisible becomes false") {
          component.state.value.isLanguageDialogVisible shouldBe false
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

      When("user selects Rials currency") {
        beforeEach {
          component.onCurrencySelected(CurrencyType.RIALS)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("selectedCurrency updates to RIALS and is persisted in UserSettingsRepository") {
          repository.currencyType.first() shouldBe CurrencyType.RIALS
          component.state.value.selectedCurrency shouldBe CurrencyType.RIALS
        }
      }

      When("user toggles end-of-day reminder off") {
        beforeEach {
          component.onReminderToggled(false)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then(
            "endOfDayReminderEnabled updates to false and is persisted in UserSettingsRepository"
        ) {
          repository.endOfDayReminderEnabled.first() shouldBe false
          component.state.value.endOfDayReminderEnabled shouldBe false
        }
      }
    }

    Given("SettingsComponent initialized with onPrivacyPolicyClick callback") {
      var privacyPolicyClicked = false
      val component =
          FakeSettingsComponent(
              onPrivacyPolicyClick = { privacyPolicyClicked = true },
          )

      When("user clicks Privacy Policy via onPrivacyPolicyClicked()") {
        component.onPrivacyPolicyClicked()

        Then("the onPrivacyPolicyClick callback is executed") { privacyPolicyClicked shouldBe true }
      }
    }
  }
}
