package com.base.feature.pocket.selection

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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LabelOff
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
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
import com.base.db.FakePocketItemModelFactory
import com.base.db.PocketItemModel
import com.base.feature.category.add.resolveCategoryIcon
import com.base.feature.pocket.PocketShapeBadge
import com.base.ui.ComponentPreviews
import com.base.ui.ScreenPreviews
import com.base.ui.colorSchemeExtended
import com.base.ui.components.PocketyTopAppBar
import com.base.ui.money.moneyLabel
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.active_badge
import pockety.app.shared.generated.resources.all_pockets_title
import pockety.app.shared.generated.resources.back_action
import pockety.app.shared.generated.resources.create_new_pocket_action
import pockety.app.shared.generated.resources.frequent_pockets_title
import pockety.app.shared.generated.resources.no_pocket_desc
import pockety.app.shared.generated.resources.no_pocket_title
import pockety.app.shared.generated.resources.no_pockets_found
import pockety.app.shared.generated.resources.search_pockets_placeholder
import pockety.app.shared.generated.resources.select_pocket_title

@Composable
fun SelectPocketScreen(
    component: SelectPocketComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.fillMaxSize().testTag("select-pocket-screen"),
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.select_pocket_title),
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
  ) { paddingValues ->
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.TopCenter,
    ) {
      SelectPocketContent(
          component = component,
          state = state,
          modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).padding(horizontal = 20.dp),
      )
    }
  }
}

@Composable
private fun SelectPocketContent(
    component: SelectPocketComponent,
    state: SelectPocketState,
    modifier: Modifier = Modifier,
) {
  val isSearchActive = state.searchQuery.isNotBlank()
  val displayList = if (isSearchActive) state.filteredPockets else state.pockets

  LazyColumn(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
  ) {
    item(key = "search_field") {
      PocketSearchField(
          query = state.searchQuery,
          onQueryChange = component::onSearchQueryChanged,
          modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
      )
    }

    if (!isSearchActive) {
      renderStandardPockets(state, component)
    } else {
      renderSearchPockets(displayList, state, component)
    }
  }
}

private fun LazyListScope.renderStandardPockets(
    state: SelectPocketState,
    component: SelectPocketComponent,
) {
  item(key = "no_pocket") {
    PocketOptionRow(
        visuals =
            PocketOptionVisuals(
                name = stringResource(Res.string.no_pocket_title),
                categoryTag = "",
                balanceFormatted = stringResource(Res.string.no_pocket_desc),
                icon = Icons.AutoMirrored.Filled.LabelOff,
                iconBackgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                iconContentColor = MaterialTheme.colorScheme.outline,
                isSelected = state.selectedPocketId == null,
            ),
        onClick = component::onNoPocketSelected,
        modifier = Modifier.testTag("pocket-option-no-pocket"),
    )
  }

  val frequentCount = if (state.pockets.size > 3) 2 else state.pockets.size
  val frequentPockets = state.pockets.take(frequentCount)
  val otherPockets = state.pockets.drop(frequentCount)

  if (frequentPockets.isNotEmpty() && otherPockets.isNotEmpty()) {
    item(key = "divider_frequent") {
      PocketSectionDivider(title = stringResource(Res.string.frequent_pockets_title))
    }
    items(frequentPockets, key = { it.id }) { pocket ->
      PocketOptionItem(
          pocket = pocket,
          isSelected = state.selectedPocketId == pocket.id,
          onClick = { component.onPocketSelected(pocket) },
      )
    }
    item(key = "divider_all") {
      PocketSectionDivider(title = stringResource(Res.string.all_pockets_title))
    }
    items(otherPockets, key = { it.id }) { pocket ->
      PocketOptionItem(
          pocket = pocket,
          isSelected = state.selectedPocketId == pocket.id,
          onClick = { component.onPocketSelected(pocket) },
      )
    }
  } else if (state.pockets.isNotEmpty()) {
    item(key = "divider_all_single") {
      PocketSectionDivider(title = stringResource(Res.string.all_pockets_title))
    }
    items(state.pockets, key = { it.id }) { pocket ->
      PocketOptionItem(
          pocket = pocket,
          isSelected = state.selectedPocketId == pocket.id,
          onClick = { component.onPocketSelected(pocket) },
      )
    }
  }

  item(key = "action_create") {
    CreatePocketButton(
        onClick = component::onCreatePocketClicked,
        modifier = Modifier.padding(top = 4.dp).testTag("create-pocket-button"),
    )
  }
}

private fun LazyListScope.renderSearchPockets(
    displayList: List<PocketItemModel>,
    state: SelectPocketState,
    component: SelectPocketComponent,
) {
  if (displayList.isEmpty()) {
    item(key = "search_empty") {
      PocketEmptyState(
          modifier = Modifier.testTag("empty-pockets-message"),
      )
    }
  } else {
    items(displayList, key = { it.id }) { pocket ->
      PocketOptionItem(
          pocket = pocket,
          isSelected = state.selectedPocketId == pocket.id,
          onClick = { component.onPocketSelected(pocket) },
      )
    }
  }

  item(key = "action_create_search") {
    CreatePocketButton(
        onClick = component::onCreatePocketClicked,
        modifier = Modifier.padding(top = 4.dp).testTag("create-pocket-button"),
    )
  }
}

