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

package com.base.design.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.design.ui.ComponentPreviews
import com.base.design.ui.theme.colorSchemeExtended

data class OutflowCategory(
    val name: String,
    val percentage: Float,
    val amountFormatted: String,
    val color: Color,
)

data class ActivePocket(
    val name: String,
    val amount: String,
    val icon: ImageVector,
)

/** Banner notifying the user about pending/undecided expenses requiring review. */
@Composable
fun UndecidedExpensesBanner(
    count: Int,
    amountFormatted: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
  Surface(
      onClick = onClick,
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.secondaryContainer,
      shadowElevation = 1.dp,
      modifier = modifier.fillMaxWidth(),
  ) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
          modifier = Modifier.weight(1f, fill = false),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(40.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
          }
        }

        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Text(
                text = "Undecided expenses",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
              Text(
                  text = "$count",
                  style =
                      MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              )
            }
          }
          Text(
              text = "$amountFormatted pending review",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.secondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
          )
        }
      }

      Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.8f),
          contentColor = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(32.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "View undecided expenses",
              modifier = Modifier.size(18.dp),
          )
        }
      }
    }
  }
}

/** Segmented progress bar showing proportional distribution of categories. */
@Composable
fun SegmentedProgressBar(
    segments: List<OutflowCategory>,
    modifier: Modifier = Modifier,
) {
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
              modifier = Modifier.weight(segment.percentage.coerceAtLeast(0.01f)).fillMaxHeight(),
              shape = shape,
              color = segment.color,
          ) {}
        }
      }
    }
  }
}

/** Single category row displaying legend dot, category name, percentage, and amount. */
@Composable
fun CategoryBreakdownRow(
    category: OutflowCategory,
    modifier: Modifier = Modifier,
    currencySymbol: String = "T",
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
          color = category.color,
          modifier = Modifier.size(10.dp),
      ) {}

      Text(
          text = category.name,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )

      Text(
          text = "${(category.percentage * 100).toInt()}%",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary,
      )
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
      Text(
          text = category.amountFormatted,
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = currencySymbol,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.padding(bottom = 1.dp),
      )
    }
  }
}

/** Hero card showcasing total monthly outflow, segmented progress bar, and category breakdown. */
@Composable
fun MonthlyOutflowHeroCard(
    monthLabel: String,
    totalAmount: String,
    categories: List<OutflowCategory>,
    modifier: Modifier = Modifier,
    currencySymbol: String = "T",
    onDateClick: () -> Unit = {},
) {
  Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shadowElevation = 1.dp,
      modifier = modifier.fillMaxWidth(),
  ) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      // Header with Date Pill
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            onClick = onDateClick,
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
                text = monthLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
      }

      // Amount Hero Group
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "MONTHLY OUTFLOW",
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
              text = totalAmount,
              style =
                  MaterialTheme.typography.displayLarge.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 36.sp,
                      lineHeight = 44.sp,
                  ),
              color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
              text = currencySymbol,
              style =
                  MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Medium,
                  ),
              color = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.padding(bottom = 6.dp),
          )
        }
      }

      // Segmented Linear Progress Bar
      SegmentedProgressBar(segments = categories)

      // Category Breakdown List
      Column(
          modifier = Modifier.padding(top = 4.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        categories.forEach { category ->
          CategoryBreakdownRow(
              category = category,
              currencySymbol = currencySymbol,
          )
        }
      }
    }
  }
}

/** Comparison card displaying month-over-month savings or increase. */
@Composable
fun MonthOverMonthCard(
    comparisonLabel: String,
    percentageChange: String,
    isSaved: Boolean,
    previousMonthAmount: String,
    modifier: Modifier = Modifier,
    currencySymbol: String = "T",
    onAllMonthsClick: () -> Unit = {},
) {
  Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
      modifier = modifier.fillMaxWidth(),
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
              text = comparisonLabel,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.secondary,
          )
          Surface(
              shape = CircleShape,
              color =
                  if (isSaved) {
                    MaterialTheme.colorScheme.secondaryContainer
                  } else {
                    MaterialTheme.colorScheme.errorContainer
                  },
              contentColor =
                  if (isSaved) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                  } else {
                    MaterialTheme.colorScheme.onErrorContainer
                  },
          ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                  imageVector =
                      if (isSaved) {
                        Icons.Default.TrendingDown
                      } else {
                        Icons.Default.TrendingUp
                      },
                  contentDescription = null,
                  modifier = Modifier.size(14.dp),
              )
              Text(
                  text = percentageChange,
                  style =
                      MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              )
            }
          }
        }

        Text(
            text = "$previousMonthAmount $currencySymbol last month",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
      }

      TextButton(onClick = onAllMonthsClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
              text = "All months",
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
  }
}

