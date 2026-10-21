package com.base.capability.adding_expense

import com.base.capability.root_navigation.RootComponentFactory
import com.base.component.TestCContext
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.assertAddCategoryComponent
import com.base.util.assertAddExpenseComponent
import com.base.util.assertAddExpenseFlow
import com.base.util.assertCategorySelectionComponent
import com.base.util.assertHomeComponent
import com.base.util.assertMainComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

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
  }
}
