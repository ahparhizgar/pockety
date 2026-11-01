package com.base.feature.sms.inbox

import com.base.component.TestCContext
import com.base.db.CandidateExpenseRepository
import com.base.db.ExpenseRepository
import com.base.feature.root.RootComponentFactory
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.assertAddExpenseComponent
import com.base.util.assertAddExpenseFlow
import com.base.util.assertHomeComponent
import com.base.util.assertInbox
import com.base.util.assertMainComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

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
          val inbox = root.assertInbox()
          inbox.state.value.candidates shouldHaveSize 1
          inbox.state.value.candidates[0].amount shouldBe 45000L
        }

        And("user selects a candidate expense from Inbox") {
          beforeEach {
            val inbox = root.assertInbox()
            val candidate = inbox.state.value.candidates.first()
            inbox.onSelectCandidate(candidate)
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("navigates to AddExpense screen with amount prefilled") {
            val addExpense = root.assertAddExpenseFlow().assertAddExpenseComponent()
            addExpense.state.value.amountText shouldBe "45000"
          }

          And("user enters description and saves the expense") {
            beforeEach {
              val addExpense = root.assertAddExpenseFlow().assertAddExpenseComponent()
              addExpense.onDescriptionChanged("Groceries")
              addExpense.onSaveClicked()
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("navigates back to Home screen and persists the saved expense") {
              val home = root.assertMainComponent().assertHomeComponent()
              home.state.value.pendingCandidateCount shouldBe 0
              val expenseRepo = koin.get<ExpenseRepository>()
              val expenses = expenseRepo.observeExpenses().first()
              expenses shouldHaveSize 1
              expenses[0].amount shouldBe 45000L
              expenses[0].description shouldBe "Groceries"
            }
          }
        }

        And("user dismisses candidate expense from Inbox") {
          beforeEach {
            val inbox = root.assertInbox()
            val candidateId = inbox.state.value.candidates.first().id
            inbox.onDismiss(candidateId)
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("candidate is removed from Inbox state") {
            val inbox = root.assertInbox()
            inbox.state.value.candidates shouldHaveSize 0
          }

          And("user clicks back button in Inbox") {
            beforeEach {
              val inbox = root.assertInbox()
              inbox.onBackClicked()
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("navigates back to Home screen with zero pending candidates") {
              val home = root.assertMainComponent().assertHomeComponent()
              home.state.value.pendingCandidateCount shouldBe 0
            }
          }
        }
      }
    }
  }
}
