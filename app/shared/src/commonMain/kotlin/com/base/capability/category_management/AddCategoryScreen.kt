package com.base.capability.category_management

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.ui.ScreenPreviews
import com.base.ui.components.AccentColorPicker
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.add_category_title
import pockety.app.shared.generated.resources.back_action
import pockety.app.shared.generated.resources.category_icon_label

@Composable
fun AddCategoryScreen(
    component: AddCategoryComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.fillMaxSize().testTag("add-category-screen").navigationBarsPadding(),
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.add_category_title),
            navigationIcon = {
              IconButton(
                  onClick = component::onBackClicked,
                  modifier = Modifier.testTag("back-button"),
              ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.back_action),
                )
              }
            },
        )
      },
  ) { innerPadding ->
    AddCategoryFormContent(
        component = component,
        state = state,
        modifier = Modifier.padding(innerPadding),
    )
  }
}

@Composable
private fun AddCategoryFormContent(
    component: AddCategoryComponent,
    state: AddCategoryState,
    modifier: Modifier = Modifier,
) {
  var selectedFilter by remember { mutableStateOf("All") }
  val focusManager = LocalFocusManager.current

  val iconOption =
      defaultCategoryIcons.find { it.id == state.selectedIcon } ?: defaultCategoryIcons[0]
  val colorOption =
      defaultCategoryColors.find { it.colorHex == state.selectedColor } ?: defaultCategoryColors[0]

  AddCategoryFormBody(
      modifier = modifier,
      previewAndName = {
        CategoryLivePreviewAndNameFields(
            nameData =
                CategoryNameFieldData(
                    name = state.nameText,
                    nameError = state.nameError?.content(),
                    onNameChange = component::onNameChanged,
                    onDone = {
                      focusManager.clearFocus()
                      component.onSaveClicked()
                    },
                ),
            visualSelection = CategoryVisualSelection(iconOption, colorOption),
        )
      },
      colorPicker = {
        AccentColorPicker(
            colors = CategoryColorsWrapper(defaultCategoryColors),
            selectedColor = colorOption,
            onColorSelect = { component.onColorSelected(it.colorHex) },
        )
      },
      iconSection = {
        CategoryIconSection(
            config =
                CategoryIconSectionConfig(
                    icons = CategoryIconsWrapper(defaultCategoryIcons),
                    selectedIcon = iconOption,
                    filters = CategoryFiltersWrapper(defaultIconCategoryFilters),
                    selectedFilter = selectedFilter,
                    accentColor = colorOption.color,
                ),
            onIconSelect = { component.onIconSelected(it.id) },
            onFilterSelect = { selectedFilter = it },
        )
      },
      saveButton = {
        SaveCategoryButton(
            onClick = {
              focusManager.clearFocus()
              component.onSaveClicked()
            },
            isSaving = state.isSaving,
        )
      },
  )
}

@Immutable
private data class CategoryIconSectionConfig(
    val icons: CategoryIconsWrapper,
    val selectedIcon: CategoryIconOption,
    val filters: CategoryFiltersWrapper,
    val selectedFilter: String,
    val accentColor: Color,
)

@Composable
private fun CategoryIconSection(
    config: CategoryIconSectionConfig,
    onIconSelect: (CategoryIconOption) -> Unit,
    onFilterSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
  val displayedIcons =
      if (config.selectedFilter == "All") {
        config.icons.items
      } else {
        val filtered = config.icons.items.filter { it.categoryFilter == config.selectedFilter }
        if (filtered.isEmpty()) config.icons.items else filtered
      }

  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
        text = stringResource(Res.string.category_icon_label),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(start = 4.dp),
    )

    IconFiltersBar(
        filters = config.filters,
        selectedFilter = config.selectedFilter,
        onFilterSelect = onFilterSelect,
    )

    IconGridCard(
        displayedIcons = CategoryIconsWrapper(displayedIcons),
        selectedIcon = config.selectedIcon,
        accentColor = config.accentColor,
        onIconSelect = onIconSelect,
    )
  }
}

@Composable
private fun IconFiltersBar(
    filters: CategoryFiltersWrapper,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    filters.items.forEach { filter ->
      val isSelected = filter == selectedFilter
      Surface(
          shape = CircleShape,
          color =
              if (isSelected) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.surfaceContainerLowest,
          contentColor =
              if (isSelected) MaterialTheme.colorScheme.onPrimary
              else MaterialTheme.colorScheme.onSurfaceVariant,
          shadowElevation = if (isSelected) 2.dp else 1.dp,
          onClick = { onFilterSelect(filter) },
      ) {
        Text(
            text = filter,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
      }
    }
  }
}

private const val ICONS_PER_ROW = 5

@Composable
private fun IconGridCard(
    displayedIcons: CategoryIconsWrapper,
    selectedIcon: CategoryIconOption,
    accentColor: Color,
    onIconSelect: (CategoryIconOption) -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      val rows = displayedIcons.items.chunked(ICONS_PER_ROW)
      rows.forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          row.forEach { iconOption ->
            IconGridTile(
                iconOption = iconOption,
                isSelected = iconOption.id == selectedIcon.id,
                accentColor = accentColor,
                onSelect = { onIconSelect(iconOption) },
                modifier = Modifier.weight(1f),
            )
          }
          if (row.size < ICONS_PER_ROW) {
            repeat(ICONS_PER_ROW - row.size) { Spacer(modifier = Modifier.weight(1f)) }
          }
        }
      }
    }
  }
}

@Composable
private fun IconGridTile(
    iconOption: CategoryIconOption,
    isSelected: Boolean,
    accentColor: Color,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.aspectRatio(1f),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      border =
          if (isSelected) {
            BorderStroke(2.dp, accentColor)
          } else {
            null
          },
      shadowElevation = 1.dp,
      onClick = onSelect,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
          imageVector = iconOption.icon,
          contentDescription = iconOption.label,
          tint =
              if (isSelected) {
                accentColor
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant
              },
          modifier = Modifier.size(24.dp),
      )
    }
  }
}

@ScreenPreviews
@Composable
private fun AddCategoryScreenPreview() {
  AddCategoryScreen(FakeAddCategoryComponent())
}

@ScreenPreviews
@Composable
private fun AddCategoryScreenFilledPreview() {
  AddCategoryScreen(
      FakeAddCategoryComponent(
          initialState =
              AddCategoryState(
                  nameText = "Groceries",
                  selectedColor = 0xFF4A7C59,
                  selectedIcon = "shopping_cart",
              )
      )
  )
}

@ScreenPreviews
@Composable
private fun AddCategoryScreenSavingPreview() {
  AddCategoryScreen(
      FakeAddCategoryComponent(
          initialState =
              AddCategoryState(
                  nameText = "Groceries",
                  selectedColor = 0xFF4A7C59,
                  selectedIcon = "shopping_cart",
                  isSaving = true,
              )
      )
  )
}
