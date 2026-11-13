package com.base

import android.app.Application
import com.base.di.appModule
import com.base.notification.AndroidExpenseNotificationManager
import com.base.notification.ExpenseNotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Application : Application() {
  override fun onCreate() {
    super.onCreate()

    startKoin {
      androidContext(this@Application)
      modules(appModule())
      modules(
          module {
            single { DataDir("${applicationContext.filesDir.path}/A") }
            single<ExpenseNotificationManager> {
              AndroidExpenseNotificationManager(androidContext())
            }
          }
      )
    }
  }
}
