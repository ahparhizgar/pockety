package com.base.feature.auth.signup

import com.base.component.TestCContext
import com.base.testValue
import com.base.util.FakeCallback
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldHaveKey

class SignupTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())
    Given("a signup component") {
      val koin by integrationKoin()
      val cContext by testValue { TestCContext().resumed() }
      val onAuthenticated by testValue { FakeCallback<Unit>() }
      var component by testValue {
        koin
            .get<SignUpComponentFactory>()
            .create(
                context = cContext,
                onAuthenticated = onAuthenticated,
                onBack = {},
            )
      }
      When("using invalid email and password") {
        beforeEach {
          with(component) {
            onEmailChanged("bad-email")
            onPasswordChanged("short")
            onDoneClicked()
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
    }
  }
}
