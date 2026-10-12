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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.design.ui.ComponentPreviews

data class CategorySpending(
    val id: String,
    val name: String,
    val percentage: Int,
    val amountFormatted: String,
    val currency: String = "T",
    val color: Color,
    val isTopSpent: Boolean = false,
)

data class ExpenseItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val amountFormatted: String,
    val currency: String = "T",
    val icon: ImageVector,
    val iconBackgroundColor: Color,
    val iconColor: Color,
)

data class ExpenseDateGroup(
    val dateLabel: String,
    val totalAmountFormatted: String,
    val items: List<ExpenseItem>,
)

val defaultCategories =
    listOf(
        CategorySpending(
            id = "food",
            name = "Food & Groceries",
            percentage = 42,
            amountFormatted = "6,200,000",
            currency = "T",
            color = Color(0xFF4A7C59),
            isTopSpent = true,
        ),
        CategorySpending(
            id = "transport",
            name = "Transportation",
            percentage = 21,
            amountFormatted = "3,100,000",
            currency = "T",
            color = Color(0xFF689F78),
        ),
        CategorySpending(
            id = "cafe",
            name = "Cafe & Dining",
            percentage = 16,
            amountFormatted = "2,450,000",
            currency = "T",
            color = Color(0xFF88B896),
        ),
        CategorySpending(
            id = "bills",
            name = "Bills & Utilities",
            percentage = 12,
            amountFormatted = "1,800,000",
            currency = "T",
            color = Color(0xFFA8D5B5),
        ),
        CategorySpending(
            id = "entertainment",
            name = "Entertainment & Other",
            percentage = 9,
            amountFormatted = "1,300,000",
            currency = "T",
            color = Color(0xFFD5EBD9),
        ),
    )

val defaultExpenseGroups =
    listOf(
        ExpenseDateGroup(
            dateLabel = "Today, 24 Oct",
            totalAmountFormatted = "1,505,000 T",
            items =
                listOf(
                    ExpenseItem(
                        id = "1",
                        title = "Groceries & Household",
                        subtitle = "Hyperstar weekly groceries",
                        amountFormatted = "-1,420,000",
                        currency = "T",
                        icon = Icons.Default.ShoppingCart,
                        iconBackgroundColor = Color(0x264A7C59),
                        iconColor = Color(0xFF316342),
                    ),
                    ExpenseItem(
                        id = "2",
                        title = "Daily Cafe & Coffee",
                        subtitle = "Sam Cafe Vanak meeting",
                        amountFormatted = "-85,000",
                        currency = "T",
                        icon = Icons.Default.LocalCafe,
                        iconBackgroundColor = Color(0xFFCCEBC7),
                        iconColor = Color(0xFF334D33),
                    ),
                ),
        ),
        ExpenseDateGroup(
            dateLabel = "Yesterday, 23 Oct",
            totalAmountFormatted = "715,000 T",
            items =
                listOf(
                    ExpenseItem(
                        id = "3",
                        title = "Transportation",
                        subtitle = "Snapp ride to office",
                        amountFormatted = "-95,000",
                        currency = "T",
                        icon = Icons.Default.DirectionsCar,
                        iconBackgroundColor = Color(0xFFC9E6D7),
                        iconColor = Color(0xFF4E685C),
                    ),
                    ExpenseItem(
                        id = "4",
                        title = "Entertainment & Shopping",
                        subtitle = "Digikala headphone order",
                        amountFormatted = "-620,000",
                        currency = "T",
                        icon = Icons.Default.Devices,
                        iconBackgroundColor = Color(0xFFDFE4DE),
                        iconColor = Color(0xFF181D19),
                    ),
                ),
        ),
        ExpenseDateGroup(
            dateLabel = "21 Oct 2026",
            totalAmountFormatted = "405,000 T",
            items =
                listOf(
                    ExpenseItem(
                        id = "5",
                        title = "Bills & Utilities",
                        subtitle = "Tehran electricity bill payment",
                        amountFormatted = "-340,000",
                        currency = "T",
                        icon = Icons.Default.ElectricBolt,
                        iconBackgroundColor = Color(0xFFB9EFC5),
                        iconColor = Color(0xFF1E5031),
                    ),
                    ExpenseItem(
                        id = "6",
                        title = "Groceries & Household",
                        subtitle = "Reza Bakery fresh bread",
                        amountFormatted = "-65,000",
                        currency = "T",
                        icon = Icons.Default.BakeryDining,
                        iconBackgroundColor = Color(0x264A7C59),
                        iconColor = Color(0xFF316342),
                    ),
                ),
        ),
        ExpenseDateGroup(
            dateLabel = "19 Oct 2026",
            totalAmountFormatted = "580,000 T",
            items =
                listOf(
                    ExpenseItem(
                        id = "7",
                        title = "Entertainment & Gaming",
                        subtitle = "Steam Game Store purchase",
                        amountFormatted = "-580,000",
                        currency = "T",
                        icon = Icons.Default.SportsEsports,
                        iconBackgroundColor = Color(0xFFCCEBC7),
                        iconColor = Color(0xFF334D33),
                    ),
                ),
        ),
    )

