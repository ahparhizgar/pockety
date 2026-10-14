package com.base.feature.main

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import com.base.component.CContext
import com.base.feature.home.FakeHomeComponent
import com.base.feature.home.HomeComponent
import com.base.feature.home.HomeComponentFactory
import com.base.feature.root.TopLevelDestinationConfig
import com.base.feature.transaction.add.AddTransactionComponent
import com.base.feature.transaction.add.AddTransactionComponentFactory
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainChildConfig {
  @Serializable data object Home : MainChildConfig

  @Serializable data object AddTransaction : MainChildConfig
}

interface MainComponent : TopLevelDestinationConfig {
  val stack: Value<ChildStack<MainChildConfig, Child>>
  val backHandler: BackHandler

  fun onBack()

  sealed interface Child {
    class Home(val component: HomeComponent) : Child

    class AddTransaction(val component: AddTransactionComponent) : Child
  }

  @Serializable data object Config : TopLevelDestinationConfig
}

interface MainComponentFactory {
  fun create(cContext: CContext): MainComponent
}

class DefaultMainComponent(
    cContext: CContext,
    private val homeComponentFactory: HomeComponentFactory,
    private val addTransactionComponentFactory: AddTransactionComponentFactory,
) : MainComponent, CContext by cContext {
  private val mainNavigation = StackNavigation<MainChildConfig>()
  override val backHandler: BackHandler = cContext.backHandler

  override fun onBack() {
    mainNavigation.pop()
  }

  override val stack: Value<ChildStack<MainChildConfig, MainComponent.Child>> =
      childStack(
          source = mainNavigation,
          serializer = null,
          initialConfiguration = MainChildConfig.Home,
          handleBackButton = true,
          childFactory = ::createChild,
      )

  private fun createChild(config: MainChildConfig, context: CContext): MainComponent.Child {
    return when (config) {
      MainChildConfig.Home ->
          MainComponent.Child.Home(
              homeComponentFactory.create(
                  cContext = context,
                  onAddTransaction = { mainNavigation.pushNew(MainChildConfig.AddTransaction) },
              )
          )

      MainChildConfig.AddTransaction ->
          MainComponent.Child.AddTransaction(
              addTransactionComponentFactory.create(
                  cContext = context,
                  onCompleted = { mainNavigation.pop() },
                  onCancelled = { mainNavigation.pop() },
              )
          )
    }
  }
}

class DefaultMainComponentFactory(
    private val homeComponentFactory: HomeComponentFactory,
    private val addTransactionComponentFactory: AddTransactionComponentFactory,
) : MainComponentFactory {
  override fun create(cContext: CContext): MainComponent =
      DefaultMainComponent(cContext, homeComponentFactory, addTransactionComponentFactory)
}

class FakeMainComponent(
    override val stack: Value<ChildStack<MainChildConfig, MainComponent.Child>> =
        MutableValue(
            ChildStack(
                active =
                    Child.Created(
                        MainChildConfig.Home,
                        MainComponent.Child.Home(FakeHomeComponent()),
                    ),
                backStack = emptyList(),
            )
        )
) : MainComponent {
  override val backHandler: BackHandler = BackDispatcher()

  override fun onBack() {}
}

class FakeMainComponentFactory : MainComponentFactory {
  override fun create(cContext: CContext): MainComponent = FakeMainComponent()
}
