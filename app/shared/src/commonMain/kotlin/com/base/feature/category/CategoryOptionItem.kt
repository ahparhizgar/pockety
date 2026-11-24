package com.base.feature.category

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.base.ui.colorSchemeExtended
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.active_badge

data class CategoryOptionData(
    val name: String,
    val icon: ImageVector,
    val iconBackgroundColor: Color,
    val iconContentColor: Color,
    val description: String? = null,
    val isSelected: Boolean = false,
)

/**
 * Shared category list row used by the category selection and management screens.
 * [trailingContent] lets the management screen attach a per-row action (delete)
 * without changing the row's appearance for selection.
 */
@Composable
fun CategoryOptionItem(
    data: CategoryOptionData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
) {
  val containerColor =
      if (data.isSelected) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
      } else {
        MaterialTheme.colorScheme.surfaceContainerLow
      }

  Surface(
      onClick = onClick,
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = containerColor,
      contentColor = MaterialTheme.colorScheme.onSurface,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
          modifier = Modifier.size(44.dp),
          shape = RoundedCornerShape(14.dp),
          color = data.iconBackgroundColor,
      ) {
        Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center,
        ) {
          Icon(
              imageVector = data.icon,
              contentDescription = null,
              tint = data.iconContentColor,
              modifier = Modifier.size(22.dp),
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      CategoryOptionLabels(
          name = data.name,
          description = data.description,
          isSelected = data.isSelected,
          modifier = Modifier.weight(1f),
      )

      if (trailingContent != null) {
        Spacer(modifier = Modifier.width(8.dp))
        trailingContent()
      }
    }
  }
}

@Composable
private fun CategoryOptionLabels(
    name: String,
    description: String?,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier,
      verticalArrangement = Arrangement.Center,
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
          text = name,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )
      if (isSelected) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorSchemeExtended.primaryFixed,
        ) {
          Text(
              text = stringResource(Res.string.active_badge),
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorSchemeExtended.onPrimaryFixedVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
          )
        }
      }
    }
    if (description != null) {
      Spacer(modifier = Modifier.height(2.dp))
      Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )
    }
  }
}
