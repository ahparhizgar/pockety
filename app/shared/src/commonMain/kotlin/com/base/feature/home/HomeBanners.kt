package com.base.feature.home

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.base.ui.ComponentPreviews
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.auto_track_bank_sms
import pockety.app.shared.generated.resources.enable_auto_detection_subtitle
import pockety.app.shared.generated.resources.enable_sms_permission_description
import pockety.app.shared.generated.resources.pending_review
import pockety.app.shared.generated.resources.undecided_expenses
import pockety.app.shared.generated.resources.view_undecided_expenses_description

@Composable
fun SmsPermissionBanner(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
  BannerContainer(
      style =
          BannerStyle(
              testTag = "sms-permission-banner",
              icon = Icons.Default.Sms,
              arrowDescription = stringResource(Res.string.enable_sms_permission_description),
          ),
      modifier = modifier,
      onClick = onClick,
  ) {
    Text(
        text = stringResource(Res.string.auto_track_bank_sms),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    Text(
        text = stringResource(Res.string.enable_auto_detection_subtitle),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.secondary,
        overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
fun UndecidedExpensesBanner(
    count: Int,
    amountFormatted: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
  BannerContainer(
      style =
          BannerStyle(
              testTag = "undecided-expenses-banner",
              icon = Icons.Default.Inbox,
              arrowDescription = stringResource(Res.string.view_undecided_expenses_description),
          ),
      modifier = modifier,
      onClick = onClick,
  ) {
    UndecidedExpensesTitleRow(count = count)
    Text(
        text = stringResource(Res.string.pending_review, amountFormatted),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.secondary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun UndecidedExpensesTitleRow(count: Int) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
        text = stringResource(Res.string.undecided_expenses),
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
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
      )
    }
  }
}

private data class BannerStyle(
    val testTag: String,
    val icon: ImageVector,
    val arrowDescription: String,
)

@Composable
private fun BannerContainer(
    style: BannerStyle,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
  Surface(
      onClick = onClick,
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.secondaryContainer,
      shadowElevation = 1.dp,
      modifier = modifier.fillMaxWidth().testTag(style.testTag),
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
        BannerIcon(icon = style.icon)
        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
          content()
        }
      }
      BannerArrow(contentDescription = style.arrowDescription)
    }
  }
}

@Composable
private fun BannerIcon(icon: ImageVector) {
  Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier.size(40.dp),
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(20.dp),
      )
    }
  }
}

@Composable
private fun BannerArrow(contentDescription: String) {
  Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.8f),
      contentColor = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(32.dp),
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = contentDescription,
          modifier = Modifier.size(18.dp),
      )
    }
  }
}

@ComponentPreviews
@Composable
private fun SmsPermissionBannerPreview() {
  SmsPermissionBanner()
}

@ComponentPreviews
@Composable
private fun UndecidedExpensesBannerPreview() {
  UndecidedExpensesBanner(
      count = 0,
      amountFormatted = "0 T",
  )
}
