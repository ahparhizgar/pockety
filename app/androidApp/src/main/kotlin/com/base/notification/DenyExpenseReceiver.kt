package com.base.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.base.db.CandidateExpenseRepository
import com.base.db.CandidateExpenseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DenyExpenseReceiver(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : BroadcastReceiver(), KoinComponent {

  private val candidateExpenseRepository: CandidateExpenseRepository by inject()
  private val expenseNotificationManager: ExpenseNotificationManager by inject()

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent == null) return
    val candidateId = intent.getLongExtra(EXTRA_CANDIDATE_ID, -1L)
    if (candidateId == -1L) return

    val pendingResult = goAsync()
    scope.launch {
      try {
        processDeny(
            candidateId = candidateId,
            repository = candidateExpenseRepository,
            notificationManager = expenseNotificationManager,
        )
      } finally {
        pendingResult?.finish()
      }
    }
  }

  suspend fun processDeny(
      candidateId: Long,
      repository: CandidateExpenseRepository,
      notificationManager: ExpenseNotificationManager,
  ) {
    notificationManager.cancelNotification(candidateId)
    repository.updateStatus(candidateId, CandidateExpenseStatus.REJECTED)
  }

  companion object {
    const val ACTION_DENY_EXPENSE = "com.base.notification.ACTION_DENY_EXPENSE"
    const val EXTRA_CANDIDATE_ID = "extra_candidate_id"
    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
  }
}
