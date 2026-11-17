package com.base

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.feature.expense.AddExpenseFlowScreen
import com.base.feature.home.HomeScreen
import com.base.feature.main.MainScreen
import com.base.feature.pocket.PocketsScreen
import com.base.feature.pocket.details.PocketDetailsScreen
import com.base.feature.pocket.settings.PocketSettingsFlowScreen
import com.base.feature.privacypolicy.PrivacyPolicyScreen
import com.base.feature.root.FakeRootComponent
import com.base.feature.root.RootComponent
import com.base.feature.root.RootFlowDestination
import com.base.feature.settings.AppLanguage
import com.base.feature.sms.inbox.InboxScreen
import com.base.feature.splash.SplashScreen
import com.base.ui.BaseTheme
import com.base.ui.ScreenPreviews
import com.base.usermessage.CustomSnackbar
import com.base.usermessage.SnackbarHost
import com.base.usermessage.SnackbarHostState

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootScreen(
    root: RootComponent,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
  val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
  SideEffect { root.windowSizeHolder.update(windowSizeClass) }

  val appLanguage by root.appLanguage.subscribeAsState()
  val layoutDirection =
      when (appLanguage) {
        AppLanguage.PERSIAN -> LayoutDirection.Rtl
        AppLanguage.ENGLISH -> LayoutDirection.Ltr
      }

  CompositionLocalProvider(
      LocalLayoutDirection provides layoutDirection,
      LocalAppLanguage provides appLanguage,
  ) {
    BaseTheme {
      if (content != null) {
        content()
      } else {
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
  }
}

@Composable
private fun RootChildContent(child: RootFlowDestination.Instance) {
  when (child) {
    is RootFlowDestination.Splash.Instance -> {
      SplashScreen(modifier = Modifier.testTag("splash-screen"))
    }
    is RootFlowDestination.Main.Instance -> {
      MainScreen(
          component = child.component,
          modifier = Modifier.testTag("main-screen"),
      )
    }
    is RootFlowDestination.PrivacyPolicy.Instance -> {
      PrivacyPolicyScreen(
          component = child.component,
          modifier = Modifier.testTag("privacy-policy-screen"),
      )
    }
    is RootFlowDestination.Home.Instance -> {
      HomeScreen(
          component = child.component,
          modifier = Modifier.testTag("home-screen"),
      )
    }
    is RootFlowDestination.Inbox.Instance -> {
      InboxScreen(
          component = child.component,
          modifier = Modifier.testTag("inbox-screen"),
      )
    }
    is RootFlowDestination.Pockets.Instance -> {
      PocketsScreen(
          component = child.component,
          modifier = Modifier.testTag("pockets-screen"),
      )
    }
    is RootFlowDestination.AddExpenseFlow.Instance -> {
      AddExpenseFlowScreen(
          component = child.component,
          modifier = Modifier.testTag("add-expense-flow-screen"),
      )
    }
    is RootFlowDestination.PocketSettingsFlow.Instance -> {
      PocketSettingsFlowScreen(
          component = child.component,
          modifier = Modifier.testTag("pocket-settings-flow-screen"),
      )
    }
    is RootFlowDestination.PocketDetails.Instance -> {
      PocketDetailsScreen(
          component = child.component,
          modifier = Modifier.testTag("pocket-details-screen"),
      )
    }
  }
}

@ScreenPreviews
@Composable
private fun Preview() {
  RootScreen(FakeRootComponent())
}
