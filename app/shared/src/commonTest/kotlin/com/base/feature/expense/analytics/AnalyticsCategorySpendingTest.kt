package com.base.feature.expense.analytics

import com.base.component.TestCContext
import com.base.db.FakeCategoryFactory
import com.base.db.FakeCategoryRepository
import com.base.db.FakeExpenseFactory
import com.base.db.FakeExpenseRepository
import com.base.feature.analytics.DefaultAnalyticsComponent
import com.base.feature.settings.FakeUserSettingsRepository
import com.base.testValue
import com.base.util.MainDispatcherExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

private fun previousMonthInstant(): Instant {
  val timeZone = TimeZone.currentSystemDefault()
  val today = Clock.System.now().toLocalDateTime(timeZone).date
  return LocalDate(today.year, today.month, 15)
      .minus(1, DateTimeUnit.MONTH)
      .atStartOfDayIn(timeZone)
}

class AnalyticsCategorySpendingTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("current-month expenses across categories plus a previous-month expense") {
      val categories =
          listOf(
              FakeCategoryFactory.create(id = 1, name = "Food"),
              FakeCategoryFactory.create(id = 2, name = "Transport"),
          )
      val component by testValue {
        DefaultAnalyticsComponent(
            cContext = TestCContext(),
            expenseRepository =
                FakeExpenseRepository(
                    initialExpenses =
                        listOf(
                            FakeExpenseFactory.create(id = 1, amount = 500, categoryId = 1),
                            FakeExpenseFactory.create(id = 2, amount = 300, categoryId = 2),
                            FakeExpenseFactory.create(id = 3, amount = 200, categoryId = null),
                            FakeExpenseFactory.create(
                                id = 4,
                                amount = 700,
                                categoryId = 1,
                                createdAt = previousMonthInstant(),
                            ),
                        ),
                ),
            categoryRepository = FakeCategoryRepository(initialCategories = categories),
            userSettingsRepository = FakeUserSettingsRepository(),
            onExpenseClick = {},
        )
      }

      When("the component state settles") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("previousMonthOutflow sums only the previous month") {
          component.state.value.previousMonthOutflow shouldBe 700L
        }

        Then("category spending is aggregated per category, ordered by amount") {
          val spending = component.state.value.categorySpending
          spending shouldHaveSize 3
          spending[0].categoryName shouldBe "Food"
          spending[0].amount shouldBe 500L
          spending[1].categoryName shouldBe "Transport"
          spending[1].amount shouldBe 300L
        }

        Then("percentages are whole shares of the monthly outflow") {
          val spending = component.state.value.categorySpending
          spending[0].percentage shouldBe 50
          spending[1].percentage shouldBe 30
          spending[2].percentage shouldBe 20
        }

        Then("uncategorized expenses form their own group") {
          val uncategorized = component.state.value.categorySpending[2]
          uncategorized.categoryId shouldBe null
          uncategorized.categoryName shouldBe null
          uncategorized.amount shouldBe 200L
        }
      }
    }

    Given("no expenses at all") {
      val component by testValue {
        DefaultAnalyticsComponent(
            cContext = TestCContext(),
            expenseRepository = FakeExpenseRepository(),
            categoryRepository = FakeCategoryRepository(),
            userSettingsRepository = FakeUserSettingsRepository(),
            onExpenseClick = {},
        )
      }

      When("the component state settles") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("category spending is empty and previous outflow is zero") {
          component.state.value.categorySpending shouldHaveSize 0
          component.state.value.previousMonthOutflow shouldBe 0L
        }
      }
    }
  }
}
