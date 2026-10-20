package com.base.capability.root_navigation

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import com.base.capability.adding_expense.AddExpenseFlowComponent
import com.base.capability.adding_expense.AddExpenseFlowComponentFactory
import com.base.capability.main.MainComponent
import com.base.capability.main.MainComponentFactory
import com.base.capability.pocket_management.PocketDetailsComponent
import com.base.capability.pocket_management.PocketDetailsComponentFactory
import com.base.capability.pocket_management.PocketSettingsFlowComponent
import com.base.capability.pocket_management.PocketSettingsFlowComponentFactory
import com.base.capability.sms_inbox.InboxComponent
import com.base.capability.sms_inbox.InboxComponentFactory
import com.base.capability.splash.SplashDestination
import com.base.component.CContext
import com.base.component.WindowSizeHolder
import com.base.component.componentScope
import com.base.navigation.NavigationAction
import com.base.navigation.NavigationActionBus
import com.base.usermessage.MessageHolder
import kotlin.reflect.KClass
import kotlinx.coroutines.launch
import org.koin.core.scope.Scope

interface RootComponent {
  val backHandler: BackHandler
  val childStack: Value<ChildStack<TopLevelDestinationConfig, Any>>
  val messageHolder: MessageHolder
  val windowSizeHolder: WindowSizeHolder

  fun onBack()
}

interface RootComponentFactory {
  fun create(context: CContext): RootComponent
}

class DefaultRootComponent(
    cContext: CContext,
    private val componentProvider: ComponentProvider,
    private val navigationActionBus: NavigationActionBus,
) : RootComponent, CContext by cContext {
  val scope = componentScope()

  private val rootNavigation = StackNavigation<TopLevelDestinationConfig>()
  override val backHandler: BackHandler = cContext.backHandler
  override val messageHolder: MessageHolder = MessageHolder()

  override fun onBack() {
    rootNavigation.pop()
  }

  init {
    @Suppress("UNCHECKED_CAST")
    cContext.navigation = rootNavigation
    messageShower = messageHolder

    scope.launch { rootNavigation.replaceAll(MainComponent.Config) }

    scope.launch {
      navigationActionBus.events.collect { action ->
        when (action) {
          is NavigationAction.OpenAddExpense -> {
            rootNavigation.pushNew(
                AddExpenseFlowComponent.Config(
                    initialAmount = action.initialAmount,
                    candidateExpenseId = action.candidateExpenseId,
                )
            )
          }
        }
      }
    }
  }

  override val childStack: Value<ChildStack<TopLevelDestinationConfig, Any>> =
      cContext.childStack(
          source = rootNavigation,
          serializer = null,
          initialConfiguration = SplashDestination,
          handleBackButton = true,
          childFactory = ::createChild,
      )

  @Suppress("LongMethod")
  private fun createChild(config: TopLevelDestinationConfig, cContext: CContext): Any {
    return when (config) {
      is SplashDestination -> SplashDestination
      is MainComponent.Config ->
          componentProvider.provide(MainComponentFactory::class).create(cContext)
      is InboxComponent.Config ->
          componentProvider
              .provide(InboxComponentFactory::class)
              .create(
                  cContext = cContext,
                  onSelectCandidate = { candidate ->
                    rootNavigation.pop()
                    rootNavigation.pushNew(
                        AddExpenseFlowComponent.Config(
                            initialAmount = candidate.amount.toString(),
                            candidateExpenseId = candidate.id,
                        )
                    )
                  },
                  onBack = { rootNavigation.pop() },
              )
      is AddExpenseFlowComponent.Config ->
          componentProvider
              .provide(AddExpenseFlowComponentFactory::class)
              .create(
                  cContext = cContext,
                  config = config,
                  onCompleted = { rootNavigation.pop() },
                  onCancelled = { rootNavigation.pop() },
              )
      is PocketSettingsFlowComponent.Config ->
          componentProvider
              .provide(PocketSettingsFlowComponentFactory::class)
              .create(
                  cContext = cContext,
                  config = config,
                  onCompleted = { rootNavigation.pop() },
                  onCancelled = { rootNavigation.pop() },
                  onDeleted = { rootNavigation.popWhile { it !is MainComponent.Config } },
              )
      is PocketDetailsComponent.Config ->
          componentProvider
              .provide(PocketDetailsComponentFactory::class)
              .create(
                  cContext = cContext,
                  pocketId = config.pocketId,
                  onBack = { rootNavigation.pop() },
                  onConfigure = { pocketId ->
                    rootNavigation.pushNew(PocketSettingsFlowComponent.Config(pocketId = pocketId))
                  },
              )

      else -> error("Destination not defined in createChild")
    }
  }
}

class DefaultRootComponentFactory(
    private val componentProvider: ComponentProvider,
    private val navigationActionBus: NavigationActionBus,
) : RootComponentFactory {
  override fun create(context: CContext): RootComponent =
      DefaultRootComponent(
          cContext = context,
          componentProvider = componentProvider,
          navigationActionBus = navigationActionBus,
      )
}

class FakeRootComponent : RootComponent {
  override val backHandler: BackHandler = BackDispatcher()
  override val messageHolder: MessageHolder = MessageHolder()
  override val windowSizeHolder: WindowSizeHolder = WindowSizeHolder()

  override fun onBack() {}

  override val childStack: Value<ChildStack<TopLevelDestinationConfig, Any>> =
      MutableValue(
          ChildStack(
              active = Child.Created(SplashDestination, SplashDestination),
              backStack = emptyList(),
          )
      )
}

interface TopLevelDestinationConfig

interface ComponentProvider {
  fun <T : Any> provide(kClass: KClass<T>): T
}

class KoinComponentProvider(private val scope: Scope) : ComponentProvider {
  override fun <T : Any> provide(kClass: KClass<T>): T {
    return scope.get(clazz = kClass)
  }
}
