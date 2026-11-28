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
import com.base.ui.money.localizedNumber
import com.base.ui.components.PocketyTopAppBar
import com.base.usermessage.UiString
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.apply_action
import pockety.app.shared.generated.resources.category_label
import pockety.app.shared.generated.resources.continue_action
import pockety.app.shared.generated.resources.pocket_create_intro
import pockety.app.shared.generated.resources.pocket_inherited_note
import pockety.app.shared.generated.resources.pocket_pick_advanced_hint
import pockety.app.shared.generated.resources.pocket_pick_categories_helper
import pockety.app.shared.generated.resources.pocket_pick_categories_title
import pockety.app.shared.generated.resources.pocket_pick_none_hint
import pockety.app.shared.generated.resources.pocket_pick_simple_hint
import pockety.app.shared.generated.resources.pocket_settings_title
import pockety.app.shared.generated.resources.save_action

@Composable
fun PocketSettingsScreen(
    component: PocketSettingsComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()
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

  if (state.showManualConfirm) {
    PocketManualConfirmDialog(component = component)
  }
}

@Composable
private fun PocketSettingsBottomBar(
    component: PocketSettingsComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()
  val isNewPocket = state.pocketId == null
  val isPickStep = isNewPocket && state.phase == PocketCreationPhase.CategoryPick

  Surface(
      color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
      modifier = modifier.fillMaxWidth(),
  ) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
      Button(
          onClick = {
            if (isPickStep) component.onContinueClicked() else component.onSaveClicked()
          },
          modifier =
              Modifier.fillMaxWidth()
                  .widthIn(max = 640.dp)
                  .height(56.dp)
                  .testTag(if (isPickStep) "continue-btn" else "save-pocket-btn"),
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
                  when {
                    isPickStep -> stringResource(Res.string.continue_action)
                    isNewPocket -> stringResource(Res.string.save_action)
                    else -> stringResource(Res.string.apply_action)
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
    when {
      state.pocketId == null && state.phase == PocketCreationPhase.CategoryPick ->
          CategoryPickContent(component, state)
      state.pocketId == null && !state.manualMode -> SimpleCreationContent(component, state)
      state.pocketId == null -> ManualCreationContent(component, state)
      !state.manualMode -> SimpleEditContent(component, state)
      else -> EditPocketContent(component, state)
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

/** Step 1 of creation: pick zero or more categories; the pick decides the form that follows. */
@Composable
private fun CategoryPickContent(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
) {
  Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
    Text(
        text = stringResource(Res.string.pocket_pick_categories_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )

    Text(
        text = stringResource(Res.string.pocket_pick_categories_helper),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    PocketCategoryPickList(component = component, state = state)

    val pickedCount = state.pickedCategoryIds.size
    Text(
        text =
            when {
              pickedCount == 0 -> stringResource(Res.string.pocket_pick_none_hint)
              pickedCount == 1 ->
                  stringResource(
                      Res.string.pocket_pick_simple_hint,
                      state.categories
                          .firstOrNull { it.id == state.pickedCategoryIds.first() }
                          ?.name
                          .orEmpty(),
                  )
              else -> stringResource(Res.string.pocket_pick_advanced_hint, localizedNumber(pickedCount))
            },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

/** Category-first creation: pick a category and the pocket inherits its identity. */
@Composable
private fun SimpleCreationContent(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
) {

  Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
    Text(
        text = stringResource(Res.string.pocket_create_intro),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    val selectedCategory = state.selectedCategory
    if (selectedCategory != null || state.nameError != null) {
      PocketIdentitySection(component = component, state = state)
      if (selectedCategory != null) {
        Text(
            text = stringResource(Res.string.pocket_inherited_note, selectedCategory.name),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp),
        )
      }
    }

    if (selectedCategory != null) {
      Text(
          text = stringResource(Res.string.category_label),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(start = 4.dp),
      )
      PocketSelectedCategoryRow(state = state)
    }

    PocketMonthlyAllocationCard(component = component, state = state)

    PocketManualSetupRow(component = component)
  }
}

/** The classic form plus multi-category linking, reached through the confirm dialog. */
@Composable
private fun ManualCreationContent(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
) {

  Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
    PocketIdentitySection(component = component, state = state)

    PocketLinkedCategoriesSection(component = component, state = state)

    PocketAccentColorPicker(component = component, state = state)

    PocketIconPickerSection(component = component, state = state)

    PocketMonthlyAllocationCard(component = component, state = state)
  }
}

/** Editing a pocket that still follows its category: the identity stays locked to it. */
@Composable
private fun SimpleEditContent(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
) {

  Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
    PocketIdentitySection(component = component, state = state)

    val selectedCategory = state.selectedCategory
    if (selectedCategory != null) {
      Text(
          text = stringResource(Res.string.pocket_inherited_note, selectedCategory.name),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(start = 4.dp),
      )

      Text(
          text = stringResource(Res.string.category_label),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(start = 4.dp),
      )
      PocketSelectedCategoryRow(state = state)
    }

    PocketMonthlyAllocationCard(component = component, state = state)

    PocketManualSetupRow(component = component)

    PocketDangerZoneCard(
        onDeletePocketClick = { component.onDeleteClicked() },
    )
  }
}

@Composable
private fun EditPocketContent(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
) {

  Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
    PocketIdentitySection(component = component, state = state)

    PocketAccentColorPicker(component = component, state = state)

    PocketIconPickerSection(component = component, state = state)

    if (state.linkedCategoryIds.size > 1) {
      PocketLinkedCategoriesSection(component = component, state = state)
    } else {
      PocketAssociatedCategoryCard(component = component, state = state)
    }

    PocketMonthlyAllocationCard(component = component, state = state)

    PocketDangerZoneCard(
        onDeletePocketClick = { component.onDeleteClicked() },
    )
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
                  manualMode = true,
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
private fun PocketSettingsScreenSimpleSelectedPreview() {
  val categories = FakeCategoryFactory.createList()
  PocketSettingsScreen(
      FakePocketSettingsComponent(
          initialState =
              FakePocketSettingsStateFactory.create(
                  categories = categories,
                  selectedCategory = categories.first(),
                  nameText = categories.first().name,
                  selectedIcon = categories.first().icon,
                  selectedColor = categories.first().color,
              )
      )
  )
}

@ScreenPreviews
@Composable
private fun PocketSettingsScreenPickPreview() {
  val categories = FakeCategoryFactory.createList()
  PocketSettingsScreen(
      FakePocketSettingsComponent(
          initialState =
              FakePocketSettingsStateFactory.create(
                  phase = PocketCreationPhase.CategoryPick,
                  categories = categories,
                  pickedCategoryIds = setOf(categories.first().id),
              )
      )
  )
}

@ScreenPreviews
@Composable
private fun PocketSettingsScreenManualPreview() {
  val categories = FakeCategoryFactory.createList()
  PocketSettingsScreen(
      FakePocketSettingsComponent(
          initialState =
              FakePocketSettingsStateFactory.create(
                  categories = categories,
                  manualMode = true,
                  linkedCategoryIds = setOf(categories[0].id, categories[1].id),
                  nameText = "Travel",
              )
      )
  )
}

@ScreenPreviews
@Composable
private fun PocketSettingsScreenSimpleEditPreview() {
  val category = FakeCategoryFactory.create(name = "Cafe & Dining")
  PocketSettingsScreen(
      FakePocketSettingsComponent(
          initialState =
              FakePocketSettingsStateFactory.create(
                  pocketId = 1L,
                  nameText = category.name,
                  selectedIcon = category.icon,
                  selectedColor = category.color,
                  selectedCategory = category,
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
