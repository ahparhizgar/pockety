package com.base.design.features.editexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.base.design.globalcomponents.PocketyBottomActionBar
import com.base.design.globalcomponents.PocketyTopAppBar
import com.base.design.ui.ScreenPreviews

@Composable
fun EditExpenseScreen(
    modifier: Modifier = Modifier,
    amount: String = "320,000",
    currency: String = "T",
    selectedCategoryName: String = "Cafe & Dining",
    selectedCategoryIcon: ImageVector = Icons.Default.LocalCafe,
    quickChips: List<CategoryQuickChip> = defaultQuickCategories,
    selectedChipId: String? = "cafe",
    description: String = "Latte & Croissant at Sam Cafe",
    labels: List<String> = listOf("#WorkMeeting", "#Personal"),
    pocketName: String = "Coffee Pocket",
    pocketBalance: String = "400,000 T",
    expenseTimeText: String = "October 16, 2026 • 14:32",
    smsSender: String = "+9820000",
    smsTime: String = "14:32:18",
    rawSms: String =
        "Withdrawal from acct: 3,200,000 Rials | Card: 1234****6104 | Terminal: Sam Cafe Fereshteh | Remaining: 18,450,000 Rials",
    onBackClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onAmountChange: (String) -> Unit = {},
    onChangeCategoryClick: () -> Unit = {},
    onQuickChipClick: (CategoryQuickChip) -> Unit = {},
    onDescriptionChange: (String) -> Unit = {},
    onRemoveLabel: (String) -> Unit = {},
    onAddLabelClick: () -> Unit = {},
    onUnlinkPocketClick: () -> Unit = {},
    onEditTimeClick: () -> Unit = {},
    onSaveExpense: () -> Unit = {},
    onDismissSms: () -> Unit = {},
) {
  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopAppBar(
            title = "Edit Expense",
            onBackClick = onBackClick,
        )
      },
      bottomBar = {
        PocketyBottomActionBar(
            text = "Save Expense",
            onClick = onSaveExpense,
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
                  .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        AmountHeroSection(
            amount = amount,
            currency = currency,
            onAmountChange = onAmountChange,
        )

        CategorySelectorSection(
            selectedCategoryName = selectedCategoryName,
            selectedCategoryIcon = selectedCategoryIcon,
            quickChips = quickChips,
            selectedChipId = selectedChipId,
            onChangeClick = onChangeCategoryClick,
            onQuickChipClick = onQuickChipClick,
        )

        DescriptionSection(
            description = description,
            labels = labels,
            onDescriptionChange = onDescriptionChange,
            onRemoveLabel = onRemoveLabel,
            onAddLabelClick = onAddLabelClick,
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text,
                ),
            keyboardActions = KeyboardActions.Default,
        )

        PocketSelectorCard(
            pocketName = pocketName,
            pocketBalance = pocketBalance,
            onUnlinkClick = onUnlinkPocketClick,
        )

        ExpenseTimeCard(
            timeText = expenseTimeText,
            onEditClick = onEditTimeClick,
        )

        SourceSmsCard(
            sender = smsSender,
            time = smsTime,
            rawSms = rawSms,
        )

        TextButton(
            onClick = onDismissSms,
            modifier = Modifier.fillMaxWidth(),
        ) {
          Text(
              text = "Dismiss SMS",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@ScreenPreviews
@Composable
private fun EditExpenseScreenPreview() {
  var amount by remember { mutableStateOf("320,000") }
  var description by remember { mutableStateOf("Latte & Croissant at Sam Cafe") }
  var selectedChip by remember { mutableStateOf("cafe") }
  var labels by remember { mutableStateOf(listOf("#WorkMeeting", "#Personal")) }

  EditExpenseScreen(
      amount = amount,
      onAmountChange = { amount = it },
      description = description,
      onDescriptionChange = { description = it },
      selectedChipId = selectedChip,
      onQuickChipClick = { selectedChip = it.id },
      labels = labels,
      onRemoveLabel = { labels = labels - it },
      onAddLabelClick = { labels = labels + "#New" },
  )
}

@ScreenPreviews
@Composable
private fun EditExpenseScreenGroceriesPreview() {
  EditExpenseScreen(
      amount = "1,450,000",
      selectedCategoryName = "Groceries",
      selectedCategoryIcon = Icons.Default.ShoppingBasket,
      selectedChipId = "groceries",
      description = "Hyperstar weekly shopping",
      labels = listOf("#Home", "#Groceries"),
      pocketName = "Daily Food Pocket",
      pocketBalance = "2,500,000 T",
      expenseTimeText = "October 15, 2026 • 19:45",
      smsSender = "+9820000",
      smsTime = "19:45:02",
      rawSms =
          "Withdrawal from acct: 14,500,000 Rials | Card: 1234****6104 | Terminal: Hyperstar Bakhtiari | Remaining: 17,000,000 Rials",
  )
}
