package com.base.feature.category.management

import com.base.calendar.CalendarType
import com.base.component.TestCContext
import com.base.db.CategoryRepository
import com.base.db.ExpenseRepository
import com.base.feature.root.RootComponentFactory
import com.base.feature.settings.CurrencyType
import com.base.testValue
import com.base.ui.components.PocketyTab
import com.base.util.MainDispatcherExtension
import com.base.util.assertAddCategory
import com.base.util.assertBrowse
import com.base.util.assertCategoryManagementFlow
import com.base.util.assertMainComponent
import com.base.util.assertSettingsComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class CategoryManagementFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("the app is open") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }
      val categoryRepository by testValue { koin.get<CategoryRepository>() }
      val expenseRepository by testValue { koin.get<ExpenseRepository>() }
      var assignedExpenseId = 0L

      When("the user opens Settings Categories") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          root.assertMainComponent().onTabSelect(PocketyTab.Settings)
          testCoroutineScheduler.advanceUntilIdle()
          val settings = root.assertMainComponent().assertSettingsComponent()
          settings.onCalendarTypeSelected(CalendarType.PERSIAN)
          settings.onCurrencySelected(CurrencyType.RIALS)
          settings.onReminderToggled(false)
          testCoroutineScheduler.advanceUntilIdle()
          settings.onCategoriesClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("a category browse destination opens with an empty collection") {
          root.assertCategoryManagementFlow().assertBrowse().state.value.categories shouldBe
              emptyList()
        }

        And("the user deletes a category assigned to an expense") {
          beforeEach {
            val categoryId =
                categoryRepository.addCategory("Groceries", "shopping_cart", 0xFF81C784)
            assignedExpenseId =
                expenseRepository.addExpense(
                    amount = 12000,
                    description = "Supermarket",
                    categoryId = categoryId,
                )
            testCoroutineScheduler.advanceUntilIdle()
            val browse = root.assertCategoryManagementFlow().assertBrowse()
            val category = browse.state.value.categories.single { it.id == categoryId }
            browse.onDeleteClicked(category)
            testCoroutineScheduler.advanceUntilIdle()
            browse.onDeleteConfirmed()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("the category no longer shows in the browse destination") {
            root.assertCategoryManagementFlow().assertBrowse().state.value.categories shouldBe
                emptyList()
          }

          Then("the expense still exists and is uncategorized") {
            val expense =
                expenseRepository.observeExpenses().first().single { it.id == assignedExpenseId }
            expense.categoryId.shouldBeNull()
          }

          And("the user returns to Settings") {
            beforeEach {
              root.assertCategoryManagementFlow().assertBrowse().onBackClicked()
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("Settings shows the decreased category count") {
              root
                  .assertMainComponent()
                  .assertSettingsComponent()
                  .state
                  .value
                  .categoriesCount shouldBe 0
            }
          }
        }

        And("the user cancels deleting a category") {
          beforeEach {
            categoryRepository.addCategory("Groceries", "shopping_cart", 0xFF81C784)
            testCoroutineScheduler.advanceUntilIdle()
            val browse = root.assertCategoryManagementFlow().assertBrowse()
            browse.onDeleteClicked(browse.state.value.categories.single())
            testCoroutineScheduler.advanceUntilIdle()
            browse.onDeleteDismissed()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("the category remains and nothing is pending deletion") {
            val state = root.assertCategoryManagementFlow().assertBrowse().state.value
            state.categories.map { it.name } shouldBe listOf("Groceries")
            state.categoryPendingDeletion.shouldBeNull()
          }
        }

        And("the flow Back callback is invoked from Add") {
          beforeEach {
            root.assertCategoryManagementFlow().assertBrowse().onAddClicked()
            testCoroutineScheduler.advanceUntilIdle()
            root.assertCategoryManagementFlow().onBack()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("the browse destination remains active") {
            root.assertCategoryManagementFlow().assertBrowse().state.value.categories shouldBe
                emptyList()
          }
        }

        And("the user opens Add and saves a category") {
          beforeEach {
            root.assertCategoryManagementFlow().assertBrowse().onAddClicked()
            testCoroutineScheduler.advanceUntilIdle()
            val add = root.assertCategoryManagementFlow().assertAddCategory()
            add.onNameChanged("Groceries")
            add.onIconSelected("local_grocery_store")
            add.onSaveClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("the new category appears in the browse destination") {
            root.assertCategoryManagementFlow().assertBrowse().state.value.categories.map {
              it.name
            } shouldContain "Groceries"
            root
                .assertCategoryManagementFlow()
                .assertBrowse()
                .state
                .value
                .categories
                .single()
                .icon shouldBe "local_grocery_store"
          }

          And("the user returns with visible Back") {
            beforeEach {
              root.assertCategoryManagementFlow().assertBrowse().onBackClicked()
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("Settings retains its preferences and updated category count") {
              val settings = root.assertMainComponent().assertSettingsComponent()
              settings.state.value.categoriesCount shouldBe 1
              settings.state.value.selectedCalendarType shouldBe CalendarType.PERSIAN
              settings.state.value.selectedCurrency shouldBe CurrencyType.RIALS
              settings.state.value.endOfDayReminderEnabled shouldBe false
            }
          }
        }

        And("the user cancels category creation") {
          beforeEach {
            root.assertCategoryManagementFlow().assertBrowse().onAddClicked()
            testCoroutineScheduler.advanceUntilIdle()
            val add = root.assertCategoryManagementFlow().assertAddCategory()
            add.onNameChanged("Groceries")
            add.onSaveClicked()
            testCoroutineScheduler.advanceUntilIdle()
            root.assertCategoryManagementFlow().assertBrowse().onBackClicked()
            testCoroutineScheduler.advanceUntilIdle()
            root.assertMainComponent().assertSettingsComponent().onCategoriesClicked()
            testCoroutineScheduler.advanceUntilIdle()
            root.assertCategoryManagementFlow().assertBrowse().onAddClicked()
            testCoroutineScheduler.advanceUntilIdle()
            root.assertCategoryManagementFlow().assertAddCategory().onBackClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("the saved category remains and the cancelled category is absent") {
            root.assertCategoryManagementFlow().assertBrowse().state.value.categories.map {
              it.name
            } shouldBe listOf("Groceries")
          }

          And("platform Back is invoked from browsing") {
            beforeEach {
              root.onBack()
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("the same Settings state is active") {
              val settings = root.assertMainComponent().assertSettingsComponent()
              settings.state.value.categoriesCount shouldBe 1
              settings.state.value.selectedCalendarType shouldBe CalendarType.PERSIAN
              settings.state.value.selectedCurrency shouldBe CurrencyType.RIALS
              settings.state.value.endOfDayReminderEnabled shouldBe false
            }
          }
        }
      }
    }
  }
}
