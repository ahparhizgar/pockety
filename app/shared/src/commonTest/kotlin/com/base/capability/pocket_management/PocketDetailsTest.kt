package com.base.capability.pocket_management

import com.base.component.TestCContext
import com.base.db.PocketRepository
import com.base.testValue
import com.base.util.FakeCallback
import com.base.util.FakeCallback1
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class PocketDetailsTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("PocketDetailsComponent initialized with an existing pocket") {
      val koin by integrationKoin()
      val backCallback by testValue { FakeCallback<Unit>() }
      val configureCallback by testValue { FakeCallback1<Long>() }
      val pocketRepository by testValue { koin.get<PocketRepository>() }
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

      val component: PocketDetailsComponent by testValue {
        DefaultPocketDetailsComponent(
            cContext = TestCContext(),
            pocketRepository = pocketRepository,
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

          val updatedPocket = pocketRepository.getPocketById(pocketId)
          updatedPocket.shouldNotBeNull()
          updatedPocket.budgetAmount shouldBe 950000L
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

        Then("pocket balance increases by 250,000 T and confirmation message is displayed") {
          val state = component.state.value
          state.isQuickTopUpVisible shouldBe false
          state.balanceAmount shouldBe 1100000L
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

      When("user deletes a mock transaction in the UI layer") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          component.onDeleteTransaction("tx-1")
        }

        Then("transaction is removed and confirmation feedback is set") {
          val state = component.state.value
          state.transactions.none { it.id == "tx-1" } shouldBe true
          state.confirmationMessage.shouldNotBeNull()
        }
      }
    }
  }
}
