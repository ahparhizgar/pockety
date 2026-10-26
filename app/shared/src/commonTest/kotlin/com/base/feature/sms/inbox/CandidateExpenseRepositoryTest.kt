package com.base.feature.sms.inbox

import com.base.db.CandidateExpenseRepository
import com.base.db.CandidateExpenseStatus
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class CandidateExpenseRepositoryTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("CandidateExpenseRepository with in-memory database") {
      val koin by integrationKoin()
      val repository by testValue { koin.get<CandidateExpenseRepository>() }

      When("no candidate expenses exist") {
        Then("observePendingCandidates emits empty list") {
          val initialList = repository.observePendingCandidates().first()
          initialList shouldHaveSize 0
        }
      }

      When("a candidate expense is added") {
        var insertedId: Long by testValue { 0 }

        beforeEach {
          insertedId =
              repository.addCandidate(
                  sender = "Bank Mellat",
                  rawBody = "Withdrawal: 5,000,000 Rials",
                  amount = 5000000L,
              )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("candidate expense is persisted and observed as pending") {
          insertedId shouldBe 1
          val list = repository.observePendingCandidates().first()
          list shouldHaveSize 1
          list[0].amount shouldBe 5000000L
          list[0].sender shouldBe "Bank Mellat"
          list[0].status shouldBe CandidateExpenseStatus.PENDING

          val fetched = repository.getCandidateById(insertedId)
          fetched?.amount shouldBe 5000000L
        }

        Then("updating status to REJECTED removes it from pending list") {
          repository.updateStatus(insertedId, CandidateExpenseStatus.REJECTED)
          testCoroutineScheduler.advanceUntilIdle()

          val list = repository.observePendingCandidates().first()
          list shouldHaveSize 0

          val fetched = repository.getCandidateById(insertedId)
          fetched?.status shouldBe CandidateExpenseStatus.REJECTED
        }

        Then("updating status to ACCEPTED removes it from pending list") {
          repository.updateStatus(insertedId, CandidateExpenseStatus.ACCEPTED)
          testCoroutineScheduler.advanceUntilIdle()

          val list = repository.observePendingCandidates().first()
          list shouldHaveSize 0

          val fetched = repository.getCandidateById(insertedId)
          fetched?.status shouldBe CandidateExpenseStatus.ACCEPTED
        }
      }
    }
  }
}
