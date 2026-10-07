package com.base.feature.auth

import io.ktor.server.application.Application

fun Application.authModule() {
  configureAuthRoutes()
}
