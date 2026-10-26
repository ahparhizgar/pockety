package com.base.di

import com.base.feature.category.add.AddCategoryComponentFactory
import com.base.feature.category.add.DefaultAddCategoryComponentFactory
import com.base.feature.category.selection.CategorySelectionComponentFactory
import com.base.feature.category.selection.DefaultCategorySelectionComponentFactory
import com.base.feature.expense.AddExpenseFlowComponentFactory
import com.base.feature.expense.DefaultAddExpenseFlowComponentFactory
import com.base.feature.expense.add.AddExpenseComponentFactory
import com.base.feature.expense.add.DefaultAddExpenseComponentFactory
import com.base.feature.home.DefaultHomeComponentFactory
import com.base.feature.home.HomeComponentFactory
import com.base.feature.main.DefaultMainComponentFactory
import com.base.feature.main.MainComponentFactory
import com.base.feature.pocket.DefaultPocketsComponentFactory
import com.base.feature.pocket.PocketsComponentFactory
import com.base.feature.pocket.details.DefaultPocketDetailsComponentFactory
import com.base.feature.pocket.details.PocketDetailsComponentFactory
import com.base.feature.pocket.selection.DefaultSelectPocketComponentFactory
import com.base.feature.pocket.selection.SelectPocketComponentFactory
import com.base.feature.pocket.settings.DefaultPocketSettingsComponentFactory
import com.base.feature.pocket.settings.DefaultPocketSettingsFlowComponentFactory
import com.base.feature.pocket.settings.PocketSettingsComponentFactory
import com.base.feature.pocket.settings.PocketSettingsFlowComponentFactory
import com.base.feature.root.ComponentProvider
import com.base.feature.root.DefaultRootComponentFactory
import com.base.feature.root.KoinComponentProvider
import com.base.feature.root.RootComponentFactory
import com.base.feature.sms.inbox.DefaultInboxComponentFactory
import com.base.feature.sms.inbox.InboxComponentFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun decomposeModule() = module {
  factory { KoinComponentProvider(this) }.bind<ComponentProvider>()
  factoryOf(::DefaultRootComponentFactory).bind<RootComponentFactory>()
  factoryOf(::DefaultMainComponentFactory).bind<MainComponentFactory>()
  factoryOf(::DefaultHomeComponentFactory).bind<HomeComponentFactory>()
  factoryOf(::DefaultAddExpenseComponentFactory).bind<AddExpenseComponentFactory>()
  factoryOf(::DefaultInboxComponentFactory).bind<InboxComponentFactory>()
  factoryOf(::DefaultPocketsComponentFactory).bind<PocketsComponentFactory>()
  factoryOf(::DefaultPocketDetailsComponentFactory).bind<PocketDetailsComponentFactory>()
  factoryOf(::DefaultAddCategoryComponentFactory).bind<AddCategoryComponentFactory>()
  factoryOf(::DefaultCategorySelectionComponentFactory).bind<CategorySelectionComponentFactory>()
  factoryOf(::DefaultSelectPocketComponentFactory).bind<SelectPocketComponentFactory>()
  factoryOf(::DefaultAddExpenseFlowComponentFactory).bind<AddExpenseFlowComponentFactory>()
  factoryOf(::DefaultPocketSettingsComponentFactory).bind<PocketSettingsComponentFactory>()
  factoryOf(::DefaultPocketSettingsFlowComponentFactory).bind<PocketSettingsFlowComponentFactory>()
}
