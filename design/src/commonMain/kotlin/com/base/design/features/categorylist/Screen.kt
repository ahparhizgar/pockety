package com.base.design.features.categorylist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.design.features.selectcategory.CategorySearchField
import com.base.design.features.selectcategory.CreateCategoryButton
import com.base.design.globalcomponents.PocketyTopAppBar
import com.base.design.ui.ScreenPreviews
import com.base.design.ui.theme.colorSchemeExtended
import com.base.design.features.selectcategory.CategoryEmptyState as SearchEmptyState

/**
 * Categories management screen, modeled on the Select Category screen: a search field,
 * a bounded centered list of category rows with a delete action per row, and a bottom
 * create button. There is no selection state and no Uncategorized row — this screen
 * manages categories instead of assigning them.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    modifier: Modifier = Modifier,
    initialCategories: List<CategoryItemUiModel> = defaultCategoryList(),
    initialSearchQuery: String = "",
    initialCategoryPendingDeletion: CategoryItemUiModel? = null,
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCategoryClick: (CategoryItemUiModel) -> Unit = {},
) {
  var categoryList by remember(initialCategories) { mutableStateOf(initialCategories) }
  var searchQuery by remember(initialSearchQuery) { mutableStateOf(initialSearchQuery) }
  var categoryPendingDeletion by
      remember(initialCategoryPendingDeletion) { mutableStateOf(initialCategoryPendingDeletion) }
  var showAddCategorySheet by remember { mutableStateOf(false) }

  val isSearchActive = searchQuery.isNotBlank()
  val filteredCategories =
      if (!isSearchActive) {
        categoryList
      } else {
        val query = searchQuery.trim().lowercase()
        categoryList.filter { it.name.lowercase().contains(query) }
      }

  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopAppBar(
            title = "Categories",
            onBackClick = onBackClick,
        )
      },
  ) { innerPadding ->
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
      Column(
          modifier =
              Modifier.fillMaxWidth()
                  .widthIn(max = 640.dp)
                  .padding(horizontal = 20.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        if (categoryList.isNotEmpty()) {
          CategorySearchField(
              query = searchQuery,
              onQueryChange = { searchQuery = it },
          )
        }

        when {
          categoryList.isEmpty() -> {
            CategoryEmptyState(
                modifier = Modifier.weight(1f),
                onAddCategoryClick = { showAddCategorySheet = true },
            )
          }

          filteredCategories.isEmpty() -> {
            SearchEmptyState(modifier = Modifier.weight(1f))
          }

          else -> {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
              items(filteredCategories, key = { it.id }) { category ->
                CategoryManagementRow(
                    category = category,
                    onClick = { onCategoryClick(category) },
                    onDelete = { categoryPendingDeletion = category },
                )
              }
            }
          }
        }

        CreateCategoryButton(onClick = { showAddCategorySheet = true })
      }
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

  categoryPendingDeletion?.let { category ->
    DeleteCategoryDialog(
        category = category,
        onConfirm = {
          categoryList = categoryList.filter { it.id != category.id }
          categoryPendingDeletion = null
        },
        onDismiss = { categoryPendingDeletion = null },
    )
  }
}

@ScreenPreviews
@Composable
fun CategoryListScreenPreview() {
  CategoryListScreen()
}

@ScreenPreviews
@Composable
fun CategoryListEmptyScreenPreview() {
  CategoryListScreen(initialCategories = emptyList())
}

@ScreenPreviews
@Composable
fun CategoryListScreenSearchPreview() {
  CategoryListScreen(initialSearchQuery = "gro")
}

@ScreenPreviews
@Composable
fun CategoryListScreenNoResultsPreview() {
  CategoryListScreen(initialSearchQuery = "unknownxyz")
}

@ScreenPreviews
@Composable
fun CategoryListScreenDeleteConfirmationPreview() {
  val categories = defaultCategoryList()
  CategoryListScreen(
      initialCategories = categories,
      initialCategoryPendingDeletion = categories.first(),
  )
}
