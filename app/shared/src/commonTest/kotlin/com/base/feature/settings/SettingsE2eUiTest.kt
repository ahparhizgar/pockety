package com.base.feature.settings

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
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
  fun testCurrencyOptionsKeepRadioSemanticsAndKeyboardActivation() = runComposeUiTest {
    val component = FakeSettingsComponent()

    setContent { SettingsScreen(component = component) }

    onNode(hasTestTag("currency-option-tomans")).waitForExistence()
    onNode(hasTestTag("currency-option-rials")).waitForExistence()
    val radioOptions = onAllNodes(radioRoleMatcher(), useUnmergedTree = true)
    radioOptions.assertCountEquals(4)
    onAllNodes(radioSelectionMatcher(selected = true)).assertCountEquals(2)
    onAllNodes(radioSelectionMatcher(selected = false)).assertCountEquals(2)
    radioOptions[1].performSemanticsAction(SemanticsActions.RequestFocus)
    radioOptions[1].assertIsFocused()
    onNode(isRoot()).performKeyInput { pressKey(Key.Enter) }
    component.state.value.selectedCurrency shouldBe CurrencyType.RIALS
    onAllNodes(radioSelectionMatcher(selected = true)).assertCountEquals(2)
    onAllNodes(radioSelectionMatcher(selected = false)).assertCountEquals(2)

    radioOptions[0].performSemanticsAction(SemanticsActions.RequestFocus)
    radioOptions[0].assertIsFocused()
    onNode(isRoot()).performKeyInput { pressKey(Key.Enter) }
    component.state.value.selectedCurrency shouldBe CurrencyType.TOMANS
  }

  @Test
  fun testCalendarOptionsKeepRadioSemanticsAndKeyboardActivation() = runComposeUiTest {
    val component = FakeSettingsComponent()

    setContent { SettingsScreen(component = component) }

    onNode(hasTestTag("calendar-option-gregorian")).waitForExistence().performScrollTo()
    onNode(hasTestTag("calendar-option-persian")).waitForExistence().performScrollTo()
    val radioOptions = onAllNodes(radioRoleMatcher(), useUnmergedTree = true)
    radioOptions.assertCountEquals(4)
    onAllNodes(radioSelectionMatcher(selected = true)).assertCountEquals(2)
    onAllNodes(radioSelectionMatcher(selected = false)).assertCountEquals(2)
    radioOptions[3].performSemanticsAction(SemanticsActions.RequestFocus)
    radioOptions[3].assertIsFocused()
    onNode(isRoot()).performKeyInput { pressKey(Key.Enter) }
    component.state.value.selectedCalendarType shouldBe CalendarType.PERSIAN
    onAllNodes(radioSelectionMatcher(selected = true)).assertCountEquals(2)
    onAllNodes(radioSelectionMatcher(selected = false)).assertCountEquals(2)

    radioOptions[2].performSemanticsAction(SemanticsActions.RequestFocus)
    radioOptions[2].assertIsFocused()
    onNode(isRoot()).performKeyInput { pressKey(Key.Enter) }
    component.state.value.selectedCalendarType shouldBe CalendarType.GREGORIAN
  }

  private fun radioSelectionMatcher(selected: Boolean): SemanticsMatcher =
      SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)
          .and(SemanticsMatcher.expectValue(SemanticsProperties.Selected, selected))

  private fun radioRoleMatcher(): SemanticsMatcher =
      SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)

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
