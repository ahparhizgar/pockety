package com.base.feature.auth.login

import com.base.component.TestCContext
import com.base.feature.auth.TokenStorage
import com.base.feature.auth.assertAuth
import com.base.feature.auth.assertLogin
import com.base.feature.auth.assertWelcome
import com.base.feature.root.RootComponentFactory
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.nulls.shouldNotBeNull

class LoginFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("unauthenticated user on cold start") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val tokenStorage by testValue { koin.get<TokenStorage>() }
      var root by testValue { koin.get<RootComponentFactory>().create(context) }

      When("waiting on cold start") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("shows auth welcome screen") { root.assertAuth().assertWelcome() }

        And("clicking login") {
          beforeEach { root.assertAuth().assertWelcome().onLoginClicked() }

          Then("shows login screen") { root.assertAuth().assertLogin() }

          And("submitting valid credentials") {
            beforeEach {
              with(root.assertAuth().assertLogin()) {
                onEmailChanged("existinguser@example.com")
                onPasswordChanged("password123")
                onLoginClicked()
              }
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("token is stored and profile is persisted") {
              tokenStorage.getAccessToken().shouldNotBeNull()
            }
          }
        }
      }
    }
  }
}
