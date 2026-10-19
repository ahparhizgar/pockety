package com.base.capability.pocket_management

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.RootScreen
import com.base.component.TestCContext
import com.base.db.ExpenseRepository
import com.base.db.PocketRepository
import com.base.util.injectUiRoot
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalTestApi::class)
class PocketDetailsE2eUiTest {

  @Test
  fun testPocketDetailsNavigationAndQuickTopUp() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    var pocketId = 0L

    runTest {
      val repository = koin.get<PocketRepository>()
      val expenseRepository = koin.get<ExpenseRepository>()
      pocketId =
          repository.addPocket(
              name = "Daily Cafe & Coffee",
              categoryTag = "Drinks",
              budgetAmount = 850000,
              icon = "coffee",
              color = 0xFF4A7C59,
          )
      expenseRepository.addExpense(
          amount = 50000,
          description = "Sam Cafe",
          pocketId = pocketId,
      )
    }

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // Navigate to Pockets tab
    onNode(hasTestTag("tab-pockets")).waitForExistence().performClick()

    // Tap pocket card
    onNode(hasTestTag("pocket-card-$pocketId")).waitForExistence().performClick()

    // Pocket Details Screen is displayed
    onNode(hasTestTag("pocket-details-screen")).waitForExistence().assertExists()
    onNode(hasText("Daily Cafe & Coffee")).assertExists()
    onNode(hasText("800,000")).assertExists()

    // Combined activity feed contains expense and top-up
    onNode(hasText("Sam Cafe")).assertExists()
    onNode(hasText("-50,000 T")).assertExists()
    onNode(hasText("Initial Balance")).assertExists()
    onNode(hasText("+850,000 T")).assertExists()

    // Tap action Top-up button
    onNode(hasTestTag("action-top-up-button")).performClick()

    // Quick top-up bottom sheet is displayed
    onNode(hasTestTag("quick-top-up-bottom-sheet")).waitForExistence().assertExists()

    // Select preset chip +100,000 T
    onNode(hasTestTag("preset-chip-100000")).performClick()

    // Confirm top-up
    onNode(hasTestTag("confirm-top-up-button")).performClick()

    // Balance is updated to 900,000 T, new top-up transaction appears, and feedback banner is shown
    onNode(hasText("900,000")).waitForExistence().assertExists()
    onNode(hasText("+100,000 T")).assertExists()
    onNode(hasTestTag("feedback-banner")).assertExists()
  }

  @Test
  fun testCustomAmountKeyboardInput() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    var pocketId = 0L

    runTest {
      val repository = koin.get<PocketRepository>()
      pocketId =
          repository.addPocket(
              name = "Groceries Pocket",
              categoryTag = "Food",
              budgetAmount = 500000,
              icon = "shopping_bag",
              color = 0xFF316342,
          )
    }

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    onNode(hasTestTag("tab-pockets")).waitForExistence().performClick()
    onNode(hasTestTag("pocket-card-$pocketId")).waitForExistence().performClick()
    onNode(hasTestTag("pocket-details-screen")).waitForExistence().assertExists()

    // Open top-up
    onNode(hasTestTag("action-top-up-button")).performClick()
    onNode(hasTestTag("quick-top-up-bottom-sheet")).waitForExistence().assertExists()

    // Focus custom input and type via keyboard
    onNode(hasTestTag("top-up-amount-input")).performTextInput("50000")

    // Confirm top-up
    onNode(hasTestTag("confirm-top-up-button")).performClick()

    // Balance is updated to 550,000 T
    onNode(hasText("550,000")).waitForExistence().assertExists()
  }

  @Test
  fun testNavigateToConfigureFromPocketDetails() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    var pocketId = 0L

    runTest {
      val repository = koin.get<PocketRepository>()
      pocketId =
          repository.addPocket(
              name = "Utilities",
              categoryTag = "Bills",
              budgetAmount = 200000,
              icon = "bolt",
              color = 0xFF316342,
          )
    }

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    onNode(hasTestTag("tab-pockets")).waitForExistence().performClick()
    onNode(hasTestTag("pocket-card-$pocketId")).waitForExistence().performClick()
    onNode(hasTestTag("pocket-details-screen")).waitForExistence().assertExists()

    // Tap Configure button in action bar
    onNode(hasTestTag("action-configure-button")).performClick()

    // Pocket Settings Screen is displayed
    onNode(hasTestTag("pocket-settings-screen")).waitForExistence().assertExists()

    // Press Back from settings
    onNode(hasTestTag("back-button")).performClick()

    // Back to Pocket Details
    onNode(hasTestTag("pocket-details-screen")).waitForExistence().assertExists()

    // Press Back from details
    onNode(hasTestTag("back-button")).performClick()

    // Back to Pockets Screen
    onNode(hasTestTag("pockets-screen")).waitForExistence().assertExists()
  }

  @Test
  fun testPocketDetailsEmptyState() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    var pocketId = 0L

    runTest {
      val repository = koin.get<PocketRepository>()
      pocketId =
          repository.addPocket(
              name = "Zero Balance Pocket",
              categoryTag = "Other",
              budgetAmount = 0,
              icon = "wallet",
              color = 0xFF4A7C59,
          )
    }

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    onNode(hasTestTag("tab-pockets")).waitForExistence().performClick()
    onNode(hasTestTag("pocket-card-$pocketId")).waitForExistence().performClick()
    onNode(hasTestTag("pocket-details-screen")).waitForExistence().assertExists()

    onNode(hasText("No expenses recorded yet")).assertExists()
  }
}
