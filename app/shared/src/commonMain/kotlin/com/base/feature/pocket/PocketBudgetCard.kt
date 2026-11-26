package com.base.feature.pocket

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
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.base.db.FakePocketItemModelFactory
import com.base.db.PocketItemModel
import com.base.feature.category.add.resolveCategoryIcon
import com.base.ui.ComponentPreviews
import com.base.ui.money.moneyLabel
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.pocket_over_budget_amount
import pockety.app.shared.generated.resources.pockets_dynamic_sync_desc
import pockety.app.shared.generated.resources.pockets_dynamic_sync_title

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
    pocket: PocketItemModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
  Surface(
      modifier = modifier.fillMaxWidth().testTag("pocket-card-${pocket.id}"),
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      tonalElevation = 1.dp,
      onClick = onClick,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      if (pocket.warningMessage != null) {
        PocketWarningHeader(
            message =
                stringResource(
                    Res.string.pocket_over_budget_amount,
                    moneyLabel(-pocket.remainingBalance),
                )
        )
      }
      PocketCardHeader(pocket = pocket)
      Text(
          text = moneyLabel(pocket.remainingBalance),
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color =
              if (pocket.isOverBudget) {
                MaterialTheme.colorScheme.error
              } else {
                MaterialTheme.colorScheme.onSurface
              },
      )
    }
  }
}

@Composable
private fun PocketCardHeader(pocket: PocketItemModel) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top,
  ) {
    PocketCardTitle(pocket = pocket, modifier = Modifier.weight(1f))
    PocketCardIconBadge(pocket = pocket)
  }
}

@Composable
private fun PocketCardTitle(pocket: PocketItemModel, modifier: Modifier = Modifier) {
  Column(
      modifier = modifier,
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
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
      ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Label,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = pocket.categoryTag,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
      }
    }

    Text(
        text = pocket.name,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )
  }
}

@Composable
private fun PocketCardIconBadge(pocket: PocketItemModel) {
  PocketShapeBadge(
      icon = resolveCategoryIcon(pocket.icon),
      color = Color(pocket.color),
      contentDescription = pocket.name,
  )
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
            text = stringResource(Res.string.pockets_dynamic_sync_title),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(Res.string.pockets_dynamic_sync_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

fun formatCurrency(amount: Long): String {
  val isNegative = amount < 0
  val absAmount = if (isNegative) -amount else amount
  val formatted = absAmount.toString().reversed().chunked(3).joinToString(",").reversed()
  return if (isNegative) "-$formatted T" else "$formatted T"
}

@ComponentPreviews
@Composable
private fun PocketBudgetCardNormalPreview() {
  PocketBudgetCard(pocket = FakePocketItemModelFactory.create())
}

@ComponentPreviews
@Composable
private fun PocketBudgetCardWarningPreview() {
  PocketBudgetCard(
      pocket =
          FakePocketItemModelFactory.create(
              id = 2,
              name = "Daily Cafe & Coffee",
              categoryTag = "Coffee & Dining",
              budgetAmount = 500_000,
              remainingBalance = -65_000,
              icon = "coffee",
              isOverBudget = true,
              warningMessage = "Over budget by 65,000 T",
          )
  )
}

@ComponentPreviews
@Composable
private fun DynamicBalanceSyncBannerPreview() {
  DynamicBalanceSyncBanner()
}
