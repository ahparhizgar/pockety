package com.base.feature.settings

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.calendar.CalendarType
import com.base.util.waitForExistence
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SettingsE2eUiTest {

  @Test
  fun testSettingsScreenHierarchy() = runComposeUiTest {
    val component = FakeSettingsComponent()

    setContent { SettingsScreen(component = component) }

    onNode(hasTestTag("settings-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("settings-navigation-categories")).waitForExistence().assertExists()
    onNode(hasTestTag("settings-navigation-labels")).waitForExistence().assertExists()
    onNode(hasTestTag("settings-row-language")).waitForExistence().assertExists()
    onNode(hasTestTag("currency-option-tomans")).waitForExistence().assertExists()
    onNode(hasTestTag("currency-option-rials")).waitForExistence().assertExists()
    onNode(hasTestTag("calendar-option-gregorian")).waitForExistence().assertExists()
    onNode(hasTestTag("calendar-option-persian")).waitForExistence().assertExists()
    onNode(hasTestTag("settings-switch-reminder")).waitForExistence().assertExists()
    onNode(hasTestTag("settings-section-privacy")).waitForExistence().assertExists()
    onNode(hasTestTag("settings-navigation-privacy-policy")).waitForExistence().assertExists()

    component.state.value.selectedCalendarType shouldBe CalendarType.GREGORIAN
    component.state.value.selectedCurrency shouldBe CurrencyType.TOMANS
    component.state.value.endOfDayReminderEnabled shouldBe true
  }

  @Test
  fun testSettingsScreenCalendarSelection() = runComposeUiTest {
    val component = FakeSettingsComponent()

    setContent { SettingsScreen(component = component) }

    onNode(hasTestTag("calendar-option-persian"))
        .waitForExistence()
        .performScrollTo()
        .performClick()
    component.state.value.selectedCalendarType shouldBe CalendarType.PERSIAN
  }

  @Test
  fun testSettingsScreenCurrencySelection() = runComposeUiTest {
    val component = FakeSettingsComponent()

    setContent { SettingsScreen(component = component) }

    onNode(hasTestTag("currency-option-rials")).waitForExistence().performScrollTo().performClick()
    component.state.value.selectedCurrency shouldBe CurrencyType.RIALS
  }

  @Test
  fun testSettingsScreenReminderToggle() = runComposeUiTest {
    val component = FakeSettingsComponent()

    setContent { SettingsScreen(component = component) }

    onNode(hasTestTag("settings-switch-reminder"))
        .waitForExistence()
        .performScrollTo()
        .performClick()
    component.state.value.endOfDayReminderEnabled shouldBe false
  }

  @Test
  fun testSettingsScreenCategoriesNavigation() = runComposeUiTest {
    var categoriesClicked = false
    val component =
        FakeSettingsComponent(
            onCategoriesClick = { categoriesClicked = true },
        )

    setContent { SettingsScreen(component = component) }

    onNode(hasTestTag("settings-navigation-categories"))
        .waitForExistence()
        .performScrollTo()
        .performClick()
    categoriesClicked shouldBe true
  }

  @Test
  fun testSettingsScreenPrivacyPolicyNavigation() = runComposeUiTest {
    var privacyPolicyClicked = false
    val component =
        FakeSettingsComponent(
            onPrivacyPolicyClick = { privacyPolicyClicked = true },
        )

    setContent { SettingsScreen(component = component) }

    onNode(hasTestTag("settings-navigation-privacy-policy"))
        .waitForExistence()
        .performScrollTo()
        .performClick()
    privacyPolicyClicked shouldBe true
  }
}
