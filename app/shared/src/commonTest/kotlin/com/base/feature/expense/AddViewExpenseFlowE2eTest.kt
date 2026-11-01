package com.base.feature.expense

import com.base.component.TestCContext
import com.base.feature.root.RootComponentFactory
import com.base.testValue
import com.base.ui.components.PocketyTab
import com.base.util.MainDispatcherExtension
import com.base.util.assertAddExpenseComponent
import com.base.util.assertAddExpenseFlow
import com.base.util.assertAnalyticsComponent
import com.base.util.assertHomeComponent
import com.base.util.assertMainComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class AddViewExpenseFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("user launches application on Main tab") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      When("user adds an expense with amount 320,000 and description Coffee from Home FAB") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          val main = root.assertMainComponent()
          val home = main.assertHomeComponent()
          home.onAddExpenseClicked()
          testCoroutineScheduler.advanceUntilIdle()

          val flow = root.assertAddExpenseFlow()
          val addExpense = flow.assertAddExpenseComponent()
          addExpense.onAmountChanged("320000")
          addExpense.onDescriptionChanged("Coffee")
          addExpense.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("expense is saved and user returns to Main screen") { root.assertMainComponent() }

        And("user selects the Analytics tab") {
          beforeEach {
            val main = root.assertMainComponent()
            main.onTabSelect(PocketyTab.Analytics)
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then(
              "the Analyze screen displays the month switcher card with outflow 320,000 T and expense grouped under today"
          ) {
            val analytics = root.assertMainComponent().assertAnalyticsComponent()
            val state = analytics.state.value

            state.canNavigateNext shouldBe false
            state.monthlyOutflow shouldBe 320000L
            state.dateGroups shouldHaveSize 1
            state.dateGroups[0].expenses shouldHaveSize 1
            state.dateGroups[0].expenses[0].description shouldBe "Coffee"
            state.dateGroups[0].expenses[0].amount shouldBe 320000L
          }

          And("user taps the expense item in the Analyze tab") {
            var expenseId = 0L

            beforeEach {
              val analytics = root.assertMainComponent().assertAnalyticsComponent()
              expenseId = analytics.state.value.dateGroups[0].expenses[0].id
              analytics.onExpenseClicked(expenseId)
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("Root navigation pushes AddExpenseFlow in edit mode with pre-filled data") {
              val flow = root.assertAddExpenseFlow()
              val editExpense = flow.assertAddExpenseComponent()
              editExpense.state.value.isEditMode shouldBe true
              editExpense.state.value.amountText shouldBe "320000"
              editExpense.state.value.descriptionText shouldBe "Coffee"
            }

            And("user changes description to Latte & Croissant and saves") {
              beforeEach {
                val flow = root.assertAddExpenseFlow()
                val editExpense = flow.assertAddExpenseComponent()
                editExpense.onDescriptionChanged("Latte & Croissant")
                editExpense.onSaveClicked()
                testCoroutineScheduler.advanceUntilIdle()
              }

              Then(
                  "user returns to Analyze screen and the expense item reflects Latte & Croissant"
              ) {
                val analytics = root.assertMainComponent().assertAnalyticsComponent()
                val state = analytics.state.value

                state.monthlyOutflow shouldBe 320000L
                state.dateGroups shouldHaveSize 1
                state.dateGroups[0].expenses shouldHaveSize 1
                state.dateGroups[0].expenses[0].description shouldBe "Latte & Croissant"
              }

              And("user taps the expense item again and clicks Delete in TopAppBar") {
                beforeEach {
                  val analytics = root.assertMainComponent().assertAnalyticsComponent()
                  analytics.onExpenseClicked(expenseId)
                  testCoroutineScheduler.advanceUntilIdle()

                  val flow = root.assertAddExpenseFlow()
                  val editExpense = flow.assertAddExpenseComponent()
                  editExpense.onDeleteClicked()
                  testCoroutineScheduler.advanceUntilIdle()
                }

                Then(
                    "expense is deleted and user returns to Analyze screen showing empty state and 0 T outflow"
                ) {
                  val analytics = root.assertMainComponent().assertAnalyticsComponent()
                  val state = analytics.state.value

                  state.monthlyOutflow shouldBe 0L
                  state.dateGroups shouldHaveSize 0
                }
              }
            }
          }
        }
      }
    }
  }
}
