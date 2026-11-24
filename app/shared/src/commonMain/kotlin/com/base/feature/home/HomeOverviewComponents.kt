package com.base.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.calendar.getMonthName
import com.base.feature.category.add.resolveCategoryIcon
import com.base.ui.money.currencySymbol
import com.base.ui.money.moneyAmount
import com.base.ui.money.moneyLabel
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.home_active_pockets
import pockety.app.shared.generated.resources.home_all_months
import pockety.app.shared.generated.resources.home_last_month_format
import pockety.app.shared.generated.resources.home_manage_all
import pockety.app.shared.generated.resources.home_more_format
import pockety.app.shared.generated.resources.home_other_category
import pockety.app.shared.generated.resources.home_saved_format
import pockety.app.shared.generated.resources.home_vs_month_format
import pockety.app.shared.generated.resources.monthly_outflow_label
import pockety.app.shared.generated.resources.uncategorized_title

private const val PERCENT_WHOLE: Float = 100f

private const val MIN_SEGMENT_WEIGHT: Float = 0.01f

/** Display color of a breakdown row: the category's own color, or a neutral tone. */
@Composable
@ReadOnlyComposable
private fun HomeCategoryOutflow.displayColor(): Color =
    color?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceDim

@Composable
private fun HomeCategoryOutflow.displayName(): String =
    when (kind) {
      HomeCategoryKind.CATEGORY -> name.orEmpty()
      HomeCategoryKind.UNCATEGORIZED -> stringResource(Res.string.uncategorized_title)
      HomeCategoryKind.OTHER -> stringResource(Res.string.home_other_category)
    }

/** Segmented progress bar showing proportional distribution of categories. */
@Composable
private fun HomeSegmentedProgressBar(
    state: HomeState,
    modifier: Modifier = Modifier,
) {
  val segments = state.topCategories
  Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surfaceContainer,
      modifier = modifier.fillMaxWidth().height(12.dp),
  ) {
    if (segments.isNotEmpty()) {
      Row(
          modifier = Modifier.fillMaxSize().padding(2.dp),
          horizontalArrangement = Arrangement.spacedBy(2.dp),
      ) {
        segments.forEachIndexed { index, segment ->
          val shape: Shape =
              when {
                segments.size == 1 -> CircleShape
                index == 0 ->
                    RoundedCornerShape(
                        topStart = 6.dp,
                        bottomStart = 6.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp,
                    )

                index == segments.lastIndex ->
                    RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = 6.dp,
                        bottomEnd = 6.dp,
                    )

                else -> RectangleShape
              }

          Surface(
              modifier =
                  Modifier.weight(segment.share.coerceAtLeast(MIN_SEGMENT_WEIGHT)).fillMaxHeight(),
              shape = shape,
              color = segment.displayColor(),
          ) {}
        }
      }
    }
  }
}

/** Single category row displaying legend dot, category name, percentage, and amount. */
@Composable
private fun HomeCategoryBreakdownRow(
    category: HomeCategoryOutflow,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
        modifier = Modifier.weight(1f, fill = false),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Surface(
          shape = CircleShape,
          color = category.displayColor(),
          modifier = Modifier.size(10.dp),
      ) {}

      Text(
          text = category.displayName(),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )

      Text(
          text = "${(category.share * PERCENT_WHOLE).toInt()}%",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary,
      )
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
      Text(
          text = moneyAmount(category.amount),
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = currencySymbol(),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.padding(bottom = 1.dp),
      )
    }
  }
}

@Composable
private fun HomeOutflowAmount(state: HomeState) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
        text = stringResource(Res.string.monthly_outflow_label).uppercase(),
        style =
            MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        color = MaterialTheme.colorScheme.secondary,
    )
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
          text = moneyAmount(state.monthlyOutflow),
          style =
              MaterialTheme.typography.displayLarge.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 36.sp,
                  lineHeight = 44.sp,
              ),
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = currencySymbol(),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.padding(bottom = 6.dp),
      )
    }
  }
}

/** Hero card showcasing total monthly outflow, segmented progress bar, and category breakdown. */
@Composable
fun MonthlyOutflowHeroCard(
    state: HomeState,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shadowElevation = 1.dp,
      modifier = modifier.fillMaxWidth().testTag("home-monthly-outflow-card"),
  ) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            onClick = onDateClick,
            modifier = Modifier.testTag("home-date-pill"),
        ) {
          Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text =
                    "${getMonthName(state.currentMonth, state.calendarType)} ${state.currentYear}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
      }

      HomeOutflowAmount(state = state)

      HomeSegmentedProgressBar(state = state)

      if (state.topCategories.isNotEmpty()) {
        Column(
            modifier = Modifier.padding(top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          state.topCategories.forEach { category -> HomeCategoryBreakdownRow(category = category) }
        }
      }
    }
  }
}

