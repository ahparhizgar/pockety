package com.base.capability.main

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import com.base.Fake
import com.base.capability.adding_expense.AddExpenseFlowComponent
import com.base.capability.home.FakeHomeComponent
import com.base.capability.home.HomeComponent
import com.base.capability.home.HomeComponentFactory
import com.base.capability.pocket_management.FakePocketsComponent
import com.base.capability.pocket_management.PocketDetailsComponent
import com.base.capability.pocket_management.PocketSettingsFlowComponent
import com.base.capability.pocket_management.PocketsComponent
import com.base.capability.pocket_management.PocketsComponentFactory
import com.base.capability.root_navigation.TopLevelDestinationConfig
import com.base.capability.sms_inbox.FakeInboxComponent
import com.base.capability.sms_inbox.InboxComponent
import com.base.capability.sms_inbox.InboxComponentFactory
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CandidateExpenseRepository
import com.base.ui.components.PocketyTab
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainChildConfig {
  @Serializable data object Home : MainChildConfig

  @Serializable data object Inbox : MainChildConfig

  @Serializable data object Pockets : MainChildConfig

  @Serializable data object Analytics : MainChildConfig

  @Serializable data object Settings : MainChildConfig
}

data class MainState(
    val inboxBadgeCount: Int = 0,
)

interface MainComponent : TopLevelDestinationConfig {
  val stack: Value<ChildStack<MainChildConfig, Child>>
  val state: Value<MainState>
  val backHandler: BackHandler

  fun onBack()

  fun onTabSelect(tab: PocketyTab)

  fun onTabSelected(tab: PocketyTab) = onTabSelect(tab)

  sealed interface Child {
    class Home(val component: HomeComponent) : Child

    class Inbox(val component: InboxComponent) : Child

    class Pockets(val component: PocketsComponent) : Child

    data object Analytics : Child

    data object Settings : Child
  }

  @Serializable data object Config : TopLevelDestinationConfig
}

interface MainComponentFactory {
  fun create(cContext: CContext): MainComponent
}

class DefaultMainComponent(
    cContext: CContext,
    private val homeComponentFactory: HomeComponentFactory,
    private val inboxComponentFactory: InboxComponentFactory,
    private val pocketsComponentFactory: PocketsComponentFactory,
    private val candidateExpenseRepository: CandidateExpenseRepository,
) : MainComponent, CContext by cContext {
  private val scope = componentScope()
  private val mainNavigation = StackNavigation<MainChildConfig>()
  override val backHandler: BackHandler = cContext.backHandler

  private val _state = MutableValue(MainState())
  override val state: Value<MainState> = _state

  init {
    candidateExpenseRepository
        .observePendingCandidates()
        .onEach { candidates -> _state.update { it.copy(inboxBadgeCount = candidates.size) } }
        .launchIn(scope)
  }

  override fun onBack() {
    mainNavigation.pop()
  }

  override fun onTabSelect(tab: PocketyTab) {
    val config =
        when (tab) {
          PocketyTab.Home -> MainChildConfig.Home
          PocketyTab.Inbox -> MainChildConfig.Inbox
          PocketyTab.Pockets -> MainChildConfig.Pockets
          PocketyTab.Analytics -> MainChildConfig.Analytics
          PocketyTab.Settings -> MainChildConfig.Settings
        }
    if (config == MainChildConfig.Home) {
      mainNavigation.replaceAll(MainChildConfig.Home)
    } else {
      mainNavigation.replaceAll(MainChildConfig.Home, config)
    }
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
                  onAddExpense = {},
                  onOpenInbox = {
                    mainNavigation.replaceAll(MainChildConfig.Home, MainChildConfig.Inbox)
                  },
              )
          )

      MainChildConfig.Inbox ->
          MainComponent.Child.Inbox(
              inboxComponentFactory.create(
                  cContext = context,
                  onSelectCandidate = { candidate ->
                    mainNavigation.replaceAll(MainChildConfig.Home)
                    navigation.pushNew(
                        AddExpenseFlowComponent.Config(
                            initialAmount = candidate.amount.toString(),
                            candidateExpenseId = candidate.id,
                        )
                    )
                  },
                  onBack = {
                    mainNavigation.pop { success ->
                      if (!success) {
                        mainNavigation.replaceAll(MainChildConfig.Home)
                      }
                    }
                  },
              )
          )

      MainChildConfig.Pockets ->
          MainComponent.Child.Pockets(
              pocketsComponentFactory.create(
                  cContext = context,
                  onPocketClick = { pocketId ->
                    navigation.pushNew(PocketDetailsComponent.Config(pocketId = pocketId))
                  },
                  onAddPocketClick = { navigation.pushNew(PocketSettingsFlowComponent.Config()) },
              )
          )
      MainChildConfig.Analytics -> MainComponent.Child.Analytics
      MainChildConfig.Settings -> MainComponent.Child.Settings
    }
  }
}

