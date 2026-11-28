package com.base.feature.root

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import com.base.component.CContext
import com.base.component.WindowSizeHolder
import com.base.component.componentScope
import com.base.db.CategoryRepository
import com.base.db.PocketRepository
import com.base.feature.category.management.CategoryManagementFlowComponentFactory
import com.base.feature.expense.AddExpenseFlowComponentFactory
import com.base.feature.home.HomeComponentFactory
import com.base.feature.main.MainComponentFactory
import com.base.feature.pocket.PocketsComponentFactory
import com.base.feature.pocket.details.PocketDetailsComponentFactory
import com.base.feature.pocket.settings.PocketSettingsFlowComponentFactory
import com.base.feature.privacypolicy.PrivacyPolicyComponentFactory
import com.base.feature.settings.AppLanguage
import com.base.feature.settings.CurrencyType
import com.base.feature.settings.UserSettingsRepository
import com.base.feature.sms.inbox.InboxComponentFactory
import com.base.navigation.NavigationAction
import com.base.navigation.NavigationActionBus
import com.base.usermessage.MessageHolder
import kotlin.reflect.KClass
import kotlin.time.Clock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.scope.Scope

interface RootComponent {
  val backHandler: BackHandler
  val childStack: Value<ChildStack<RootFlowDestination, RootFlowDestination.Instance>>
  val messageHolder: MessageHolder
  val windowSizeHolder: WindowSizeHolder
  val currency: Value<CurrencyType>
  val appLanguage: Value<AppLanguage>

  fun onBack()
}

interface RootComponentFactory {
  fun create(context: CContext): RootComponent
}

