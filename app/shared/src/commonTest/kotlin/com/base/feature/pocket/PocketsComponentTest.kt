package com.base.feature.pocket

import com.base.component.TestCContext
import com.base.db.ExpenseRepository
import com.base.db.PocketRepository
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class PocketsComponentTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("a PocketRepository with no pockets") {
      val koin by integrationKoin()
      val pocketRepository by testValue { koin.get<PocketRepository>() }
      val expenseRepository by testValue { koin.get<ExpenseRepository>() }

      val component: PocketsComponent by testValue {
        DefaultPocketsComponent(
            cContext = TestCContext(),
            pocketRepository = pocketRepository,
        )
      }

      When("PocketsComponent is created") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("state.value.pockets is empty and state.value.isLoading becomes false") {
          val state = component.state.value
          state.pockets shouldHaveSize 0
          state.isLoading shouldBe false
        }
      }

      When("PocketsComponent is initialized with pockets having no linked expenses") {
        beforeEach {
          pocketRepository.addPocket(
              name = "Groceries",
              categoryTag = "Groceries",
              budgetAmount = 1_000_000L,
              icon = "shopping_bag",
          )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then(
            "state.value.pockets contains the pockets with remaining balance equal to full budget amount and not over budget"
        ) {
          val state = component.state.value
          state.pockets shouldHaveSize 1
          val pocket = state.pockets[0]
          pocket.remainingBalance shouldBe 1_000_000L
          pocket.isOverBudget shouldBe false
          pocket.warningMessage shouldBe null
        }
      }

      When("PocketsComponent observes repository updates with expenses exceeding budget") {
        var pocketId: Long by testValue { 0 }

        beforeEach {
          pocketId =
              pocketRepository.addPocket(
                  name = "Cafe",
                  categoryTag = "Drinks",
                  budgetAmount = 500_000L,
                  icon = "coffee",
              )
          expenseRepository.addExpense(
              amount = 565_000L,
              description = "Coffee",
              pocketId = pocketId,
          )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then(
            "state.value.pockets contains the pocket marked with isOverBudget = true and over-budget warning message"
        ) {
          val state = component.state.value
          val pocket = state.pockets.first { it.id == pocketId }
          pocket.isOverBudget shouldBe true
          pocket.remainingBalance shouldBe -65_000L
          pocket.warningMessage shouldBe "Over budget by 65,000 T"
        }
      }
    }
  }
}
