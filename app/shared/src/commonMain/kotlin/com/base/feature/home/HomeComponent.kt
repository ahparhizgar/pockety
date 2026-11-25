package com.base.feature.home

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.calendar.Calendar
import com.base.calendar.CalendarDate
import com.base.calendar.CalendarType
import com.base.calendar.GregorianCalendar
import com.base.calendar.PersianCalendar
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CandidateExpenseRepository
import com.base.db.CategoryEntity
import com.base.db.CategoryRepository
import com.base.db.ExpenseEntity
import com.base.db.ExpenseRepository
import com.base.db.PocketRepository
import com.base.feature.root.RootFlowDestination
import com.base.feature.settings.UserSettingsRepository
import com.base.sms.InitialSmsImport
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone

enum class HomeCategoryKind {
  CATEGORY,
  UNCATEGORIZED,
  OTHER,
}

@Immutable
data class HomeCategoryOutflow(
    val kind: HomeCategoryKind,
    val name: String? = null,
    val amount: Long,
    val share: Float,
    val color: Long? = null,
)

@Immutable
data class HomePocketUi(
    val id: Long,
    val name: String,
    val balance: Long,
    val icon: String,
)

data class HomeState(
    val isSmsPermissionGranted: Boolean = false,
    val pendingCandidateCount: Int = 0,
    val pendingCandidateSum: Long = 0L,
    val calendarType: CalendarType = CalendarType.GREGORIAN,
    val currentYear: Int = 2026,
    val currentMonth: Int = 1,
    val previousYear: Int = 2025,
    val previousMonth: Int = 12,
    val monthlyOutflow: Long = 0L,
    val previousMonthOutflow: Long = 0L,
    val monthOverMonthPercent: Int? = null,
    val isMonthOverMonthSaved: Boolean = true,
    val topCategories: List<HomeCategoryOutflow> = emptyList(),
    val pockets: List<HomePocketUi> = emptyList(),
)

interface HomeComponent {
  val state: Value<HomeState>

  fun onAddExpenseClicked()

  fun onUndecidedExpensesClicked()

  fun onEnableSmsTrackingClicked(): Job

  fun onAllMonthsClicked()

  fun onManagePocketsClicked()

  fun onPocketClicked(pocketId: Long)
}

interface HomeComponentFactory {
  fun create(
      cContext: CContext,
      onAddExpense: () -> Unit,
      onOpenInbox: () -> Unit = {},
      onOpenAnalytics: () -> Unit = {},
      onOpenPockets: () -> Unit = {},
  ): HomeComponent
}

