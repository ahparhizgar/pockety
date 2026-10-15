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
import com.base.feature.inbox.InboxComponent
import com.base.feature.inbox.InboxScreen
import com.base.feature.main.MainComponent
import com.base.feature.main.MainScreen
import com.base.feature.root.FakeRootComponent
import com.base.feature.root.RootComponent
import com.base.feature.splash.SplashDestination
import com.base.feature.splash.SplashScreen
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
      when (val child = it.instance) {
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

        else -> {
          error("Unknown child: $child")
        }
      }
    }
    SnackbarHost(hostState = hostState) { CustomSnackbar(snackbarData = it) }
  }
}

@ScreenPreviews
@Composable
private fun Preview() {
  RootScreen(FakeRootComponent())
}
