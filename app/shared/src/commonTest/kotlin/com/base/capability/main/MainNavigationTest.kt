package com.base.capability.main

import com.base.component.TestCContext
import com.base.db.CandidateExpenseRepository
import com.base.testValue
import com.base.ui.components.PocketyTab
import com.base.util.MainDispatcherExtension
import com.base.util.assertHomeComponent
import com.base.util.assertInboxComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class MainNavigationTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("DefaultMainComponent") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val factory by testValue { koin.get<MainComponentFactory>() }
      val candidateRepo by testValue { koin.get<CandidateExpenseRepository>() }
      val main by testValue { factory.create(context) }

      When("cold started") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("active child is Home") { main.assertHomeComponent() }

        Then("backStack is empty so back on Home closes the application") {
          main.stack.value.backStack shouldHaveSize 0
        }

        Then("inbox badge count is 0") { main.state.value.inboxBadgeCount shouldBe 0 }
      }

      When("pending candidates exist in repository") {
        beforeEach {
          candidateRepo.addCandidate(
              sender = "Bank Mellat",
              rawBody = "Withdrawal: 50,000 Toman",
              amount = 50000L,
          )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("inbox badge count reflects pending candidates") {
          main.state.value.inboxBadgeCount shouldBe 1
        }

        And("user selects Inbox tab") {
          beforeEach {
            main.onTabSelect(PocketyTab.Inbox)
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("active child is Inbox and backStack contains only Home") {
            main.assertInboxComponent()
            main.stack.value.backStack shouldHaveSize 1
          }

          And("user selects Settings tab directly from Inbox") {
            beforeEach {
              main.onTabSelect(PocketyTab.Settings)
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("active child is Settings and backStack contains only Home") {
              main.stack.value.active.instance.shouldBeInstanceOf<MainComponent.Child.Settings>()
              main.stack.value.backStack shouldHaveSize 1
            }

            And("user presses back") {
              beforeEach {
                main.onBack()
                testCoroutineScheduler.advanceUntilIdle()
              }

              Then("navigates directly back to Home") {
                main.assertHomeComponent()
                main.stack.value.backStack shouldHaveSize 0
              }
            }
          }
        }
      }
    }
  }
}
