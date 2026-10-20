package com.base.design.features.selectpocket

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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.ShoppingBag
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

/** Creates and remembers the default list of budget pockets for the selection screen. */
@Composable
fun rememberDefaultPockets(): List<PocketItemData> {
  val extended = MaterialTheme.colorSchemeExtended
  val colors = MaterialTheme.colorScheme

  return remember(extended, colors) {
    listOf(
        PocketItemData(
            id = "no_pocket",
            name = "No Pocket",
            categoryTag = "",
            balanceFormatted = "Clear pocket assignment",
            icon = Icons.AutoMirrored.Filled.LabelOff,
            iconBackgroundColor = colors.surfaceContainerHighest,
            iconContentColor = colors.outline,
            isFrequent = false,
            searchKeywords = listOf("no pocket", "clear", "remove", "none", "reset"),
        ),
        PocketItemData(
            id = "cafe",
            name = "Daily Cafe & Coffee",
            categoryTag = "Cafe & Dining",
            balanceFormatted = "400,000 T",
            icon = Icons.Default.LocalCafe,
            iconBackgroundColor = colors.primary,
            iconContentColor = colors.onPrimary,
            isFrequent = true,
            searchKeywords = listOf("coffee", "cafe", "daily", "dining", "sam cafe", "drink"),
        ),
        PocketItemData(
            id = "groceries",
            name = "Groceries & Household",
            categoryTag = "Groceries",
            balanceFormatted = "850,000 T",
            icon = Icons.Default.ShoppingBag,
            iconBackgroundColor = extended.tertiaryFixed,
            iconContentColor = extended.onTertiaryFixed,
            isFrequent = true,
            searchKeywords = listOf("groceries", "supermarket", "market", "food", "household"),
        ),
        PocketItemData(
            id = "gaming",
            name = "Entertainment & Gaming",
            categoryTag = "Entertainment",
            balanceFormatted = "1,200,000 T",
            icon = Icons.Default.SportsEsports,
            iconBackgroundColor = colors.secondaryContainer,
            iconContentColor = colors.onSecondaryContainer,
            isFrequent = true,
            searchKeywords = listOf("gaming", "entertainment", "steam", "playstation", "fun"),
        ),
        PocketItemData(
            id = "commute",
            name = "Commute & Fuel",
            categoryTag = "Transportation",
            balanceFormatted = "320,000 T",
            icon = Icons.Default.DirectionsCar,
            iconBackgroundColor = extended.secondaryFixed,
            iconContentColor = extended.onSecondaryFixed,
            isFrequent = false,
            searchKeywords = listOf("commute", "fuel", "gas", "car", "taxi", "snapp", "metro"),
        ),
        PocketItemData(
            id = "utilities",
            name = "Bills & Utilities",
            categoryTag = "Bills",
            balanceFormatted = "180,000 T",
            icon = Icons.Default.Bolt,
            iconBackgroundColor = extended.primaryFixed,
            iconContentColor = extended.onPrimaryFixed,
            isFrequent = false,
            searchKeywords =
                listOf("bills", "utilities", "electricity", "water", "internet", "phone"),
        ),
    )
  }
}

/**
 * Reusable modal sheet content for selecting a budget pocket.
 *
 * Can be embedded directly inside an expressive BottomSheet or rendered standalone.
 */
