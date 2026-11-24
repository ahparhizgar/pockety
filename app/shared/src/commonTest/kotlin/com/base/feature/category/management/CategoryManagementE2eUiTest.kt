package com.base.feature.category.management

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.withKeyDown
import com.base.RootScreen
import com.base.component.TestCContext
import com.base.db.CategoryRepository
import com.base.util.injectUiRoot
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

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
    activateWithEnter("icon-filter-Shopping")
    activateWithEnter("category-icon-local_grocery_store")
    activateWithEnter("save-category-button", maxTabPresses = 96)
    onNode(hasText("Groceries")).waitForExistence().assertExists()
    activateWithEnter("back-button")
    assertVisible("settings-screen", "returning from Category Management")
  }

  @Test
  fun categoryCanBeDeletedFromManagementScreen() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val categoryRepository = koin.get<CategoryRepository>()
    runBlocking {
      categoryRepository.addCategory(name = "Dining", icon = "restaurant", color = 0xFFE57373)
      categoryRepository.addCategory(
          name = "Groceries",
          icon = "shopping_cart",
          color = 0xFF81C784,
      )
    }
    val component =
        koin
            .get<CategoryManagementComponentFactory>()
            .create(cContext = context, onAdd = {}, onBack = {})
    setContent { CategoryManagementScreen(component = component) }

    onNode(hasTestTag("category-item-Groceries")).waitForExistence().assertExists()
    assertTrue(
        onAllNodes(hasTestTag("uncategorized-item")).fetchSemanticsNodes().isEmpty(),
        "Management screen must not show an Uncategorized row",
    )
    assertTrue(
        onAllNodes(hasText("Active")).fetchSemanticsNodes().isEmpty(),
        "Management screen must not show a selected state",
    )

    onNode(hasTestTag("delete-category-Groceries")).performClick()
    onNode(hasTestTag("confirm-delete-button")).waitForExistence().assertExists()
    onNode(hasTestTag("cancel-delete-button")).performClick()
    onNode(hasTestTag("category-item-Groceries")).assertExists()

    onNode(hasTestTag("delete-category-Groceries")).performClick()
    onNode(hasTestTag("confirm-delete-button")).waitForExistence().performClick()
    assertTrue(
        onAllNodes(hasTestTag("category-item-Groceries")).fetchSemanticsNodes().isEmpty(),
        "Groceries row should be removed after confirming deletion",
    )
    onNode(hasTestTag("category-item-Dining")).assertExists()
  }

  @Test
  fun searchFiltersManagementRows() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val categoryRepository = koin.get<CategoryRepository>()
    runBlocking {
      categoryRepository.addCategory(name = "Dining", icon = "restaurant", color = 0xFFE57373)
      categoryRepository.addCategory(
          name = "Groceries",
          icon = "shopping_cart",
          color = 0xFF81C784,
      )
    }
    val component =
        koin
            .get<CategoryManagementComponentFactory>()
            .create(cContext = context, onAdd = {}, onBack = {})
    setContent { CategoryManagementScreen(component = component) }

    onNode(hasTestTag("category-search-field")).waitForExistence().performClick()
    onNode(isFocused()).performTextInput("Din")
    onNode(hasTestTag("category-item-Dining")).waitForExistence().assertExists()
    assertTrue(
        onAllNodes(hasTestTag("category-item-Groceries")).fetchSemanticsNodes().isEmpty(),
        "Groceries row should be filtered out by the search query",
    )
  }

  @Test
  fun searchWithNoMatchesShowsNoResultsState() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val categoryRepository = koin.get<CategoryRepository>()
    runBlocking {
      categoryRepository.addCategory(name = "Dining", icon = "restaurant", color = 0xFFE57373)
      categoryRepository.addCategory(
          name = "Groceries",
          icon = "shopping_cart",
          color = 0xFF81C784,
      )
    }
    val component =
        koin
            .get<CategoryManagementComponentFactory>()
            .create(cContext = context, onAdd = {}, onBack = {})
    setContent { CategoryManagementScreen(component = component) }

    onNode(hasTestTag("category-search-field")).waitForExistence().performClick()
    onNode(isFocused()).performTextInput("unknownxyz")
    onNode(hasText("No categories found")).waitForExistence().assertExists()
  }

  @Test
  fun categoryCanBeReorderedWithKeyboard() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val categoryRepository = koin.get<CategoryRepository>()
    runBlocking {
      categoryRepository.addCategory(name = "Dining", icon = "restaurant", color = 0xFFE57373)
      categoryRepository.addCategory(
          name = "Groceries",
          icon = "shopping_cart",
          color = 0xFF81C784,
      )
    }
    val component =
        koin
            .get<CategoryManagementComponentFactory>()
            .create(cContext = context, onAdd = {}, onBack = {})
    setContent { CategoryManagementScreen(component = component) }

    onNode(hasTestTag("reorder-handle-Groceries")).waitForExistence().assertExists()
    onNode(hasTestTag("reorder-handle-Groceries")).performClick()
    onNode(hasTestTag("reorder-handle-Groceries")).assertIsFocused()
    onNode(hasTestTag("reorder-handle-Groceries")).performKeyInput { pressKey(Key.DirectionUp) }

    waitUntil(timeoutMillis = 5_000) {
      component.state.value.categories.map { it.name } == listOf("Groceries", "Dining")
    }
    val persisted = runBlocking { categoryRepository.observeCategories().first() }
    assertTrue(
        persisted.map { it.name } == listOf("Groceries", "Dining"),
        "Keyboard reorder should be persisted",
    )
  }
}
