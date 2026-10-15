package com.base.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.base.db.CandidateExpenseRepository
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

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

    val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
    if (messages.isNullOrEmpty()) return

    val messagesBySender = messages.filterNotNull().groupBy { it.displayOriginatingAddress ?: "" }

    for ((sender, partList) in messagesBySender) {
      val fullBody = partList.joinToString("") { it.displayMessageBody ?: "" }
      val parsed = SmsExpenseParser.parse(fullBody) ?: continue

      val pendingResult = goAsync()
      scope.launch {
        try {
          candidateExpenseRepository.addCandidate(
              sender = sender,
              rawBody = fullBody,
              amount = parsed.amount,
          )
        } finally {
          pendingResult.finish()
        }
      }
    }
  }

  suspend fun processSms(
      sender: String,
      body: String,
      repository: CandidateExpenseRepository,
  ): Long? {
    val parsed = SmsExpenseParser.parse(body) ?: return null
    return repository.addCandidate(
        sender = sender,
        rawBody = body,
        amount = parsed.amount,
    )
  }
}
