package com.base.sms

import android.content.Context
import android.provider.Telephony
import com.base.permission.AndroidPermissionController
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AndroidSmsInboxReader(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val configStore: SmsRemoteConfigStore,
) : SmsInboxReader {
  override suspend fun latestExpense(): SmsInboxResult =
      withContext(ioDispatcher) {
        if (!AndroidPermissionController.checkIsSmsPermissionGranted(context))
            return@withContext SmsInboxResult.Unavailable
        val rules = configStore.currentRules()
        context.contentResolver
            .query(
                Telephony.Sms.Inbox.CONTENT_URI,
                arrayOf(
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.BODY,
                    Telephony.Sms.DATE,
                    Telephony.Sms.DATE_SENT,
                ),
                null,
                null,
                "${Telephony.Sms.DATE} DESC, ${Telephony.Sms._ID} DESC",
            )
            ?.use { cursor ->
              val senderColumn = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
              val bodyColumn = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
              val dateColumn = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
              val sentDateColumn = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE_SENT)
              val messages = sequence {
                while (cursor.moveToNext()) {
                  val body = cursor.getString(bodyColumn) ?: continue
                  yield(
                      ReceivedSms(
                          sender = cursor.getString(senderColumn).orEmpty(),
                          body = body,
                          receivedAt =
                              Instant.fromEpochMilliseconds(
                                  cursor.getLong(sentDateColumn).takeIf { it > 0 }
                                      ?: cursor.getLong(dateColumn)
                              ),
                      )
                  )
                }
              }
              return@withContext SmsInboxResult.Available(latestBankExpense(messages, rules))
            }
        SmsInboxResult.Unavailable
      }
}
