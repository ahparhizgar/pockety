package com.base.design.features.selectcategory

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LabelOff
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
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
import com.base.design.globalcomponents.PocketyTopAppBar
import com.base.design.ui.ScreenPreviews
import com.base.design.ui.theme.colorSchemeExtended

/** Creates and remembers the default list of category items based on the Pockety design system. */
@Composable
fun rememberDefaultCategories(): List<CategoryItemData> {
  val extended = MaterialTheme.colorSchemeExtended
  val colors = MaterialTheme.colorScheme

  return remember(extended, colors) {
    listOf(
        CategoryItemData(
            id = "uncategorized",
            name = "Uncategorized",
            description = "Clear category assignment",
            icon = Icons.AutoMirrored.Filled.LabelOff,
            iconBackgroundColor = colors.surfaceContainerHighest,
            iconContentColor = colors.outline,
            isFrequent = false,
            searchKeywords = listOf("uncategorized", "none", "clear", "reset"),
        ),
        CategoryItemData(
            id = "dining",
            name = "Cafe & Dining",
            icon = Icons.Default.Restaurant,
            iconBackgroundColor = colors.primary,
            iconContentColor = colors.onPrimary,
            isFrequent = true,
            searchKeywords = listOf("cafe", "dining", "food", "restaurant", "coffee", "drinks"),
        ),
        CategoryItemData(
            id = "groceries",
            name = "Groceries & Supermarket",
            icon = Icons.Default.ShoppingCart,
            iconBackgroundColor = extended.tertiaryFixed,
            iconContentColor = extended.onTertiaryFixed,
            isFrequent = true,
            searchKeywords = listOf("groceries", "supermarket", "market", "shopping", "food"),
        ),
        CategoryItemData(
            id = "transport",
            name = "Transportation & Fuel",
            icon = Icons.Default.DirectionsCar,
            iconBackgroundColor = extended.secondaryFixed,
            iconContentColor = extended.onSecondaryFixed,
            isFrequent = true,
            searchKeywords =
                listOf("transportation", "fuel", "car", "transit", "gas", "metro", "taxi"),
        ),
        CategoryItemData(
            id = "bills",
            name = "Bills & Utilities",
            icon = Icons.Default.Bolt,
            iconBackgroundColor = extended.primaryFixed,
            iconContentColor = extended.onPrimaryFixed,
            isFrequent = true,
            searchKeywords =
                listOf("bills", "utilities", "electricity", "water", "internet", "phone"),
        ),
        CategoryItemData(
            id = "leisure",
            name = "Entertainment & Leisure",
            icon = Icons.Default.SportsEsports,
            iconBackgroundColor = colors.secondaryContainer,
            iconContentColor = colors.onSecondaryContainer,
            isFrequent = true,
            searchKeywords =
                listOf("entertainment", "leisure", "movies", "games", "events", "streaming", "fun"),
        ),
        CategoryItemData(
            id = "shopping",
            name = "Shopping & Clothes",
            icon = Icons.Default.ShoppingBag,
            iconBackgroundColor = colors.tertiaryContainer,
            iconContentColor = colors.onTertiaryContainer,
            isFrequent = true,
            searchKeywords = listOf("shopping", "clothes", "apparel", "shoes", "gadgets", "items"),
        ),
        CategoryItemData(
            id = "health",
            name = "Health & Pharmacy",
            icon = Icons.Default.HealthAndSafety,
            iconBackgroundColor = colors.surfaceContainerHighest,
            iconContentColor = colors.secondary,
            isFrequent = false,
            searchKeywords = listOf("health", "pharmacy", "medical", "doctor", "medicine", "pills"),
        ),
        CategoryItemData(
            id = "education",
            name = "Education & Books",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            iconBackgroundColor = colors.surfaceContainerHigh,
            iconContentColor = colors.tertiary,
            isFrequent = false,
            searchKeywords =
                listOf("education", "books", "school", "courses", "tuition", "study", "reading"),
        ),
        CategoryItemData(
            id = "care",
            name = "Personal Care",
            icon = Icons.Default.AutoAwesome,
            iconBackgroundColor = extended.secondaryFixedDim,
            iconContentColor = extended.onSecondaryFixedVariant,
            isFrequent = false,
            searchKeywords =
                listOf("personal care", "barber", "salon", "grooming", "beauty", "spa"),
        ),
    )
  }
}

/**
 * Reusable modal sheet content for selecting a category.
 *
 * Can be embedded directly inside an expressive BottomSheet or rendered standalone.
 */
