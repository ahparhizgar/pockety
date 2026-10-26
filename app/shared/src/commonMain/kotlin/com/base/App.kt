package com.base

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.base.capability.adding_expense.AddExpenseFlowComponent
import com.base.capability.adding_expense.AddExpenseFlowScreen
import com.base.capability.adding_expense.add.AddExpenseComponent
import com.base.capability.adding_expense.add.AddExpenseScreen
import com.base.capability.main.MainComponent
import com.base.capability.main.MainScreen
import com.base.capability.pocket_management.PocketDetailsComponent
import com.base.capability.pocket_management.PocketDetailsScreen
import com.base.capability.pocket_management.PocketSettingsFlowComponent
import com.base.capability.pocket_management.PocketSettingsFlowScreen
import com.base.capability.root_navigation.FakeRootComponent
import com.base.capability.root_navigation.RootComponent
import com.base.capability.sms_inbox.InboxComponent
import com.base.capability.sms_inbox.InboxScreen
import com.base.capability.splash.SplashDestination
import com.base.capability.splash.SplashScreen
import com.base.component.TopLevelChild
import com.base.ui.BaseTheme
import com.base.ui.ScreenPreviews
import com.base.usermessage.CustomSnackbar
import com.base.usermessage.SnackbarHost
import com.base.usermessage.SnackbarHostState

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootScreen(root: RootComponent, modifier: Modifier = Modifier) {
  val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
  SideEffect { root.windowSizeHolder.update(windowSizeClass) }

  BaseTheme {
    val hostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
      while (true) {
        root.messageHolder.showAll { hostState.showSnackbar(it) }
      }
    }

    Children(
        // The background is to prevent a flicker when animating children
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        stack = root.childStack,
        animation =
            predictiveBackAnimation(
                backHandler = root.backHandler,
                onBack = root::onBack,
                fallbackAnimation = stackAnimation(fade() + scale()),
            ),
    ) {
      RootChildContent(child = it.instance)
    }
    SnackbarHost(hostState = hostState) { CustomSnackbar(snackbarData = it) }
  }
}

@Composable
private fun RootChildContent(child: TopLevelChild) {
  when (child) {
    is SplashDestination -> {
      SplashScreen(modifier = Modifier.testTag("splash-screen"))
    }
    is MainComponent -> {
      MainScreen(
          component = child,
          modifier = Modifier.testTag("main-screen"),
      )
    }
    is InboxComponent -> {
      InboxScreen(
          component = child,
          modifier = Modifier.testTag("inbox-screen"),
      )
    }
    is AddExpenseComponent -> {
      AddExpenseScreen(
          component = child,
          modifier = Modifier.testTag("add-expense-screen"),
      )
    }
    is AddExpenseFlowComponent -> {
      AddExpenseFlowScreen(
          component = child,
          modifier = Modifier.testTag("add-expense-flow-screen"),
      )
    }
    is PocketSettingsFlowComponent -> {
      PocketSettingsFlowScreen(
          component = child,
          modifier = Modifier.testTag("pocket-settings-flow-screen"),
      )
    }
    is PocketDetailsComponent -> {
      PocketDetailsScreen(
          component = child,
          modifier = Modifier.testTag("pocket-details-screen"),
      )
    }
    else -> {
      error("Unknown child: $child")
    }
  }
}

@ScreenPreviews
@Composable
private fun Preview() {
  RootScreen(FakeRootComponent())
}
