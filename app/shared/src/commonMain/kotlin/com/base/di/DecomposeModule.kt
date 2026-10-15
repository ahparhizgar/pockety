package com.base.di

import com.base.capability.adding_expense.add.AddExpenseComponentFactory
import com.base.capability.adding_expense.add.DefaultAddExpenseComponentFactory
import com.base.capability.home.DefaultHomeComponentFactory
import com.base.capability.home.HomeComponentFactory
import com.base.capability.main.DefaultMainComponentFactory
import com.base.capability.main.MainComponentFactory
import com.base.capability.root_navigation.ComponentProvider
import com.base.capability.root_navigation.DefaultRootComponentFactory
import com.base.capability.root_navigation.KoinComponentProvider
import com.base.capability.root_navigation.RootComponentFactory
import com.base.capability.sms_inbox.DefaultInboxComponentFactory
import com.base.capability.sms_inbox.InboxComponentFactory
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
}
