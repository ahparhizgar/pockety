package com.base.util

import com.base.dto.AuthResult
import com.base.dto.SignUpRequest
import com.base.feature.auth.AuthService
import com.base.baseApp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.ClientProvider
import io.ktor.server.testing.testApplication
import org.koin.ktor.ext.inject

fun testBaseApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
  var token = ""
  application {
    baseApp(isTest = true)

    resetDatabase()

    val authService by inject<AuthService>()
    val auth = authService.signUp("registersdf@example.com", "password", "Amir")
    token = auth.accessToken
  }
  startApplication()
  client = createClientByToken(token)
  block()
}

@Deprecated("use createClient instead")
fun ClientProvider.createClientByToken(token: String): HttpClient = createClient {
  install(ContentNegotiation) { json() }

  install(DefaultRequest) { contentType(ContentType.Application.Json) }

  install(Auth) {
    bearer {
      cacheTokens = false
      loadTokens { BearerTokens(accessToken = token, refreshToken = null) }
    }
  }
}

suspend fun ClientProvider.createClient(name: String = "Other"): HttpClient {
  return createClientWithResult(name).first
}

suspend fun ClientProvider.createClientWithResult(
    name: String = "Other"
): Pair<HttpClient, AuthResult> {
  val otherUser =
      client
          .post("/users") {
            setBody(SignUpRequest("${name.lowercase()}@example.com", "password123", name))
          }
          .body<AuthResult>()
  return Pair(createClientByToken(otherUser.accessToken), otherUser)
}
