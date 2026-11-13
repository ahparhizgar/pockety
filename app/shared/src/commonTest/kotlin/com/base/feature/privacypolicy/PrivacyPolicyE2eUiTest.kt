package com.base.feature.privacypolicy

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.util.waitForExistence
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class PrivacyPolicyE2eUiTest {

  @Test
  fun testPrivacyPolicyScreenRendering() = runComposeUiTest {
    val component = FakePrivacyPolicyComponent()

    setContent { PrivacyPolicyScreen(component = component) }

    onNode(hasTestTag("privacy-policy-screen")).waitForExistence().assertExists()
    onNode(hasTestTag("privacy-policy-hero-card")).waitForExistence().assertExists()
    onNode(hasTestTag("privacy-policy-pillar-personal-data")).waitForExistence().assertExists()
    onNode(hasTestTag("privacy-policy-pillar-sms-data")).waitForExistence().assertExists()
    onNode(hasTestTag("privacy-policy-pillar-telemetry")).waitForExistence().assertExists()
    onNode(hasTestTag("privacy-policy-summary-card")).waitForExistence().assertExists()
    onNode(hasTestTag("privacy-policy-version-footer")).waitForExistence().assertExists()
  }

  @Test
  fun testPrivacyPolicyBackNavigationClick() = runComposeUiTest {
    var backClicked = false
    val component = FakePrivacyPolicyComponent(onBackClick = { backClicked = true })

    setContent { PrivacyPolicyScreen(component = component) }

    onNode(hasTestTag("privacy-policy-back")).waitForExistence().performClick()
    backClicked shouldBe true
  }
}
