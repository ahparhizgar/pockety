package com.base.feature.pocket.selection

import com.base.component.TestCContext
import com.base.db.PocketItemModel
import com.base.db.PocketRepository
import com.base.testValue
import com.base.util.FakeCallback
import com.base.util.FakeCallback1
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class SelectPocketTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("SelectPocketComponent initialized with available budget pockets") {
      val koin by integrationKoin()
      val selectedCallback by testValue { FakeCallback1<PocketItemModel?>() }
      val createPocketCallback by testValue { FakeCallback<Unit>() }
      val backCallback by testValue { FakeCallback<Unit>() }
      val pocketRepository by testValue { koin.get<PocketRepository>() }

      var coffeePocketId = 0L
      var groceriesPocketId = 0L

      beforeEach {
        coffeePocketId =
            pocketRepository.addPocket(
                name = "Daily Cafe & Coffee",
                categoryTag = "Coffee & Dining",
                budgetAmount = 500_000,
                icon = "local_cafe",
            )
        groceriesPocketId =
            pocketRepository.addPocket(
                name = "Groceries & Household",
                categoryTag = "Groceries",
                budgetAmount = 1_000_000,
                icon = "shopping_bag",
            )
        testCoroutineScheduler.advanceUntilIdle()
      }

      val component: SelectPocketComponent by testValue {
        koin
            .get<SelectPocketComponentFactory>()
            .create(
                cContext = TestCContext(),
                initialSelectedPocketId = coffeePocketId,
                onPocketSelected = selectedCallback,
                onCreatePocketClicked = createPocketCallback,
                onBack = backCallback,
            )
      }

      When("searching with query Coffee") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          component.onSearchQueryChanged("Coffee")
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("filtered list displays only pockets matching Coffee by name or category tag") {
          val filtered = component.state.value.filteredPockets
          filtered shouldHaveSize 1
          filtered[0].name shouldBe "Daily Cafe & Coffee"
        }
      }

      When("selecting a pocket item") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          val selectedItem = component.state.value.pockets.first { p -> p.id == groceriesPocketId }
          component.onPocketSelected(selectedItem)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("onPocketSelected callback is invoked with the chosen pocket") {
          selectedCallback.shouldBeCalled()
          selectedCallback.lastValue?.id shouldBe groceriesPocketId
          component.state.value.selectedPocketId shouldBe groceriesPocketId
        }
      }

      When("selecting the No Pocket option") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          component.onNoPocketSelected()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("onPocketSelected callback is invoked with null") {
          selectedCallback.shouldBeCalled()
          selectedCallback.lastValue.shouldBeNull()
          component.state.value.selectedPocketId.shouldBeNull()
        }
      }

      When("tapping back") {
        beforeEach {
          component.onBackClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("onBack callback is invoked") { backCallback.shouldBeCalled() }
      }

      When("tapping create pocket") {
        beforeEach {
          component.onCreatePocketClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("onCreatePocketClicked callback is invoked") { createPocketCallback.shouldBeCalled() }
      }
    }
  }
}
