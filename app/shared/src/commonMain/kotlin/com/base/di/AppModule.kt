package com.base.di

import com.base.datastore.createDataStore
import com.base.db.AppDatabase
import com.base.db.AppDatabaseBuilderFactory
import com.base.db.DefaultTransactionRepository
import com.base.db.TransactionRepository
import com.base.db.getRoomDatabase
import com.base.feature.auth.DataStoreTokenStorage
import com.base.feature.auth.TokenStorage
import com.base.feature.item.ItemApi
import com.base.feature.item.KtorItemApi
import com.base.ktor.createHttpClient
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
  factoryOf(::KtorItemApi).bind<ItemApi>()
  single { CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>(named("default"))) }

  single {
    getRoomDatabase(
        get<AppDatabaseBuilderFactory>().createBuilder(get()),
        get<CoroutineDispatcher>(named("default")),
    )
  }
  single { get<AppDatabase>().itemDao() }
  single { get<AppDatabase>().transactionDao() }
  singleOf(::DefaultTransactionRepository).bind<TransactionRepository>()
}
