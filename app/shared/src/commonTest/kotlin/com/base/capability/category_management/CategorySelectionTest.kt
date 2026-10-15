package com.base.capability.category_management

import com.base.component.TestCContext
import com.base.db.CategoryEntity
import com.base.db.CategoryRepository
import com.base.testValue
import com.base.util.FakeCallback
import com.base.util.FakeCallback1
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class CategorySelectionTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("CategorySelectionComponent with existing categories") {
      val koin by integrationKoin()
      val selectedCallback by testValue { FakeCallback1<CategoryEntity?>() }
      val createCategoryCallback by testValue { FakeCallback<Unit>() }
      val backCallback by testValue { FakeCallback<Unit>() }
      val categoryRepository by testValue { koin.get<CategoryRepository>() }

      var diningCategory: CategoryEntity by testValue {
        CategoryEntity(
            name = "Dining",
            icon = "restaurant",
            color = 0xFFE57373,
            createdAt = kotlin.time.Clock.System.now(),
        )
      }
      var groceriesCategory: CategoryEntity by testValue {
        CategoryEntity(
            name = "Groceries",
            icon = "shopping_cart",
            color = 0xFF81C784,
            createdAt = kotlin.time.Clock.System.now(),
        )
      }

      beforeEach {
        val diningId = categoryRepository.addCategory("Dining", "restaurant", 0xFFE57373)
        val groceriesId = categoryRepository.addCategory("Groceries", "shopping_cart", 0xFF81C784)
        diningCategory =
            CategoryEntity(
                id = diningId,
                name = "Dining",
                icon = "restaurant",
                color = 0xFFE57373,
                createdAt = kotlin.time.Clock.System.now(),
            )
        groceriesCategory =
            CategoryEntity(
                id = groceriesId,
                name = "Groceries",
                icon = "shopping_cart",
                color = 0xFF81C784,
                createdAt = kotlin.time.Clock.System.now(),
            )
        testCoroutineScheduler.advanceUntilIdle()
      }

      val component: CategorySelectionComponent by testValue {
        koin
            .get<CategorySelectionComponentFactory>()
            .create(
                cContext = TestCContext(),
                initialSelectedCategoryId = diningCategory.id,
                onCategorySelected = selectedCallback,
                onCreateCategoryClicked = createCategoryCallback,
                onBack = backCallback,
            )
      }

      When("a category item is selected") {
        beforeEach {
          component.onCategorySelected(groceriesCategory)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("onCategorySelected callback is invoked with the chosen category") {
          selectedCallback.shouldBeCalled()
          selectedCallback.lastValue?.id shouldBe groceriesCategory.id
        }
      }

      When("the Uncategorized item is selected") {
        beforeEach {
          component.onUncategorizedSelected()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("onCategorySelected is invoked with null (clearing category)") {
          selectedCallback.shouldBeCalled()
          selectedCallback.lastValue.shouldBeNull()
        }
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
      }
    }
  }
}
