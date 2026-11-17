package com.base.feature.analytics

import androidx.compose.foundation.focusable
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
import com.base.LocalAppLanguage
import com.base.calendar.CalendarDate
import com.base.calendar.CalendarType
import com.base.feature.category.add.resolveCategoryIcon
import com.base.feature.pocket.details.formatAmount
import com.base.feature.settings.AppLanguage
import com.base.ui.ComponentPreviews
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.analytics_aban
import pockety.app.shared.generated.resources.analytics_aban_fa
import pockety.app.shared.generated.resources.analytics_april
import pockety.app.shared.generated.resources.analytics_april_fa
import pockety.app.shared.generated.resources.analytics_august
import pockety.app.shared.generated.resources.analytics_august_fa
import pockety.app.shared.generated.resources.analytics_azar
import pockety.app.shared.generated.resources.analytics_azar_fa
import pockety.app.shared.generated.resources.analytics_bahman
import pockety.app.shared.generated.resources.analytics_bahman_fa
import pockety.app.shared.generated.resources.analytics_currency_symbol_fa
import pockety.app.shared.generated.resources.analytics_december
import pockety.app.shared.generated.resources.analytics_december_fa
import pockety.app.shared.generated.resources.analytics_dey
import pockety.app.shared.generated.resources.analytics_dey_fa
import pockety.app.shared.generated.resources.analytics_esfand
import pockety.app.shared.generated.resources.analytics_esfand_fa
import pockety.app.shared.generated.resources.analytics_farvardin
import pockety.app.shared.generated.resources.analytics_farvardin_fa
import pockety.app.shared.generated.resources.analytics_february
import pockety.app.shared.generated.resources.analytics_february_fa
import pockety.app.shared.generated.resources.analytics_january
import pockety.app.shared.generated.resources.analytics_january_fa
import pockety.app.shared.generated.resources.analytics_july
import pockety.app.shared.generated.resources.analytics_july_fa
import pockety.app.shared.generated.resources.analytics_june
import pockety.app.shared.generated.resources.analytics_june_fa
import pockety.app.shared.generated.resources.analytics_khordad
import pockety.app.shared.generated.resources.analytics_khordad_fa
import pockety.app.shared.generated.resources.analytics_march
import pockety.app.shared.generated.resources.analytics_march_fa
import pockety.app.shared.generated.resources.analytics_may
import pockety.app.shared.generated.resources.analytics_may_fa
import pockety.app.shared.generated.resources.analytics_mehr
import pockety.app.shared.generated.resources.analytics_mehr_fa
import pockety.app.shared.generated.resources.analytics_month_year
import pockety.app.shared.generated.resources.analytics_month_year_fa
import pockety.app.shared.generated.resources.analytics_monthly_outflow_label_fa
import pockety.app.shared.generated.resources.analytics_mordad
import pockety.app.shared.generated.resources.analytics_mordad_fa
import pockety.app.shared.generated.resources.analytics_next_month_action_fa
import pockety.app.shared.generated.resources.analytics_no_expenses_month_fa
import pockety.app.shared.generated.resources.analytics_november
import pockety.app.shared.generated.resources.analytics_november_fa
import pockety.app.shared.generated.resources.analytics_october
import pockety.app.shared.generated.resources.analytics_october_fa
import pockety.app.shared.generated.resources.analytics_ordibehesht
import pockety.app.shared.generated.resources.analytics_ordibehesht_fa
import pockety.app.shared.generated.resources.analytics_other_date
import pockety.app.shared.generated.resources.analytics_other_date_fa
import pockety.app.shared.generated.resources.analytics_previous_month_action_fa
import pockety.app.shared.generated.resources.analytics_september
import pockety.app.shared.generated.resources.analytics_september_fa
import pockety.app.shared.generated.resources.analytics_shahrivar
import pockety.app.shared.generated.resources.analytics_shahrivar_fa
import pockety.app.shared.generated.resources.analytics_tir
import pockety.app.shared.generated.resources.analytics_tir_fa
import pockety.app.shared.generated.resources.analytics_title_fa
import pockety.app.shared.generated.resources.analytics_today
import pockety.app.shared.generated.resources.analytics_today_fa
import pockety.app.shared.generated.resources.analytics_yesterday
import pockety.app.shared.generated.resources.analytics_yesterday_fa
import pockety.app.shared.generated.resources.currency_symbol
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
            title = analyticsStaticText(Res.string.tab_analytics, Res.string.analytics_title_fa),
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
              text =
                  analyticsStaticText(
                      Res.string.no_expenses_month,
                      Res.string.analytics_no_expenses_month_fa,
                  ),
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
              date = group.date,
              relativeDate = group.relativeDate,
              totalAmount = group.totalAmount,
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
          contentDescription =
              analyticsStaticText(
                  Res.string.previous_month_action,
                  Res.string.analytics_previous_month_action_fa,
              ),
          tint = MaterialTheme.colorScheme.onSurface,
      )
    }

    Text(
        text = analyticsMonthLabel(state),
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
          contentDescription =
              analyticsStaticText(
                  Res.string.next_month_action,
                  Res.string.analytics_next_month_action_fa,
              ),
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
        text =
            analyticsStaticText(
                Res.string.monthly_outflow_label,
                Res.string.analytics_monthly_outflow_label_fa,
            ),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
        text = "${formatAmount(state.monthlyOutflow)} ${analyticsCurrencySymbol()}",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.testTag("monthly-outflow"),
    )
  }
}

