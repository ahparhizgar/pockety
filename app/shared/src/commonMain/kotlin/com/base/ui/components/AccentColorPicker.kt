package com.base.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.base.feature.category.add.CategoryColorOption
import com.base.feature.category.add.CategoryColorsWrapper
import com.base.feature.category.add.defaultCategoryColors
import com.base.ui.ComponentPreviews
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.category_accent_color_label

@Composable
fun AccentColorPicker(
    colors: CategoryColorsWrapper,
    selectedColor: CategoryColorOption,
    onColorSelect: (CategoryColorOption) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.category_accent_color_label),
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    AccentColorHeader(
        title = title,
        selectedColorName = selectedColor.name,
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
      Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp).horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        colors.items.forEach { option ->
          ColorCircleItem(
              option = option,
              isSelected = option.colorHex == selectedColor.colorHex,
              onSelect = { onColorSelect(option) },
          )
        }
      }
    }
  }
}

@Composable
private fun AccentColorHeader(
    title: String,
    selectedColorName: String,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        text = title,
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
          modifier = Modifier.size(36.dp).testTag("color-swatch-${option.colorHex}"),
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
          modifier = Modifier.size(38.dp).testTag("color-swatch-${option.colorHex}"),
          shape = CircleShape,
          color = option.color,
          shadowElevation = 1.dp,
          onClick = onSelect,
      ) {}
    }
  }
}

@ComponentPreviews
@Composable
private fun AccentColorPickerPreview() {
  AccentColorPicker(
      colors = CategoryColorsWrapper(defaultCategoryColors),
      selectedColor = defaultCategoryColors.first(),
      onColorSelect = {},
  )
}
