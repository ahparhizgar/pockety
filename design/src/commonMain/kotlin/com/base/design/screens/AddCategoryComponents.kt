@file:Suppress(
    "LongMethod",
    "LongParameterList",
    "TooManyFunctions",
    "MagicNumber",
    "MatchingDeclarationName",
    "SpreadOperator",
    "MaxLineLength",
    "UnusedPrivateFunction",
    "ComposableParamOrder",
    "PreviewNaming",
    "PreviewPublic",
    "ParameterNaming",
    "UnstableCollections",
)

package com.base.design.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.base.design.ui.ComponentPreviews

/** Color option model for categories. */
data class CategoryColorOption(
    val name: String,
    val color: Color,
    val onColor: Color = Color.White,
)

/** Icon option model for categories with category filtering support. */
data class CategoryIconOption(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val categoryFilter: String = "All",
)

val defaultCategoryColors =
    listOf(
        CategoryColorOption(
            name = "Forest Green",
            color = Color(0xFF316342),
            onColor = Color.White,
        ),
        CategoryColorOption(
            name = "Emerald",
            color = Color(0xFF4A7C59),
            onColor = Color.White,
        ),
        CategoryColorOption(
            name = "Sage",
            color = Color(0xFF9DD3AA),
            onColor = Color(0xFF00210E),
        ),
        CategoryColorOption(
            name = "Warm Amber",
            color = Color(0xFFB87D4B),
            onColor = Color.White,
        ),
        CategoryColorOption(
            name = "Deep Teal",
            color = Color(0xFF324C41),
            onColor = Color.White,
        ),
        CategoryColorOption(
            name = "Slate Muted",
            color = Color(0xFF4A6458),
            onColor = Color.White,
        ),
    )

val defaultCategoryFilters = listOf("All", "Food", "Shopping", "Transport", "Utilities")

val defaultCategoryIcons =
    listOf(
        CategoryIconOption(
            id = "restaurant",
            label = "Dining",
            icon = Icons.Default.Restaurant,
            categoryFilter = "Food",
        ),
        CategoryIconOption(
            id = "shopping_cart",
            label = "Shopping",
            icon = Icons.Default.ShoppingCart,
            categoryFilter = "Shopping",
        ),
        CategoryIconOption(
            id = "directions_car",
            label = "Transport",
            icon = Icons.Default.DirectionsCar,
            categoryFilter = "Transport",
        ),
        CategoryIconOption(
            id = "home",
            label = "Housing",
            icon = Icons.Default.Home,
            categoryFilter = "Utilities",
        ),
        CategoryIconOption(
            id = "bolt",
            label = "Utilities",
            icon = Icons.Default.Bolt,
            categoryFilter = "Utilities",
        ),
        CategoryIconOption(
            id = "fitness_center",
            label = "Fitness",
            icon = Icons.Default.FitnessCenter,
            categoryFilter = "All",
        ),
        CategoryIconOption(
            id = "flight",
            label = "Travel",
            icon = Icons.Default.Flight,
            categoryFilter = "Transport",
        ),
        CategoryIconOption(
            id = "medical_services",
            label = "Health",
            icon = Icons.Default.MedicalServices,
            categoryFilter = "All",
        ),
        CategoryIconOption(
            id = "pets",
            label = "Pets",
            icon = Icons.Default.Pets,
            categoryFilter = "All",
        ),
        CategoryIconOption(
            id = "payments",
            label = "Income",
            icon = Icons.Default.Payments,
            categoryFilter = "All",
        ),
    )

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

/** Accent color picker displaying a row of circular color swatches with check marks. */
@Composable
fun AccentColorPicker(
    colors: List<CategoryColorOption>,
    selectedColor: CategoryColorOption,
    onColorSelected: (CategoryColorOption) -> Unit,
    modifier: Modifier = Modifier,
) {
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
          text = "Accent Color",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = selectedColor.name,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary,
      )
    }

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
        colors.forEach { option ->
          val isSelected = option.name == selectedColor.name
          Box(
              modifier = Modifier.size(44.dp),
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
                  onClick = { onColorSelected(option) },
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
                  onClick = { onColorSelected(option) },
              ) {}
            }
          }
        }
      }
    }
  }
}

/** Filter chips for category icon categories (All, Food, Shopping, Transport, Utilities). */
@Composable
fun CategoryIconFilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    filters.forEach { filter ->
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
          onClick = { onFilterSelected(filter) },
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

/** 5-column grid of icon options with selection rings. */
@Composable
fun CategoryIconGrid(
    icons: List<CategoryIconOption>,
    selectedIcon: CategoryIconOption,
    onIconSelected: (CategoryIconOption) -> Unit,
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
      val rows = icons.chunked(5)
      rows.forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          row.forEach { iconOption ->
            val isSelected = iconOption.id == selectedIcon.id
            Surface(
                modifier = Modifier.weight(1f).aspectRatio(1f),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                border =
                    if (isSelected) {
                      BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else {
                      null
                    },
                shadowElevation = 1.dp,
                onClick = { onIconSelected(iconOption) },
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = iconOption.icon,
                    contentDescription = iconOption.label,
                    tint =
                        if (isSelected) {
                          MaterialTheme.colorScheme.primary
                        } else {
                          MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    modifier = Modifier.size(24.dp),
                )
              }
            }
          }
          // Pad remaining slots if row has fewer than 5 items
          if (row.size < 5) {
            repeat(5 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
          }
        }
      }
    }
  }
}

/** Section grouping filter chips and the 5-column icon selection grid. */
@Composable
fun CategoryIconSection(
    icons: List<CategoryIconOption>,
    selectedIcon: CategoryIconOption,
    onIconSelected: (CategoryIconOption) -> Unit,
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
  val displayedIcons =
      if (selectedFilter == "All") {
        icons
      } else {
        val filtered = icons.filter { it.categoryFilter == selectedFilter }
        if (filtered.isEmpty()) icons else filtered
      }

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
          text = "Select Icon",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = selectedIcon.label,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary,
      )
    }

    CategoryIconFilterChips(
        filters = filters,
        selectedFilter = selectedFilter,
        onFilterSelected = onFilterSelected,
    )

    CategoryIconGrid(
        icons = displayedIcons,
        selectedIcon = selectedIcon,
        onIconSelected = onIconSelected,
    )
  }
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
      selectedIcon = Icons.Default.Restaurant,
      selectedColor = Color(0xFF316342),
      onColor = Color.White,
  )
}

@ComponentPreviews
@Composable
private fun CategoryLivePreviewCardCustomPreview() {
  CategoryLivePreviewCard(
      categoryName = "Freelance Income",
      selectedIcon = Icons.Default.Payments,
      selectedColor = Color(0xFFB87D4B),
      onColor = Color.White,
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
private fun CategoryIconFilterChipsPreview() {
  CategoryIconFilterChips(
      filters = defaultCategoryFilters,
      selectedFilter = "All",
      onFilterSelected = {},
  )
}

@ComponentPreviews
@Composable
private fun CategoryIconGridPreview() {
  CategoryIconGrid(
      icons = defaultCategoryIcons,
      selectedIcon = defaultCategoryIcons.first(),
      onIconSelected = {},
  )
}

@ComponentPreviews
@Composable
private fun SaveCategoryButtonPreview() {
  SaveCategoryButton(onClick = {})
}
