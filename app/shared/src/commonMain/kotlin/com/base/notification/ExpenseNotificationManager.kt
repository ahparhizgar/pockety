package com.base.notification

interface ExpenseNotificationManager {
  fun showExpenseCandidateNotification(
      candidateId: Long,
      amount: Long,
      sender: String,
      rawBody: String,
  )

  fun cancelNotification(candidateId: Long)
}
