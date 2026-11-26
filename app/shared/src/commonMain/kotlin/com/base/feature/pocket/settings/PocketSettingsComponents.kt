package com.base.feature.pocket.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.LabelOff
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.base.feature.category.CategoryOptionData
import com.base.feature.category.CategoryOptionItem
import com.base.feature.category.add.CategoryColorsWrapper
import com.base.feature.category.add.CategoryIconFilter
import com.base.feature.category.add.CategoryIconsWrapper
import com.base.feature.category.add.defaultCategoryColors
import com.base.feature.category.add.defaultCategoryIcons
import com.base.feature.category.add.defaultIconCategoryFilters
import com.base.feature.category.add.resolveCategoryIcon
import com.base.ui.ComponentPreviews
import com.base.ui.RequestFocusOnce
import com.base.ui.components.AccentColorPicker
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.cancel_action
import pockety.app.shared.generated.resources.category_icon_label
import pockety.app.shared.generated.resources.pocket_icon_description
import pockety.app.shared.generated.resources.pocket_linked_categories_subtitle
import pockety.app.shared.generated.resources.pocket_linked_categories_title
import pockety.app.shared.generated.resources.pocket_manual_dialog_confirm
import pockety.app.shared.generated.resources.pocket_manual_dialog_text
import pockety.app.shared.generated.resources.pocket_manual_dialog_title
import pockety.app.shared.generated.resources.pocket_manual_setup_subtitle
import pockety.app.shared.generated.resources.pocket_manual_setup_title
import pockety.app.shared.generated.resources.pocket_name_label
import pockety.app.shared.generated.resources.pocket_no_category

private const val ICONS_PER_ROW = 5

@Composable
fun PocketIdentitySection(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    PocketAvatar(state = state)
    PocketNameField(component = component, state = state)
  }
}

@Composable
private fun PocketAvatar(
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  val icon = resolveCategoryIcon(state.selectedIcon)
  val color = Color(state.selectedColor)

  Surface(
      modifier = modifier.size(96.dp).testTag("pocket-identity-icon"),
      shape = CircleShape,
      color = color,
      shadowElevation = 2.dp,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
          imageVector = icon,
          contentDescription = stringResource(Res.string.pocket_icon_description),
          modifier = Modifier.size(44.dp),
          tint = Color.White,
      )
    }
  }
}

@Composable
private fun PocketNameField(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  val focusManager = LocalFocusManager.current
  val focusRequester = remember { FocusRequester() }
  if (state.pocketId == null && state.manualMode) {
    RequestFocusOnce(focusRequester)
  }

  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Text(
        text = stringResource(Res.string.pocket_name_label),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp),
    )
    OutlinedTextField(
        value = state.nameText,
        onValueChange = component::onNameChanged,
        modifier =
            Modifier.fillMaxWidth().focusRequester(focusRequester).testTag("pocket-name-input"),
        singleLine = true,
        textStyle = MaterialTheme.typography.titleMedium,
        shape = RoundedCornerShape(16.dp),
        isError = state.nameError != null,
        supportingText =
            state.nameError?.let { error ->
              {
                Text(
                    text = error.content(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
              }
            },
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent,
            ),
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
        keyboardActions =
            KeyboardActions(
                onDone = { focusManager.clearFocus() },
            ),
    )
  }
}

@Composable
fun PocketAccentColorPicker(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  val selectedOption =
      defaultCategoryColors.firstOrNull { it.colorHex == state.selectedColor }
          ?: defaultCategoryColors.first()

  AccentColorPicker(
      colors = CategoryColorsWrapper(defaultCategoryColors),
      selectedColor = selectedOption,
      onColorSelect = { component.onColorSelected(it.colorHex) },
      modifier = modifier,
  )
}

@Composable
fun PocketIconPickerSection(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  var selectedFilter by remember { mutableStateOf(CategoryIconFilter.All) }
  val displayedIcons =
      if (selectedFilter == CategoryIconFilter.All) {
        defaultCategoryIcons
      } else {
        defaultCategoryIcons.filter { it.categoryFilter == selectedFilter }
      }

  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text(
        text = stringResource(Res.string.category_icon_label),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(start = 4.dp),
    )

    PocketIconFilterRow(
        selectedFilter = selectedFilter,
        onFilterSelect = { selectedFilter = it },
    )

    PocketIconGrid(
        iconsWrapper = CategoryIconsWrapper(displayedIcons),
        component = component,
        state = state,
    )
  }
}

@Composable
private fun PocketIconFilterRow(
    selectedFilter: CategoryIconFilter,
    onFilterSelect: (CategoryIconFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    defaultIconCategoryFilters.forEach { filter ->
      val isSelected = filter == selectedFilter
      FilterChip(
          selected = isSelected,
          onClick = { onFilterSelect(filter) },
          label = { Text(text = stringResource(filter.label)) },
          colors =
              FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primary,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              ),
          modifier = Modifier.testTag("icon-filter-$filter"),
      )
    }
  }
}

