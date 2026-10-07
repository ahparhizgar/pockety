package com.base.feature.component

import com.base.component.TestCContext
import com.base.feature.auth.AuthComponent
import com.base.feature.auth.AuthComponentFactory
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import com.base.util.testValue
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.beEmpty
import io.kotest.matchers.shouldNot
import io.kotest.matchers.types.shouldBeInstanceOf

class AuthComponentTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())
    Given("an Auth component") {
      val koin by integrationKoin()
      val cContext by testValue { TestCContext().resumed() }
      var component by testValue { koin.get<AuthComponentFactory>().create(cContext) }

      When("navigating to sign up and using invalid input") {
        beforeEach {
          component.assertWelcome().onSignUpClicked()

          with(component.assertSignUp()) {
            onEmailChanged("bad-email")
            onPasswordChanged("short")
            onDoneClicked()
          }
        }
        Then("shows validation errors") {
          component.assertSignUp().state.value.let { state ->
            state.fieldErrors shouldNot beEmpty()
          }
        }
      }
    }
  }
}

private fun AuthComponent.activeInstance(): AuthComponent.Child = stack.value.active.instance

fun AuthComponent.assertWelcome() =
    activeInstance().shouldBeInstanceOf<AuthComponent.Child.Welcome>().component

fun AuthComponent.assertSignUp() =
    activeInstance().shouldBeInstanceOf<AuthComponent.Child.SignUp>().component

fun AuthComponent.assertLogin() =
    activeInstance().shouldBeInstanceOf<AuthComponent.Child.Login>().component

fun AuthComponent.assertResetPassword() =
    activeInstance().shouldBeInstanceOf<AuthComponent.Child.ResetPassword>().component
