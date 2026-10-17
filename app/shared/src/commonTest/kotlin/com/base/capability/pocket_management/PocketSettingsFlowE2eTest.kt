package com.base.capability.pocket_management

import com.base.capability.root_navigation.RootComponentFactory
import com.base.component.TestCContext
import com.base.db.CategoryRepository
import com.base.testValue
import com.base.ui.components.PocketyTab
import com.base.util.MainDispatcherExtension
import com.base.util.assertCategorySelection
import com.base.util.assertMainComponent
import com.base.util.assertPocketSettings
import com.base.util.assertPocketSettingsFlow
import com.base.util.assertPockets
import com.base.util.assertPocketsComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class PocketSettingsFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("app on Main screen at Pockets tab") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }
      val categoryRepository by testValue { koin.get<CategoryRepository>() }

      beforeEach {
        testCoroutineScheduler.advanceUntilIdle()
        val main = root.assertMainComponent()
        main.onTabSelect(PocketyTab.Pockets)
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("user taps Add Pocket floating action button") {
        beforeEach {
          val pockets = root.assertPockets()
          pockets.onAddPocketClick()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then(
            "active child in RootComponent is PocketSettingsFlowComponent with PocketSettingsComponent active"
        ) {
          val flow = root.assertPocketSettingsFlow()
          flow.assertPocketSettings()
        }

        And("user taps Change Category on pocket settings") {
          beforeEach {
            categoryRepository.addCategory(
                name = "Groceries Category",
                icon = "shopping_bag",
                color = 0xFF4A7C59,
            )
            testCoroutineScheduler.advanceUntilIdle()

            val flow = root.assertPocketSettingsFlow()
            val pocketSettings = flow.assertPocketSettings()
            pocketSettings.onSelectCategoryClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("active child in PocketSettingsFlowComponent is CategorySelectionComponent") {
            val flow = root.assertPocketSettingsFlow()
            flow.assertCategorySelection()
          }

          And("user selects a category from the category list") {
            beforeEach {
              val flow = root.assertPocketSettingsFlow()
              val categorySelection = flow.assertCategorySelection()
              val category =
                  categorySelection.state.value.categories.first { cat ->
                    cat.name == "Groceries Category"
                  }
              categorySelection.onCategorySelected(category)
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then(
                "CategorySelectionComponent is popped returning to PocketSettingsComponent with selected category populated"
            ) {
              val flow = root.assertPocketSettingsFlow()
              val pocketSettings = flow.assertPocketSettings()
              pocketSettings.state.value.selectedCategory.shouldNotBeNull()
              pocketSettings.state.value.selectedCategory?.name shouldBe "Groceries Category"
            }

            And("user enters valid pocket name and taps Save Changes") {
              beforeEach {
                val flow = root.assertPocketSettingsFlow()
                val pocketSettings = flow.assertPocketSettings()
                pocketSettings.onNameChanged("Weekly Groceries")
                pocketSettings.onSaveClicked()
                testCoroutineScheduler.advanceUntilIdle()
              }

              Then(
                  "PocketSettingsFlowComponent is dismissed returning to PocketsScreen with new pocket visible"
              ) {
                val pocketsComponent = root.assertMainComponent().assertPocketsComponent()
                val pockets = pocketsComponent.state.value.pockets
                pockets shouldHaveSize 1
                pockets[0].name shouldBe "Weekly Groceries"
                pockets[0].categoryTag shouldBe "Groceries Category"
              }
            }
          }
        }
      }
    }

    Given("app in PocketSettingsFlowComponent") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      beforeEach {
        testCoroutineScheduler.advanceUntilIdle()
        val main = root.assertMainComponent()
        main.onTabSelect(PocketyTab.Pockets)
        testCoroutineScheduler.advanceUntilIdle()
        root.assertPockets().onAddPocketClick()
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("user taps back navigation button") {
        beforeEach {
          val flow = root.assertPocketSettingsFlow()
          val pocketSettings = flow.assertPocketSettings()
          pocketSettings.onBackClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then(
            "PocketSettingsFlowComponent is dismissed returning to PocketsScreen without creating a pocket"
        ) {
          val pocketsComponent = root.assertMainComponent().assertPocketsComponent()
          pocketsComponent.state.value.pockets shouldHaveSize 0
        }
      }
    }
  }
}
