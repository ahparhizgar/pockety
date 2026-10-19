package com.base.capability.pocket_management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import pockety.app.shared.generated.resources.pocket_settings_title

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

    PocketManualTopUpCard(component = component, state = state)

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
