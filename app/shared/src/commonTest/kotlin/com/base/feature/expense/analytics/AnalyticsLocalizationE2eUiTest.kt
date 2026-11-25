package com.base.feature.expense.analytics

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.RootScreen
import com.base.calendar.CalendarDate
import com.base.calendar.CalendarType
import com.base.component.TestCContext
import com.base.db.ExpenseRepository
import com.base.feature.analytics.AnalyticsRelativeDate
import com.base.feature.analytics.AnalyticsScreen
import com.base.feature.analytics.FakeAnalyticsComponent
import com.base.feature.analytics.FakeAnalyticsDateGroupUiModelFactory
import com.base.feature.analytics.FakeAnalyticsExpenseUiModelFactory
import com.base.feature.analytics.FakeAnalyticsStateFactory
import com.base.feature.root.FakeRootComponent
import com.base.feature.settings.AppLanguage
import com.base.feature.settings.FakeUserSettingsRepository
import com.base.feature.settings.UserSettingsRepository
import com.base.util.assertAnalyticsComponent
import com.base.util.assertMainComponent
import com.base.util.assertSettingsComponent
import com.base.util.injectUiRoot
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalTestApi::class)
class AnalyticsLocalizationE2eUiTest {
  @Test
  fun emptyAnalyticsUsesPersianLabelsAndTomanUnit() = runComposeUiTest {
    val component =
        FakeAnalyticsComponent(
            initialState =
                FakeAnalyticsStateFactory.create(
                    monthlyOutflow = 0,
                    previousMonthOutflow = 0,
                    categorySpending = emptyList(),
                    dateGroups = emptyList(),
                )
        )

    setContent {
      RootScreen(root = FakeRootComponent(initialLanguage = AppLanguage.PERSIAN)) {
        AnalyticsScreen(component = component)
      }
    }

    onNode(hasText("تحلیل")).waitForExistence().assertExists()
    onNode(hasText("جریان نقدی ماهانه")).assertExists()
    onNode(hasText("0 تومان", substring = true)).assertExists()
    onNode(hasText("این ماه هنوز خرجی ثبت نکردی")).assertExists()
    onNode(hasContentDescription("ماه قبل")).assertExists()
    onNode(hasContentDescription("ماه بعد")).assertExists()
  }

