package com.base.sms

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.base.Fake
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

/** Downloads the remote config document; returns null on any failure. */
fun interface SmsRemoteConfigFetcher {
  suspend fun fetch(url: String): String?
}

class KtorSmsRemoteConfigFetcher : SmsRemoteConfigFetcher {
  // A dedicated client: the shared one carries the user's API token, which
  // must never be sent to the config host.
  override suspend fun fetch(url: String): String? =
      try {
        HttpClient().use { client ->
          val response = client.get(url)
          if (response.status.isSuccess()) response.bodyAsText() else null
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        null
      }
}

interface SmsRemoteConfigStore {
  /** The last successfully fetched rules, or empty rules when none are cached. */
  suspend fun currentRules(): SmsRemoteRules

  /**
   * Fetches a fresh config when the previous attempt is at least a day old. Failures are silent by
   * design: the cached config (if any) keeps applying.
   */
  suspend fun refreshIfDue(now: Instant)
}

class DefaultSmsRemoteConfigStore(
    private val dataStore: DataStore<Preferences>,
    private val fetcher: SmsRemoteConfigFetcher,
    private val url: String = SMS_REMOTE_CONFIG_URL,
) : SmsRemoteConfigStore {
  private val configKey = stringPreferencesKey("sms_remote_config_json")
  private val lastAttemptKey = longPreferencesKey("sms_remote_config_last_attempt_ms")
  private val json = Json { ignoreUnknownKeys = true }

  override suspend fun currentRules(): SmsRemoteRules {
    val stored = dataStore.data.first()[configKey] ?: return SmsRemoteRules()
    return SmsRemoteRules(decode(stored))
  }

  override suspend fun refreshIfDue(now: Instant) {
    val lastAttempt = dataStore.data.first()[lastAttemptKey]
    if (lastAttempt != null && now.toEpochMilliseconds() - lastAttempt < REFRESH_INTERVAL_MS) {
      return
    }
    // Stamp the attempt before fetching: a failed fetch also waits a day, so
    // the app requests at most once a day and never surfaces an error.
    dataStore.edit { preferences -> preferences[lastAttemptKey] = now.toEpochMilliseconds() }
    val body = fetcher.fetch(url) ?: return
    // A malformed document must not replace a working cached config.
    decodeOrNull(body) ?: return
    dataStore.edit { preferences -> preferences[configKey] = body }
  }

  private fun decode(text: String): SmsRemoteConfig = decodeOrNull(text) ?: SmsRemoteConfig()

  private fun decodeOrNull(text: String): SmsRemoteConfig? = runCatching {
    json.decodeFromString<SmsRemoteConfig>(text)
  }.getOrNull()

  private companion object {
    val REFRESH_INTERVAL_MS = 1.days.inWholeMilliseconds
  }
}

@Fake
class FakeSmsRemoteConfigStore(
    private val rules: SmsRemoteRules = SmsRemoteRules(),
) : SmsRemoteConfigStore {
  var refreshCount = 0
    private set

  override suspend fun currentRules(): SmsRemoteRules = rules

  override suspend fun refreshIfDue(now: Instant) {
    refreshCount++
  }
}
