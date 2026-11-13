package com.base.di

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.base.DataDir
import com.base.db.AppDatabase
import com.base.db.AppDatabaseBuilderFactory
import com.base.feature.settings.DefaultLanguageSettingsHandler
import com.base.feature.settings.LanguageSettingsHandler
import com.base.notification.ExpenseNotificationManager
import com.base.notification.FakeExpenseNotificationManager
import com.base.permission.FakePermissionController
import com.base.permission.PermissionController
import java.io.File
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule(): Module = module {
  single { JvmAppDatabaseBuilderFactory() }.bind<AppDatabaseBuilderFactory>()
  single { FakePermissionController() }.bind<PermissionController>()
  single { FakeExpenseNotificationManager() }.bind<ExpenseNotificationManager>()
  single { DefaultLanguageSettingsHandler() }.bind<LanguageSettingsHandler>()
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
