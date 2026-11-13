package com.base.design.features.privacypolicy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SmsFailed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.design.features.settings.SettingsSectionHeader
import com.base.design.features.settings.SettingsVersionBadge
import com.base.design.globalcomponents.PocketyTopAppBar
import com.base.design.ui.ScreenPreviews
import com.base.design.ui.theme.colorSchemeExtended

@Composable
fun PrivacyPolicyScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopAppBar(
            title = "Privacy Policy",
            onBackClick = onBackClick,
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
          PrivacyHeroCard()

          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SettingsSectionHeader(title = "Our Core Commitments")

            PrivacyPolicyPillarCard(
                title = "We do not send personal data",
                description =
                    "All your accounts, pockets, categories, transactions, and financial logs are saved locally on your device. We do not transmit or monetize personal financial data.",
                icon = Icons.Default.Lock,
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                iconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            PrivacyPolicyPillarCard(
                title = "We do not send SMS data",
                description =
                    "SMS reading and bank notification parsing run entirely on your device. Message contents, phone numbers, and bank details never reach any remote server.",
                icon = Icons.Default.SmsFailed,
                iconContainerColor = MaterialTheme.colorSchemeExtended.tertiaryFixed,
                iconContentColor = MaterialTheme.colorSchemeExtended.onTertiaryFixedVariant,
            )

            PrivacyPolicyPillarCard(
                title = "Anonymous usage data only",
                description =
                    "We only send and store anonymous aggregated usage metrics (such as crash diagnostics and performance stats) solely to improve stability and UX.",
                icon = Icons.Default.Analytics,
                iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
          }

          PrivacyDataSummaryCard()

          SettingsVersionBadge(version = "Privacy Policy • Updated Nov 2026")
        }
      }
    }
  }
}

@ScreenPreviews
@Composable
fun PrivacyPolicyScreenPreview() {
  PrivacyPolicyScreen()
}
