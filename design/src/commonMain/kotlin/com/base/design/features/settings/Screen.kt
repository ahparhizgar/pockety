package com.base.design.features.settings

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.design.globalcomponents.PocketyBottomBar
import com.base.design.globalcomponents.PocketyTab
import com.base.design.globalcomponents.PocketyTopAppBar
import com.base.design.ui.ScreenPreviews
import com.base.design.ui.theme.colorSchemeExtended

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    categoriesCount: Int = 18,
    labelsCount: Int = 7,
    selectedCurrency: CurrencyType = CurrencyType.Tomans,
    onCurrencySelected: (CurrencyType) -> Unit = {},
    selectedCalendarSystem: CalendarSystemType = CalendarSystemType.Gregorian,
    onCalendarSystemSelected: (CalendarSystemType) -> Unit = {},
    endOfDayReminderEnabled: Boolean = true,
    onEndOfDayReminderToggle: (Boolean) -> Unit = {},
    selectedLanguage: String = "English",
    onLanguageClick: () -> Unit = {},
    onCategoriesClick: () -> Unit = {},
    onLabelsClick: () -> Unit = {},
    selectedTab: PocketyTab = PocketyTab.Settings,
    onTabSelected: (PocketyTab) -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
) {
  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        if (onBackClick != null) {
          PocketyTopAppBar(
              title = "Settings",
              onBackClick = onBackClick,
          )
        } else {
          PocketyTopAppBar(
              title = "Settings",
          )
        }
      },
      bottomBar = {
        PocketyBottomBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
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
          // Section 1: Organize & Taxonomy
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SettingsSectionHeader(title = "Organize & Taxonomy")
            SettingsCardContainer(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              SettingsNavigationRow(
                  title = "Categories",
                  subtitle = "Manage expense categories & icons",
                  icon = Icons.Default.Category,
                  iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                  iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                  countBadgeText = "$categoriesCount items",
                  onClick = onCategoriesClick,
              )
              HorizontalDivider(
                  modifier = Modifier.padding(horizontal = 16.dp),
                  color = MaterialTheme.colorScheme.surfaceContainer,
                  thickness = 1.dp,
              )
              SettingsNavigationRow(
                  title = "Labels",
                  subtitle = "Manage custom transaction tags & labels",
                  icon = Icons.Default.LocalOffer,
                  iconContainerColor = MaterialTheme.colorSchemeExtended.tertiaryFixed,
                  iconContentColor = MaterialTheme.colorSchemeExtended.onTertiaryFixedVariant,
                  countBadgeText = "$labelsCount items",
                  onClick = onLabelsClick,
              )
            }
          }

          // Section 2: Preferences & Region
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SettingsSectionHeader(title = "Preferences & Region")
            SettingsCardContainer(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
              SettingsLanguageRow(
                  title = "App language",
                  selectedLanguage = selectedLanguage,
                  onClick = onLanguageClick,
              )
              DisplayCurrencyCard(
                  selectedCurrency = selectedCurrency,
                  onCurrencySelected = onCurrencySelected,
              )
              DisplayCalendarSystemCard(
                  selectedCalendarSystem = selectedCalendarSystem,
                  onCalendarSystemSelected = onCalendarSystemSelected,
              )
            }
          }

          // Section 3: Notifications & Automation
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SettingsSectionHeader(title = "Notifications & Automation")
            SettingsCardContainer(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
              SwitchRow(
                  title = "End-of-day reminder",
                  subtitle = "Notify at 21:00 to review pending SMS",
                  icon = Icons.Default.Alarm,
                  checked = endOfDayReminderEnabled,
                  onCheckedChange = onEndOfDayReminderToggle,
              )
            }
          }

          // Footer Badge
          SettingsVersionBadge(version = "Pockety v1.2.0")
        }
      }
    }
  }
}

@ScreenPreviews
@Composable
fun SettingsScreenPreview() {
  var selectedCurrency by remember { mutableStateOf(CurrencyType.Tomans) }
  var selectedCalendarSystem by remember { mutableStateOf(CalendarSystemType.Gregorian) }
  var reminderEnabled by remember { mutableStateOf(true) }
  var selectedTab by remember { mutableStateOf(PocketyTab.Settings) }

  SettingsScreen(
      selectedCurrency = selectedCurrency,
      onCurrencySelected = { selectedCurrency = it },
      selectedCalendarSystem = selectedCalendarSystem,
      onCalendarSystemSelected = { selectedCalendarSystem = it },
      endOfDayReminderEnabled = reminderEnabled,
      onEndOfDayReminderToggle = { reminderEnabled = it },
      selectedTab = selectedTab,
      onTabSelected = { selectedTab = it },
  )
}

@ScreenPreviews
@Composable
fun SettingsScreenPersianPreview() {
  SettingsScreen(
      selectedCurrency = CurrencyType.Rials,
      selectedCalendarSystem = CalendarSystemType.Persian,
      endOfDayReminderEnabled = false,
  )
}
