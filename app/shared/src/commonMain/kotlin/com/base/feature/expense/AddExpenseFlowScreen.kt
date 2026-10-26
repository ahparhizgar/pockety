package com.base.feature.expense

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.base.feature.category.add.AddCategoryScreen
import com.base.feature.category.selection.CategorySelectionScreen
import com.base.feature.expense.add.AddExpenseScreen
import com.base.feature.pocket.selection.SelectPocketScreen
import com.base.ui.ScreenPreviews

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun AddExpenseFlowScreen(
    component: AddExpenseFlowComponent,
    modifier: Modifier = Modifier,
) {
  Children(
      stack = component.stack,
      modifier = modifier.testTag("add-expense-flow-screen"),
      animation =
          predictiveBackAnimation(
              backHandler = component.backHandler,
              onBack = component::onBack,
              fallbackAnimation = stackAnimation(fade() + scale()),
          ),
  ) {
    when (val child = it.instance) {
      is AddExpenseFlowComponent.Child.AddExpense -> {
        AddExpenseScreen(
            component = child.component,
            modifier = Modifier.testTag("add-expense-screen"),
        )
      }

      is AddExpenseFlowComponent.Child.CategorySelection -> {
        CategorySelectionScreen(
            component = child.component,
            modifier = Modifier.testTag("category-selection-screen"),
        )
      }

      is AddExpenseFlowComponent.Child.AddCategory -> {
        AddCategoryScreen(
            component = child.component,
            modifier = Modifier.testTag("add-category-screen"),
        )
      }

      is AddExpenseFlowComponent.Child.PocketSelection -> {
        SelectPocketScreen(
            component = child.component,
            modifier = Modifier.testTag("select-pocket-screen"),
        )
      }
    }
  }
}

@Composable
@ScreenPreviews
private fun AddExpenseFlowScreenPreview() {
  AddExpenseFlowScreen(FakeAddExpenseFlowComponent())
}
