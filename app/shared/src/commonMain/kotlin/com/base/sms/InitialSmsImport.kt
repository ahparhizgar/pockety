package com.base.sms

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.base.db.CandidateExpenseRepository
import kotlin.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Provides only the newest parsable expense, without retaining the rest of the inbox. */
interface SmsInboxReader {
  suspend fun latestExpense(): SmsInboxResult
}

sealed interface SmsInboxResult {
  data object Unavailable : SmsInboxResult

  data class Available(val latest: BankSms?) : SmsInboxResult
}

data class ReceivedSms(val sender: String, val body: String, val receivedAt: Instant)

fun latestBankExpense(
    messagesNewestFirst: Sequence<ReceivedSms>,
    rules: SmsRemoteRules = SmsRemoteRules(),
): BankSms? =
    messagesNewestFirst
        .mapNotNull { sms ->
          SmsExpenseParser.parse(sms.body, rules)?.let {
            BankSms(sms.sender, sms.body, it.amount, sms.receivedAt)
          }
        }
        .firstOrNull()

data class BankSms(val sender: String, val body: String, val amount: Long, val receivedAt: Instant)

interface SmsImportHistory {
  suspend fun isComplete(): Boolean

  suspend fun markComplete()
}

class DataStoreSmsImportHistory(private val dataStore: DataStore<Preferences>) : SmsImportHistory {
  private val completedKey = booleanPreferencesKey("initial_bank_sms_import_complete")

  override suspend fun isComplete(): Boolean = dataStore.data.first()[completedKey] ?: false

  override suspend fun markComplete() {
    dataStore.edit { it[completedKey] = true }
  }
}

class InitialSmsImport(
    private val reader: SmsInboxReader,
    private val history: SmsImportHistory,
    private val repository: CandidateExpenseRepository,
) {
  private val mutex = Mutex()

  suspend fun importOnce() = mutex.withLock {
    if (!history.isComplete()) {
      val result = reader.latestExpense()
      if (result is SmsInboxResult.Available) {
        result.latest?.let { sms ->
          repository.addCandidate(sms.sender, sms.body, sms.amount, sms.receivedAt)
        }
        history.markComplete()
      }
    }
  }
}

class NoOpSmsInboxReader : SmsInboxReader {
  override suspend fun latestExpense(): SmsInboxResult = SmsInboxResult.Available(null)
}
