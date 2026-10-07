package com.base.e2e.component

import com.base.component.TestCContext
import com.base.feature.auth.AuthComponent
import com.base.feature.component.assertLogin
import com.base.feature.component.assertWelcome
import com.base.feature.main.MainComponent
import com.base.feature.root.RootComponent
import com.base.feature.root.RootComponentFactory
import com.base.feature.splash.SplashDestination
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import com.base.util.testValue
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.types.shouldBeInstanceOf

class E2EAuthTest : BehaviorSpec() {
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
        And("click on login") {
          beforeEach { root.assertAuth().assertWelcome().onLoginClicked() }
          Then("shows login screen") { root.assertAuth().assertLogin() }
          And("entering valid credentials") {
            beforeEach {
              root.assertAuth().assertLogin().let {
                it.onEmailChanged("valid@example.com")
                it.onPasswordChanged("password123")
                it.onLoginClicked()
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

private fun RootComponent.activeInstance(): Any = childStack.value.active.instance

fun RootComponent.assertSplash() = activeInstance().shouldBeInstanceOf<SplashDestination>()

fun RootComponent.assertAuth() = activeInstance().shouldBeInstanceOf<AuthComponent>()

fun RootComponent.assertMain() = activeInstance().shouldBeInstanceOf<MainComponent>()
