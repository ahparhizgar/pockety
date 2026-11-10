package com.base.feature.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.feature.category.add.resolveCategoryIcon
import com.base.ui.ComponentPreviews
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.monthly_outflow_label
import pockety.app.shared.generated.resources.next_month_action
import pockety.app.shared.generated.resources.no_expenses_month
import pockety.app.shared.generated.resources.previous_month_action
import pockety.app.shared.generated.resources.tab_analytics

@Composable
fun AnalyticsScreen(
    component: AnalyticsComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.testTag("analytics-screen"),
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.tab_analytics),
        )
      },
  ) { paddingValues ->
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.TopCenter,
    ) {
      Box(
          modifier = Modifier.widthIn(max = 640.dp).fillMaxSize(),
          contentAlignment = Alignment.TopCenter,
      ) {
        if (state.isLoading) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.testTag("analytics-loading"))
          }
        } else {
          AnalyticsContent(
              component = component,
              state = state,
          )
        }
      }
    }
  }
}

@Composable
private fun AnalyticsContent(
    component: AnalyticsComponent,
    state: AnalyticsState,
    modifier: Modifier = Modifier,
) {
  LazyColumn(
      modifier = modifier.fillMaxSize().testTag("analytics-list"),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item(key = "month-switcher") {
      MonthSwitcherCard(
          component = component,
          state = state,
      )
    }

    if (state.dateGroups.isEmpty()) {
      item(key = "empty-expenses") {
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
          Text(
              text = stringResource(Res.string.no_expenses_month),
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.testTag("empty-expenses-message"),
          )
        }
      }
    } else {
      state.dateGroups.forEach { group ->
        item(key = "group-header-${group.date}") {
          DateGroupHeader(
              headerTitle = group.headerTitle,
              formattedTotalAmount = group.formattedTotalAmount,
          )
        }
        items(group.expenses, key = { it.id }) { expense ->
          AnalyticsExpenseItem(
              expense = expense,
              onClick = { component.onExpenseClicked(expense.id) },
          )
        }
      }
    }
  }
}

@Composable
private fun MonthSwitcherCard(
    component: AnalyticsComponent,
    state: AnalyticsState,
    modifier: Modifier = Modifier,
) {
  Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = modifier.fillMaxWidth().testTag("month-switcher-card"),
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      MonthNavigationRow(component = component, state = state)
      MonthlyOutflowSummary(state = state)
    }
  }
}

@Composable
private fun MonthNavigationRow(
    component: AnalyticsComponent,
    state: AnalyticsState,
) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    IconButton(
        onClick = component::onPreviousMonthClicked,
        modifier = Modifier.testTag("prev-month-button"),
    ) {
      Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = stringResource(Res.string.previous_month_action),
          tint = MaterialTheme.colorScheme.onSurface,
      )
    }

    Text(
        text = state.selectedMonthLabel,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.testTag("month-label"),
    )

    IconButton(
        onClick = component::onNextMonthClicked,
        enabled = state.canNavigateNext,
        modifier = Modifier.testTag("next-month-button"),
    ) {
      Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = stringResource(Res.string.next_month_action),
          tint =
              if (state.canNavigateNext) {
                MaterialTheme.colorScheme.onSurface
              } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
              },
      )
    }
  }
}

@Composable
private fun MonthlyOutflowSummary(
    state: AnalyticsState,
) {
  Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Text(
        text = stringResource(Res.string.monthly_outflow_label),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
        text = state.formattedMonthlyOutflow,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.testTag("monthly-outflow"),
    )
  }
}

@Composable
private fun DateGroupHeader(
    headerTitle: String,
    formattedTotalAmount: String,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        text = headerTitle,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.testTag("date-group-header"),
    )
    Text(
        text = formattedTotalAmount,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.testTag("date-group-total"),
    )
  }
}

@Composable
fun AnalyticsExpenseItem(
    expense: AnalyticsExpenseUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      onClick = onClick,
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = modifier.fillMaxWidth().testTag("expense-item-${expense.id}"),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      ExpenseCategoryAvatar(expense = expense)

      Spacer(modifier = Modifier.width(16.dp))

      val titleText =
          if (expense.description.isNotBlank()) {
            expense.description
          } else {
            expense.categoryName
          }

      if (titleText != null || expense.formattedTime.isNotEmpty()) {
        Column(modifier = Modifier.weight(1f)) {
          if (titleText != null) {
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("expense-description"),
            )
          }
          if (expense.formattedTime.isNotEmpty()) {
            Text(
                text = expense.formattedTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      } else {
        Spacer(modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.width(16.dp))

      Text(
          text = expense.formattedAmount,
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.testTag("expense-amount"),
      )
    }
  }
}

@Composable
private fun ExpenseCategoryAvatar(
    expense: AnalyticsExpenseUiModel,
    modifier: Modifier = Modifier,
) {
  val categoryIcon =
      expense.categoryIcon?.let { resolveCategoryIcon(it) } ?: Icons.AutoMirrored.Filled.ReceiptLong
  val avatarBg =
      if (expense.categoryColor != null) {
        Color(expense.categoryColor).copy(alpha = 0.15f)
      } else {
        MaterialTheme.colorScheme.secondaryContainer
      }
  val avatarTint =
      if (expense.categoryColor != null) {
        Color(expense.categoryColor)
      } else {
        MaterialTheme.colorScheme.onSecondaryContainer
      }

  Surface(
      modifier = modifier.size(44.dp),
      shape = CircleShape,
      color = avatarBg,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
          imageVector = categoryIcon,
          contentDescription = expense.categoryName,
          tint = avatarTint,
          modifier = Modifier.size(22.dp),
      )
    }
  }
}

@ComponentPreviews
@Composable
private fun MonthSwitcherCardPreview() {
  MonthSwitcherCard(
      component = FakeAnalyticsComponent(),
      state = FakeAnalyticsStateFactory.create(),
  )
}

@ComponentPreviews
@Composable
private fun AnalyticsExpenseItemPreview() {
  AnalyticsExpenseItem(
      expense =
          FakeAnalyticsExpenseUiModelFactory.create(
              description = "",
              categoryName = null,
              amount = 25_000,
          ),
      onClick = {},
  )
}

@ScreenPreviews
@Composable
private fun AnalyticsScreenDefaultPreview() {
  AnalyticsScreen(
      component = FakeAnalyticsComponent(),
  )
}

@ScreenPreviews
@Composable
private fun AnalyticsScreenEmptyPreview() {
  AnalyticsScreen(
      component =
          FakeAnalyticsComponent(
              initialState =
                  FakeAnalyticsStateFactory.create(
                      monthlyOutflow = 0L,
                      dateGroups = emptyList(),
                  ),
          ),
  )
}

@ScreenPreviews
@Composable
private fun AnalyticsScreenPastMonthPreview() {
  AnalyticsScreen(
      component =
          FakeAnalyticsComponent(
              initialState =
                  FakeAnalyticsStateFactory.create(
                      selectedMonth = 9,
                      canNavigateNext = true,
                  ),
          ),
  )
}
