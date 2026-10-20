package com.base.capability.pocket_management

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.base.capability.category_management.resolveCategoryIcon
import com.base.db.CategoryEntity
import com.base.db.FakeCategoryFactory
import com.base.ui.ComponentPreviews
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.associated_category_subtitle
import pockety.app.shared.generated.resources.associated_category_title
import pockety.app.shared.generated.resources.change_category_action
import pockety.app.shared.generated.resources.choose_category_action
import pockety.app.shared.generated.resources.clear_association_action
import pockety.app.shared.generated.resources.no_category_associated
import pockety.app.shared.generated.resources.pocket_danger_zone_delete_action
import pockety.app.shared.generated.resources.pocket_danger_zone_desc
import pockety.app.shared.generated.resources.pocket_danger_zone_title

@Composable
fun PocketAssociatedCategoryCard(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      PocketCategoryCardHeader(
          hasSelectedCategory = state.selectedCategory != null,
          onSelectCategory = component::onSelectCategoryClicked,
      )

      Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 1.dp,
      ) {
        if (state.selectedCategory != null) {
          PocketSelectedCategoryRow(
              category = state.selectedCategory,
              onClear = component::onCategoryCleared,
          )
        } else {
          Row(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
                text = stringResource(Res.string.no_category_associated),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PocketCategoryCardHeader(
    hasSelectedCategory: Boolean,
    onSelectCategory: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        text = stringResource(Res.string.associated_category_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
    TextButton(
        onClick = onSelectCategory,
        modifier = Modifier.testTag("change-category-btn"),
    ) {
      Text(
          text =
              if (hasSelectedCategory) {
                stringResource(Res.string.change_category_action)
              } else {
                stringResource(Res.string.choose_category_action)
              },
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

@Composable
private fun PocketSelectedCategoryRow(
    category: CategoryEntity,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Surface(
          modifier = Modifier.size(48.dp),
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = resolveCategoryIcon(category.icon),
              contentDescription = null,
              modifier = Modifier.size(24.dp),
          )
        }
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(Res.string.associated_category_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
      }
    }

    IconButton(
        onClick = onClear,
        modifier = Modifier.testTag("clear-category-btn"),
    ) {
      Icon(
          imageVector = Icons.Default.Close,
          contentDescription = stringResource(Res.string.clear_association_action),
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp),
      )
    }
  }
}

@ComponentPreviews
@Composable
private fun PocketAssociatedCategoryCardPreview() {
  PocketAssociatedCategoryCard(
      component = FakePocketSettingsComponent(),
      state =
          FakePocketSettingsStateFactory.create(
              selectedCategory = FakeCategoryFactory.create(name = "Cafe & Dining"),
          ),
  )
}

@Composable
fun PocketDangerZoneCard(
    onDeletePocketClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = stringResource(Res.string.pocket_danger_zone_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.error,
        )
      }

      Text(
          text = stringResource(Res.string.pocket_danger_zone_desc),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(modifier = Modifier.height(4.dp))

      DangerZoneDeleteButton(onClick = onDeletePocketClick)
    }
  }
}

@Composable
private fun DangerZoneDeleteButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  Surface(
      onClick = onClick,
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      contentColor = MaterialTheme.colorScheme.error,
      shadowElevation = 1.dp,
      modifier = modifier.fillMaxWidth().height(48.dp).testTag("delete-pocket-btn"),
  ) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
      Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
          text = stringResource(Res.string.pocket_danger_zone_delete_action),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.error,
      )
    }
  }
}

@ComponentPreviews
@Composable
private fun PocketDangerZoneCardPreview() {
  PocketDangerZoneCard(
      onDeletePocketClick = {},
  )
}
