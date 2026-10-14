package com.base.feature.main

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
import com.base.feature.home.HomeScreen
import com.base.feature.transaction.add.AddTransactionScreen
import com.base.ui.ScreenPreviews

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun MainScreen(
    component: MainComponent,
    modifier: Modifier = Modifier,
) {
  Children(
      stack = component.stack,
      modifier = modifier.testTag("main-screen"),
      animation =
          predictiveBackAnimation(
              backHandler = component.backHandler,
              onBack = component::onBack,
              fallbackAnimation = stackAnimation(fade() + scale()),
          ),
  ) {
    when (val child = it.instance) {
      is MainComponent.Child.Home -> {
        HomeScreen(
            component = child.component,
            modifier = Modifier.testTag("main-home-screen"),
        )
      }

      is MainComponent.Child.AddTransaction -> {
        AddTransactionScreen(
            component = child.component,
            modifier = Modifier.testTag("main-add-transaction-screen"),
        )
      }
    }
  }
}

@Composable
@ScreenPreviews
private fun MainScreenPreview() {
  MainScreen(FakeMainComponent())
}
