package com.base.feature.pocket.settings

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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.base.db.CategoryEntity
import com.base.db.FakeCategoryFactory
import com.base.feature.category.add.resolveCategoryIcon
import com.base.ui.ComponentPreviews
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.associated_category_subtitle
import pockety.app.shared.generated.resources.associated_category_title
import pockety.app.shared.generated.resources.associated_category_unlinked_subtitle
import pockety.app.shared.generated.resources.clear_association_action
import pockety.app.shared.generated.resources.pocket_danger_zone_delete_action
import pockety.app.shared.generated.resources.pocket_danger_zone_desc
import pockety.app.shared.generated.resources.pocket_danger_zone_title
import pockety.app.shared.generated.resources.tap_to_select_category

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
      PocketAssociatedCategoryHeader()

      PocketAssociatedCategoryItemSurface(
          category = state.selectedCategory,
          onSelectCategory = component::onSelectCategoryClicked,
          onClearCategory = component::onCategoryCleared,
      )
    }
  }
}

@Composable
private fun PocketAssociatedCategoryHeader(
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
          text = stringResource(Res.string.associated_category_title),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

@Composable
private fun PocketAssociatedCategoryItemSurface(
    category: CategoryEntity?,
    onSelectCategory: () -> Unit,
    onClearCategory: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val hasCategory = category != null

  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shadowElevation = 1.dp,
      onClick = onSelectCategory,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        PocketCategoryIconBadge(category = category)
        PocketCategoryTextDetails(category = category)
      }

      if (hasCategory) {
        IconButton(
            onClick = onClearCategory,
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
  }
}

@Composable
private fun PocketCategoryIconBadge(
    category: CategoryEntity?,
    modifier: Modifier = Modifier,
) {
  val hasCategory = category != null
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
                resolveCategoryIcon(category.icon)
              } else {
                Icons.Default.Category
              },
          contentDescription = null,
          modifier = Modifier.size(24.dp),
      )
    }
  }
}

@Composable
private fun PocketCategoryTextDetails(
    category: CategoryEntity?,
    modifier: Modifier = Modifier,
) {
  val hasCategory = category != null
  Column(modifier = modifier) {
    Text(
        text = category?.name ?: stringResource(Res.string.tap_to_select_category),
        style = MaterialTheme.typography.titleMedium,
        color =
            if (hasCategory) {
              MaterialTheme.colorScheme.onSurface
            } else {
              MaterialTheme.colorScheme.onSurfaceVariant
            },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    Text(
        text =
            if (hasCategory) {
              stringResource(Res.string.associated_category_subtitle)
            } else {
              stringResource(Res.string.associated_category_unlinked_subtitle)
            },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
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

@ComponentPreviews
@Composable
private fun PocketAssociatedCategoryCardUnlinkedPreview() {
  PocketAssociatedCategoryCard(
      component = FakePocketSettingsComponent(),
      state =
          FakePocketSettingsStateFactory.create(
              selectedCategory = null,
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
