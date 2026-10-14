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

package com.base.design.features

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.LabelOff
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.draw.clip
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

/** Data model representing a category item in the selection sheet. */
data class CategoryItemData(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val iconBackgroundColor: Color,
    val iconContentColor: Color,
    val description: String? = null,
    val isFrequent: Boolean = true,
    val searchKeywords: List<String> = emptyList(),
)

/** Top pill-shaped grab handle for the expressive bottom sheet. */
@Composable
fun CategoryGrabHandle(modifier: Modifier = Modifier) {
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

/** Header row containing the "Select Category" title and optional close action. */
@Composable
fun CategorySheetHeader(
    title: String = "Select Category",
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

/**
 * Search input field for filtering categories.
 *
 * Includes search icon, clear button, keyboard accessibility options, and actions.
 */
@Composable
fun CategorySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search categories...",
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

/**
 * Individual category option row item.
 *
 * Supports selected/active state with badge, icon badge, title, and optional description.
 */
@Composable
fun CategoryOptionItem(
    name: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconContentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    isSelected: Boolean = false,
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
            ) {
              Text(
                  text = badgeText,
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
  }
}

/** Divider labeled with uppercase category section header (e.g., "FREQUENT"). */
@Composable
fun CategorySectionDivider(
    title: String = "Frequent",
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Box(
        modifier =
            Modifier.weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
    )
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
    Box(
        modifier =
            Modifier.weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
    )
  }
}

/** Action button for creating a new custom category. */
@Composable
fun CreateCategoryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Create New Category",
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
          text = text,
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

/** Empty state displayed when category search returns zero results. */
@Composable
fun CategoryEmptyState(
    modifier: Modifier = Modifier,
    title: String = "No categories found",
    message: String = "Try another search term or create a custom category below.",
) {
  Column(
      modifier = modifier.fillMaxWidth().padding(vertical = 24.dp, horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    Surface(
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
      Box(
          modifier = Modifier.size(56.dp),
          contentAlignment = Alignment.Center,
      ) {
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
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 24.dp),
    )
  }
}

/** Mock underlying expense form preview matching the Stitch design context. */
@Composable
fun UnderlyingExpenseFormPreview(modifier: Modifier = Modifier) {
  Column(
      modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
      Column(
          modifier = Modifier.padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
              text = "Expense Name",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
              text = "T 420.00",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold,
          )
        }
        Box(
            modifier =
                Modifier.fillMaxWidth(0.75f)
                    .height(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Box(
              modifier =
                  Modifier.size(38.dp)
                      .clip(CircleShape)
                      .background(MaterialTheme.colorScheme.secondaryContainer),
          )
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier =
                    Modifier.width(90.dp)
                        .height(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer),
            )
            Box(
                modifier =
                    Modifier.width(140.dp)
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            )
          }
        }
      }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
      Column(
          modifier = Modifier.padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Box(
            modifier =
                Modifier.width(100.dp)
                    .height(14.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
        )
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
        )
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
        )
      }
    }
  }
}

// ============================================================================
// Component Previews
// ============================================================================

@ComponentPreviews
@Composable
fun CategoryGrabHandlePreview() {
  Surface(color = MaterialTheme.colorScheme.surface) { CategoryGrabHandle() }
}

@ComponentPreviews
@Composable
fun CategorySheetHeaderPreview() {
  Surface(color = MaterialTheme.colorScheme.surface) { CategorySheetHeader(onCloseClick = {}) }
}

@ComponentPreviews
@Composable
fun CategorySearchFieldEmptyPreview() {
  Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
    CategorySearchField(query = "", onQueryChange = {})
  }
}

@ComponentPreviews
@Composable
fun CategorySearchFieldFilledPreview() {
  Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
    CategorySearchField(query = "Dining", onQueryChange = {})
  }
}

@ComponentPreviews
@Composable
fun CategoryOptionItemSelectedPreview() {
  Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
    CategoryOptionItem(
        name = "Cafe & Dining",
        icon = Icons.Default.Restaurant,
        iconBackgroundColor = MaterialTheme.colorScheme.primary,
        iconContentColor = MaterialTheme.colorScheme.onPrimary,
        isSelected = true,
        onClick = {},
    )
  }
}

@ComponentPreviews
@Composable
fun CategoryOptionItemUnselectedPreview() {
  Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
    CategoryOptionItem(
        name = "Groceries & Supermarket",
        icon = Icons.Default.ShoppingCart,
        iconBackgroundColor = MaterialTheme.colorSchemeExtended.tertiaryFixed,
        iconContentColor = MaterialTheme.colorSchemeExtended.onTertiaryFixed,
        isSelected = false,
        onClick = {},
    )
  }
}

@ComponentPreviews
@Composable
fun CategoryOptionItemUncategorizedPreview() {
  Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
    CategoryOptionItem(
        name = "Uncategorized",
        description = "Clear category assignment",
        icon = Icons.Default.LabelOff,
        iconBackgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        iconContentColor = MaterialTheme.colorScheme.outline,
        isSelected = false,
        onClick = {},
    )
  }
}

@ComponentPreviews
@Composable
fun CategorySectionDividerPreview() {
  Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
    CategorySectionDivider(title = "Frequent")
  }
}

@ComponentPreviews
@Composable
fun CreateCategoryButtonPreview() {
  Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
    CreateCategoryButton(onClick = {})
  }
}

@ComponentPreviews
@Composable
fun CategoryEmptyStatePreview() {
  Surface(color = MaterialTheme.colorScheme.surface) { CategoryEmptyState() }
}
