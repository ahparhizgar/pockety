package com.base.capability.main

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
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
class MainBottomBarE2eUiTest {

  @Test
  fun testBottomBarTabsSwitchScreensAndShowBadge() = runComposeUiTest {
    val context = TestCContext()
    context.fakePermissionController.setSmsPermissionGranted(true)
    val koin = uiKoin()
    val candidateExpenseRepository = koin.get<CandidateExpenseRepository>()
    runBlocking {
      candidateExpenseRepository.addCandidate(
          sender = "Bank Mellat",
          rawBody = "Withdrawal: 12,000 Toman",
          amount = 12000L,
      )
    }

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // Bottom bar is visible
    onNode(hasTestTag("pockety-bottom-bar")).waitForExistence().assertExists()

    // Inbox badge with count 1 is visible (unmerged tree because NavigationBarItem merges
    // descendants)
    onNode(hasTestTag("inbox-badge"), useUnmergedTree = true).waitForExistence().assertExists()
    onNode(hasText("1").and(hasParent(hasTestTag("inbox-badge"))), useUnmergedTree = true)
        .assertExists()

    // Home screen is initially displayed
    onNode(hasTestTag("main-home-screen")).waitForExistence().assertExists()

    // Click Inbox tab in bottom bar
    onNode(hasTestTag("tab-inbox")).performClick()

    // Inbox screen is displayed
    onNode(hasTestTag("inbox-screen")).waitForExistence().assertExists()

    // Click Pockets tab in bottom bar
    onNode(hasTestTag("tab-pockets")).performClick()
    onNode(hasTestTag("pockets-screen")).waitForExistence().assertExists()

    // Click Analytics tab in bottom bar
    onNode(hasTestTag("tab-analytics")).performClick()
    onNode(hasTestTag("analytics-screen")).waitForExistence().assertExists()

    // Click Settings tab in bottom bar
    onNode(hasTestTag("tab-settings")).performClick()
    onNode(hasTestTag("settings-screen")).waitForExistence().assertExists()

    // Click Home tab in bottom bar
    onNode(hasTestTag("tab-home")).performClick()
    onNode(hasTestTag("main-home-screen")).waitForExistence().assertExists()
  }

  @Test
  fun testBackOnTabNavigatesToHome() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    onNode(hasTestTag("tab-settings")).waitForExistence().performClick()
    onNode(hasTestTag("settings-screen")).waitForExistence().assertExists()

    context.backDispatcher.back()

    onNode(hasTestTag("main-home-screen")).waitForExistence().assertExists()
  }
}
