package com.base.feature.settings

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.calendar.CalendarType
import com.base.util.waitForExistence
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SettingsE2eUiTest {

  @Test
  fun testSettingsScreenCalendarSelection() = runComposeUiTest {
    val component = FakeSettingsComponent()

    setContent { SettingsScreen(component = component) }

    onNode(hasTestTag("settings-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("calendar-option-gregorian")).waitForExistence().assertExists()
    onNode(hasTestTag("calendar-option-persian")).waitForExistence().assertExists()

    component.state.value.selectedCalendarType shouldBe CalendarType.GREGORIAN

    onNode(hasTestTag("calendar-option-persian")).performClick()

    component.state.value.selectedCalendarType shouldBe CalendarType.PERSIAN
  }
}
