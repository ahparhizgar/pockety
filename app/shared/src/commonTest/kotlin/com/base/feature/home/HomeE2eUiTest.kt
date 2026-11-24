package com.base.feature.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.util.waitForExistence
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class HomeE2eUiTest {

  private fun populatedState() =
      HomeState(
          isSmsPermissionGranted = true,
          pendingCandidateCount = 2,
          pendingCandidateSum = 100_000L,
          currentYear = 2026,
          currentMonth = 11,
          previousYear = 2026,
          previousMonth = 10,
          monthlyOutflow = 900_000L,
          previousMonthOutflow = 1_000_000L,
          monthOverMonthPercent = 10,
          isMonthOverMonthSaved = true,
          topCategories =
              listOf(
                  HomeCategoryOutflow(
                      kind = HomeCategoryKind.CATEGORY,
                      name = "Food",
                      amount = 900_000L,
                      share = 1f,
                      color = 0xFF316342,
                  ),
              ),
          pockets =
              listOf(
                  HomePocketUi(id = 1, name = "Cafe", balance = 85_000L, icon = "coffee"),
                  HomePocketUi(
                      id = 2,
                      name = "Groceries",
                      balance = 450_000L,
                      icon = "shopping_cart",
                  ),
                  HomePocketUi(id = 3, name = "Travel", balance = 900_000L, icon = "beach_access"),
              ),
      )

  @Test
  fun testHomeOverviewSectionsRender() = runComposeUiTest {
    val component = FakeHomeComponent(initialState = populatedState())

    setContent { HomeScreen(component = component) }

    onNode(hasTestTag("home-monthly-outflow-card")).waitForExistence().assertExists()
    onNode(hasTestTag("home-date-pill")).assertExists()
    onNode(hasTestTag("home-mom-card")).performScrollTo().assertExists()
    onNode(hasTestTag("home-mom-badge")).assertExists()
    onNode(hasTestTag("home-pockets-section")).performScrollTo().assertExists()
    onNode(hasTestTag("home-pocket-1")).assertExists()
    onNode(hasTestTag("home-pocket-2")).assertExists()
    onNode(hasTestTag("home-pocket-3")).performScrollTo().assertExists()
  }

  @Test
  fun testHomeOverviewActions() = runComposeUiTest {
    val component = FakeHomeComponent(initialState = populatedState())

    setContent { HomeScreen(component = component) }

    onNode(hasTestTag("home-date-pill")).waitForExistence().performClick()
    component.allMonthsClickedCount shouldBe 1

    onNode(hasTestTag("home-all-months")).performScrollTo().performClick()
    component.allMonthsClickedCount shouldBe 2

    onNode(hasTestTag("home-mom-card")).performScrollTo().performClick()
    component.allMonthsClickedCount shouldBe 3

    onNode(hasTestTag("home-manage-pockets")).performScrollTo().performClick()
    component.managePocketsClickedCount shouldBe 1

    onNode(hasTestTag("home-pocket-2")).performScrollTo().performClick()
    component.clickedPocketIds shouldBe listOf(2L)
  }

  @Test
  fun testHomeEmptyOverviewHidesPocketsAndBadge() = runComposeUiTest {
    val component =
        FakeHomeComponent(
            initialState =
                HomeState(
                    isSmsPermissionGranted = true,
                    currentYear = 2026,
                    currentMonth = 11,
                    previousYear = 2026,
                    previousMonth = 10,
                ),
        )

    setContent { HomeScreen(component = component) }

    onNode(hasTestTag("home-monthly-outflow-card")).waitForExistence().assertExists()
    assertTrue(
        onAllNodes(hasTestTag("home-pockets-section")).fetchSemanticsNodes().isEmpty(),
        "Pockets section must be hidden when there are no pockets",
    )
    assertTrue(
        onAllNodes(hasTestTag("home-mom-badge")).fetchSemanticsNodes().isEmpty(),
        "MoM badge must be hidden when the previous month total is zero",
    )
  }
}
