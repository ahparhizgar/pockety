package com.base.capability.category_management

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.category_accent_color_label
import pockety.app.shared.generated.resources.category_name_label
import pockety.app.shared.generated.resources.category_name_placeholder
import pockety.app.shared.generated.resources.save_category_action

@Composable
internal fun CategoryLivePreviewCard(
    categoryName: String,
    selectedIcon: ImageVector,
    selectedColor: Color,
    onColor: Color,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      Surface(
          modifier = Modifier.size(80.dp),
          shape = CircleShape,
          color = selectedColor,
          shadowElevation = 4.dp,
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = selectedIcon,
              contentDescription = null,
              tint = onColor,
              modifier = Modifier.size(36.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
          text = "LIVE PREVIEW",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          letterSpacing = 1.2.sp,
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
          text = categoryName.ifBlank { "Dining Out" },
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
internal fun CategoryNameInputField(
    name: String,
    nameError: String?,
    onNameChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
        text = stringResource(Res.string.category_name_label),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
    )

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        modifier = Modifier.fillMaxWidth().testTag("category-name-field"),
        placeholder = {
          Text(
              text = stringResource(Res.string.category_name_placeholder),
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
          )
        },
        leadingIcon = {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.Label,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(20.dp),
          )
        },
        isError = nameError != null,
        supportingText =
            nameError?.let {
              {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
              }
            },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            ),
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
            ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
    )
  }
}

@Composable
internal fun AccentColorPicker(
    colors: CategoryColorsWrapper,
    selectedColor: CategoryColorOption,
    onColorSelect: (CategoryColorOption) -> Unit,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    AccentColorHeader(selectedColorName = selectedColor.name)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
      Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        colors.items.forEach { option ->
          ColorCircleItem(
              option = option,
              isSelected = option.name == selectedColor.name,
              onSelect = { onColorSelect(option) },
          )
        }
      }
    }
  }
}

@Composable
private fun AccentColorHeader(
    selectedColorName: String,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        text = stringResource(Res.string.category_accent_color_label),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
        text = selectedColorName,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.secondary,
    )
  }
}

@Composable
private fun ColorCircleItem(
    option: CategoryColorOption,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier.size(44.dp),
      contentAlignment = Alignment.Center,
  ) {
    if (isSelected) {
      Surface(
          modifier = Modifier.size(44.dp),
          shape = CircleShape,
          color = Color.Transparent,
          border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
      ) {}
      Surface(
          modifier = Modifier.size(36.dp),
          shape = CircleShape,
          color = option.color,
          shadowElevation = 2.dp,
          onClick = onSelect,
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Selected ${option.name}",
              tint = option.onColor,
              modifier = Modifier.size(18.dp),
          )
        }
      }
    } else {
      Surface(
          modifier = Modifier.size(38.dp),
          shape = CircleShape,
          color = option.color,
          shadowElevation = 1.dp,
          onClick = onSelect,
      ) {}
    }
  }
}

@Immutable
internal data class CategoryVisualSelection(
    val iconOption: CategoryIconOption,
    val colorOption: CategoryColorOption,
)

@Immutable
internal data class CategoryNameFieldData(
    val name: String,
    val nameError: String?,
    val onNameChange: (String) -> Unit,
    val onDone: () -> Unit,
)

@Composable
internal fun CategoryLivePreviewAndNameFields(
    nameData: CategoryNameFieldData,
    visualSelection: CategoryVisualSelection,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(20.dp),
  ) {
    CategoryLivePreviewCard(
        categoryName = nameData.name,
        selectedIcon = visualSelection.iconOption.icon,
        selectedColor = visualSelection.colorOption.color,
        onColor = visualSelection.colorOption.onColor,
    )

    CategoryNameInputField(
        name = nameData.name,
        nameError = nameData.nameError,
        onNameChange = nameData.onNameChange,
        onDone = nameData.onDone,
    )
  }
}

@Composable
internal fun SaveCategoryButton(
    onClick: () -> Unit,
    isSaving: Boolean,
    modifier: Modifier = Modifier,
) {
  Button(
      onClick = onClick,
      enabled = !isSaving,
      modifier = modifier.fillMaxWidth().height(56.dp).testTag("save-category-button"),
      shape = CircleShape,
      colors =
          ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
      elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
  ) {
    if (isSaving) {
      CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          color = MaterialTheme.colorScheme.onPrimary,
      )
    } else {
      Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(Res.string.save_category_action),
            style = MaterialTheme.typography.labelLarge,
        )
      }
    }
  }
}

@Composable
internal fun AddCategoryFormBody(
    previewAndName: @Composable () -> Unit,
    colorPicker: @Composable () -> Unit,
    iconSection: @Composable () -> Unit,
    saveButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .widthIn(max = 640.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      previewAndName()
      colorPicker()
      iconSection()
      saveButton()
    }
  }
}
