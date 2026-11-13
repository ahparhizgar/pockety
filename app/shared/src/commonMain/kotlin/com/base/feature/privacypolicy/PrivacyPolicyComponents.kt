package com.base.feature.privacypolicy

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
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
import com.base.ui.ComponentPreviews
import com.base.ui.colorSchemeExtended
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.privacy_policy_hero_subtitle
import pockety.app.shared.generated.resources.privacy_policy_hero_title
import pockety.app.shared.generated.resources.privacy_policy_takeaway_personal_data
import pockety.app.shared.generated.resources.privacy_policy_takeaway_sms_data
import pockety.app.shared.generated.resources.privacy_policy_takeaway_telemetry
import pockety.app.shared.generated.resources.privacy_policy_takeaways_title

@Composable
fun PrivacyHeroCard(
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.privacy_policy_hero_title),
    subtitle: String = stringResource(Res.string.privacy_policy_hero_subtitle),
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

@Suppress("LongParameterList")
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
          text = stringResource(Res.string.privacy_policy_takeaways_title),
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface,
      )
      PrivacyBulletPoint(text = stringResource(Res.string.privacy_policy_takeaway_personal_data))
      PrivacyBulletPoint(text = stringResource(Res.string.privacy_policy_takeaway_sms_data))
      PrivacyBulletPoint(text = stringResource(Res.string.privacy_policy_takeaway_telemetry))
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
private fun PrivacyHeroCardPreview() {
  PrivacyHeroCard()
}

@ComponentPreviews
@Composable
private fun PrivacyPolicyPillarCardPreview() {
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
private fun PrivacyDataSummaryCardPreview() {
  PrivacyDataSummaryCard()
}
