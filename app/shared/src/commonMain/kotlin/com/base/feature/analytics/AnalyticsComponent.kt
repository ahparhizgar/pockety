package com.base.feature.analytics

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.base.Fake
import com.base.calendar.Calendar
import com.base.calendar.CalendarDate
import com.base.calendar.CalendarType
import com.base.calendar.GregorianCalendar
import com.base.calendar.PersianCalendar
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CategoryEntity
import com.base.db.CategoryRepository
import com.base.db.ExpenseEntity
import com.base.db.ExpenseRepository
import com.base.feature.settings.UserSettingsRepository
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private const val PERCENT_WHOLE = 100.0

enum class AnalyticsRelativeDate {
  TODAY,
  YESTERDAY,
  OTHER,
}

data class AnalyticsExpenseUiModel(
    val id: Long,
    val amount: Long,
    val description: String,
    val categoryName: String? = null,
    val categoryIcon: String? = null,
    val categoryColor: Long? = null,
    val formattedTime: String = "",
    val createdAt: Instant,
)

data class AnalyticsDateGroupUiModel(
    val date: CalendarDate,
    val relativeDate: AnalyticsRelativeDate,
    val totalAmount: Long,
    val expenses: List<AnalyticsExpenseUiModel>,
)

data class AnalyticsCategorySpendingUiModel(
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val categoryIcon: String? = null,
    val categoryColor: Long? = null,
    val amount: Long,
    val percentage: Int,
)

data class AnalyticsState(
    val calendarType: CalendarType = CalendarType.GREGORIAN,
    val selectedYear: Int = 2026,
    val selectedMonth: Int = 1,
    val canNavigateNext: Boolean = false,
    val monthlyOutflow: Long = 0L,
    val previousMonthOutflow: Long = 0L,
    val categorySpending: List<AnalyticsCategorySpendingUiModel> = emptyList(),
    val dateGroups: List<AnalyticsDateGroupUiModel> = emptyList(),
    val isLoading: Boolean = false,
) {
  val allExpenses: List<AnalyticsExpenseUiModel>
    get() = dateGroups.flatMap { it.expenses }
}

interface AnalyticsComponent {
  val state: Value<AnalyticsState>

  fun onPreviousMonthClicked()

  fun onNextMonthClicked()

  fun onExpenseClicked(id: Long)
}

interface AnalyticsComponentFactory {
  fun create(
      cContext: CContext,
      onExpenseClick: (Long) -> Unit,
  ): AnalyticsComponent
}

