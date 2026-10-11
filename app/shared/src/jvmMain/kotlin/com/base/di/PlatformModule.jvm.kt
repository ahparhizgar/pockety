package com.base.di

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.base.DataDir
import com.base.db.AppDatabase
import com.base.db.AppDatabaseBuilderFactory
import java.io.File
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule(): Module = module {
  single { JvmAppDatabaseBuilderFactory() }.bind<AppDatabaseBuilderFactory>()
}

class JvmAppDatabaseBuilderFactory : AppDatabaseBuilderFactory {
  override fun createBuilder(dataDir: DataDir): RoomDatabase.Builder<AppDatabase> {
    val builder =
        if (dataDir == DataDir.MEMORY) {
          Room.inMemoryDatabaseBuilder<AppDatabase>()
        } else {
          Room.databaseBuilder<AppDatabase>(name = File(dataDir.dir, "database.db").absolutePath)
        }
    return builder.setDriver(BundledSQLiteDriver())
  }
}
