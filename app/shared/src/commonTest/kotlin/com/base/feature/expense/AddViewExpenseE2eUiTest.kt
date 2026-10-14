package com.base.feature.expense

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.isRoot
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
class AddViewExpenseE2eUiTest {
  @Test
  fun testAddAndObserveExpenseWithKeyboardActions() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // Start on HomeScreen displaying empty state
    onNode(hasTestTag("main-home-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("empty-expenses-message")).waitForExistence().assertExists()

    // Click "Add Expense" FAB
    onNode(hasTestTag("add-expense-fab")).waitForExistence().performClick()

    // Screen transitions to AddExpenseScreen
    onNode(hasTestTag("main-add-expense-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("amount-field")).waitForExistence().assertExists()

    // Type amount, tab to description, type description, and submit with Enter key
    onNode(hasTestTag("amount-field")).performClick()
    onNode(isFocused()).performTextInput("120000")
    onNode(isRoot()).performKeyInput { pressKey(Key.Tab) }
    onNode(isFocused()).performTextInput("Dinner with friends")
    onNode(isRoot()).performKeyInput { pressKey(Key.Enter) }

    // Verify navigating back to HomeScreen and seeing recorded expense
    onNode(hasTestTag("main-home-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("expense-list")).waitForExistence().assertExists()
    onNode(hasText("Dinner with friends")).waitForExistence().assertExists()
    onNode(hasText("120000 T")).assertExists()
  }
}
