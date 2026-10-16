package com.base.design.features.pockets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.base.design.ui.ComponentPreviews

data class PocketItem(
    val id: String,
    val name: String,
    val categoryTag: String,
    val amountFormatted: String,
    val icon: ImageVector,
    val isOverBudget: Boolean = false,
    val warningMessage: String? = null,
    val showHistory: Boolean = false,
)

@Composable
fun PocketWarningHeader(
    message: String,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.errorContainer,
      contentColor = MaterialTheme.colorScheme.onErrorContainer,
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Icon(
          imageVector = Icons.Default.Error,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(18.dp),
      )
      Text(
          text = message,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onErrorContainer,
      )
    }
  }
}

@Composable
fun PocketBudgetCard(
    pocket: PocketItem,
    modifier: Modifier = Modifier,
    onTopUpClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      tonalElevation = 1.dp,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      if (pocket.warningMessage != null) {
        PocketWarningHeader(message = pocket.warningMessage)
      }

      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top,
      ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Surface(
              shape = CircleShape,
              color =
                  if (pocket.isOverBudget) {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                  } else {
                    MaterialTheme.colorScheme.secondaryContainer
                  },
              contentColor =
                  if (pocket.isOverBudget) {
                    MaterialTheme.colorScheme.secondary
                  } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                  },
          ) {
            Text(
                text = pocket.categoryTag,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            )
          }

          Text(
              text = pocket.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface,
          )
        }

        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = pocket.icon,
                contentDescription = pocket.name,
                tint =
                    if (pocket.isOverBudget) {
                      MaterialTheme.colorScheme.error
                    } else {
                      MaterialTheme.colorScheme.primary
                    },
                modifier = Modifier.size(20.dp),
            )
          }
        }
      }

      Text(
          text = pocket.amountFormatted,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color =
              if (pocket.isOverBudget) {
                MaterialTheme.colorScheme.error
              } else {
                MaterialTheme.colorScheme.onSurface
              },
      )

      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            contentColor = MaterialTheme.colorScheme.primary,
            onClick = onTopUpClick,
        ) {
          Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = "Top-up",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
          }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          if (pocket.showHistory) {
            IconButton(onClick = onHistoryClick) {
              Icon(
                  imageVector = Icons.Default.History,
                  contentDescription = "Pocket activity",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(20.dp),
              )
            }
          }
          IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Pocket settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
fun DynamicBalanceSyncBanner(
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainer,
  ) {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
      Surface(
          modifier = Modifier.size(36.dp),
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp),
          )
        }
      }

      Column(
          verticalArrangement = Arrangement.spacedBy(2.dp),
      ) {
        Text(
            text = "Dynamic Balance Sync",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text =
                "Deleting or editing an expense linked to a pocket automatically restores its balance in real time.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
fun PocketsSectionHeader(
    title: String = "Your Budget Pockets",
    modifier: Modifier = Modifier,
    onFilterClick: () -> Unit = {},
) {
  Row(
      modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )
  }
}

object FakePocketFactory {
  fun create(
      id: String = "1",
      name: String = "Groceries & Household",
      categoryTag: String = "Groceries",
      amountFormatted: String = "850,000 T",
      icon: ImageVector = Icons.Default.ShoppingBag,
      isOverBudget: Boolean = false,
      warningMessage: String? = null,
      showHistory: Boolean = false,
  ): PocketItem =
      PocketItem(
          id = id,
          name = name,
          categoryTag = categoryTag,
          amountFormatted = amountFormatted,
          icon = icon,
          isOverBudget = isOverBudget,
          warningMessage = warningMessage,
          showHistory = showHistory,
      )

  fun createList(): List<PocketItem> =
      listOf(
          PocketItem(
              id = "1",
              name = "Groceries & Household",
              categoryTag = "Groceries",
              amountFormatted = "850,000 T",
              icon = Icons.Default.ShoppingBag,
              isOverBudget = false,
              warningMessage = null,
              showHistory = false,
          ),
          PocketItem(
              id = "2",
              name = "Daily Cafe & Coffee",
              categoryTag = "Coffee & Dining",
              amountFormatted = "565,000 T",
              icon = Icons.Default.LocalCafe,
              isOverBudget = true,
              warningMessage = "Over budget by 65,000 T",
              showHistory = false,
          ),
          PocketItem(
              id = "3",
              name = "Entertainment & Gaming",
              categoryTag = "Entertainment",
              amountFormatted = "1,200,000 T",
              icon = Icons.Default.SportsEsports,
              isOverBudget = false,
              warningMessage = null,
              showHistory = true,
          ),
      )
}

@ComponentPreviews
@Composable
private fun PocketBudgetCardNormalPreview() {
  PocketBudgetCard(
      pocket = FakePocketFactory.create(),
  )
}

@ComponentPreviews
@Composable
private fun PocketBudgetCardWarningPreview() {
  PocketBudgetCard(
      pocket =
          FakePocketFactory.create(
              id = "2",
              name = "Daily Cafe & Coffee",
              categoryTag = "Coffee & Dining",
              amountFormatted = "565,000 T",
              icon = Icons.Default.LocalCafe,
              isOverBudget = true,
              warningMessage = "Over budget by 65,000 T",
          ),
  )
}

@ComponentPreviews
@Composable
private fun PocketWarningHeaderPreview() {
  PocketWarningHeader(message = "Over budget by 65,000 T")
}

@ComponentPreviews
@Composable
private fun DynamicBalanceSyncBannerPreview() {
  DynamicBalanceSyncBanner()
}

@ComponentPreviews
@Composable
private fun PocketsSectionHeaderPreview() {
  PocketsSectionHeader()
}