@Composable
fun MonthSwitcherCard(
    modifier: Modifier = Modifier,
    currentMonth: String = "October 2026",
    previousMonthLabel: String = "Sep",
    nextMonthLabel: String = "Nov",
    isNextEnabled: Boolean = false,
    subtitle: String = "Monthly Cash Flow",
    totalSpendAmount: String = "14,850,000",
    currency: String = "T",
    trendText: String = "12% vs Sep (16,920,000 T)",
    onPreviousMonthClick: () -> Unit = {},
    onNextMonthClick: () -> Unit = {},
    onMonthPickerClick: () -> Unit = {},
) {
  Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = modifier.fillMaxWidth(),
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
            onClick = onPreviousMonthClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
          Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous Month",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = previousMonthLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        Surface(
            onClick = onMonthPickerClick,
            shape = RoundedCornerShape(8.dp),
            color = Color.Transparent,
        ) {
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Text(
                  text = currentMonth,
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface,
              )
              Icon(
                  imageVector = Icons.Default.ExpandMore,
                  contentDescription = "Change Month",
                  modifier = Modifier.size(18.dp),
                  tint = MaterialTheme.colorScheme.secondary,
              )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary,
            )
          }
        }

        Surface(
            onClick = onNextMonthClick,
            enabled = isNextEnabled,
            shape = CircleShape,
            color =
                if (isNextEnabled) MaterialTheme.colorScheme.surfaceContainerHigh
                else MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f),
        ) {
          Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
                text = nextMonthLabel,
                style = MaterialTheme.typography.labelSmall,
                color =
                    if (isNextEnabled) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.outlineVariant,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next Month",
                modifier = Modifier.size(18.dp),
                tint =
                    if (isNextEnabled) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.outlineVariant,
            )
          }
        }
      }

      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.padding(vertical = 4.dp),
      ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Text(
              text = totalSpendAmount,
              style = MaterialTheme.typography.displayLarge.copy(fontSize = 36.sp),
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
              text = currency,
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(bottom = 4.dp),
          )
        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) {
          Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = trendText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
          }
        }
      }
    }
  }
}

@Composable
fun SpendingDonutChart(
    categories: List<CategorySpending>,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    strokeWidth: Dp = 16.dp,
    topSpentPercentage: String = "42%",
    topSpentCategoryName: String = "Food",
) {
  val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }

  Box(
      modifier = modifier.size(192.dp),
      contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val canvasSize = size.minDimension
      val radius = (canvasSize - strokeWidthPx) / 2f
      val topLeft = Offset((size.width - 2 * radius) / 2f, (size.height - 2 * radius) / 2f)
      val arcSize = Size(radius * 2f, radius * 2f)

      drawArc(
          color = trackColor,
          startAngle = 0f,
          sweepAngle = 360f,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
      )

      var currentAngle = -90f
      val totalPercentage = categories.sumOf { it.percentage }.toFloat().coerceAtLeast(1f)
      val gapAngle = 4f

      categories.forEach { cat ->
        val sweepAngle = (cat.percentage / totalPercentage) * 360f
        if (sweepAngle > gapAngle) {
          drawArc(
              color = cat.color,
              startAngle = currentAngle + (gapAngle / 2f),
              sweepAngle = sweepAngle - gapAngle,
              useCenter = false,
              topLeft = topLeft,
              size = arcSize,
              style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
          )
        }
        currentAngle += sweepAngle
      }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      Text(
          text = "TOP SPENT",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.5.sp,
      )
      Text(
          text = topSpentPercentage,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = topSpentCategoryName,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Medium,
      )
    }
  }
}

