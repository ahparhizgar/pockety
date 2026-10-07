package com.base.di

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import com.base.DataDir
import com.base.db.AppDatabase
import com.base.db.AppDatabaseBuilderFactory
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.w3c.dom.Worker

actual fun platformModule(): Module = module {
  single { WasmAppDatabaseBuilderFactory() }.bind<AppDatabaseBuilderFactory>()
}

class WasmAppDatabaseBuilderFactory : AppDatabaseBuilderFactory {
  override fun createBuilder(dataDir: DataDir): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(name = "opensplit.db")
        .setDriver(createSQLiteWasmWorker())
  }
}

fun createSQLiteWasmWorker() = WebWorkerSQLiteDriver(jsWorker())

@OptIn(ExperimentalWasmJsInterop::class)
private fun jsWorker(): Worker =
    js("""new Worker(new URL("sqlite-wasm-worker/worker.js", import.meta.url))""")
