package com.base.feature.auth

import com.base.feature.main.MainComponent
import com.base.feature.root.RootComponent
import com.base.feature.splash.SplashDestination
import io.kotest.matchers.types.shouldBeInstanceOf

private fun AuthComponent.activeInstance(): AuthComponent.Child = stack.value.active.instance

fun AuthComponent.assertWelcome() =
    activeInstance().shouldBeInstanceOf<AuthComponent.Child.Welcome>().component

fun AuthComponent.assertSignUp() =
    activeInstance().shouldBeInstanceOf<AuthComponent.Child.SignUp>().component

fun AuthComponent.assertLogin() =
    activeInstance().shouldBeInstanceOf<AuthComponent.Child.Login>().component

fun AuthComponent.assertResetPassword() =
    activeInstance().shouldBeInstanceOf<AuthComponent.Child.ResetPassword>().component

private fun RootComponent.activeInstance(): Any = childStack.value.active.instance

fun RootComponent.assertSplash() = activeInstance().shouldBeInstanceOf<SplashDestination>()

fun RootComponent.assertAuth() = activeInstance().shouldBeInstanceOf<AuthComponent>()

fun RootComponent.assertMain() = activeInstance().shouldBeInstanceOf<MainComponent>()
