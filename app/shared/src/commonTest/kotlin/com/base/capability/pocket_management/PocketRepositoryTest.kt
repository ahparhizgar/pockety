package com.base.capability.pocket_management

import com.base.db.ExpenseRepository
import com.base.db.PocketRepository
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class PocketRepositoryTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("PocketRepository with in-memory database") {
      val koin by integrationKoin()
      val pocketRepository by testValue { koin.get<PocketRepository>() }
      val expenseRepository by testValue { koin.get<ExpenseRepository>() }

      When("pocket repository is empty") {
        Then("observePockets emits empty list") {
          val pockets = pocketRepository.observePockets().first()
          pockets shouldHaveSize 0
        }
      }

      When("a pocket with budget 1000 and linked transactions totaling 400") {
        var pocketId: Long by testValue { 0 }

        beforeEach {
          pocketId =
              pocketRepository.addPocket(
                  name = "Groceries",
                  categoryTag = "Food",
                  budgetAmount = 1000,
                  icon = "shopping_bag",
              )
          expenseRepository.addExpense(
              amount = 400,
              description = "Supermarket",
              pocketId = pocketId,
          )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the emitted item has remaining balance of 600 and isOverBudget = false") {
          val list = pocketRepository.observePockets().first()
          list shouldHaveSize 1
          val item = list[0]
          item.id shouldBe pocketId
          item.budgetAmount shouldBe 1000L
          item.remainingBalance shouldBe 600L
          item.isOverBudget shouldBe false
          item.warningMessage shouldBe null
        }
      }

      When("a pocket with budget 1000 and linked transactions totaling 1200") {
        var pocketId: Long by testValue { 0 }

        beforeEach {
          pocketId =
              pocketRepository.addPocket(
                  name = "Cafe",
                  categoryTag = "Drinks",
                  budgetAmount = 1000,
                  icon = "coffee",
              )
          expenseRepository.addExpense(
              amount = 1200,
              description = "Coffee shop",
              pocketId = pocketId,
          )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the emitted item has remaining balance of -200 and isOverBudget = true") {
          val list = pocketRepository.observePockets().first()
          val item = list.first { it.id == pocketId }
          item.remainingBalance shouldBe -200L
          item.isOverBudget shouldBe true
          item.warningMessage shouldBe "Over budget by 200 T"
        }
      }
    }
  }
}
