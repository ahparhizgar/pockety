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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.design.ui.ComponentPreviews

enum class CurrencyType(
    val title: String,
    val subtitle: String,
    val symbol: String,
) {
  Tomans(
      title = "Tomans",
      subtitle = "Common (10 Rials)",
      symbol = "T",
  ),
  Rials(
      title = "Rials",
      subtitle = "Official Banking",
      symbol = "IRR",
  ),
}

@Composable
fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
  Text(
      text = title.uppercase(),
      style =
          MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.sp,
          ),
      color = MaterialTheme.colorScheme.secondary,
      modifier = modifier.padding(horizontal = 8.dp),
  )
}

@Composable
fun SettingsCardContainer(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
  Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shadowElevation = 1.dp,
      modifier = modifier.fillMaxWidth(),
  ) {
    Column(
        modifier = Modifier.padding(contentPadding),
        verticalArrangement = verticalArrangement,
        content = content,
    )
  }
}

@Composable
fun SettingsNavigationRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconContainerColor: Color,
    iconContentColor: Color,
    modifier: Modifier = Modifier,
    countBadgeText: String? = null,
    onClick: () -> Unit = {},
) {
  Surface(
      onClick = onClick,
      shape = RoundedCornerShape(16.dp),
      color = Color.Transparent,
      modifier = modifier.fillMaxWidth(),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = iconContainerColor,
            contentColor = iconContentColor,
            modifier = Modifier.size(44.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )
          }
        }
        Column(modifier = Modifier.weight(1f)) {
          Text(
              text = title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
          )
          Text(
              text = subtitle,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
          )
        }
      }
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        if (countBadgeText != null) {
          Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.surfaceContainerHigh,
          ) {
            Text(
                text = countBadgeText,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
          }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
      }
    }
  }
}

@Composable
fun SettingsLanguageRow(
    title: String = "App language",
    selectedLanguage: String = "English",
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
  Row(
      modifier = modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceContainerHigh,
          contentColor = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(44.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = Icons.Default.Translate,
              contentDescription = null,
              modifier = Modifier.size(22.dp),
          )
        }
      }
      Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.onSurface,
      )
    }
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
      Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Text(
            text = selectedLanguage,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(16.dp),
        )
      }
    }
  }
}

@Composable
fun CurrencyOptionCard(
    title: String,
    subtitle: String,
    symbolText: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
  val containerColor =
      if (isSelected) {
        MaterialTheme.colorScheme.surfaceContainerLowest
      } else {
        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
      }
  val shadowElevation = if (isSelected) 1.dp else 0.dp

  Surface(
      onClick = onClick,
      shape = RoundedCornerShape(16.dp),
      color = containerColor,
      shadowElevation = shadowElevation,
      modifier = modifier,
  ) {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
          modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Surface(
            shape = CircleShape,
            color =
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceContainer,
            contentColor =
                if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbolText,
                style =
                    MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (symbolText.length > 2) 10.sp else 12.sp,
                    ),
            )
          }
        }
        Icon(
            imageVector =
                if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = if (isSelected) "Selected" else "Unselected",
            tint =
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp),
        )
      }
      Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
        )
      }
    }
  }
}

@Composable
fun DisplayCurrencyCard(
    selectedCurrency: CurrencyType,
    modifier: Modifier = Modifier,
    onCurrencySelected: (CurrencyType) -> Unit = {},
) {
  Surface(
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = modifier.fillMaxWidth(),
  ) {
    Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
            imageVector = Icons.Default.CurrencyExchange,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = "Display Currency",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
      }
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        CurrencyOptionCard(
            title = CurrencyType.Tomans.title,
            subtitle = CurrencyType.Tomans.subtitle,
            symbolText = CurrencyType.Tomans.symbol,
            isSelected = selectedCurrency == CurrencyType.Tomans,
            onClick = { onCurrencySelected(CurrencyType.Tomans) },
            modifier = Modifier.weight(1f),
        )
        CurrencyOptionCard(
            title = CurrencyType.Rials.title,
            subtitle = CurrencyType.Rials.subtitle,
            symbolText = CurrencyType.Rials.symbol,
            isSelected = selectedCurrency == CurrencyType.Rials,
            onClick = { onCurrencySelected(CurrencyType.Rials) },
            modifier = Modifier.weight(1f),
        )
      }
    }
  }
}

@Composable
fun SwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    modifier: Modifier = Modifier,
    iconContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    iconContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    onCheckedChange: (Boolean) -> Unit = {},
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
        modifier = Modifier.weight(1f).padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Surface(
          shape = RoundedCornerShape(16.dp),
          color = iconContainerColor,
          contentColor = iconContentColor,
          modifier = Modifier.size(44.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = icon,
              contentDescription = null,
              modifier = Modifier.size(22.dp),
          )
        }
      }
      Column(modifier = Modifier.weight(1f)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
      }
    }
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        thumbContent = {
          if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
          }
        },
        colors =
            SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedIconColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
    )
  }
}

@Composable
fun SettingsVersionBadge(
    modifier: Modifier = Modifier,
    version: String = "Pockety v1.2.0",
) {
  Box(
      modifier = modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
      contentAlignment = Alignment.Center,
  ) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
      Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Icon(
            imageVector = Icons.Default.Eco,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = version,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
        )
      }
    }
  }
}

@ComponentPreviews
@Composable
fun SettingsSectionHeaderPreview() {
  SettingsSectionHeader(title = "Organize & Taxonomy")
}

@ComponentPreviews
@Composable
fun SettingsNavigationRowPreview() {
  SettingsNavigationRow(
      title = "Categories",
      subtitle = "Manage expense categories & icons",
      icon = Icons.Default.Category,
      iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
      iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
      countBadgeText = "18 items",
  )
}

@ComponentPreviews
@Composable
fun SettingsLanguageRowPreview() {
  SettingsLanguageRow(
      title = "App language",
      selectedLanguage = "English",
  )
}

@ComponentPreviews
@Composable
fun CurrencyOptionCardSelectedPreview() {
  CurrencyOptionCard(
      title = "Tomans",
      subtitle = "Common (10 Rials)",
      symbolText = "T",
      isSelected = true,
  )
}

@ComponentPreviews
@Composable
fun CurrencyOptionCardUnselectedPreview() {
  CurrencyOptionCard(
      title = "Rials",
      subtitle = "Official Banking",
      symbolText = "IRR",
      isSelected = false,
  )
}

@ComponentPreviews
@Composable
fun DisplayCurrencyCardPreview() {
  DisplayCurrencyCard(
      selectedCurrency = CurrencyType.Tomans,
  )
}

@ComponentPreviews
@Composable
fun SwitchRowCheckedPreview() {
  SwitchRow(
      title = "End-of-day reminder",
      subtitle = "Notify at 21:00 to review pending SMS",
      icon = Icons.Default.Alarm,
      checked = true,
  )
}

@ComponentPreviews
@Composable
fun SwitchRowUncheckedPreview() {
  SwitchRow(
      title = "End-of-day reminder",
      subtitle = "Notify at 21:00 to review pending SMS",
      icon = Icons.Default.Alarm,
      checked = false,
  )
}

@ComponentPreviews
@Composable
fun SettingsVersionBadgePreview() {
  SettingsVersionBadge(version = "Pockety v1.2.0")
}
