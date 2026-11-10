package com.base.feature.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.base.calendar.CalendarType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

interface UserSettingsRepository {
  val calendarType: Flow<CalendarType>

  suspend fun setCalendarType(type: CalendarType)
}

class DataStoreUserSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : UserSettingsRepository {
  private val calendarTypeKey = stringPreferencesKey("calendar_system")

  override val calendarType: Flow<CalendarType> =
      dataStore.data.map { preferences ->
        val saved = preferences[calendarTypeKey]
        saved?.let { runCatching { CalendarType.valueOf(it) }.getOrNull() }
            ?: CalendarType.GREGORIAN
      }

  override suspend fun setCalendarType(type: CalendarType) {
    dataStore.edit { preferences -> preferences[calendarTypeKey] = type.name }
  }
}

class FakeUserSettingsRepository(
    initialCalendarType: CalendarType = CalendarType.GREGORIAN,
) : UserSettingsRepository {
  private val _calendarType = MutableStateFlow(initialCalendarType)
  override val calendarType: Flow<CalendarType> = _calendarType

  override suspend fun setCalendarType(type: CalendarType) {
    _calendarType.value = type
  }
}
