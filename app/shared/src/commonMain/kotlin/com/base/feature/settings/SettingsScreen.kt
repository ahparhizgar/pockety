package com.base.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.calendar.CalendarType
import com.base.ui.ScreenPreviews
import com.base.ui.colorSchemeExtended
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.settings_categories_count_format
import pockety.app.shared.generated.resources.settings_categories_subtitle
import pockety.app.shared.generated.resources.settings_categories_title
import pockety.app.shared.generated.resources.settings_language_english
import pockety.app.shared.generated.resources.settings_language_persian
import pockety.app.shared.generated.resources.settings_privacy_policy_subtitle
import pockety.app.shared.generated.resources.settings_privacy_policy_title
import pockety.app.shared.generated.resources.settings_reminder_subtitle
import pockety.app.shared.generated.resources.settings_reminder_title
import pockety.app.shared.generated.resources.settings_section_notifications
import pockety.app.shared.generated.resources.settings_section_organize
import pockety.app.shared.generated.resources.settings_section_preferences
import pockety.app.shared.generated.resources.settings_section_privacy
import pockety.app.shared.generated.resources.settings_title
import pockety.app.shared.generated.resources.settings_version_format

@Composable
fun SettingsScreen(
    component: SettingsComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.fillMaxSize().testTag("settings-screen"),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.settings_title),
        )
      },
  ) { paddingValues ->
    Surface(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        color = MaterialTheme.colorScheme.surface,
    ) {
      Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.TopCenter,
      ) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
          SettingsOrganizeSection(
              state = state,
              component = component,
          )
          SettingsPreferencesSection(
              state = state,
              component = component,
          )
          SettingsNotificationsSection(
              state = state,
              component = component,
          )
          SettingsPrivacySection(
              component = component,
          )
          SettingsVersionBadge(
              version = stringResource(Res.string.settings_version_format, state.appVersion),
          )
        }
      }
    }
  }
}

@Composable
private fun SettingsOrganizeSection(
    state: SettingsState,
    component: SettingsComponent,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    SettingsSectionHeader(title = stringResource(Res.string.settings_section_organize))
    SettingsCardContainer(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      SettingsNavigationRow(
          data =
              NavigationRowData(
                  title = stringResource(Res.string.settings_categories_title),
                  subtitle = stringResource(Res.string.settings_categories_subtitle),
                  iconData =
                      SettingsRowIconData(
                          icon = Icons.Default.Category,
                          containerColor = MaterialTheme.colorScheme.secondaryContainer,
                          contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                      ),
                  countBadgeText =
                      stringResource(
                          Res.string.settings_categories_count_format,
                          state.categoriesCount,
                      ),
              ),
          onClick = component::onCategoriesClicked,
          modifier = Modifier.testTag("settings-navigation-categories"),
      )
    }
  }
}

@Composable
private fun SettingsPreferencesSection(
    state: SettingsState,
    component: SettingsComponent,
    modifier: Modifier = Modifier,
) {
  val languageLabel =
      when (state.selectedLanguage) {
        AppLanguage.ENGLISH -> stringResource(Res.string.settings_language_english)
        AppLanguage.PERSIAN -> stringResource(Res.string.settings_language_persian)
      }

  Column(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    SettingsSectionHeader(title = stringResource(Res.string.settings_section_preferences))
    SettingsCardContainer(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      SettingsLanguageRow(
          onClick = component::onLanguageClicked,
          selectedLanguage = languageLabel,
          modifier = Modifier.testTag("settings-row-language"),
      )
      DisplayCurrencyCard(
          selectedCurrency = state.selectedCurrency,
          onCurrencySelect = component::onCurrencySelected,
      )
      DisplayCalendarSystemCard(
          selectedCalendarType = state.selectedCalendarType,
          onCalendarTypeSelect = component::onCalendarTypeSelected,
      )
    }
  }

  if (state.isLanguageDialogVisible) {
    LanguageSelectionDialog(
        selectedLanguage = state.selectedLanguage,
        onLanguageSelect = component::onLanguageSelected,
        onDismissRequest = component::onDismissLanguageDialog,
    )
  }
}

@Composable
private fun SettingsNotificationsSection(
    state: SettingsState,
    component: SettingsComponent,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    SettingsSectionHeader(title = stringResource(Res.string.settings_section_notifications))
    SettingsCardContainer(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      SwitchRow(
          data =
              SwitchRowData(
                  title = stringResource(Res.string.settings_reminder_title),
                  subtitle = stringResource(Res.string.settings_reminder_subtitle),
                  icon = Icons.Default.Alarm,
                  checked = state.endOfDayReminderEnabled,
              ),
          onCheckedChange = component::onReminderToggled,
          modifier = Modifier.testTag("settings-switch-reminder"),
      )
    }
  }
}

@Composable
private fun SettingsPrivacySection(
    component: SettingsComponent,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.testTag("settings-section-privacy"),
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    SettingsSectionHeader(title = stringResource(Res.string.settings_section_privacy))
    SettingsCardContainer(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      SettingsNavigationRow(
          data =
              NavigationRowData(
                  title = stringResource(Res.string.settings_privacy_policy_title),
                  subtitle = stringResource(Res.string.settings_privacy_policy_subtitle),
                  iconData =
                      SettingsRowIconData(
                          icon = Icons.Default.Lock,
                          containerColor = MaterialTheme.colorScheme.primaryContainer,
                          contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                      ),
              ),
          onClick = component::onPrivacyPolicyClicked,
          modifier = Modifier.testTag("settings-navigation-privacy-policy"),
      )
    }
  }
}

@ScreenPreviews
@Composable
private fun SettingsScreenPreview() {
  SettingsScreen(
      component =
          FakeSettingsComponent(
              initialState =
                  FakeSettingsStateFactory.create(
                      selectedCalendarType = CalendarType.GREGORIAN,
                      selectedCurrency = CurrencyType.TOMANS,
                      endOfDayReminderEnabled = true,
                      categoriesCount = 18,
                      appVersion = "1.2.0",
                  ),
          ),
  )
}

@ScreenPreviews
@Composable
private fun SettingsScreenPersianPreview() {
  SettingsScreen(
      component =
          FakeSettingsComponent(
              initialState =
                  FakeSettingsStateFactory.create(
                      selectedCalendarType = CalendarType.PERSIAN,
                      selectedCurrency = CurrencyType.RIALS,
                      endOfDayReminderEnabled = false,
                      categoriesCount = 5,
                      appVersion = "1.2.0",
                  ),
          ),
  )
}
