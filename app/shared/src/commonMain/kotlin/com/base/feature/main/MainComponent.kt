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
import com.base.feature.expense.add.AddExpenseComponent
import com.base.feature.expense.add.AddExpenseComponentFactory
import com.base.feature.home.FakeHomeComponent
import com.base.feature.home.HomeComponent
import com.base.feature.home.HomeComponentFactory
import com.base.feature.root.TopLevelDestinationConfig
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainChildConfig {
  @Serializable data object Home : MainChildConfig

  @Serializable
  data class AddExpense(
      val initialAmount: String = "",
      val candidateExpenseId: Long? = null,
  ) : MainChildConfig
}

interface MainComponent : TopLevelDestinationConfig {
  val stack: Value<ChildStack<MainChildConfig, Child>>
  val backHandler: BackHandler

  fun onBack()

  sealed interface Child {
    class Home(val component: HomeComponent) : Child

    class AddExpense(val component: AddExpenseComponent) : Child
  }

  @Serializable data object Config : TopLevelDestinationConfig
}

interface MainComponentFactory {
  fun create(cContext: CContext): MainComponent
}

class DefaultMainComponent(
    cContext: CContext,
    private val homeComponentFactory: HomeComponentFactory,
    private val addExpenseComponentFactory: AddExpenseComponentFactory,
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
                  onAddExpense = { mainNavigation.pushNew(MainChildConfig.AddExpense()) },
              )
          )

      is MainChildConfig.AddExpense ->
          MainComponent.Child.AddExpense(
              addExpenseComponentFactory.create(
                  cContext = context,
                  config =
                      AddExpenseComponent.Config(
                          initialAmount = config.initialAmount,
                          candidateExpenseId = config.candidateExpenseId,
                      ),
                  onCompleted = { mainNavigation.pop() },
                  onCancelled = { mainNavigation.pop() },
              )
          )
    }
  }
}

class DefaultMainComponentFactory(
    private val homeComponentFactory: HomeComponentFactory,
    private val addExpenseComponentFactory: AddExpenseComponentFactory,
) : MainComponentFactory {
  override fun create(cContext: CContext): MainComponent =
      DefaultMainComponent(cContext, homeComponentFactory, addExpenseComponentFactory)
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
