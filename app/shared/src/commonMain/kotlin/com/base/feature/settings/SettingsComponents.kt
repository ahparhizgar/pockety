package com.base.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.calendar.CalendarType
import com.base.ui.ComponentPreviews
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.calendar_gregorian
import pockety.app.shared.generated.resources.calendar_gregorian_subtitle
import pockety.app.shared.generated.resources.calendar_gregorian_symbol
import pockety.app.shared.generated.resources.calendar_persian
import pockety.app.shared.generated.resources.calendar_persian_subtitle
import pockety.app.shared.generated.resources.calendar_persian_symbol
import pockety.app.shared.generated.resources.calendar_system_title
import pockety.app.shared.generated.resources.settings_currency_kilo_tomans_subtitle
import pockety.app.shared.generated.resources.settings_currency_kilo_tomans_symbol
import pockety.app.shared.generated.resources.settings_currency_kilo_tomans_title
import pockety.app.shared.generated.resources.settings_currency_rials_subtitle
import pockety.app.shared.generated.resources.settings_currency_rials_symbol
import pockety.app.shared.generated.resources.settings_currency_rials_title
import pockety.app.shared.generated.resources.settings_currency_title
import pockety.app.shared.generated.resources.settings_currency_tomans_subtitle
import pockety.app.shared.generated.resources.settings_currency_tomans_symbol
import pockety.app.shared.generated.resources.settings_currency_tomans_title
import pockety.app.shared.generated.resources.settings_language_english
import pockety.app.shared.generated.resources.settings_language_title

data class SettingsRowIconData(
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color,
)

data class NavigationRowData(
    val title: String,
    val subtitle: String,
    val iconData: SettingsRowIconData,
    val countBadgeText: String? = null,
)

data class OptionCardData(
    val title: String,
    val subtitle: String,
    val symbolText: String,
    val isSelected: Boolean,
)

data class SwitchRowData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val checked: Boolean,
)

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
    data: NavigationRowData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
        SettingsRowIconBox(iconData = data.iconData)
        SettingsRowTextColumn(
            title = data.title,
            subtitle = data.subtitle,
            modifier = Modifier.weight(1f),
        )
      }
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        if (data.countBadgeText != null) {
          SettingsBadge(text = data.countBadgeText)
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
private fun SettingsRowIconBox(
    iconData: SettingsRowIconData,
    modifier: Modifier = Modifier,
) {
  Surface(
      shape = RoundedCornerShape(16.dp),
      color = iconData.containerColor,
      contentColor = iconData.contentColor,
      modifier = modifier.size(44.dp),
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
          imageVector = iconData.icon,
          contentDescription = null,
          modifier = Modifier.size(22.dp),
      )
    }
  }
}

@Composable
private fun SettingsRowTextColumn(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
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

@Composable
private fun SettingsBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
  Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
      modifier = modifier,
  ) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
    )
  }
}

@Composable
fun SettingsLanguageRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.settings_language_title),
    selectedLanguage: String = stringResource(Res.string.settings_language_english),
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
        modifier = Modifier.testTag("settings-language-picker-pill"),
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
fun OptionSelectionCard(
    data: OptionCardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val containerColor =
      if (data.isSelected) {
        MaterialTheme.colorScheme.surfaceContainerLowest
      } else {
        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
      }
  val shadowElevation = if (data.isSelected) 1.dp else 0.dp

  Surface(
      shape = RoundedCornerShape(16.dp),
      color = containerColor,
      shadowElevation = shadowElevation,
      modifier = modifier.semantics(mergeDescendants = true) {},
  ) {
    Column(
        modifier =
            Modifier.clip(RoundedCornerShape(16.dp))
                .selectable(
                    selected = data.isSelected,
                    onClick = onClick,
                    role = Role.RadioButton,
                )
                .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
      OptionCardHeader(
          symbolText = data.symbolText,
          isSelected = data.isSelected,
      )
      Column {
        Text(
            text = data.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = data.subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
        )
      }
    }
  }
}

@Composable
private fun OptionCardHeader(
    symbolText: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().padding(bottom = 12.dp),
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
}

@Composable
fun DisplayCurrencyCard(
    selectedCurrency: CurrencyType,
    onCurrencySelect: (CurrencyType) -> Unit,
    modifier: Modifier = Modifier,
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
            text = stringResource(Res.string.settings_currency_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
      }
      Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        listOf(CurrencyType.TOMANS, CurrencyType.RIALS, CurrencyType.KILO_TOMANS).forEach { currency
          ->
          OptionSelectionCard(
              data = currencyOptionData(currency, selectedCurrency),
              onClick = { onCurrencySelect(currency) },
              modifier =
                  Modifier.fillMaxWidth().testTag("currency-option-${currency.name.lowercase()}"),
          )
        }
      }
    }
  }
}

@Composable
private fun currencyOptionData(currency: CurrencyType, selected: CurrencyType): OptionCardData {
  val (title, subtitle, symbol) =
      when (currency) {
        CurrencyType.TOMANS ->
            Triple(
                Res.string.settings_currency_tomans_title,
                Res.string.settings_currency_tomans_subtitle,
                Res.string.settings_currency_tomans_symbol,
            )
        CurrencyType.RIALS ->
            Triple(
                Res.string.settings_currency_rials_title,
                Res.string.settings_currency_rials_subtitle,
                Res.string.settings_currency_rials_symbol,
            )
        CurrencyType.KILO_TOMANS ->
            Triple(
                Res.string.settings_currency_kilo_tomans_title,
                Res.string.settings_currency_kilo_tomans_subtitle,
                Res.string.settings_currency_kilo_tomans_symbol,
            )
      }
  return OptionCardData(
      title = stringResource(title),
      subtitle = stringResource(subtitle),
      symbolText = stringResource(symbol),
      isSelected = currency == selected,
  )
}

