package com.base.feature.home

import com.base.calendar.CalendarType
import com.base.calendar.PersianCalendar
import com.base.component.TestCContext
import com.base.db.FakeCandidateExpenseRepository
import com.base.db.FakeCategoryFactory
import com.base.db.FakeCategoryRepository
import com.base.db.FakeExpenseFactory
import com.base.db.FakeExpenseRepository
import com.base.db.FakePocketRepository
import com.base.db.PocketEntity
import com.base.db.PocketTopUpEntity
import com.base.feature.settings.FakeUserSettingsRepository
import com.base.sms.InitialSmsImport
import com.base.sms.SmsImportHistory
import com.base.sms.SmsInboxReader
import com.base.sms.SmsInboxResult
import com.base.testValue
import com.base.util.MainDispatcherExtension
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
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

private fun stubInitialSmsImport() =
    InitialSmsImport(
        reader =
            object : SmsInboxReader {
              override suspend fun latestExpense(): SmsInboxResult = SmsInboxResult.Unavailable
            },
        history =
            object : SmsImportHistory {
              override suspend fun isComplete(): Boolean = true

              override suspend fun markComplete() {}
            },
        repository = FakeCandidateExpenseRepository(),
    )

class HomeComponentTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("expenses in the current and previous months") {
      val component by testValue {
        DefaultHomeComponent(
            cContext = TestCContext(),
            candidateExpenseRepository = FakeCandidateExpenseRepository(),
            expenseRepository =
                FakeExpenseRepository(
                    initialExpenses =
                        listOf(
                            FakeExpenseFactory.create(id = 1, amount = 100_000),
                            FakeExpenseFactory.create(id = 2, amount = 50_000),
                            FakeExpenseFactory.create(
                                id = 3,
                                amount = 300_000,
                                createdAt = previousMonthInstant(),
                            ),
                        ),
                ),
            categoryRepository = FakeCategoryRepository(),
            userSettingsRepository = FakeUserSettingsRepository(),
            pocketRepository = FakePocketRepository(),
            initialSmsImport = stubInitialSmsImport(),
            onAddExpense = {},
        )
      }

      When("the component state settles") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("monthly outflow covers only the current month") {
          component.state.value.monthlyOutflow shouldBe 150_000L
        }

