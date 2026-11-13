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
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
    val isLanguageDialogVisible: Boolean = false,
    val categoriesCount: Int = 0,
    val appVersion: String = "1.2.0",
)

interface SettingsComponent {
  val state: Value<SettingsState>

  fun onCalendarTypeSelected(type: CalendarType): Job

  fun onCurrencySelected(type: CurrencyType): Job

  fun onReminderToggled(enabled: Boolean): Job

  fun onLanguageSelected(language: AppLanguage): Job

  fun onDismissLanguageDialog()

  fun onCategoriesClicked()

  fun onLabelsClicked()

  fun onLanguageClicked()

  fun onPrivacyPolicyClicked()
}

interface SettingsComponentFactory {
  fun create(
      cContext: CContext,
      onCategoriesClick: () -> Unit = {},
      onLabelsClick: () -> Unit = {},
      onLanguageClick: () -> Unit = {},
      onPrivacyPolicyClick: () -> Unit = {},
  ): SettingsComponent
}

@Suppress("LongParameterList")
class DefaultSettingsComponent(
    cContext: CContext,
    private val userSettingsRepository: UserSettingsRepository,
    private val categoryRepository: CategoryRepository,
    private val languageSettingsHandler: LanguageSettingsHandler = DefaultLanguageSettingsHandler(),
    private val onCategoriesClick: () -> Unit = {},
    private val onLabelsClick: () -> Unit = {},
    private val onLanguageClick: () -> Unit = {},
    private val onPrivacyPolicyClick: () -> Unit = {},
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

    userSettingsRepository.appLanguage
        .onEach { language -> _state.update { it.copy(selectedLanguage = language) } }
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

  override fun onLanguageSelected(language: AppLanguage): Job = scope.launch {
    userSettingsRepository.setAppLanguage(language)
    _state.update { it.copy(isLanguageDialogVisible = false) }
  }

  override fun onDismissLanguageDialog() {
    _state.update { it.copy(isLanguageDialogVisible = false) }
  }

  override fun onCategoriesClicked() {
    onCategoriesClick()
  }

  override fun onLabelsClicked() {
    onLabelsClick()
  }

  override fun onLanguageClicked() {
    onLanguageClick()
    if (languageSettingsHandler.supportsSystemLocaleSettings) {
      languageSettingsHandler.openSystemLocaleSettings()
    } else {
      _state.update { it.copy(isLanguageDialogVisible = true) }
    }
  }

  override fun onPrivacyPolicyClicked() {
    onPrivacyPolicyClick()
  }
}

class DefaultSettingsComponentFactory(
    private val userSettingsRepository: UserSettingsRepository,
    private val categoryRepository: CategoryRepository,
    private val languageSettingsHandler: LanguageSettingsHandler = DefaultLanguageSettingsHandler(),
) : SettingsComponentFactory {
  override fun create(
      cContext: CContext,
      onCategoriesClick: () -> Unit,
      onLabelsClick: () -> Unit,
      onLanguageClick: () -> Unit,
      onPrivacyPolicyClick: () -> Unit,
  ): SettingsComponent =
      DefaultSettingsComponent(
          cContext = cContext,
          userSettingsRepository = userSettingsRepository,
          categoryRepository = categoryRepository,
          languageSettingsHandler = languageSettingsHandler,
          onCategoriesClick = onCategoriesClick,
          onLabelsClick = onLabelsClick,
          onLanguageClick = onLanguageClick,
          onPrivacyPolicyClick = onPrivacyPolicyClick,
      )
}

@Fake
@Suppress("LongParameterList")
class FakeSettingsComponent(
    initialState: SettingsState = FakeSettingsStateFactory.create(),
    private val onCategoriesClick: () -> Unit = {},
    private val onLabelsClick: () -> Unit = {},
    private val onLanguageClick: () -> Unit = {},
    private val onPrivacyPolicyClick: () -> Unit = {},
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

  override fun onLanguageSelected(language: AppLanguage): Job {
    _state.update { it.copy(selectedLanguage = language, isLanguageDialogVisible = false) }
    return Job().apply { complete() }
  }

  override fun onDismissLanguageDialog() {
    _state.update { it.copy(isLanguageDialogVisible = false) }
  }

  override fun onCategoriesClicked() {
    onCategoriesClick()
  }

  override fun onLabelsClicked() {
    onLabelsClick()
  }

  override fun onLanguageClicked() {
    onLanguageClick()
    _state.update { it.copy(isLanguageDialogVisible = true) }
  }

  override fun onPrivacyPolicyClicked() {
    onPrivacyPolicyClick()
  }
}

class FakeSettingsComponentFactory : SettingsComponentFactory {
  override fun create(
      cContext: CContext,
      onCategoriesClick: () -> Unit,
      onLabelsClick: () -> Unit,
      onLanguageClick: () -> Unit,
      onPrivacyPolicyClick: () -> Unit,
  ): SettingsComponent =
      FakeSettingsComponent(
          onCategoriesClick = onCategoriesClick,
          onLabelsClick = onLabelsClick,
          onLanguageClick = onLanguageClick,
          onPrivacyPolicyClick = onPrivacyPolicyClick,
      )
}

object FakeSettingsStateFactory {
  @Suppress("LongParameterList")
  fun create(
      selectedCalendarType: CalendarType = CalendarType.GREGORIAN,
      selectedCurrency: CurrencyType = CurrencyType.TOMANS,
      endOfDayReminderEnabled: Boolean = true,
      selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
      isLanguageDialogVisible: Boolean = false,
      categoriesCount: Int = 0,
      appVersion: String = "1.2.0",
  ): SettingsState =
      SettingsState(
          selectedCalendarType = selectedCalendarType,
          selectedCurrency = selectedCurrency,
          endOfDayReminderEnabled = endOfDayReminderEnabled,
          selectedLanguage = selectedLanguage,
          isLanguageDialogVisible = isLanguageDialogVisible,
          categoriesCount = categoriesCount,
          appVersion = appVersion,
      )
}
