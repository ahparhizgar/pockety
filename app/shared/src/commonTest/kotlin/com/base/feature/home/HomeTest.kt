package com.base.feature.home

import com.base.component.TestCContext
import com.base.db.CandidateExpenseRepository
import com.base.db.ExpenseRepository
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe

class HomeTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("application with recorded expenses") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      var addExpenseClicked = false
      var openInboxClicked = false

      val component by testValue {
        koin
            .get<HomeComponentFactory>()
            .create(
                cContext = context,
                onAddExpense = { addExpenseClicked = true },
                onOpenInbox = { openInboxClicked = true },
            )
      }

      beforeEach {
        val expenseRepo = koin.get<ExpenseRepository>()
        expenseRepo.addExpense(amount = 50_000, description = "Lunch")
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("HomeComponent is initialized") {
        Then("HomeState aggregates expenses into the monthly outflow without a list") {
          val state = component.state.value
          state.isSmsPermissionGranted shouldBe false
          state.pendingCandidateCount shouldBe 0
          state.pendingCandidateSum shouldBe 0L
          state.monthlyOutflow shouldBe 50_000L
        }

        Then("SMS permission banner actions and permission updates function normally") {
          component.state.value.isSmsPermissionGranted shouldBe false
          context.fakePermissionController.setSmsPermissionGranted(true)
          testCoroutineScheduler.advanceUntilIdle()
          component.state.value.isSmsPermissionGranted shouldBe true
        }

        Then("undecided candidates are observed and updated") {
          val candidateRepo = koin.get<CandidateExpenseRepository>()
          candidateRepo.addCandidate("Bank", "Paid 20000", 20000L)
          testCoroutineScheduler.advanceUntilIdle()

          val state = component.state.value
          state.pendingCandidateCount shouldBe 1
          state.pendingCandidateSum shouldBe 20000L
        }

        Then("onAddExpenseClicked invokes callback") {
          component.onAddExpenseClicked()
          addExpenseClicked shouldBe true
        }

        Then("onUndecidedExpensesClicked invokes callback") {
          component.onUndecidedExpensesClicked()
          openInboxClicked shouldBe true
        }
      }
    }
  }
}
