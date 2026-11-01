package com.base.feature.expense

import com.base.db.ExpenseRepository
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

class ExpenseRepositoryTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("ExpenseRepository with in-memory database") {
      val koin by integrationKoin()
      val repository by testValue { koin.get<ExpenseRepository>() }

      When("no expenses exist") {
        Then("observeExpenses emits empty list") {
          val initialList = repository.observeExpenses().first()
          initialList shouldHaveSize 0
        }
      }

      When("a expense is added") {
        var insertedId: Long by testValue { 0 }

        beforeEach {
          insertedId = repository.addExpense(amount = 2500, description = "Lunch")
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("expense is persisted and observed") {
          insertedId shouldBe 1
          val list = repository.observeExpenses().first()
          list shouldHaveSize 1
          list[0].amount shouldBe 2500
          list[0].description shouldBe "Lunch"
        }
      }

      When("getExpenseById is called with that ID") {
        var insertedId: Long by testValue { 0 }

        beforeEach {
          insertedId = repository.addExpense(amount = 3500, description = "Dinner")
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the matching ExpenseEntity is returned") {
          val expense = repository.getExpenseById(insertedId)
          expense.shouldNotBeNull()
          expense.id shouldBe insertedId
          expense.amount shouldBe 3500
          expense.description shouldBe "Dinner"
        }
      }

      When("updateExpense is called with modified description and amount") {
        var insertedId: Long by testValue { 0 }

        beforeEach {
          insertedId = repository.addExpense(amount = 5000, description = "Groceries")
          testCoroutineScheduler.advanceUntilIdle()
          val expense = repository.getExpenseById(insertedId)!!
          repository.updateExpense(expense.copy(amount = 6000, description = "Supermarket"))
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("getExpenseById and observeExpenses return the updated values") {
          val updated = repository.getExpenseById(insertedId)
          updated.shouldNotBeNull()
          updated.amount shouldBe 6000
          updated.description shouldBe "Supermarket"

          val list = repository.observeExpenses().first()
          val item = list.first { it.id == insertedId }
          item.amount shouldBe 6000
          item.description shouldBe "Supermarket"
        }
      }

      When("deleteExpense is called with that ID") {
        var insertedId: Long by testValue { 0 }

        beforeEach {
          insertedId = repository.addExpense(amount = 1200, description = "Snack")
          testCoroutineScheduler.advanceUntilIdle()
          repository.deleteExpense(insertedId)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("getExpenseById returns null and observeExpenses no longer contains the expense") {
          val expense = repository.getExpenseById(insertedId)
          expense.shouldBeNull()

          val list = repository.observeExpenses().first()
          list.none { it.id == insertedId } shouldBe true
        }
      }
    }
  }
}
