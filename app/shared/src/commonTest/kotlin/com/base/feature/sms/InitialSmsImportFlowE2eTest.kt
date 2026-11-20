package com.base.feature.sms

import com.base.component.TestCContext
import com.base.feature.root.RootComponentFactory
import com.base.sms.SmsInboxReader
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.assertHomeComponent
import com.base.util.assertInbox
import com.base.util.assertMainComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe

class InitialSmsImportFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())
    Given("SMS access has not been granted and the reader has the latest bank expense") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }
      beforeEach {
        (koin.get<SmsInboxReader>() as FakeSmsInboxReader).latest = FakeBankSmsFactory.create()
        root.childStack
        testCoroutineScheduler.advanceUntilIdle()
        root.assertMainComponent().assertHomeComponent()
      }
      When("permission is denied") {
        beforeEach {
          root.assertMainComponent().assertHomeComponent().onEnableSmsTrackingClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }
        Then("no historical candidate is shown") {
          root
              .assertMainComponent()
              .assertHomeComponent()
              .state
              .value
              .pendingCandidateCount shouldBe 0
        }
      }
      When("SMS access is temporarily unavailable on the first grant") {
        beforeEach {
          val reader = koin.get<SmsInboxReader>() as FakeSmsInboxReader
          reader.available = false
          context.fakePermissionController.setSmsPermissionGranted(true)
          testCoroutineScheduler.advanceUntilIdle()
          context.fakePermissionController.setSmsPermissionGranted(false)
          testCoroutineScheduler.advanceUntilIdle()
          reader.available = true
          context.fakePermissionController.setSmsPermissionGranted(true)
          testCoroutineScheduler.advanceUntilIdle()
        }
        Then("a later successful grant still imports the latest expense") {
          root
              .assertMainComponent()
              .assertHomeComponent()
              .state
              .value
              .pendingCandidateCount shouldBe 1
        }
      }
      When("SMS permission is granted") {
        beforeEach {
          context.fakePermissionController.setSmsPermissionGranted(true)
          testCoroutineScheduler.advanceUntilIdle()
        }
        Then("the latest bank expense appears in home state") {
          val state = root.assertMainComponent().assertHomeComponent().state.value
          state.pendingCandidateCount shouldBe 1
          state.pendingCandidateSum shouldBe 500000L
        }
        And("SMS access is revoked and granted again after a newer message arrives") {
          beforeEach {
            context.fakePermissionController.setSmsPermissionGranted(false)
            testCoroutineScheduler.advanceUntilIdle()
            (koin.get<SmsInboxReader>() as FakeSmsInboxReader).latest =
                FakeBankSmsFactory.create(700000L)
            context.fakePermissionController.setSmsPermissionGranted(true)
            testCoroutineScheduler.advanceUntilIdle()
          }
          Then("the initial import does not repeat") {
            val state = root.assertMainComponent().assertHomeComponent().state.value
            state.pendingCandidateCount shouldBe 1
            state.pendingCandidateSum shouldBe 500000L
          }
        }
        And("the pending SMS card is opened and notification permission is denied") {
          var notificationPromptShown = false
          beforeEach {
            context.fakePermissionController.onRequestNotificationPermission = {
              notificationPromptShown = true
              false
            }
            root.assertMainComponent().assertHomeComponent().onUndecidedExpensesClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }
          Then("the prompt was presented and the imported candidate remains reviewable") {
            notificationPromptShown shouldBe true
            root.assertInbox().state.value.candidates.single().amount shouldBe 500000L
          }
        }
      }
    }
    Given("no bank expense can be parsed on the first grant") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }
      When("permission is granted and later granted again with a matching message") {
        beforeEach {
          root.childStack
          testCoroutineScheduler.advanceUntilIdle()
          context.fakePermissionController.setSmsPermissionGranted(true)
          testCoroutineScheduler.advanceUntilIdle()
          context.fakePermissionController.setSmsPermissionGranted(false)
          testCoroutineScheduler.advanceUntilIdle()
          (koin.get<SmsInboxReader>() as FakeSmsInboxReader).latest = FakeBankSmsFactory.create()
          context.fakePermissionController.setSmsPermissionGranted(true)
          testCoroutineScheduler.advanceUntilIdle()
        }
        Then("the initial import stays complete and inbox remains empty") {
          root
              .assertMainComponent()
              .assertHomeComponent()
              .state
              .value
              .pendingCandidateCount shouldBe 0
        }
      }
    }
  }
}
