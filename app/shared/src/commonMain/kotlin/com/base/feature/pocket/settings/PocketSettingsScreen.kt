package com.base.feature.pocket.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.db.FakeCategoryFactory
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import com.base.usermessage.UiString
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.apply_action
import pockety.app.shared.generated.resources.pocket_settings_title
import pockety.app.shared.generated.resources.save_action

@Composable
fun PocketSettingsScreen(
    component: PocketSettingsComponent,
    modifier: Modifier = Modifier,
) {
  Scaffold(
      modifier = modifier.fillMaxSize().testTag("pocket-settings-screen").imePadding(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.pocket_settings_title),
            onBackClick = component::onBackClicked,
            modifier = Modifier.testTag("pocket-settings-topbar"),
        )
      },
      bottomBar = { PocketSettingsBottomBar(component = component) },
  ) { innerPadding ->
    Box(
        modifier = Modifier.padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
      PocketSettingsContent(component = component)
    }
  }
}

@Composable
private fun PocketSettingsBottomBar(
    component: PocketSettingsComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()
  val isNewPocket = state.pocketId == null

  Surface(
      color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
      modifier = modifier.fillMaxWidth(),
  ) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
      Button(
          onClick = { component.onSaveClicked() },
          modifier =
              Modifier.fillMaxWidth()
                  .widthIn(max = 640.dp)
                  .height(56.dp)
                  .testTag("save-pocket-btn"),
          shape = CircleShape,
          colors =
              ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primary,
                  contentColor = MaterialTheme.colorScheme.onPrimary,
              ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
      ) {
        if (state.isSaving) {
          CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = MaterialTheme.colorScheme.onPrimary,
          )
        } else {
          Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              modifier = Modifier.size(22.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
              text =
                  if (isNewPocket) {
                    stringResource(Res.string.save_action)
                  } else {
                    stringResource(Res.string.apply_action)
                  },
              style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}

@Composable
private fun PocketSettingsContent(
    component: PocketSettingsComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()
  Column(
      modifier =
          modifier
              .fillMaxWidth()
              .widthIn(max = 640.dp)
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp),
  ) {
    PocketIdentitySection(component = component, state = state)

    PocketAccentColorPicker(component = component, state = state)

    PocketIconPickerSection(component = component, state = state)

    PocketAssociatedCategoryCard(component = component, state = state)

    PocketMonthlyAllocationCard(component = component, state = state)

    val isNewPocket = state.pocketId == null
    if (!isNewPocket) {
      PocketDangerZoneCard(
          onDeletePocketClick = { component.onDeleteClicked() },
      )
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@ScreenPreviews
@Composable
private fun PocketSettingsScreenNormalPreview() {
  PocketSettingsScreen(
      FakePocketSettingsComponent(
          initialState =
              FakePocketSettingsStateFactory.create(
                  pocketId = 1L,
                  nameText = "Daily Cafe & Coffee",
                  balanceAmount = 850_000,
                  selectedCategory = FakeCategoryFactory.create(name = "Cafe & Dining"),
              )
      )
  )
}

@ScreenPreviews
@Composable
private fun PocketSettingsScreenEmptyPreview() {
  PocketSettingsScreen(
      FakePocketSettingsComponent(initialState = FakePocketSettingsStateFactory.create())
  )
}

@ScreenPreviews
@Composable
private fun PocketSettingsScreenErrorPreview() {
  PocketSettingsScreen(
      FakePocketSettingsComponent(
          initialState =
              FakePocketSettingsStateFactory.create(
                  nameError = UiString("Pocket name cannot be empty")
              )
      )
  )
}

@ScreenPreviews
@Composable
private fun PocketSettingsScreenSavingPreview() {
  PocketSettingsScreen(
      FakePocketSettingsComponent(
          initialState =
              FakePocketSettingsStateFactory.create(
                  nameText = "Groceries",
                  isSaving = true,
              )
      )
  )
}