class DefaultMainComponentFactory(
    private val homeComponentFactory: HomeComponentFactory,
    private val inboxComponentFactory: InboxComponentFactory,
    private val pocketsComponentFactory: PocketsComponentFactory,
    private val candidateExpenseRepository: CandidateExpenseRepository,
) : MainComponentFactory {
  override fun create(cContext: CContext): MainComponent =
      DefaultMainComponent(
          cContext = cContext,
          homeComponentFactory = homeComponentFactory,
          inboxComponentFactory = inboxComponentFactory,
          pocketsComponentFactory = pocketsComponentFactory,
          candidateExpenseRepository = candidateExpenseRepository,
      )
}

@Fake
class FakeMainComponent(
    stack: Value<ChildStack<MainChildConfig, MainComponent.Child>>? = null,
    initialState: MainState = MainState(),
) : MainComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<MainState> = _state
  override val backHandler: BackHandler = BackDispatcher()

  private val _stack: MutableValue<ChildStack<MainChildConfig, MainComponent.Child>> =
      (stack as? MutableValue<ChildStack<MainChildConfig, MainComponent.Child>>)
          ?: MutableValue(
              ChildStack(
                  active =
                      Child.Created(
                          MainChildConfig.Home,
                          MainComponent.Child.Home(FakeHomeComponent()),
                      ),
                  backStack = emptyList(),
              )
          )
  override val stack: Value<ChildStack<MainChildConfig, MainComponent.Child>> = _stack

  override fun onBack() {
    if (_stack.value.backStack.isNotEmpty()) {
      _stack.value =
          ChildStack(
              active =
                  Child.Created(
                      MainChildConfig.Home,
                      MainComponent.Child.Home(FakeHomeComponent()),
                  ),
              backStack = emptyList(),
          )
    }
  }

  override fun onTabSelect(tab: PocketyTab) {
    val child =
        when (tab) {
          PocketyTab.Home -> MainComponent.Child.Home(FakeHomeComponent())
          PocketyTab.Inbox -> MainComponent.Child.Inbox(FakeInboxComponent())
          PocketyTab.Pockets -> MainComponent.Child.Pockets(FakePocketsComponent())
          PocketyTab.Analytics -> MainComponent.Child.Analytics
          PocketyTab.Settings -> MainComponent.Child.Settings
        }
    val config =
        when (tab) {
          PocketyTab.Home -> MainChildConfig.Home
          PocketyTab.Inbox -> MainChildConfig.Inbox
          PocketyTab.Pockets -> MainChildConfig.Pockets
          PocketyTab.Analytics -> MainChildConfig.Analytics
          PocketyTab.Settings -> MainChildConfig.Settings
        }
    val backStack =
        if (config == MainChildConfig.Home) {
          emptyList()
        } else {
          listOf(
              Child.Created(
                  MainChildConfig.Home,
                  MainComponent.Child.Home(FakeHomeComponent()),
              )
          )
        }
    _stack.value = ChildStack(active = Child.Created(config, child), backStack = backStack)
  }
}

@Fake
class FakeMainComponentFactory : MainComponentFactory {
  override fun create(cContext: CContext): MainComponent = FakeMainComponent()
}