@Composable
fun CategoryAllocationRow(
    category: CategorySpending,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
  Surface(
      onClick = onClick,
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      modifier = modifier.fillMaxWidth(),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
          modifier = Modifier.size(14.dp).clip(CircleShape).background(category.color),
      )
      Spacer(modifier = Modifier.width(10.dp))
      Text(
          text = category.name,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )
      Spacer(modifier = Modifier.width(8.dp))
      Surface(
          shape = CircleShape,
          color =
              if (category.isTopSpent) MaterialTheme.colorScheme.secondaryContainer
              else MaterialTheme.colorScheme.surfaceContainerHigh,
      ) {
        Text(
            text = "${category.percentage}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color =
                if (category.isTopSpent) MaterialTheme.colorScheme.onSecondaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
      }
      Spacer(modifier = Modifier.weight(1f))
      Row(
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
            text = category.amountFormatted,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = category.currency,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
        )
      }
    }
  }
}

@Composable
fun SpendingDonutChartSection(
    modifier: Modifier = Modifier,
    categories: List<CategorySpending> = defaultCategories,
    topSpentPercentage: String = "42%",
    topSpentCategoryName: String = "Food",
    title: String = "Category Spending",
    onCategoryClick: (CategorySpending) -> Unit = {},
) {
  Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = modifier.fillMaxWidth(),
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Box(
          modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
          contentAlignment = Alignment.Center,
      ) {
        SpendingDonutChart(
            categories = categories,
            topSpentPercentage = topSpentPercentage,
            topSpentCategoryName = topSpentCategoryName,
        )
      }
      Column(
          verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        categories.forEach { category ->
          CategoryAllocationRow(
              category = category,
              onClick = { onCategoryClick(category) },
          )
        }
      }
    }
  }
}

@Composable
fun ExpensesLedgerHeader(
    modifier: Modifier = Modifier,
    totalItemsCount: Int = 48,
    title: String = "Expenses",
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
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondaryContainer,
      ) {
        Text(
            text = "$totalItemsCount items",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
        )
      }
    }
  }
}

@Composable
fun DateGroupHeader(
    dateLabel: String,
    totalAmountFormatted: String,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        text = dateLabel.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
        color = MaterialTheme.colorScheme.secondary,
    )
    Text(
        text = totalAmountFormatted,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun ExpenseLedgerItem(
    item: ExpenseItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
  Surface(
      onClick = onClick,
      shape = RoundedCornerShape(16.dp),
      color = Color.Transparent,
      modifier = modifier.fillMaxWidth(),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
          shape = RoundedCornerShape(14.dp),
          color = item.iconBackgroundColor,
          contentColor = item.iconColor,
          modifier = Modifier.size(44.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = item.icon,
              contentDescription = null,
              modifier = Modifier.size(24.dp),
          )
        }
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(
          modifier = Modifier.weight(1f),
      ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = item.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Row(
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
            text = item.amountFormatted,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = item.currency,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
        )
      }
    }
  }
}

@ComponentPreviews
@Composable
private fun MonthSwitcherCardPreview() {
  MonthSwitcherCard()
}

@ComponentPreviews
@Composable
private fun MonthSwitcherCardNextEnabledPreview() {
  MonthSwitcherCard(
      currentMonth = "September 2026",
      previousMonthLabel = "Aug",
      nextMonthLabel = "Oct",
      isNextEnabled = true,
  )
}

@ComponentPreviews
@Composable
private fun SpendingDonutChartSectionPreview() {
  SpendingDonutChartSection()
}

@ComponentPreviews
@Composable
private fun CategoryAllocationRowPreview() {
  CategoryAllocationRow(
      category = defaultCategories.first(),
  )
}

@ComponentPreviews
@Composable
private fun DateGroupHeaderPreview() {
  DateGroupHeader(
      dateLabel = "Today, 24 Oct",
      totalAmountFormatted = "1,505,000 T",
  )
}

@ComponentPreviews
@Composable
private fun ExpenseLedgerItemPreview() {
  ExpenseLedgerItem(
      item = defaultExpenseGroups.first().items.first(),
  )
}
