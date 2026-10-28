package com.base.feature.pocket.details

import com.base.component.TestCContext
import com.base.db.ExpenseRepository
import com.base.db.PocketRepository
import com.base.feature.root.RootComponentFactory
import com.base.testValue
import com.base.ui.components.PocketyTab
import com.base.util.MainDispatcherExtension
import com.base.util.assertMainComponent
import com.base.util.assertPocketDetails
import com.base.util.assertPocketSettings
import com.base.util.assertPocketSettingsFlow
import com.base.util.assertPockets
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class PocketDetailsFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("application with existing pockets on the Pockets tab") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }
      val pocketRepository by testValue { koin.get<PocketRepository>() }
      val expenseRepository by testValue { koin.get<ExpenseRepository>() }
      var pocketId: Long by testValue { 0 }

      beforeEach {
        pocketId =
            pocketRepository.addPocket(
                name = "Daily Cafe & Coffee",
                categoryTag = "Drinks",
                budgetAmount = 850000,
                icon = "coffee",
                color = 0xFF4A7C59,
            )
        expenseRepository.addExpense(
            amount = 50000,
            description = "Sam Cafe",
            pocketId = pocketId,
        )
        testCoroutineScheduler.advanceUntilIdle()

        val main = root.assertMainComponent()
        main.onTabSelect(PocketyTab.Pockets)
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("user clicks a pocket card in Pockets list") {
        beforeEach {
          val pockets = root.assertPockets()
          pockets.onPocketClick(pocketId)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("active child in RootComponent becomes PocketDetailsComponent") {
          val details = root.assertPocketDetails()
          details.state.value.pocketId shouldBe pocketId
          details.state.value.name shouldBe "Daily Cafe & Coffee"
          details.state.value.balanceAmount shouldBe 800000L
          details.state.value.transactions shouldHaveSize 2
          details.state.value.transactions[0].isTopUp shouldBe false
          details.state.value.transactions[0].amount shouldBe 50000L
          details.state.value.transactions[1].isTopUp shouldBe true
          details.state.value.transactions[1].amount shouldBe 850000L
        }

        And("user unlinks an expense transaction") {
          beforeEach {
            val details = root.assertPocketDetails()
            val expenseTx = details.state.value.transactions.first { tx -> !tx.isTopUp }
            details.onUnlinkTransaction(expenseTx.id)
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then(
              "pocket details updates transactions and balance, and expense remains in repository unlinked"
          ) {
            val details = root.assertPocketDetails()
            details.state.value.balanceAmount shouldBe 850000L
            details.state.value.transactions.none { tx -> !tx.isTopUp } shouldBe true
            val expenses = expenseRepository.observeExpenses().first()
            expenses shouldHaveSize 1
            expenses[0].pocketId shouldBe null
          }
        }

        And("user opens quick top-up and confirms an addition of 100,000 T") {
          beforeEach {
            val details = root.assertPocketDetails()
            details.onTopUpClicked()
            details.onQuickTopUpPresetSelected(100000)
            details.onConfirmQuickTopUp()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("pocket balance reflects the new total and new top-up item appears in the feed") {
            val details = root.assertPocketDetails()
            details.state.value.balanceAmount shouldBe 900000L
            details.state.value.transactions shouldHaveSize 3
            details.state.value.transactions[0].isTopUp shouldBe true
            details.state.value.transactions[0].amount shouldBe 100000L
          }

          And("user taps Configure") {
            beforeEach {
              val details = root.assertPocketDetails()
              details.onConfigureClicked()
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("RootComponent pushes PocketSettingsFlowComponent with pocketId in edit mode") {
              val flow = root.assertPocketSettingsFlow()
              val settings = flow.assertPocketSettings()
              settings.state.value.pocketId shouldBe pocketId
            }

            And("user presses back from pocket settings") {
              beforeEach {
                root.onBack()
                testCoroutineScheduler.advanceUntilIdle()
              }

              Then("RootComponent returns to PocketDetailsComponent") {
                val details = root.assertPocketDetails()
                details.state.value.pocketId shouldBe pocketId
              }

              And("user presses back from pocket details") {
                beforeEach {
                  val details = root.assertPocketDetails()
                  details.onBackClicked()
                  testCoroutineScheduler.advanceUntilIdle()
                }

                Then("RootComponent returns to MainComponent on Pockets tab with updated balance") {
                  root.assertMainComponent()
                  val pockets = root.assertPockets()
                  val pocket = pockets.state.value.pockets.first { it.id == pocketId }
                  pocket.remainingBalance shouldBe 900000L
                }
              }
            }
          }
        }
      }
    }
  }
}
