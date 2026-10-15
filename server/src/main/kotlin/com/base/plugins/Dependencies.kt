package com.base.plugins

import com.base.capability.auth.authKoinModule
import com.base.capability.auth.testAuthKoinModule
import com.base.config.AppConfig
import com.base.config.DatabaseConfig
import com.base.database.DatabaseInitializer
import com.base.database.connectDatabase
import com.base.database.createHikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureDependencies(isTest: Boolean) {
  install(Koin) {
    modules(
        module {
          single<AppConfig> {
            if (isTest) {
              AppConfig.test()
            } else {
              AppConfig.fromEnvironment(developmentMode)
            }
          }
          single<DatabaseConfig> {
            if (isTest) {
              DatabaseConfig.test()
            } else {
              DatabaseConfig.fromEnvironment()
            }
          }
          single { createHikariDataSource(get()) }
          single { connectDatabase(get()) }
          single { DatabaseInitializer(get()) }
        },
        authKoinModule(),
    )
    if (isTest) {
      modules(testAuthKoinModule())
    }
  }
}