class DefaultAnalyticsComponent(
    cContext: CContext,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val onExpenseClick: (Long) -> Unit,
) : AnalyticsComponent, CContext by cContext {
  private val scope = componentScope()

  private var calendarType: CalendarType = CalendarType.GREGORIAN
  private var activeCalendar: Calendar = GregorianCalendar

  private val timeZone = TimeZone.currentSystemDefault()
  private var currentCalendarDate: CalendarDate =
      activeCalendar.toCalendarDate(Clock.System.now().toEpochMilliseconds(), timeZone)
  private var selectedYear: Int = currentCalendarDate.year
  private var selectedMonth: Int = currentCalendarDate.month

  private var rawExpenses: List<ExpenseEntity> = emptyList()
  private var rawCategories: List<CategoryEntity> = emptyList()

  private val _state = MutableValue(buildState())
  override val state: Value<AnalyticsState> = _state

  init {
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
            currentCalendarDate =
                activeCalendar.toCalendarDate(Clock.System.now().toEpochMilliseconds(), timeZone)
            selectedYear = currentCalendarDate.year
            selectedMonth = currentCalendarDate.month
          }
          buildState()
        }
        .onEach { newState -> _state.value = newState }
        .launchIn(scope)
  }

  override fun onPreviousMonthClicked() {
    val tempDate = CalendarDate(selectedYear, selectedMonth, 1, calendarType)
    val prevMonthDate = activeCalendar.addMonths(tempDate, -1)
    selectedYear = prevMonthDate.year
    selectedMonth = prevMonthDate.month
    _state.value = buildState()
  }

  override fun onNextMonthClicked() {
    val canNext =
        selectedYear < currentCalendarDate.year ||
            (selectedYear == currentCalendarDate.year && selectedMonth < currentCalendarDate.month)
    if (!canNext) return

    val tempDate = CalendarDate(selectedYear, selectedMonth, 1, calendarType)
    val nextMonthDate = activeCalendar.addMonths(tempDate, 1)
    selectedYear = nextMonthDate.year
    selectedMonth = nextMonthDate.month
    _state.value = buildState()
  }

  override fun onExpenseClicked(id: Long) {
    onExpenseClick(id)
  }

  private fun buildState(): AnalyticsState {
    val today = activeCalendar.toCalendarDate(Clock.System.now().toEpochMilliseconds(), timeZone)
    val canNavigateNext =
        selectedYear < currentCalendarDate.year ||
            (selectedYear == currentCalendarDate.year && selectedMonth < currentCalendarDate.month)

    val monthExpenses = rawExpenses.filter { expense ->
      val date = activeCalendar.toCalendarDate(expense.createdAt.toEpochMilliseconds(), timeZone)
      date.year == selectedYear && date.month == selectedMonth
    }

    val monthlyOutflow = monthExpenses.sumOf { it.amount }

    val previousMonthDate =
        activeCalendar.addMonths(CalendarDate(selectedYear, selectedMonth, 1, calendarType), -1)
    val previousMonthOutflow =
        rawExpenses
            .filter { expense ->
              val date =
                  activeCalendar.toCalendarDate(expense.createdAt.toEpochMilliseconds(), timeZone)
              date.year == previousMonthDate.year && date.month == previousMonthDate.month
            }
            .sumOf { it.amount }

    val categoriesMap = rawCategories.associateBy { it.id }
    val categorySpending = buildCategorySpending(monthExpenses, categoriesMap, monthlyOutflow)
    val dateGroups = buildDateGroups(monthExpenses, categoriesMap, timeZone, today)

    return AnalyticsState(
        calendarType = calendarType,
        selectedYear = selectedYear,
        selectedMonth = selectedMonth,
        canNavigateNext = canNavigateNext,
        monthlyOutflow = monthlyOutflow,
        previousMonthOutflow = previousMonthOutflow,
        categorySpending = categorySpending,
        dateGroups = dateGroups,
        isLoading = false,
    )
  }

  private fun buildCategorySpending(
      monthExpenses: List<ExpenseEntity>,
      categoriesMap: Map<Long, CategoryEntity>,
      monthlyOutflow: Long,
  ): List<AnalyticsCategorySpendingUiModel> {
    if (monthExpenses.isEmpty() || monthlyOutflow <= 0L) return emptyList()
    return monthExpenses
        .groupBy { it.categoryId }
        .map { (categoryId, expenses) ->
          val category = categoryId?.let { categoriesMap[it] }
          val amount = expenses.sumOf { it.amount }
          AnalyticsCategorySpendingUiModel(
              categoryId = categoryId,
              categoryName = category?.name,
              categoryIcon = category?.icon,
              categoryColor = category?.color,
              amount = amount,
              percentage =
                  (amount.toDouble() * PERCENT_WHOLE / monthlyOutflow).roundToInt(),
          )
        }
        .sortedByDescending { it.amount }
  }

  private fun buildDateGroups(
      monthExpenses: List<ExpenseEntity>,
      categoriesMap: Map<Long, CategoryEntity>,
      timeZone: TimeZone,
      today: CalendarDate,
  ): List<AnalyticsDateGroupUiModel> =
      monthExpenses
          .groupBy { activeCalendar.toCalendarDate(it.createdAt.toEpochMilliseconds(), timeZone) }
          .entries
          .sortedByDescending { it.key }
          .map { (date, expensesForDate) ->
            val sortedExpenses = expensesForDate.sortedByDescending { it.createdAt }
            val groupTotal = sortedExpenses.sumOf { it.amount }
            val relativeDate = classifyRelativeDate(date, today)
            AnalyticsDateGroupUiModel(
                date = date,
                relativeDate = relativeDate,
                totalAmount = groupTotal,
                expenses =
                    sortedExpenses.map { expense ->
                      toExpenseUiModel(expense, categoriesMap, timeZone)
                    },
            )
          }

  private fun classifyRelativeDate(date: CalendarDate, today: CalendarDate): AnalyticsRelativeDate {
    if (date.year == today.year && date.month == today.month && date.day == today.day) {
      return AnalyticsRelativeDate.TODAY
    }
    val yesterday = activeCalendar.addDays(today, -1)
    return if (
        date.year == yesterday.year && date.month == yesterday.month && date.day == yesterday.day
    ) {
      AnalyticsRelativeDate.YESTERDAY
    } else {
      AnalyticsRelativeDate.OTHER
    }
  }

  private fun toExpenseUiModel(
      expense: ExpenseEntity,
      categoriesMap: Map<Long, CategoryEntity>,
      timeZone: TimeZone,
  ): AnalyticsExpenseUiModel {
    val localTime = expense.createdAt.toLocalDateTime(timeZone)
    val hour = localTime.hour.toString().padStart(2, '0')
    val minute = localTime.minute.toString().padStart(2, '0')
    val category = categoriesMap[expense.categoryId]
    return AnalyticsExpenseUiModel(
        id = expense.id,
        amount = expense.amount,
        description = expense.description,
        categoryName = category?.name,
        categoryIcon = category?.icon,
        categoryColor = category?.color,
        formattedTime = "$hour:$minute",
        createdAt = expense.createdAt,
    )
  }
}