@Composable
private fun analyticsStaticText(
    english: StringResource,
    persian: StringResource,
): String =
    stringResource(if (LocalAppLanguage.current == AppLanguage.PERSIAN) persian else english)

@Composable
private fun analyticsCurrencySymbol(): String =
    stringResource(
        if (LocalAppLanguage.current == AppLanguage.PERSIAN) Res.string.analytics_currency_symbol_fa
        else Res.string.currency_symbol
    )

@Composable
private fun analyticsMonthLabel(state: AnalyticsState): String {
  val month = analyticsMonthName(state.selectedMonth, state.calendarType)
  val isPersian = LocalAppLanguage.current == AppLanguage.PERSIAN
  return stringResource(
      if (isPersian) Res.string.analytics_month_year_fa else Res.string.analytics_month_year,
      month,
      state.selectedYear,
  )
}

@Composable
private fun analyticsMonthName(month: Int, calendarType: CalendarType): String {
  val isPersian = LocalAppLanguage.current == AppLanguage.PERSIAN
  val names =
      when (calendarType) {
        CalendarType.PERSIAN -> if (isPersian) persianMonthsFa else persianMonths
        CalendarType.GREGORIAN -> if (isPersian) gregorianMonthsFa else gregorianMonths
      }
  val key = names.getOrElse(month - 1) { names.first() }
  return stringResource(key)
}

private val gregorianMonths =
    listOf(
        Res.string.analytics_january,
        Res.string.analytics_february,
        Res.string.analytics_march,
        Res.string.analytics_april,
        Res.string.analytics_may,
        Res.string.analytics_june,
        Res.string.analytics_july,
        Res.string.analytics_august,
        Res.string.analytics_september,
        Res.string.analytics_october,
        Res.string.analytics_november,
        Res.string.analytics_december,
    )
private val gregorianMonthsFa =
    listOf(
        Res.string.analytics_january_fa,
        Res.string.analytics_february_fa,
        Res.string.analytics_march_fa,
        Res.string.analytics_april_fa,
        Res.string.analytics_may_fa,
        Res.string.analytics_june_fa,
        Res.string.analytics_july_fa,
        Res.string.analytics_august_fa,
        Res.string.analytics_september_fa,
        Res.string.analytics_october_fa,
        Res.string.analytics_november_fa,
        Res.string.analytics_december_fa,
    )
private val persianMonths =
    listOf(
        Res.string.analytics_farvardin,
        Res.string.analytics_ordibehesht,
        Res.string.analytics_khordad,
        Res.string.analytics_tir,
        Res.string.analytics_mordad,
        Res.string.analytics_shahrivar,
        Res.string.analytics_mehr,
        Res.string.analytics_aban,
        Res.string.analytics_azar,
        Res.string.analytics_dey,
        Res.string.analytics_bahman,
        Res.string.analytics_esfand,
    )
private val persianMonthsFa =
    listOf(
        Res.string.analytics_farvardin_fa,
        Res.string.analytics_ordibehesht_fa,
        Res.string.analytics_khordad_fa,
        Res.string.analytics_tir_fa,
        Res.string.analytics_mordad_fa,
        Res.string.analytics_shahrivar_fa,
        Res.string.analytics_mehr_fa,
        Res.string.analytics_aban_fa,
        Res.string.analytics_azar_fa,
        Res.string.analytics_dey_fa,
        Res.string.analytics_bahman_fa,
        Res.string.analytics_esfand_fa,
    )

@Composable
private fun DateGroupHeader(
    date: CalendarDate,
    relativeDate: AnalyticsRelativeDate,
    totalAmount: Long,
    modifier: Modifier = Modifier,
) {
  val isPersian = LocalAppLanguage.current == AppLanguage.PERSIAN
  val month = analyticsMonthName(date.month, date.type)
  val headerTitle =
      when (relativeDate) {
        AnalyticsRelativeDate.TODAY ->
            stringResource(
                if (isPersian) Res.string.analytics_today_fa else Res.string.analytics_today,
                date.day,
                month,
            )
        AnalyticsRelativeDate.YESTERDAY ->
            stringResource(
                if (isPersian) Res.string.analytics_yesterday_fa
                else Res.string.analytics_yesterday,
                date.day,
                month,
            )
        AnalyticsRelativeDate.OTHER ->
            stringResource(
                if (isPersian) Res.string.analytics_other_date_fa
                else Res.string.analytics_other_date,
                date.day,
                month,
                date.year,
            )
      }
  val formattedTotalAmount = "${formatAmount(totalAmount)} ${analyticsCurrencySymbol()}"
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
      modifier = modifier.fillMaxWidth().focusable().testTag("expense-item-${expense.id}"),
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
          text = "-${formatAmount(expense.amount)} ${analyticsCurrencySymbol()}",
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
