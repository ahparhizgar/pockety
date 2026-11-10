package com.base.feature.analytics

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.base.Fake
import com.base.calendar.Calendar
import com.base.calendar.CalendarDate
import com.base.calendar.CalendarType
import com.base.calendar.GregorianCalendar
import com.base.calendar.PersianCalendar
import com.base.calendar.getMonthName
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CategoryEntity
import com.base.db.CategoryRepository
import com.base.db.ExpenseEntity
import com.base.db.ExpenseRepository
import com.base.feature.pocket.details.formatAmount
import com.base.feature.settings.UserSettingsRepository
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AnalyticsExpenseUiModel(
    val id: Long,
    val amount: Long,
    val formattedAmount: String,
    val description: String,
    val categoryName: String? = null,
    val categoryIcon: String? = null,
    val categoryColor: Long? = null,
    val formattedTime: String = "",
    val createdAt: Instant,
)

data class AnalyticsDateGroupUiModel(
    val date: CalendarDate,
    val headerTitle: String,
    val totalAmount: Long,
    val formattedTotalAmount: String,
    val expenses: List<AnalyticsExpenseUiModel>,
)

data class AnalyticsState(
    val selectedYear: Int = 2026,
    val selectedMonth: Int = 1,
    val selectedMonthLabel: String = "",
    val canNavigateNext: Boolean = false,
    val monthlyOutflow: Long = 0L,
    val formattedMonthlyOutflow: String = "0 T",
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

    val monthName = getMonthName(selectedMonth, calendarType)
    val selectedMonthLabel = "$monthName $selectedYear"

    val monthExpenses = rawExpenses.filter { expense ->
      val date = activeCalendar.toCalendarDate(expense.createdAt.toEpochMilliseconds(), timeZone)
      date.year == selectedYear && date.month == selectedMonth
    }

    val monthlyOutflow = monthExpenses.sumOf { it.amount }
    val formattedMonthlyOutflow = "${formatAmount(monthlyOutflow)} T"

    val categoriesMap = rawCategories.associateBy { it.id }
    val dateGroups = buildDateGroups(monthExpenses, categoriesMap, timeZone, today)

    return AnalyticsState(
        selectedYear = selectedYear,
        selectedMonth = selectedMonth,
        selectedMonthLabel = selectedMonthLabel,
        canNavigateNext = canNavigateNext,
        monthlyOutflow = monthlyOutflow,
        formattedMonthlyOutflow = formattedMonthlyOutflow,
        dateGroups = dateGroups,
        isLoading = false,
    )
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
            val headerTitle = formatGroupHeaderTitle(date, today)
            AnalyticsDateGroupUiModel(
                date = date,
                headerTitle = headerTitle,
                totalAmount = groupTotal,
                formattedTotalAmount = "${formatAmount(groupTotal)} T",
                expenses =
                    sortedExpenses.map { expense ->
                      toExpenseUiModel(expense, categoriesMap, timeZone)
                    },
            )
          }

  private fun formatGroupHeaderTitle(date: CalendarDate, today: CalendarDate): String {
    val monthName = getMonthName(date.month, date.type)
    val isToday = date.year == today.year && date.month == today.month && date.day == today.day
    val yesterday = activeCalendar.addDays(today, -1)
    val isYesterday =
        date.year == yesterday.year && date.month == yesterday.month && date.day == yesterday.day

    return when {
      isToday -> "Today, ${date.day} $monthName"
      isYesterday -> "Yesterday, ${date.day} $monthName"
      else -> "${date.day} $monthName ${date.year}"
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
        formattedAmount = "-${formatAmount(expense.amount)} T",
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
          formattedAmount = "-${formatAmount(amount)} T",
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
      headerTitle: String = "Today, 24 Oct",
      totalAmount: Long = 50_000,
      formattedTotalAmount: String = "50,000 T",
      expenses: List<AnalyticsExpenseUiModel> = listOf(FakeAnalyticsExpenseUiModelFactory.create()),
  ): AnalyticsDateGroupUiModel =
      AnalyticsDateGroupUiModel(
          date = date,
          headerTitle = headerTitle,
          totalAmount = totalAmount,
          formattedTotalAmount = formattedTotalAmount,
          expenses = expenses,
      )
}

object FakeAnalyticsStateFactory {
  fun create(
      selectedYear: Int = 2026,
      selectedMonth: Int = 10,
      canNavigateNext: Boolean = false,
      monthlyOutflow: Long = 50_000,
      dateGroups: List<AnalyticsDateGroupUiModel> =
          listOf(FakeAnalyticsDateGroupUiModelFactory.create()),
  ): AnalyticsState {
    val monthName = getMonthName(selectedMonth, CalendarType.GREGORIAN)
    return AnalyticsState(
        selectedYear = selectedYear,
        selectedMonth = selectedMonth,
        selectedMonthLabel = "$monthName $selectedYear",
        canNavigateNext = canNavigateNext,
        monthlyOutflow = monthlyOutflow,
        formattedMonthlyOutflow = "${formatAmount(monthlyOutflow)} T",
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
