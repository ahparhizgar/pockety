package com.base.feature.category.selection

import com.base.db.CategoryRepository
import com.base.db.ExpenseRepository
import com.base.db.PocketRepository
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class CategoryRepositoryTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("CategoryRepository with in-memory database") {
      val koin by integrationKoin()
      val categoryRepository by testValue { koin.get<CategoryRepository>() }
      val expenseRepository by testValue { koin.get<ExpenseRepository>() }
      val pocketRepository by testValue { koin.get<PocketRepository>() }

      When("category repository is empty") {
        Then("observeCategories emits empty list") {
          val categories = categoryRepository.observeCategories().first()
          categories shouldHaveSize 0
        }
      }

      When("addCategory is called with valid parameters") {
        var categoryId: Long by testValue { 0 }

        beforeEach {
          categoryId =
              categoryRepository.addCategory(
                  name = "Dining",
                  icon = "restaurant",
                  color = 0xFFE57373,
              )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("returned category ID is positive") { categoryId shouldBeGreaterThan 0L }

        Then("observeCategories emits the newly created category") {
          val list = categoryRepository.observeCategories().first()
          list shouldHaveSize 1
          list[0].id shouldBe categoryId
          list[0].name shouldBe "Dining"
          list[0].icon shouldBe "restaurant"
          list[0].color shouldBe 0xFFE57373
        }
      }

      When("category assigned to an expense is deleted") {
        var categoryId: Long by testValue { 0 }
        var expenseId: Long by testValue { 0 }

        beforeEach {
          categoryId = categoryRepository.addCategory("Groceries", "shopping_cart", 0xFF81C784)
          expenseId =
              expenseRepository.addExpense(
                  amount = 12000,
                  description = "Supermarket",
                  categoryId = categoryId,
              )
          testCoroutineScheduler.advanceUntilIdle()

          categoryRepository.deleteCategory(categoryId)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("expense's categoryId is set to null") {
          val expenses = expenseRepository.observeExpenses().first()
          val expense = expenses.first { it.id == expenseId }
          expense.categoryId.shouldBeNull()
        }
      }

      When("category assigned to a pocket is deleted") {
        var categoryId: Long by testValue { 0 }
        var pocketId: Long by testValue { 0 }

        beforeEach {
          categoryId = categoryRepository.addCategory("Bills", "receipt", 0xFF64B5F6)
          pocketId =
              pocketRepository.addPocket(
                  name = "Bills Pocket",
                  categoryTag = "Bills",
                  budgetAmount = 1_000_000,
                  icon = "receipt",
                  categoryId = categoryId,
              )
          testCoroutineScheduler.advanceUntilIdle()

          categoryRepository.deleteCategory(categoryId)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the category is gone") {
          categoryRepository.observeCategories().first() shouldHaveSize 0
        }

        Then("pocket's categoryId is set to null") {
          val pockets = pocketRepository.observePockets().first()
          val pocket = pockets.first { it.id == pocketId }
          pocket.categoryId.shouldBeNull()
        }
      }

      When("three categories are added") {
        beforeEach {
          categoryRepository.addCategory("Dining", "restaurant", 0xFFE57373)
          categoryRepository.addCategory("Groceries", "shopping_cart", 0xFF81C784)
          categoryRepository.addCategory("Bills", "receipt", 0xFF64B5F6)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("observeCategories emits them in the order they were added") {
          val names = categoryRepository.observeCategories().first().map { it.name }
          names shouldBe listOf("Dining", "Groceries", "Bills")
        }
      }

      When("categories are reordered") {
        beforeEach {
          val diningId = categoryRepository.addCategory("Dining", "restaurant", 0xFFE57373)
          val groceriesId =
              categoryRepository.addCategory("Groceries", "shopping_cart", 0xFF81C784)
          val billsId = categoryRepository.addCategory("Bills", "receipt", 0xFF64B5F6)
          categoryRepository.reorderCategories(listOf(billsId, diningId, groceriesId))
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("observeCategories emits the manual order") {
          val names = categoryRepository.observeCategories().first().map { it.name }
          names shouldBe listOf("Bills", "Dining", "Groceries")
        }

        Then("sortOrder values match the positions") {
          val sortOrders = categoryRepository.observeCategories().first().map { it.sortOrder }
          sortOrders shouldBe listOf(0, 1, 2)
        }
      }

      When("a category is added after reordering") {
        beforeEach {
          val diningId = categoryRepository.addCategory("Dining", "restaurant", 0xFFE57373)
          val groceriesId =
              categoryRepository.addCategory("Groceries", "shopping_cart", 0xFF81C784)
          categoryRepository.reorderCategories(listOf(groceriesId, diningId))
          categoryRepository.addCategory("Bills", "receipt", 0xFF64B5F6)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the new category is appended at the end") {
          val names = categoryRepository.observeCategories().first().map { it.name }
          names shouldBe listOf("Groceries", "Dining", "Bills")
        }
      }

      When("a category is deleted after reordering") {
        beforeEach {
          val diningId = categoryRepository.addCategory("Dining", "restaurant", 0xFFE57373)
          val groceriesId =
              categoryRepository.addCategory("Groceries", "shopping_cart", 0xFF81C784)
          val billsId = categoryRepository.addCategory("Bills", "receipt", 0xFF64B5F6)
          categoryRepository.reorderCategories(listOf(billsId, diningId, groceriesId))
          categoryRepository.deleteCategory(diningId)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the rest keep their relative order") {
          val names = categoryRepository.observeCategories().first().map { it.name }
          names shouldBe listOf("Bills", "Groceries")
        }
      }
    }
  }
}