@Composable
private fun PocketIconGrid(
    iconsWrapper: CategoryIconsWrapper,
    component: PocketSettingsComponent,
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      iconsWrapper.items.chunked(ICONS_PER_ROW).forEach { rowIcons ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          rowIcons.forEach { iconOption ->
            val isSelected = iconOption.id == state.selectedIcon
            Surface(
                onClick = { component.onIconSelected(iconOption.id) },
                modifier = Modifier.size(48.dp).testTag("icon-option-${iconOption.id}"),
                shape = CircleShape,
                color =
                    if (isSelected) {
                      Color(state.selectedColor)
                    } else {
                      MaterialTheme.colorScheme.surfaceContainerLowest
                    },
                contentColor =
                    if (isSelected) {
                      Color.White
                    } else {
                      MaterialTheme.colorScheme.onSurfaceVariant
                    },
                shadowElevation = if (isSelected) 2.dp else 0.dp,
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = iconOption.icon,
                    contentDescription = stringResource(iconOption.label),
                    modifier = Modifier.size(24.dp),
                )
              }
            }
          }
          repeat(ICONS_PER_ROW - rowIcons.size) { Spacer(modifier = Modifier.size(48.dp)) }
        }
      }
    }
  }
}

/**
 * Step 1 of creation: the "No Category" escape hatch (goes straight to the advanced form), a
 * divider, then the multi-select category rows.
 */
@Composable
fun PocketCategoryPickList(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    CategoryOptionItem(
        data =
            CategoryOptionData(
                name = stringResource(Res.string.pocket_no_category),
                icon = Icons.AutoMirrored.Filled.LabelOff,
                iconBackgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                iconContentColor = MaterialTheme.colorScheme.outline,
                isSelected = false,
            ),
        onClick = component::onNoCategoryClicked,
        modifier = Modifier.testTag("no-category-row"),
        trailingContent = {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(22.dp),
          )
        },
    )
    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
    state.categories.forEach { category ->
      val isPicked = category.id in state.pickedCategoryIds
      CategoryOptionItem(
          data =
              CategoryOptionData(
                  name = category.name,
                  icon = resolveCategoryIcon(category.icon),
                  iconBackgroundColor = Color(category.color),
                  iconContentColor = Color.White,
                  isSelected = isPicked,
              ),
          onClick = { component.onPickedCategoryToggled(category) },
          modifier = Modifier.testTag("pocket-pick-category-${category.id}"),
          trailingContent =
              if (isPicked) {
                {
                  Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(22.dp),
                  )
                }
              } else {
                null
              },
      )
    }
  }
}

/** The single category a simple-mode pocket follows, shown read-only in the form. */
@Composable
fun PocketSelectedCategoryRow(
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  val category = state.selectedCategory ?: return
  CategoryOptionItem(
      data =
          CategoryOptionData(
              name = category.name,
              icon = resolveCategoryIcon(category.icon),
              iconBackgroundColor = Color(category.color),
              iconContentColor = Color.White,
              isSelected = true,
          ),
      onClick = {},
      modifier = modifier,
  )
}

@Composable
fun PocketLinkedCategoriesSection(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
        text = stringResource(Res.string.pocket_linked_categories_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(start = 4.dp),
    )
    Text(
        text = stringResource(Res.string.pocket_linked_categories_subtitle),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp),
    )
    state.categories.forEach { category ->
      val isLinked = category.id in state.linkedCategoryIds
      CategoryOptionItem(
          data =
              CategoryOptionData(
                  name = category.name,
                  icon = resolveCategoryIcon(category.icon),
                  iconBackgroundColor = Color(category.color),
                  iconContentColor = Color.White,
                  isSelected = isLinked,
              ),
          onClick = { component.onLinkedCategoryToggled(category) },
          modifier = Modifier.testTag("pocket-linked-category-${category.id}"),
          trailingContent =
              if (isLinked) {
                {
                  Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(22.dp),
                  )
                }
              } else {
                null
              },
      )
    }
  }
}

/** The quiet row at the bottom of the simple creation mode that gates manual setup. */
@Composable
fun PocketManualSetupRow(
    component: PocketSettingsComponent,
    modifier: Modifier = Modifier,
) {
  Surface(
      onClick = component::onManualSetupClicked,
      modifier = modifier.fillMaxWidth().testTag("manual-setup-row"),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(20.dp),
      )
      Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(2.dp),
      ) {
        Text(
            text = stringResource(Res.string.pocket_manual_setup_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(Res.string.pocket_manual_setup_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Icon(
          imageVector = Icons.Default.ExpandMore,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(22.dp),
      )
    }
  }
}

@Composable
fun PocketManualConfirmDialog(
    component: PocketSettingsComponent,
    modifier: Modifier = Modifier,
) {
  AlertDialog(
      onDismissRequest = component::onManualConfirmDismissed,
      modifier = modifier.testTag("manual-setup-dialog"),
      title = { Text(text = stringResource(Res.string.pocket_manual_dialog_title)) },
      text = { Text(text = stringResource(Res.string.pocket_manual_dialog_text)) },
      confirmButton = {
        TextButton(onClick = component::onManualConfirmConfirmed) {
          Text(text = stringResource(Res.string.pocket_manual_dialog_confirm))
        }
      },
      dismissButton = {
        TextButton(onClick = component::onManualConfirmDismissed) {
          Text(text = stringResource(Res.string.cancel_action))
        }
      },
  )
}

@ComponentPreviews
@Composable
private fun PocketIdentitySectionPreview() {
  PocketIdentitySection(
      component = FakePocketSettingsComponent(),
      state = FakePocketSettingsStateFactory.create(nameText = "Daily Cafe & Coffee"),
  )
}
