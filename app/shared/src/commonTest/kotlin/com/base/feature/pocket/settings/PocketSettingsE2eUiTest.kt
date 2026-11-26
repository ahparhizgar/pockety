package com.base.feature.pocket.settings

import androidx.compose.ui.test.ComposeUiTest
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
import com.base.db.CategoryRepository
import com.base.util.injectUiRoot
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test
import kotlinx.coroutines.runBlocking
import org.koin.core.Koin

@OptIn(ExperimentalTestApi::class)
class PocketSettingsE2eUiTest {

  private fun ComposeUiTest.openCreatePocket() {
    onNode(hasTestTag("tab-pockets")).waitForExistence().performClick()
    onNode(hasTestTag("add-pocket-fab")).waitForExistence().performClick()
    onNode(hasTestTag("pocket-settings-screen")).waitForExistence().assertExists()
  }

  /** Simple mode -> confirmation dialog -> manual form. */
  private fun ComposeUiTest.enterManualMode() {
    onNode(hasTestTag("manual-setup-row")).waitForExistence().performScrollTo().performClick()
    onNode(hasText("Switch")).waitForExistence().performClick()
    onNode(hasTestTag("pocket-name-input")).waitForExistence().assertExists()
  }

  private fun ComposeUiTest.createPocketViaManualMode(name: String) {
    openCreatePocket()
    enterManualMode()
    onNode(hasTestTag("pocket-name-input")).performClick()
    onNode(isFocused()).performTextInput(name)
    onNode(hasTestTag("save-pocket-btn")).performClick()
    onNode(hasTestTag("pockets-screen")).waitForExistence().assertExists()
  }

  private fun seedCategory(koin: Koin, name: String, icon: String, color: Long): Long =
      runBlocking {
        koin.get<CategoryRepository>().addCategory(name, icon, color)
      }

  @Test
  fun testCreatePocketWithKeyboardInputAndSave() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    openCreatePocket()

    // Assert "Save" button is displayed for new pocket
    onNode(hasTestTag("save-pocket-btn")).assertExists()
    onNode(hasText("Save")).assertExists()

    enterManualMode()

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
  fun testSimpleModeCategorySelectionCreatesInheritedPocket() = runComposeUiTest {
    val koin = uiKoin()
    val cafeId = seedCategory(koin, "Cafe & Dining", "coffee", 0xFFE57373)
    seedCategory(koin, "Groceries", "shopping_cart", 0xFF4A7C59)
    val context = TestCContext()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    openCreatePocket()

    // Pick the category; the pocket name is filled from it without typing
    onNode(hasTestTag("pocket-category-$cafeId")).waitForExistence().performClick()
    onNode(hasTestTag("pocket-name-input")).waitForExistence().assertExists()

    onNode(hasTestTag("save-pocket-btn")).performClick()

    onNode(hasTestTag("pockets-screen")).waitForExistence().assertExists()
    onNode(hasText("Cafe & Dining")).waitForExistence().assertExists()
  }

  @Test
  fun testManualModeLinksMultipleCategories() = runComposeUiTest {
    val koin = uiKoin()
    val cafeId = seedCategory(koin, "Cafe & Dining", "coffee", 0xFFE57373)
    val groceriesId = seedCategory(koin, "Groceries", "shopping_cart", 0xFF4A7C59)
    val context = TestCContext()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    openCreatePocket()
    enterManualMode()

    onNode(hasTestTag("pocket-name-input")).performClick()
    onNode(isFocused()).performTextInput("Food")

    // Link both categories to the pocket
    onNode(hasTestTag("pocket-linked-category-$cafeId"))
        .waitForExistence()
        .performScrollTo()
        .performClick()
    onNode(hasTestTag("pocket-linked-category-$groceriesId"))
        .waitForExistence()
        .performScrollTo()
        .performClick()

    onNode(hasTestTag("save-pocket-btn")).performClick()

    onNode(hasTestTag("pockets-screen")).waitForExistence().assertExists()
    onNode(hasText("Food")).waitForExistence().assertExists()
  }

  @Test
  fun testEmptyPocketNameShowsValidationError() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    openCreatePocket()

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

    openCreatePocket()
    enterManualMode()

    // Select Coral Red color swatch
    val coralRedHex = 0xFFE57373
    onNode(hasTestTag("color-swatch-$coralRedHex"))
        .waitForExistence()
        .performScrollTo()
        .performClick()

    // Coral Red swatch is selected and has checkmark
    onNode(hasText("Coral Red")).assertExists()
  }

  @Test
  fun testFilterChipsAndIconSelection() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    openCreatePocket()
    enterManualMode()

    // Filter by Food category
    onNode(hasTestTag("icon-filter-Food")).waitForExistence().performScrollTo().performClick()

    // Select restaurant icon
    onNode(hasTestTag("icon-option-restaurant")).waitForExistence().performScrollTo().performClick()
  }

  @Test
  fun testEditPocketDisplaysApplyButtonAndSaves() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    createPocketViaManualMode("Original Pocket")

    onNode(hasText("Original Pocket")).waitForExistence().performClick()

    // On Pocket Details screen, click settings / configure
    onNode(hasTestTag("action-configure-button")).waitForExistence().performClick()

    // Assert "Apply" button is displayed for existing pocket
    onNode(hasTestTag("save-pocket-btn")).waitForExistence().assertExists()
    onNode(hasText("Apply")).assertExists()

    // Update name
    onNode(hasTestTag("pocket-name-input")).performClick()
    onNode(isFocused()).performTextInput(" Renamed")
    onNode(hasTestTag("save-pocket-btn")).performClick()

    // Verify updated pocket name
    onNode(hasText("Original Pocket Renamed")).waitForExistence().assertExists()
  }

  @Test
  fun testDeletePocketFromDangerZone() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // 1. Create a pocket (danger zone shouldn't exist in create mode)
    openCreatePocket()
    onNode(hasTestTag("delete-pocket-btn")).assertDoesNotExist()
    enterManualMode()
    onNode(hasTestTag("pocket-name-input")).performClick()
    onNode(isFocused()).performTextInput("Pocket To Be Deleted")
    onNode(hasTestTag("save-pocket-btn")).performClick()

    // 2. Open details then configure/settings
    onNode(hasTestTag("pockets-screen")).waitForExistence().assertExists()
    onNode(hasText("Pocket To Be Deleted")).waitForExistence().performClick()
    onNode(hasTestTag("action-configure-button")).waitForExistence().performClick()

    // 3. Danger zone should be visible in edit mode
    onNode(hasTestTag("pocket-settings-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("delete-pocket-btn")).waitForExistence().performScrollTo().performClick()

    // 4. Verify pocket is removed on PocketsScreen (popped back directly upon deletion)
    onNode(hasTestTag("pockets-screen")).waitForExistence().assertExists()
    onNode(hasText("Pocket To Be Deleted")).assertDoesNotExist()
  }

  @Test
  fun testMonthlyAllocationCardAdjustmentAndAutoRefillSwitch() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    openCreatePocket()

    // Scroll to monthly allocation card
    onNode(hasTestTag("monthly-allocation-card"))
        .waitForExistence()
        .performScrollTo()
        .assertExists()

    // Toggle auto-refill switch
    onNode(hasTestTag("auto-refill-switch")).performClick()

    // The monthly allocation is an inline text field: type the new amount directly
    onNode(hasTestTag("adjust-allocation-input")).waitForExistence().performTextInput("250000")

    // Verify updated amount is displayed in the inline field
    onNode(hasText("250,000")).waitForExistence().assertExists()
  }
}
