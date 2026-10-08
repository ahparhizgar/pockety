package com.base.feature.item

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
import com.base.dto.FakeItemDtoFactory
import com.base.util.injectUiRoot
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ItemListUiTest {
  @Test
  fun testItemListDisplaysItemsFromBackend() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val itemApi = koin.get<ItemApi>() as FakeItemApi
    itemApi.items = FakeItemDtoFactory.createList(2).toMutableList()

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // Start on welcome screen and navigate to login
    onNode(hasTestTag("welcome-screen")).waitForExistence().assertExists()
    onNode(hasText("Log in", ignoreCase = true)).performClick()
    onNode(hasTestTag("login-screen")).assertExists()

    // Type credentials with keyboard navigation
    onNode(isFocused()).performTextInput("user1@example.com")
    onNode(isRoot()).performKeyInput { pressKey(Key.Tab) }
    onNode(isFocused()).performTextInput("password1234")
    onNode(isRoot()).performKeyInput { pressKey(Key.Enter) }

    // Verify main screen and item list items
    onNode(hasTestTag("main-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("item-list")).waitForExistence().assertExists()
    onNode(hasText("Test Item 1")).assertExists()
    onNode(hasText("Test Item 2")).assertExists()
  }

  @Test
  fun testItemListDisplaysEmptyStateWhenNoItems() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val itemApi = koin.get<ItemApi>() as FakeItemApi
    itemApi.items = mutableListOf()

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
    onNode(hasText("No items available")).waitForExistence().assertExists()
  }
}
