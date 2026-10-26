package com.base.util

import com.base.DataDir
import com.base.db.AppDatabaseBuilderFactory
import com.base.db.getRoomDatabase
import com.base.di.appModule
import com.base.feature.auth.FakeTokenStorage
import com.base.feature.auth.TokenStorage
import com.base.testValue
import io.kotest.core.spec.Spec
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module

fun Spec.integrationKoin() = testValue {
  koinApplication {
    allowOverride(true)
    modules(
        appModule(),
        integrationTestModule(),
    )
  }
      .koin
}

fun uiKoin() = koinApplication {
  allowOverride(true)
  modules(
      appModule(),
      integrationTestModule(),
  )
}
    .koin

fun integrationTestModule() = module {
  single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
  single { DataDir.MEMORY }
  single {
    getRoomDatabase(
        get<AppDatabaseBuilderFactory>().createBuilder(dataDir = get()),
        Dispatchers.Main,
    )
  }
  single { FakeTokenStorage() }.bind<TokenStorage>()

  // Everything is overridden to Main. Main should be set to StandardTestDispatcher.
  factory<CoroutineDispatcher>(named("default")) { Dispatchers.Main }
  factory<CoroutineDispatcher>(named("io")) { Dispatchers.Main }
  factory<CoroutineDispatcher>(named("main")) { Dispatchers.Main }
  factory<CoroutineDispatcher>(named("unconfined")) { Dispatchers.Main }
}
