@file:Suppress(
    "LongMethod",
    "LongParameterList",
    "TooManyFunctions",
    "MagicNumber",
    "MatchingDeclarationName",
    "SpreadOperator",
    "MaxLineLength",
    "UnusedPrivateFunction",
    "ComposableParamOrder",
    "PreviewNaming",
    "PreviewPublic",
    "ParameterNaming",
    "UnstableCollections",
)

package com.base.design.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.base.design.components.PocketyBottomBar
import com.base.design.components.PocketyTab
import com.base.design.components.PocketyTopBar
import com.base.design.ui.ScreenPreviews
import com.base.design.ui.theme.colorSchemeExtended

data class HomeScreenState(
    val title: String = "Home",
    val appName: String = "Pockety",
    val undecidedExpenseCount: Int = 8,
    val undecidedExpenseAmount: String = "2,340,000T",
    val monthLabel: String = "Oct 2026",
    val totalMonthlyOutflow: String = "14,850,000",
    val currencySymbol: String = "T",
    val categories: List<OutflowCategory> = emptyList(),
    val comparisonMonthLabel: String = "vs September:",
    val monthOverMonthPercent: String = "12% saved",
    val isMonthOverMonthSaved: Boolean = true,
    val previousMonthOutflow: String = "16,900,000",
    val activePockets: List<ActivePocket> = emptyList(),
    val inboxBadgeCount: Int = 8,
    val notificationCount: Int = 1,
)

/** Creates default preview state populated with reference design data. */
@Composable
fun rememberDefaultHomeScreenState(): HomeScreenState {
  val primary = MaterialTheme.colorScheme.primary
  val primaryContainer = MaterialTheme.colorScheme.primaryContainer
  val secondary = MaterialTheme.colorScheme.secondary
  val secondaryFixedDim = MaterialTheme.colorSchemeExtended.secondaryFixedDim
  val surfaceDim = MaterialTheme.colorScheme.surfaceDim

  return remember(primary, primaryContainer, secondary, secondaryFixedDim, surfaceDim) {
    HomeScreenState(
        title = "Home",
        appName = "Pockety",
        undecidedExpenseCount = 8,
        undecidedExpenseAmount = "2,340,000T",
        monthLabel = "Oct 2026",
        totalMonthlyOutflow = "14,850,000",
        currencySymbol = "T",
        categories =
            listOf(
                OutflowCategory("Food & Groceries", 0.42f, "6,200,000", primary),
                OutflowCategory("Transportation", 0.21f, "3,100,000", primaryContainer),
                OutflowCategory("Cafe & Dining", 0.16f, "2,450,000", secondary),
                OutflowCategory("Bills & Utilities", 0.12f, "1,800,000", secondaryFixedDim),
                OutflowCategory("Other", 0.09f, "1,300,000", surfaceDim),
            ),
        comparisonMonthLabel = "vs September:",
        monthOverMonthPercent = "12% saved",
        isMonthOverMonthSaved = true,
        previousMonthOutflow = "16,900,000",
        activePockets =
            listOf(
                ActivePocket("Cafe", "85,000", Icons.Default.LocalCafe),
                ActivePocket("Groceries", "450,000", Icons.Default.ShoppingCart),
            ),
        inboxBadgeCount = 8,
        notificationCount = 1,
    )
  }
}

/**
 * Main Home screen for the Pockety application.
 *
 * Assembles TopBar, BottomBar, FloatingActionButton, UndecidedExpensesBanner,
 * MonthlyOutflowHeroCard, MonthOverMonthCard, and ActivePocketsSection with adaptive centering.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeScreenState = rememberDefaultHomeScreenState(),
    onTabSelected: (PocketyTab) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onUndecidedExpensesClick: () -> Unit = {},
    onDateClick: () -> Unit = {},
    onAllMonthsClick: () -> Unit = {},
    onManagePocketsClick: () -> Unit = {},
    onPocketClick: (ActivePocket) -> Unit = {},
    onAddExpenseClick: () -> Unit = {},
) {
  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopBar(
            title = state.title,
            subtitle = state.appName,
            notificationCount = state.notificationCount,
            navigationIcon = {
              Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                  contentColor = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(start = 12.dp).size(36.dp),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                      imageVector = Icons.Default.Insights,
                      contentDescription = "Pockety logo",
                      modifier = Modifier.size(20.dp),
                  )
                }
              }
            },
            onSearchClick = onSearchClick,
            onNotificationsClick = onNotificationsClick,
            onProfileClick = onProfileClick,
        )
      },
      bottomBar = {
        PocketyBottomBar(
            selectedTab = PocketyTab.Home,
            inboxBadgeCount = state.inboxBadgeCount,
            onTabSelected = onTabSelected,
        )
      },
      floatingActionButton = {
        ExtendedFloatingActionButton(
            onClick = onAddExpenseClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            icon = {
              Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = null,
                  modifier = Modifier.size(24.dp),
              )
            },
            text = {
              Text(
                  text = "Expense",
                  style =
                      MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
              )
            },
        )
      },
  ) { paddingValues ->
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.TopCenter,
    ) {
      Column(
          modifier =
              Modifier.fillMaxWidth()
                  .widthIn(max = 640.dp)
                  .verticalScroll(rememberScrollState())
                  .padding(horizontal = 20.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        if (state.undecidedExpenseCount > 0) {
          UndecidedExpensesBanner(
              count = state.undecidedExpenseCount,
              amountFormatted = state.undecidedExpenseAmount,
              onClick = onUndecidedExpensesClick,
          )
        }

        MonthlyOutflowHeroCard(
            monthLabel = state.monthLabel,
            totalAmount = state.totalMonthlyOutflow,
            categories = state.categories,
            currencySymbol = state.currencySymbol,
            onDateClick = onDateClick,
        )

        MonthOverMonthCard(
            comparisonLabel = state.comparisonMonthLabel,
            percentageChange = state.monthOverMonthPercent,
            isSaved = state.isMonthOverMonthSaved,
            previousMonthAmount = state.previousMonthOutflow,
            currencySymbol = state.currencySymbol,
            onAllMonthsClick = onAllMonthsClick,
        )

        if (state.activePockets.isNotEmpty()) {
          ActivePocketsSection(
              pockets = state.activePockets,
              currencySymbol = state.currencySymbol,
              onManageAllClick = onManagePocketsClick,
              onPocketClick = onPocketClick,
          )
        }

        Spacer(modifier = Modifier.height(72.dp))
      }
    }
  }
}

// ==================== Screen Previews ====================

@ScreenPreviews
@Composable
private fun HomeScreenPopulatedPreview() {
  HomeScreen(
      state = rememberDefaultHomeScreenState(),
  )
}

@ScreenPreviews
@Composable
private fun HomeScreenEmptyPreview() {
  HomeScreen(
      state =
          HomeScreenState(
              title = "Home",
              appName = "Pockety",
              undecidedExpenseCount = 0,
              undecidedExpenseAmount = "0T",
              monthLabel = "Oct 2026",
              totalMonthlyOutflow = "0",
              currencySymbol = "T",
              categories = emptyList(),
              comparisonMonthLabel = "vs September:",
              monthOverMonthPercent = "0%",
              isMonthOverMonthSaved = true,
              previousMonthOutflow = "0",
              activePockets = emptyList(),
              inboxBadgeCount = 0,
              notificationCount = 0,
          ),
  )
}