@Composable
fun DisplayCalendarSystemCard(
    selectedCalendarType: CalendarType,
    onCalendarTypeSelect: (CalendarType) -> Unit,
    modifier: Modifier = Modifier,
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
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = stringResource(Res.string.calendar_system_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
      }
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        OptionSelectionCard(
            data =
                OptionCardData(
                    title = stringResource(Res.string.calendar_gregorian),
                    subtitle = stringResource(Res.string.calendar_gregorian_subtitle),
                    symbolText = stringResource(Res.string.calendar_gregorian_symbol),
                    isSelected = selectedCalendarType == CalendarType.GREGORIAN,
                ),
            onClick = { onCalendarTypeSelect(CalendarType.GREGORIAN) },
            modifier = Modifier.weight(1f).testTag("calendar-option-gregorian"),
        )
        OptionSelectionCard(
            data =
                OptionCardData(
                    title = stringResource(Res.string.calendar_persian),
                    subtitle = stringResource(Res.string.calendar_persian_subtitle),
                    symbolText = stringResource(Res.string.calendar_persian_symbol),
                    isSelected = selectedCalendarType == CalendarType.PERSIAN,
                ),
            onClick = { onCalendarTypeSelect(CalendarType.PERSIAN) },
            modifier = Modifier.weight(1f).testTag("calendar-option-persian"),
        )
      }
    }
  }
}

@Composable
fun SwitchRow(
    data: SwitchRowData,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    iconContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    iconContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
  Row(
      modifier =
          modifier
              .fillMaxWidth()
              .toggleable(
                  value = data.checked,
                  role = Role.Switch,
                  onValueChange = onCheckedChange,
              ),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
        modifier = Modifier.weight(1f).padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      SettingsRowIconBox(
          iconData =
              SettingsRowIconData(
                  icon = data.icon,
                  containerColor = iconContainerColor,
                  contentColor = iconContentColor,
              ),
      )
      SettingsRowTextColumn(
          title = data.title,
          subtitle = data.subtitle,
          modifier = Modifier.weight(1f),
      )
    }
    Switch(
        checked = data.checked,
        onCheckedChange = null,
        thumbContent = {
          if (data.checked) {
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
    version: String,
    modifier: Modifier = Modifier,
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
private fun SettingsSectionHeaderPreview() {
  SettingsSectionHeader(title = "Organize & Taxonomy")
}

@ComponentPreviews
@Composable
private fun SettingsNavigationRowPreview() {
  SettingsNavigationRow(
      data =
          NavigationRowData(
              title = "Categories",
              subtitle = "Manage expense categories & icons",
              iconData =
                  SettingsRowIconData(
                      icon = Icons.Default.Category,
                      containerColor = MaterialTheme.colorScheme.secondaryContainer,
                      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                  ),
              countBadgeText = "18 items",
          ),
      onClick = {},
  )
}

@ComponentPreviews
@Composable
private fun SettingsLanguageRowPreview() {
  SettingsLanguageRow(
      title = "App language",
      selectedLanguage = "English",
      onClick = {},
  )
}

@ComponentPreviews
@Composable
private fun OptionSelectionCardSelectedPreview() {
  OptionSelectionCard(
      data =
          OptionCardData(
              title = "Tomans",
              subtitle = "Common (10 Rials)",
              symbolText = "T",
              isSelected = true,
          ),
      onClick = {},
  )
}

@ComponentPreviews
@Composable
private fun OptionSelectionCardUnselectedPreview() {
  OptionSelectionCard(
      data =
          OptionCardData(
              title = "Rials",
              subtitle = "Official Banking",
              symbolText = "IRR",
              isSelected = false,
          ),
      onClick = {},
  )
}

@ComponentPreviews
@Composable
private fun DisplayCurrencyCardPreview() {
  DisplayCurrencyCard(
      selectedCurrency = CurrencyType.TOMANS,
      onCurrencySelect = {},
  )
}

@ComponentPreviews
@Composable
private fun DisplayCalendarSystemCardPreview() {
  DisplayCalendarSystemCard(
      selectedCalendarType = CalendarType.GREGORIAN,
      onCalendarTypeSelect = {},
  )
}

@ComponentPreviews
@Composable
private fun SwitchRowCheckedPreview() {
  SwitchRow(
      data =
          SwitchRowData(
              title = "End-of-day reminder",
              subtitle = "Notify at 21:00 to review pending SMS",
              icon = Icons.Default.Alarm,
              checked = true,
          ),
      onCheckedChange = {},
  )
}

@ComponentPreviews
@Composable
private fun SwitchRowUncheckedPreview() {
  SwitchRow(
      data =
          SwitchRowData(
              title = "End-of-day reminder",
              subtitle = "Notify at 21:00 to review pending SMS",
              icon = Icons.Default.Alarm,
              checked = false,
          ),
      onCheckedChange = {},
  )
}

@ComponentPreviews
@Composable
private fun SettingsVersionBadgePreview() {
  SettingsVersionBadge(version = "Pockety v1.2.0")
}
