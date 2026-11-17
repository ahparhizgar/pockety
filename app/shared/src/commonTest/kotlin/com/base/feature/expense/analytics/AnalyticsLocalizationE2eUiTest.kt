package com.base.feature.expense.analytics

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.RootScreen
import com.base.calendar.CalendarDate
import com.base.calendar.CalendarType
import com.base.feature.analytics.AnalyticsRelativeDate
import com.base.feature.analytics.AnalyticsScreen
import com.base.feature.analytics.FakeAnalyticsComponent
import com.base.feature.analytics.FakeAnalyticsDateGroupUiModelFactory
import com.base.feature.analytics.FakeAnalyticsExpenseUiModelFactory
import com.base.feature.analytics.FakeAnalyticsStateFactory
import com.base.feature.root.FakeRootComponent
import com.base.feature.settings.AppLanguage
import com.base.util.waitForExistence
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AnalyticsLocalizationE2eUiTest {
  @Test
  fun emptyAnalyticsUsesPersianLabelsAndTomanUnit() = runComposeUiTest {
    val component =
        FakeAnalyticsComponent(
            initialState =
                FakeAnalyticsStateFactory.create(
                    monthlyOutflow = 0,
                    dateGroups = emptyList(),
                )
        )

    setContent {
      RootScreen(root = FakeRootComponent(initialLanguage = AppLanguage.PERSIAN)) {
        AnalyticsScreen(component = component)
      }
    }

    onNode(hasText("تحلیل")).waitForExistence().assertExists()
    onNode(hasText("برون‌رفت ماهانه")).assertExists()
    onNode(hasText("0 تومان", substring = true)).assertExists()
    onNode(hasText("در این ماه خرجی ثبت نشده است")).assertExists()
    onNode(hasContentDescription("ماه قبل")).assertExists()
    onNode(hasContentDescription("ماه بعد")).assertExists()
  }

  @Test
  fun populatedAnalyticsLocalizesDateGroupsAndKeepsKeyboardExpenseActivation() = runComposeUiTest {
    var openedExpenseId: Long? = null
    val expense = FakeAnalyticsExpenseUiModelFactory.create(id = 42, amount = 12_500)
    val dateGroups =
        listOf(
            FakeAnalyticsDateGroupUiModelFactory.create(
                date = CalendarDate(2026, 10, 24, CalendarType.GREGORIAN),
                relativeDate = AnalyticsRelativeDate.TODAY,
                expenses = listOf(expense),
            ),
            FakeAnalyticsDateGroupUiModelFactory.create(
                date = CalendarDate(2026, 10, 23, CalendarType.GREGORIAN),
                relativeDate = AnalyticsRelativeDate.YESTERDAY,
            ),
            FakeAnalyticsDateGroupUiModelFactory.create(
                date = CalendarDate(2026, 10, 22, CalendarType.GREGORIAN),
                relativeDate = AnalyticsRelativeDate.OTHER,
            ),
        )
    val component =
        FakeAnalyticsComponent(
            initialState = FakeAnalyticsStateFactory.create(dateGroups = dateGroups),
            onExpenseClick = { openedExpenseId = it },
        )

    setContent {
      RootScreen(root = FakeRootComponent(initialLanguage = AppLanguage.PERSIAN)) {
        AnalyticsScreen(component = component)
      }
    }

    onNode(hasText("اکتبر 2026")).waitForExistence().assertExists()
    onNode(hasText("امروز", substring = true)).assertExists()
    onNode(hasText("دیروز", substring = true)).assertExists()
    onNode(hasText("اکتبر", substring = true)).assertExists()
    onNode(hasText("تومان", substring = true)).assertExists()
    onNode(hasTestTag("expense-item-42")).performSemanticsAction(SemanticsActions.RequestFocus) {
      it()
    }
    onNode(hasTestTag("expense-item-42")).performKeyInput { pressKey(Key.Enter) }
    openedExpenseId shouldBe 42
  }

  @Test
  fun persianCalendarMonthsFollowSelectedLanguage() = runComposeUiTest {
    val group =
        FakeAnalyticsDateGroupUiModelFactory.create(
            date = CalendarDate(1405, 7, 24, CalendarType.PERSIAN),
            relativeDate = AnalyticsRelativeDate.TODAY,
        )

    setContent {
      RootScreen(root = FakeRootComponent(initialLanguage = AppLanguage.PERSIAN)) {
        AnalyticsScreen(
            component =
                FakeAnalyticsComponent(
                    initialState =
                        FakeAnalyticsStateFactory.create(
                            calendarType = CalendarType.PERSIAN,
                            selectedYear = 1405,
                            selectedMonth = 7,
                            dateGroups = listOf(group),
                        )
                )
        )
      }
    }

    onNode(hasText("مهر 1405")).waitForExistence().assertExists()
    onNode(hasText("امروز، 24 مهر")).assertExists()
  }

  @Test
  fun englishLanguageUsesTransliteratedPersianCalendarMonths() = runComposeUiTest {
    val group =
        FakeAnalyticsDateGroupUiModelFactory.create(
            date = CalendarDate(1405, 7, 24, CalendarType.PERSIAN),
            relativeDate = AnalyticsRelativeDate.TODAY,
        )

    setContent {
      RootScreen(root = FakeRootComponent(initialLanguage = AppLanguage.ENGLISH)) {
        AnalyticsScreen(
            component =
                FakeAnalyticsComponent(
                    initialState =
                        FakeAnalyticsStateFactory.create(
                            calendarType = CalendarType.PERSIAN,
                            selectedYear = 1405,
                            selectedMonth = 7,
                            dateGroups = listOf(group),
                        )
                )
        )
      }
    }

    onNode(hasText("Mehr 1405")).waitForExistence().assertExists()
    onNode(hasText("Today, 24 Mehr")).assertExists()
  }

  @Test
  fun populatedAnalyticsUsesEnglishDateGroupsAndTUnits() = runComposeUiTest {
    val group =
        FakeAnalyticsDateGroupUiModelFactory.create(
            date = CalendarDate(2026, 10, 24, CalendarType.GREGORIAN),
            relativeDate = AnalyticsRelativeDate.TODAY,
            expenses = listOf(FakeAnalyticsExpenseUiModelFactory.create(id = 7)),
        )

    setContent {
      RootScreen(root = FakeRootComponent(initialLanguage = AppLanguage.ENGLISH)) {
        AnalyticsScreen(
            component =
                FakeAnalyticsComponent(
                    initialState = FakeAnalyticsStateFactory.create(dateGroups = listOf(group))
                )
        )
      }
    }

    onNode(hasText("Analytics")).waitForExistence().assertExists()
    onNode(hasText("October 2026")).assertExists()
    onNode(hasText("Today", substring = true)).assertExists()
    onNode(hasTestTag("expense-amount").and(hasText("T", substring = true))).assertExists()
  }
}
