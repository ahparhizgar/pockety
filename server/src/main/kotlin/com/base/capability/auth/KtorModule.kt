package com.base.capability.auth

import io.ktor.server.application.Application

fun Application.authModule() {
  configureAuthRoutes()
}
