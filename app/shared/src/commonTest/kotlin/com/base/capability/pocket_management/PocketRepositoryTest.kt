package com.base.capability.pocket_management

import com.base.db.CategoryRepository
import com.base.db.ExpenseRepository
import com.base.db.PocketRepository
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
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

      When("adding a pocket with name, icon, color, category, and budget") {
        var pocketId: Long by testValue { 0 }
        val categoryRepository by testValue { koin.get<CategoryRepository>() }

        beforeEach {
          val catId = categoryRepository.addCategory("Food", "restaurant", 0xFFE57373)
          pocketId =
              pocketRepository.addPocket(
                  name = "Groceries",
                  categoryTag = "Food",
                  budgetAmount = 5000,
                  icon = "shopping_bag",
                  color = 0xFF4A7C59,
                  categoryId = catId,
              )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the pocket is persisted and observable via observePockets with matching properties") {
          val list = pocketRepository.observePockets().first()
          val pocket = list.first { it.id == pocketId }
          pocket.name shouldBe "Groceries"
          pocket.icon shouldBe "shopping_bag"
          pocket.color shouldBe 0xFF4A7C59
          pocket.categoryId.shouldNotBeNull()
          pocket.budgetAmount shouldBe 5000L
        }

        Then(
            "fetching pocket by id returns entity containing saved color, categoryId, and metadata"
        ) {
          val entity = pocketRepository.getPocketById(pocketId)
          entity.shouldNotBeNull()
          entity.name shouldBe "Groceries"
          entity.icon shouldBe "shopping_bag"
          entity.color shouldBe 0xFF4A7C59
          entity.categoryId.shouldNotBeNull()
          entity.budgetAmount shouldBe 5000L
        }
      }
    }
  }
}
