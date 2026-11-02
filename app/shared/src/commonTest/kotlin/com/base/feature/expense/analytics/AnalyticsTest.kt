package com.base.feature.expense.analytics

import com.base.component.TestCContext
import com.base.db.ExpenseDao
import com.base.db.FakeExpenseFactory
import com.base.feature.analytics.AnalyticsComponent
import com.base.feature.analytics.AnalyticsComponentFactory
import com.base.testValue
import com.base.util.FakeCallback1
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class AnalyticsTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("AnalyticsComponent with expenses spanning multiple months") {
      val koin by integrationKoin()
      val expenseDao by testValue { koin.get<ExpenseDao>() }
      val onExpenseClickCallback by testValue { FakeCallback1<Long>() }

      val timeZone = TimeZone.currentSystemDefault()
      val now = Clock.System.now()
      val localNow = now.toLocalDateTime(timeZone)

      val prevMonthYear = if (localNow.month.ordinal + 1 == 1) localNow.year - 1 else localNow.year
      val prevMonthNumber = if (localNow.month.ordinal + 1 == 1) 12 else localNow.month.ordinal
      val prevMonthInstant =
          LocalDate(prevMonthYear, prevMonthNumber, 15).atTime(10, 0).toInstant(timeZone)

      var currentMonthExpense1Id: Long by testValue { 0 }
      var currentMonthExpense2Id: Long by testValue { 0 }
      var prevMonthExpenseId: Long by testValue { 0 }

      val component: AnalyticsComponent by testValue {
        koin
            .get<AnalyticsComponentFactory>()
            .create(
                cContext = TestCContext(),
                onExpenseClick = onExpenseClickCallback,
            )
      }

      beforeEach {
        currentMonthExpense1Id =
            expenseDao.insertExpense(
                FakeExpenseFactory.create(
                    id = 1,
                    amount = 50_000,
                    description = "Coffee",
                    createdAt = now,
                )
            )
        currentMonthExpense2Id =
            expenseDao.insertExpense(
                FakeExpenseFactory.create(
                    id = 2,
                    amount = 70_000,
                    description = "Lunch",
                    createdAt = now,
                )
            )
        prevMonthExpenseId =
            expenseDao.insertExpense(
                FakeExpenseFactory.create(
                    id = 3,
                    amount = 120_000,
                    description = "Groceries",
                    createdAt = prevMonthInstant,
                )
            )
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("initialized on the current calendar month") {
        Then("canNavigateNext is false") { component.state.value.canNavigateNext shouldBe false }

        Then("only expenses recorded in the current month are displayed") {
          val state = component.state.value
          val expenses = state.allExpenses
          expenses shouldHaveSize 2
          expenses.map { it.id }.toSet() shouldBe
              setOf(currentMonthExpense1Id, currentMonthExpense2Id)
        }

        Then("monthlyOutflow equals the sum of expenses in the current month") {
          component.state.value.monthlyOutflow shouldBe 120_000
        }

        Then(
            "expenses are grouped by date in reverse-chronological order with date group headers and daily sums"
        ) {
          val state = component.state.value
          state.dateGroups shouldHaveSize 1
          val group = state.dateGroups[0]
          group.totalAmount shouldBe 120_000
          group.expenses shouldHaveSize 2
          group.headerTitle.startsWith("Today") shouldBe true
        }
      }

      When("user triggers previous month navigation") {
        beforeEach {
          component.onPreviousMonthClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("selected month decrements to the preceding month") {
          component.state.value.selectedMonth shouldBe prevMonthNumber
          component.state.value.selectedYear shouldBe prevMonthYear
        }

        Then("canNavigateNext becomes true") { component.state.value.canNavigateNext shouldBe true }

        Then("monthlyOutflow updates to the sum of expenses for that preceding month") {
          component.state.value.monthlyOutflow shouldBe 120_000
        }

        Then("the transaction list displays only expenses for that preceding month") {
          val expenses = component.state.value.allExpenses
          expenses shouldHaveSize 1
          expenses[0].id shouldBe prevMonthExpenseId
          expenses[0].amount shouldBe 120_000
        }
      }

      When("user clicks an expense item") {
        beforeEach {
          component.onExpenseClicked(currentMonthExpense1Id)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("onExpenseClick callback is invoked with the clicked expense ID") {
          onExpenseClickCallback.shouldBeCalled()
          onExpenseClickCallback.lastValue shouldBe currentMonthExpense1Id
        }
      }
    }
  }
}
