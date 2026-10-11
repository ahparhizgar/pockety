package com.base.util

import com.base.database.Items
import com.base.database.Users
import com.base.baseApp
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestType
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ClientProvider
import io.ktor.server.testing.TestApplication
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/** BehaviorSpec with integrated Ktor TestApplication lifecycle per test. */
abstract class KtorBehaviorSpec(body: KtorBehaviorSpec.() -> Unit = {}) :
    BehaviorSpec(), ClientProvider {
  var testApplication: TestApplication? = null
  var defaultClient: HttpClient? = null

  override val client: HttpClient
    get() = requireNotNull(defaultClient) { "TestApplication is not initialized" }

  override fun createClient(
      block: HttpClientConfig<out HttpClientEngineConfig>.() -> Unit
  ): HttpClient {
    return testApplication?.createClient(block)
        ?: throw IllegalStateException("TestApplication is not initialized")
  }

  init {
    beforeEach { testCase ->
      if (testCase.type == TestType.Test) {
        testApplication = TestApplication { application { baseApp(isTest = true) } }
        testApplication!!.start()
        resetDatabase()
        defaultClient =
            testApplication!!.createClient {
              install(ContentNegotiation) { json() }
              install(DefaultRequest) { contentType(ContentType.Application.Json) }
            }
        defaultClient = createClient("UserDefault")
      }
    }

    afterEach { (testCase, _) ->
      if (testCase.type == TestType.Test) {
        testApplication?.stop()
        testApplication = null
      }
    }

    body()
  }
}

fun resetDatabase() {
  transaction {
    Users.deleteAll()
    Items.deleteAll()
  }
}
