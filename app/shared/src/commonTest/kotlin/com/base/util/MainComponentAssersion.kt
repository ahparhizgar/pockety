package com.base.util

import com.base.feature.home.HomeComponent
import com.base.feature.main.MainComponent
import io.kotest.matchers.types.shouldBeInstanceOf

val MainComponent.activeInstance
  get() = stack.value.active.instance

fun MainComponent.assertHomeComponent(): HomeComponent {
  return activeInstance.shouldBeInstanceOf<MainComponent.Child.Home>().component
}
