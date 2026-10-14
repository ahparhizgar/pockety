package com.base.feature.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.RootScreen
import com.base.component.TestCContext
import com.base.util.injectUiRoot
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SmsPermissionIntegrationUiTest {

  @Test
  fun testSmsPermissionBannerDisplayedAndClickRequestsPermission() = runComposeUiTest {
    val context = TestCContext()
    context.fakePermissionController.setSmsPermissionGranted(false)

    val koin = uiKoin()
    val root = koin.injectUiRoot(context)

    setContent { RootScreen(root) }

    // Verify SmsPermissionBanner is visible
    onNode(hasTestTag("sms-permission-banner")).waitForExistence().assertExists()
    onNode(hasTestTag("undecided-expenses-banner")).assertDoesNotExist()

    // Click banner
    onNode(hasTestTag("sms-permission-banner")).performClick()

    // Grant permission
    context.fakePermissionController.setSmsPermissionGranted(true)

    // Verify it switches to UndecidedExpensesBanner
    onNode(hasTestTag("undecided-expenses-banner")).waitForExistence().assertExists()
    onNode(hasTestTag("sms-permission-banner")).assertDoesNotExist()
  }

  @Test
  fun testUndecidedExpensesBannerDisplayedWhenPermissionInitiallyGranted() = runComposeUiTest {
    val context = TestCContext()
    context.fakePermissionController.setSmsPermissionGranted(true)

    val koin = uiKoin()
    val root = koin.injectUiRoot(context)

    setContent { RootScreen(root) }

    onNode(hasTestTag("undecided-expenses-banner")).waitForExistence().assertExists()
    onNode(hasTestTag("sms-permission-banner")).assertDoesNotExist()
  }
}
