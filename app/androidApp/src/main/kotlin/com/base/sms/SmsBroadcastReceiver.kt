package com.base.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.base.db.CandidateExpenseRepository
import com.base.notification.ExpenseNotificationManager
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SmsBroadcastReceiver(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : BroadcastReceiver(), KoinComponent {

  private val candidateExpenseRepository: CandidateExpenseRepository by inject()
  private val expenseNotificationManager: ExpenseNotificationManager by inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

    val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
    if (messages.isNullOrEmpty()) return

    val pendingResult = goAsync()
    scope.launch {
      try {
        val messagesBySender =
            messages.filterNotNull().groupBy { it.displayOriginatingAddress ?: "" }

        for ((sender, partList) in messagesBySender) {
          val fullBody = partList.joinToString("") { it.displayMessageBody ?: "" }
          processSms(
              receivedAt = Instant.fromEpochMilliseconds(partList.first().timestampMillis),
              sender = sender,
              body = fullBody,
              repository = candidateExpenseRepository,
              notificationManager = expenseNotificationManager,
          )
        }
      } finally {
        pendingResult?.finish()
      }
    }
  }

  suspend fun processSms(
      sender: String,
      body: String,
      repository: CandidateExpenseRepository,
      notificationManager: ExpenseNotificationManager? = null,
      receivedAt: Instant = Clock.System.now(),
  ): Long? {
    val parsed = SmsExpenseParser.parse(body) ?: return null
    val id =
        repository.addCandidate(
            sender = sender,
            rawBody = body,
            amount = parsed.amount,
            receivedAt = receivedAt,
        )
    notificationManager?.showExpenseCandidateNotification(
        candidateId = id,
        amount = parsed.amount,
        sender = sender,
        rawBody = body,
    )
    return id
  }
}
