package com.base.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.calendar.CalendarType
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.calendar_gregorian
import pockety.app.shared.generated.resources.calendar_persian
import pockety.app.shared.generated.resources.calendar_system_desc
import pockety.app.shared.generated.resources.calendar_system_title
import pockety.app.shared.generated.resources.settings_title

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
        CalendarSettingsSection(
            selectedType = state.selectedCalendarType,
            onSelect = component::onCalendarTypeSelected,
        )
      }
    }
  }
}

@Composable
private fun CalendarSettingsSection(
    selectedType: CalendarType,
    onSelect: (CalendarType) -> Unit,
    modifier: Modifier = Modifier,
) {
  Card(
      modifier = modifier.fillMaxWidth(),
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant,
          ),
  ) {
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
      Text(
          text = stringResource(Res.string.calendar_system_title),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
          text = stringResource(Res.string.calendar_system_desc),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
      )
      Spacer(modifier = Modifier.height(16.dp))

      CalendarOptionRow(
          title = stringResource(Res.string.calendar_gregorian),
          selected = selectedType == CalendarType.GREGORIAN,
          onClick = { onSelect(CalendarType.GREGORIAN) },
          testTag = "calendar-option-gregorian",
      )
      Spacer(modifier = Modifier.height(8.dp))
      CalendarOptionRow(
          title = stringResource(Res.string.calendar_persian),
          selected = selectedType == CalendarType.PERSIAN,
          onClick = { onSelect(CalendarType.PERSIAN) },
          testTag = "calendar-option-persian",
      )
    }
  }
}

@Composable
private fun CalendarOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier =
          modifier
              .fillMaxWidth()
              .selectable(
                  selected = selected,
                  onClick = onClick,
                  role = Role.RadioButton,
              )
              .testTag(testTag)
              .padding(vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    RadioButton(
        selected = selected,
        onClick = null,
    )
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(start = 12.dp),
    )
  }
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
