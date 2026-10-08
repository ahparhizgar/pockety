package com.base.feature.auth.signup

import com.base.component.TestCContext
import com.base.feature.auth.assertAuth
import com.base.feature.auth.assertMain
import com.base.feature.auth.assertSignUp
import com.base.feature.auth.assertSplash
import com.base.feature.auth.assertWelcome
import com.base.feature.root.RootComponentFactory
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler

class SignUPE2eComponentTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())
    Given("app opens for first time") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      var root by testValue { koin.get<RootComponentFactory>().create(context) }
      Then("shows welcome screen") { root.assertSplash() }
      When("waiting") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }
        Then("shows auth screen") { root.assertAuth().assertWelcome() }
        And("click on sign up") {
          beforeEach { root.assertAuth().assertWelcome().onSignUpClicked() }
          Then("shows sign up screen") { root.assertAuth().assertSignUp() }
          And("entering valid credentials") {
            beforeEach {
              root.assertAuth().assertSignUp().let {
                it.onEmailChanged("valid@example.com")
                it.onPasswordChanged("password123")
                it.onDoneClicked()
              }
              testCoroutineScheduler.advanceUntilIdle()
            }
            Then("navigates to MainComponent") { root.assertMain() }
          }
        }
      }
    }
  }
}
