package com.base.feature.auth.login

import com.arkivanov.decompose.router.stack.StackNavigation
import com.base.component.TestCContext
import com.base.feature.auth.AuthConfig
import com.base.testValue
import com.base.util.FakeCallback
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.maps.shouldHaveKey
import io.kotest.matchers.shouldBe

class LoginTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())
    Given("a login component") {
      val koin by integrationKoin()
      val cContext by testValue { TestCContext().resumed() }
      val navigation by testValue { StackNavigation<AuthConfig>() }
      val onAuthenticated by testValue { FakeCallback<Unit>() }
      var component by testValue {
        koin
            .get<LoginComponentFactory>()
            .create(
                context = cContext,
                navigation = navigation,
                onAuthenticated = onAuthenticated,
            )
      }

      When("using invalid email and password") {
        beforeEach {
          with(component) {
            onEmailChanged("bad-email")
            onPasswordChanged("short")
            onLoginClicked()
          }
        }
        Then("should have validation errors") {
          component.state.value.let { state ->
            state.fieldErrors shouldHaveKey "email"
            state.fieldErrors shouldHaveKey "password"
          }
        }
        Then("should not call onAuthenticated") { onAuthenticated.shouldNotBeCalled() }
      }

      When("using valid email and password") {
        beforeEach {
          with(component) {
            onEmailChanged("valid@example.com")
            onPasswordChanged("password123")
            onLoginClicked()
          }
          testCoroutineScheduler.advanceUntilIdle()
        }
        Then("should call onAuthenticated") { onAuthenticated.shouldBeCalled() }
        Then("submitting state should be false") {
          component.state.value.isSubmitting shouldBe false
        }
      }
    }
  }
}
