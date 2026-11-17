package com.base.feature.expense.analytics

import com.base.calendar.CalendarType
import com.base.component.TestCContext
import com.base.db.ExpenseDao
import com.base.db.FakeExpenseFactory
import com.base.feature.analytics.AnalyticsComponent
import com.base.feature.analytics.AnalyticsComponentFactory
import com.base.feature.settings.UserSettingsRepository
import com.base.testValue
import com.base.util.FakeCallback1
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlin.time.Instant

class AnalyticsCalendarTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("AnalyticsComponent with user preference set to PERSIAN") {
      val koin by integrationKoin()
      val userSettingsRepository by testValue { koin.get<UserSettingsRepository>() }
      val expenseDao by testValue { koin.get<ExpenseDao>() }
      val onExpenseClickCallback by testValue { FakeCallback1<Long>() }

      // 1403-01-01 Persian date corresponds to 2024-03-20T00:00:00Z
      val nowruzInstant = Instant.parse("2024-03-20T10:00:00Z")

      val component: AnalyticsComponent by testValue {
        koin
            .get<AnalyticsComponentFactory>()
            .create(
                cContext = TestCContext(),
                onExpenseClick = onExpenseClickCallback,
            )
      }

      beforeEach {
        userSettingsRepository.setCalendarType(CalendarType.PERSIAN)
        expenseDao.insertExpense(
            FakeExpenseFactory.create(
                id = 10,
                amount = 250_000,
                description = "Eid Gift",
                createdAt = nowruzInstant,
            )
        )
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("component loads expenses") {
        Then("component state reflects Persian calendar navigation") {
          component.state.value.calendarType shouldBe CalendarType.PERSIAN
        }
      }

      When("user clicks previous month") {
        var initialMonth = 0
        var initialYear = 0

        beforeEach {
          initialMonth = component.state.value.selectedMonth
          initialYear = component.state.value.selectedYear
          component.onPreviousMonthClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("selected month navigates to the previous Persian solar month") {
          if (initialMonth == 1) {
            component.state.value.selectedMonth shouldBe 12
            component.state.value.selectedYear shouldBe initialYear - 1
          } else {
            component.state.value.selectedMonth shouldBe initialMonth - 1
          }
        }
      }
    }
  }
}
