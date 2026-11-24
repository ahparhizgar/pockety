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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.base.ui.colorSchemeExtended
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.active_badge
import pockety.app.shared.generated.resources.category_search_placeholder
import pockety.app.shared.generated.resources.clear_search_action
import pockety.app.shared.generated.resources.create_new_category_action
import pockety.app.shared.generated.resources.no_categories_found
import pockety.app.shared.generated.resources.search_action

/**
 * Shared category list row used by the category selection and management screens. [trailingContent]
 * lets the management screen attach a per-row action (delete) without changing the row's appearance
 * for selection. [leadingContent] renders a leading element (the management screen's drag handle)
 * and receives [leadingModifier] so the caller can attach drag/key handling to it. [isDragging]
 * raises the row while it is being reordered.
 */
@Composable
fun CategoryOptionItem(
    data: CategoryOptionData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable (Modifier) -> Unit)? = null,
    leadingModifier: Modifier = Modifier,
    isDragging: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null,
) {
  val containerColor =
      when {
        data.isSelected -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
        isDragging -> MaterialTheme.colorScheme.surfaceContainerHigh
        else -> MaterialTheme.colorScheme.surfaceContainerLow
      }

  Surface(
      onClick = onClick,
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = containerColor,
      contentColor = MaterialTheme.colorScheme.onSurface,
      shadowElevation = if (isDragging) 8.dp else 0.dp,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      if (leadingContent != null) {
        leadingContent(leadingModifier)
        Spacer(modifier = Modifier.width(2.dp))
      }

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

/** Shared search field used above the category lists on selection and management. */
@Composable
fun CategorySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
  val focusManager = LocalFocusManager.current

  TextField(
      value = query,
      onValueChange = onQueryChange,
      modifier = modifier.fillMaxWidth().testTag("category-search-field"),
      placeholder = {
        Text(
            text = stringResource(Res.string.category_search_placeholder),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )
      },
      leadingIcon = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(Res.string.search_action),
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(22.dp),
        )
      },
      trailingIcon = {
        if (query.isNotEmpty()) {
          IconButton(onClick = { onQueryChange("") }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.clear_search_action),
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp),
            )
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      colors =
          TextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
              unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
              disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent,
              disabledIndicatorColor = Color.Transparent,
              focusedTextColor = MaterialTheme.colorScheme.onSurface,
              unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
          ),
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      keyboardActions =
          KeyboardActions(
              onSearch = { focusManager.clearFocus() },
          ),
  )
}

/**
 * Selection-style create button pinned below the category lists. Callers set their own test tag.
 */
@Composable
fun CreateCategoryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      onClick = onClick,
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainer,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
      Icon(
          imageVector = Icons.Default.AddCircle,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
          text = stringResource(Res.string.create_new_category_action),
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

/** Empty state shown when a category search matches nothing. */
@Composable
fun CategoryNoResultsState(
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.fillMaxWidth().padding(vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    Surface(
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(28.dp),
        )
      }
    }
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = stringResource(Res.string.no_categories_found),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
    )
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
