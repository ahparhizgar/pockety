package com.base.feature.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.feature.analytics.AnalyticsScreen
import com.base.feature.home.HomeScreen
import com.base.feature.pocket.PocketsScreen
import com.base.feature.settings.SettingsScreen
import com.base.feature.sms.inbox.InboxScreen
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyBottomBar
import com.base.ui.components.PocketyTab

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun MainScreen(
    component: MainComponent,
    modifier: Modifier = Modifier,
) {
  val activeStack by component.stack.subscribeAsState()
  val state by component.state.subscribeAsState()

  val selectedTab =
      when (activeStack.active.configuration) {
        MainChildConfig.Home -> PocketyTab.Home
        MainChildConfig.Inbox -> PocketyTab.Inbox
        MainChildConfig.Pockets -> PocketyTab.Pockets
        MainChildConfig.Analytics -> PocketyTab.Analytics
        MainChildConfig.Settings -> PocketyTab.Settings
      }

  Scaffold(
      modifier = modifier.testTag("main-screen"),
      contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
      bottomBar = {
        PocketyBottomBar(
            selectedTab = selectedTab,
            inboxBadgeCount = state.inboxBadgeCount,
            onTabSelect = component::onTabSelected,
        )
      },
  ) { paddingValues ->
    MainNavigationContent(
        component = component,
        modifier = Modifier.padding(paddingValues),
    )
  }
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
private fun MainNavigationContent(
    component: MainComponent,
    modifier: Modifier = Modifier,
) {
  Children(
      stack = component.stack,
      modifier = modifier,
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

      is MainComponent.Child.Inbox -> {
        InboxScreen(
            component = child.component,
            modifier = Modifier.testTag("inbox-screen"),
        )
      }

      is MainComponent.Child.Pockets -> {
        PocketsScreen(
            component = child.component,
            modifier = Modifier.testTag("pockets-screen"),
        )
      }

      is MainComponent.Child.Analytics -> {
        AnalyticsScreen(
            component = child.component,
            modifier = Modifier.fillMaxSize(),
        )
      }

      is MainComponent.Child.Settings -> {
        SettingsScreen(
            component = child.component,
            modifier = Modifier.fillMaxSize(),
        )
      }
    }
  }
}

@Composable
@ScreenPreviews
private fun MainScreenHomePreview() {
  MainScreen(FakeMainComponent())
}

@Composable
@ScreenPreviews
private fun MainScreenWithBadgePreview() {
  MainScreen(FakeMainComponent(initialState = MainState(inboxBadgeCount = 4)))
}
