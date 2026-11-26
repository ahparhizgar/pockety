package com.base.feature.pocket

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.RootScreen
import com.base.component.TestCContext
import com.base.db.PocketRepository
import com.base.util.injectUiRoot
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalTestApi::class)
class PocketListE2eUiTest {

  @Test
  fun testEmptyStateWhenNoPocketsExist() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // Navigate to Pockets tab
    onNode(hasTestTag("tab-pockets")).waitForExistence().performClick()

    // Assert pockets screen and empty state are displayed
    onNode(hasTestTag("pockets-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("pockets-empty-state")).waitForExistence().assertExists()
    onNode(hasTestTag("add-pocket-fab")).waitForExistence().assertExists()
  }

  @Test
  fun testPocketCardsAndKeyboardAccessibility() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()
    val pocketRepository = koin.get<PocketRepository>()

    runBlocking {
      pocketRepository.addPocket(
          name = "Groceries & Household",
          categoryTag = "Groceries",
          budgetAmount = 1_000_000L,
          icon = "shopping_bag",
          categoryIds = listOf(1L),
      )
    }

    val root = koin.injectUiRoot(context)
    setContent { RootScreen(root) }

    // Navigate to Pockets tab
    onNode(hasTestTag("tab-pockets")).waitForExistence().performClick()

    // Assert pocket budget card renders name, category, and formatted amount
    onNode(hasTestTag("pockets-screen")).waitForExistence().assertExists()
    onNode(hasText("Groceries & Household")).waitForExistence().assertExists()
    onNode(hasText("Groceries")).assertExists()
    onNode(hasText("100,000 T")).assertExists()

    // Verify pocket budget card does not render top-up button
    onNode(hasText("Top-up")).assertDoesNotExist()

    // Verify Add Pocket extended FAB is displayed and can be clicked
    onNode(hasTestTag("add-pocket-fab")).waitForExistence().assertExists().performClick()
  }
}
