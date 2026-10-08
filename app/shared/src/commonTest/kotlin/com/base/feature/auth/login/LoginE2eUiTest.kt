package com.base.feature.auth.login

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
class LoginE2eUiTest {
  @Test
  fun testLoginFlowHappyPath() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    onNode(hasTestTag("welcome-screen")).waitForExistence().assertExists()
    onNode(hasText("Log in", ignoreCase = true)).performClick()
    onNode(hasTestTag("login-screen")).assertExists()
    onNode(isFocused()).performTextInput("user1@example.com")
    onNode(isRoot()).performKeyInput { pressKey(Key.Tab) }
    onNode(isFocused()).performTextInput("password1234")
    onNode(isRoot()).performKeyInput { pressKey(Key.Enter) }
    onNode(hasTestTag("main-screen")).waitForExistence().assertExists()
  }

  @Test
  fun testLoginValidationErrors() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    onNode(hasTestTag("welcome-screen")).waitForExistence().assertExists()
    onNode(hasText("Log in", ignoreCase = true)).performClick()
    onNode(hasTestTag("login-screen")).assertExists()

    // Type invalid email and invalid password using keyboard navigation
    onNode(isFocused()).performTextInput("invalid-email")
    onNode(isRoot()).performKeyInput { pressKey(Key.Tab) }
    onNode(isFocused()).performTextInput("short")
    onNode(isRoot()).performKeyInput { pressKey(Key.Enter) }

    // Verify validation errors are shown and user remains on login screen
    onNode(hasText("Enter a valid email address")).assertExists()
    onNode(hasText("Password must be at least 8 characters")).assertExists()
    onNode(hasTestTag("login-screen")).assertExists()
  }
}
