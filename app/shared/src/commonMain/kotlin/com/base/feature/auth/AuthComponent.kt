package com.base.feature.auth

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import com.base.component.CContext
import com.base.feature.auth.login.LoginComponent
import com.base.feature.auth.login.LoginComponentFactory
import com.base.feature.auth.reset.ResetPasswordComponent
import com.base.feature.auth.reset.ResetPasswordComponentFactory
import com.base.feature.auth.signup.SignUpComponent
import com.base.feature.auth.signup.SignUpComponentFactory
import com.base.feature.auth.welcome.FakeWelcomeComponent
import com.base.feature.auth.welcome.WelcomeComponent
import com.base.feature.auth.welcome.WelcomeComponentFactory
import com.base.feature.main.MainComponent
import com.base.feature.root.TopLevelDestinationConfig
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthConfig {
  @Serializable data object Welcome : AuthConfig

  @Serializable data object Login : AuthConfig

  @Serializable data object SignUp : AuthConfig

  @Serializable data object ResetPassword : AuthConfig
}

interface AuthComponent : TopLevelDestinationConfig {
  val stack: Value<ChildStack<AuthConfig, Child>>
  val backHandler: BackHandler

  fun onBack() {}

  sealed class Child {
    class Welcome(val component: WelcomeComponent) : Child()

    class Login(val component: LoginComponent) : Child()

    class SignUp(val component: SignUpComponent) : Child()

    class ResetPassword(val component: ResetPasswordComponent) : Child()
  }

  @Serializable data object Config : TopLevelDestinationConfig
}

interface AuthComponentFactory {
  fun create(cContext: CContext): AuthComponent
}

class DefaultAuthComponent(
    context: CContext,
    private val welcomeFactory: WelcomeComponentFactory,
    private val loginFactory: LoginComponentFactory,
    private val signUpFactory: SignUpComponentFactory,
    private val resetPasswordFactory: ResetPasswordComponentFactory,
) : AuthComponent, CContext by context {

  private val authNavigation = StackNavigation<AuthConfig>()
  override val backHandler: BackHandler = context.backHandler

  override fun onBack() {
    authNavigation.pop()
  }

  override val stack: Value<ChildStack<AuthConfig, AuthComponent.Child>> =
      childStack(
          source = authNavigation,
          serializer = null,
          initialConfiguration = AuthConfig.Welcome,
          handleBackButton = true,
          childFactory = ::createChild,
      )

  private fun createChild(config: AuthConfig, context: CContext): AuthComponent.Child {
    return when (config) {
      AuthConfig.Welcome -> AuthComponent.Child.Welcome(welcomeFactory.create(authNavigation))
      AuthConfig.Login ->
          AuthComponent.Child.Login(loginFactory.create(context, authNavigation, ::onAuthenticated))
      AuthConfig.SignUp ->
          AuthComponent.Child.SignUp(
              signUpFactory.create(context, authNavigation, ::onAuthenticated)
          )
      AuthConfig.ResetPassword ->
          AuthComponent.Child.ResetPassword(resetPasswordFactory.create(authNavigation))
    }
  }

  private fun onAuthenticated() {
    navigation.replaceAll(MainComponent.Config)
  }
}

class DefaultAuthComponentFactory(
    private val welcomeFactory: WelcomeComponentFactory,
    private val loginFactory: LoginComponentFactory,
    private val signUpFactory: SignUpComponentFactory,
    private val resetPasswordFactory: ResetPasswordComponentFactory,
) : AuthComponentFactory {
  override fun create(cContext: CContext): AuthComponent =
      DefaultAuthComponent(
          cContext,
          welcomeFactory,
          loginFactory,
          signUpFactory,
          resetPasswordFactory,
      )
}

class FakeAuthComponent(
    override val stack: Value<ChildStack<AuthConfig, AuthComponent.Child>> =
        MutableValue(
            ChildStack(
                active =
                    com.arkivanov.decompose.Child.Created(
                        AuthConfig.Welcome,
                        AuthComponent.Child.Welcome(FakeWelcomeComponent()),
                    ),
                backStack = emptyList(),
            )
        )
) : AuthComponent {
  override val backHandler: BackHandler = BackDispatcher()
}

class FakeAuthComponentFactory : AuthComponentFactory {
  override fun create(cContext: CContext): AuthComponent = FakeAuthComponent()
}