@Composable
fun SelectCategorySheetContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategoryId: String?,
    categories: List<CategoryItemData>,
    onCategorySelected: (CategoryItemData) -> Unit,
    modifier: Modifier = Modifier,
    onCreateCategoryClick: () -> Unit = {},
    onCloseClick: (() -> Unit)? = null,
) {
  val uncategorizedItem = categories.firstOrNull { it.id == "uncategorized" }
  val frequentItems = categories.filter { it.isFrequent && it.id != "uncategorized" }
  val otherItems = categories.filter { !it.isFrequent && it.id != "uncategorized" }

  val isSearchActive = searchQuery.isNotBlank()
  val filteredItems =
      remember(searchQuery, categories) {
        if (!isSearchActive) {
          emptyList()
        } else {
          val query = searchQuery.trim().lowercase()
          categories.filter { item ->
            item.name.lowercase().contains(query) ||
                item.description?.lowercase()?.contains(query) == true ||
                item.searchKeywords.any { it.lowercase().contains(query) }
          }
        }
      }

  Column(
      modifier = modifier.fillMaxWidth().padding(bottom = 20.dp),
  ) {
    CategoryGrabHandle()

    CategorySheetHeader(
        title = "Select Category",
        onCloseClick = onCloseClick,
    )

    CategorySearchField(
        query = searchQuery,
        onQueryChange = onSearchQueryChange,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp),
    ) {
      if (!isSearchActive) {
        // Option: Uncategorized / Clear assignment
        if (uncategorizedItem != null) {
          item(key = uncategorizedItem.id) {
            CategoryOptionItem(
                name = uncategorizedItem.name,
                description = uncategorizedItem.description,
                icon = uncategorizedItem.icon,
                iconBackgroundColor = uncategorizedItem.iconBackgroundColor,
                iconContentColor = uncategorizedItem.iconContentColor,
                isSelected = selectedCategoryId == uncategorizedItem.id,
                onClick = { onCategorySelected(uncategorizedItem) },
            )
          }
        }

        // Section Divider: Frequent
        item(key = "divider_frequent") { CategorySectionDivider(title = "Frequent") }

        // Frequent categories
        items(frequentItems, key = { it.id }) { item ->
          CategoryOptionItem(
              name = item.name,
              description = item.description,
              icon = item.icon,
              iconBackgroundColor = item.iconBackgroundColor,
              iconContentColor = item.iconContentColor,
              isSelected = selectedCategoryId == item.id,
              onClick = { onCategorySelected(item) },
          )
        }

        // Section Divider: All Categories (if others exist)
        if (otherItems.isNotEmpty()) {
          item(key = "divider_all") { CategorySectionDivider(title = "All Categories") }

          items(otherItems, key = { it.id }) { item ->
            CategoryOptionItem(
                name = item.name,
                description = item.description,
                icon = item.icon,
                iconBackgroundColor = item.iconBackgroundColor,
                iconContentColor = item.iconContentColor,
                isSelected = selectedCategoryId == item.id,
                onClick = { onCategorySelected(item) },
            )
          }
        }

        // Bottom Action: Create New Category
        item(key = "action_create") {
          CreateCategoryButton(
              onClick = onCreateCategoryClick,
              modifier = Modifier.padding(top = 4.dp),
          )
        }
      } else {
        // Search Results State
        if (filteredItems.isEmpty()) {
          item(key = "search_empty") { CategoryEmptyState() }
        } else {
          items(filteredItems, key = { it.id }) { item ->
            CategoryOptionItem(
                name = item.name,
                description = item.description,
                icon = item.icon,
                iconBackgroundColor = item.iconBackgroundColor,
                iconContentColor = item.iconContentColor,
                isSelected = selectedCategoryId == item.id,
                onClick = { onCategorySelected(item) },
            )
          }
        }

        // Bottom Action: Create New Category (always accessible)
        item(key = "action_create_search") {
          CreateCategoryButton(
              onClick = onCreateCategoryClick,
              modifier = Modifier.padding(top = 4.dp),
          )
        }
      }
    }
  }
}

/**
 * Screen 9: SelectCategoryScreen
 *
 * Full screen representation featuring:
 * - TopAppBar with back navigation and clear "Select Category" title
 * - Live category search field
 * - Frequent and full category list with option to clear or create category
 * - Support for large screens by centering with max 640.dp width
 */
