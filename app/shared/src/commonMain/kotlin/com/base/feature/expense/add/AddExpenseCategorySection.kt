package com.base.feature.expense.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.base.db.CategoryEntity
import com.base.db.FakeCategoryFactory
import com.base.feature.category.add.resolveCategoryIcon
import com.base.ui.ComponentPreviews
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.assign_category_expense_subtitle
import pockety.app.shared.generated.resources.category_label
import pockety.app.shared.generated.resources.clear_category_action
import pockety.app.shared.generated.resources.tap_to_select_category

@Composable
internal fun CategorySelectorCard(
    component: AddExpenseComponent,
    selectedCategory: CategoryEntity?,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      CategorySelectorHeader()

      CategorySelectionItemSurface(
          selectedCategory = selectedCategory,
          onSelectCategory = component::onSelectCategoryClicked,
          onClearCategory = component::onCategoryCleared,
      )
    }
  }
}

@Composable
private fun CategorySelectorHeader(
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Icon(
          imageVector = Icons.Default.LocalOffer,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp),
      )
      Text(
          text = stringResource(Res.string.category_label),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

@Composable
private fun CategorySelectionItemSurface(
    selectedCategory: CategoryEntity?,
    onSelectCategory: () -> Unit,
    onClearCategory: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val hasCategory = selectedCategory != null

  Surface(
      modifier = modifier.fillMaxWidth().testTag("selected-category-card"),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shadowElevation = 1.dp,
      onClick = onSelectCategory,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        CategoryIconBadge(selectedCategory = selectedCategory)
        CategoryTextDetails(selectedCategory = selectedCategory)
      }

      if (hasCategory) {
        IconButton(
            onClick = onClearCategory,
            modifier = Modifier.testTag("clear-category-btn"),
        ) {
          Icon(
              imageVector = Icons.Default.Close,
              contentDescription = stringResource(Res.string.clear_category_action),
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun CategoryIconBadge(
    selectedCategory: CategoryEntity?,
    modifier: Modifier = Modifier,
) {
  val hasCategory = selectedCategory != null
  Surface(
      modifier = modifier.size(48.dp),
      shape = CircleShape,
      color =
          if (hasCategory) {
            MaterialTheme.colorScheme.secondaryContainer
          } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
          },
      contentColor =
          if (hasCategory) {
            MaterialTheme.colorScheme.onSecondaryContainer
          } else {
            MaterialTheme.colorScheme.onSurfaceVariant
          },
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
          imageVector =
              if (hasCategory) {
                resolveCategoryIcon(selectedCategory.icon)
              } else {
                Icons.Default.Category
              },
          contentDescription = selectedCategory?.name,
          modifier = Modifier.size(24.dp),
      )
    }
  }
}

@Composable
private fun CategoryTextDetails(
    selectedCategory: CategoryEntity?,
    modifier: Modifier = Modifier,
) {
  val hasCategory = selectedCategory != null
  Column(modifier = modifier) {
    Text(
        text = selectedCategory?.name ?: stringResource(Res.string.tap_to_select_category),
        style = MaterialTheme.typography.titleMedium,
        color =
            if (hasCategory) {
              MaterialTheme.colorScheme.onSurface
            } else {
              MaterialTheme.colorScheme.onSurfaceVariant
            },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.testTag("selected-category-name"),
    )
    if (!hasCategory) {
      Text(
          text = stringResource(Res.string.assign_category_expense_subtitle),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@ComponentPreviews
@Composable
private fun CategorySelectorCardSelectedPreview() {
  CategorySelectorCard(
      component = FakeAddExpenseComponent(),
      selectedCategory =
          FakeCategoryFactory.create(
              name = "Groceries",
              icon = "shopping_basket",
          ),
  )
}

@ComponentPreviews
@Composable
private fun CategorySelectorCardUnselectedPreview() {
  CategorySelectorCard(
      component = FakeAddExpenseComponent(),
      selectedCategory = null,
  )
}

@ComponentPreviews
@Composable
private fun CategorySelectionItemSurfaceSelectedPreview() {
  CategorySelectionItemSurface(
      selectedCategory =
          FakeCategoryFactory.create(
              name = "Cafe & Dining",
              icon = "restaurant",
          ),
      onSelectCategory = {},
      onClearCategory = {},
  )
}

@ComponentPreviews
@Composable
private fun CategorySelectionItemSurfaceUnselectedPreview() {
  CategorySelectionItemSurface(
      selectedCategory = null,
      onSelectCategory = {},
      onClearCategory = {},
  )
}
