package com.base.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.base.MainActivity
import ir.ahparhizgar.pockety.R

class AndroidExpenseNotificationManager(
    private val context: Context,
) : ExpenseNotificationManager {

  private val notificationManager = NotificationManagerCompat.from(context)

  init {
    createNotificationChannel()
  }

  fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = context.getString(R.string.notification_channel_name)
      val descriptionText = context.getString(R.string.notification_channel_description)
      val importance = NotificationManager.IMPORTANCE_LOW
      val channel =
          NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            setSound(null, null)
            enableVibration(false)
          }
      val systemNotificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      systemNotificationManager?.createNotificationChannel(channel)
    }
  }

  fun buildNotification(
      candidateId: Long,
      amount: Long,
      sender: String,
      rawBody: String,
  ): NotificationCompat.Builder {
    val contentIntent =
        Intent(context, MainActivity::class.java).apply {
          action = MainActivity.ACTION_OPEN_ADD_EXPENSE
          flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
          putExtra(MainActivity.EXTRA_NAV_CANDIDATE_ID, candidateId)
          putExtra(MainActivity.EXTRA_NAV_AMOUNT, amount.toString())
        }
    val contentPendingIntent =
        PendingIntent.getActivity(
            context,
            candidateId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    val denyIntent =
        Intent(context, DenyExpenseReceiver::class.java).apply {
          action = DenyExpenseReceiver.ACTION_DENY_EXPENSE
          putExtra(DenyExpenseReceiver.EXTRA_CANDIDATE_ID, candidateId)
          putExtra(DenyExpenseReceiver.EXTRA_NOTIFICATION_ID, candidateId.toInt())
        }
    val denyPendingIntent =
        PendingIntent.getBroadcast(
            context,
            candidateId.toInt(),
            denyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    val title = context.getString(R.string.notification_expense_title)
    val body = context.getString(R.string.notification_expense_body, amount, sender)
    val denyActionText = context.getString(R.string.notification_action_deny)

    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification_expense)
        .setContentTitle(title)
        .setContentText(body)
        .setStyle(NotificationCompat.BigTextStyle().bigText(rawBody.ifBlank { body }))
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setSound(null)
        .setVibrate(null)
        .setAutoCancel(true)
        .setContentIntent(contentPendingIntent)
        .addAction(0, denyActionText, denyPendingIntent)
  }

  override fun showExpenseCandidateNotification(
      candidateId: Long,
      amount: Long,
      sender: String,
      rawBody: String,
  ) {
    if (!notificationManager.areNotificationsEnabled()) {
      return
    }

    val notification =
        buildNotification(
                candidateId = candidateId,
                amount = amount,
                sender = sender,
                rawBody = rawBody,
            )
            .build()
    notificationManager.notify(candidateId.toInt(), notification)
  }

  override fun cancelNotification(candidateId: Long) {
    notificationManager.cancel(candidateId.toInt())
  }

  companion object {
    const val CHANNEL_ID = "candidate_expense_channel"
  }
}
