package com.base.capability.pocket_management

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.base.capability.category_management.CategoryIconsWrapper
import com.base.capability.category_management.defaultCategoryColors
import com.base.capability.category_management.defaultCategoryIcons
import com.base.capability.category_management.defaultIconCategoryFilters
import com.base.capability.category_management.resolveCategoryIcon
import com.base.ui.ComponentPreviews
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.category_accent_color_label
import pockety.app.shared.generated.resources.category_icon_label
import pockety.app.shared.generated.resources.pocket_name_label

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
          contentDescription = "Pocket icon",
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
        modifier = Modifier.fillMaxWidth().testTag("pocket-name-input"),
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

  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
          text = stringResource(Res.string.category_accent_color_label),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = selectedOption.name,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary,
      )
    }

    PocketColorSwatchRow(component = component, state = state)
  }
}

@Composable
private fun PocketColorSwatchRow(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp).horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      defaultCategoryColors.forEach { option ->
        val isSelected = option.colorHex == state.selectedColor
        Surface(
            onClick = { component.onColorSelected(option.colorHex) },
            modifier = Modifier.size(44.dp).testTag("color-swatch-${option.colorHex}"),
            shape = CircleShape,
            color = option.color,
            border =
                if (isSelected) {
                  BorderStroke(3.dp, MaterialTheme.colorScheme.surface)
                } else {
                  null
                },
            shadowElevation = if (isSelected) 4.dp else 1.dp,
        ) {
          if (isSelected) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = option.onColor,
                  modifier = Modifier.size(20.dp),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun PocketIconPickerSection(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  var selectedFilter by remember { mutableStateOf("All") }
  val displayedIcons =
      if (selectedFilter == "All") {
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
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
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
          label = { Text(text = filter) },
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
                    contentDescription = iconOption.label,
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

@ComponentPreviews
@Composable
private fun PocketIdentitySectionPreview() {
  PocketIdentitySection(
      component = FakePocketSettingsComponent(),
      state = FakePocketSettingsStateFactory.create(nameText = "Daily Cafe & Coffee"),
  )
}
