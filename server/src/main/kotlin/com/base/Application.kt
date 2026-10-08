package com.base

import com.base.database.DatabaseInitializer
import com.base.feature.auth.authModule
import com.base.feature.item.itemRoutes
import com.base.plugins.configureDependencies
import com.base.plugins.configureHTTP
import com.base.plugins.configureSecurity
import com.base.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.inject

fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::openSplit)
      .start(wait = true)
}

fun Application.openSplit(isTest: Boolean = false) {
  configureHTTP(isTest)
  configureDependencies(isTest)
  configureSerialization()
  configureSecurity()

  val initializer by inject<DatabaseInitializer>()
  initializer.init()

  authModule()
  itemRoutes()
}
