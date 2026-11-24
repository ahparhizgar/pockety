package com.base.feature.category.management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
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
import com.base.feature.category.CategoryNoResultsState
import com.base.feature.category.CategoryOptionData
import com.base.feature.category.CategoryOptionItem
import com.base.feature.category.CategorySearchField
import com.base.feature.category.CreateCategoryButton
import com.base.feature.category.add.AddCategoryScreen
import com.base.feature.category.add.resolveCategoryIcon
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import sh.calvin.reorderable.reorderable
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.back_action
import pockety.app.shared.generated.resources.cancel_action
import pockety.app.shared.generated.resources.categories_title
import pockety.app.shared.generated.resources.category_delete_action
import pockety.app.shared.generated.resources.category_delete_dialog_message
import pockety.app.shared.generated.resources.category_delete_dialog_title
import pockety.app.shared.generated.resources.category_empty
import pockety.app.shared.generated.resources.category_reorder_action
import pockety.app.shared.generated.resources.delete_action

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
  ) { innerPadding ->
    CategoryManagementContent(
        component = component,
        state = state,
        modifier = Modifier.padding(innerPadding),
    )
  }

  state.categoryPendingDeletion?.let { category ->
    DeleteCategoryDialog(component = component, category = category)
  }
}

@Composable
private fun CategoryManagementContent(
    component: CategoryManagementComponent,
    state: CategoryManagementState,
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
      if (state.categories.isNotEmpty()) {
        CategorySearchField(
            query = state.searchQuery,
            onQueryChange = component::onSearchQueryChanged,
        )
      }

      if (state.categories.isEmpty()) {
        EmptyCategoriesContent(modifier = Modifier.fillMaxWidth().weight(1f))
      } else {
        CategoryManagementList(
            component = component,
            state = state,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )
      }

      CreateCategoryButton(
          onClick = component::onAddClicked,
          modifier = Modifier.testTag("add-category-button"),
      )
    }
  }
}

@Composable
private fun CategoryManagementList(
    component: CategoryManagementComponent,
    state: CategoryManagementState,
    modifier: Modifier = Modifier,
) {
  val showAll = state.searchQuery.isBlank()
  val displayList = if (showAll) state.categories else state.filteredCategories
  val lazyListState = rememberLazyListState()
  val reorderableState =
      rememberReorderableLazyListState(lazyListState) { from, to ->
        component.onCategoryMoved(from.index, to.index)
      }

  LazyColumn(
      state = lazyListState,
      modifier = if (showAll) modifier.reorderable(reorderableState) else modifier,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(bottom = 16.dp),
  ) {
    if (!showAll && displayList.isEmpty()) {
      item(key = "empty_state") { CategoryNoResultsState() }
    } else {
      itemsIndexed(displayList, key = { _, category -> category.id }) { index, category ->
        ReorderableItem(reorderableState, key = category.id) { isDragging ->
          CategoryOptionItem(
              data =
                  CategoryOptionData(
                      name = category.name,
                      icon = resolveCategoryIcon(category.icon),
                      iconBackgroundColor = Color(category.color),
                      iconContentColor = Color.White,
                  ),
              onClick = {},
              isDragging = isDragging,
              modifier = Modifier.testTag("category-item-${category.name}"),
              leadingContent =
                  if (showAll) {
                    { handleModifier ->
                      IconButton(
                          onClick = {},
                          modifier =
                              handleModifier
                                  .draggableHandle(
                                      onDragStopped = { component.onCategoryMoveFinished() },
                                  )
                                  .onKeyEvent { event ->
                                    handleReorderKeyEvent(event, index, component)
                                  }
                                  .testTag("reorder-handle-${category.name}"),
                      ) {
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription =
                                stringResource(Res.string.category_reorder_action, category.name),
                        )
                      }
                    }
                  } else {
                    null
                  },
              trailingContent = {
                IconButton(
                    onClick = { component.onDeleteClicked(category) },
                    modifier = Modifier.testTag("delete-category-${category.name}"),
                ) {
                  Icon(
                      imageVector = Icons.Default.Delete,
                      contentDescription = stringResource(Res.string.category_delete_action),
                      tint = MaterialTheme.colorScheme.error,
                  )
                }
              },
          )
        }
      }
    }
  }
}