@Suppress("LongParameterList")
class DefaultHomeComponent(
    cContext: CContext,
    private val candidateExpenseRepository: CandidateExpenseRepository,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val pocketRepository: PocketRepository,
    private val initialSmsImport: InitialSmsImport,
    private val onAddExpense: () -> Unit,
    private val onOpenInbox: () -> Unit = {},
    private val onOpenAnalytics: () -> Unit = {},
    private val onOpenPockets: () -> Unit = {},
) : HomeComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state =
      MutableValue(
          HomeState(
              isSmsPermissionGranted = permissionController.isSmsPermissionGranted.value,
          )
      )
  override val state: Value<HomeState> = _state

  private var calendarType: CalendarType = CalendarType.GREGORIAN
  private var activeCalendar: Calendar = GregorianCalendar
  private val timeZone = TimeZone.currentSystemDefault()
  private var rawExpenses: List<ExpenseEntity> = emptyList()
  private var rawCategories: List<CategoryEntity> = emptyList()

  init {
    observePermission()
    observePendingCandidates()
    observeOverview()
    observePockets()
  }

  private fun observePermission(): Job =
      permissionController.isSmsPermissionGranted
          .onEach { granted ->
            _state.update { it.copy(isSmsPermissionGranted = granted) }
            if (granted) initialSmsImport.importOnce()
          }
          .launchIn(scope)

  private fun observePendingCandidates(): Job =
      candidateExpenseRepository
          .observePendingCandidates()
          .onEach { candidates ->
            _state.update {
              it.copy(
                  pendingCandidateCount = candidates.size,
                  pendingCandidateSum = candidates.sumOf { candidate -> candidate.amount },
              )
            }
          }
          .launchIn(scope)

  private fun observeOverview(): Job =
      combine(
              expenseRepository.observeExpenses(),
              categoryRepository.observeCategories(),
              userSettingsRepository.calendarType,
          ) { expenses, categories, type ->
            rawExpenses = expenses
            rawCategories = categories
            if (calendarType != type) {
              calendarType = type
              activeCalendar =
                  when (type) {
                    CalendarType.GREGORIAN -> GregorianCalendar
                    CalendarType.PERSIAN -> PersianCalendar
                  }
            }
            buildOverview()
          }
          .onEach { overview -> _state.update { it.copyOverview(overview) } }
          .launchIn(scope)

  private fun observePockets(): Job =
      pocketRepository
          .observePockets()
          .onEach { pockets ->
            _state.update {
              it.copy(
                  pockets =
                      pockets.map { pocket ->
                        HomePocketUi(
                            id = pocket.id,
                            name = pocket.name,
                            balance = pocket.remainingBalance,
                            icon = pocket.icon,
                        )
                      },
              )
            }
          }
          .launchIn(scope)

  private fun buildOverview(): HomeState {
    val nowInstant = Clock.System.now()
    val now = activeCalendar.toCalendarDate(nowInstant.toEpochMilliseconds(), timeZone)
    val previous = activeCalendar.addMonths(CalendarDate(now.year, now.month, 1, calendarType), -1)

    val currentExpenses = rawExpenses.filter { expense -> expense.isInMonth(now) }
    val monthlyOutflow = currentExpenses.sumOf { it.amount }

    val recentWindowStart = nowInstant - COMPARISON_WINDOW_DAYS.days
    val previousWindowStart = nowInstant - (2 * COMPARISON_WINDOW_DAYS).days
    val recentOutflow =
        rawExpenses
            .filter { it.createdAt > recentWindowStart && it.createdAt <= nowInstant }
            .sumOf { it.amount }
    val previousMonthOutflow =
        rawExpenses
            .filter { it.createdAt > previousWindowStart && it.createdAt <= recentWindowStart }
            .sumOf { it.amount }

    val percent =
        if (previousMonthOutflow > 0) {
          (abs(recentOutflow - previousMonthOutflow).toDouble() * 100.0 / previousMonthOutflow)
              .roundToInt()
        } else {
          null
        }

    return HomeState(
        calendarType = calendarType,
        currentYear = now.year,
        currentMonth = now.month,
        previousYear = previous.year,
        previousMonth = previous.month,
        monthlyOutflow = monthlyOutflow,
        previousMonthOutflow = previousMonthOutflow,
        monthOverMonthPercent = percent,
        isMonthOverMonthSaved = recentOutflow <= previousMonthOutflow,
        topCategories = buildBreakdown(currentExpenses, monthlyOutflow),
    )
  }

  private fun buildBreakdown(
      monthExpenses: List<ExpenseEntity>,
      total: Long,
  ): List<HomeCategoryOutflow> {
    if (total <= 0) return emptyList()
    val categoriesById = rawCategories.associateBy { it.id }
    val groups =
        monthExpenses
            .groupBy { it.categoryId }
            .map { (categoryId, expenses) ->
              val category = categoryId?.let { categoriesById[it] }
              HomeCategoryOutflow(
                  kind =
                      if (categoryId == null) {
                        HomeCategoryKind.UNCATEGORIZED
                      } else {
                        HomeCategoryKind.CATEGORY
                      },
                  name = category?.name,
                  amount = expenses.sumOf { it.amount },
                  share = 0f,
                  color = category?.color,
              )
            }
            .sortedByDescending { it.amount }
    val top = groups.take(4)
    val rest = groups.drop(4)
    val rows = top.map { it.copy(share = it.amount.toFloat() / total) }.toMutableList()
    if (rest.isNotEmpty()) {
      val otherAmount = rest.sumOf { it.amount }
      rows +=
          HomeCategoryOutflow(
              kind = HomeCategoryKind.OTHER,
              amount = otherAmount,
              share = otherAmount.toFloat() / total,
          )
    }
    return rows
  }

  private fun ExpenseEntity.isInMonth(month: CalendarDate): Boolean {
    val date = activeCalendar.toCalendarDate(createdAt.toEpochMilliseconds(), timeZone)
    return date.year == month.year && date.month == month.month
  }

  private fun HomeState.copyOverview(overview: HomeState): HomeState =
      copy(
          calendarType = overview.calendarType,
          currentYear = overview.currentYear,
          currentMonth = overview.currentMonth,
          previousYear = overview.previousYear,
          previousMonth = overview.previousMonth,
          monthlyOutflow = overview.monthlyOutflow,
          previousMonthOutflow = overview.previousMonthOutflow,
          monthOverMonthPercent = overview.monthOverMonthPercent,
          isMonthOverMonthSaved = overview.isMonthOverMonthSaved,
          topCategories = overview.topCategories,
      )

  override fun onAddExpenseClicked() {
    navigation.pushNew(RootFlowDestination.AddExpenseFlow())
    onAddExpense()
  }

  override fun onUndecidedExpensesClicked() {
    onOpenInbox()
    scope.launch { permissionController.requestNotificationPermission() }
  }

  override fun onEnableSmsTrackingClicked(): Job = scope.launch {
    permissionController.requestReceiveSmsPermission()
  }

  override fun onAllMonthsClicked() {
    onOpenAnalytics()
  }

  override fun onManagePocketsClicked() {
    onOpenPockets()
  }

  override fun onPocketClicked(pocketId: Long) {
    navigation.pushNew(RootFlowDestination.PocketDetails(pocketId = pocketId))
  }
}