@Composable
fun SelectPocketSheetContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedPocketId: String?,
    pockets: List<PocketItemData>,
    onPocketSelected: (PocketItemData) -> Unit,
    modifier: Modifier = Modifier,
    onCreatePocketClick: () -> Unit = {},
    onCloseClick: (() -> Unit)? = null,
) {
  val noPocketItem = pockets.firstOrNull { it.id == "no_pocket" }
  val frequentItems = pockets.filter { it.isFrequent && it.id != "no_pocket" }
  val otherItems = pockets.filter { !it.isFrequent && it.id != "no_pocket" }

  val isSearchActive = searchQuery.isNotBlank()
  val filteredItems =
      remember(searchQuery, pockets) {
        if (!isSearchActive) {
          emptyList()
        } else {
          val query = searchQuery.trim().lowercase()
          pockets.filter { item ->
            item.name.lowercase().contains(query) ||
                item.categoryTag.lowercase().contains(query) ||
                item.searchKeywords.any { it.lowercase().contains(query) }
          }
        }
      }

  Column(
      modifier = modifier.fillMaxWidth().padding(bottom = 20.dp),
  ) {
    PocketGrabHandle()

    PocketSheetHeader(
        title = "Select Pocket",
        onCloseClick = onCloseClick,
    )

    PocketSearchField(
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
        // Option: No Pocket / Clear assignment
        if (noPocketItem != null) {
          item(key = noPocketItem.id) {
            PocketOptionItem(
                name = noPocketItem.name,
                categoryTag = noPocketItem.categoryTag,
                balanceFormatted = noPocketItem.balanceFormatted,
                icon = noPocketItem.icon,
                iconBackgroundColor = noPocketItem.iconBackgroundColor,
                iconContentColor = noPocketItem.iconContentColor,
                isSelected = selectedPocketId == noPocketItem.id,
                onClick = { onPocketSelected(noPocketItem) },
            )
          }
        }

        // Section: Frequent Pockets
        if (frequentItems.isNotEmpty()) {
          item(key = "divider_frequent") { PocketSectionDivider(title = "Frequent Pockets") }

          items(frequentItems, key = { it.id }) { item ->
            PocketOptionItem(
                name = item.name,
                categoryTag = item.categoryTag,
                balanceFormatted = item.balanceFormatted,
                icon = item.icon,
                iconBackgroundColor = item.iconBackgroundColor,
                iconContentColor = item.iconContentColor,
                isSelected = selectedPocketId == item.id,
                isOverBudget = item.isOverBudget,
                onClick = { onPocketSelected(item) },
            )
          }
        }

        // Section: All Pockets
        if (otherItems.isNotEmpty()) {
          item(key = "divider_all") { PocketSectionDivider(title = "All Pockets") }

          items(otherItems, key = { it.id }) { item ->
            PocketOptionItem(
                name = item.name,
                categoryTag = item.categoryTag,
                balanceFormatted = item.balanceFormatted,
                icon = item.icon,
                iconBackgroundColor = item.iconBackgroundColor,
                iconContentColor = item.iconContentColor,
                isSelected = selectedPocketId == item.id,
                isOverBudget = item.isOverBudget,
                onClick = { onPocketSelected(item) },
            )
          }
        }

        // Bottom Action: Create New Pocket
        item(key = "action_create") {
          CreatePocketButton(
              onClick = onCreatePocketClick,
              modifier = Modifier.padding(top = 4.dp),
          )
        }
      } else {
        // Search Results State
        if (filteredItems.isEmpty()) {
          item(key = "search_empty") { PocketEmptyState() }
        } else {
          items(filteredItems, key = { it.id }) { item ->
            PocketOptionItem(
                name = item.name,
                categoryTag = item.categoryTag,
                balanceFormatted = item.balanceFormatted,
                icon = item.icon,
                iconBackgroundColor = item.iconBackgroundColor,
                iconContentColor = item.iconContentColor,
                isSelected = selectedPocketId == item.id,
                isOverBudget = item.isOverBudget,
                onClick = { onPocketSelected(item) },
            )
          }
        }

        item(key = "action_create_search") {
          CreatePocketButton(
              onClick = onCreatePocketClick,
              modifier = Modifier.padding(top = 4.dp),
          )
        }
      }
    }
  }
}

/**
 * Screen: SelectPocketScreen
 *
 * Full screen representation for selecting a pocket consistently with SelectCategoryScreen:
 * - TopAppBar with back navigation and clear "Select Pocket" title
 * - Live pocket search field
 * - "No Pocket" option at top to clear assignment
 * - Frequent and all pockets list with category chips, balance, and active badge
 * - "Create New Pocket" action at the bottom
 * - Responsive support for large screens by centering with max 640.dp width
 */
