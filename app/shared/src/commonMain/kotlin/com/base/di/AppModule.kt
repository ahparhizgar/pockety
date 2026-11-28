package com.base.di

import com.base.datastore.createDataStore
import com.base.db.AppDatabase
import com.base.db.AppDatabaseBuilderFactory
import com.base.db.CandidateExpenseRepository
import com.base.db.CategoryRepository
import com.base.db.DefaultCandidateExpenseRepository
import com.base.db.DefaultCategoryRepository
import com.base.db.DefaultExpenseRepository
import com.base.db.DefaultPocketRepository
import com.base.db.ExpenseRepository
import com.base.db.PocketRepository
import com.base.db.getRoomDatabase
import com.base.feature.auth.DataStoreTokenStorage
import com.base.feature.auth.TokenStorage
import com.base.feature.settings.DataStoreUserSettingsRepository
import com.base.feature.settings.UserSettingsRepository
import com.base.ktor.createHttpClient
import com.base.navigation.DefaultNavigationActionBus
import com.base.navigation.NavigationActionBus
import com.base.sms.DataStoreSmsImportHistory
import com.base.sms.DefaultSmsRemoteConfigStore
import com.base.sms.KtorSmsRemoteConfigFetcher
import com.base.sms.SmsRemoteConfigFetcher
import com.base.sms.SmsRemoteConfigStore
import com.base.sms.InitialSmsImport
import com.base.sms.SmsImportHistory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

fun appModule() = module {
  includes(othersModule(), decomposeModule(), platformModule(), coroutinesModule())
}

fun othersModule() = module {
  factoryOf(::createHttpClient)
  singleOf(::createDataStore)
  singleOf(::InitialSmsImport)
  singleOf(::DataStoreSmsImportHistory).bind<SmsImportHistory>()
  singleOf(::KtorSmsRemoteConfigFetcher).bind<SmsRemoteConfigFetcher>()
  singleOf(::DefaultSmsRemoteConfigStore).bind<SmsRemoteConfigStore>()
  singleOf(::DataStoreTokenStorage).bind<TokenStorage>()
  singleOf(::DataStoreUserSettingsRepository).bind<UserSettingsRepository>()
  single { CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>(named("default"))) }

  single {
    getRoomDatabase(
        get<AppDatabaseBuilderFactory>().createBuilder(get()),
        get<CoroutineDispatcher>(named("default")),
    )
  }
  single { get<AppDatabase>().expenseDao() }
  single { get<AppDatabase>().candidateExpenseDao() }
  single { get<AppDatabase>().categoryDao() }
  single { get<AppDatabase>().pocketDao() }
  singleOf(::DefaultExpenseRepository).bind<ExpenseRepository>()
  singleOf(::DefaultCandidateExpenseRepository).bind<CandidateExpenseRepository>()
  singleOf(::DefaultCategoryRepository).bind<CategoryRepository>()
  singleOf(::DefaultPocketRepository).bind<PocketRepository>()
  singleOf(::DefaultNavigationActionBus).bind<NavigationActionBus>()
}
