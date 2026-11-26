package com.base.feature.expense.add

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.base.db.PocketItemModel
import com.base.feature.category.add.resolveCategoryIcon
import com.base.feature.pocket.PocketGlyphIcon
import com.base.feature.pocket.PocketShapeBadge
import com.base.ui.ComponentPreviews
import com.base.ui.money.moneyLabel
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.auto_deduct_pocket_budget
import pockety.app.shared.generated.resources.pocket_label
import pockety.app.shared.generated.resources.tap_to_link_pocket
import pockety.app.shared.generated.resources.unlink_pocket_action

@Composable
internal fun PocketSelectorSection(
    component: AddExpenseComponent,
    selectedPocket: PocketItemModel?,
    modifier: Modifier = Modifier,
) {
  val icon =
      if (selectedPocket != null) {
        resolveCategoryIcon(selectedPocket.icon)
      } else {
        Icons.Default.AccountBalanceWallet
      }
  val balanceText = if (selectedPocket != null) moneyLabel(selectedPocket.remainingBalance) else ""

  PocketSelectorCard(
      pocketName = selectedPocket?.name,
      pocketColor = selectedPocket?.color,
      onPocketClick = component::onSelectPocketClicked,
      onClearPocketClick = component::onPocketCleared,
      modifier = modifier,
      pocketBalance = balanceText,
      pocketIcon = icon,
  )
}

@Suppress("LongParameterList")
@Composable
fun PocketSelectorCard(
    pocketName: String?,
    onPocketClick: () -> Unit,
    onClearPocketClick: () -> Unit,
    modifier: Modifier = Modifier,
    pocketBalance: String = "",
    pocketIcon: ImageVector = Icons.Default.AccountBalanceWallet,
    pocketColor: Long? = null,
) {
  val hasPocket = !pocketName.isNullOrBlank()

  Surface(
      modifier = modifier.fillMaxWidth().testTag("pocket-selector-section"),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      PocketSelectorHeader()

      Surface(
          modifier = Modifier.fillMaxWidth().testTag("selected-pocket-card"),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 1.dp,
          onClick = onPocketClick,
      ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
          PocketCardContent(
              pocketName = pocketName,
              pocketColor = pocketColor,
              hasPocket = hasPocket,
              pocketBalance = pocketBalance,
              pocketIcon = pocketIcon,
              modifier = Modifier.weight(1f),
          )

          if (hasPocket) {
            IconButton(
                onClick = onClearPocketClick,
                modifier = Modifier.testTag("clear-pocket-btn"),
            ) {
              Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = stringResource(Res.string.unlink_pocket_action),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(20.dp),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun PocketSelectorHeader(
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
      PocketGlyphIcon(
          tint = MaterialTheme.colorScheme.tertiary,
          stitchColor = MaterialTheme.colorScheme.surfaceContainerLow,
          glyphSize = 20.dp,
      )
      Text(
          text = stringResource(Res.string.pocket_label),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

@Composable
private fun PocketCardContent(
    pocketName: String?,
    hasPocket: Boolean,
    pocketBalance: String,
    pocketIcon: ImageVector,
    modifier: Modifier = Modifier,
    pocketColor: Long? = null,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    PocketShapeBadge(
        icon = if (hasPocket) pocketIcon else null,
        color =
            when {
              hasPocket && pocketColor != null -> Color(pocketColor)
              hasPocket -> MaterialTheme.colorScheme.secondaryContainer
              else -> MaterialTheme.colorScheme.surfaceContainerHigh
            },
        contentDescription = pocketName,
        badgeSize = 48.dp,
        iconTint =
            when {
              hasPocket && pocketColor != null -> Color.White
              hasPocket -> MaterialTheme.colorScheme.onSecondaryContainer
              else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
    )
    Column(modifier = Modifier.weight(1f)) {
      Text(
          text =
              if (hasPocket) pocketName.orEmpty()
              else stringResource(Res.string.tap_to_link_pocket),
          style = MaterialTheme.typography.titleMedium,
          color =
              if (hasPocket) {
                MaterialTheme.colorScheme.onSurface
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant
              },
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )
      Text(
          text =
              if (hasPocket) {
                "Balance: $pocketBalance"
              } else {
                stringResource(Res.string.auto_deduct_pocket_budget)
              },
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

// ============================================================================
// Previews
// ============================================================================

@ComponentPreviews
@Composable
private fun PocketSelectorCardLinkedPreview() {
  PocketSelectorCard(
      pocketName = "Coffee Pocket",
      onPocketClick = {},
      onClearPocketClick = {},
      pocketBalance = "400,000 T",
      pocketIcon = Icons.Default.LocalCafe,
  )
}

@ComponentPreviews
@Composable
private fun PocketSelectorCardUnlinkedPreview() {
  PocketSelectorCard(
      pocketName = null,
      onPocketClick = {},
      onClearPocketClick = {},
  )
}
