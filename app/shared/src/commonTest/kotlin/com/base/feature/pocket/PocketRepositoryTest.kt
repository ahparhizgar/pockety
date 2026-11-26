package com.base.feature.pocket

import com.base.db.CategoryRepository
import com.base.db.ExpenseRepository
import com.base.db.PocketRepository
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
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
                  categoryIds = listOf(catId),
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
        }
      }

      When("adding a pocket linked to two categories and then re-linking it") {
        val categoryRepository by testValue { koin.get<CategoryRepository>() }
        var pocketId: Long by testValue { 0 }
        var firstCategoryId: Long by testValue { 0 }
        var secondCategoryId: Long by testValue { 0 }

        beforeEach {
          firstCategoryId = categoryRepository.addCategory("Food", "restaurant", 0xFFE57373)
          secondCategoryId = categoryRepository.addCategory("Cafe", "coffee", 0xFFB87D4B)
          pocketId =
              pocketRepository.addPocket(
                  name = "Food & Cafe",
                  categoryTag = "Food",
                  budgetAmount = 5000,
                  icon = "restaurant",
                  categoryIds = listOf(firstCategoryId, secondCategoryId),
              )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("both links are stored and the first category is the primary") {
          pocketRepository.getPocketCategoryIds(pocketId) shouldBe
              listOf(firstCategoryId, secondCategoryId)
          val pocket = pocketRepository.observePockets().first().first { it.id == pocketId }
          pocket.categoryIds shouldBe listOf(firstCategoryId, secondCategoryId)
          pocket.categoryId shouldBe firstCategoryId
        }

        And("the pocket is updated with only the second category linked") {
          beforeEach {
            pocketRepository.updatePocket(
                id = pocketId,
                name = "Food & Cafe",
                categoryTag = "Cafe",
                icon = "restaurant",
                categoryIds = listOf(secondCategoryId),
            )
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("the links are replaced and the primary follows") {
            pocketRepository.getPocketCategoryIds(pocketId) shouldBe listOf(secondCategoryId)
            val pocket = pocketRepository.observePockets().first().first { it.id == pocketId }
            pocket.categoryIds shouldBe listOf(secondCategoryId)
            pocket.categoryId shouldBe secondCategoryId
          }
        }
      }

      When("topping up an existing pocket with expenses") {
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
              amount = 300,
              description = "Espresso",
              pocketId = pocketId,
          )
          pocketRepository.topUpPocket(pocketId, 500, "Weekly allowance")
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("new top-up row is recorded and observePockets reflects initial + topUp - expenses") {
          val list = pocketRepository.observePockets().first()
          val pocket = list.first { it.id == pocketId }
          pocket.budgetAmount shouldBe 1500L
          pocket.remainingBalance shouldBe 1200L

          val topUps = pocketRepository.observePocketTopUps(pocketId).first()
          topUps shouldHaveSize 2
          topUps[0].amount shouldBe 500L
          topUps[0].description shouldBe "Weekly allowance"
          topUps[1].amount shouldBe 1000L
          topUps[1].description.shouldBeNull()

          val expenses = pocketRepository.observePocketExpenses(pocketId).first()
          expenses shouldHaveSize 1
          expenses[0].amount shouldBe 300L
        }
      }

      When("deleting an existing pocket") {
        var pocketId: Long by testValue { 0 }
        var expenseId: Long by testValue { 0 }

        beforeEach {
          pocketId =
              pocketRepository.addPocket(
                  name = "To Delete",
                  categoryTag = "Test",
                  budgetAmount = 2000,
                  icon = "delete",
              )
          expenseId =
              expenseRepository.addExpense(
                  amount = 500,
                  description = "Expense to unlink",
                  pocketId = pocketId,
              )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the pocket is removed, top-ups are cleared, and expenses are unlinked") {
          pocketRepository.deletePocket(pocketId)
          testCoroutineScheduler.advanceUntilIdle()

          val pockets = pocketRepository.observePockets().first()
          pockets.none { it.id == pocketId } shouldBe true

          pocketRepository.getPocketById(pocketId) shouldBe null

          val topUps = pocketRepository.observePocketTopUps(pocketId).first()
          topUps shouldHaveSize 0

          val expense = expenseRepository.observeExpenses().first().first { it.id == expenseId }
          expense.pocketId shouldBe null
        }
      }

      When("unlinking an expense from a pocket") {
        var pocketId: Long by testValue { 0 }
        var expenseId: Long by testValue { 0 }

        beforeEach {
          pocketId =
              pocketRepository.addPocket(
                  name = "Cafe",
                  categoryTag = "Drinks",
                  budgetAmount = 1000,
                  icon = "coffee",
              )
          expenseId =
              expenseRepository.addExpense(
                  amount = 300,
                  description = "Latte",
                  pocketId = pocketId,
              )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the expense is unlinked from the pocket but remains in repository") {
          pocketRepository.unlinkExpense(expenseId)
          testCoroutineScheduler.advanceUntilIdle()

          val pocketExpenses = pocketRepository.observePocketExpenses(pocketId).first()
          pocketExpenses shouldHaveSize 0

          val pocket = pocketRepository.observePockets().first().first { p -> p.id == pocketId }
          pocket.remainingBalance shouldBe 1000L

          val generalExpense =
              expenseRepository.observeExpenses().first().first { exp -> exp.id == expenseId }
          generalExpense.pocketId shouldBe null
          generalExpense.description shouldBe "Latte"
        }
      }

      When("processing monthly top ups") {
        var autoPocketId: Long by testValue { 0 }
        var disabledPocketId: Long by testValue { 0 }
        var zeroAmountPocketId: Long by testValue { 0 }

        beforeEach {
          autoPocketId =
              pocketRepository.addPocket(
                  name = "Subscriptions",
                  categoryTag = "Services",
                  budgetAmount = 0,
                  icon = "card",
                  monthlyTopUpAmount = 50000,
                  autoTopUpEnabled = true,
              )
          disabledPocketId =
              pocketRepository.addPocket(
                  name = "Savings",
                  categoryTag = "Money",
                  budgetAmount = 0,
                  icon = "savings",
                  monthlyTopUpAmount = 100000,
                  autoTopUpEnabled = false,
              )
          zeroAmountPocketId =
              pocketRepository.addPocket(
                  name = "Gym",
                  categoryTag = "Health",
                  budgetAmount = 0,
                  icon = "fitness",
                  monthlyTopUpAmount = 0,
                  autoTopUpEnabled = true,
              )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("only eligible pocket is topped up and lastTopUpMonth is updated") {
          val generated = pocketRepository.processMonthlyTopUps("2026-11")
          testCoroutineScheduler.advanceUntilIdle()

          generated shouldHaveSize 1
          generated[0].pocketId shouldBe autoPocketId
          generated[0].amount shouldBe 50000L
          generated[0].description.shouldBeNull()

          val autoPocket = pocketRepository.getPocketById(autoPocketId)
          autoPocket.shouldNotBeNull()
          autoPocket.lastTopUpMonth shouldBe "2026-11"

          val disabledPocket = pocketRepository.getPocketById(disabledPocketId)
          disabledPocket.shouldNotBeNull()
          disabledPocket.lastTopUpMonth shouldBe null

          // Second execution in the same month is idempotent
          val secondRun = pocketRepository.processMonthlyTopUps("2026-11")
          testCoroutineScheduler.advanceUntilIdle()
          secondRun shouldHaveSize 0

          val topUps = pocketRepository.observePocketTopUps(autoPocketId).first()
          topUps shouldHaveSize 1
        }
      }
    }
  }
}
