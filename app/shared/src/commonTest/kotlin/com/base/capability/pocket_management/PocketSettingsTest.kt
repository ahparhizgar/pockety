package com.base.capability.pocket_management

import com.base.component.TestCContext
import com.base.db.FakeCategoryFactory
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
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class PocketSettingsTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("PocketSettingsComponent initialized in creation mode") {
      val koin by integrationKoin()
      val completedCallback by testValue { FakeCallback1<Long>() }
      val cancelledCallback by testValue { FakeCallback<Unit>() }
      val pocketRepository by testValue { koin.get<PocketRepository>() }

      val component: PocketSettingsComponent by testValue {
        koin
            .get<PocketSettingsComponentFactory>()
            .create(
                cContext = TestCContext(),
                pocketId = null,
                onCompleted = completedCallback,
                onCancelled = cancelledCallback,
            )
      }

      When("initialized") {
        Then("nameText is empty, nameError is null, and default icon and color are pre-selected") {
          val state = component.state.value
          state.nameText shouldBe ""
          state.nameError.shouldBeNull()
          state.selectedColor shouldBe 0xFF316342
          state.selectedIcon shouldBe "coffee"
        }
      }

      When("user changes name text to Coffee & Dining") {
        beforeEach { component.onNameChanged("Coffee & Dining") }

        Then("nameText is updated to Coffee & Dining and nameError is cleared") {
          val state = component.state.value
          state.nameText shouldBe "Coffee & Dining"
          state.nameError.shouldBeNull()
        }
      }

      When("user taps save action with empty name text") {
        beforeEach {
          component.onNameChanged("")
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("nameError is set to empty name error message and pocket is not saved") {
          val state = component.state.value
          state.nameError.shouldNotBeNull()
          completedCallback.shouldNotBeCalled()

          val pockets = pocketRepository.observePockets().first()
          pockets shouldHaveSize 0
        }
      }

      When("user selects an accent color") {
        beforeEach { component.onColorSelected(0xFFE57373) }

        Then("selectedColor is updated to the chosen color Long") {
          component.state.value.selectedColor shouldBe 0xFFE57373
        }
      }

      When("user selects an icon") {
        beforeEach { component.onIconSelected("restaurant") }

        Then("selectedIcon is updated to the chosen icon identifier") {
          component.state.value.selectedIcon shouldBe "restaurant"
        }
      }

      When("user selects an associated category") {
        val category = FakeCategoryFactory.create(id = 10, name = "Dining Out")

        beforeEach { component.onCategorySelected(category) }

        Then("selectedCategory is updated and displayed") {
          val selected = component.state.value.selectedCategory
          selected.shouldNotBeNull()
          selected.id shouldBe 10L
          selected.name shouldBe "Dining Out"
        }
      }

      When("user clears the category association") {
        val category = FakeCategoryFactory.create(id = 10, name = "Dining Out")

        beforeEach {
          component.onCategorySelected(category)
          component.onCategoryCleared()
        }

        Then("selectedCategory becomes null") {
          component.state.value.selectedCategory.shouldBeNull()
        }
      }

      When("user inputs 50,000 in top-up amount and confirms on a balance of 100,000") {
        beforeEach {
          component.onTopUpAmountChanged("100,000")
          component.onConfirmTopUp()
          component.onTopUpAmountChanged("50,000")
          component.onConfirmTopUp()
        }

        Then("balance updates to 150,000 and top-up input field clears") {
          val state = component.state.value
          state.balanceAmount shouldBe 150_000L
          state.topUpAmountText shouldBe ""
        }
      }

      When("user taps save action with valid name, icon, color, and budget") {
        beforeEach {
          component.onNameChanged("Groceries")
          component.onColorSelected(0xFF4A7C59)
          component.onIconSelected("shopping_bag")
          component.onTopUpAmountChanged("150,000")
          component.onConfirmTopUp()
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("pocket is saved in repository and onCompleted callback is invoked") {
          completedCallback.shouldBeCalled()

          val pockets = pocketRepository.observePockets().first()
          pockets shouldHaveSize 1
          val saved = pockets[0]
          saved.name shouldBe "Groceries"
          saved.color shouldBe 0xFF4A7C59
          saved.icon shouldBe "shopping_bag"
          saved.budgetAmount shouldBe 150_000L
        }
      }
    }
  }
}
