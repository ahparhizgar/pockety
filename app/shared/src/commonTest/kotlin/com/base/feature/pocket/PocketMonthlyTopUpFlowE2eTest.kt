package com.base.feature.pocket

import com.base.component.TestCContext
import com.base.db.PocketRepository
import com.base.feature.root.RootComponentFactory
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.time.Clock
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PocketMonthlyTopUpFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("a pocket with auto-refill enabled and lastTopUpMonth as previous month") {
      val koin by integrationKoin()
      val pocketRepository by testValue { koin.get<PocketRepository>() }
      var pocketId: Long by testValue { 0 }

      val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
      val monthNumber = now.month.ordinal + 1
      val currentMonth = "${now.year}-${monthNumber.toString().padStart(2, '0')}"
      val previousMonth =
          if (monthNumber == 1) {
            "${now.year - 1}-12"
          } else {
            "${now.year}-${(monthNumber - 1).toString().padStart(2, '0')}"
          }

      beforeEach {
        pocketId =
            pocketRepository.addPocket(
                name = "Monthly Allowance",
                categoryTag = "Allowance",
                budgetAmount = 0L,
                icon = "wallet",
                monthlyTopUpAmount = 500_000L,
                autoTopUpEnabled = true,
            )
        testCoroutineScheduler.advanceUntilIdle()
        // Simulate previous month top up
        pocketRepository.processMonthlyTopUps(previousMonth)
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("application launches (RootComponent initializes)") {
        beforeEach {
          val context = TestCContext()
          koin.get<RootComponentFactory>().create(context)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("a top-up transaction of 500,000 T is recorded and balance increases") {
          val topUps = pocketRepository.observePocketTopUps(pocketId).first()
          // Initial previous month + new current month = 2
          topUps shouldHaveSize 2
          topUps[0].amount shouldBe 500_000L
          topUps[0].description shouldBe "Monthly Top-Up"

          val pocket = pocketRepository.getPocketById(pocketId)
          pocket.shouldNotBeNull()
          pocket.lastTopUpMonth shouldBe currentMonth

          val pocketItems = pocketRepository.observePockets().first()
          val pocketItem = pocketItems.first { it.id == pocketId }
          pocketItem.budgetAmount shouldBe 1_000_000L
          pocketItem.remainingBalance shouldBe 1_000_000L
        }

        And("application launches a second time in the same month") {
          beforeEach {
            val context2 = TestCContext()
            koin.get<RootComponentFactory>().create(context2)
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("no duplicate top-up transaction is created and balance remains unchanged") {
            val topUps = pocketRepository.observePocketTopUps(pocketId).first()
            topUps shouldHaveSize 2

            val pocketItems = pocketRepository.observePockets().first()
            val pocketItem = pocketItems.first { it.id == pocketId }
            pocketItem.budgetAmount shouldBe 1_000_000L
          }
        }
      }
    }

    Given("a pocket with auto-refill disabled") {
      val koin by integrationKoin()
      val pocketRepository by testValue { koin.get<PocketRepository>() }
      var pocketId: Long by testValue { 0 }

      beforeEach {
        pocketId =
            pocketRepository.addPocket(
                name = "Manual Top Up Only",
                categoryTag = "Manual",
                budgetAmount = 0L,
                icon = "wallet",
                monthlyTopUpAmount = 200_000L,
                autoTopUpEnabled = false,
            )
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("application launches") {
        beforeEach {
          val context = TestCContext()
          koin.get<RootComponentFactory>().create(context)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("no top-up transaction is generated for this pocket") {
          val topUps = pocketRepository.observePocketTopUps(pocketId).first()
          topUps shouldHaveSize 0

          val pocketItems = pocketRepository.observePockets().first()
          val pocketItem = pocketItems.first { it.id == pocketId }
          pocketItem.budgetAmount shouldBe 0L
        }
      }
    }
  }
}
