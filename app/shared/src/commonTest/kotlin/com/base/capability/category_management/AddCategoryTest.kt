package com.base.capability.category_management

import com.base.component.TestCContext
import com.base.db.CategoryRepository
import com.base.testValue
import com.base.util.FakeCallback
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class AddCategoryTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("AddCategoryComponent") {
      val koin by integrationKoin()
      val completedCallback by testValue { FakeCallback<Unit>() }
      val cancelledCallback by testValue { FakeCallback<Unit>() }
      val categoryRepository by testValue { koin.get<CategoryRepository>() }

      val component: AddCategoryComponent by testValue {
        koin
            .get<AddCategoryComponentFactory>()
            .create(
                cContext = TestCContext(),
                onCompleted = completedCallback,
                onCancelled = cancelledCallback,
            )
      }

      When("submitting with an empty category name") {
        beforeEach {
          component.onNameChanged("")
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("validation error is shown and category is not saved") {
          val state = component.state.value
          state.nameError.shouldNotBeNull()
          completedCallback.shouldNotBeCalled()

          val categories = categoryRepository.observeCategories().first()
          categories shouldHaveSize 0
        }
      }

      When("submitting with a valid category name, color, and icon") {
        beforeEach {
          component.onNameChanged("Coffee")
          component.onColorSelected(0xFF6F4E37)
          component.onIconSelected("coffee")
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("category is saved to repository and onCompleted callback is called") {
          completedCallback.shouldBeCalled()

          val categories = categoryRepository.observeCategories().first()
          categories shouldHaveSize 1
          categories[0].name shouldBe "Coffee"
          categories[0].color shouldBe 0xFF6F4E37
          categories[0].icon shouldBe "coffee"
        }
      }
    }
  }
}
