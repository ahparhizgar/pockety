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
  RIALS,
}

interface UserSettingsRepository {
  val calendarType: Flow<CalendarType>
  val currencyType: Flow<CurrencyType>
  val endOfDayReminderEnabled: Flow<Boolean>

  suspend fun setCalendarType(type: CalendarType)

  suspend fun setCurrencyType(type: CurrencyType)

  suspend fun setEndOfDayReminderEnabled(enabled: Boolean)
}

class DataStoreUserSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : UserSettingsRepository {
  private val calendarTypeKey = stringPreferencesKey("calendar_system")
  private val currencyTypeKey = stringPreferencesKey("currency_type")
  private val endOfDayReminderKey = booleanPreferencesKey("end_of_day_reminder")

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

  override suspend fun setCalendarType(type: CalendarType) {
    dataStore.edit { preferences -> preferences[calendarTypeKey] = type.name }
  }

  override suspend fun setCurrencyType(type: CurrencyType) {
    dataStore.edit { preferences -> preferences[currencyTypeKey] = type.name }
  }

  override suspend fun setEndOfDayReminderEnabled(enabled: Boolean) {
    dataStore.edit { preferences -> preferences[endOfDayReminderKey] = enabled }
  }
}

class FakeUserSettingsRepository(
    initialCalendarType: CalendarType = CalendarType.GREGORIAN,
    initialCurrencyType: CurrencyType = CurrencyType.TOMANS,
    initialEndOfDayReminderEnabled: Boolean = true,
) : UserSettingsRepository {
  private val _calendarType = MutableStateFlow(initialCalendarType)
  override val calendarType: Flow<CalendarType> = _calendarType

  private val _currencyType = MutableStateFlow(initialCurrencyType)
  override val currencyType: Flow<CurrencyType> = _currencyType

  private val _endOfDayReminderEnabled = MutableStateFlow(initialEndOfDayReminderEnabled)
  override val endOfDayReminderEnabled: Flow<Boolean> = _endOfDayReminderEnabled

  override suspend fun setCalendarType(type: CalendarType) {
    _calendarType.value = type
  }

  override suspend fun setCurrencyType(type: CurrencyType) {
    _currencyType.value = type
  }

  override suspend fun setEndOfDayReminderEnabled(enabled: Boolean) {
    _endOfDayReminderEnabled.value = enabled
  }
}
