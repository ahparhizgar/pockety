package com.base.util

import com.base.feature.analytics.AnalyticsComponent
import com.base.feature.home.HomeComponent
import com.base.feature.main.MainComponent
import com.base.feature.pocket.PocketsComponent
import com.base.feature.settings.SettingsComponent
import com.base.feature.sms.inbox.InboxComponent
import io.kotest.matchers.types.shouldBeInstanceOf

private val MainComponent.mainActiveInstance
  get() = stack.value.active.instance

fun MainComponent.assertHomeComponent(): HomeComponent {
  return mainActiveInstance.shouldBeInstanceOf<MainComponent.Child.Home>().component
}

fun MainComponent.assertInboxComponent(): InboxComponent {
  return mainActiveInstance.shouldBeInstanceOf<MainComponent.Child.Inbox>().component
}

fun MainComponent.assertPocketsComponent(): PocketsComponent {
  return mainActiveInstance.shouldBeInstanceOf<MainComponent.Child.Pockets>().component
}

fun MainComponent.assertAnalyticsComponent(): AnalyticsComponent {
  return mainActiveInstance.shouldBeInstanceOf<MainComponent.Child.Analytics>().component
}

fun MainComponent.assertSettingsComponent(): SettingsComponent {
  return mainActiveInstance.shouldBeInstanceOf<MainComponent.Child.Settings>().component
}
