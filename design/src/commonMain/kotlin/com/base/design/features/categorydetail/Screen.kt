package com.base.design.features.categorydetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.base.design.globalcomponents.PocketyTopBar
import com.base.design.ui.ScreenPreviews

@Composable
fun CategoryDetailScreen(
    modifier: Modifier = Modifier,
    categoryName: String = "Groceries & Supermarket",
    categoryIcon: ImageVector = Icons.Default.LocalGroceryStore,
    totalSpend: String = "1,450,000",
    currencySymbol: String = "T",
    budgetProgress: Float = 0.725f,
    budgetUsedText: String = "72.5% of budget used",
    budgetLeftText: String = "550,000 T left",
    expenses: List<CategoryExpense> = CategoryExpenseDefaults.defaultExpenses,
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onRenameClick: () -> Unit = {},
    onChangeIconClick: () -> Unit = {},
    onExpenseClick: (CategoryExpense) -> Unit = {},
) {
  var selectedFilter by remember { mutableStateOf(CategoryExpenseFilter.LATEST) }
  val displayedExpenses =
      remember(selectedFilter, expenses) {
        when (selectedFilter) {
          CategoryExpenseFilter.LATEST -> expenses
          CategoryExpenseFilter.AMOUNT -> {
            expenses.sortedByDescending { expense ->
              expense.amount.filter { it.isDigit() }.toLongOrNull() ?: 0L
            }
          }
        }
      }

  Scaffold(
      modifier = modifier,
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopBar(
            title = "Category Detail",
            navigationIcon = {
              IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
              }
            },
        )
      },
  ) { innerPadding ->
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
          modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).padding(horizontal = 20.dp),
          contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
          verticalArrangement = Arrangement.spacedBy(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        item {
          CategoryHeroSection(
              categoryName = categoryName,
              icon = categoryIcon,
          )
        }
        item {
          CategorySpendSummaryCard(
              totalSpend = totalSpend,
              currencySymbol = currencySymbol,
              budgetProgress = budgetProgress,
              budgetUsedText = budgetUsedText,
              budgetLeftText = budgetLeftText,
          )
        }
        item {
          CategoryQuickActionRow(
              onRenameClick = onRenameClick,
              onChangeIconClick = onChangeIconClick,
          )
        }
        item {
          Row(
              modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
                text = "Expenses",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            CategoryExpenseFilterChips(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
            )
          }
        }
        items(items = displayedExpenses, key = { it.id }) { expense ->
          CategoryExpenseItem(
              expense = expense,
              onClick = { onExpenseClick(expense) },
          )
        }
      }
    }
  }
}

@ScreenPreviews
@Composable
private fun CategoryDetailScreenPreview() {
  CategoryDetailScreen()
}

@ScreenPreviews
@Composable
private fun CategoryDetailScreenOverBudgetPreview() {
  CategoryDetailScreen(
      budgetProgress = 0.95f,
      budgetUsedText = "95% of budget used",
      budgetLeftText = "100,000 T left",
  )
}