@Composable
private fun HomeMonthOverMonthBadge(state: HomeState, percent: Int) {
  Surface(
      shape = CircleShape,
      color =
          if (state.isMonthOverMonthSaved) {
            MaterialTheme.colorScheme.secondaryContainer
          } else {
            MaterialTheme.colorScheme.errorContainer
          },
      contentColor =
          if (state.isMonthOverMonthSaved) {
            MaterialTheme.colorScheme.onSecondaryContainer
          } else {
            MaterialTheme.colorScheme.onErrorContainer
          },
      modifier = Modifier.testTag("home-mom-badge"),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Icon(
          imageVector =
              if (state.isMonthOverMonthSaved) {
                Icons.AutoMirrored.Filled.TrendingDown
              } else {
                Icons.AutoMirrored.Filled.TrendingUp
              },
          contentDescription = null,
          modifier = Modifier.size(14.dp),
      )
      Text(
          text =
              stringResource(
                  if (state.isMonthOverMonthSaved) {
                    Res.string.home_saved_format
                  } else {
                    Res.string.home_more_format
                  },
                  percent,
              ),
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
      )
    }
  }
}

@Composable
private fun HomeAllMonthsButton(onClick: () -> Unit) {
  TextButton(
      onClick = onClick,
      modifier = Modifier.testTag("home-all-months"),
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Text(
          text = stringResource(Res.string.home_all_months),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.primary,
      )
      Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
          tint = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

/** Comparison card displaying month-over-month savings or increase. */
@Composable
fun MonthOverMonthCard(
    state: HomeState,
    onAllMonthsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
      modifier = modifier.fillMaxWidth().testTag("home-mom-card"),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(
          modifier = Modifier.weight(1f, fill = false),
          verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
              text =
                  stringResource(
                      Res.string.home_vs_month_format,
                      getMonthName(state.previousMonth, state.calendarType),
                  ),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.secondary,
          )
          state.monthOverMonthPercent?.let { percent ->
            HomeMonthOverMonthBadge(state = state, percent = percent)
          }
        }

        Text(
            text =
                stringResource(
                    Res.string.home_last_month_format,
                    moneyLabel(state.previousMonthOutflow),
                ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
      }

      HomeAllMonthsButton(onClick = onAllMonthsClick)
    }
  }
}

/** Individual pocket card in the active pockets grid. */
@Composable
fun HomePocketCard(
    pocket: HomePocketUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      onClick = onClick,
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shadowElevation = 1.dp,
      modifier = modifier.testTag("home-pocket-${pocket.id}"),
  ) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceContainer,
          contentColor = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(36.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = resolveCategoryIcon(pocket.icon),
              contentDescription = pocket.name,
              modifier = Modifier.size(18.dp),
          )
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = pocket.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
          Text(
              text = moneyAmount(pocket.balance),
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
          )
          Text(
              text = currencySymbol(),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.padding(bottom = 2.dp),
          )
        }
      }
    }
  }
}

/** Active pockets section: title, "Manage All" action, and a responsive grid of pocket cards. */
@Composable
fun ActivePocketsSection(
    state: HomeState,
    onManageAllClick: () -> Unit,
    onPocketClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.fillMaxWidth().testTag("home-pockets-section"),
      verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
          text = stringResource(Res.string.home_active_pockets),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onSurface,
      )
      TextButton(
          onClick = onManageAllClick,
          modifier = Modifier.testTag("home-manage-pockets"),
      ) {
        Text(
            text = stringResource(Res.string.home_manage_all),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
        )
      }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val columns = (maxWidth / 176.dp).toInt().coerceIn(2, 6)
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        state.pockets.chunked(columns).forEach { rowPockets ->
          Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            rowPockets.forEach { pocket ->
              HomePocketCard(
                  pocket = pocket,
                  onClick = { onPocketClick(pocket.id) },
                  modifier = Modifier.weight(1f),
              )
            }
            repeat(columns - rowPockets.size) { Spacer(modifier = Modifier.weight(1f)) }
          }
        }
      }
    }
  }
}
