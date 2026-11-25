package com.base.feature.analytics

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.LocalAppLanguage
import com.base.calendar.CalendarDate
import com.base.calendar.CalendarType
import com.base.feature.category.add.resolveCategoryIcon
import com.base.feature.settings.AppLanguage
import com.base.ui.ComponentPreviews
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import com.base.ui.money.moneyLabel
import kotlin.math.abs
import kotlin.math.roundToInt
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
import pockety.app.shared.generated.resources.analytics_cash_flow_subtitle
import pockety.app.shared.generated.resources.analytics_cash_flow_subtitle_fa
import pockety.app.shared.generated.resources.analytics_category_spending_title
import pockety.app.shared.generated.resources.analytics_category_spending_title_fa
import pockety.app.shared.generated.resources.analytics_december
import pockety.app.shared.generated.resources.analytics_december_fa
import pockety.app.shared.generated.resources.analytics_dey
import pockety.app.shared.generated.resources.analytics_dey_fa
import pockety.app.shared.generated.resources.analytics_esfand
import pockety.app.shared.generated.resources.analytics_esfand_fa
import pockety.app.shared.generated.resources.analytics_expenses_title
import pockety.app.shared.generated.resources.analytics_expenses_title_fa
import pockety.app.shared.generated.resources.analytics_farvardin
import pockety.app.shared.generated.resources.analytics_farvardin_fa
import pockety.app.shared.generated.resources.analytics_february
import pockety.app.shared.generated.resources.analytics_february_fa
import pockety.app.shared.generated.resources.analytics_items_count
import pockety.app.shared.generated.resources.analytics_items_count_fa
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
import pockety.app.shared.generated.resources.analytics_top_spent
import pockety.app.shared.generated.resources.analytics_top_spent_fa
import pockety.app.shared.generated.resources.analytics_trend_vs
import pockety.app.shared.generated.resources.analytics_trend_vs_fa
import pockety.app.shared.generated.resources.analytics_yesterday
import pockety.app.shared.generated.resources.analytics_yesterday_fa
import pockety.app.shared.generated.resources.next_month_action
import pockety.app.shared.generated.resources.no_expenses_month
import pockety.app.shared.generated.resources.previous_month_action
import pockety.app.shared.generated.resources.tab_analytics
import pockety.app.shared.generated.resources.uncategorized_title

private const val PERCENT_WHOLE = 100.0
private const val DONUT_FULL_CIRCLE = 360f
private const val DONUT_START_ANGLE = -90f
private const val DONUT_GAP_ANGLE = 4f
private const val MONTHS_IN_YEAR = 12

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
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp),
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
      if (state.categorySpending.isNotEmpty()) {
        item(key = "category-spending") { CategorySpendingCard(state = state) }
      }
      item(key = "expenses-header") {
        ExpensesLedgerHeader(totalItemsCount = state.allExpenses.size)
      }
      state.dateGroups.forEach { group ->
        item(key = "group-${group.date}") {
          DateGroupSection(component = component, group = group)
        }
      }
    }
  }
}

@Composable
private fun DateGroupSection(
    component: AnalyticsComponent,
    group: AnalyticsDateGroupUiModel,
) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    DateGroupHeader(
        date = group.date,
        relativeDate = group.relativeDate,
        totalAmount = group.totalAmount,
    )
    Column(modifier = Modifier.fillMaxWidth()) {
      group.expenses.forEachIndexed { index, expense ->
        AnalyticsExpenseItem(
            expense = expense,
            onClick = { component.onExpenseClicked(expense.id) },
        )
        if (index < group.expenses.size - 1) {
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
        horizontalAlignment = Alignment.CenterHorizontally,
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
    MonthNavPill(
        label = analyticsMonthName(adjacentMonth(state.selectedMonth, -1), state.calendarType),
        isPrevious = true,
        enabled = true,
        onClick = component::onPreviousMonthClicked,
        modifier = Modifier.testTag("prev-month-button"),
    )

    MonthTitleBlock(state = state)

    MonthNavPill(
        label = analyticsMonthName(adjacentMonth(state.selectedMonth, 1), state.calendarType),
        isPrevious = false,
        enabled = state.canNavigateNext,
        onClick = component::onNextMonthClicked,
        modifier = Modifier.testTag("next-month-button"),
    )
  }
}

@Composable
private fun MonthTitleBlock(state: AnalyticsState) {
Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
            text = analyticsMonthLabel(state),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("month-label"),
        )
        Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.secondary,
        )
      }
      Text(
          text =
              analyticsStaticText(
                  Res.string.analytics_cash_flow_subtitle,
                  Res.string.analytics_cash_flow_subtitle_fa,
              ),
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.secondary,
      )
    }
}

