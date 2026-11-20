package com.base.feature.expense

import com.base.component.TestCContext
import com.base.feature.root.RootComponentFactory
import com.base.feature.root.RootFlowDestination
import com.base.navigation.NavigationAction
import com.base.navigation.NavigationActionBus
import com.base.testValue
import com.base.ui.components.PocketyTab
import com.base.util.MainDispatcherExtension
import com.base.util.assertAddExpenseComponent
import com.base.util.assertAddExpenseFlow
import com.base.util.assertAnalyticsComponent
import com.base.util.assertMainComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class NewExpenseEntryFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("a new expense action queued before a fresh application starts") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val bus by testValue { koin.get<NavigationActionBus>() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      When("the application starts without any pockets") {
        beforeEach {
          bus.emit(NavigationAction.OpenNewExpense)
          root
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("a blank expense is available above the main screen") {
          val state = root.assertAddExpenseFlow().assertAddExpenseComponent().state.value
          state.amountText shouldBe ""
          state.descriptionText shouldBe ""
          state.selectedPocket shouldBe null
          state.isEditMode shouldBe false
          root.childStack.value.backStack.map { it.configuration } shouldBe
              listOf(RootFlowDestination.Main)
        }

        And("the user records an expense without a pocket") {
          beforeEach {
            val expense = root.assertAddExpenseFlow().assertAddExpenseComponent()
            expense.onAmountChanged("1000")
            expense.onDescriptionChanged("Coffee")
            expense.onSaveClicked()
            testCoroutineScheduler.advanceUntilIdle()
            root.assertMainComponent().onTabSelect(PocketyTab.Analytics)
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("the expense is saved and main navigation is restored") {
            val analytics = root.assertMainComponent().assertAnalyticsComponent()
            analytics.state.value.monthlyOutflow shouldBe 1000L
          }
        }
      }
    }

    Given("the application is already running") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val bus by testValue { koin.get<NavigationActionBus>() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      beforeEach {
        root
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("the same new expense action arrives repeatedly while a draft is open") {
        beforeEach {
          bus.emit(NavigationAction.OpenNewExpense)
          testCoroutineScheduler.advanceUntilIdle()
          root.assertAddExpenseFlow().assertAddExpenseComponent().onDescriptionChanged("Draft")
          root.assertAddExpenseFlow().assertAddExpenseComponent().onSelectCategoryClicked()
          testCoroutineScheduler.advanceUntilIdle()
          repeat(3) { bus.emit(NavigationAction.OpenNewExpense) }
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("one expense form retains the draft") {
          root
              .assertAddExpenseFlow()
              .assertAddExpenseComponent()
              .state
              .value
              .descriptionText shouldBe "Draft"
          root.childStack.value.backStack shouldHaveSize 1
        }

        And("the user cancels the draft") {
          beforeEach {
            root.assertAddExpenseFlow().assertAddExpenseComponent().onCancelClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }
          Then("the main screen is shown without another expense form underneath") {
            root.assertMainComponent()
          }
        }
      }

      When("a new expense action arrives while a candidate form is open") {
        beforeEach {
          bus.emit(NavigationAction.OpenAddExpense(candidateExpenseId = 1L, initialAmount = "2000"))
          testCoroutineScheduler.advanceUntilIdle()
          bus.emit(NavigationAction.OpenNewExpense)
          testCoroutineScheduler.advanceUntilIdle()
        }
        Then("one blank expense replaces the candidate form") {
          root.assertAddExpenseFlow().assertAddExpenseComponent().state.value.amountText shouldBe ""
          root.childStack.value.backStack shouldHaveSize 1
        }
      }
    }
  }
}
