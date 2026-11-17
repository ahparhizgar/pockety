package com.base.feature.category.management

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.RootScreen
import com.base.component.TestCContext
import com.base.util.injectUiRoot
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class CategoryManagementE2eUiTest {
  @Test
  fun settingsCategoryCanBeCreatedAndReturnedToUsingKeyboard() = runComposeUiTest {
    val context = TestCContext()
    val root = uiKoin().injectUiRoot(context)
    setContent { RootScreen(root) }

    onNode(hasTestTag("tab-settings")).waitForExistence().performClick()
    onNode(hasTestTag("settings-navigation-categories")).performClick()
    onNode(hasTestTag("category-management-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("add-category-button")).performClick()
    onNode(hasTestTag("category-name-field")).waitForExistence().performTextInput("Groceries")
    onNode(hasTestTag("category-name-field")).performKeyInput { pressKey(Key.Enter) }
    onNode(hasText("Groceries")).waitForExistence().assertExists()
    onNode(hasTestTag("back-button")).performClick()
    onNode(hasTestTag("settings-screen")).waitForExistence().assertExists()
  }
}