@Composable
private fun MonthNavPill(
    label: String,
    isPrevious: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val contentDescription =
      if (isPrevious) {
        analyticsStaticText(
            Res.string.previous_month_action,
            Res.string.analytics_previous_month_action_fa,
        )
      } else {
        analyticsStaticText(
            Res.string.next_month_action,
            Res.string.analytics_next_month_action_fa,
        )
      }
  val contentColor =
      if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
      else MaterialTheme.colorScheme.outlineVariant
  Surface(
      onClick = onClick,
      enabled = enabled,
      shape = CircleShape,
      color =
          if (enabled) MaterialTheme.colorScheme.surfaceContainerHigh
          else MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f),
      modifier = modifier,
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      if (isPrevious) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp),
            tint = contentColor,
        )
      }
      Text(
          text = label,
          style = MaterialTheme.typography.labelSmall,
          color = contentColor,
      )
      if (!isPrevious) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp),
            tint = contentColor,
        )
      }
    }
  }
}

private fun adjacentMonth(month: Int, delta: Int): Int =
    ((month - 1 + delta + MONTHS_IN_YEAR) % MONTHS_IN_YEAR) + 1

@Composable
private fun MonthlyOutflowSummary(
    state: AnalyticsState,
) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(vertical = 4.dp),
  ) {
    Text(
        text = moneyLabel(state.monthlyOutflow),
        style = MaterialTheme.typography.displayLarge.copy(fontSize = 36.sp),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.testTag("monthly-outflow"),
    )
    if (state.previousMonthOutflow > 0L) {
      TrendPill(state = state)
    }
  }
}

@Composable
private fun TrendPill(state: AnalyticsState) {
  val previous = state.previousMonthOutflow
  val percent =
      (abs(state.monthlyOutflow - previous).toDouble() * PERCENT_WHOLE / previous).roundToInt()
  val isSaved = state.monthlyOutflow <= previous
  Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
      modifier = Modifier.testTag("trend-pill"),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Icon(
          imageVector =
              if (isSaved) Icons.AutoMirrored.Filled.TrendingDown
              else Icons.AutoMirrored.Filled.TrendingUp,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
      )
      Text(
          text =
              stringResource(
                  if (LocalAppLanguage.current == AppLanguage.PERSIAN)
                      Res.string.analytics_trend_vs_fa
                  else Res.string.analytics_trend_vs,
                  percent,
                  analyticsMonthName(
                      adjacentMonth(state.selectedMonth, -1),
                      state.calendarType,
                  ),
                  moneyLabel(previous),
              ),
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
      )
    }
  }
}

@Composable
private fun analyticsStaticText(
    english: StringResource,
    persian: StringResource,
): String =
    stringResource(if (LocalAppLanguage.current == AppLanguage.PERSIAN) persian else english)

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
private fun CategorySpendingCard(
    state: AnalyticsState,
    modifier: Modifier = Modifier,
) {
  Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = modifier.fillMaxWidth().testTag("category-spending-card"),
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(
          text =
              analyticsStaticText(
                  Res.string.analytics_category_spending_title,
                  Res.string.analytics_category_spending_title_fa,
              ),
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Box(
          modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
          contentAlignment = Alignment.Center,
      ) {
        SpendingDonutChart(state = state)
      }
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        state.categorySpending.forEachIndexed { index, item ->
          CategoryAllocationRow(item = item, isTopSpent = index == 0)
        }
      }
    }
  }
}