@Composable
private fun PocketSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
  val focusManager = LocalFocusManager.current

  TextField(
      value = query,
      onValueChange = onQueryChange,
      modifier = modifier.fillMaxWidth().testTag("pocket-search-field"),
      placeholder = {
        Text(
            text = stringResource(Res.string.search_pockets_placeholder),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )
      },
      leadingIcon = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(22.dp),
        )
      },
      trailingIcon = {
        if (query.isNotEmpty()) {
          IconButton(onClick = { onQueryChange("") }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
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

private data class PocketOptionVisuals(
    val name: String,
    val categoryTag: String,
    val balanceFormatted: String,
    val icon: ImageVector,
    val iconBackgroundColor: Color,
    val iconContentColor: Color,
    val isSelected: Boolean,
    val isOverBudget: Boolean = false,
)

@Composable
private fun PocketOptionItem(
    pocket: PocketItemModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  PocketOptionRow(
      visuals =
          PocketOptionVisuals(
              name = pocket.name,
              categoryTag = pocket.categoryTag,
              balanceFormatted = moneyLabel(pocket.remainingBalance),
              icon = resolveCategoryIcon(pocket.icon),
              iconBackgroundColor = Color(pocket.color),
              iconContentColor = Color.White,
              isSelected = isSelected,
              isOverBudget = pocket.isOverBudget,
          ),
      onClick = onClick,
      modifier = modifier.testTag("pocket-option-${pocket.id}"),
  )
}

@Composable
private fun PocketOptionRow(
    visuals: PocketOptionVisuals,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val containerColor =
      if (visuals.isSelected) {
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
      PocketOptionBadge(
          icon = visuals.icon,
          backgroundColor = visuals.iconBackgroundColor,
          contentColor = visuals.iconContentColor,
      )

      Spacer(modifier = Modifier.width(14.dp))

      PocketOptionDetails(
          visuals = visuals,
          modifier = Modifier.weight(1f),
      )

      if (visuals.isSelected) {
        PocketCheckmark()
      }
    }
  }
}

@Composable
private fun PocketOptionBadge(
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
  PocketShapeBadge(
      icon = icon,
      color = backgroundColor,
      contentDescription = null,
      modifier = modifier,
      badgeSize = 44.dp,
      iconSize = 20.dp,
  )
}

@Composable
private fun PocketOptionDetails(
    visuals: PocketOptionVisuals,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier,
      verticalArrangement = Arrangement.Center,
  ) {
    PocketOptionTitleRow(name = visuals.name, isSelected = visuals.isSelected)
    Spacer(modifier = Modifier.height(2.dp))
    PocketOptionSubtitleRow(
        categoryTag = visuals.categoryTag,
        balanceFormatted = visuals.balanceFormatted,
        isOverBudget = visuals.isOverBudget,
    )
  }
}

@Composable
private fun PocketOptionTitleRow(
    name: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier,
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
          contentColor = MaterialTheme.colorSchemeExtended.onPrimaryFixed,
      ) {
        Text(
            text = stringResource(Res.string.active_badge),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
        )
      }
    }
  }
}

@Composable
private fun PocketOptionSubtitleRow(
    categoryTag: String,
    balanceFormatted: String,
    isOverBudget: Boolean,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier,
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
          text = if (categoryTag.isNotEmpty()) "• $balanceFormatted" else balanceFormatted,
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

@Composable
private fun PocketCheckmark(
    modifier: Modifier = Modifier,
) {
  Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      modifier = modifier.size(24.dp),
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
      )
    }
  }
}

@Composable
private fun PocketSectionDivider(
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

@Composable
private fun PocketEmptyState(
    modifier: Modifier = Modifier,
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
        text = stringResource(Res.string.no_pockets_found),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun CreatePocketButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      onClick = onClick,
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      contentColor = MaterialTheme.colorScheme.primary,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
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
          text = stringResource(Res.string.create_new_pocket_action),
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

// ============================================================================
// Previews
// ============================================================================

@ScreenPreviews
@Composable
private fun SelectPocketScreenPreview() {
  SelectPocketScreen(
      component =
          FakeSelectPocketComponent(
              initialState =
                  SelectPocketState(
                      pockets = FakePocketItemModelFactory.createList(),
                      selectedPocketId = 1L,
                  ),
          ),
  )
}

@ScreenPreviews
@Composable
private fun SelectPocketScreenSearchPreview() {
  SelectPocketScreen(
      component =
          FakeSelectPocketComponent(
              initialState =
                  SelectPocketState(
                      pockets = FakePocketItemModelFactory.createList(),
                      filteredPockets = FakePocketItemModelFactory.createList().take(1),
                      searchQuery = "groceries",
                      selectedPocketId = 1L,
                  ),
          ),
  )
}

@ScreenPreviews
@Composable
private fun SelectPocketScreenEmptyPreview() {
  SelectPocketScreen(
      component =
          FakeSelectPocketComponent(
              initialState =
                  SelectPocketState(
                      pockets = FakePocketItemModelFactory.createList(),
                      filteredPockets = emptyList(),
                      searchQuery = "nonexistent",
                  ),
          ),
  )
}

@ComponentPreviews
@Composable
private fun PocketOptionItemPreview() {
  PocketOptionItem(
      pocket = FakePocketItemModelFactory.create(),
      isSelected = true,
      onClick = {},
  )
}

@ComponentPreviews
@Composable
private fun PocketSearchFieldPreview() {
  PocketSearchField(
      query = "Cafe",
      onQueryChange = {},
  )
}
