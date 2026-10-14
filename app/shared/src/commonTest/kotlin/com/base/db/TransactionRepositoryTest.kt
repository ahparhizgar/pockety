package com.base.db

import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class TransactionRepositoryTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("TransactionRepository with in-memory database") {
      val koin by integrationKoin()
      val repository by testValue { koin.get<TransactionRepository>() }

      When("no transactions exist") {
        Then("observeTransactions emits empty list") {
          val initialList = repository.observeTransactions().first()
          initialList shouldHaveSize 0
        }
      }

      When("a transaction is added") {
        var insertedId: Long = 0

        beforeEach {
          insertedId = repository.addTransaction(amount = 2500, description = "Lunch")
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("transaction is persisted and observed") {
          insertedId shouldBe 1
          val list = repository.observeTransactions().first()
          list shouldHaveSize 1
          list[0].amount shouldBe 2500
          list[0].description shouldBe "Lunch"
        }
      }
    }
  }
}