@Composable
private fun SpendingDonutChart(
    state: AnalyticsState,
    modifier: Modifier = Modifier,
) {
  val top = state.categorySpending.firstOrNull() ?: return
  val trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
  val strokeWidthPx = with(LocalDensity.current) { 16.dp.toPx() }
  Box(
      modifier = modifier.size(192.dp).testTag("spending-donut"),
      contentAlignment = Alignment.Center,
  ) {
    DonutArcsCanvas(
        state = state,
        trackColor = trackColor,
        strokeWidthPx = strokeWidthPx,
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      Text(
          text =
              analyticsStaticText(
                  Res.string.analytics_top_spent,
                  Res.string.analytics_top_spent_fa,
              ),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.5.sp,
      )
      Text(
          text = "${top.percentage}%",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = top.categoryName ?: stringResource(Res.string.uncategorized_title),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
private fun DonutArcsCanvas(
    state: AnalyticsState,
    trackColor: Color,
    strokeWidthPx: Float,
) {
  val fallbackColor = MaterialTheme.colorScheme.surfaceDim
  val spending = state.categorySpending
  Canvas(modifier = Modifier.fillMaxSize()) {
    val canvasSize = size.minDimension
    val radius = (canvasSize - strokeWidthPx) / 2f
    val topLeft = Offset((size.width - 2 * radius) / 2f, (size.height - 2 * radius) / 2f)
    val arcSize = Size(radius * 2f, radius * 2f)
    drawArc(
        color = trackColor,
        startAngle = 0f,
        sweepAngle = DONUT_FULL_CIRCLE,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
    )
    var currentAngle = DONUT_START_ANGLE
    val totalPercentage = spending.sumOf { it.percentage }.toFloat().coerceAtLeast(1f)
    spending.forEach { item ->
      val sweepAngle = (item.percentage / totalPercentage) * DONUT_FULL_CIRCLE
      if (sweepAngle > DONUT_GAP_ANGLE) {
        drawArc(
            color = item.categoryColor?.let { Color(it) } ?: fallbackColor,
            startAngle = currentAngle + (DONUT_GAP_ANGLE / 2f),
            sweepAngle = sweepAngle - DONUT_GAP_ANGLE,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
        )
      }
      currentAngle += sweepAngle
    }
  }
}

@Composable
private fun CategoryAllocationRow(
    item: AnalyticsCategorySpendingUiModel,
    isTopSpent: Boolean,
    modifier: Modifier = Modifier,
) {
  val dotColor = item.categoryColor?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceDim
  Surface(
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      modifier =
          modifier.fillMaxWidth().testTag("category-row-${item.categoryId ?: "uncategorized"}"),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
          modifier = Modifier.size(14.dp),
          shape = CircleShape,
          color = dotColor,
          content = {},
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
          text = item.categoryName ?: stringResource(Res.string.uncategorized_title),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Surface(
          shape = CircleShape,
          color =
              if (isTopSpent) MaterialTheme.colorScheme.secondaryContainer
              else MaterialTheme.colorScheme.surfaceContainerHigh,
      ) {
        Text(
            text = "${item.percentage}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color =
                if (isTopSpent) MaterialTheme.colorScheme.onSecondaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Text(
          text = moneyLabel(item.amount),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

@Composable
private fun ExpensesLedgerHeader(
    totalItemsCount: Int,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
          text =
              analyticsStaticText(
                  Res.string.analytics_expenses_title,
                  Res.string.analytics_expenses_title_fa,
              ),
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondaryContainer,
      ) {
        Text(
            text =
                stringResource(
                    if (LocalAppLanguage.current == AppLanguage.PERSIAN)
                        Res.string.analytics_items_count_fa
                    else Res.string.analytics_items_count,
                    totalItemsCount,
                ),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier =
                Modifier.padding(horizontal = 10.dp, vertical = 2.dp).testTag("expenses-count"),
        )
      }
    }
  }
}

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
  Row(
      modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        text = headerTitle,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.testTag("date-group-header"),
    )
    Text(
        text = moneyLabel(totalAmount),
        style = MaterialTheme.typography.labelSmall,
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
      color = Color.Transparent,
      modifier = modifier.fillMaxWidth().testTag("expense-item-${expense.id}"),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      ExpenseCategoryTile(expense = expense)
      Spacer(modifier = Modifier.width(12.dp))
      val uncategorizedTitle = stringResource(Res.string.uncategorized_title)
      val titleText =
          expense.categoryName
              ?: expense.description.takeIf { it.isNotBlank() }
              ?: uncategorizedTitle
      val subtitleText =
          when {
            expense.categoryName != null && expense.description.isNotBlank() -> expense.description
            expense.formattedTime.isNotEmpty() -> expense.formattedTime
            else -> null
          }
      Column(modifier = Modifier.weight(1f)) {
        Text(
            text = titleText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.testTag("expense-description"),
        )
        if (subtitleText != null) {
          Text(
              text = subtitleText,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
          )
        }
      }
      Spacer(modifier = Modifier.width(12.dp))
      Text(
          text = "-${moneyLabel(expense.amount)}",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.testTag("expense-amount"),
      )
    }
  }
}

@Composable
private fun ExpenseCategoryTile(
    expense: AnalyticsExpenseUiModel,
    modifier: Modifier = Modifier,
) {
  val categoryIcon =
      expense.categoryIcon?.let { resolveCategoryIcon(it) } ?: Icons.AutoMirrored.Filled.ReceiptLong
  val tileBackground =
      if (expense.categoryColor != null) {
        Color(expense.categoryColor).copy(alpha = 0.15f)
      } else {
        MaterialTheme.colorScheme.secondaryContainer
      }
  val tileTint =
      if (expense.categoryColor != null) {
        Color(expense.categoryColor)
      } else {
        MaterialTheme.colorScheme.onSecondaryContainer
      }

  Surface(
      modifier = modifier.size(44.dp),
      shape = RoundedCornerShape(14.dp),
      color = tileBackground,
      contentColor = tileTint,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
          imageVector = categoryIcon,
          contentDescription = expense.categoryName,
          modifier = Modifier.size(24.dp),
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
                      previousMonthOutflow = 0L,
                      categorySpending = emptyList(),
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
