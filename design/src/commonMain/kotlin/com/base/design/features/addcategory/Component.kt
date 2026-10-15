package com.base.design.features.addcategory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.design.globalcomponents.AccentColorPicker
import com.base.design.globalcomponents.IconPickerSection
import com.base.design.globalcomponents.ItemColorOption
import com.base.design.globalcomponents.ItemIconOption
import com.base.design.globalcomponents.defaultIconCategoryFilters
import com.base.design.globalcomponents.defaultItemColors
import com.base.design.globalcomponents.defaultItemIcons
import com.base.design.ui.ComponentPreviews

// Typealiases for backwards compatibility within features
typealias CategoryColorOption = ItemColorOption

typealias CategoryIconOption = ItemIconOption

val defaultCategoryColors = defaultItemColors
val defaultCategoryFilters = defaultIconCategoryFilters
val defaultCategoryIcons = defaultItemIcons

/** Hero live preview card showcasing the chosen icon, accent color, and title. */
@Composable
fun CategoryLivePreviewCard(
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

/** Text field for category name input with accessibility and keyboard configuration. */
@Composable
fun CategoryNameInputField(
    name: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Category Name",
    placeholder: String = "e.g. Groceries, Freelance, Rent",
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(
            imeAction = ImeAction.Next,
            capitalization = KeyboardCapitalization.Words,
            keyboardType = KeyboardType.Text,
        ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
    )

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
          Text(
              text = placeholder,
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
          )
        },
        leadingIcon = {
          Icon(
              imageVector = Icons.Default.Label,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(20.dp),
          )
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
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
  }
}

/** Section grouping filter chips and the icon selection grid. */
@Composable
fun CategoryIconSection(
    icons: List<ItemIconOption>,
    selectedIcon: ItemIconOption,
    onIconSelected: (ItemIconOption) -> Unit,
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
) {
  IconPickerSection(
      icons = icons,
      selectedIcon = selectedIcon,
      onIconSelected = onIconSelected,
      filters = filters,
      selectedFilter = selectedFilter,
      onFilterSelected = onFilterSelected,
      modifier = modifier,
      title = "Select Icon",
      accentColor = accentColor,
  )
}

/** Bottom action button for saving the category. */
@Composable
fun SaveCategoryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Save Category",
    enabled: Boolean = true,
) {
  Button(
      onClick = onClick,
      modifier = modifier.fillMaxWidth().height(56.dp),
      shape = CircleShape,
      colors =
          ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary,
          ),
      elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
      enabled = enabled,
  ) {
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
          text = text,
          style = MaterialTheme.typography.labelLarge,
      )
    }
  }
}

// ==================== Component Previews ====================

@ComponentPreviews
@Composable
private fun CategoryLivePreviewCardPreview() {
  CategoryLivePreviewCard(
      categoryName = "Dining Out",
      selectedIcon = defaultItemIcons.first().icon,
      selectedColor = defaultItemColors.first().color,
      onColor = defaultItemColors.first().onColor,
  )
}

@ComponentPreviews
@Composable
private fun CategoryNameInputFieldPreview() {
  CategoryNameInputField(
      name = "Dining Out",
      onNameChange = {},
  )
}

@ComponentPreviews
@Composable
private fun AccentColorPickerPreview() {
  AccentColorPicker(
      colors = defaultCategoryColors,
      selectedColor = defaultCategoryColors.first(),
      onColorSelected = {},
  )
}

@ComponentPreviews
@Composable
private fun CategoryIconSectionPreview() {
  CategoryIconSection(
      icons = defaultCategoryIcons,
      selectedIcon = defaultCategoryIcons.first(),
      onIconSelected = {},
      filters = defaultCategoryFilters,
      selectedFilter = "All",
      onFilterSelected = {},
  )
}

@ComponentPreviews
@Composable
private fun SaveCategoryButtonPreview() {
  SaveCategoryButton(onClick = {})
}
