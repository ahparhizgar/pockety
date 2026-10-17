package com.base.capability.pocket_management

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
import com.base.capability.category_management.AddCategoryScreen
import com.base.capability.category_management.CategorySelectionScreen
import com.base.ui.ScreenPreviews

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun PocketSettingsFlowScreen(
    component: PocketSettingsFlowComponent,
    modifier: Modifier = Modifier,
) {
  Children(
      stack = component.stack,
      modifier = modifier.testTag("pocket-settings-flow-screen"),
      animation =
          predictiveBackAnimation(
              backHandler = component.backHandler,
              onBack = component::onBack,
              fallbackAnimation = stackAnimation(fade() + scale()),
          ),
  ) {
    when (val child = it.instance) {
      is PocketSettingsFlowComponent.Child.PocketSettings -> {
        PocketSettingsScreen(
            component = child.component,
            modifier = Modifier.testTag("pocket-settings-screen"),
        )
      }

      is PocketSettingsFlowComponent.Child.CategorySelection -> {
        CategorySelectionScreen(
            component = child.component,
            modifier = Modifier.testTag("category-selection-screen"),
        )
      }

      is PocketSettingsFlowComponent.Child.AddCategory -> {
        AddCategoryScreen(
            component = child.component,
            modifier = Modifier.testTag("add-category-screen"),
        )
      }
    }
  }
}

@Composable
@ScreenPreviews
private fun PocketSettingsFlowScreenPreview() {
  PocketSettingsFlowScreen(FakePocketSettingsFlowComponent())
}
