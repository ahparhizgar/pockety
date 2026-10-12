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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.design.components.PocketyBottomBar
import com.base.design.components.PocketyTab
import com.base.design.components.PocketyTopBar
import com.base.design.ui.ScreenPreviews

@Composable
fun MonthDetailScreen(
    modifier: Modifier = Modifier,
    categories: List<CategorySpending> = defaultCategories,
    expenseGroups: List<ExpenseDateGroup> = defaultExpenseGroups,
    currentMonth: String = "October 2026",
    previousMonthLabel: String = "Sep",
    nextMonthLabel: String = "Nov",
    isNextEnabled: Boolean = false,
    subtitle: String = "Monthly Cash Flow",
    totalSpendAmount: String = "14,850,000",
    currency: String = "T",
    trendText: String = "12% vs Sep (16,920,000 T)",
    totalExpensesCount: Int = 48,
    selectedTab: PocketyTab = PocketyTab.Analytics,
    onTabSelected: (PocketyTab) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onPreviousMonthClick: () -> Unit = {},
    onNextMonthClick: () -> Unit = {},
    onMonthPickerClick: () -> Unit = {},
    onCategoryClick: (CategorySpending) -> Unit = {},
    onExpenseClick: (ExpenseItem) -> Unit = {},
) {
  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopBar(
            title = "Expenses",
            subtitle = "Pockety",
            onSearchClick = onSearchClick,
            onNotificationsClick = onNotificationsClick,
            onProfileClick = onProfileClick,
        )
      },
      bottomBar = {
        PocketyBottomBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
        )
      },
  ) { innerPadding ->
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
          modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).padding(horizontal = 16.dp),
          contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
          verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        item(key = "month_switcher") {
          MonthSwitcherCard(
              currentMonth = currentMonth,
              previousMonthLabel = previousMonthLabel,
              nextMonthLabel = nextMonthLabel,
              isNextEnabled = isNextEnabled,
              subtitle = subtitle,
              totalSpendAmount = totalSpendAmount,
              currency = currency,
              trendText = trendText,
              onPreviousMonthClick = onPreviousMonthClick,
              onNextMonthClick = onNextMonthClick,
              onMonthPickerClick = onMonthPickerClick,
          )
        }

        item(key = "spending_donut") {
          SpendingDonutChartSection(
              categories = categories,
              onCategoryClick = onCategoryClick,
          )
        }

        item(key = "expenses_header") {
          ExpensesLedgerHeader(
              totalItemsCount = totalExpensesCount,
          )
        }

        items(expenseGroups, key = { it.dateLabel }) { group ->
          Column(
              verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            DateGroupHeader(
                dateLabel = group.dateLabel,
                totalAmountFormatted = group.totalAmountFormatted,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
              group.items.forEachIndexed { index, item ->
                ExpenseLedgerItem(
                    item = item,
                    onClick = { onExpenseClick(item) },
                )
                if (index < group.items.size - 1) {
                  HorizontalDivider(
                      color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
                      thickness = 0.5.dp,
                      modifier = Modifier.padding(horizontal = 4.dp),
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@ScreenPreviews
@Composable
private fun MonthDetailScreenPreview() {
  MonthDetailScreen()
}

@ScreenPreviews
@Composable
private fun MonthDetailScreenSeptemberPreview() {
  MonthDetailScreen(
      currentMonth = "September 2026",
      previousMonthLabel = "Aug",
      nextMonthLabel = "Oct",
      isNextEnabled = true,
      totalSpendAmount = "16,920,000",
      trendText = "4% vs Aug (16,250,000 T)",
  )
}
