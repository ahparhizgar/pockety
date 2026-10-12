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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.design.components.PocketyTopBar
import com.base.design.ui.ComponentPreviews
import com.base.design.ui.ScreenPreviews
import com.base.design.ui.theme.colorSchemeExtended

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    modifier: Modifier = Modifier,
    initialCategories: List<CategoryItemUiModel> = defaultCategoryList(),
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCategoryClick: (CategoryItemUiModel) -> Unit = {},
) {
  var categoryList by remember(initialCategories) { mutableStateOf(initialCategories) }
  var showAddCategorySheet by remember { mutableStateOf(false) }

  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopBar(
            title = "Categories",
            showSearch = false,
            showNotifications = false,
            navigationIcon = {
              IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
              }
            },
            onProfileClick = onProfileClick,
        )
      },
      floatingActionButton = {
        FloatingActionButton(
            onClick = { showAddCategorySheet = true },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(8.dp),
        ) {
          Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add Category FAB",
              modifier = Modifier.size(28.dp),
          )
        }
      },
  ) { innerPadding ->
    Surface(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        color = MaterialTheme.colorScheme.surface,
    ) {
      CategoryListContent(
          categories = categoryList,
          onCategoryClick = onCategoryClick,
          onDeleteCategory = { category ->
            categoryList = categoryList.filter { it.id != category.id }
          },
          onAddCategoryClick = { showAddCategorySheet = true },
      )
    }
  }

  if (showAddCategorySheet) {
    val ext = MaterialTheme.colorSchemeExtended
    AddCategoryBottomSheet(
        onDismissRequest = { showAddCategorySheet = false },
        onCategoryCreated = { name, icon ->
          val newCategory =
              CategoryItemUiModel(
                  id = name.lowercase().replace(" ", "-") + "-${categoryList.size}",
                  name = name,
                  icon = icon,
                  iconContainerColor = ext.primaryFixed,
                  iconContentColor = ext.onPrimaryFixed,
                  subtitle = "0 transactions • $0.00",
              )
          categoryList = listOf(newCategory) + categoryList
          showAddCategorySheet = false
        },
    )
  }
}

@Composable
fun CategoryListContent(
    categories: List<CategoryItemUiModel>,
    modifier: Modifier = Modifier,
    onCategoryClick: (CategoryItemUiModel) -> Unit = {},
    onDeleteCategory: ((CategoryItemUiModel) -> Unit)? = null,
    onAddCategoryClick: () -> Unit = {},
) {
  Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.TopCenter,
  ) {
    if (categories.isEmpty()) {
      CategoryEmptyState(
          modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
          onAddCategoryClick = onAddCategoryClick,
      )
    } else {
      LazyColumn(
          modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(
            items = categories,
            key = { it.id },
        ) { category ->
          CategoryListItem(
              category = category,
              onClick = { onCategoryClick(category) },
              onDelete = onDeleteCategory?.let { { it(category) } },
          )
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
      }
    }
  }
}

@ScreenPreviews
@Composable
private fun CategoryListScreenPreview() {
  CategoryListScreen()
}

@ScreenPreviews
@Composable
private fun CategoryListEmptyScreenPreview() {
  CategoryListScreen(initialCategories = emptyList())
}

@ComponentPreviews
@Composable
private fun CategoryListContentPreview() {
  Surface(color = MaterialTheme.colorScheme.surface) {
    CategoryListContent(
        categories = FakeCategoryFactory.createList(),
    )
  }
}
