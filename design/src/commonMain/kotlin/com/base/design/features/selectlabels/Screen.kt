package com.base.design.features.selectlabels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.base.design.globalcomponents.PocketyBottomActionBar
import com.base.design.globalcomponents.PocketyTopAppBar
import com.base.design.ui.ScreenPreviews

@Composable
fun SelectLabelsScreen(
    initialLabels: List<LabelItem> = FakeLabelsFactory.createList(),
    initialSearchQuery: String = "",
    onBackClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onApply: (List<LabelItem>) -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
  var labels by remember { mutableStateOf(initialLabels) }
  var searchQuery by remember { mutableStateOf(initialSearchQuery) }

  val filteredLabels =
      remember(labels, searchQuery) {
        val trimmed = searchQuery.trim()
        if (trimmed.isEmpty()) {
          labels
        } else {
          labels.filter { it.name.contains(trimmed, ignoreCase = true) }
        }
      }

  val selectedCount = remember(labels) { labels.count { it.isSelected } }
  val selectedLabels = remember(labels) { labels.filter { it.isSelected } }

  val showCreateRow =
      remember(searchQuery, filteredLabels) {
        searchQuery.isNotBlank() &&
            filteredLabels.none { it.name.equals(searchQuery.trim(), ignoreCase = true) }
      }

  val handleBack: () -> Unit = { (onBackClick ?: onClose ?: onCancel)?.invoke() }
  val handleCancel: () -> Unit = { (onCancel ?: onBackClick ?: onClose)?.invoke() }
  val handleApply: () -> Unit = {
    onApply(labels.filter { it.isSelected })
    (onDone ?: onBackClick ?: onClose)?.invoke()
  }

  SelectLabelsContent(
      filteredLabels = filteredLabels,
      selectedLabels = selectedLabels,
      selectedCount = selectedCount,
      searchQuery = searchQuery,
      showCreateRow = showCreateRow,
      onSearchQueryChange = { searchQuery = it },
      onClearSearchQuery = { searchQuery = "" },
      onCreateLabel = { name ->
        val newLabel =
            LabelItem(
                id = name.lowercase().replace("\\s+".toRegex(), "_"),
                name = name,
                isSelected = true,
            )
        labels = listOf(newLabel) + labels
        searchQuery = ""
      },
      onToggleLabel = { labelId ->
        labels = labels.map {
          if (it.id == labelId) {
            it.copy(isSelected = !it.isSelected)
          } else {
            it
          }
        }
      },
      onRemoveLabel = { labelId ->
        labels = labels.map {
          if (it.id == labelId) {
            it.copy(isSelected = false)
          } else {
            it
          }
        }
      },
      onClearAll = { labels = labels.map { it.copy(isSelected = false) } },
      onBackClick = handleBack,
      onCancel = handleCancel,
      onApply = handleApply,
      onDone = handleApply,
      onProfileClick = onProfileClick,
      modifier = modifier,
  )
}

@Composable
fun SelectLabelsContent(
    filteredLabels: List<LabelItem>,
    selectedLabels: List<LabelItem>,
    selectedCount: Int,
    searchQuery: String,
    showCreateRow: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    onCreateLabel: (String) -> Unit,
    onToggleLabel: (String) -> Unit,
    onRemoveLabel: (String) -> Unit,
    onClearAll: () -> Unit,
    onBackClick: () -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
    onDone: () -> Unit = onApply,
    onProfileClick: () -> Unit = {},
) {
  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopAppBar(
            title = "Select Labels",
            onBackClick = onBackClick,
        )
      },
      bottomBar = {
        PocketyBottomActionBar(
            text = "Apply ($selectedCount)",
            onClick = onApply,
            secondaryButtonText = "Cancel",
            onSecondaryClick = onCancel,
        )
      },
  ) { innerPadding ->
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
          modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        item(key = "search_field") {
          LabelSearchField(
              query = searchQuery,
              onQueryChange = onSearchQueryChange,
              onClearQuery = onClearSearchQuery,
          )
        }

        if (selectedLabels.isNotEmpty()) {
          item(key = "selected_chips") {
            SelectedLabelChipsRow(
                selectedLabels = selectedLabels,
                onRemoveLabel = onRemoveLabel,
            )
          }
        }

        item(key = "selection_header") {
          SelectLabelsHeader(
              selectedCount = selectedCount,
              onClearAll = onClearAll,
              title = "All Labels",
          )
        }

        if (showCreateRow) {
          item(key = "create_prompt") {
            CreateLabelRow(
                labelName = searchQuery.trim(),
                onCreateClick = { onCreateLabel(searchQuery.trim()) },
            )
          }
        }

        if (filteredLabels.isEmpty() && !showCreateRow) {
          item(key = "empty_state") { LabelsEmptyState() }
        } else {
          items(
              items = filteredLabels,
              key = { it.id },
          ) { label ->
            LabelCheckItem(
                label = label,
                onToggle = { onToggleLabel(label.id) },
            )
          }
        }

        item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(16.dp)) }
      }
    }
  }
}

@ScreenPreviews
@Composable
private fun SelectLabelsScreenPreview() {
  SelectLabelsScreen()
}

@ScreenPreviews
@Composable
private fun SelectLabelsScreenSearchingPreview() {
  SelectLabelsScreen(initialSearchQuery = "sdfs")
}

@ScreenPreviews
@Composable
private fun SelectLabelsScreenEmptySelectionPreview() {
  SelectLabelsScreen(
      initialLabels = FakeLabelsFactory.createList().map { it.copy(isSelected = false) },
  )
}

@ScreenPreviews
@Composable
private fun SelectLabelsScreenNoResultsPreview() {
  SelectLabelsScreen(
      initialLabels = emptyList(),
  )
}
