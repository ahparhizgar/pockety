package com.base.feature.expense.analytics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import com.arkivanov.decompose.value.MutableValue
import com.base.RootScreen
import com.base.calendar.CalendarDate
import com.base.calendar.CalendarType
import com.base.feature.analytics.AnalyticsComponent
import com.base.feature.analytics.AnalyticsRelativeDate
import com.base.feature.analytics.AnalyticsScreen
import com.base.feature.analytics.FakeAnalyticsComponent
import com.base.feature.analytics.FakeAnalyticsDateGroupUiModelFactory
import com.base.feature.analytics.FakeAnalyticsExpenseUiModelFactory
import com.base.feature.analytics.FakeAnalyticsStateFactory
import com.base.feature.root.FakeRootComponent
import com.base.feature.settings.AppLanguage
import com.base.feature.settings.FakeSettingsComponent
import com.base.feature.settings.SettingsComponent
import com.base.feature.settings.SettingsScreen
import com.base.feature.settings.SettingsState
import com.base.ui.components.PocketyTab
import com.base.util.waitForExistence
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.Job

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
    onNode(hasText("دیروز", substring = true)).assertExists()
    onNode(hasText("22 اکتبر 2026")).assertExists()
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
    onNode(hasText("Today", substring = true)).assertExists()
    onNode(hasText("-50,000 T", substring = true)).assertExists()
  }

  @Test
  fun settingsLanguageSwitchRefreshesAnalyticsWithoutResettingContent() = runComposeUiTest {
    val language = MutableValue(AppLanguage.ENGLISH)
    val root = FakeRootComponent(initialLanguage = AppLanguage.ENGLISH)
    val rootLanguage = root.appLanguage as MutableValue<AppLanguage>
    val expense =
        FakeAnalyticsExpenseUiModelFactory.create(
            id = 33,
            amount = 12_500,
            description = "Personal expense",
            categoryName = "Custom category",
        )
    val analytics =
        FakeAnalyticsComponent(
            initialState =
                FakeAnalyticsStateFactory.create(
                    selectedMonth = 10,
                    dateGroups =
                        listOf(
                            FakeAnalyticsDateGroupUiModelFactory.create(
                                date = CalendarDate(2026, 10, 24, CalendarType.GREGORIAN),
                                expenses = listOf(expense),
                            )
                        ),
                )
        )
    val settings =
        LanguageUpdatingSettingsComponent(
            component = FakeSettingsComponent(initialState = SettingsState()),
            onLanguageSelected = { selected ->
              language.value = selected
              rootLanguage.value = selected
            },
        )
    setContent {
      RootScreen(root = root) {
        AnalyticsSettingsLanguageFlowHost(
            analyticsComponent = analytics,
            settingsComponent = settings,
        )
      }
    }

    onNode(hasText("Analytics")).waitForExistence().assertExists()
    onNode(hasText("October 2026")).assertExists()
    onNode(hasText("Personal expense")).assertExists()

    focusAndEnter(onNode(hasTestTag("analytics-settings-test-navigation")))
    selectLanguageFromKeyboard(AppLanguage.PERSIAN)
    focusAndEnter(onNode(hasTestTag("analytics-settings-test-navigation")))
    onNode(hasText("تحلیل")).waitForExistence().assertExists()
    onNode(hasText("اکتبر 2026")).assertExists()
    onNode(hasText("امروز", substring = true)).assertExists()
    onNode(hasText("Personal expense")).assertExists()

    focusAndEnter(onNode(hasTestTag("analytics-settings-test-navigation")))
    selectLanguageFromKeyboard(AppLanguage.ENGLISH)
    focusAndEnter(onNode(hasTestTag("analytics-settings-test-navigation")))
    onNode(hasText("Analytics")).waitForExistence().assertExists()
    onNode(hasText("October 2026")).assertExists()
    onNode(hasText("Today", substring = true)).assertExists()
    onNode(hasText("Personal expense")).assertExists()
    assertAnalyticsContentRetained(language.value, analytics)
  }

  private fun assertAnalyticsContentRetained(
      language: AppLanguage,
      analytics: AnalyticsComponent,
  ) {
    assertEquals(AppLanguage.ENGLISH, language)
    assertEquals(CalendarType.GREGORIAN, analytics.state.value.calendarType)
    assertEquals(10, analytics.state.value.selectedMonth)
    assertEquals(2026, analytics.state.value.selectedYear)
    assertEquals(50_000, analytics.state.value.monthlyOutflow)
    assertEquals("Personal expense", analytics.state.value.allExpenses.single().description)
    assertEquals("Custom category", analytics.state.value.allExpenses.single().categoryName)
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

@Composable
private fun AnalyticsSettingsLanguageFlowHost(
    analyticsComponent: AnalyticsComponent,
    settingsComponent: SettingsComponent,
) {
  var selectedTab by remember { mutableStateOf(PocketyTab.Analytics) }

  Box(modifier = Modifier.fillMaxSize()) {
    when (selectedTab) {
      PocketyTab.Analytics -> AnalyticsScreen(component = analyticsComponent)
      PocketyTab.Settings -> SettingsScreen(component = settingsComponent)
      else -> Unit
    }
    TextButton(
        onClick = {
          selectedTab =
              if (selectedTab == PocketyTab.Analytics) PocketyTab.Settings else PocketyTab.Analytics
        },
        modifier = Modifier.align(Alignment.TopEnd).testTag("analytics-settings-test-navigation"),
    ) {
      Text(if (selectedTab == PocketyTab.Analytics) "Settings" else "Analytics")
    }
  }
}

private class LanguageUpdatingSettingsComponent(
    private val component: FakeSettingsComponent,
    private val onLanguageSelected: (AppLanguage) -> Unit,
) : SettingsComponent by component {
  override fun onLanguageSelected(language: AppLanguage): Job {
    onLanguageSelected.invoke(language)
    return component.onLanguageSelected(language)
  }
}
