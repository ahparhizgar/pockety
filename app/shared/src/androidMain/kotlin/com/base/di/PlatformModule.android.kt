package com.base.di

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.base.DataDir
import com.base.db.AppDatabase
import com.base.db.AppDatabaseBuilderFactory
import com.base.permission.AndroidPermissionController
import com.base.permission.PermissionController
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule(): Module = module {
  single { AndroidAppDatabaseBuilderFactory(androidContext()) }.bind<AppDatabaseBuilderFactory>()
  single { AndroidPermissionController(androidContext()) }.bind<PermissionController>()
}

class AndroidAppDatabaseBuilderFactory(private val context: Context) : AppDatabaseBuilderFactory {
  override fun createBuilder(dataDir: DataDir): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(
            context = context.applicationContext,
            name = "database.db",
        )
        .setDriver(BundledSQLiteDriver())
  }
}
