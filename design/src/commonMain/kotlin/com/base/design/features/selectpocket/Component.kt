package com.base.design.features.selectpocket

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
import androidx.compose.material.icons.automirrored.filled.LabelOff
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.design.ui.ComponentPreviews
import com.base.design.ui.theme.colorSchemeExtended

/** Data model representing a budget pocket item in the selection sheet. */
data class PocketItemData(
    val id: String,
    val name: String,
    val categoryTag: String,
    val balanceFormatted: String,
    val icon: ImageVector,
    val iconBackgroundColor: Color,
    val iconContentColor: Color,
    val isFrequent: Boolean = true,
    val isOverBudget: Boolean = false,
    val searchKeywords: List<String> = emptyList(),
)

/** Top pill-shaped grab handle for bottom sheet rendering. */
@Composable
fun PocketGrabHandle(modifier: Modifier = Modifier) {
  Box(
      modifier = modifier.fillMaxWidth().padding(top = 12.dp, bottom = 6.dp),
      contentAlignment = Alignment.Center,
  ) {
    Surface(
        modifier = Modifier.width(40.dp).height(5.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
    ) {}
  }
}

/** Header row containing title and optional close action. */
@Composable
fun PocketSheetHeader(
    title: String = "Select Pocket",
    modifier: Modifier = Modifier,
    onCloseClick: (() -> Unit)? = null,
) {
  Row(
      modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )
    if (onCloseClick != null) {
      IconButton(onClick = onCloseClick) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close sheet",
            tint = MaterialTheme.colorScheme.outline,
        )
      }
    }
  }
}

/** Search input field for filtering pockets. */
@Composable
fun PocketSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search pockets...",
    onSearch: () -> Unit = {},
) {
  val focusManager = LocalFocusManager.current

  TextField(
      value = query,
      onValueChange = onQueryChange,
      modifier = modifier.fillMaxWidth(),
      placeholder = {
        Text(
            text = placeholder,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )
      },
      leadingIcon = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(22.dp),
        )
      },
      trailingIcon = {
        if (query.isNotEmpty()) {
          IconButton(onClick = { onQueryChange("") }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear search",
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
              onSearch = {
                focusManager.clearFocus()
                onSearch()
              },
          ),
  )
}

/** Individual pocket option row item with category badge, balance, and active indicator. */
@Composable
fun PocketOptionItem(
    name: String,
    categoryTag: String,
    balanceFormatted: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconContentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isOverBudget: Boolean = false,
    badgeText: String? = if (isSelected) "Active" else null,
) {
  val containerColor =
      if (isSelected) {
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
        modifier = Modifier.fillMaxWidth().padding(all = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
          modifier = Modifier.size(44.dp),
          shape = RoundedCornerShape(14.dp),
          color = iconBackgroundColor,
      ) {
        Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center,
        ) {
          Icon(
              imageVector = icon,
              contentDescription = null,
              tint = iconContentColor,
              modifier = Modifier.size(22.dp),
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(
          modifier = Modifier.weight(1f),
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
          if (badgeText != null) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorSchemeExtended.primaryFixed,
                contentColor = MaterialTheme.colorSchemeExtended.onPrimaryFixed,
            ) {
              Text(
                  text = badgeText,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          if (categoryTag.isNotEmpty()) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
              Text(
                  text = categoryTag,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
              )
            }
          }

          if (balanceFormatted.isNotEmpty()) {
            Text(
                text = "• $balanceFormatted",
                style = MaterialTheme.typography.bodySmall,
                color =
                    if (isOverBudget) {
                      MaterialTheme.colorScheme.error
                    } else {
                      MaterialTheme.colorScheme.onSurfaceVariant
                    },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
          }
        }
      }

      if (isSelected) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(24.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                modifier = Modifier.size(16.dp),
            )
          }
        }
      }
    }
  }
}

/** Section divider with subtle text heading. */
@Composable
fun PocketSectionDivider(
    title: String,
    modifier: Modifier = Modifier,
) {
  Text(
      text = title.uppercase(),
      style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.outline,
      modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
  )
}

/** Empty state when no pockets match the search query. */
@Composable
fun PocketEmptyState(
    modifier: Modifier = Modifier,
    message: String = "No pockets found matching your search.",
) {
  Column(
      modifier = modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    Surface(
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
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
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
  }
}

/** Action button at the bottom to create a new budget pocket. */
@Composable
fun CreatePocketButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Create New Pocket",
) {
  Surface(
      onClick = onClick,
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      contentColor = MaterialTheme.colorScheme.primary,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(all = 16.dp),
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
          text = label,
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

// ============================================================================
// Component Previews
// ============================================================================

@ComponentPreviews
@Composable
private fun PocketOptionItemPreview() {
  Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    PocketOptionItem(
        name = "Daily Cafe & Coffee",
        categoryTag = "Cafe & Dining",
        balanceFormatted = "400,000 T",
        icon = Icons.Default.LocalCafe,
        iconBackgroundColor = MaterialTheme.colorScheme.primary,
        iconContentColor = MaterialTheme.colorScheme.onPrimary,
        isSelected = true,
        onClick = {},
    )
    PocketOptionItem(
        name = "Groceries & Household",
        categoryTag = "Groceries",
        balanceFormatted = "850,000 T",
        icon = Icons.Default.ShoppingBag,
        iconBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        isSelected = false,
        onClick = {},
    )
    PocketOptionItem(
        name = "No Pocket",
        categoryTag = "",
        balanceFormatted = "Clear pocket assignment",
        icon = Icons.AutoMirrored.Filled.LabelOff,
        iconBackgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        iconContentColor = MaterialTheme.colorScheme.outline,
        isSelected = false,
        onClick = {},
    )
  }
}

@ComponentPreviews
@Composable
private fun PocketSearchFieldPreview() {
  PocketSearchField(
      query = "Coffee",
      onQueryChange = {},
      modifier = Modifier.padding(16.dp),
  )
}

@ComponentPreviews
@Composable
private fun CreatePocketButtonPreview() {
  CreatePocketButton(
      onClick = {},
      modifier = Modifier.padding(16.dp),
  )
}

@ComponentPreviews
@Composable
private fun PocketEmptyStatePreview() {
  PocketEmptyState()
}
