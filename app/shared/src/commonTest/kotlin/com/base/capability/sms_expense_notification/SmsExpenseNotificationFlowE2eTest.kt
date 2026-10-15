package com.base.capability.sms_expense_notification

import com.base.capability.root_navigation.RootComponentFactory
import com.base.component.TestCContext
import com.base.db.CandidateExpenseRepository
import com.base.navigation.NavigationAction
import com.base.navigation.NavigationActionBus
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.assertAddExpense
import com.base.util.assertHomeComponent
import com.base.util.assertMainComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class SmsExpenseNotificationFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("application is running at the home screen with a candidate expense") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val candidateRepo by testValue { koin.get<CandidateExpenseRepository>() }
      val navBus by testValue { koin.get<NavigationActionBus>() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      beforeEach {
        candidateRepo.addCandidate(
            sender = "Bank Mellat",
            rawBody = "Withdrawal: 75,000 Rials",
            amount = 75000L,
        )
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("a NavigationAction.OpenAddExpense is emitted on NavigationActionBus") {
        beforeEach {
          val candidate = candidateRepo.observePendingCandidates().first().first()
          navBus.emit(
              NavigationAction.OpenAddExpense(
                  candidateExpenseId = candidate.id,
                  initialAmount = candidate.amount.toString(),
              )
          )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the active child becomes AddExpenseComponent with prefilled amount") {
          val addExpense = root.assertAddExpense()
          addExpense.state.value.amountText shouldBe "75000"
        }

        And("user enters description and saves the expense") {
          beforeEach {
            val addExpense = root.assertAddExpense()
            addExpense.onDescriptionChanged("Coffee")
            addExpense.onSaveClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("candidate expense status is updated to ACCEPTED") {
            val candidates = candidateRepo.observePendingCandidates().first()
            candidates shouldHaveSize 0
          }

          Then("expense is saved and user returns to home screen") {
            val home = root.assertMainComponent().assertHomeComponent()
            home.state.value.expenses shouldHaveSize 1
            home.state.value.expenses[0].amount shouldBe 75000L
            home.state.value.expenses[0].description shouldBe "Coffee"
          }
        }
      }
    }
  }
}
