package com.base.feature.expense

import com.base.component.TestCContext
import com.base.db.CategoryRepository
import com.base.db.PocketRepository
import com.base.feature.root.RootComponentFactory
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.assertAddCategoryComponent
import com.base.util.assertAddExpenseComponent
import com.base.util.assertAddExpenseFlow
import com.base.util.assertCategorySelectionComponent
import com.base.util.assertHomeComponent
import com.base.util.assertMainComponent
import com.base.util.assertSelectPocketComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class AddExpenseFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("app on Main screen") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      When("user opens Add Expense") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          val home = root.assertMainComponent().assertHomeComponent()
          home.onAddExpenseClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("AddExpenseFlowComponent is active showing AddExpenseComponent") {
          val flow = root.assertAddExpenseFlow()
          flow.assertAddExpenseComponent()
        }

        And("user taps Change Category") {
          beforeEach {
            val flow = root.assertAddExpenseFlow()
            val addExpense = flow.assertAddExpenseComponent()
            addExpense.onSelectCategoryClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("CategorySelectionComponent is pushed on the flow stack") {
            val flow = root.assertAddExpenseFlow()
            flow.assertCategorySelectionComponent()
          }

          And("user taps Create New Category") {
            beforeEach {
              val flow = root.assertAddExpenseFlow()
              val categorySelection = flow.assertCategorySelectionComponent()
              categorySelection.onCreateCategoryClicked()
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("AddCategoryComponent is pushed on the flow stack") {
              val flow = root.assertAddExpenseFlow()
              flow.assertAddCategoryComponent()
            }

            And("user enters Coffee, selects color and icon, and saves") {
              beforeEach {
                val flow = root.assertAddExpenseFlow()
                val addCategory = flow.assertAddCategoryComponent()
                addCategory.onNameChanged("Coffee")
                addCategory.onColorSelected(0xFF6F4E37)
                addCategory.onIconSelected("coffee")
                addCategory.onSaveClicked()
                testCoroutineScheduler.advanceUntilIdle()
              }

              Then("category is saved, and flow pops back with Coffee category available") {
                val flow = root.assertAddExpenseFlow()
                val addExpense = flow.assertAddExpenseComponent()
                addExpense.state.value.selectedCategory?.id shouldBe 1L
                addExpense.state.value.selectedCategory?.name shouldBe "Coffee"
              }

              And("user enters amount and description, and taps save") {
                beforeEach {
                  val flow = root.assertAddExpenseFlow()
                  val addExpense = flow.assertAddExpenseComponent()
                  addExpense.onAmountChanged("45000")
                  addExpense.onDescriptionChanged("Morning flat white")
                  addExpense.onSaveClicked()
                  testCoroutineScheduler.advanceUntilIdle()
                }

                Then(
                    "expense is saved with Coffee category, flow completes, and Root displays the expense"
                ) {
                  val home = root.assertMainComponent().assertHomeComponent()
                  val expenses = home.state.value.expenses

                  expenses shouldHaveSize 1
                  expenses[0].amount shouldBe 45000L
                  expenses[0].description shouldBe "Morning flat white"
                  expenses[0].categoryId shouldBe 1L
                }
              }
            }
          }
        }
      }
    }

    Given("AddExpenseFlow with a pocket Coffee Pocket linked to category Cafe") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }
      val categoryRepo by testValue { koin.get<CategoryRepository>() }
      val pocketRepo by testValue { koin.get<PocketRepository>() }

      var cafeCategoryId = 0L
      var coffeePocketId = 0L
      var generalPocketId = 0L

      beforeEach {
        cafeCategoryId = categoryRepo.addCategory("Cafe", "coffee", 0xFF6F4E37)
        coffeePocketId =
            pocketRepo.addPocket(
                name = "Coffee Pocket",
                categoryTag = "Cafe",
                budgetAmount = 500_000,
                icon = "local_cafe",
                categoryId = cafeCategoryId,
            )
        generalPocketId =
            pocketRepo.addPocket(
                name = "General Pocket",
                categoryTag = "General",
                budgetAmount = 300_000,
                icon = "wallet",
            )
        testCoroutineScheduler.advanceUntilIdle()

        val home = root.assertMainComponent().assertHomeComponent()
        home.onAddExpenseClicked()
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("user navigates to CategorySelection and selects Cafe") {
        beforeEach {
          val flow = root.assertAddExpenseFlow()
          val addExpense = flow.assertAddExpenseComponent()
          addExpense.onSelectCategoryClicked()
          testCoroutineScheduler.advanceUntilIdle()

          val categorySelection = flow.assertCategorySelectionComponent()
          val cafeCategory =
              categorySelection.state.value.categories.first { cat -> cat.id == cafeCategoryId }
          categorySelection.onCategorySelected(cafeCategory)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("flow returns to AddExpense and selectedPocket is auto-assigned to Coffee Pocket") {
          val flow = root.assertAddExpenseFlow()
          val addExpense = flow.assertAddExpenseComponent()
          addExpense.state.value.selectedCategory?.id shouldBe cafeCategoryId
          addExpense.state.value.selectedPocket?.id shouldBe coffeePocketId
        }

        And("user enters valid amount, description, and saves") {
          beforeEach {
            val flow = root.assertAddExpenseFlow()
            val addExpense = flow.assertAddExpenseComponent()
            addExpense.onAmountChanged("35000")
            addExpense.onDescriptionChanged("Espresso")
            addExpense.onSaveClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("expense is recorded with Coffee Pocket ID and pocket balance is deducted") {
            val home = root.assertMainComponent().assertHomeComponent()
            val expenses = home.state.value.expenses
            expenses shouldHaveSize 1
            expenses[0].pocketId shouldBe coffeePocketId

            val updatedPocket =
                pocketRepo.observePockets().first().first { p -> p.id == coffeePocketId }
            updatedPocket.remainingBalance shouldBe (500_000 - 35_000)
          }
        }
      }

      When("user taps the pocket card to select a different pocket") {
        beforeEach {
          val flow = root.assertAddExpenseFlow()
          val addExpense = flow.assertAddExpenseComponent()
          addExpense.onSelectCategoryClicked()
          testCoroutineScheduler.advanceUntilIdle()

          val categorySelection = flow.assertCategorySelectionComponent()
          val cafeCategory =
              categorySelection.state.value.categories.first { cat -> cat.id == cafeCategoryId }
          categorySelection.onCategorySelected(cafeCategory)
          testCoroutineScheduler.advanceUntilIdle()

          addExpense.onSelectPocketClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("flow navigates to PocketSelection child") {
          val flow = root.assertAddExpenseFlow()
          flow.assertSelectPocketComponent()
        }

        And("user selects General Pocket and saves expense") {
          beforeEach {
            val flow = root.assertAddExpenseFlow()
            val selectPocket = flow.assertSelectPocketComponent()
            val generalPocket =
                selectPocket.state.value.pockets.first { p -> p.id == generalPocketId }
            selectPocket.onPocketSelected(generalPocket)
            testCoroutineScheduler.advanceUntilIdle()

            val addExpense = flow.assertAddExpenseComponent()
            addExpense.onAmountChanged("20000")
            addExpense.onDescriptionChanged("Snack")
            addExpense.onSaveClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("expense is recorded with General Pocket ID") {
            val home = root.assertMainComponent().assertHomeComponent()
            val expenses = home.state.value.expenses
            expenses shouldHaveSize 1
            expenses[0].pocketId shouldBe generalPocketId
          }
        }
      }

      When("user unlinks the auto-assigned pocket and saves expense") {
        beforeEach {
          val flow = root.assertAddExpenseFlow()
          val addExpense = flow.assertAddExpenseComponent()
          addExpense.onSelectCategoryClicked()
          testCoroutineScheduler.advanceUntilIdle()

          val categorySelection = flow.assertCategorySelectionComponent()
          val cafeCategory =
              categorySelection.state.value.categories.first { cat -> cat.id == cafeCategoryId }
          categorySelection.onCategorySelected(cafeCategory)
          testCoroutineScheduler.advanceUntilIdle()

          addExpense.onPocketCleared()
          addExpense.onAmountChanged("20000")
          addExpense.onDescriptionChanged("Tea")
          addExpense.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("expense is saved with null pocketId and pocket balance remains intact") {
          val home = root.assertMainComponent().assertHomeComponent()
          val expenses = home.state.value.expenses
          expenses shouldHaveSize 1
          expenses[0].pocketId.shouldBeNull()

          val untouchedPocket =
              pocketRepo.observePockets().first().first { p -> p.id == coffeePocketId }
          untouchedPocket.remainingBalance shouldBe 500_000
        }
      }
    }
  }
}
