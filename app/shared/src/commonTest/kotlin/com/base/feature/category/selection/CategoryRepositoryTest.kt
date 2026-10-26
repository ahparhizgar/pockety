package com.base.feature.category.selection

import com.base.db.CategoryRepository
import com.base.db.ExpenseRepository
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
    }
  }
}
