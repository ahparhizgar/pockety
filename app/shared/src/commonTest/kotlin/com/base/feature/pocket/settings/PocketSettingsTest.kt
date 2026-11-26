package com.base.feature.pocket.settings

import com.base.component.TestCContext
import com.base.db.CategoryRepository
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

      When("user configures monthly allocation and auto-refill") {
        beforeEach {
          component.onMonthlyTopUpAmountChanged(300_000L)
          component.onAutoTopUpEnabledChanged(true)
        }

        Then("state updates with monthlyTopUpAmount and autoTopUpEnabled = true") {
          val state = component.state.value
          state.monthlyTopUpAmount shouldBe 300_000L
          state.autoTopUpEnabled shouldBe true
        }

        And("user taps save action with valid name, icon, and color") {
          beforeEach {
            component.onNameChanged("Groceries")
            component.onColorSelected(0xFF4A7C59)
            component.onIconSelected("shopping_bag")
            component.onSaveClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("pocket is saved in repository with monthly allocation settings preserved") {
            completedCallback.shouldBeCalled()

            val pockets = pocketRepository.observePockets().first()
            pockets shouldHaveSize 1
            val savedEntity = pocketRepository.getPocketById(pockets[0].id)
            savedEntity.shouldNotBeNull()
            savedEntity.monthlyTopUpAmount shouldBe 300_000L
            savedEntity.autoTopUpEnabled shouldBe true
          }
        }
      }

      When("user taps save action with valid name, icon, and color") {
        beforeEach {
          component.onNameChanged("Groceries")
          component.onColorSelected(0xFF4A7C59)
          component.onIconSelected("shopping_bag")
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
        }
      }

      When("user picks a category in the simple creation mode") {
        val cafe =
            FakeCategoryFactory.create(
                id = 10,
                name = "Cafe & Dining",
                icon = "coffee",
                color = 0xFFE57373,
            )
        val transport =
            FakeCategoryFactory.create(
                id = 11,
                name = "Transport",
                icon = "directions_car",
                color = 0xFF1E88E5,
            )

        beforeEach { component.onCategorySelected(cafe) }

        Then("the pocket inherits the category name, icon and color") {
          val state = component.state.value
          state.nameText shouldBe "Cafe & Dining"
          state.selectedIcon shouldBe "coffee"
          state.selectedColor shouldBe 0xFFE57373
          state.linkedCategoryIds shouldBe setOf(10L)
        }

        And("user picks a different category") {
          beforeEach { component.onCategorySelected(transport) }

          Then("the identity re-syncs to the new category") {
            val state = component.state.value
            state.nameText shouldBe "Transport"
            state.selectedIcon shouldBe "directions_car"
            state.selectedColor shouldBe 0xFF1E88E5
            state.linkedCategoryIds shouldBe setOf(11L)
          }
        }
      }

      When("user opens manual setup and only dismisses the confirmation") {
        beforeEach {
          component.onManualSetupClicked()
          component.onManualConfirmDismissed()
        }

        Then("manual mode stays off and the dialog is hidden") {
          val state = component.state.value
          state.manualMode shouldBe false
          state.showManualConfirm shouldBe false
        }
      }

      When("user confirms manual setup, customizes the pocket and links two categories") {
        val categoryRepository by testValue { koin.get<CategoryRepository>() }
        var cafe by testValue { FakeCategoryFactory.create() }
        var groceries by testValue { FakeCategoryFactory.create() }

        beforeEach {
          val cafeId = categoryRepository.addCategory("Cafe & Dining", "coffee", 0xFFE57373)
          val groceriesId = categoryRepository.addCategory("Groceries", "shopping_cart", 0xFF4A7C59)
          cafe =
              FakeCategoryFactory.create(
                  id = cafeId,
                  name = "Cafe & Dining",
                  icon = "coffee",
                  color = 0xFFE57373,
              )
          groceries =
              FakeCategoryFactory.create(
                  id = groceriesId,
                  name = "Groceries",
                  icon = "shopping_cart",
                  color = 0xFF4A7C59,
              )
          testCoroutineScheduler.advanceUntilIdle()
          component.onCategorySelected(cafe)
          component.onManualSetupClicked()
          component.onManualConfirmConfirmed()
          component.onNameChanged("Travel")
          component.onLinkedCategoryToggled(groceries)
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("manual mode is on and the identity no longer follows the category") {
          component.state.value.manualMode shouldBe true
          val pockets = pocketRepository.observePockets().first()
          pockets shouldHaveSize 1
          pockets[0].name shouldBe "Travel"
        }

        Then("the saved pocket links both categories with the first as primary") {
          val pockets = pocketRepository.observePockets().first()
          val saved = pockets[0]
          saved.categoryIds shouldBe listOf(cafe.id, groceries.id)
          saved.categoryId shouldBe cafe.id
          pocketRepository.getPocketCategoryIds(saved.id) shouldBe listOf(cafe.id, groceries.id)
        }
      }

      When("user picks categories on the pick step and continues") {
        val categoryRepository by testValue { koin.get<CategoryRepository>() }
        var cafe by testValue { FakeCategoryFactory.create() }
        var groceries by testValue { FakeCategoryFactory.create() }

        beforeEach {
          val cafeId = categoryRepository.addCategory("Cafe & Dining", "coffee", 0xFFE57373)
          val groceriesId = categoryRepository.addCategory("Groceries", "shopping_cart", 0xFF4A7C59)
          cafe =
              FakeCategoryFactory.create(
                  id = cafeId,
                  name = "Cafe & Dining",
                  icon = "coffee",
                  color = 0xFFE57373,
              )
          groceries =
              FakeCategoryFactory.create(
                  id = groceriesId,
                  name = "Groceries",
                  icon = "shopping_cart",
                  color = 0xFF4A7C59,
              )
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the flow starts on the pick step") {
          component.state.value.phase shouldBe PocketCreationPhase.CategoryPick
        }

        And("user picks exactly one category and continues") {
          beforeEach {
            component.onPickedCategoryToggled(cafe)
            component.onContinueClicked()
          }

          Then("the simple form opens with the identity inherited from the category") {
            val state = component.state.value
            state.phase shouldBe PocketCreationPhase.Form
            state.manualMode shouldBe false
            state.nameText shouldBe "Cafe & Dining"
            state.selectedIcon shouldBe "coffee"
            state.selectedColor shouldBe 0xFFE57373
            state.linkedCategoryIds shouldBe setOf(cafe.id)
          }
        }

        And("user picks two categories and continues") {
          beforeEach {
            component.onPickedCategoryToggled(cafe)
            component.onPickedCategoryToggled(groceries)
            component.onContinueClicked()
          }

          Then("the advanced form opens with both categories linked") {
            val state = component.state.value
            state.phase shouldBe PocketCreationPhase.Form
            state.manualMode shouldBe true
            state.manualFromSimple shouldBe false
            state.linkedCategoryIds shouldBe setOf(cafe.id, groceries.id)
          }

          And("user goes back") {
            beforeEach { component.onBackClicked() }

            Then("the pick step returns with the picks kept and nothing cancelled") {
              val state = component.state.value
              state.phase shouldBe PocketCreationPhase.CategoryPick
              state.pickedCategoryIds shouldBe setOf(cafe.id, groceries.id)
              cancelledCallback.shouldNotBeCalled()
            }
          }
        }

        And("user taps the no category option") {
          beforeEach { component.onNoCategoryClicked() }

          Then("the advanced form opens with no categories linked") {
            val state = component.state.value
            state.phase shouldBe PocketCreationPhase.Form
            state.manualMode shouldBe true
            state.linkedCategoryIds shouldBe emptySet()
          }
        }
      }

      When("user goes back from manual setup before saving") {
        val cafe =
            FakeCategoryFactory.create(
                id = 10,
                name = "Cafe & Dining",
                icon = "coffee",
                color = 0xFFE57373,
            )

        beforeEach {
          component.onCategorySelected(cafe)
          component.onManualSetupClicked()
          component.onManualConfirmConfirmed()
          component.onNameChanged("Custom Name")
          component.onBackClicked()
        }

        Then("the flow returns to simple mode without cancelling") {
          val state = component.state.value
          state.manualMode shouldBe false
          state.nameText shouldBe "Cafe & Dining"
          state.linkedCategoryIds shouldBe setOf(10L)
          cancelledCallback.shouldNotBeCalled()
        }
      }
    }

    Given("PocketSettingsComponent initialized in edit mode") {
      val koin by integrationKoin()
      val completedCallback by testValue { FakeCallback1<Long>() }
      val cancelledCallback by testValue { FakeCallback<Unit>() }
      val pocketRepository by testValue { koin.get<PocketRepository>() }
      var pocketId: Long by testValue { 0 }

      beforeEach {
        pocketId =
            pocketRepository.addPocket(
                name = "Original Name",
                categoryTag = "shopping",
                icon = "shopping_bag",
                budgetAmount = 50_000L,
                color = 0xFF112233,
                categoryIds = emptyList(),
            )
        testCoroutineScheduler.advanceUntilIdle()
      }

      val component: PocketSettingsComponent by testValue {
        koin
            .get<PocketSettingsComponentFactory>()
            .create(
                cContext = TestCContext(),
                pocketId = pocketId,
                onCompleted = completedCallback,
                onCancelled = cancelledCallback,
            )
      }

      When("user updates pocket details and clicks save") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          component.onNameChanged("Updated Name")
          component.onIconSelected("restaurant")
          component.onColorSelected(0xFF998877)
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("existing pocket is updated without creating a new pocket") {
          completedCallback.shouldBeCalled()

          val pockets = pocketRepository.observePockets().first()
          pockets shouldHaveSize 1
          val updated = pockets.first()
          updated.name shouldBe "Updated Name"
          updated.icon shouldBe "restaurant"
          updated.color shouldBe 0xFF998877
        }
      }

      When("user clicks delete pocket") {
        val deletedCallback by testValue { FakeCallback<Unit>() }
        val editComponentWithDelete: PocketSettingsComponent by testValue {
          koin
              .get<PocketSettingsComponentFactory>()
              .create(
                  cContext = TestCContext(),
                  pocketId = pocketId,
                  onCompleted = completedCallback,
                  onCancelled = cancelledCallback,
                  onDeleted = deletedCallback,
              )
        }

        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          editComponentWithDelete.onDeleteClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("pocket is deleted from repository and onDeleted callback is invoked") {
          deletedCallback.shouldBeCalled()

          val pockets = pocketRepository.observePockets().first()
          pockets.none { it.id == pocketId } shouldBe true
        }
      }

      When("pocket has monthly top up settings configured") {
        var configuredPocketId: Long by testValue { 0 }

        beforeEach {
          configuredPocketId =
              pocketRepository.addPocket(
                  name = "Subscriptions",
                  categoryTag = "Services",
                  budgetAmount = 0L,
                  icon = "card",
                  monthlyTopUpAmount = 300_000L,
                  autoTopUpEnabled = true,
              )
          testCoroutineScheduler.advanceUntilIdle()
        }

        val autoTopUpComponent: PocketSettingsComponent by testValue {
          koin
              .get<PocketSettingsComponentFactory>()
              .create(
                  cContext = TestCContext(),
                  pocketId = configuredPocketId,
                  onCompleted = completedCallback,
                  onCancelled = cancelledCallback,
              )
        }

        Then("state displays existing monthly top-up amount and toggle enabled") {
          testCoroutineScheduler.advanceUntilIdle()
          val state = autoTopUpComponent.state.value
          state.monthlyTopUpAmount shouldBe 300_000L
          state.autoTopUpEnabled shouldBe true
        }

        And("user toggles off auto-refill and saves") {
          beforeEach {
            testCoroutineScheduler.advanceUntilIdle()
            autoTopUpComponent.onAutoTopUpEnabledChanged(false)
            autoTopUpComponent.onSaveClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("pocket repository updates pocket with autoTopUpEnabled = false") {
            completedCallback.shouldBeCalled()

            val pocket = pocketRepository.getPocketById(configuredPocketId)
            pocket.shouldNotBeNull()
            pocket.autoTopUpEnabled shouldBe false
            pocket.monthlyTopUpAmount shouldBe 300_000L
          }
        }
      }
    }

    Given("PocketSettingsComponent edit mode detection") {
      val koin by integrationKoin()
      val pocketRepository by testValue { koin.get<PocketRepository>() }
      val categoryRepository by testValue { koin.get<CategoryRepository>() }
      var categoryId: Long by testValue { 0 }
      var otherCategoryId: Long by testValue { 0 }

      beforeEach {
        categoryId = categoryRepository.addCategory("Cafe", "coffee", 0xFF445566)
        otherCategoryId = categoryRepository.addCategory("Travel", "flight", 0xFF112233)
        testCoroutineScheduler.advanceUntilIdle()
      }

      fun editComponent(pocketId: Long): PocketSettingsComponent =
          koin.get<PocketSettingsComponentFactory>().create(
              cContext = TestCContext(),
              pocketId = pocketId,
          )

      When("the pocket name and icon exactly match its only category") {
        Then("it opens in the simple setup with the identity following the category") {
          val pocketId =
              pocketRepository.addPocket(
                  name = "Cafe",
                  categoryTag = "Cafe",
                  icon = "coffee",
                  budgetAmount = 10_000L,
                  color = 0xFF999999,
                  categoryIds = listOf(categoryId),
              )
          val component = editComponent(pocketId)
          testCoroutineScheduler.advanceUntilIdle()
          component.state.value.manualMode shouldBe false
          component.state.value.nameText shouldBe "Cafe"
          component.state.value.selectedIcon shouldBe "coffee"
          component.state.value.selectedColor shouldBe 0xFF445566
        }
      }

      When("the pocket name differs from its category") {
        Then("it opens in the advanced setup") {
          val pocketId =
              pocketRepository.addPocket(
                  name = "My Cafe",
                  categoryTag = "Cafe",
                  icon = "coffee",
                  budgetAmount = 10_000L,
                  categoryIds = listOf(categoryId),
              )
          val component = editComponent(pocketId)
          testCoroutineScheduler.advanceUntilIdle()
          component.state.value.manualMode shouldBe true
        }
      }

      When("the pocket icon differs from its category") {
        Then("it opens in the advanced setup") {
          val pocketId =
              pocketRepository.addPocket(
                  name = "Cafe",
                  categoryTag = "Cafe",
                  icon = "restaurant",
                  budgetAmount = 10_000L,
                  categoryIds = listOf(categoryId),
              )
          val component = editComponent(pocketId)
          testCoroutineScheduler.advanceUntilIdle()
          component.state.value.manualMode shouldBe true
        }
      }

      When("the pocket follows two categories") {
        Then("it opens in the advanced setup") {
          val pocketId =
              pocketRepository.addPocket(
                  name = "Cafe",
                  categoryTag = "Cafe",
                  icon = "coffee",
                  budgetAmount = 10_000L,
                  categoryIds = listOf(categoryId, otherCategoryId),
              )
          val component = editComponent(pocketId)
          testCoroutineScheduler.advanceUntilIdle()
          component.state.value.manualMode shouldBe true
        }
      }

      When("the pocket has no category") {
        Then("it opens in the advanced setup") {
          val pocketId =
              pocketRepository.addPocket(
                  name = "Cafe",
                  categoryTag = "",
                  icon = "coffee",
                  budgetAmount = 10_000L,
                  categoryIds = emptyList(),
              )
          val component = editComponent(pocketId)
          testCoroutineScheduler.advanceUntilIdle()
          component.state.value.manualMode shouldBe true
        }
      }
    }
  }
}