        Then("the previous month total and comparison are computed") {
          val state = component.state.value
          state.previousMonthOutflow shouldBe 300_000L
          state.monthOverMonthPercent shouldBe 50
          state.isMonthOverMonthSaved shouldBe true
        }
      }
    }

    Given("current-month expenses across many categories") {
      val categories =
          (1L..5L).map { id ->
            FakeCategoryFactory.create(id = id, name = "Category $id", sortOrder = id.toInt())
          }
      val component by testValue {
        DefaultHomeComponent(
            cContext = TestCContext(),
            candidateExpenseRepository = FakeCandidateExpenseRepository(),
            expenseRepository =
                FakeExpenseRepository(
                    initialExpenses =
                        listOf(
                            FakeExpenseFactory.create(id = 1, amount = 500, categoryId = 1),
                            FakeExpenseFactory.create(id = 2, amount = 400, categoryId = 2),
                            FakeExpenseFactory.create(id = 3, amount = 300, categoryId = 3),
                            FakeExpenseFactory.create(id = 4, amount = 200, categoryId = 4),
                            FakeExpenseFactory.create(id = 5, amount = 100, categoryId = 5),
                            FakeExpenseFactory.create(id = 6, amount = 50, categoryId = null),
                        ),
                ),
            categoryRepository = FakeCategoryRepository(initialCategories = categories),
            userSettingsRepository = FakeUserSettingsRepository(),
            pocketRepository = FakePocketRepository(),
            initialSmsImport = stubInitialSmsImport(),
            onAddExpense = {},
        )
      }

      When("the component state settles") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("the breakdown holds the top 4 categories plus an Other row") {
          val rows = component.state.value.topCategories
          rows shouldHaveSize 5
          rows.map { it.amount } shouldBe listOf(500L, 400L, 300L, 200L, 150L)
          rows.last().kind shouldBe HomeCategoryKind.OTHER
          rows.first().name shouldBe "Category 1"
        }
      }
    }

    Given("only uncategorized expenses and no previous-month data") {
      val component by testValue {
        DefaultHomeComponent(
            cContext = TestCContext(),
            candidateExpenseRepository = FakeCandidateExpenseRepository(),
            expenseRepository =
                FakeExpenseRepository(
                    initialExpenses =
                        listOf(FakeExpenseFactory.create(id = 1, amount = 700, categoryId = null)),
                ),
            categoryRepository = FakeCategoryRepository(),
            userSettingsRepository = FakeUserSettingsRepository(),
            pocketRepository = FakePocketRepository(),
            initialSmsImport = stubInitialSmsImport(),
            onAddExpense = {},
        )
      }

      When("the component state settles") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("the single row is the uncategorized group") {
          val rows = component.state.value.topCategories
          rows shouldHaveSize 1
          rows.single().kind shouldBe HomeCategoryKind.UNCATEGORIZED
          rows.single().share shouldBe 1f
        }

        Then("the month-over-month percent is null without previous data") {
          component.state.value.monthOverMonthPercent.shouldBeNull()
        }
      }
    }

    Given("pockets with top-ups and spending") {
      val now = Clock.System.now()
      val component by testValue {
        DefaultHomeComponent(
            cContext = TestCContext(),
            candidateExpenseRepository = FakeCandidateExpenseRepository(),
            expenseRepository =
                FakeExpenseRepository(
                    initialExpenses =
                        listOf(FakeExpenseFactory.create(id = 1, amount = 30_000, pocketId = 7)),
                ),
            categoryRepository = FakeCategoryRepository(),
            userSettingsRepository = FakeUserSettingsRepository(),
            pocketRepository =
                FakePocketRepository(
                    initialPockets =
                        listOf(
                            PocketEntity(
                                id = 7,
                                name = "Cafe",
                                categoryTag = "Cafe",
                                icon = "coffee",
                                createdAt = now,
                            ),
                        ),
                    initialExpenses =
                        listOf(FakeExpenseFactory.create(id = 1, amount = 30_000, pocketId = 7)),
                    initialTopUps =
                        listOf(
                            PocketTopUpEntity(
                                id = 1,
                                pocketId = 7,
                                amount = 100_000,
                                createdAt = now,
                            )
                        ),
                ),
            initialSmsImport = stubInitialSmsImport(),
            onAddExpense = {},
        )
      }

      When("the component state settles") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("pockets are mirrored with their remaining balances") {
          val pockets = component.state.value.pockets
          pockets shouldHaveSize 1
          pockets.single().name shouldBe "Cafe"
          pockets.single().balance shouldBe 70_000L
        }
      }
    }

    Given("the Persian calendar setting") {
      val component by testValue {
        DefaultHomeComponent(
            cContext = TestCContext(),
            candidateExpenseRepository = FakeCandidateExpenseRepository(),
            expenseRepository =
                FakeExpenseRepository(
                    initialExpenses = listOf(FakeExpenseFactory.create(id = 1, amount = 42_000)),
                ),
            categoryRepository = FakeCategoryRepository(),
            userSettingsRepository =
                FakeUserSettingsRepository(initialCalendarType = CalendarType.PERSIAN),
            pocketRepository = FakePocketRepository(),
            initialSmsImport = stubInitialSmsImport(),
            onAddExpense = {},
        )
      }

      When("the component state settles") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("the current month follows the Persian calendar") {
          val state = component.state.value
          val persianNow =
              PersianCalendar.toCalendarDate(
                  Clock.System.now().toEpochMilliseconds(),
                  TimeZone.currentSystemDefault(),
              )
          state.calendarType shouldBe CalendarType.PERSIAN
          state.currentYear shouldBe persianNow.year
          state.currentMonth shouldBe persianNow.month
          state.monthlyOutflow shouldBe 42_000L
        }
      }
    }
  }
}
