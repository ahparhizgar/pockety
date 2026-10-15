package com.base.capability.sms_inbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.ui.ComponentPreviews
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.inbox_auto_sync_on
import pockety.app.shared.generated.resources.inbox_candidates_desc
import pockety.app.shared.generated.resources.inbox_candidates_pending
import pockety.app.shared.generated.resources.inbox_smart_extraction

@Composable
fun SmartExtractionHeaderCard(
    remainingCount: Int,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      SmartExtractionHeaderBadge()
      SmartExtractionHeaderText(remainingCount = remainingCount)

      LinearProgressIndicator(
          progress = { if (remainingCount > 0) 0.5f else 1f },
          modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
          color = MaterialTheme.colorScheme.primaryContainer,
          trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
      )
    }
  }
}

@Composable
private fun SmartExtractionHeaderBadge() {
  Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
          text = stringResource(Res.string.inbox_smart_extraction),
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.secondary,
          letterSpacing = 0.5.sp,
      )

      Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondaryContainer,
      ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Surface(
              modifier = Modifier.size(6.dp),
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primary,
          ) {}
          Text(
              text = stringResource(Res.string.inbox_auto_sync_on),
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              fontWeight = FontWeight.Medium,
          )
        }
      }
    }
  }
}

@Composable
private fun SmartExtractionHeaderText(remainingCount: Int) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
        text = stringResource(Res.string.inbox_candidates_pending, remainingCount),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
        text = stringResource(Res.string.inbox_candidates_desc, remainingCount),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 18.sp,
    )
  }
}

@ComponentPreviews
@Composable
private fun SmartExtractionHeaderCardPreview() {
  SmartExtractionHeaderCard(remainingCount = 3)
}
