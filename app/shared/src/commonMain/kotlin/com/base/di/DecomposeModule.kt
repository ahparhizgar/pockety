package com.base.di

import com.base.feature.auth.AuthComponentFactory
import com.base.feature.auth.DefaultAuthComponentFactory
import com.base.feature.auth.login.DefaultLoginComponentFactory
import com.base.feature.auth.login.LoginComponentFactory
import com.base.feature.auth.signup.DefaultSignUpComponentFactory
import com.base.feature.auth.signup.SignUpComponentFactory
import com.base.feature.auth.welcome.DefaultWelcomeComponentFactory
import com.base.feature.auth.welcome.WelcomeComponentFactory
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
  factoryOf(::DefaultAuthComponentFactory).bind<AuthComponentFactory>()
  factoryOf(::DefaultWelcomeComponentFactory).bind<WelcomeComponentFactory>()
  factoryOf(::DefaultLoginComponentFactory).bind<LoginComponentFactory>()
  factoryOf(::DefaultSignUpComponentFactory).bind<SignUpComponentFactory>()
  factoryOf(::DefaultMainComponentFactory).bind<MainComponentFactory>()
  factoryOf(::DefaultItemListComponentFactory).bind<ItemListComponentFactory>()
}
