package com.base.feature.inbox

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
import com.base.db.CandidateExpenseRepository
import com.base.util.injectUiRoot
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalTestApi::class)
class InboxE2eUiTest {

  @Test
  fun testSelectCandidateExpenseAndSaveReturnsToHome() = runComposeUiTest {
    val context = TestCContext()
    context.fakePermissionController.setSmsPermissionGranted(true)

    val koin = uiKoin()
    val candidateExpenseRepository = koin.get<CandidateExpenseRepository>()
    runBlocking {
      candidateExpenseRepository.addCandidate(
          sender = "Bank Mellat",
          rawBody = "Withdrawal: 45,000 Toman",
          amount = 45000L,
      )
    }

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // On Home screen: undecided banner is displayed
    onNode(hasTestTag("undecided-expenses-banner")).waitForExistence().assertExists()
    onNode(hasTestTag("undecided-expenses-banner")).performClick()

    // Navigates to Inbox screen
    onNode(hasTestTag("inbox-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("candidate-open-button")).waitForExistence().assertExists()

    // Open candidate to edit/add expense
    onNode(hasTestTag("candidate-open-button")).performClick()

    // Navigates to AddExpense screen with amount 45000 prefilled
    onNode(hasTestTag("add-expense-screen")).waitForExistence().assertExists()
    onNode(hasText("45000")).waitForExistence().assertExists()

    // Enter description and submit
    onNode(hasTestTag("description-field")).performClick()
    onNode(isFocused()).performTextInput("Groceries")
    onNode(isRoot()).performKeyInput { pressKey(Key.Enter) }

    // Navigates back to Home screen and displays recorded expense
    onNode(hasTestTag("main-home-screen")).waitForExistence().assertExists()
    onNode(hasText("Groceries")).waitForExistence().assertExists()
    onNode(hasText("45000 T")).assertExists()
  }

  @Test
  fun testDismissCandidateExpenseShowsCleanCardAndNavigatesBack() = runComposeUiTest {
    val context = TestCContext()
    context.fakePermissionController.setSmsPermissionGranted(true)

    val koin = uiKoin()
    val candidateExpenseRepository = koin.get<CandidateExpenseRepository>()
    runBlocking {
      candidateExpenseRepository.addCandidate(
          sender = "Bank Saman",
          rawBody = "Purchase 15000",
          amount = 15000L,
      )
    }

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // Click undecided expenses banner
    onNode(hasTestTag("undecided-expenses-banner")).waitForExistence().performClick()

    // On Inbox screen: dismiss candidate
    onNode(hasTestTag("inbox-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("candidate-dismiss-button")).waitForExistence().performClick()

    // Clean card appears because candidate list is now empty
    onNode(hasTestTag("inbox-clean-card")).waitForExistence().assertExists()

    // Click back button to return to Home screen
    onNode(hasTestTag("inbox-back-button")).performClick()

    // Returns to Home screen
    onNode(hasTestTag("main-home-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("empty-expenses-message")).assertExists()
  }
}
