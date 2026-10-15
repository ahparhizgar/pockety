package com.base.capability.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * Abstraction for storing authentication tokens. Implement platform-specific persistence (e.g.
 * DataStore on Android) where possible.
 */
interface TokenStorage {
  suspend fun saveAccessToken(token: String)

  suspend fun getAccessToken(): String?

  suspend fun clearAccessToken()
}

class DataStoreTokenStorage(
    private val dataStore: DataStore<Preferences>,
) : TokenStorage {
  private val accessTokenKey = stringPreferencesKey("access_token")

  override suspend fun saveAccessToken(token: String) {
    dataStore.edit { preferences -> preferences[accessTokenKey] = token }
  }

  override suspend fun getAccessToken(): String? {
    return dataStore.data.map { preferences -> preferences[accessTokenKey] }.firstOrNull()
  }

  override suspend fun clearAccessToken() {
    dataStore.edit { preferences -> preferences.remove(accessTokenKey) }
  }
}

class FakeTokenStorage(private var token: String? = null) : TokenStorage {
  override suspend fun saveAccessToken(token: String) {
    delay(1.milliseconds)
    this.token = token
  }

  override suspend fun getAccessToken(): String? {
    delay(1.milliseconds)
    return token
  }

  override suspend fun clearAccessToken() {
    delay(1.milliseconds)
    this.token = null
  }
}
