package com.base.di

import com.base.capability.auth.DataStoreTokenStorage
import com.base.capability.auth.TokenStorage
import com.base.datastore.createDataStore
import com.base.db.AppDatabase
import com.base.db.AppDatabaseBuilderFactory
import com.base.db.CandidateExpenseRepository
import com.base.db.CategoryRepository
import com.base.db.DefaultCandidateExpenseRepository
import com.base.db.DefaultCategoryRepository
import com.base.db.DefaultExpenseRepository
import com.base.db.ExpenseRepository
import com.base.db.getRoomDatabase
import com.base.ktor.createHttpClient
import com.base.navigation.DefaultNavigationActionBus
import com.base.navigation.NavigationActionBus
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
  singleOf(::DataStoreTokenStorage).bind<TokenStorage>()
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
  singleOf(::DefaultExpenseRepository).bind<ExpenseRepository>()
  singleOf(::DefaultCandidateExpenseRepository).bind<CandidateExpenseRepository>()
  singleOf(::DefaultCategoryRepository).bind<CategoryRepository>()
  singleOf(::DefaultNavigationActionBus).bind<NavigationActionBus>()
}
