package com.base.feature.category.management

import com.base.calendar.CalendarType
import com.base.component.TestCContext
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
import io.kotest.matchers.shouldBe

class CategoryManagementFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("the app is open") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

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
            add.onSaveClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("the new category appears in the browse destination") {
            root.assertCategoryManagementFlow().assertBrowse().state.value.categories.map {
              it.name
            } shouldContain "Groceries"
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
