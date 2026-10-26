package com.base.feature.auth

import com.base.feature.root.RootComponent
import com.base.feature.root.RootFlowDestination
import io.kotest.matchers.types.shouldBeInstanceOf

private fun AuthComponent.activeInstance(): AuthComponent.Child = stack.value.active.instance

fun AuthComponent.assertWelcome() =
    activeInstance().shouldBeInstanceOf<AuthComponent.Child.Welcome>().component

fun AuthComponent.assertSignUp() =
    activeInstance().shouldBeInstanceOf<AuthComponent.Child.SignUp>().component

fun AuthComponent.assertLogin() =
    activeInstance().shouldBeInstanceOf<AuthComponent.Child.Login>().component

private fun RootComponent.activeInstance(): Any = childStack.value.active.instance

fun RootComponent.assertSplash() =
    activeInstance().shouldBeInstanceOf<RootFlowDestination.Splash.Instance>()

fun RootComponent.assertAuth() =
    activeInstance().shouldBeInstanceOf<RootFlowDestination.Auth.Instance>().component

fun RootComponent.assertMain() =
    activeInstance().shouldBeInstanceOf<RootFlowDestination.Main.Instance>().component
