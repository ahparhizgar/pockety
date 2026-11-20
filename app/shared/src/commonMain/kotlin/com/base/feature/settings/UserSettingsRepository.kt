package com.base.feature.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.base.calendar.CalendarType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

enum class CurrencyType {
  TOMANS,
  KILO_TOMANS,
  RIALS,
}

interface UserSettingsRepository {
  val calendarType: Flow<CalendarType>
  val currencyType: Flow<CurrencyType>
  val endOfDayReminderEnabled: Flow<Boolean>
  val appLanguage: Flow<AppLanguage>

  suspend fun setCalendarType(type: CalendarType)

  suspend fun setCurrencyType(type: CurrencyType)

  suspend fun setEndOfDayReminderEnabled(enabled: Boolean)

  suspend fun setAppLanguage(language: AppLanguage)
}

class DataStoreUserSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : UserSettingsRepository {
  private val calendarTypeKey = stringPreferencesKey("calendar_system")
  private val currencyTypeKey = stringPreferencesKey("currency_type")
  private val endOfDayReminderKey = booleanPreferencesKey("end_of_day_reminder")
  private val appLanguageKey = stringPreferencesKey("app_language")

  override val calendarType: Flow<CalendarType> =
      dataStore.data.map { preferences ->
        val saved = preferences[calendarTypeKey]
        saved?.let { runCatching { CalendarType.valueOf(it) }.getOrNull() }
            ?: CalendarType.GREGORIAN
      }

  override val currencyType: Flow<CurrencyType> =
      dataStore.data.map { preferences ->
        val saved = preferences[currencyTypeKey]
        saved?.let { runCatching { CurrencyType.valueOf(it) }.getOrNull() } ?: CurrencyType.TOMANS
      }

  override val endOfDayReminderEnabled: Flow<Boolean> =
      dataStore.data.map { preferences -> preferences[endOfDayReminderKey] ?: true }

  override val appLanguage: Flow<AppLanguage> =
      dataStore.data.map { preferences ->
        val saved = preferences[appLanguageKey]
        saved?.let { AppLanguage.fromCode(it) } ?: AppLanguage.ENGLISH
      }

  override suspend fun setCalendarType(type: CalendarType) {
    dataStore.edit { preferences -> preferences[calendarTypeKey] = type.name }
  }

  override suspend fun setCurrencyType(type: CurrencyType) {
    dataStore.edit { preferences -> preferences[currencyTypeKey] = type.name }
  }

  override suspend fun setEndOfDayReminderEnabled(enabled: Boolean) {
    dataStore.edit { preferences -> preferences[endOfDayReminderKey] = enabled }
  }

  override suspend fun setAppLanguage(language: AppLanguage) {
    dataStore.edit { preferences -> preferences[appLanguageKey] = language.code }
  }
}

class FakeUserSettingsRepository(
    initialCalendarType: CalendarType = CalendarType.GREGORIAN,
    initialCurrencyType: CurrencyType = CurrencyType.TOMANS,
    initialEndOfDayReminderEnabled: Boolean = true,
    initialAppLanguage: AppLanguage = AppLanguage.ENGLISH,
) : UserSettingsRepository {
  private val _calendarType = MutableStateFlow(initialCalendarType)
  override val calendarType: Flow<CalendarType> = _calendarType

  private val _currencyType = MutableStateFlow(initialCurrencyType)
  override val currencyType: Flow<CurrencyType> = _currencyType

  private val _endOfDayReminderEnabled = MutableStateFlow(initialEndOfDayReminderEnabled)
  override val endOfDayReminderEnabled: Flow<Boolean> = _endOfDayReminderEnabled

  private val _appLanguage = MutableStateFlow(initialAppLanguage)
  override val appLanguage: Flow<AppLanguage> = _appLanguage

  override suspend fun setCalendarType(type: CalendarType) {
    _calendarType.value = type
  }

  override suspend fun setCurrencyType(type: CurrencyType) {
    _currencyType.value = type
  }

  override suspend fun setEndOfDayReminderEnabled(enabled: Boolean) {
    _endOfDayReminderEnabled.value = enabled
  }

  override suspend fun setAppLanguage(language: AppLanguage) {
    _appLanguage.value = language
  }
}