class DefaultAnalyticsComponentFactory(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val userSettingsRepository: UserSettingsRepository,
) : AnalyticsComponentFactory {
  override fun create(
      cContext: CContext,
      onExpenseClick: (Long) -> Unit,
  ): AnalyticsComponent =
      DefaultAnalyticsComponent(
          cContext = cContext,
          expenseRepository = expenseRepository,
          categoryRepository = categoryRepository,
          userSettingsRepository = userSettingsRepository,
          onExpenseClick = onExpenseClick,
      )
}

object FakeAnalyticsExpenseUiModelFactory {
  fun create(
      id: Long = 1,
      amount: Long = 50_000,
      description: String = "Coffee",
      categoryName: String? = "Cafe",
      createdAt: Instant = Clock.System.now(),
  ): AnalyticsExpenseUiModel =
      AnalyticsExpenseUiModel(
          id = id,
          amount = amount,
          description = description,
          categoryName = categoryName,
          categoryIcon = "local_cafe",
          categoryColor = 0xFF4CAF50,
          formattedTime = "12:30",
          createdAt = createdAt,
      )
}

object FakeAnalyticsDateGroupUiModelFactory {
  @Suppress("MagicNumber")
  fun create(
      date: CalendarDate = CalendarDate(2026, 10, 24, CalendarType.GREGORIAN),
      relativeDate: AnalyticsRelativeDate = AnalyticsRelativeDate.TODAY,
      totalAmount: Long = 50_000,
      expenses: List<AnalyticsExpenseUiModel> = listOf(FakeAnalyticsExpenseUiModelFactory.create()),
  ): AnalyticsDateGroupUiModel =
      AnalyticsDateGroupUiModel(
          date = date,
          relativeDate = relativeDate,
          totalAmount = totalAmount,
          expenses = expenses,
      )
}

object FakeAnalyticsCategorySpendingUiModelFactory {
  fun create(
      categoryId: Long? = 1,
      categoryName: String? = "Cafe",
      amount: Long = 50_000,
      percentage: Int = 100,
  ): AnalyticsCategorySpendingUiModel =
      AnalyticsCategorySpendingUiModel(
          categoryId = categoryId,
          categoryName = categoryName,
          categoryIcon = "local_cafe",
          categoryColor = 0xFF4CAF50,
          amount = amount,
          percentage = percentage,
      )
}

object FakeAnalyticsStateFactory {
  fun create(
      calendarType: CalendarType = CalendarType.GREGORIAN,
      selectedMonth: Int = 10,
      canNavigateNext: Boolean = false,
      monthlyOutflow: Long = 50_000,
      previousMonthOutflow: Long = 40_000,
      categorySpending: List<AnalyticsCategorySpendingUiModel> =
          listOf(FakeAnalyticsCategorySpendingUiModelFactory.create()),
      dateGroups: List<AnalyticsDateGroupUiModel> =
          listOf(FakeAnalyticsDateGroupUiModelFactory.create()),
  ): AnalyticsState {
    return AnalyticsState(
        calendarType = calendarType,
        selectedMonth = selectedMonth,
        canNavigateNext = canNavigateNext,
        monthlyOutflow = monthlyOutflow,
        previousMonthOutflow = previousMonthOutflow,
        categorySpending = categorySpending,
        dateGroups = dateGroups,
        isLoading = false,
    )
  }
}

@Fake
class FakeAnalyticsComponent(
    initialState: AnalyticsState = FakeAnalyticsStateFactory.create(),
    private val onExpenseClick: (Long) -> Unit = {},
) : AnalyticsComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<AnalyticsState> = _state

  override fun onPreviousMonthClicked() {}

  override fun onNextMonthClicked() {}

  override fun onExpenseClicked(id: Long) {
    onExpenseClick(id)
  }
}

class FakeAnalyticsComponentFactory : AnalyticsComponentFactory {
  override fun create(
      cContext: CContext,
      onExpenseClick: (Long) -> Unit,
  ): AnalyticsComponent =
      FakeAnalyticsComponent(
          onExpenseClick = onExpenseClick,
      )
}
