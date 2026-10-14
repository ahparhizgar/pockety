package com.base.component

import androidx.window.core.layout.WindowSizeClass
import com.ahparhizgar.katch.ApiCallError
import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ComponentContextFactory
import com.arkivanov.decompose.GenericComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.InstanceKeeperDispatcher
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import com.base.permission.FakePermissionController
import com.base.permission.PermissionController
import com.base.remote.userMessage
import com.base.usermessage.MessageHolder
import com.base.usermessage.MessageShower
import com.base.usermessage.SnackbarMessage
import com.base.usermessage.UserMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WindowSizeHolder(
    initialWindowSizeClass: WindowSizeClass = WindowSizeClass(0, 0),
) {
  private val _windowSizeClass = MutableStateFlow(initialWindowSizeClass)
  val windowSizeClass: StateFlow<WindowSizeClass> = _windowSizeClass.asStateFlow()

  fun update(windowSizeClass: WindowSizeClass) {
    _windowSizeClass.value = windowSizeClass
  }
}

interface CContext : GenericComponentContext<CContext> {
  var navigation: StackNavigation<Any>
  var messageShower: MessageShower
  var windowSizeHolder: WindowSizeHolder
  var permissionController: PermissionController
}

@Suppress("LongParameterList")
class DefaultCContext(
    override val lifecycle: Lifecycle,
    override val stateKeeper: StateKeeper = StateKeeperDispatcher(),
    override val instanceKeeper: InstanceKeeper =
        InstanceKeeperDispatcher().also { lifecycle.doOnDestroy(it::destroy) },
    override val backHandler: BackHandler = BackDispatcher(),
    override var navigation: StackNavigation<Any> = NoopStackNavigation(),
    override var messageShower: MessageShower = MessageHolder(),
    override var windowSizeHolder: WindowSizeHolder = WindowSizeHolder(),
    override var permissionController: PermissionController = FakePermissionController(),
) : CContext {
  override val componentContextFactory: ComponentContextFactory<CContext> =
      ComponentContextFactory { lifecycle, stateKeeper, instanceKeeper, backHandler ->
        DefaultCContext(
            lifecycle = lifecycle,
            stateKeeper = stateKeeper,
            instanceKeeper = instanceKeeper,
            backHandler = backHandler,
            navigation = navigation,
            messageShower = messageShower,
            windowSizeHolder = windowSizeHolder,
            permissionController = permissionController,
        )
      }
}

object InitialTestDestination

fun CContext.componentScope(): CoroutineScope {
  return CoroutineScope(
          Dispatchers.Main +
              SupervisorJob() +
              snackbarExceptionHandler(
                  messageShower,
                  CoroutineScope(Dispatchers.Main + SupervisorJob()).also {
                    lifecycle.doOnDestroy { it.cancel("Component was destroyed.") }
                  },
              )
      )
      .also { lifecycle.doOnDestroy { it.cancel("Component was destroyed.") } }
}

fun snackbarExceptionHandler(messageShower: MessageShower, scope: CoroutineScope) =
    CoroutineExceptionHandler { _, exception ->
      if (exception is ApiCallError) {
        scope.launch {
          messageShower.showSnackbar(
              SnackbarMessage(
                  content = exception.userMessage,
                  tone = UserMessage.Tone.Error,
              )
          )
        }
      } else {
        throw exception
      }
    }

fun defaultCContext(
    componentContext: ComponentContext,
    permissionController: PermissionController = FakePermissionController(),
) =
    DefaultCContext(
        lifecycle = componentContext.lifecycle,
        stateKeeper = componentContext.stateKeeper,
        instanceKeeper = componentContext.instanceKeeper,
        backHandler = componentContext.backHandler,
        permissionController = permissionController,
    )

class FakeStackNavigation<C : Any>(initial: C) : StackNavigation<C> {
  private val _stack = mutableListOf<C>(initial)
  val stack: List<C>
    get() = _stack

  override fun navigate(
      transformer: (stack: List<C>) -> List<C>,
      onComplete: (newStack: List<C>, oldStack: List<C>) -> Unit,
  ) {
    val oldStack = _stack.toList()
    _stack.clear()
    val newStack = transformer(oldStack)
    _stack.addAll(newStack)
    onComplete(_stack, oldStack)
  }

  override fun subscribe(observer: (StackNavigation.Event<C>) -> Unit): Cancellation {
    // For testing purposes, we can ignore subscriptions
    return Cancellation {}
  }
}

class NoopStackNavigation<C : Any> : StackNavigation<C> {
  private val _stack = mutableListOf<C>()
  val stack: List<C>
    get() = _stack

  override fun navigate(
      transformer: (stack: List<C>) -> List<C>,
      onComplete: (newStack: List<C>, oldStack: List<C>) -> Unit,
  ) {
    // Do nothing, no-op navigation
  }

  override fun subscribe(observer: (StackNavigation.Event<C>) -> Unit): Cancellation {
    // For testing purposes, we can ignore subscriptions
    return Cancellation {}
  }
}

fun CContext.fakeStack() =
    (navigation as? FakeStackNavigation<Any>)?.stack
        ?: error("navigation by default has a FakeStackNavigation unless is set by specific test!")

class TestCContext : CContext {
  val lifecycleRegistry = LifecycleRegistry()
  val fakeStackNavigation = FakeStackNavigation<Any>(InitialTestDestination)
  val backDispatcher = BackDispatcher()
  val messageHolder = MessageHolder()
  val testWindowSizeHolder = WindowSizeHolder()

  val fakePermissionController = FakePermissionController()

  override val lifecycle: Lifecycle = lifecycleRegistry

  override val stateKeeper: StateKeeper = StateKeeperDispatcher()

  override val instanceKeeper: InstanceKeeper =
      InstanceKeeperDispatcher().also { lifecycle.doOnDestroy(it::destroy) }

  override val backHandler: BackHandler = backDispatcher

  override val componentContextFactory: ComponentContextFactory<CContext> =
      ComponentContextFactory { lifecycle, stateKeeper, instanceKeeper, backHandler ->
        DefaultCContext(
            lifecycle = lifecycle,
            stateKeeper = stateKeeper,
            instanceKeeper = instanceKeeper,
            backHandler = backHandler,
            navigation = navigation,
            messageShower = messageShower,
            windowSizeHolder = windowSizeHolder,
            permissionController = permissionController,
        )
      }
  override var navigation: StackNavigation<Any> = fakeStackNavigation
  override var messageShower: MessageShower = messageHolder
  override var windowSizeHolder: WindowSizeHolder = testWindowSizeHolder
  override var permissionController: PermissionController = fakePermissionController

  fun resumed(): TestCContext {
    lifecycleRegistry.resume()
    return this
  }
}

fun BackHandler.register(lifecycle: Lifecycle, callback: BackCallback) {
  lifecycle.doOnCreate { register(callback) }
  lifecycle.doOnDestroy { unregister(callback) }
}
