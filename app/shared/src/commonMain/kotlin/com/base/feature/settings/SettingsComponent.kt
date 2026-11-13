package com.base.feature.settings

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.calendar.CalendarType
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CategoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class SettingsState(
    val selectedCalendarType: CalendarType = CalendarType.GREGORIAN,
    val selectedCurrency: CurrencyType = CurrencyType.TOMANS,
    val endOfDayReminderEnabled: Boolean = true,
    val categoriesCount: Int = 0,
    val appVersion: String = "1.2.0",
)

interface SettingsComponent {
  val state: Value<SettingsState>

  fun onCalendarTypeSelected(type: CalendarType): Job

  fun onCurrencySelected(type: CurrencyType): Job

  fun onReminderToggled(enabled: Boolean): Job

  fun onCategoriesClicked()

  fun onLabelsClicked()

  fun onLanguageClicked()
}

interface SettingsComponentFactory {
  fun create(
      cContext: CContext,
      onCategoriesClick: () -> Unit = {},
      onLabelsClick: () -> Unit = {},
      onLanguageClick: () -> Unit = {},
  ): SettingsComponent
}

class DefaultSettingsComponent(
    cContext: CContext,
    private val userSettingsRepository: UserSettingsRepository,
    private val categoryRepository: CategoryRepository,
    private val onCategoriesClick: () -> Unit = {},
    private val onLabelsClick: () -> Unit = {},
    private val onLanguageClick: () -> Unit = {},
) : SettingsComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state = MutableValue(SettingsState())
  override val state: Value<SettingsState> = _state

  init {
    userSettingsRepository.calendarType
        .onEach { type -> _state.update { it.copy(selectedCalendarType = type) } }
        .launchIn(scope)

    userSettingsRepository.currencyType
        .onEach { currency -> _state.update { it.copy(selectedCurrency = currency) } }
        .launchIn(scope)

    userSettingsRepository.endOfDayReminderEnabled
        .onEach { reminder -> _state.update { it.copy(endOfDayReminderEnabled = reminder) } }
        .launchIn(scope)

    categoryRepository
        .observeCategories()
        .onEach { categories -> _state.update { it.copy(categoriesCount = categories.size) } }
        .launchIn(scope)
  }

  override fun onCalendarTypeSelected(type: CalendarType): Job = scope.launch {
    userSettingsRepository.setCalendarType(type)
  }

  override fun onCurrencySelected(type: CurrencyType): Job = scope.launch {
    userSettingsRepository.setCurrencyType(type)
  }

  override fun onReminderToggled(enabled: Boolean): Job = scope.launch {
    userSettingsRepository.setEndOfDayReminderEnabled(enabled)
  }

  override fun onCategoriesClicked() {
    onCategoriesClick()
  }

  override fun onLabelsClicked() {
    onLabelsClick()
  }

  override fun onLanguageClicked() {
    onLanguageClick()
  }
}

class DefaultSettingsComponentFactory(
    private val userSettingsRepository: UserSettingsRepository,
    private val categoryRepository: CategoryRepository,
) : SettingsComponentFactory {
  override fun create(
      cContext: CContext,
      onCategoriesClick: () -> Unit,
      onLabelsClick: () -> Unit,
      onLanguageClick: () -> Unit,
  ): SettingsComponent =
      DefaultSettingsComponent(
          cContext = cContext,
          userSettingsRepository = userSettingsRepository,
          categoryRepository = categoryRepository,
          onCategoriesClick = onCategoriesClick,
          onLabelsClick = onLabelsClick,
          onLanguageClick = onLanguageClick,
      )
}

@Fake
class FakeSettingsComponent(
    initialState: SettingsState = FakeSettingsStateFactory.create(),
    private val onCategoriesClick: () -> Unit = {},
    private val onLabelsClick: () -> Unit = {},
    private val onLanguageClick: () -> Unit = {},
) : SettingsComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<SettingsState> = _state

  override fun onCalendarTypeSelected(type: CalendarType): Job {
    _state.update { it.copy(selectedCalendarType = type) }
    return Job().apply { complete() }
  }

  override fun onCurrencySelected(type: CurrencyType): Job {
    _state.update { it.copy(selectedCurrency = type) }
    return Job().apply { complete() }
  }

  override fun onReminderToggled(enabled: Boolean): Job {
    _state.update { it.copy(endOfDayReminderEnabled = enabled) }
    return Job().apply { complete() }
  }

  override fun onCategoriesClicked() {
    onCategoriesClick()
  }

  override fun onLabelsClicked() {
    onLabelsClick()
  }

  override fun onLanguageClicked() {
    onLanguageClick()
  }
}

class FakeSettingsComponentFactory : SettingsComponentFactory {
  override fun create(
      cContext: CContext,
      onCategoriesClick: () -> Unit,
      onLabelsClick: () -> Unit,
      onLanguageClick: () -> Unit,
  ): SettingsComponent =
      FakeSettingsComponent(
          onCategoriesClick = onCategoriesClick,
          onLabelsClick = onLabelsClick,
          onLanguageClick = onLanguageClick,
      )
}

object FakeSettingsStateFactory {
  fun create(
      selectedCalendarType: CalendarType = CalendarType.GREGORIAN,
      selectedCurrency: CurrencyType = CurrencyType.TOMANS,
      endOfDayReminderEnabled: Boolean = true,
      categoriesCount: Int = 0,
      appVersion: String = "1.2.0",
  ): SettingsState =
      SettingsState(
          selectedCalendarType = selectedCalendarType,
          selectedCurrency = selectedCurrency,
          endOfDayReminderEnabled = endOfDayReminderEnabled,
          categoriesCount = categoriesCount,
          appVersion = appVersion,
      )
}
