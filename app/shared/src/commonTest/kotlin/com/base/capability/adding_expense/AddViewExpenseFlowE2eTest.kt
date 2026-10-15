package com.base.capability.adding_expense

import com.base.capability.main.MainComponent
import com.base.capability.root_navigation.RootComponentFactory
import com.base.component.TestCContext
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.assertHomeComponent
import com.base.util.assertMainComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

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
          val main = root.childStack.value.active.instance.shouldBeInstanceOf<MainComponent>()
          val homeChild =
              main.stack.value.active.instance.shouldBeInstanceOf<MainComponent.Child.Home>()
          homeChild.component.state.value.expenses shouldHaveSize 0
        }

        And("user navigates to add expense and saves an expense") {
          beforeEach {
            val main = root.childStack.value.active.instance.shouldBeInstanceOf<MainComponent>()
            val homeChild =
                main.stack.value.active.instance.shouldBeInstanceOf<MainComponent.Child.Home>()
            homeChild.component.onAddExpenseClicked()

            val addExpenseChild =
                main.stack.value.active.instance.shouldBeInstanceOf<
                    MainComponent.Child.AddExpense
                >()
            addExpenseChild.component.onAmountChanged("320000")
            addExpenseChild.component.onDescriptionChanged("Coffee and croissant")
            addExpenseChild.component.onSaveClicked()
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
