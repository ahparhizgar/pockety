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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LabelOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.feature.category.add.resolveCategoryIcon
import com.base.feature.category.CategoryOptionData
import com.base.feature.category.CategoryOptionItem
import com.base.feature.category.CategoryNoResultsState
import com.base.feature.category.CategorySearchField
import com.base.feature.category.CreateCategoryButton
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.back_action
import pockety.app.shared.generated.resources.categories_title
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
          modifier = Modifier.testTag("create-category-button"),
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
      item(key = "empty_state") { CategoryNoResultsState() }
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