private fun handleReorderKeyEvent(
    event: androidx.compose.ui.input.key.KeyEvent,
    index: Int,
    component: CategoryManagementComponent,
): Boolean {
  if (event.type != KeyEventType.KeyDown) return false
  return when (event.key) {
    Key.DirectionUp -> {
      component.onCategoryMoved(index, index - 1)
      component.onCategoryMoveFinished()
      true
    }
    Key.DirectionDown -> {
      component.onCategoryMoved(index, index + 1)
      component.onCategoryMoveFinished()
      true
    }
    else -> false
  }
}

@Composable
private fun EmptyCategoriesContent(modifier: Modifier = Modifier) {
  Box(
      modifier = modifier.padding(24.dp),
      contentAlignment = Alignment.Center,
  ) {
    Text(stringResource(Res.string.category_empty), style = MaterialTheme.typography.bodyLarge)
  }
}

@Composable
private fun DeleteCategoryDialog(
    component: CategoryManagementComponent,
    category: CategoryEntity,
) {
  AlertDialog(
      onDismissRequest = component::onDeleteDismissed,
      title = { Text(text = stringResource(Res.string.category_delete_dialog_title)) },
      text = {
        Text(text = stringResource(Res.string.category_delete_dialog_message, category.name))
      },
      confirmButton = {
        TextButton(
            onClick = component::onDeleteConfirmed,
            modifier = Modifier.testTag("confirm-delete-button"),
        ) {
          Text(
              text = stringResource(Res.string.delete_action),
              color = MaterialTheme.colorScheme.error,
          )
        }
      },
      dismissButton = {
        TextButton(
            onClick = component::onDeleteDismissed,
            modifier = Modifier.testTag("cancel-delete-button"),
        ) {
          Text(text = stringResource(Res.string.cancel_action))
        }
      },
  )
}

@ScreenPreviews
@Composable
private fun EmptyCategoryManagementPreview() {
  CategoryManagementScreen(FakeCategoryManagementComponent())
}

@ScreenPreviews
@Composable
private fun PopulatedCategoryManagementPreview() {
  val categories = FakeCategoryFactory.createList()
  CategoryManagementScreen(
      FakeCategoryManagementComponent(
          state =
              MutableValue(
                  CategoryManagementState(
                      categories = categories,
                      filteredCategories = categories,
                  )
              ),
      )
  )
}

@ScreenPreviews
@Composable
private fun SearchCategoryManagementPreview() {
  val categories = FakeCategoryFactory.createList()
  CategoryManagementScreen(
      FakeCategoryManagementComponent(
          state =
              MutableValue(
                  CategoryManagementState(
                      categories = categories,
                      filteredCategories = listOf(categories.first()),
                      searchQuery = "Category 1",
                  )
              ),
      )
  )
}

@ScreenPreviews
@Composable
private fun NoResultsCategoryManagementPreview() {
  val categories = FakeCategoryFactory.createList()
  CategoryManagementScreen(
      FakeCategoryManagementComponent(
          state =
              MutableValue(
                  CategoryManagementState(
                      categories = categories,
                      filteredCategories = emptyList(),
                      searchQuery = "unknownxyz",
                  )
              ),
      )
  )
}

@ScreenPreviews
@Composable
private fun DeleteCategoryManagementPreview() {
  val categories = FakeCategoryFactory.createList()
  CategoryManagementScreen(
      FakeCategoryManagementComponent(
          state =
              MutableValue(
                  CategoryManagementState(
                      categories = categories,
                      filteredCategories = categories,
                      categoryPendingDeletion = categories.first(),
                  )
              ),
      )
  )
}

@ScreenPreviews
@Composable
private fun RightToLeftCategoryManagementPreview() {
  CompositionLocalProvider(
      LocalLayoutDirection provides LayoutDirection.Rtl,
  ) {
    val categories = FakeCategoryFactory.createList()
    CategoryManagementScreen(
        FakeCategoryManagementComponent(
            state =
                MutableValue(
                    CategoryManagementState(
                        categories = categories,
                        filteredCategories = categories,
                    )
                ),
        )
    )
  }
}

@ScreenPreviews
@Composable
private fun CategoryManagementFlowPreview() {
  CategoryManagementFlowScreen(FakeCategoryManagementFlowComponent())
}
