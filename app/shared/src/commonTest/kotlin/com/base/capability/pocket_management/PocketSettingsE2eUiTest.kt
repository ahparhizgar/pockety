package com.base.capability.pocket_management

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.RootScreen
import com.base.component.TestCContext
import com.base.util.injectUiRoot
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class PocketSettingsE2eUiTest {

  @Test
  fun testCreatePocketWithKeyboardInputAndSave() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // Navigate to Pockets tab
    onNode(hasTestTag("tab-pockets")).waitForExistence().performClick()

    // Tap Add Pocket FAB
    onNode(hasTestTag("add-pocket-fab")).waitForExistence().performClick()

    // Pocket Settings Screen is open
    onNode(hasTestTag("pocket-settings-screen")).waitForExistence().assertExists()

    // Assert "Save" button is displayed for new pocket
    onNode(hasTestTag("save-pocket-btn")).assertExists()
    onNode(hasText("Save")).assertExists()

    // Focus pocket name field and type name
    onNode(hasTestTag("pocket-name-input")).performClick()
    onNode(isFocused()).performTextInput("Coffee & Dining")

    // Save changes
    onNode(hasTestTag("save-pocket-btn")).performClick()

    // Verify navigating back to PocketsScreen with newly created pocket
    onNode(hasTestTag("pockets-screen")).waitForExistence().assertExists()
    onNode(hasText("Coffee & Dining")).waitForExistence().assertExists()
  }

  @Test
  fun testEmptyPocketNameShowsValidationError() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // Navigate to Pockets tab
    onNode(hasTestTag("tab-pockets")).waitForExistence().performClick()

    // Tap Add Pocket FAB
    onNode(hasTestTag("add-pocket-fab")).waitForExistence().performClick()

    // Pocket Settings Screen is open
    onNode(hasTestTag("pocket-settings-screen")).waitForExistence().assertExists()

    // Submit with empty pocket name
    onNode(hasTestTag("save-pocket-btn")).performClick()

    // Assert validation error message is shown
    onNode(hasText("Pocket name cannot be empty")).waitForExistence().assertExists()
  }

  @Test
  fun testAccentColorSelectionUpdatesVisualSwatch() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    onNode(hasTestTag("tab-pockets")).waitForExistence().performClick()
    onNode(hasTestTag("add-pocket-fab")).waitForExistence().performClick()
    onNode(hasTestTag("pocket-settings-screen")).waitForExistence().assertExists()

    // Select Coral Red color swatch
    val coralRedHex = 0xFFE57373
    onNode(hasTestTag("color-swatch-$coralRedHex")).waitForExistence().performClick()

    // Coral Red swatch is selected and has checkmark
    onNode(hasText("Coral Red")).assertExists()
  }

  @Test
  fun testFilterChipsAndIconSelection() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    onNode(hasTestTag("tab-pockets")).waitForExistence().performClick()
    onNode(hasTestTag("add-pocket-fab")).waitForExistence().performClick()
    onNode(hasTestTag("pocket-settings-screen")).waitForExistence().assertExists()

    // Filter by Food category
    onNode(hasTestTag("icon-filter-Food")).waitForExistence().performClick()

    // Select restaurant icon
    onNode(hasTestTag("icon-option-restaurant")).waitForExistence().performClick()
  }

  @Test
  fun testManualTopUpUpdatesDisplayedBalance() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    onNode(hasTestTag("tab-pockets")).waitForExistence().performClick()
    onNode(hasTestTag("add-pocket-fab")).waitForExistence().performClick()
    onNode(hasTestTag("pocket-settings-screen")).waitForExistence().assertExists()

    // Enter custom top-up amount
    onNode(hasTestTag("top-up-amount-input")).performScrollTo().performClick()
    onNode(isFocused()).performTextInput("150000")

    // Confirm top-up
    onNode(hasTestTag("confirm-top-up-btn")).performScrollTo().performClick()

    // Verify balance update displayed on current-balance-display
    onNode(hasTestTag("current-balance-display"))
        .performScrollTo()
        .waitForExistence()
        .assertExists()
    onNode(hasText("150,000 T")).assertExists()
  }
}
