package com.base.capability.category_management

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.component.TestCContext
import com.base.db.CategoryRepository
import com.base.util.uiKoin
import com.base.util.waitForExistence
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalTestApi::class)
class CategorySelectionE2eUiTest {

  @Test
  fun testSearchCategoryAndSelectViaKeyboard() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val categoryRepository = koin.get<CategoryRepository>()

    runBlocking {
      categoryRepository.addCategory(name = "Dining", icon = "restaurant", color = 0xFFE57373)
      categoryRepository.addCategory(name = "Groceries", icon = "shopping_cart", color = 0xFF81C784)
    }

    var selectedCategoryId: Long? = null
    val component =
        koin
            .get<CategorySelectionComponentFactory>()
            .create(
                cContext = context,
                onCategorySelected = { selectedCategoryId = it?.id },
                onCreateCategoryClicked = {},
                onBack = {},
            )

    setContent { CategorySelectionScreen(component = component) }

    onNode(hasTestTag("category-search-field")).waitForExistence().assertExists()
    onNode(hasTestTag("category-search-field")).performClick()
    onNode(isFocused()).performTextInput("Groceries")
    onNode(isRoot()).performKeyInput { pressKey(Key.Enter) }

    onNode(hasTestTag("category-item-Groceries")).waitForExistence().performClick()
  }

  @Test
  fun testSelectUncategorizedOption() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()

    var selectedCategoryCleared = false
    val component =
        koin
            .get<CategorySelectionComponentFactory>()
            .create(
                cContext = context,
                initialSelectedCategoryId = 1L,
                onCategorySelected = { if (it == null) selectedCategoryCleared = true },
                onCreateCategoryClicked = {},
                onBack = {},
            )

    setContent { CategorySelectionScreen(component = component) }

    onNode(hasTestTag("uncategorized-item")).waitForExistence().performClick()
    selectedCategoryCleared shouldBe true
  }

  @Test
  fun testNavigateToCreateNewCategory() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()

    var createCategoryTriggered = false
    val component =
        koin
            .get<CategorySelectionComponentFactory>()
            .create(
                cContext = context,
                onCategorySelected = {},
                onCreateCategoryClicked = { createCategoryTriggered = true },
                onBack = {},
            )

    setContent { CategorySelectionScreen(component = component) }

    onNode(hasTestTag("create-category-button")).waitForExistence().performClick()

    createCategoryTriggered shouldBe true
  }
}
