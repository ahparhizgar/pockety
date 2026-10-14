package com.base.di

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.base.DataDir
import com.base.db.AppDatabase
import com.base.db.AppDatabaseBuilderFactory
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

import com.base.permission.FakePermissionController
import com.base.permission.PermissionController

actual fun platformModule(): Module = module {
  single { IosAppDatabaseBuilderFactory() }.bind<AppDatabaseBuilderFactory>()
  single { FakePermissionController() }.bind<PermissionController>()
}

class IosAppDatabaseBuilderFactory : AppDatabaseBuilderFactory {
  override fun createBuilder(dataDir: DataDir): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(name = "database.db").setDriver(BundledSQLiteDriver())
  }
}
