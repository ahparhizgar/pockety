package com.base.design.features.pocketdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.design.globalcomponents.PocketyTopAppBar
import com.base.design.ui.ScreenPreviews

@Composable
fun PocketDetailScreen(
    modifier: Modifier = Modifier,
    title: String = "Pocket Details",
    pocketTitle: String = "Daily Cafe & Coffee",
    balance: String = "850,000",
    currencySymbol: String = "T",
    balanceLabel: String = "Balance",
    feedbackMessage: String? = null,
    items: List<PocketLedgerItem> = samplePocketLedgerItems,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onMoreOptionsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTopUpClick: () -> Unit = {},
    onConfigureClick: () -> Unit = {},
    onDismissFeedback: () -> Unit = {},
    onDeleteExpense: (PocketLedgerItem.Expense) -> Unit = {},
) {
  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopAppBar(
            title = title,
            onBackClick = onBackClick,
        )
      },
  ) { innerPadding ->
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
          modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).padding(horizontal = 20.dp),
          contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
      ) {
        item {
          PocketHeroBalance(
              title = pocketTitle,
              balance = balance,
              currencySymbol = currencySymbol,
              balanceLabel = balanceLabel,
          )
        }

        item {
          PocketActionBar(
              onTopUpClick = onTopUpClick,
              onConfigureClick = onConfigureClick,
          )
          Spacer(modifier = Modifier.height(16.dp))
        }

        if (feedbackMessage != null) {
          item {
            PocketFeedbackBanner(
                message = feedbackMessage,
                onDismissClick = onDismissFeedback,
            )
            Spacer(modifier = Modifier.height(16.dp))
          }
        }

        item {
          val expenseCount = items.filterIsInstance<PocketLedgerItem.Expense>().size
          PocketExpensesHeader(
              title = "Expenses",
              countText = if (items.isNotEmpty()) "$expenseCount expenses" else null,
          )
          Spacer(modifier = Modifier.height(4.dp))
        }

        itemsIndexed(
            items = items,
            key = { _, item -> item.id },
        ) { index, item ->
          when (item) {
            is PocketLedgerItem.Expense -> {
              PocketTransactionRow(
                  title = item.title,
                  date = item.date,
                  amount = item.amount,
                  note = item.note,
                  icon = item.icon,
                  onDeleteClick = { onDeleteExpense(item) },
              )
            }
            is PocketLedgerItem.TopUp -> {
              PocketTopUpEventRow(
                  title = item.title,
                  date = item.date,
                  amount = item.amount,
                  note = item.note,
                  icon = item.icon,
              )
            }
          }
          if (index < items.size - 1) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
            )
          }
        }
      }
    }
  }
}

@Composable
fun PocketDetailInteractiveScreen(
    modifier: Modifier = Modifier,
    initialBalance: Int = 850_000,
    initialItems: List<PocketLedgerItem> = samplePocketLedgerItems,
) {
  var balance by remember { mutableIntStateOf(initialBalance) }
  var items by remember { mutableStateOf(initialItems) }
  var feedbackMessage by remember { mutableStateOf<String?>(null) }

  PocketDetailScreen(
      modifier = modifier,
      balance = formatThousandSeparator(balance),
      feedbackMessage = feedbackMessage,
      items = items,
      onTopUpClick = {
        balance += 100_000
        feedbackMessage = "Pocket topped up by +100,000 T!"
      },
      onDismissFeedback = { feedbackMessage = null },
      onDeleteExpense = { expense ->
        items = items.filter { it.id != expense.id }
        val numericAmount = expense.amount.filter { it.isDigit() }.toIntOrNull() ?: 0
        balance += numericAmount
        feedbackMessage =
            "Unlinked ${expense.title}. +${formatThousandSeparator(numericAmount)} T restored!"
      },
  )
}

private fun formatThousandSeparator(value: Int): String {
  val str = value.toString()
  val result = StringBuilder()
  val offset = str.length % 3
  for (i in str.indices) {
    if (i > 0 && (i - offset) % 3 == 0) {
      result.append(',')
    }
    result.append(str[i])
  }
  return result.toString()
}

@ScreenPreviews
@Composable
private fun PocketDetailScreenPreview() {
  PocketDetailScreen()
}

@ScreenPreviews
@Composable
private fun PocketDetailScreenWithFeedbackPreview() {
  PocketDetailScreen(
      feedbackMessage = "Expense removed. +85,000 T restored to pocket.",
  )
}

@ScreenPreviews
@Composable
private fun PocketDetailScreenEmptyPreview() {
  PocketDetailScreen(
      items = emptyList(),
  )
}
