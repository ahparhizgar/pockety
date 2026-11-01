package com.base.feature.analytics

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CategoryEntity
import com.base.db.CategoryRepository
import com.base.db.ExpenseEntity
import com.base.db.ExpenseRepository
import com.base.feature.pocket.details.formatAmount
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
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
    val date: LocalDate,
    val headerTitle: String,
    val totalAmount: Long,
    val formattedTotalAmount: String,
    val expenses: List<AnalyticsExpenseUiModel>,
)

data class AnalyticsState(
    val selectedYear: Int =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
    val selectedMonth: Int =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).month.ordinal + 1,
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

private const val FIRST_MONTH = 1
private const val MONTHS_IN_YEAR = 12

class DefaultAnalyticsComponent(
    cContext: CContext,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val onExpenseClick: (Long) -> Unit,
) : AnalyticsComponent, CContext by cContext {
  private val scope = componentScope()

  private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
  private val currentCalendarYear = now.year
  private val currentCalendarMonth = now.month.ordinal + 1

  private var currentYear = currentCalendarYear
  private var currentMonth = currentCalendarMonth

  private var rawExpenses: List<ExpenseEntity> = emptyList()
  private var rawCategories: List<CategoryEntity> = emptyList()

  private val _state = MutableValue(buildState())
  override val state: Value<AnalyticsState> = _state

  init {
    combine(
            expenseRepository.observeExpenses(),
            categoryRepository.observeCategories(),
        ) { expenses, categories ->
          rawExpenses = expenses
          rawCategories = categories
          buildState()
        }
        .onEach { newState -> _state.value = newState }
        .launchIn(scope)
  }

  override fun onPreviousMonthClicked() {
    if (currentMonth == FIRST_MONTH) {
      currentMonth = MONTHS_IN_YEAR
      currentYear -= 1
    } else {
      currentMonth -= 1
    }
    _state.value = buildState()
  }

  override fun onNextMonthClicked() {
    val canNext =
        currentYear < currentCalendarYear ||
            (currentYear == currentCalendarYear && currentMonth < currentCalendarMonth)
    if (!canNext) return

    if (currentMonth == MONTHS_IN_YEAR) {
      currentMonth = FIRST_MONTH
      currentYear += 1
    } else {
      currentMonth += 1
    }
    _state.value = buildState()
  }

  override fun onExpenseClicked(id: Long) {
    onExpenseClick(id)
  }

  private fun buildState(): AnalyticsState {
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val canNavigateNext =
        currentYear < currentCalendarYear ||
            (currentYear == currentCalendarYear && currentMonth < currentCalendarMonth)

    val monthName =
        Month.entries[currentMonth - 1].name.lowercase().replaceFirstChar { it.uppercase() }
    val selectedMonthLabel = "$monthName $currentYear"

    val monthExpenses = rawExpenses.filter { expense ->
      val local = expense.createdAt.toLocalDateTime(timeZone)
      local.year == currentYear && local.month.ordinal + 1 == currentMonth
    }

    val monthlyOutflow = monthExpenses.sumOf { it.amount }
    val formattedMonthlyOutflow = "${formatAmount(monthlyOutflow)} T"

    val categoriesMap = rawCategories.associateBy { it.id }
    val dateGroups = buildDateGroups(monthExpenses, categoriesMap, timeZone, today)

    return AnalyticsState(
        selectedYear = currentYear,
        selectedMonth = currentMonth,
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
      today: LocalDate,
  ): List<AnalyticsDateGroupUiModel> =
      monthExpenses
          .groupBy { it.createdAt.toLocalDateTime(timeZone).date }
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

  private fun formatGroupHeaderTitle(date: LocalDate, today: LocalDate): String {
    val monthShort = date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    return when {
      date == today -> "Today, ${date.day} $monthShort"
      date.toEpochDays() == today.toEpochDays() - 1 -> "Yesterday, ${date.day} $monthShort"
      else -> "${date.day} $monthShort ${date.year}"
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
) : AnalyticsComponentFactory {
  override fun create(
      cContext: CContext,
      onExpenseClick: (Long) -> Unit,
  ): AnalyticsComponent =
      DefaultAnalyticsComponent(
          cContext = cContext,
          expenseRepository = expenseRepository,
          categoryRepository = categoryRepository,
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
  fun create(
      date: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
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
    val monthName =
        Month.entries[selectedMonth - 1].name.lowercase().replaceFirstChar { it.uppercase() }
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

  override fun onPreviousMonthClicked() {
    val newMonth = if (_state.value.selectedMonth == 1) 12 else _state.value.selectedMonth - 1
    val newYear =
        if (_state.value.selectedMonth == 1) _state.value.selectedYear - 1
        else _state.value.selectedYear
    val monthName = Month.entries[newMonth - 1].name.lowercase().replaceFirstChar { it.uppercase() }
    _state.update {
      it.copy(
          selectedMonth = newMonth,
          selectedYear = newYear,
          selectedMonthLabel = "$monthName $newYear",
          canNavigateNext = true,
      )
    }
  }

  override fun onNextMonthClicked() {
    val newMonth = if (_state.value.selectedMonth == 12) 1 else _state.value.selectedMonth + 1
    val newYear =
        if (_state.value.selectedMonth == 12) _state.value.selectedYear + 1
        else _state.value.selectedYear
    val monthName = Month.entries[newMonth - 1].name.lowercase().replaceFirstChar { it.uppercase() }
    _state.update {
      it.copy(
          selectedMonth = newMonth,
          selectedYear = newYear,
          selectedMonthLabel = "$monthName $newYear",
          canNavigateNext = false,
      )
    }
  }

  override fun onExpenseClicked(id: Long) {
    onExpenseClick(id)
  }
}

@Fake
class FakeAnalyticsComponentFactory : AnalyticsComponentFactory {
  override fun create(
      cContext: CContext,
      onExpenseClick: (Long) -> Unit,
  ): AnalyticsComponent =
      FakeAnalyticsComponent(
          onExpenseClick = onExpenseClick,
      )
}
