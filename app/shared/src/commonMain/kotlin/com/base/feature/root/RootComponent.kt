package com.base.feature.root

import com.arkivanov.decompose.Child
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
import com.base.component.WindowSizeHolder
import com.base.component.componentScope
import com.base.feature.auth.AuthComponentFactory
import com.base.feature.auth.TokenStorage
import com.base.feature.item.ItemListComponentFactory
import com.base.feature.main.MainComponentFactory
import com.base.usermessage.MessageHolder
import kotlin.reflect.KClass
import kotlinx.coroutines.launch
import org.koin.core.scope.Scope

interface RootComponent {
  val backHandler: BackHandler
  val childStack: Value<ChildStack<RootFlowDestination, RootFlowDestination.Instance>>
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
    private val tokenStorage: TokenStorage,
) : RootComponent, CContext by cContext {
  val scope = componentScope()

  private val rootNavigation = StackNavigation<RootFlowDestination>()
  override val backHandler: BackHandler = cContext.backHandler
  override val messageHolder: MessageHolder = MessageHolder()

  override fun onBack() {
    rootNavigation.pop()
  }

  init {
    cContext.navigation = rootNavigation
    messageShower = messageHolder

    scope.launch {
      val token = tokenStorage.getAccessToken()
      if (!token.isNullOrEmpty()) {
        rootNavigation.replaceAll(RootFlowDestination.Main)
      } else {
        rootNavigation.replaceAll(RootFlowDestination.Auth)
      }
    }
  }

  override val childStack: Value<ChildStack<RootFlowDestination, RootFlowDestination.Instance>> =
      cContext.childStack(
          source = rootNavigation,
          serializer = null,
          initialConfiguration = RootFlowDestination.Splash,
          handleBackButton = true,
          childFactory = ::createChild,
      )

  private fun createChild(
      config: RootFlowDestination,
      cContext: CContext,
  ): RootFlowDestination.Instance {
    return when (config) {
      is RootFlowDestination.Splash -> RootFlowDestination.Splash.Instance
      is RootFlowDestination.Auth ->
          RootFlowDestination.Auth.Instance(
              componentProvider.provide(AuthComponentFactory::class).create(cContext)
          )
      is RootFlowDestination.Main ->
          RootFlowDestination.Main.Instance(
              componentProvider.provide(MainComponentFactory::class).create(cContext)
          )
      is RootFlowDestination.ItemList ->
          RootFlowDestination.ItemList.Instance(
              componentProvider.provide(ItemListComponentFactory::class).create(cContext)
          )
    }
  }
}

class DefaultRootComponentFactory(
    private val componentProvider: ComponentProvider,
    private val tokenStorage: TokenStorage,
) : RootComponentFactory {
  override fun create(context: CContext): RootComponent =
      DefaultRootComponent(
          cContext = context,
          componentProvider = componentProvider,
          tokenStorage = tokenStorage,
      )
}

class FakeRootComponent : RootComponent {
  override val backHandler: BackHandler = BackDispatcher()
  override val messageHolder: MessageHolder = MessageHolder()
  override val windowSizeHolder: WindowSizeHolder = WindowSizeHolder()

  override fun onBack() {}

  override val childStack: Value<ChildStack<RootFlowDestination, RootFlowDestination.Instance>> =
      MutableValue(
          ChildStack(
              active =
                  Child.Created(RootFlowDestination.Splash, RootFlowDestination.Splash.Instance),
              backStack = emptyList(),
          )
      )
}

interface ComponentProvider {
  fun <T : Any> provide(kClass: KClass<T>): T
}

class KoinComponentProvider(private val scope: Scope) : ComponentProvider {
  override fun <T : Any> provide(kClass: KClass<T>): T {
    return scope.get(clazz = kClass)
  }
}
