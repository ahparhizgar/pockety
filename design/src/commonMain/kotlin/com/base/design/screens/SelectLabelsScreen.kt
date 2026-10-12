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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.design.components.PocketyTopBar
import com.base.design.ui.ScreenPreviews

@Composable
fun SelectLabelsScreen(
    modifier: Modifier = Modifier,
    initialLabels: List<LabelItem> = FakeLabelsFactory.createList(),
    initialSearchQuery: String = "",
    onClose: () -> Unit = {},
    onDone: () -> Unit = {},
    onCancel: () -> Unit = {},
    onApply: (List<LabelItem>) -> Unit = {},
    onProfileClick: () -> Unit = {},
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

  val showCreateRow =
      remember(searchQuery, filteredLabels) {
        searchQuery.isNotBlank() &&
            filteredLabels.none { it.name.equals(searchQuery.trim(), ignoreCase = true) }
      }

  SelectLabelsContent(
      filteredLabels = filteredLabels,
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
      onClearAll = { labels = labels.map { it.copy(isSelected = false) } },
      onClose = onClose,
      onDone = onDone,
      onCancel = onCancel,
      onApply = { onApply(labels.filter { it.isSelected }) },
      onProfileClick = onProfileClick,
      modifier = modifier,
  )
}

@Composable
fun SelectLabelsContent(
    filteredLabels: List<LabelItem>,
    selectedCount: Int,
    searchQuery: String,
    showCreateRow: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    onCreateLabel: (String) -> Unit,
    onToggleLabel: (String) -> Unit,
    onClearAll: () -> Unit,
    onClose: () -> Unit,
    onDone: () -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Scaffold(
      modifier = modifier,
      topBar = {
        PocketyTopBar(
            title = "Select Labels",
            navigationIcon = {
              IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss dialog",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            },
            showSearch = false,
            showNotifications = false,
            actions = {
              Button(
                  onClick = onDone,
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                          contentColor = MaterialTheme.colorScheme.primary,
                      ),
                  shape = CircleShape,
                  contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                  modifier = Modifier.height(36.dp),
              ) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
              }
            },
            onProfileClick = onProfileClick,
        )
      },
      containerColor = MaterialTheme.colorScheme.surface,
  ) { innerPadding ->
    Box(
        modifier =
            Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter,
    ) {
      Surface(
          modifier =
              Modifier.fillMaxWidth()
                  .widthIn(max = 640.dp)
                  .padding(horizontal = 16.dp, vertical = 8.dp),
          shape = RoundedCornerShape(28.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 8.dp,
      ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          SelectLabelsCardHeader(
              selectedCount = selectedCount,
              onClearAll = onClearAll,
          )

          LabelSearchField(
              query = searchQuery,
              onQueryChange = onSearchQueryChange,
              onClearQuery = onClearSearchQuery,
          )

          if (showCreateRow) {
            CreateLabelRow(
                labelName = searchQuery.trim(),
                onCreateClick = { onCreateLabel(searchQuery.trim()) },
            )
          }

          Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            filteredLabels.forEach { label ->
              LabelCheckItem(
                  label = label,
                  onToggle = { onToggleLabel(label.id) },
              )
            }
          }

          SelectLabelsBottomBar(
              selectedCount = selectedCount,
              onCancel = onCancel,
              onApply = onApply,
          )
        }
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
