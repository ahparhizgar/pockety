package com.base.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.calendar.CalendarType
import com.base.ui.ComponentPreviews
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.calendar_gregorian
import pockety.app.shared.generated.resources.calendar_gregorian_subtitle
import pockety.app.shared.generated.resources.calendar_gregorian_symbol
import pockety.app.shared.generated.resources.calendar_persian
import pockety.app.shared.generated.resources.calendar_persian_subtitle
import pockety.app.shared.generated.resources.calendar_persian_symbol
import pockety.app.shared.generated.resources.calendar_system_title
import pockety.app.shared.generated.resources.settings_title

private data class CalendarOptionData(
    val title: String,
    val subtitle: String,
    val symbolText: String,
    val isSelected: Boolean,
)

@Composable
fun SettingsScreen(
    component: SettingsComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.testTag("settings-screen"),
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.settings_title),
        )
      },
  ) { paddingValues ->
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.TopCenter,
    ) {
      Column(
          modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth().padding(16.dp).selectableGroup(),
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        DisplayCalendarSystemCard(
            selectedCalendarType = state.selectedCalendarType,
            onCalendarTypeSelect = component::onCalendarTypeSelected,
        )
      }
    }
  }
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
        CalendarOptionCard(
            data =
                CalendarOptionData(
                    title = stringResource(Res.string.calendar_gregorian),
                    subtitle = stringResource(Res.string.calendar_gregorian_subtitle),
                    symbolText = stringResource(Res.string.calendar_gregorian_symbol),
                    isSelected = selectedCalendarType == CalendarType.GREGORIAN,
                ),
            onClick = { onCalendarTypeSelect(CalendarType.GREGORIAN) },
            modifier = Modifier.weight(1f).testTag("calendar-option-gregorian"),
        )
        CalendarOptionCard(
            data =
                CalendarOptionData(
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
private fun CalendarOptionCard(
    data: CalendarOptionData,
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
      modifier =
          modifier.selectable(
              selected = data.isSelected,
              onClick = onClick,
              role = Role.RadioButton,
          ),
  ) {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
      CalendarOptionHeader(
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
private fun CalendarOptionHeader(
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
                    fontSize = 12.sp,
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

@ComponentPreviews
@Composable
private fun CalendarOptionCardSelectedPreview() {
  CalendarOptionCard(
      data =
          CalendarOptionData(
              title = "Gregorian",
              subtitle = "Standard Western (Jan-Dec)",
              symbolText = "G",
              isSelected = true,
          ),
      onClick = {},
  )
}

@ComponentPreviews
@Composable
private fun CalendarOptionCardUnselectedPreview() {
  CalendarOptionCard(
      data =
          CalendarOptionData(
              title = "Persian",
              subtitle = "Solar Hijri (Farvardin-Esfand)",
              symbolText = "P",
              isSelected = false,
          ),
      onClick = {},
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

@Composable
@ScreenPreviews
private fun SettingsScreenPreview() {
  SettingsScreen(component = FakeSettingsComponent())
}

@Composable
@ScreenPreviews
private fun SettingsScreenPersianPreview() {
  SettingsScreen(
      component =
          FakeSettingsComponent(
              initialState = SettingsState(selectedCalendarType = CalendarType.PERSIAN),
          ),
  )
}
