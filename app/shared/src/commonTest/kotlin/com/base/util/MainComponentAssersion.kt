package com.base.util

import com.base.capability.home.HomeComponent
import com.base.capability.main.MainComponent
import com.base.capability.sms_inbox.InboxComponent
import io.kotest.matchers.types.shouldBeInstanceOf

val MainComponent.activeInstance
  get() = stack.value.active.instance

fun MainComponent.assertHomeComponent(): HomeComponent {
  return activeInstance.shouldBeInstanceOf<MainComponent.Child.Home>().component
}

fun MainComponent.assertInboxComponent(): InboxComponent {
  return activeInstance.shouldBeInstanceOf<MainComponent.Child.Inbox>().component
}
