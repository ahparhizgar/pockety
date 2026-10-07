package com.base.e2e.component

import com.base.component.TestCContext
import com.base.feature.auth.TokenStorage
import com.base.feature.component.assertSignUp
import com.base.feature.component.assertWelcome
import com.base.feature.root.RootComponentFactory
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import com.base.util.testValue
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.nulls.shouldNotBeNull

class SignUpFlowE2eTest : BehaviorSpec() {
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

        And("clicking sign up") {
          beforeEach { root.assertAuth().assertWelcome().onSignUpClicked() }

          Then("shows sign up screen") { root.assertAuth().assertSignUp() }

          And("submitting valid credentials") {
            beforeEach {
              with(root.assertAuth().assertSignUp()) {
                onEmailChanged("newuser@example.com")
                onPasswordChanged("securePassword123")
                onDoneClicked()
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
