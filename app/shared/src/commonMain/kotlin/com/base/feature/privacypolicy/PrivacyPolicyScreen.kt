package com.base.feature.privacypolicy

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.base.feature.settings.SettingsSectionHeader
import com.base.feature.settings.SettingsVersionBadge
import com.base.ui.ScreenPreviews
import com.base.ui.colorSchemeExtended
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.privacy_policy_commitments_header
import pockety.app.shared.generated.resources.privacy_policy_pillar_personal_data_desc
import pockety.app.shared.generated.resources.privacy_policy_pillar_personal_data_title
import pockety.app.shared.generated.resources.privacy_policy_pillar_sms_data_desc
import pockety.app.shared.generated.resources.privacy_policy_pillar_sms_data_title
import pockety.app.shared.generated.resources.privacy_policy_pillar_telemetry_desc
import pockety.app.shared.generated.resources.privacy_policy_pillar_telemetry_title
import pockety.app.shared.generated.resources.privacy_policy_title
import pockety.app.shared.generated.resources.privacy_policy_version_footer

@Composable
fun PrivacyPolicyScreen(
    component: PrivacyPolicyComponent,
    modifier: Modifier = Modifier,
) {
  Scaffold(
      modifier = modifier.fillMaxSize().testTag("privacy-policy-screen"),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.privacy_policy_title),
            onBackClick = component::onBack,
            modifier = Modifier.testTag("privacy-policy-top-app-bar"),
            backActionTestTag = "privacy-policy-back",
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
          PrivacyHeroCard(modifier = Modifier.testTag("privacy-policy-hero-card"))

          PrivacyPolicyCommitmentsSection()

          PrivacyDataSummaryCard(modifier = Modifier.testTag("privacy-policy-summary-card"))

          SettingsVersionBadge(
              version = stringResource(Res.string.privacy_policy_version_footer),
              modifier = Modifier.testTag("privacy-policy-version-footer"),
          )
        }
      }
    }
  }
}

@Composable
private fun PrivacyPolicyCommitmentsSection(
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    SettingsSectionHeader(title = stringResource(Res.string.privacy_policy_commitments_header))

    PrivacyPolicyPillarCard(
        title = stringResource(Res.string.privacy_policy_pillar_personal_data_title),
        description = stringResource(Res.string.privacy_policy_pillar_personal_data_desc),
        icon = Icons.Default.Lock,
        iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
        iconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.testTag("privacy-policy-pillar-personal-data"),
    )

    PrivacyPolicyPillarCard(
        title = stringResource(Res.string.privacy_policy_pillar_sms_data_title),
        description = stringResource(Res.string.privacy_policy_pillar_sms_data_desc),
        icon = Icons.Default.SmsFailed,
        iconContainerColor = MaterialTheme.colorSchemeExtended.tertiaryFixed,
        iconContentColor = MaterialTheme.colorSchemeExtended.onTertiaryFixedVariant,
        modifier = Modifier.testTag("privacy-policy-pillar-sms-data"),
    )

    PrivacyPolicyPillarCard(
        title = stringResource(Res.string.privacy_policy_pillar_telemetry_title),
        description = stringResource(Res.string.privacy_policy_pillar_telemetry_desc),
        icon = Icons.Default.Analytics,
        iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.testTag("privacy-policy-pillar-telemetry"),
    )
  }
}

@ScreenPreviews
@Composable
private fun PrivacyPolicyScreenPreview() {
  PrivacyPolicyScreen(component = FakePrivacyPolicyComponent())
}
