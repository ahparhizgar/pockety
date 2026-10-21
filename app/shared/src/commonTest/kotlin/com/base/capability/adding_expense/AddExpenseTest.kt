package com.base.capability.adding_expense

import com.base.capability.adding_expense.add.AddExpenseComponent
import com.base.capability.adding_expense.add.DefaultAddExpenseComponentFactory
import com.base.capability.home.HomeComponent
import com.base.capability.home.HomeComponentFactory
import com.base.component.TestCContext
import com.base.db.CandidateExpenseRepository
import com.base.db.CategoryRepository
import com.base.db.FakeCategoryFactory
import com.base.notification.ExpenseNotificationManager
import com.base.notification.FakeExpenseNotificationManager
import com.base.testValue
import com.base.usermessage.UiString
import com.base.util.FakeCallback
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.error_empty_description
import pockety.app.shared.generated.resources.error_invalid_amount

class AddExpenseTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("AddExpenseComponent") {
      val koin by integrationKoin()
      val completedCallback by testValue { FakeCallback<Unit>() }
      val cancelledCallback by testValue { FakeCallback<Unit>() }
      val component: AddExpenseComponent by testValue {
        koin
            .get<DefaultAddExpenseComponentFactory>()
            .create(
                cContext = TestCContext(),
                onSelectCategory = {},
                onCompleted = completedCallback,
                onCancelled = cancelledCallback,
            )
      }
      val homeComponent: HomeComponent by testValue {
        koin
            .get<HomeComponentFactory>()
            .create(
                cContext = TestCContext(),
                onAddExpense = {},
            )
      }

      When("submitting with empty amount and empty description") {
        beforeEach {
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("shows amount and description validation errors") {
          val state = component.state.value
          state.amountError shouldBe UiString(Res.string.error_invalid_amount)
          state.descriptionError shouldBe UiString(Res.string.error_empty_description)
          completedCallback.shouldNotBeCalled()
        }
      }

      When("submitting with zero amount") {
        beforeEach {
          component.onAmountChanged("0")
          component.onDescriptionChanged("Coffee")
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("shows amount validation error") {
          val state = component.state.value
          state.amountError shouldBe UiString(Res.string.error_invalid_amount)
          state.descriptionError.shouldBeNull()
          completedCallback.shouldNotBeCalled()
        }
      }

      When("submitting with negative amount") {
        beforeEach {
          component.onAmountChanged("-500")
          component.onDescriptionChanged("Coffee")
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("shows amount validation error") {
          val state = component.state.value
          state.amountError shouldBe UiString(Res.string.error_invalid_amount)
          completedCallback.shouldNotBeCalled()
        }
      }

      When("submitting with valid amount and description") {
        beforeEach {
          component.onAmountChanged("15000")
          component.onDescriptionChanged("Groceries")
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("saves expense and reflects on home screen while calling onCompleted") {
          val state = component.state.value
          state.amountError.shouldBeNull()
          state.descriptionError.shouldBeNull()
          completedCallback.shouldBeCalled()

          val expenses = homeComponent.state.value.expenses
          expenses shouldHaveSize 1
          expenses[0].amount shouldBe 15000
          expenses[0].description shouldBe "Groceries"
        }
      }

      When("submitting with linked candidateExpenseId") {
        val candidateRepo by testValue { koin.get<CandidateExpenseRepository>() }
        val notificationManager by testValue {
          koin.get<ExpenseNotificationManager>() as FakeExpenseNotificationManager
        }
        val candidateId by testValue {
          candidateRepo.addCandidate("Mellat", "Withdrawal 20000", 20000L)
        }
        val candidateComponent: AddExpenseComponent by testValue {
          koin
              .get<DefaultAddExpenseComponentFactory>()
              .create(
                  cContext = TestCContext(),
                  config =
                      AddExpenseComponent.Config(
                          initialAmount = "20000",
                          candidateExpenseId = candidateId,
                      ),
                  onSelectCategory = {},
                  onCompleted = completedCallback,
                  onCancelled = cancelledCallback,
              )
        }

        beforeEach {
          candidateComponent.onDescriptionChanged("Dinner")
          candidateComponent.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("updates candidate status to ACCEPTED and cancels notification") {
          completedCallback.shouldBeCalled()
          notificationManager.cancelledCandidateIds shouldBe listOf(candidateId)
        }
      }

      When("submitting with valid amount, description, and selected category") {
        var categoryId = 0L

        beforeEach {
          val categoryRepo = koin.get<CategoryRepository>()
          categoryId = categoryRepo.addCategory("Dining", "restaurant", 0xFFE57373)
          val categories = categoryRepo.observeCategories().first()
          val category = categories.first { cat -> cat.id == categoryId }
          component.onAmountChanged("75000")
          component.onDescriptionChanged("Dinner with colleagues")
          component.onCategorySelected(category)
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("expense is persisted in repository with categoryId") {
          val expenses = homeComponent.state.value.expenses
          expenses shouldHaveSize 1
          expenses[0].amount shouldBe 75000L
          expenses[0].description shouldBe "Dinner with colleagues"
          expenses[0].categoryId shouldBe categoryId
        }
      }

      When("user selects an associated category directly") {
        val category = FakeCategoryFactory.create(id = 10, name = "Dining Out", icon = "restaurant")

        beforeEach { component.onCategorySelected(category) }

        Then("selectedCategory is updated in state") {
          val state = component.state.value
          state.selectedCategory?.id shouldBe 10L
          state.selectedCategory?.name shouldBe "Dining Out"
          state.selectedCategory?.icon shouldBe "restaurant"
        }
      }

      When("user clears the selected category") {
        val category = FakeCategoryFactory.create(id = 10, name = "Dining Out")

        beforeEach {
          component.onCategorySelected(category)
          component.onCategoryCleared()
        }

        Then("selectedCategory becomes null") {
          val state = component.state.value
          state.selectedCategory.shouldBeNull()
        }
      }

      When("cancel is clicked") {
        beforeEach { component.onCancelClicked() }

        Then("calls onCancelled") { cancelledCallback.shouldBeCalled() }
      }
    }
  }
}