  @Test
  fun populatedAnalyticsLocalizesDateGroupsAndExpenseActivation() = runComposeUiTest {
    var wasExpenseClicked = false
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
                expenses = emptyList(),
            ),
            FakeAnalyticsDateGroupUiModelFactory.create(
                date = CalendarDate(2026, 10, 22, CalendarType.GREGORIAN),
                relativeDate = AnalyticsRelativeDate.OTHER,
                expenses = emptyList(),
            ),
        )
    val component =
        FakeAnalyticsComponent(
            initialState = FakeAnalyticsStateFactory.create(dateGroups = dateGroups),
            onExpenseClick = { wasExpenseClicked = true },
        )

    setContent {
      RootScreen(root = FakeRootComponent(initialLanguage = AppLanguage.PERSIAN)) {
        AnalyticsScreen(component = component)
      }
    }

    onNode(hasText("اکتبر 2026")).waitForExistence().assertExists()
    onNode(hasText("امروز", substring = true)).assertExists()
    onNode(hasTestTag("analytics-list")).performScrollToNode(hasText("دیروز", substring = true))
    onNode(hasText("دیروز", substring = true)).assertExists()
    onNode(hasTestTag("analytics-list")).performScrollToNode(hasText("22 اکتبر 2026"))
    onNode(hasText("22 اکتبر 2026")).assertExists()
    onNode(hasTestTag("analytics-list")).performScrollToNode(hasTestTag("expense-item-42"))
    onNode(hasTestTag("expense-amount"), useUnmergedTree = true)
        .assertTextContains("تومان", substring = true)
    onNode(hasTestTag("expense-item-42")).performSemanticsAction(SemanticsActions.RequestFocus) {
      it()
    }
    onNode(hasTestTag("expense-item-42")).performKeyInput { pressKey(Key.Enter) }
    assertTrue(wasExpenseClicked)
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
                                selectedMonth = 7,
                                dateGroups = listOf(group),
                            )
                            .copy(selectedYear = 1405)
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
                                selectedMonth = 7,
                                dateGroups = listOf(group),
                            )
                            .copy(selectedYear = 1405)
                )
        )
      }
    }

    onNode(hasText("Mehr 1405")).waitForExistence().assertExists()
    onNode(hasTestTag("analytics-list")).performScrollToNode(hasText("Today, 24 Mehr"))
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
                    initialState =
                        FakeAnalyticsStateFactory.create(
                            selectedMonth = 10,
                            dateGroups = listOf(group),
                        )
                )
        )
      }
    }

    onNode(hasText("Analytics")).waitForExistence().assertExists()
    onNode(hasText("October 2026")).assertExists()
    onNode(hasTestTag("analytics-list")).performScrollToNode(hasText("Today", substring = true))
    onNode(hasText("Today", substring = true)).assertExists()
    onNode(hasText("-5,000 T", substring = true)).assertExists()
  }

  @Test
  fun productionSettingsLanguageSwitchRefreshesAnalyticsWithoutResettingContent() =
      runComposeUiTest {
        val context = TestCContext()
        val koin = uiKoin()
        val settingsRepository = koin.get<UserSettingsRepository>() as FakeUserSettingsRepository
        val expenseRepository = koin.get<ExpenseRepository>()
        runBlocking {
          settingsRepository.setCalendarType(CalendarType.PERSIAN)
          expenseRepository.addExpense(amount = 12_500, description = "Personal expense")
        }

        val root = koin.injectUiRoot(context)
        setContent { RootScreen(root) }
        focusAndEnter(onNode(hasTestTag("tab-analytics")).waitForExistence())
        onNode(hasText("Personal expense")).waitForExistence().assertExists()
        val initialAnalytics = root.assertMainComponent().assertAnalyticsComponent()
        val initialState = initialAnalytics.state.value
        assertEquals(CalendarType.PERSIAN, initialState.calendarType)

        focusAndEnter(onNode(hasTestTag("tab-settings")))
        selectLanguageFromKeyboard(AppLanguage.PERSIAN)
        waitUntil(timeoutMillis = 5000) { root.appLanguage.value == AppLanguage.PERSIAN }
        onNode(hasText("زبان برنامه")).waitForExistence().assertExists()
        assertEquals(
            AppLanguage.PERSIAN,
            root.assertMainComponent().assertSettingsComponent().state.value.selectedLanguage,
        )

        focusAndEnter(onNode(hasTestTag("tab-analytics")))
        onNode(hasTestTag("analytics-screen")).waitForExistence().assertExists()
        onAllNodes(hasText("تحلیل")).assertCountEquals(2)
        onNode(hasText("جریان نقدی ماهانه")).assertExists()
        onNode(hasTestTag("analytics-list")).performScrollToNode(hasText("امروز", substring = true))
        onNode(hasText("امروز", substring = true)).assertExists()
        onNode(hasTestTag("monthly-outflow")).assertTextContains("تومان", substring = true)
        onNode(hasText("Personal expense")).assertExists()

        focusAndEnter(onNode(hasTestTag("tab-settings")))
        selectLanguageFromKeyboard(AppLanguage.ENGLISH)
        waitUntil(timeoutMillis = 5000) { root.appLanguage.value == AppLanguage.ENGLISH }
        onNode(hasText("App language")).waitForExistence().assertExists()
        focusAndEnter(onNode(hasTestTag("tab-analytics")))
        onNode(hasTestTag("analytics-screen")).waitForExistence().assertExists()
        onNode(hasText("Monthly Cash Flow")).assertExists()
        onNode(hasTestTag("analytics-list")).performScrollToNode(hasText("Today", substring = true))
        onNode(hasText("Today", substring = true)).assertExists()
        onNode(hasText("Personal expense")).assertExists()

        val finalState = root.assertMainComponent().assertAnalyticsComponent().state.value
        assertEquals(initialState.calendarType, finalState.calendarType)
        assertEquals(initialState.selectedMonth, finalState.selectedMonth)
        assertEquals(initialState.selectedYear, finalState.selectedYear)
        assertEquals(initialState.monthlyOutflow, finalState.monthlyOutflow)
        assertEquals(initialState.allExpenses, finalState.allExpenses)
      }

  private fun ComposeUiTest.selectLanguageFromKeyboard(language: AppLanguage) {
    onNode(hasTestTag("settings-language-picker-pill")).performScrollTo()
    focusAndEnter(onNode(hasTestTag("settings-language-picker-pill")))
    val languageTag =
        when (language) {
          AppLanguage.ENGLISH -> "language-option-english"
          AppLanguage.PERSIAN -> "language-option-persian"
        }
    focusAndEnter(onNode(hasTestTag(languageTag)))
  }

  private fun ComposeUiTest.focusAndEnter(node: SemanticsNodeInteraction) {
    node.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
    node.performKeyInput { pressKey(Key.Enter) }
  }
}
