package com.base.feature.category.management

import com.base.component.TestCContext
import com.base.db.CategoryEntity
import com.base.db.CategoryRepository
import com.base.db.FakeCategoryFactory
import com.base.testValue
import com.base.util.FakeCallback
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class CategoryManagementTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("CategoryManagementComponent with existing categories") {
      val koin by integrationKoin()
      val addCallback by testValue { FakeCallback<Unit>() }
      val backCallback by testValue { FakeCallback<Unit>() }
      val categoryRepository by testValue { koin.get<CategoryRepository>() }

      var diningCategory: CategoryEntity by testValue {
        FakeCategoryFactory.create(name = "Dining", icon = "restaurant", color = 0xFFE57373)
      }
      var groceriesCategory: CategoryEntity by testValue {
        FakeCategoryFactory.create(name = "Groceries", icon = "shopping_cart", color = 0xFF81C784)
      }

      beforeEach {
        val diningId = categoryRepository.addCategory("Dining", "restaurant", 0xFFE57373)
        val groceriesId = categoryRepository.addCategory("Groceries", "shopping_cart", 0xFF81C784)
        diningCategory =
            FakeCategoryFactory.create(
                id = diningId,
                name = "Dining",
                icon = "restaurant",
                color = 0xFFE57373,
            )
        groceriesCategory =
            FakeCategoryFactory.create(
                id = groceriesId,
                name = "Groceries",
                icon = "shopping_cart",
                color = 0xFF81C784,
            )
        testCoroutineScheduler.advanceUntilIdle()
      }

      val component: CategoryManagementComponent by testValue {
        koin
            .get<CategoryManagementComponentFactory>()
            .create(
                cContext = TestCContext(),
                onAdd = addCallback,
                onBack = backCallback,
            )
      }

      When("search query is entered") {
        beforeEach {
          component.onSearchQueryChanged("Din")
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("list filters to categories matching query name") {
          val filtered = component.state.value.filteredCategories
          filtered shouldHaveSize 1
          filtered[0].name shouldBe "Dining"
        }

        And("the search query is cleared") {
          beforeEach {
            component.onSearchQueryChanged("")
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("list contains all categories again") {
            component.state.value.filteredCategories shouldHaveSize 2
          }
        }
      }

      When("delete is activated for a category") {
        beforeEach {
          component.onDeleteClicked(groceriesCategory)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the category is pending deletion and nothing is deleted yet") {
          component.state.value.categoryPendingDeletion?.id shouldBe groceriesCategory.id
          component.state.value.categories shouldHaveSize 2
        }

        And("the deletion is confirmed") {
          beforeEach {
            component.onDeleteConfirmed()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("the category is removed and nothing is pending deletion") {
            val state = component.state.value
            state.categories.map { it.name } shouldBe listOf("Dining")
            state.categoryPendingDeletion.shouldBeNull()
          }
        }

        And("the deletion is dismissed") {
          beforeEach {
            component.onDeleteDismissed()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("the category remains and nothing is pending deletion") {
            val state = component.state.value
            state.categories shouldHaveSize 2
            state.categoryPendingDeletion.shouldBeNull()
          }
        }
      }

      When("add is clicked") {
        beforeEach {
          component.onAddClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the add callback is invoked") { addCallback.shouldBeCalled() }
      }

      When("back is clicked") {
        beforeEach {
          component.onBackClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("the back callback is invoked") { backCallback.shouldBeCalled() }
      }
    }
  }
}
