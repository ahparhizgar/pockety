package com.base.feature.pocket.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.base.feature.category.add.resolveCategoryIcon
import com.base.ui.ComponentPreviews
import com.base.ui.money.currencySymbol
import com.base.ui.money.moneyAmount
import com.base.ui.money.moneyLabel
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.pocket_details_balance_label
import pockety.app.shared.generated.resources.pocket_details_configure_button
import pockety.app.shared.generated.resources.pocket_details_top_up_button
import pockety.app.shared.generated.resources.pocket_details_tx_unlink_action

@Composable
fun PocketDetailsHeaderSection(
    name: String,
    balanceAmount: Long,
    icon: String,
    color: Long,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Surface(
        modifier = Modifier.size(64.dp),
        shape = CircleShape,
        color = Color(color),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
            imageVector = resolveCategoryIcon(icon),
            contentDescription = name,
            tint = Color.White,
            modifier = Modifier.size(32.dp),
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = name,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
    ) {
      Text(
          text = moneyAmount(balanceAmount),
          style = MaterialTheme.typography.displayMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.testTag("pocket-balance-amount"),
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
          text = currencySymbol(),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(bottom = 6.dp),
      )
    }

    Text(
        text = stringResource(Res.string.pocket_details_balance_label),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun PocketDetailsActionBar(
    onTopUpClick: () -> Unit,
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Button(
        onClick = onTopUpClick,
        modifier = Modifier.weight(1f).height(48.dp).testTag("action-top-up-button"),
        shape = RoundedCornerShape(24.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
    ) {
      Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
          text = stringResource(Res.string.pocket_details_top_up_button),
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
      )
    }

    FilledTonalButton(
        onClick = onConfigureClick,
        modifier = Modifier.weight(1f).height(48.dp).testTag("action-configure-button"),
        shape = RoundedCornerShape(24.dp),
        colors =
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
    ) {
      Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
          text = stringResource(Res.string.pocket_details_configure_button),
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
      )
    }
  }
}

@Composable
fun PocketFeedbackBanner(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth().testTag("feedback-banner"),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
      }
      IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(24.dp).testTag("dismiss-feedback-button"),
      ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Dismiss",
            modifier = Modifier.size(16.dp),
        )
      }
    }
  }
}

@Composable
fun PocketTransactionRow(
    item: PocketTransactionUiModel,
    onUnlink: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier =
          modifier.fillMaxWidth().padding(vertical = 10.dp).testTag("transaction-item-${item.id}"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    PocketTransactionLeading(item = item, modifier = Modifier.weight(1f))
    PocketTransactionTrailing(item = item, onUnlink = onUnlink)
  }
}

@Composable
private fun PocketTransactionLeading(
    item: PocketTransactionUiModel,
    modifier: Modifier = Modifier,
) {
  val iconBgColor =
      if (item.isTopUp) {
        MaterialTheme.colorScheme.secondaryContainer
      } else {
        MaterialTheme.colorScheme.surfaceContainer
      }

  Row(
      modifier = modifier.padding(end = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Surface(
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        color = iconBgColor,
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
            imageVector = resolveCategoryIcon(item.icon),
            contentDescription = item.title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
      }
    }

    Column(modifier = Modifier.weight(1f)) {
      Text(
          text = item.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )
      val subtitle =
          if (item.note.isNotBlank()) "${item.timestamp} • ${item.note}" else item.timestamp
      Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
private fun PocketTransactionTrailing(
    item: PocketTransactionUiModel,
    onUnlink: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Text(
        text = "${if (item.isTopUp) "+" else "-"}${moneyLabel(item.amount)}",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color =
            if (item.isTopUp) {
              MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.onSurface
            },
    )
    if (!item.isTopUp) {
      IconButton(
          onClick = onUnlink,
          modifier = Modifier.size(36.dp).testTag("unlink-tx-${item.id}"),
      ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(Res.string.pocket_details_tx_unlink_action),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}

fun formatAmount(amount: Long): String {
  val chunkSize = 3
  return amount.toString().reversed().chunked(chunkSize).joinToString(",").reversed()
}

@Suppress("MagicNumber")
@ComponentPreviews
@Composable
private fun PocketDetailsHeaderSectionPreview() {
  PocketDetailsHeaderSection(
      name = "Daily Cafe & Coffee",
      balanceAmount = 850_000L,
      icon = "coffee",
      color = 0xFF4A7C59,
  )
}

@ComponentPreviews
@Composable
private fun PocketDetailsActionBarPreview() {
  PocketDetailsActionBar(
      onTopUpClick = {},
      onConfigureClick = {},
  )
}

@ComponentPreviews
@Composable
private fun PocketTransactionRowPreview() {
  PocketTransactionRow(
      item = FakePocketTransactionUiModelFactory.create(),
      onUnlink = {},
  )
}
