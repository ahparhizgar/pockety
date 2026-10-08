package com.base.feature.main

import com.base.component.CContext
import com.base.feature.item.FakeItemListComponent
import com.base.feature.item.ItemListComponent
import com.base.feature.item.ItemListComponentFactory
import com.base.feature.root.TopLevelDestinationConfig
import kotlinx.serialization.Serializable

interface MainComponent : TopLevelDestinationConfig {
  val itemListComponent: ItemListComponent

  @Serializable data object Config : TopLevelDestinationConfig
}

interface MainComponentFactory {
  fun create(cContext: CContext): MainComponent
}

class DefaultMainComponent(
    context: CContext,
    itemListComponentFactory: ItemListComponentFactory,
) : MainComponent, CContext by context {
  override val itemListComponent: ItemListComponent = itemListComponentFactory.create(context)
}

class DefaultMainComponentFactory(
    private val itemListComponentFactory: ItemListComponentFactory,
) : MainComponentFactory {
  override fun create(cContext: CContext): MainComponent =
      DefaultMainComponent(cContext, itemListComponentFactory)
}

class FakeMainComponent(
    override val itemListComponent: ItemListComponent = FakeItemListComponent(),
) : MainComponent

class FakeMainComponentFactory : MainComponentFactory {
  override fun create(cContext: CContext): MainComponent = FakeMainComponent()
}