@Composable
fun SelectCategoryScreen(
    modifier: Modifier = Modifier,
    initialSelectedCategoryId: String = "dining",
    initialSearchQuery: String = "",
    categories: List<CategoryItemData> = rememberDefaultCategories(),
    onCategorySelected: (CategoryItemData) -> Unit = {},
    onCreateCategoryClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    onDismissRequest: () -> Unit = onBackClick ?: {},
) {
  var searchQuery by remember { mutableStateOf(initialSearchQuery) }
  var selectedCategoryId by remember { mutableStateOf<String?>(initialSelectedCategoryId) }

  val effectiveBackClick = onBackClick ?: onDismissRequest

  val uncategorizedItem = categories.firstOrNull { it.id == "uncategorized" }
  val frequentItems = categories.filter { it.isFrequent && it.id != "uncategorized" }
  val otherItems = categories.filter { !it.isFrequent && it.id != "uncategorized" }

  val isSearchActive = searchQuery.isNotBlank()
  val filteredItems =
      remember(searchQuery, categories) {
        if (!isSearchActive) {
          emptyList()
        } else {
          val query = searchQuery.trim().lowercase()
          categories.filter { item ->
            item.name.lowercase().contains(query) ||
                item.description?.lowercase()?.contains(query) == true ||
                item.searchKeywords.any { it.lowercase().contains(query) }
          }
        }
      }

  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopAppBar(
            title = "Select Category",
            onBackClick = effectiveBackClick,
        )
      },
  ) { paddingValues ->
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
          modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).padding(horizontal = 20.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
      ) {
        item(key = "search_field") {
          CategorySearchField(
              query = searchQuery,
              onQueryChange = { searchQuery = it },
              modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
          )
        }

        if (!isSearchActive) {
          if (uncategorizedItem != null) {
            item(key = uncategorizedItem.id) {
              CategoryOptionItem(
                  name = uncategorizedItem.name,
                  description = uncategorizedItem.description,
                  icon = uncategorizedItem.icon,
                  iconBackgroundColor = uncategorizedItem.iconBackgroundColor,
                  iconContentColor = uncategorizedItem.iconContentColor,
                  isSelected = selectedCategoryId == uncategorizedItem.id,
                  onClick = {
                    selectedCategoryId = uncategorizedItem.id
                    onCategorySelected(uncategorizedItem)
                  },
              )
            }
          }

          item(key = "divider_frequent") { CategorySectionDivider(title = "Frequent") }

          items(frequentItems, key = { it.id }) { item ->
            CategoryOptionItem(
                name = item.name,
                description = item.description,
                icon = item.icon,
                iconBackgroundColor = item.iconBackgroundColor,
                iconContentColor = item.iconContentColor,
                isSelected = selectedCategoryId == item.id,
                onClick = {
                  selectedCategoryId = item.id
                  onCategorySelected(item)
                },
            )
          }

          if (otherItems.isNotEmpty()) {
            item(key = "divider_all") { CategorySectionDivider(title = "All Categories") }

            items(otherItems, key = { it.id }) { item ->
              CategoryOptionItem(
                  name = item.name,
                  description = item.description,
                  icon = item.icon,
                  iconBackgroundColor = item.iconBackgroundColor,
                  iconContentColor = item.iconContentColor,
                  isSelected = selectedCategoryId == item.id,
                  onClick = {
                    selectedCategoryId = item.id
                    onCategorySelected(item)
                  },
              )
            }
          }

          item(key = "action_create") {
            CreateCategoryButton(
                onClick = onCreateCategoryClick,
                modifier = Modifier.padding(top = 4.dp),
            )
          }
        } else {
          if (filteredItems.isEmpty()) {
            item(key = "search_empty") { CategoryEmptyState() }
          } else {
            items(filteredItems, key = { it.id }) { item ->
              CategoryOptionItem(
                  name = item.name,
                  description = item.description,
                  icon = item.icon,
                  iconBackgroundColor = item.iconBackgroundColor,
                  iconContentColor = item.iconContentColor,
                  isSelected = selectedCategoryId == item.id,
                  onClick = {
                    selectedCategoryId = item.id
                    onCategorySelected(item)
                  },
              )
            }
          }

          item(key = "action_create_search") {
            CreateCategoryButton(
                onClick = onCreateCategoryClick,
                modifier = Modifier.padding(top = 4.dp),
            )
          }
        }
      }
    }
  }
}

// ============================================================================
// Screen Previews
// ============================================================================

@ScreenPreviews
@Composable
fun SelectCategoryScreenPreview() {
  SelectCategoryScreen(
      initialSelectedCategoryId = "dining",
  )
}

@ScreenPreviews
@Composable
fun SelectCategoryScreenSearchPreview() {
  SelectCategoryScreen(
      initialSelectedCategoryId = "groceries",
      initialSearchQuery = "groceries",
  )
}

@ScreenPreviews
@Composable
fun SelectCategoryScreenEmptyResultsPreview() {
  SelectCategoryScreen(
      initialSelectedCategoryId = "dining",
      initialSearchQuery = "unknownxyz",
  )
}
