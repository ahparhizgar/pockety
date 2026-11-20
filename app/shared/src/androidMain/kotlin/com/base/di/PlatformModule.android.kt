package com.base.di

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.base.DataDir
import com.base.db.AppDatabase
import com.base.db.AppDatabaseBuilderFactory
import com.base.feature.settings.DefaultLanguageSettingsHandler
import com.base.feature.settings.LanguageSettingsHandler
import com.base.permission.AndroidPermissionController
import com.base.permission.PermissionController
import com.base.sms.AndroidSmsInboxReader
import com.base.sms.SmsInboxReader
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule(): Module = module {
  single { AndroidSmsInboxReader(androidContext(), get<CoroutineDispatcher>(named("io"))) }
      .bind<SmsInboxReader>()
  single { AndroidAppDatabaseBuilderFactory(androidContext()) }.bind<AppDatabaseBuilderFactory>()
  single { AndroidPermissionController(androidContext()) }.bind<PermissionController>()
  single { DefaultLanguageSettingsHandler() }.bind<LanguageSettingsHandler>()
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
