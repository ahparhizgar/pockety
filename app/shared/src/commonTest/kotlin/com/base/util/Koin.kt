package com.base.util

import com.base.DataDir
import com.base.db.AppDatabaseBuilderFactory
import com.base.db.getRoomDatabase
import com.base.di.appModule
import com.base.feature.auth.FakeTokenStorage
import com.base.feature.auth.TokenStorage
import com.base.feature.settings.FakeLanguageSettingsHandler
import com.base.feature.settings.FakeUserSettingsRepository
import com.base.feature.settings.LanguageSettingsHandler
import com.base.feature.settings.UserSettingsRepository
import com.base.feature.sms.FakeSmsImportHistory
import com.base.feature.sms.FakeSmsInboxReader
import com.base.sms.FakeSmsRemoteConfigStore
import com.base.sms.SmsImportHistory
import com.base.sms.SmsInboxReader
import com.base.sms.SmsRemoteConfigStore
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
  single { FakeSmsInboxReader() }.bind<SmsInboxReader>()
  single { FakeSmsImportHistory() }.bind<SmsImportHistory>()
  single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
  single { DataDir.MEMORY }
  single {
    getRoomDatabase(
        get<AppDatabaseBuilderFactory>().createBuilder(dataDir = get()),
        Dispatchers.Main,
    )
  }
  single { FakeTokenStorage() }.bind<TokenStorage>()
  // Tests model an existing user: default categories were already seeded, so
  // the root does not insert them and flows start from an empty collection.
  single { FakeSmsRemoteConfigStore() }.bind<SmsRemoteConfigStore>()
  single { FakeUserSettingsRepository(initialDefaultCategoriesSeeded = true) }
      .bind<UserSettingsRepository>()
  single { FakeLanguageSettingsHandler() }.bind<LanguageSettingsHandler>()

  // Everything is overridden to Main. Main should be set to StandardTestDispatcher.
  factory<CoroutineDispatcher>(named("default")) { Dispatchers.Main }
  factory<CoroutineDispatcher>(named("io")) { Dispatchers.Main }
  factory<CoroutineDispatcher>(named("main")) { Dispatchers.Main }
  factory<CoroutineDispatcher>(named("unconfined")) { Dispatchers.Main }
}