class DefaultRootComponent(
    cContext: CContext,
    private val componentProvider: ComponentProvider,
    private val navigationActionBus: NavigationActionBus,
    initialLanguage: AppLanguage = AppLanguage.ENGLISH,
    initialCurrency: CurrencyType = CurrencyType.TOMANS,
) : RootComponent, CContext by cContext {
  val scope = componentScope()

  private val rootNavigation = StackNavigation<RootFlowDestination>()
  override val backHandler: BackHandler = cContext.backHandler
  override val messageHolder: MessageHolder = MessageHolder()
  private val _currency = MutableValue(initialCurrency)
  override val currency: Value<CurrencyType> = _currency
  private val _appLanguage = MutableValue(initialLanguage)
  override val appLanguage: Value<AppLanguage> = _appLanguage

  override fun onBack() {
    rootNavigation.pop()
  }

  init {
    @Suppress("UNCHECKED_CAST")
    cContext.navigation = rootNavigation
    messageShower = messageHolder

    scope.launch { rootNavigation.replaceAll(RootFlowDestination.Main) }

    scope.launch {
      val userSettingsRepository = componentProvider.provide(UserSettingsRepository::class)
      userSettingsRepository.appLanguage.collect { language -> _appLanguage.value = language }
    }

    scope.launch {
      componentProvider.provide(UserSettingsRepository::class).currencyType.collect {
        _currency.value = it
      }
    }

    scope.launch {
      val settings = componentProvider.provide(UserSettingsRepository::class)
      if (!settings.defaultCategoriesSeeded.first()) {
        componentProvider
            .provide(CategoryRepository::class)
            .ensureDefaultCategories(isPersian = _appLanguage.value == AppLanguage.PERSIAN)
        settings.setDefaultCategoriesSeeded()
      }
    }

    scope.launch {
      val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
      val monthNumber = now.month.ordinal + 1
      val currentYearMonth = "${now.year}-${monthNumber.toString().padStart(2, '0')}"
      val pocketRepository = componentProvider.provide(PocketRepository::class)
      pocketRepository.processMonthlyTopUps(currentYearMonth)
    }

    scope.launch {
      navigationActionBus.events.collect { action ->
        when (action) {
          NavigationAction.OpenNewExpense -> openNewExpense()
          is NavigationAction.OpenAddExpense -> {
            rootNavigation.pushNew(
                RootFlowDestination.AddExpenseFlow(
                    initialAmount = action.initialAmount,
                    candidateExpenseId = action.candidateExpenseId,
                )
            )
          }
        }
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

  private fun openNewExpense() {
    rootNavigation.navigate { stack ->
      stack.filterNot { it is RootFlowDestination.AddExpenseFlow } +
          RootFlowDestination.AddExpenseFlow()
    }
    val flow =
        (childStack.value.active.instance as? RootFlowDestination.AddExpenseFlow.Instance)
            ?.component
    if (flow != null) {
      while (flow.stack.value.backStack.isNotEmpty()) flow.onBack()
    }
  }

  @Suppress("LongMethod")
  private fun createChild(
      config: RootFlowDestination,
      cContext: CContext,
  ): RootFlowDestination.Instance {
    return when (config) {
      is RootFlowDestination.Splash -> RootFlowDestination.Splash.Instance
      is RootFlowDestination.Main ->
          RootFlowDestination.Main.Instance(
              componentProvider.provide(MainComponentFactory::class).create(cContext)
          )
      is RootFlowDestination.PrivacyPolicy ->
          RootFlowDestination.PrivacyPolicy.Instance(
              componentProvider
                  .provide(PrivacyPolicyComponentFactory::class)
                  .create(
                      cContext = cContext,
                      onBack = { rootNavigation.pop() },
                  )
          )
      is RootFlowDestination.CategoryManagementFlow ->
          RootFlowDestination.CategoryManagementFlow.Instance(
              componentProvider
                  .provide(CategoryManagementFlowComponentFactory::class)
                  .create(cContext, onBack = { rootNavigation.pop() })
          )
      is RootFlowDestination.Home ->
          RootFlowDestination.Home.Instance(
              componentProvider
                  .provide(HomeComponentFactory::class)
                  .create(
                      cContext = cContext,
                      onAddExpense = {},
                      onOpenInbox = {},
                  )
          )
      is RootFlowDestination.Inbox ->
          RootFlowDestination.Inbox.Instance(
              componentProvider
                  .provide(InboxComponentFactory::class)
                  .create(
                      cContext = cContext,
                      onSelectCandidate = { candidate ->
                        rootNavigation.pop()
                        rootNavigation.pushNew(
                            RootFlowDestination.AddExpenseFlow(
                                initialAmount = candidate.amount.toString(),
                                candidateExpenseId = candidate.id,
                            )
                        )
                      },
                      onBack = { rootNavigation.pop() },
                  )
          )
      is RootFlowDestination.Pockets ->
          RootFlowDestination.Pockets.Instance(
              componentProvider
                  .provide(PocketsComponentFactory::class)
                  .create(
                      cContext = cContext,
                  )
          )
      is RootFlowDestination.AddExpenseFlow ->
          RootFlowDestination.AddExpenseFlow.Instance(
              componentProvider
                  .provide(AddExpenseFlowComponentFactory::class)
                  .create(
                      cContext = cContext,
                      config = config,
                      onCompleted = { rootNavigation.pop() },
                      onCancelled = { rootNavigation.pop() },
                  )
          )
      is RootFlowDestination.PocketSettingsFlow ->
          RootFlowDestination.PocketSettingsFlow.Instance(
              componentProvider
                  .provide(PocketSettingsFlowComponentFactory::class)
                  .create(
                      cContext = cContext,
                      config = config,
                      onCompleted = { rootNavigation.pop() },
                      onCancelled = { rootNavigation.pop() },
                      onDeleted = { rootNavigation.popWhile { it !is RootFlowDestination.Main } },
                  )
          )
      is RootFlowDestination.PocketDetails ->
          RootFlowDestination.PocketDetails.Instance(
              componentProvider
                  .provide(PocketDetailsComponentFactory::class)
                  .create(
                      cContext = cContext,
                      pocketId = config.pocketId,
                      onBack = { rootNavigation.pop() },
                      onConfigure = { pocketId ->
                        rootNavigation.pushNew(
                            RootFlowDestination.PocketSettingsFlow(pocketId = pocketId)
                        )
                      },
                  )
          )
    }
  }
}

class DefaultRootComponentFactory(
    private val componentProvider: ComponentProvider,
    private val navigationActionBus: NavigationActionBus,
) : RootComponentFactory {
  override fun create(context: CContext): RootComponent {
    val settings = componentProvider.provide(UserSettingsRepository::class)
    // Seed the persisted language and currency before the first frame: the
    // DataStore flows emit asynchronously, so starting from the defaults
    // would flash English/LTR (and the default currency) on cold start
    // before the saved values arrive.
    val (initialLanguage, initialCurrency) =
        runBlocking { settings.appLanguage.first() to settings.currencyType.first() }
    return DefaultRootComponent(
        cContext = context,
        componentProvider = componentProvider,
        navigationActionBus = navigationActionBus,
        initialLanguage = initialLanguage,
        initialCurrency = initialCurrency,
    )
  }
}

class FakeRootComponent(
    initialLanguage: AppLanguage = AppLanguage.ENGLISH,
) : RootComponent {
  override val backHandler: BackHandler = BackDispatcher()
  override val messageHolder: MessageHolder = MessageHolder()
  override val windowSizeHolder: WindowSizeHolder = WindowSizeHolder()
  override val currency: Value<CurrencyType> = MutableValue(CurrencyType.TOMANS)
  override val appLanguage: Value<AppLanguage> = MutableValue(initialLanguage)

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
