package com.base.capability.sms_inbox

import com.base.component.TestCContext
import com.base.db.CandidateExpenseRepository
import com.base.capability.adding_expense.add.AddExpenseComponent
import com.base.capability.root_navigation.RootComponentFactory
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.assertHomeComponent
import com.base.util.assertMainComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class InboxFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("user opens application with candidate expenses") {
      val koin by integrationKoin()
      val context by testValue {
        TestCContext().apply { fakePermissionController.setSmsPermissionGranted(true) }
      }
      val candidateRepo by testValue { koin.get<CandidateExpenseRepository>() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      beforeEach {
        candidateRepo.addCandidate(
            sender = "Bank Mellat",
            rawBody = "Withdrawal: 45,000 Toman",
            amount = 45000L,
        )
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("user clicks undecided expenses banner on Home screen") {
        beforeEach {
          val home = root.assertMainComponent().assertHomeComponent()
          home.onUndecidedExpensesClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("navigates to Inbox screen listing pending candidate expenses") {
          val inbox = root.childStack.value.active.instance.shouldBeInstanceOf<InboxComponent>()
          inbox.state.value.candidates shouldHaveSize 1
          inbox.state.value.candidates[0].amount shouldBe 45000L
        }

        And("user selects a candidate expense from Inbox") {
          beforeEach {
            val inbox = root.childStack.value.active.instance.shouldBeInstanceOf<InboxComponent>()
            val candidate = inbox.state.value.candidates.first()
            inbox.onSelectCandidate(candidate)
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("navigates to AddExpense screen with amount prefilled") {
            val addExpense =
                root.childStack.value.active.instance.shouldBeInstanceOf<AddExpenseComponent>()
            addExpense.state.value.amountText shouldBe "45000"
          }

          And("user enters description and saves the expense") {
            beforeEach {
              val addExpense =
                  root.childStack.value.active.instance.shouldBeInstanceOf<AddExpenseComponent>()
              addExpense.onDescriptionChanged("Groceries")
              addExpense.onSaveClicked()
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("navigates back to Home screen and shows the saved expense") {
              val home = root.assertMainComponent().assertHomeComponent()
              home.state.value.expenses shouldHaveSize 1
              home.state.value.expenses[0].amount shouldBe 45000L
              home.state.value.expenses[0].description shouldBe "Groceries"
              home.state.value.pendingCandidateCount shouldBe 0
            }
          }
        }

        And("user dismisses candidate expense from Inbox") {
          beforeEach {
            val inbox = root.childStack.value.active.instance.shouldBeInstanceOf<InboxComponent>()
            val candidateId = inbox.state.value.candidates.first().id
            inbox.onDismiss(candidateId)
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("candidate is removed from Inbox state") {
            val inbox = root.childStack.value.active.instance.shouldBeInstanceOf<InboxComponent>()
            inbox.state.value.candidates shouldHaveSize 0
          }

          And("user clicks back button in Inbox") {
            beforeEach {
              val inbox = root.childStack.value.active.instance.shouldBeInstanceOf<InboxComponent>()
              inbox.onBackClicked()
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("navigates back to Home screen with zero pending candidates") {
              val home = root.assertMainComponent().assertHomeComponent()
              home.state.value.pendingCandidateCount shouldBe 0
              home.state.value.expenses shouldHaveSize 0
            }
          }
        }
      }
    }
  }
}
