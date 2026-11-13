package com.base.design.features.privacypolicy

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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SmsFailed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.design.ui.ComponentPreviews
import com.base.design.ui.theme.colorSchemeExtended

@Composable
fun PrivacyHeroCard(
    modifier: Modifier = Modifier,
    title: String = "Your Privacy is Respected",
    subtitle: String =
        "Pockety is designed with offline-first and strict privacy principles. Your personal financial information never leaves your device.",
    icon: ImageVector = Icons.Default.Lock,
) {
  Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorSchemeExtended.primaryFixed,
      contentColor = MaterialTheme.colorSchemeExtended.onPrimaryFixed,
      modifier = modifier.fillMaxWidth(),
  ) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.size(56.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = icon,
              contentDescription = null,
              modifier = Modifier.size(28.dp),
          )
        }
      }
      Text(
          text = title,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorSchemeExtended.onPrimaryFixed,
      )
      Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
          color = MaterialTheme.colorSchemeExtended.onPrimaryFixedVariant,
      )
    }
  }
}

@Composable
fun PrivacyPolicyPillarCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconContainerColor: Color,
    iconContentColor: Color,
    modifier: Modifier = Modifier,
) {
  Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shadowElevation = 1.dp,
      modifier = modifier.fillMaxWidth(),
  ) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Surface(
          shape = RoundedCornerShape(16.dp),
          color = iconContainerColor,
          contentColor = iconContentColor,
          modifier = Modifier.size(48.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = icon,
              contentDescription = null,
              modifier = Modifier.size(24.dp),
          )
        }
      }
      Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
fun PrivacyDataSummaryCard(
    modifier: Modifier = Modifier,
) {
  Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = modifier.fillMaxWidth(),
  ) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text(
          text = "Key Takeaways",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface,
      )
      PrivacyBulletPoint(text = "Zero personal account or identity collection")
      PrivacyBulletPoint(text = "SMS parsing is 100% on-device")
      PrivacyBulletPoint(text = "Telemetry is strictly anonymous and opt-in")
    }
  }
}

@Composable
fun PrivacyBulletPoint(
    text: String,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp),
    )
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@ComponentPreviews
@Composable
fun PrivacyHeroCardPreview() {
  PrivacyHeroCard()
}

@ComponentPreviews
@Composable
fun PrivacyPolicyPillarCardPreview() {
  PrivacyPolicyPillarCard(
      title = "No personal data sent",
      description =
          "We do not send, sell, or share personal data. Your accounts, categories, and balances stay on your phone.",
      icon = Icons.Default.Lock,
      iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
      iconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
  )
}

@ComponentPreviews
@Composable
fun PrivacyPolicyPillarSmsPreview() {
  PrivacyPolicyPillarCard(
      title = "No SMS data sent",
      description =
          "SMS parsing happens strictly on-device. SMS text and sender details never leave your phone.",
      icon = Icons.Default.SmsFailed,
      iconContainerColor = MaterialTheme.colorSchemeExtended.tertiaryFixed,
      iconContentColor = MaterialTheme.colorSchemeExtended.onTertiaryFixedVariant,
  )
}

@ComponentPreviews
@Composable
fun PrivacyPolicyPillarAnalyticsPreview() {
  PrivacyPolicyPillarCard(
      title = "Anonymous usage data only",
      description =
          "We only send and store anonymous usage metrics to improve app stability, app speed, and user experience.",
      icon = Icons.Default.Analytics,
      iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
      iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
  )
}

@ComponentPreviews
@Composable
fun PrivacyDataSummaryCardPreview() {
  PrivacyDataSummaryCard()
}