@Suppress("LongParameterList")
class DefaultHomeComponentFactory(
    private val candidateExpenseRepository: CandidateExpenseRepository,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val pocketRepository: PocketRepository,
    private val initialSmsImport: InitialSmsImport,
) : HomeComponentFactory {
  override fun create(
      cContext: CContext,
      onAddExpense: () -> Unit,
      onOpenInbox: () -> Unit,
      onOpenAnalytics: () -> Unit,
      onOpenPockets: () -> Unit,
  ): HomeComponent =
      DefaultHomeComponent(
          cContext = cContext,
          candidateExpenseRepository = candidateExpenseRepository,
          expenseRepository = expenseRepository,
          categoryRepository = categoryRepository,
          userSettingsRepository = userSettingsRepository,
          pocketRepository = pocketRepository,
          initialSmsImport = initialSmsImport,
          onAddExpense = onAddExpense,
          onOpenInbox = onOpenInbox,
          onOpenAnalytics = onOpenAnalytics,
          onOpenPockets = onOpenPockets,
      )
}

@Fake
class FakeHomeComponent(
    initialState: HomeState = HomeState(),
) : HomeComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<HomeState> = _state

  var allMonthsClickedCount: Int = 0
    private set

  var managePocketsClickedCount: Int = 0
    private set

  val clickedPocketIds: MutableList<Long> = mutableListOf()

  override fun onAddExpenseClicked() {}

  override fun onUndecidedExpensesClicked() {}

  override fun onEnableSmsTrackingClicked(): Job = Job().apply { complete() }

  override fun onAllMonthsClicked() {
    allMonthsClickedCount++
  }

  override fun onManagePocketsClicked() {
    managePocketsClickedCount++
  }

  override fun onPocketClicked(pocketId: Long) {
    clickedPocketIds += pocketId
  }
}

@Fake
class FakeHomeComponentFactory : HomeComponentFactory {
  override fun create(
      cContext: CContext,
      onAddExpense: () -> Unit,
      onOpenInbox: () -> Unit,
      onOpenAnalytics: () -> Unit,
      onOpenPockets: () -> Unit,
  ): HomeComponent = FakeHomeComponent()
}

private const val COMPARISON_WINDOW_DAYS = 30
