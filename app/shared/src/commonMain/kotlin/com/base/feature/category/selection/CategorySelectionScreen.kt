package com.base.feature.category.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LabelOff
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.feature.category.add.resolveCategoryIcon
import com.base.ui.ScreenPreviews
import com.base.ui.colorSchemeExtended
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.active_badge
import pockety.app.shared.generated.resources.back_action
import pockety.app.shared.generated.resources.categories_title
import pockety.app.shared.generated.resources.category_search_placeholder
import pockety.app.shared.generated.resources.clear_search_action
import pockety.app.shared.generated.resources.create_new_category_action
import pockety.app.shared.generated.resources.no_categories_found
import pockety.app.shared.generated.resources.search_action
import pockety.app.shared.generated.resources.select_category_title
import pockety.app.shared.generated.resources.uncategorized_desc
import pockety.app.shared.generated.resources.uncategorized_title

@Composable
fun CategorySelectionScreen(
    component: CategorySelectionComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.fillMaxSize().testTag("category-selection-screen"),
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.select_category_title),
            navigationIcon = {
              IconButton(
                  onClick = component::onBackClicked,
                  modifier = Modifier.testTag("back-button"),
              ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.back_action),
                )
              }
            },
        )
      },
  ) { innerPadding ->
    CategorySelectionContent(
        component = component,
        state = state,
        modifier = Modifier.padding(innerPadding),
    )
  }
}

@Composable
private fun CategorySelectionContent(
    component: CategorySelectionComponent,
    state: CategorySelectionState,
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.TopCenter,
  ) {
    Column(
        modifier =
            Modifier.widthIn(max = 640.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      CategorySearchField(
          query = state.searchQuery,
          onQueryChange = component::onSearchQueryChanged,
      )

      CategoryOptionsList(
          component = component,
          state = state,
          modifier = Modifier.fillMaxWidth().weight(1f),
      )

      CreateCategoryButton(
          onClick = component::onCreateCategoryClicked,
      )
    }
  }
}

@Composable
private fun CategoryOptionsList(
    component: CategorySelectionComponent,
    state: CategorySelectionState,
    modifier: Modifier = Modifier,
) {
  val showAll = state.searchQuery.isBlank()
  val displayList = if (showAll) state.categories else state.filteredCategories

  LazyColumn(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(bottom = 16.dp),
  ) {
    if (showAll) {
      item(key = "uncategorized") {
        CategoryOptionItem(
            data =
                CategoryOptionData(
                    name = stringResource(Res.string.uncategorized_title),
                    description = stringResource(Res.string.uncategorized_desc),
                    icon = Icons.AutoMirrored.Filled.LabelOff,
                    iconBackgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    iconContentColor = MaterialTheme.colorScheme.outline,
                    isSelected = state.selectedCategoryId == null,
                ),
            onClick = component::onUncategorizedSelected,
            modifier = Modifier.testTag("uncategorized-item"),
        )
      }

      if (displayList.isNotEmpty()) {
        item(key = "divider_all") {
          CategorySectionDivider(title = stringResource(Res.string.categories_title))
        }
      }
    }

    if (!showAll && displayList.isEmpty()) {
      item(key = "empty_state") { CategoryEmptyState() }
    } else {
      items(displayList, key = { it.id }) { category ->
        CategoryOptionItem(
            data =
                CategoryOptionData(
                    name = category.name,
                    icon = resolveCategoryIcon(category.icon),
                    iconBackgroundColor = Color(category.color),
                    iconContentColor = Color.White,
                    isSelected = state.selectedCategoryId == category.id,
                ),
            onClick = { component.onCategorySelected(category) },
            modifier = Modifier.testTag("category-item-${category.name}"),
        )
      }
    }
  }
}

@Composable
private fun CategorySearchField(
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

private data class CategoryOptionData(
    val name: String,
    val icon: ImageVector,
    val iconBackgroundColor: Color,
    val iconContentColor: Color,
    val description: String? = null,
    val isSelected: Boolean = false,
)

@Composable
private fun CategoryOptionItem(
    data: CategoryOptionData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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

@Composable
private fun CategorySectionDivider(
    title: String,
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

@Composable
private fun CreateCategoryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      onClick = onClick,
      modifier = modifier.fillMaxWidth().testTag("create-category-button"),
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

@Composable
private fun CategoryEmptyState(
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

@ScreenPreviews
@Composable
private fun CategorySelectionScreenPreview() {
  CategorySelectionScreen(FakeCategorySelectionComponent())
}

@ScreenPreviews
@Composable
private fun CategorySelectionScreenEmptyPreview() {
  CategorySelectionScreen(
      FakeCategorySelectionComponent(
          initialState = CategorySelectionState(categories = emptyList())
      )
  )
}
