package com.base.feature.sms

import com.base.sms.BankSms
import com.base.sms.ReceivedSms
import com.base.sms.SmsImportHistory
import com.base.sms.SmsInboxReader
import com.base.sms.SmsInboxResult
import kotlin.time.Instant

class FakeSmsInboxReader : SmsInboxReader {
  var latest: BankSms? = null

  var available = true

  override suspend fun latestExpense(): SmsInboxResult =
      if (available) SmsInboxResult.Available(latest) else SmsInboxResult.Unavailable
}

class FakeSmsImportHistory : SmsImportHistory {
  var complete = false

  override suspend fun isComplete(): Boolean = complete

  override suspend fun markComplete() {
    complete = true
  }
}

object FakeBankSmsFactory {
  fun create(amount: Long = 500000L): BankSms =
      BankSms(
          sender = "Test Bank",
          body = "برداشت: $amount ریال",
          amount = amount,
          receivedAt = Instant.fromEpochMilliseconds(1000),
      )
}

object FakeReceivedSmsFactory {
  fun create(body: String = "برداشت: 500000 ریال"): ReceivedSms =
      ReceivedSms(
          sender = "Test Bank",
          body = body,
          receivedAt = Instant.fromEpochMilliseconds(1000),
      )
}
