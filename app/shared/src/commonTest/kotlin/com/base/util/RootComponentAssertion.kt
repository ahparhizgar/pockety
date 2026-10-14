package com.base.util

import com.base.feature.main.MainComponent
import com.base.feature.root.RootComponent
import io.kotest.matchers.types.shouldBeInstanceOf

val RootComponent.activeConfig
  get() = childStack.value.active.configuration

val RootComponent.activeInstance
  get() = childStack.value.active.instance

fun RootComponent.assertMainComponent(): MainComponent {
  return activeInstance.shouldBeInstanceOf<MainComponent>()
}