@Composable
fun SelectPocketScreen(
    modifier: Modifier = Modifier,
    initialSelectedPocketId: String = "cafe",
    initialSearchQuery: String = "",
    pockets: List<PocketItemData> = rememberDefaultPockets(),
    onPocketSelected: (PocketItemData) -> Unit = {},
    onCreatePocketClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    onDismissRequest: () -> Unit = onBackClick ?: {},
) {
  var searchQuery by remember { mutableStateOf(initialSearchQuery) }
  var selectedPocketId by remember { mutableStateOf<String?>(initialSelectedPocketId) }

  val effectiveBackClick = onBackClick ?: onDismissRequest

  val noPocketItem = pockets.firstOrNull { it.id == "no_pocket" }
  val frequentItems = pockets.filter { it.isFrequent && it.id != "no_pocket" }
  val otherItems = pockets.filter { !it.isFrequent && it.id != "no_pocket" }

  val isSearchActive = searchQuery.isNotBlank()
  val filteredItems =
      remember(searchQuery, pockets) {
        if (!isSearchActive) {
          emptyList()
        } else {
          val query = searchQuery.trim().lowercase()
          pockets.filter { item ->
            item.name.lowercase().contains(query) ||
                item.categoryTag.lowercase().contains(query) ||
                item.searchKeywords.any { it.lowercase().contains(query) }
          }
        }
      }

  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopAppBar(
            title = "Select Pocket",
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
          PocketSearchField(
              query = searchQuery,
              onQueryChange = { searchQuery = it },
              modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
          )
        }

        if (!isSearchActive) {
          // Clear / No pocket option
          if (noPocketItem != null) {
            item(key = noPocketItem.id) {
              PocketOptionItem(
                  name = noPocketItem.name,
                  categoryTag = noPocketItem.categoryTag,
                  balanceFormatted = noPocketItem.balanceFormatted,
                  icon = noPocketItem.icon,
                  iconBackgroundColor = noPocketItem.iconBackgroundColor,
                  iconContentColor = noPocketItem.iconContentColor,
                  isSelected = selectedPocketId == noPocketItem.id,
                  onClick = {
                    selectedPocketId = noPocketItem.id
                    onPocketSelected(noPocketItem)
                  },
              )
            }
          }

          if (frequentItems.isNotEmpty()) {
            item(key = "divider_frequent") { PocketSectionDivider(title = "Frequent Pockets") }

            items(frequentItems, key = { it.id }) { item ->
              PocketOptionItem(
                  name = item.name,
                  categoryTag = item.categoryTag,
                  balanceFormatted = item.balanceFormatted,
                  icon = item.icon,
                  iconBackgroundColor = item.iconBackgroundColor,
                  iconContentColor = item.iconContentColor,
                  isSelected = selectedPocketId == item.id,
                  isOverBudget = item.isOverBudget,
                  onClick = {
                    selectedPocketId = item.id
                    onPocketSelected(item)
                  },
              )
            }
          }

          if (otherItems.isNotEmpty()) {
            item(key = "divider_all") { PocketSectionDivider(title = "All Pockets") }

            items(otherItems, key = { it.id }) { item ->
              PocketOptionItem(
                  name = item.name,
                  categoryTag = item.categoryTag,
                  balanceFormatted = item.balanceFormatted,
                  icon = item.icon,
                  iconBackgroundColor = item.iconBackgroundColor,
                  iconContentColor = item.iconContentColor,
                  isSelected = selectedPocketId == item.id,
                  isOverBudget = item.isOverBudget,
                  onClick = {
                    selectedPocketId = item.id
                    onPocketSelected(item)
                  },
              )
            }
          }

          item(key = "action_create") {
            CreatePocketButton(
                onClick = onCreatePocketClick,
                modifier = Modifier.padding(top = 4.dp),
            )
          }
        } else {
          if (filteredItems.isEmpty()) {
            item(key = "search_empty") { PocketEmptyState() }
          } else {
            items(filteredItems, key = { it.id }) { item ->
              PocketOptionItem(
                  name = item.name,
                  categoryTag = item.categoryTag,
                  balanceFormatted = item.balanceFormatted,
                  icon = item.icon,
                  iconBackgroundColor = item.iconBackgroundColor,
                  iconContentColor = item.iconContentColor,
                  isSelected = selectedPocketId == item.id,
                  isOverBudget = item.isOverBudget,
                  onClick = {
                    selectedPocketId = item.id
                    onPocketSelected(item)
                  },
              )
            }
          }

          item(key = "action_create_search") {
            CreatePocketButton(
                onClick = onCreatePocketClick,
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
fun SelectPocketScreenPreview() {
  SelectPocketScreen(
      initialSelectedPocketId = "cafe",
  )
}

@ScreenPreviews
@Composable
fun SelectPocketScreenSearchPreview() {
  SelectPocketScreen(
      initialSelectedPocketId = "groceries",
      initialSearchQuery = "groceries",
  )
}

@ScreenPreviews
@Composable
fun SelectPocketScreenEmptyResultsPreview() {
  SelectPocketScreen(
      initialSelectedPocketId = "cafe",
      initialSearchQuery = "nonexistent_pocket_query",
  )
}
