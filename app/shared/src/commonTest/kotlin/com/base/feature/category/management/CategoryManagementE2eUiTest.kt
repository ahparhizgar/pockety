package com.base.feature.category.management

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.withKeyDown
import com.base.RootScreen
import com.base.component.TestCContext
import com.base.util.injectUiRoot
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CategoryManagementE2eUiTest {
  @Test
  fun settingsCategoryCanBeCreatedAndReturnedToUsingKeyboard() = runComposeUiTest {
    val context = TestCContext()
    val root = uiKoin().injectUiRoot(context)
    setContent { RootScreen(root) }

    fun focusWithKeyboard(
        testTag: String,
        isShiftPressed: Boolean = false,
        maxTabPresses: Int = 40,
    ) {
      var targetFocused = false
      repeat(maxTabPresses) {
        if (!targetFocused) {
          targetFocused =
              onAllNodes(
                      hasTestTag(testTag).and(isFocused()),
                      useUnmergedTree = true,
                  )
                  .fetchSemanticsNodes()
                  .isNotEmpty()
          if (!targetFocused) {
            onNode(isRoot()).performKeyInput {
              if (isShiftPressed) {
                withKeyDown(Key.ShiftLeft) { pressKey(Key.Tab) }
              } else {
                pressKey(Key.Tab)
              }
            }
          }
        }
      }
      assertTrue(targetFocused, "Could not reach $testTag by keyboard focus traversal")
    }

    fun activateWithEnter(
        testTag: String,
        isShiftPressed: Boolean = false,
        maxTabPresses: Int = 40,
    ) {
      focusWithKeyboard(testTag, isShiftPressed, maxTabPresses)
      onNode(isRoot()).performKeyInput { pressKey(Key.Enter) }
    }

    fun assertVisible(testTag: String, afterAction: String) {
      assertTrue(
          onAllNodes(hasTestTag(testTag)).fetchSemanticsNodes().isNotEmpty(),
          "$testTag should be visible after $afterAction",
      )
    }

    activateWithEnter("tab-settings")
    assertVisible("settings-screen", "opening Settings from the bottom navigation")
    activateWithEnter("settings-navigation-categories", isShiftPressed = true)
    assertVisible("category-management-screen", "opening Categories from Settings")
    activateWithEnter("add-category-button")
    assertTrue(
        onAllNodes(hasTestTag("category-name-field")).fetchSemanticsNodes().isNotEmpty(),
        "Category form should be visible after activating Add",
    )
    focusWithKeyboard("category-name-field")
    onNode(hasTestTag("category-name-field")).performTextInput("Cancelled")
    activateWithEnter("back-button", isShiftPressed = true)
    assertVisible("category-management-screen", "cancelling Add Category with Back")
    assertTrue(
        onAllNodes(hasText("Cancelled")).fetchSemanticsNodes().isEmpty(),
        "Cancelled category should not appear in the browse destination",
    )

    activateWithEnter("add-category-button")
    assertTrue(
        onAllNodes(hasTestTag("category-name-field")).fetchSemanticsNodes().isNotEmpty(),
        "Category form should reopen after cancellation",
    )
    focusWithKeyboard("category-name-field")
    onNode(hasTestTag("category-name-field")).performTextInput("Groceries")
    activateWithEnter("save-category-button", maxTabPresses = 64)
    onNode(hasText("Groceries")).waitForExistence().assertExists()
    activateWithEnter("back-button")
    assertVisible("settings-screen", "returning from Category Management")
  }
}
