package com.base.feature.category.management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.base.db.CategoryEntity
import com.base.db.FakeCategoryFactory
import com.base.feature.category.add.AddCategoryScreen
import com.base.feature.category.add.resolveCategoryIcon
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.back_action
import pockety.app.shared.generated.resources.categories_title
import pockety.app.shared.generated.resources.category_add
import pockety.app.shared.generated.resources.category_empty

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun CategoryManagementFlowScreen(
    component: CategoryManagementFlowComponent,
    modifier: Modifier = Modifier,
) {
  Children(
      stack = component.stack,
      modifier = modifier.testTag("category-management-flow-screen"),
      animation =
          predictiveBackAnimation(
              backHandler = component.backHandler,
              onBack = component::onBack,
              fallbackAnimation = stackAnimation(fade() + scale()),
          ),
  ) {
    when (val child = it.instance) {
      is CategoryManagementFlowComponent.Child.Browse -> CategoryManagementScreen(child.component)
      is CategoryManagementFlowComponent.Child.Add -> AddCategoryScreen(child.component)
    }
  }
}

@Composable
fun CategoryManagementScreen(
    component: CategoryManagementComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()
  Scaffold(
      modifier = modifier.fillMaxSize().testTag("category-management-screen"),
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.categories_title),
            navigationIcon = {
              IconButton(
                  onClick = component::onBackClicked,
                  modifier = Modifier.testTag("back-button"),
              ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.back_action),
                )
              }
            },
        )
      },
      floatingActionButton = {
        Button(
            onClick = component::onAddClicked,
            modifier = Modifier.testTag("add-category-button"),
        ) {
          Icon(Icons.Default.Add, contentDescription = null)
          Text(stringResource(Res.string.category_add), modifier = Modifier.padding(start = 8.dp))
        }
      },
  ) { padding ->
    if (state.categories.isEmpty()) {
      EmptyCategoriesContent(padding)
    } else {
      CategoriesGridContent(padding, state)
    }
  }
}

@Composable
private fun EmptyCategoriesContent(padding: PaddingValues) {
  Box(
      Modifier.fillMaxSize().padding(padding).padding(24.dp),
      contentAlignment = Alignment.Center,
  ) {
    Text(stringResource(Res.string.category_empty), style = MaterialTheme.typography.bodyLarge)
  }
}

@Composable
private fun CategoriesGridContent(padding: PaddingValues, state: CategoryManagementState) {
  Box(
      modifier = Modifier.fillMaxSize().padding(padding),
      contentAlignment = Alignment.TopCenter,
  ) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier.fillMaxWidth().widthIn(max = 960.dp).padding(horizontal = 24.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      items(state.categories, key = { it.id }) { category -> CategoryManagementCard(category) }
    }
  }
}

@Composable
private fun CategoryManagementCard(category: CategoryEntity) {
  Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = MaterialTheme.shapes.large,
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Icon(
          imageVector = resolveCategoryIcon(category.icon),
          contentDescription = null,
          tint = Color(category.color),
      )
      Text(category.name, style = MaterialTheme.typography.titleMedium)
    }
  }
}

@ScreenPreviews
@Composable
private fun EmptyCategoryManagementPreview() {
  CategoryManagementScreen(FakeCategoryManagementComponent())
}

@ScreenPreviews
@Composable
private fun PopulatedCategoryManagementPreview() {
  CategoryManagementScreen(
      FakeCategoryManagementComponent(
          state = MutableValue(CategoryManagementState(FakeCategoryFactory.createList())),
      )
  )
}

@ScreenPreviews
@Composable
private fun RightToLeftCategoryManagementPreview() {
  CompositionLocalProvider(
      LocalLayoutDirection provides LayoutDirection.Rtl,
  ) {
    CategoryManagementScreen(
        FakeCategoryManagementComponent(
            state = MutableValue(CategoryManagementState(FakeCategoryFactory.createList())),
        )
    )
  }
}

@ScreenPreviews
@Composable
private fun CategoryManagementFlowPreview() {
  CategoryManagementFlowScreen(FakeCategoryManagementFlowComponent())
}
