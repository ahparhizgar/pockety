package com.base.capability.adding_expense

import com.base.capability.root_navigation.RootComponentFactory
import com.base.component.TestCContext
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.assertAddExpenseComponent
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

    Given("user launches application") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      When("cold start completes") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("displays HomeScreen with empty expenses") {
          val home = root.assertMainComponent().assertHomeComponent()
          home.state.value.expenses shouldHaveSize 0
        }

        And("user navigates to add expense and saves an expense") {
          beforeEach {
            val main = root.assertMainComponent()
            val home = main.assertHomeComponent()
            home.onAddExpenseClicked()

            val addExpense = main.assertAddExpenseComponent()
            addExpense.onAmountChanged("320000")
            addExpense.onDescriptionChanged("Coffee and croissant")
            addExpense.onSaveClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("navigates back to HomeScreen displaying the recorded expense") {
            val home = root.assertMainComponent().assertHomeComponent()
            val expenses = home.state.value.expenses

            expenses shouldHaveSize 1
            expenses[0].amount shouldBe 320000
            expenses[0].description shouldBe "Coffee and croissant"
          }
        }
      }
    }
  }
}
