package com.base.feature.pocket.details

import com.base.component.TestCContext
import com.base.db.ExpenseRepository
import com.base.db.PocketRepository
import com.base.feature.settings.UserSettingsRepository
import com.base.testValue
import com.base.util.FakeCallback
import com.base.util.FakeCallback1
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class PocketDetailsTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("PocketDetailsComponent initialized with an existing pocket") {
      val koin by integrationKoin()
      val backCallback by testValue { FakeCallback<Unit>() }
      val configureCallback by testValue { FakeCallback1<Long>() }
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
        testCoroutineScheduler.advanceUntilIdle()
      }

      val userSettingsRepository by testValue { koin.get<UserSettingsRepository>() }

      val component: PocketDetailsComponent by testValue {
        DefaultPocketDetailsComponent(
            cContext = TestCContext(),
            pocketRepository = pocketRepository,
            userSettingsRepository = userSettingsRepository,
            pocketId = pocketId,
            onBack = backCallback,
            onConfigure = configureCallback,
        )
      }

      When("component is created") {
        Then("state loads pocket name, balance, icon, and accent color from repository") {
          testCoroutineScheduler.advanceUntilIdle()
          val state = component.state.value
          state.pocketId shouldBe pocketId
          state.name shouldBe "Daily Cafe & Coffee"
          state.balanceAmount shouldBe 850000L
          state.icon shouldBe "coffee"
          state.color shouldBe 0xFF4A7C59
          state.isLoading shouldBe false
          state.isQuickTopUpVisible shouldBe false
          state.transactions shouldHaveSize 1
          state.transactions[0].isTopUp shouldBe true
          state.transactions[0].amount shouldBe 850000L
        }
      }

      When("pocket has linked expenses and top-ups in the repository") {
        beforeEach {
          expenseRepository.addExpense(
              amount = 50000,
              description = "Sam Cafe",
              pocketId = pocketId,
          )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then(
            "the component state emits dynamically computed balance: sum(top-ups) - sum(expenses)"
        ) {
          val state = component.state.value
          state.balanceAmount shouldBe 800000L
          state.transactions shouldHaveSize 2
          state.transactions[0].isTopUp shouldBe false
          state.transactions[0].amount shouldBe 50000L
          state.transactions[0].title shouldBe "Sam Cafe"
          state.transactions[1].isTopUp shouldBe true
          state.transactions[1].amount shouldBe 850000L
        }

        And("user unlinks a linked expense transaction") {
          beforeEach {
            val expenseTx = component.state.value.transactions.first { tx -> !tx.isTopUp }
            component.onUnlinkTransaction(expenseTx.id)
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then(
              "the expense is removed from pocket, balance reflects the restored amount, and expense remains unlinked"
          ) {
            val state = component.state.value
            state.transactions.none { tx -> !tx.isTopUp } shouldBe true
            state.balanceAmount shouldBe 850000L
            state.confirmationMessage shouldBe "Unlinked Sam Cafe. +50,000 T restored!"

            val expenses = expenseRepository.observeExpenses().first()
            expenses shouldHaveSize 1
            expenses[0].pocketId shouldBe null
            expenses[0].description shouldBe "Sam Cafe"
          }
        }
      }

      When("user taps the Top-up action") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          component.onTopUpClicked()
        }

        Then("isQuickTopUpVisible is updated to true") {
          component.state.value.isQuickTopUpVisible shouldBe true
        }
      }

      When("user dismisses the quick top-up sheet") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          component.onTopUpClicked()
          component.onDismissQuickTopUp()
        }

        Then("isQuickTopUpVisible is updated to false and balance remains unchanged") {
          component.state.value.isQuickTopUpVisible shouldBe false
          component.state.value.balanceAmount shouldBe 850000L
        }
      }

      When("user selects a preset quick top-up amount chip (+100,000 T) and confirms") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          component.onTopUpClicked()
          component.onQuickTopUpPresetSelected(100000)
          component.onConfirmQuickTopUp()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("pocket balance in repository increases by 100,000 T and sheet closes") {
          val state = component.state.value
          state.isQuickTopUpVisible shouldBe false
          state.balanceAmount shouldBe 950000L
          state.confirmationMessage.shouldNotBeNull()

          val updatedPocket = pocketRepository.observePockets().first().first { it.id == pocketId }
          updatedPocket.remainingBalance shouldBe 950000L
        }
      }

      When("user enters a custom top-up amount (250000) and confirms") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          component.onTopUpClicked()
          component.onQuickTopUpCustomAmountChanged("250000")
          component.onConfirmQuickTopUp()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then(
            "pocket balance increases by 250,000 T, new top-up transaction appears, and confirmation message is displayed"
        ) {
          val state = component.state.value
          state.isQuickTopUpVisible shouldBe false
          state.balanceAmount shouldBe 1100000L
          state.transactions.first().isTopUp shouldBe true
          state.transactions.first().amount shouldBe 250000L
          state.confirmationMessage shouldBe "Pocket topped up by +250,000 T!"
        }
      }

      When("user enters an invalid or empty custom top-up amount") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          component.onTopUpClicked()
          component.onQuickTopUpCustomAmountChanged("")
          component.onConfirmQuickTopUp()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("balance remains unchanged and sheet stays open") {
          component.state.value.balanceAmount shouldBe 850000L
          component.state.value.isQuickTopUpVisible shouldBe true
        }
      }

      When("user taps the Configure action") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          component.onConfigureClicked()
        }

        Then("onConfigure callback is invoked with the current pocket ID") {
          configureCallback.lastValue shouldBe pocketId
        }
      }

      When("user taps the Back action") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          component.onBackClicked()
        }

        Then("onBack callback is invoked") { backCallback.called shouldBe true }
      }
    }
  }
}