/** Individual Pocket card in the active pockets grid. */
@Composable
fun ActivePocketCard(
    pocket: ActivePocket,
    modifier: Modifier = Modifier,
    currencySymbol: String = "T",
    onClick: () -> Unit = {},
) {
  Surface(
      onClick = onClick,
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shadowElevation = 1.dp,
      modifier = modifier,
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
              imageVector = pocket.icon,
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
              text = pocket.amount,
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
          )
          Text(
              text = currencySymbol,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.padding(bottom = 2.dp),
          )
        }
      }
    }
  }
}

/** Active Pockets section with title, "Manage All" button, and horizontal grid of pockets. */
@Composable
fun ActivePocketsSection(
    pockets: List<ActivePocket>,
    modifier: Modifier = Modifier,
    currencySymbol: String = "T",
    onManageAllClick: () -> Unit = {},
    onPocketClick: (ActivePocket) -> Unit = {},
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
          text = "Active Pockets",
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onSurface,
      )
      TextButton(onClick = onManageAllClick) {
        Text(
            text = "Manage All",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
        )
      }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      pockets.forEach { pocket ->
        ActivePocketCard(
            pocket = pocket,
            currencySymbol = currencySymbol,
            onClick = { onPocketClick(pocket) },
            modifier = Modifier.weight(1f),
        )
      }
    }
  }
}

// ==================== Component Previews ====================

@ComponentPreviews
@Composable
private fun UndecidedExpensesBannerPreview() {
  UndecidedExpensesBanner(
      count = 8,
      amountFormatted = "2,340,000T",
  )
}

@ComponentPreviews
@Composable
private fun SegmentedProgressBarPreview() {
  val primary = MaterialTheme.colorScheme.primary
  val primaryContainer = MaterialTheme.colorScheme.primaryContainer
  val secondary = MaterialTheme.colorScheme.secondary
  val secondaryFixedDim = MaterialTheme.colorSchemeExtended.secondaryFixedDim
  val surfaceDim = MaterialTheme.colorScheme.surfaceDim

  SegmentedProgressBar(
      segments =
          listOf(
              OutflowCategory("Food & Groceries", 0.42f, "6,200,000", primary),
              OutflowCategory("Transportation", 0.21f, "3,100,000", primaryContainer),
              OutflowCategory("Cafe & Dining", 0.16f, "2,450,000", secondary),
              OutflowCategory("Bills & Utilities", 0.12f, "1,800,000", secondaryFixedDim),
              OutflowCategory("Other", 0.09f, "1,300,000", surfaceDim),
          ),
  )
}

@ComponentPreviews
@Composable
private fun MonthlyOutflowHeroCardPreview() {
  val primary = MaterialTheme.colorScheme.primary
  val primaryContainer = MaterialTheme.colorScheme.primaryContainer
  val secondary = MaterialTheme.colorScheme.secondary
  val secondaryFixedDim = MaterialTheme.colorSchemeExtended.secondaryFixedDim
  val surfaceDim = MaterialTheme.colorScheme.surfaceDim

  MonthlyOutflowHeroCard(
      monthLabel = "Oct 2026",
      totalAmount = "14,850,000",
      categories =
          listOf(
              OutflowCategory("Food & Groceries", 0.42f, "6,200,000", primary),
              OutflowCategory("Transportation", 0.21f, "3,100,000", primaryContainer),
              OutflowCategory("Cafe & Dining", 0.16f, "2,450,000", secondary),
              OutflowCategory("Bills & Utilities", 0.12f, "1,800,000", secondaryFixedDim),
              OutflowCategory("Other", 0.09f, "1,300,000", surfaceDim),
          ),
  )
}

@ComponentPreviews
@Composable
private fun MonthOverMonthCardSavedPreview() {
  MonthOverMonthCard(
      comparisonLabel = "vs September:",
      percentageChange = "12% saved",
      isSaved = true,
      previousMonthAmount = "16,900,000",
  )
}

@ComponentPreviews
@Composable
private fun MonthOverMonthCardIncreasedPreview() {
  MonthOverMonthCard(
      comparisonLabel = "vs September:",
      percentageChange = "5% over",
      isSaved = false,
      previousMonthAmount = "14,100,000",
  )
}

@ComponentPreviews
@Composable
private fun ActivePocketCardPreview() {
  ActivePocketCard(
      pocket = ActivePocket("Cafe", "85,000", Icons.Default.LocalCafe),
  )
}

@ComponentPreviews
@Composable
private fun ActivePocketsSectionPreview() {
  ActivePocketsSection(
      pockets =
          listOf(
              ActivePocket("Cafe", "85,000", Icons.Default.LocalCafe),
              ActivePocket("Groceries", "450,000", Icons.Default.ShoppingCart),
          ),
  )
}
