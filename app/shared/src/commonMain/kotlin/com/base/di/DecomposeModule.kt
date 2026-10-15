package com.base.di

import com.base.feature.expense.add.AddExpenseComponentFactory
import com.base.feature.expense.add.DefaultAddExpenseComponentFactory
import com.base.feature.home.DefaultHomeComponentFactory
import com.base.feature.home.HomeComponentFactory
import com.base.feature.inbox.DefaultInboxComponentFactory
import com.base.feature.inbox.InboxComponentFactory
import com.base.feature.item.DefaultItemListComponentFactory
import com.base.feature.item.ItemListComponentFactory
import com.base.feature.main.DefaultMainComponentFactory
import com.base.feature.main.MainComponentFactory
import com.base.feature.root.ComponentProvider
import com.base.feature.root.DefaultRootComponentFactory
import com.base.feature.root.KoinComponentProvider
import com.base.feature.root.RootComponentFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun decomposeModule() = module {
  factory { KoinComponentProvider(this) }.bind<ComponentProvider>()
  factoryOf(::DefaultRootComponentFactory).bind<RootComponentFactory>()
  factoryOf(::DefaultMainComponentFactory).bind<MainComponentFactory>()
  factoryOf(::DefaultHomeComponentFactory).bind<HomeComponentFactory>()
  factoryOf(::DefaultAddExpenseComponentFactory).bind<AddExpenseComponentFactory>()
  factoryOf(::DefaultItemListComponentFactory).bind<ItemListComponentFactory>()
  factoryOf(::DefaultInboxComponentFactory).bind<InboxComponentFactory>()
}
