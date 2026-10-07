package com.base.feature.main

import com.base.component.CContext
import com.base.feature.root.TopLevelDestinationConfig
import kotlinx.serialization.Serializable

interface MainComponent : TopLevelDestinationConfig {
  @Serializable data object Config : TopLevelDestinationConfig
}

interface MainComponentFactory {
  fun create(cContext: CContext): MainComponent
}

class DefaultMainComponent(
    context: CContext,
) : MainComponent, CContext by context

class DefaultMainComponentFactory : MainComponentFactory {
  override fun create(cContext: CContext): MainComponent = DefaultMainComponent(cContext)
}

class FakeMainComponent : MainComponent

class FakeMainComponentFactory : MainComponentFactory {
  override fun create(cContext: CContext): MainComponent = FakeMainComponent()
}
