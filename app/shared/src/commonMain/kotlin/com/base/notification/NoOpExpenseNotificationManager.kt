package com.base.notification

import com.base.Fake

class NoOpExpenseNotificationManager : ExpenseNotificationManager {
  override fun showExpenseCandidateNotification(
      candidateId: Long,
      amount: Long,
      sender: String,
      rawBody: String,
  ) {
    // No-op
  }

  override fun cancelNotification(candidateId: Long) {
    // No-op
  }
}

@Fake
class FakeExpenseNotificationManager : ExpenseNotificationManager {
  data class NotificationCall(
      val candidateId: Long,
      val amount: Long,
      val sender: String,
      val rawBody: String,
  )

  val shownNotifications = mutableListOf<NotificationCall>()
  val shownCandidateIds = mutableListOf<Long>()
  val cancelledCandidateIds = mutableListOf<Long>()

  override fun showExpenseCandidateNotification(
      candidateId: Long,
      amount: Long,
      sender: String,
      rawBody: String,
  ) {
    shownNotifications.add(NotificationCall(candidateId, amount, sender, rawBody))
    shownCandidateIds.add(candidateId)
  }

  override fun cancelNotification(candidateId: Long) {
    cancelledCandidateIds.add(candidateId)
  }
}
