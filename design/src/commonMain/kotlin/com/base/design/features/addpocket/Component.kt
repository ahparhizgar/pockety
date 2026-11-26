package com.base.design.features.addpocket

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.base.design.features.selectcategory.CategoryOptionItem
import com.base.design.globalcomponents.ItemColorOption
import com.base.design.globalcomponents.defaultItemColors
import com.base.design.globalcomponents.defaultItemIcons

/** A category the new pocket can start from or link to, with its identity to inherit. */
data class PocketCategoryOption(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: ItemColorOption,
)

private fun pocketCategory(id: String, name: String, iconId: String, colorIndex: Int) =
    PocketCategoryOption(
        id = id,
        name = name,
        icon =
            defaultItemIcons.firstOrNull { it.id == iconId }?.icon ?: defaultItemIcons.first().icon,
        color = defaultItemColors[colorIndex % defaultItemColors.size],
    )

val samplePocketCategories =
    listOf(
        pocketCategory("cafe", "Cafe & Dining", "coffee", 0),
        pocketCategory("groceries", "Groceries", "shopping_cart", 12),
        pocketCategory("transport", "Transport", "directions_car", 13),
        pocketCategory("bills", "Bills & Utilities", "bolt", 3),
        pocketCategory("health", "Health", "medication", 15),
        pocketCategory("fun", "Entertainment", "sports_esports", 14),
    )

/** Single-select category row used by the category-first creation mode. */
@Composable
fun AddPocketCategoryRow(
    category: PocketCategoryOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  CategoryOptionItem(
      name = category.name,
      icon = category.icon,
      iconBackgroundColor = category.color.color,
      iconContentColor = category.color.onColor,
      isSelected = isSelected,
      badgeText = null,
      onClick = onClick,
      modifier = modifier,
      trailingContent =
          if (isSelected) {
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

/** Multi-select category row used for linking several categories in manual mode. */
@Composable
fun LinkedCategoryRow(
    category: PocketCategoryOption,
    isLinked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  AddPocketCategoryRow(
      category = category,
      isSelected = isLinked,
      onClick = onClick,
      modifier = modifier,
  )
}

/**
 * The tucked-away entry to manual setup: quiet by design so the category-first flow stays simple,
 * but discoverable for users who want a custom pocket.
 */
@Composable
fun ManualSetupToggle(
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      onClick = onToggle,
      modifier = modifier.fillMaxWidth(),
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
            text = "Manual setup",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "Custom name, icon and multiple categories",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Icon(
          imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(22.dp),
      )
    }
  }
}

/** Small note shown under the identity header when the pocket inherits from a category. */
@Composable
fun InheritedFromCategoryNote(
    categoryName: String,
    modifier: Modifier = Modifier,
) {
  Text(
      text =
          "Linked to \u201C$categoryName\u201D \u2014 name, icon and color follow the category " +
              "until you switch to manual setup.",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = modifier,
  )
}

/** Section title used above the category lists in both modes. */
@Composable
fun AddPocketSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
  Text(
      text = text,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
      color = color,
      modifier = modifier,
  )
}
