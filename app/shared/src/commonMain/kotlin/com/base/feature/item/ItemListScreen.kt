package com.base.feature.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.dto.FakeItemDtoFactory
import com.base.dto.ItemDto
import com.base.ui.BaseTheme
import com.base.ui.ScreenPreviews
import com.base.ui.components.AdaptiveTopAppBar

@Composable
fun ItemListScreen(
    component: ItemListComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier,
      topBar = {
        AdaptiveTopAppBar(
            title = { Text("Items") },
        )
      },
  ) { padding ->
    Surface(
        modifier = Modifier.fillMaxSize().padding(padding),
        color = MaterialTheme.colorScheme.background,
    ) {
      Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.TopCenter,
      ) {
        Column(
            modifier = Modifier.fillMaxSize().widthIn(max = 840.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          when {
            state.isLoading -> {
              Box(
                  modifier = Modifier.fillMaxSize(),
                  contentAlignment = Alignment.Center,
              ) {
                CircularProgressIndicator(modifier = Modifier.testTag("loading-indicator"))
              }
            }

            state.errorMessage != null -> {
              Column(
                  modifier = Modifier.fillMaxSize().padding(16.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center,
              ) {
                Text(
                    text = state.errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { component.loadItems() },
                    modifier = Modifier.testTag("retry-button"),
                ) {
                  Text("Retry")
                }
              }
            }

            state.items.isEmpty() -> {
              Box(
                  modifier = Modifier.fillMaxSize(),
                  contentAlignment = Alignment.Center,
              ) {
                Text(
                    text = "No items available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }

            else -> {
              LazyVerticalGrid(
                  columns = GridCells.Adaptive(minSize = 240.dp),
                  modifier = Modifier.fillMaxSize().testTag("item-list"),
                  contentPadding = PaddingValues(16.dp),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                items(state.items, key = { it.id }) { item -> ItemRow(item = item) }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ItemRow(item: ItemDto) {
  Card(
      modifier = Modifier.fillMaxWidth().testTag("item-row-${item.id}"),
  ) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
    ) {
      Text(
          text = item.name,
          style = MaterialTheme.typography.titleMedium,
      )
    }
  }
}

@Composable
@ScreenPreviews
@Suppress("UnusedPrivateFunction")
private fun ItemListScreenPreview() {
  BaseTheme {
    ItemListScreen(
        FakeItemListComponent(
            initialState = ItemListState(items = FakeItemDtoFactory.createList(3)),
        ),
    )
  }
}

@Composable
@ScreenPreviews
@Suppress("UnusedPrivateFunction")
private fun ItemListScreenLoadingPreview() {
  BaseTheme {
    ItemListScreen(
        FakeItemListComponent(
            initialState = ItemListState(isLoading = true),
        ),
    )
  }
}

@Composable
@ScreenPreviews
@Suppress("UnusedPrivateFunction")
private fun ItemListScreenEmptyPreview() {
  BaseTheme {
    ItemListScreen(
        FakeItemListComponent(
            initialState = ItemListState(items = emptyList()),
        ),
    )
  }
}
