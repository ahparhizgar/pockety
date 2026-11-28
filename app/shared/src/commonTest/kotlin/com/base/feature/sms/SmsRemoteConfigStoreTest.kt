package com.base.feature.sms

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.base.sms.DefaultSmsRemoteConfigStore
import com.base.sms.SmsRemoteConfigFetcher
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlin.time.Instant

class SmsRemoteConfigStoreTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("a remote config store backed by the app DataStore") {
      val koin by integrationKoin()
      val dataStore by testValue { koin.get<DataStore<Preferences>>() }
      val dayOne = Instant.fromEpochMilliseconds(0)
      val sameDay = Instant.fromEpochMilliseconds(12 * 60 * 60 * 1000L)
      val nextDay = Instant.fromEpochMilliseconds(25 * 60 * 60 * 1000L)

      When("the fetch succeeds") {
        var fetchCount by testValue { 0 }
        val store by testValue {
          DefaultSmsRemoteConfigStore(
              dataStore,
              SmsRemoteConfigFetcher {
                fetchCount++
                """{"include":["بانک آینده"],"exclude":["جایزه"]}"""
              },
          )
        }

        beforeEach {
          store.refreshIfDue(dayOne)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the fetched rules apply") {
          val rules = store.currentRules()
          rules.isIncluded("پیام بانک آینده") shouldBe true
          rules.isExcluded("شما برنده جایزه شدید") shouldBe true
        }

        Then("a second refresh on the same day does not fetch again") {
          store.refreshIfDue(sameDay)
          fetchCount shouldBe 1
        }

        Then("a refresh on the next day fetches again") {
          store.refreshIfDue(nextDay)
          fetchCount shouldBe 2
        }
      }

      When("the fetch fails") {
        var fetchCount by testValue { 0 }
        val store by testValue {
          DefaultSmsRemoteConfigStore(
              dataStore,
              SmsRemoteConfigFetcher {
                fetchCount++
                null
              },
          )
        }

        Then("nothing breaks, no rules apply, and the attempt still throttles") {
          store.refreshIfDue(dayOne)
          store.currentRules().isIncluded("هر متنی") shouldBe false
          store.refreshIfDue(sameDay)
          fetchCount shouldBe 1
        }
      }

      When("a later fetch returns a malformed document") {
        var document by testValue { """{"include":["بانک آینده"]}""" }
        val store by testValue {
          DefaultSmsRemoteConfigStore(dataStore, SmsRemoteConfigFetcher { document })
        }

        Then("the previously cached config is kept") {
          store.refreshIfDue(dayOne)
          document = "this is not json"
          store.refreshIfDue(nextDay)
          store.currentRules().isIncluded("پیام بانک آینده") shouldBe true
        }
      }
    }
  }
}
