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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.LocalConvenienceStore
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.design.ui.ComponentPreviews
import com.base.design.ui.theme.colorSchemeExtended

data class CategoryExpense(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val icon: ImageVector,
)

enum class CategoryExpenseFilter(val label: String) {
  LATEST("Latest"),
  AMOUNT("Amount"),
}

object CategoryExpenseDefaults {
  val defaultExpenses =
      listOf(
          CategoryExpense(
              id = "1",
              title = "HyperStar Supermarket",
              subtitle = "Today, 04:15 PM • Weekly groceries",
              amount = "-320,000 T",
              icon = Icons.Default.ShoppingCart,
          ),
          CategoryExpense(
              id = "2",
              title = "Cafe Vintage & Bakery",
              subtitle = "Yesterday, 11:30 AM • Snacks & coffee",
              amount = "-145,000 T",
              icon = Icons.Default.Coffee,
          ),
          CategoryExpense(
              id = "3",
              title = "Organic Fresh Market",
              subtitle = "Oct 24, 02:20 PM • Vegetables & fruits",
              amount = "-485,000 T",
              icon = Icons.Default.Storefront,
          ),
          CategoryExpense(
              id = "4",
              title = "City Express Mart",
              subtitle = "Oct 21, 09:10 PM • Household items",
              amount = "-210,000 T",
              icon = Icons.Default.LocalConvenienceStore,
          ),
          CategoryExpense(
              id = "5",
              title = "Artisan Bakery",
              subtitle = "Oct 19, 08:00 AM • Fresh bread",
              amount = "-75,000 T",
              icon = Icons.Default.BakeryDining,
          ),
      )
}

@Composable
fun CategoryHeroSection(
    categoryName: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Surface(
        modifier = Modifier.size(96.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 1.dp,
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
            imageVector = icon,
            contentDescription = categoryName,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(48.dp),
        )
      }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = categoryName,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
    )
  }
}

@Composable
fun CategorySpendSummaryCard(
    totalSpend: String,
    modifier: Modifier = Modifier,
    currencySymbol: String = "T",
    budgetProgress: Float = 0.725f,
    budgetUsedText: String = "72.5% of budget used",
    budgetLeftText: String = "550,000 T left",
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
  ) {
    Box {
      Box(
          modifier =
              Modifier.align(Alignment.TopEnd)
                  .offset(x = 24.dp, y = (-24).dp)
                  .size(128.dp)
                  .background(
                      brush =
                          Brush.radialGradient(
                              colors =
                                  listOf(
                                      MaterialTheme.colorSchemeExtended.primaryFixed.copy(
                                          alpha = 0.35f
                                      ),
                                      Color.Transparent,
                                  ),
                          ),
                      shape = CircleShape,
                  ),
      )
      Column(
          modifier = Modifier.fillMaxWidth().padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "TOTAL SPEND THIS MONTH",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
              text = totalSpend,
              style = MaterialTheme.typography.displayLarge,
              color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
              text = currencySymbol,
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(bottom = 6.dp),
          )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
          Box(
              modifier =
                  Modifier.fillMaxWidth(fraction = budgetProgress.coerceIn(0f, 1f))
                      .fillMaxHeight()
                      .clip(CircleShape)
                      .background(MaterialTheme.colorScheme.primary),
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
              text = budgetUsedText,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
              text = budgetLeftText,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
fun CategoryQuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      onClick = onClick,
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
      modifier = modifier.height(48.dp),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
      Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
          text = label,
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

@Composable
fun CategoryQuickActionRow(
    modifier: Modifier = Modifier,
    onRenameClick: () -> Unit = {},
    onChangeIconClick: () -> Unit = {},
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    CategoryQuickActionButton(
        icon = Icons.Default.DriveFileRenameOutline,
        label = "Rename",
        onClick = onRenameClick,
        modifier = Modifier.weight(1f),
    )
    CategoryQuickActionButton(
        icon = Icons.Default.Palette,
        label = "Change Icon",
        onClick = onChangeIconClick,
        modifier = Modifier.weight(1f),
    )
  }
}

@Composable
fun CategoryExpenseFilterChips(
    selectedFilter: CategoryExpenseFilter,
    onFilterSelected: (CategoryExpenseFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    CategoryExpenseFilter.entries.forEach { filter ->
      val isSelected = filter == selectedFilter
      Surface(
          onClick = { onFilterSelected(filter) },
          shape = CircleShape,
          color =
              if (isSelected) {
                MaterialTheme.colorScheme.surfaceContainerLow
              } else {
                Color.Transparent
              },
      ) {
        Text(
            text = filter.label,
            style = MaterialTheme.typography.labelMedium,
            color =
                if (isSelected) {
                  MaterialTheme.colorScheme.onSurface
                } else {
                  MaterialTheme.colorScheme.onSurfaceVariant
                },
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        )
      }
    }
  }
}

@Composable
fun CategoryExpenseItem(
    expense: CategoryExpense,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      onClick = onClick,
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = modifier.fillMaxWidth(),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
          modifier = Modifier.size(48.dp),
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondaryContainer,
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = expense.icon,
              contentDescription = expense.title,
              tint = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.size(22.dp),
          )
        }
      }
      Spacer(modifier = Modifier.width(16.dp))
      Column(
          modifier = Modifier.weight(1f),
      ) {
        Text(
            text = expense.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = expense.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
      }
      Spacer(modifier = Modifier.width(16.dp))
      Text(
          text = expense.amount,
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

@ComponentPreviews
@Composable
private fun CategoryHeroSectionPreview() {
  CategoryHeroSection(
      categoryName = "Groceries & Supermarket",
      icon = Icons.Default.LocalGroceryStore,
  )
}

@ComponentPreviews
@Composable
private fun CategorySpendSummaryCardPreview() {
  CategorySpendSummaryCard(
      totalSpend = "1,450,000",
  )
}

@ComponentPreviews
@Composable
private fun CategoryQuickActionRowPreview() {
  CategoryQuickActionRow()
}

@ComponentPreviews
@Composable
private fun CategoryExpenseItemPreview() {
  CategoryExpenseItem(
      expense = CategoryExpenseDefaults.defaultExpenses.first(),
      onClick = {},
  )
}
