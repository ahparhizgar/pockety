package com.base.feature.expense

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.RootScreen
import com.base.component.TestCContext
import com.base.db.ExpenseRepository
import com.base.db.FakePocketItemModelFactory
import com.base.feature.expense.add.AddExpenseScreen
import com.base.feature.expense.add.AddExpenseState
import com.base.feature.expense.add.FakeAddExpenseComponent
import com.base.util.assertAnalyticsComponent
import com.base.util.assertMainComponent
import com.base.util.injectUiRoot
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalTestApi::class)
class AddViewExpenseE2eUiTest {

  @Test
  fun testAddAndObserveExpenseOnAnalyticsTabWithKeyboardActions() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // Start on HomeScreen
    onNode(hasTestTag("main-home-screen")).waitForExistence().assertExists()

    // Click "Add Expense" FAB
    onNode(hasTestTag("add-expense-fab")).waitForExistence().performClick()

    // Screen transitions to AddExpenseScreen
    onNode(hasTestTag("add-expense-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("amount-field")).waitForExistence().assertExists()

    // Type amount, move focus to description, type description, and submit with Enter key
    onNode(hasTestTag("amount-field")).performClick()
    onNode(isFocused()).performTextInput("100")
    onNode(hasTestTag("description-field")).performClick()
    onNode(isFocused()).performTextInput("Dinner with friends")
    onNode(isRoot()).performKeyInput { pressKey(Key.Enter) }

    // Verify navigating back to HomeScreen
    onNode(hasTestTag("main-home-screen")).waitForExistence().assertExists()

    // Switch to Analytics tab via bottom navigation
    onNode(hasTestTag("tab-analytics")).waitForExistence().performClick()

    // Verify AnalyticsScreen is displayed with month card, date group header, and recorded expense
    onNode(hasTestTag("analytics-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("month-switcher-card")).assertExists()
    onNode(hasTestTag("date-group-header")).assertExists()
    onNode(hasText("Dinner with friends")).assertExists()
    onNode(hasText("-100 T")).assertExists()
    assertEquals(
        1000L,
        root.assertMainComponent().assertAnalyticsComponent().state.value.monthlyOutflow,
    )
  }

  @Test
  fun testEditExistingExpenseFromAnalyticsTab() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val expenseRepo = koin.get<ExpenseRepository>()
    runBlocking {
      expenseRepo.addExpense(
          amount = 50000L,
          description = "Original Coffee",
      )
    }

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // Switch to Analytics tab
    onNode(hasTestTag("tab-analytics")).waitForExistence().performClick()
    onNode(hasTestTag("analytics-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("analytics-list")).performScrollToNode(hasText("Original Coffee"))
    onNode(hasText("Original Coffee")).waitForExistence().assertExists()

    // Click expense item to edit
    onNode(hasText("Original Coffee")).performClick()

    // Verify AddExpenseScreen opens in edit mode
    onNode(hasTestTag("add-expense-screen")).waitForExistence().assertExists()
    onNode(hasText("Edit Expense")).assertExists()
    onNode(hasTestTag("delete-button")).assertExists()

    // Append to description and submit
    onNode(hasTestTag("description-field")).performClick()
    onNode(isFocused()).performTextInput(" & Croissant")
    onNode(hasTestTag("save-button")).performClick()

    // Verify returning to AnalyticsScreen and updated description is shown
    onNode(hasTestTag("analytics-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("analytics-list"))
        .performScrollToNode(hasText("Original Coffee & Croissant"))
    onNode(hasText("Original Coffee & Croissant")).waitForExistence().assertExists()
  }

  @Test
  fun testDeleteExpenseViaTopAppBarAction() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val expenseRepo = koin.get<ExpenseRepository>()
    runBlocking {
      expenseRepo.addExpense(
          amount = 75000L,
          description = "To Be Deleted",
      )
    }

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // Switch to Analytics tab
    onNode(hasTestTag("tab-analytics")).waitForExistence().performClick()
    onNode(hasTestTag("analytics-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("analytics-list")).performScrollToNode(hasText("To Be Deleted"))
    onNode(hasText("To Be Deleted")).waitForExistence().assertExists()

    // Click expense item to edit
    onNode(hasText("To Be Deleted")).performClick()

    // AddExpenseScreen opens in edit mode with delete button
    onNode(hasTestTag("add-expense-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("delete-button")).waitForExistence().assertExists()

    // Click delete action in TopAppBar
    onNode(hasTestTag("delete-button")).performClick()

    // Navigates back to AnalyticsScreen showing empty state
    onNode(hasTestTag("analytics-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("empty-expenses-message")).waitForExistence().assertExists()
    onNode(hasText("To Be Deleted")).assertDoesNotExist()
  }

  @Test
  fun testPocketCardRenderingAndUnlink() = runComposeUiTest {
    val pocket =
        FakePocketItemModelFactory.create(
            name = "Daily Cafe & Coffee",
            remainingBalance = 400_000,
        )
    val component =
        FakeAddExpenseComponent(
            initialState =
                AddExpenseState(
                    amountText = "50000",
                    selectedPocket = pocket,
                )
        )

    setContent { AddExpenseScreen(component) }

    onNode(hasTestTag("pocket-selector-section")).waitForExistence().assertExists()
    onNode(hasText("Daily Cafe & Coffee")).assertExists()
    onNode(hasText("Balance: 40,000 T")).assertExists()
    onNode(hasTestTag("clear-pocket-btn")).assertExists()

    onNode(hasTestTag("clear-pocket-btn")).performClick()

    onNode(hasText("Tap to link a pocket")).assertExists()
    onNode(hasText("Auto-deduct from pocket budget")).assertExists()
  }

  @Test
  fun testPocketCardKeyboardAccessibility() = runComposeUiTest {
    var selectPocketClicked = false
    val component =
        FakeAddExpenseComponent(
            initialState = AddExpenseState(amountText = "10000"),
            onSelectPocket = { selectPocketClicked = true },
        )

    setContent { AddExpenseScreen(component) }

    onNode(hasTestTag("selected-pocket-card")).waitForExistence().assertExists().performClick()
    assertTrue(selectPocketClicked)
  }
}
