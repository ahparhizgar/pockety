package com.base.di

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.base.DataDir
import com.base.db.AppDatabase
import com.base.db.AppDatabaseBuilderFactory
import java.io.File
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

class AndroidAppDatabaseBuilderFactory(private val context: Context) : AppDatabaseBuilderFactory {
  override fun createBuilder(dataDir: DataDir): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(dataDir.dir, "opensplit.db")
    return Room.databaseBuilder<AppDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath,
        )
        .setDriver(androidx.sqlite.driver.bundled.BundledSQLiteDriver())
  }
}

actual fun platformModule(): Module = module {
  single { AndroidAppDatabaseBuilderFactory(androidContext()) }.bind<AppDatabaseBuilderFactory>()
}
