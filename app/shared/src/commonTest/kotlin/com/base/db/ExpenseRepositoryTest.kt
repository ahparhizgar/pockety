package com.base.db

import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
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
    }
  }
}
