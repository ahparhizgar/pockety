package com.base.feature.expense

import com.base.component.TestCContext
import com.base.db.CandidateExpenseRepository
import com.base.db.CategoryEntity
import com.base.db.CategoryRepository
import com.base.db.ExpenseRepository
import com.base.db.FakeCategoryFactory
import com.base.db.FakePocketItemModelFactory
import com.base.db.PocketRepository
import com.base.feature.expense.add.AddExpenseComponent
import com.base.feature.expense.add.DefaultAddExpenseComponentFactory
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
import pockety.app.shared.generated.resources.error_invalid_amount

class AddExpenseTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("AddExpenseComponent") {
      val koin by integrationKoin()
      val completedCallback by testValue { FakeCallback<Unit>() }
      val cancelledCallback by testValue { FakeCallback<Unit>() }
      val selectPocketCallback by testValue { FakeCallback<Unit>() }
      val component: AddExpenseComponent by testValue {
        koin
            .get<DefaultAddExpenseComponentFactory>()
            .create(
                cContext = TestCContext(),
                onSelectCategory = {},
                onSelectPocket = selectPocketCallback,
                onCompleted = completedCallback,
                onCancelled = cancelledCallback,
            )
      }

      When("submitting with empty amount and empty description") {
        beforeEach {
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("shows amount validation error only and descriptionError remains null") {
          val state = component.state.value
          state.amountError shouldBe UiString(Res.string.error_invalid_amount)
          state.descriptionError.shouldBeNull()
          completedCallback.shouldNotBeCalled()
        }
      }

      When("submitting with valid amount and empty description") {
        beforeEach {
          component.onAmountChanged("30000")
          component.onDescriptionChanged("")
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("saves expense with empty description and calls onCompleted without errors") {
          val state = component.state.value
          state.amountError.shouldBeNull()
          state.descriptionError.shouldBeNull()
          completedCallback.shouldBeCalled()

          val expenseRepo = koin.get<ExpenseRepository>()
          val expenses = expenseRepo.observeExpenses().first()
          expenses shouldHaveSize 1
          expenses[0].amount shouldBe 30000
          expenses[0].description shouldBe ""
        }
      }

      When("submitting with valid amount and whitespace description") {
        beforeEach {
          component.onAmountChanged("40000")
          component.onDescriptionChanged("   ")
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("saves expense with trimmed empty description without validation errors") {
          val state = component.state.value
          state.amountError.shouldBeNull()
          state.descriptionError.shouldBeNull()
          completedCallback.shouldBeCalled()

          val expenseRepo = koin.get<ExpenseRepository>()
          val expenses = expenseRepo.observeExpenses().first()
          expenses shouldHaveSize 1
          expenses[0].amount shouldBe 40000
          expenses[0].description shouldBe ""
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

        Then("saves expense and reflects in repository while calling onCompleted") {
          val state = component.state.value
          state.amountError.shouldBeNull()
          state.descriptionError.shouldBeNull()
          completedCallback.shouldBeCalled()

          val expenseRepo = koin.get<ExpenseRepository>()
          val expenses = expenseRepo.observeExpenses().first()
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
                  onSelectPocket = {},
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
          val expenseRepo = koin.get<ExpenseRepository>()
          val expenses = expenseRepo.observeExpenses().first()
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

      When("selecting category associated with a pocket") {
        val pocketRepo by testValue { koin.get<PocketRepository>() }
        val categoryRepo by testValue { koin.get<CategoryRepository>() }
        var pocketAId = 0L
        var category: CategoryEntity by testValue { FakeCategoryFactory.create() }

        beforeEach {
          val categoryId = categoryRepo.addCategory("Dining Out", "restaurant", 0xFFE57373)
          category = categoryRepo.observeCategories().first().first { cat -> cat.id == categoryId }
          pocketAId =
              pocketRepo.addPocket(
                  name = "Dining Pocket",
                  categoryTag = "Dining",
                  budgetAmount = 500_000,
                  icon = "restaurant",
                  categoryIds = listOf(categoryId),
              )
          testCoroutineScheduler.advanceUntilIdle()
          component.onCategorySelected(category)
        }

        Then("selectedPocket is automatically assigned in state") {
          val state = component.state.value
          state.selectedCategory?.id shouldBe category.id
          state.selectedPocket?.id shouldBe pocketAId
          state.selectedPocket?.name shouldBe "Dining Pocket"
        }
      }

      When("selecting category with no associated pocket") {
        val category = FakeCategoryFactory.create(id = 99, name = "Other", icon = "other")

        beforeEach { component.onCategorySelected(category) }

        Then("selectedPocket remains null") {
          val state = component.state.value
          state.selectedCategory?.id shouldBe 99L
          state.selectedPocket.shouldBeNull()
        }
      }

      When("pocket is assigned and user taps unlink") {
        val pocket = FakePocketItemModelFactory.create(id = 5, name = "Pocket 5", categoryId = 10L)
        val category = FakeCategoryFactory.create(id = 10, name = "Dining Out")

        beforeEach {
          component.onCategorySelected(category)
          component.onPocketSelected(pocket)
          component.onPocketCleared()
        }

        Then("selectedPocket is updated to null and selectedCategory remains unchanged") {
          val state = component.state.value
          state.selectedPocket.shouldBeNull()
          state.selectedCategory?.id shouldBe 10L
        }
      }

      When("pocket is auto-assigned and user manually selects another pocket") {
        val pocketB = FakePocketItemModelFactory.create(id = 2, name = "General Pocket")
        val category = FakeCategoryFactory.create(id = 10, name = "Dining Out")

        beforeEach {
          component.onCategorySelected(category)
          component.onPocketSelected(pocketB)
        }

        Then("selectedPocket is updated to the manually selected pocket") {
          val state = component.state.value
          state.selectedPocket?.id shouldBe 2L
          state.selectedPocket?.name shouldBe "General Pocket"
        }
      }

      When("saving an expense with an assigned pocket") {
        val pocketRepo by testValue { koin.get<PocketRepository>() }
        var pocketId = 0L

        beforeEach {
          pocketId =
              pocketRepo.addPocket(
                  name = "My Pocket",
                  categoryTag = "General",
                  budgetAmount = 500_000,
                  icon = "wallet",
              )
          testCoroutineScheduler.advanceUntilIdle()
          val pocket = pocketRepo.observePockets().first().first { p -> p.id == pocketId }
          component.onAmountChanged("50000")
          component.onDescriptionChanged("Coffee")
          component.onPocketSelected(pocket)
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("expense is persisted in repository with pocketId") {
          completedCallback.shouldBeCalled()
          val expenseRepo = koin.get<ExpenseRepository>()
          val expenses = expenseRepo.observeExpenses().first()
          expenses shouldHaveSize 1
          expenses[0].pocketId shouldBe pocketId
        }
      }

      When("saving an expense with unlinked pocket") {
        beforeEach {
          component.onAmountChanged("50000")
          component.onDescriptionChanged("Coffee")
          component.onPocketCleared()
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("expense is persisted in repository with null pocketId") {
          completedCallback.shouldBeCalled()
          val expenseRepo = koin.get<ExpenseRepository>()
          val expenses = expenseRepo.observeExpenses().first()
          expenses shouldHaveSize 1
          expenses[0].pocketId.shouldBeNull()
        }
      }

      When("user clicks select pocket") {
        beforeEach { component.onSelectPocketClicked() }

        Then("calls onSelectPocket") { selectPocketCallback.shouldBeCalled() }
      }

      When("component is initialized in add mode") {
        Then("isEditMode is false and delete action is unavailable") {
          component.state.value.isEditMode shouldBe false
          completedCallback.shouldNotBeCalled()
          component.onDeleteClicked()
          completedCallback.shouldNotBeCalled()
        }
      }

      When("AddExpenseComponent is created with existing expenseId") {
        val expenseRepo by testValue { koin.get<ExpenseRepository>() }
        val categoryRepo by testValue { koin.get<CategoryRepository>() }
        val pocketRepo by testValue { koin.get<PocketRepository>() }
        val catId by testValue { categoryRepo.addCategory("Food", "fastfood", 0xFF123456) }
        val pockId by testValue {
          pocketRepo.addPocket(
              name = "Food Pocket",
              categoryTag = "Food",
              budgetAmount = 100_000,
              icon = "fastfood",
              categoryIds = listOf(catId),
          )
        }
        val existingExpenseId by testValue {
          expenseRepo.addExpense(
              amount = 45000,
              description = "Burger",
              categoryId = catId,
              pocketId = pockId,
          )
        }
        val editCompletedCallback by testValue { FakeCallback<Unit>() }
        val editComponent: AddExpenseComponent by testValue {
          koin
              .get<DefaultAddExpenseComponentFactory>()
              .create(
                  cContext = TestCContext(),
                  config = AddExpenseComponent.Config(expenseId = existingExpenseId),
                  onSelectCategory = {},
                  onSelectPocket = {},
                  onCompleted = editCompletedCallback,
                  onCancelled = {},
              )
        }

        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("isEditMode is true and fields are pre-populated with existing expense data") {
          val state = editComponent.state.value
          state.isEditMode shouldBe true
          state.amountText shouldBe "45000"
          state.descriptionText shouldBe "Burger"
          state.selectedCategory?.id shouldBe catId
          state.selectedPocket?.id shouldBe pockId
        }
      }

      When("modifying amount and description in edit mode and saving") {
        val expenseRepo by testValue { koin.get<ExpenseRepository>() }
        val existingExpenseId by testValue {
          expenseRepo.addExpense(
              amount = 10000,
              description = "Initial Description",
          )
        }
        val editCompletedCallback by testValue { FakeCallback<Unit>() }
        val editComponent: AddExpenseComponent by testValue {
          koin
              .get<DefaultAddExpenseComponentFactory>()
              .create(
                  cContext = TestCContext(),
                  config = AddExpenseComponent.Config(expenseId = existingExpenseId),
                  onSelectCategory = {},
                  onSelectPocket = {},
                  onCompleted = editCompletedCallback,
                  onCancelled = {},
              )
        }

        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          editComponent.onAmountChanged("25000")
          editComponent.onDescriptionChanged("Updated Description")
          editComponent.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then(
            "updateExpense is called on repository with modified fields and onCompleted is invoked"
        ) {
          editCompletedCallback.shouldBeCalled()
          val updatedExpense = expenseRepo.getExpenseById(existingExpenseId)
          updatedExpense?.amount shouldBe 25000
          updatedExpense?.description shouldBe "Updated Description"
        }
      }

      When("clearing description in edit mode and saving") {
        val expenseRepo by testValue { koin.get<ExpenseRepository>() }
        val existingExpenseId by testValue {
          expenseRepo.addExpense(
              amount = 10000,
              description = "Initial Description",
          )
        }
        val editCompletedCallback by testValue { FakeCallback<Unit>() }
        val editComponent: AddExpenseComponent by testValue {
          koin
              .get<DefaultAddExpenseComponentFactory>()
              .create(
                  cContext = TestCContext(),
                  config = AddExpenseComponent.Config(expenseId = existingExpenseId),
                  onSelectCategory = {},
                  onSelectPocket = {},
                  onCompleted = editCompletedCallback,
                  onCancelled = {},
              )
        }

        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          editComponent.onDescriptionChanged("")
          editComponent.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("updates expense successfully with empty description") {
          editCompletedCallback.shouldBeCalled()
          val updatedExpense = expenseRepo.getExpenseById(existingExpenseId)
          updatedExpense?.amount shouldBe 10000
          updatedExpense?.description shouldBe ""
        }
      }

      When("user clicks Delete action in edit mode") {
        val expenseRepo by testValue { koin.get<ExpenseRepository>() }
        val existingExpenseId by testValue {
          expenseRepo.addExpense(
              amount = 15000,
              description = "To Delete",
          )
        }
        val deleteCompletedCallback by testValue { FakeCallback<Unit>() }
        val editComponent: AddExpenseComponent by testValue {
          koin
              .get<DefaultAddExpenseComponentFactory>()
              .create(
                  cContext = TestCContext(),
                  config = AddExpenseComponent.Config(expenseId = existingExpenseId),
                  onSelectCategory = {},
                  onSelectPocket = {},
                  onCompleted = deleteCompletedCallback,
                  onCancelled = {},
              )
        }

        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          editComponent.onDeleteClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("deleteExpense is called on repository and onCompleted is invoked") {
          deleteCompletedCallback.shouldBeCalled()
          val deletedExpense = expenseRepo.getExpenseById(existingExpenseId)
          deletedExpense.shouldBeNull()
        }
      }
    }
  }
}
