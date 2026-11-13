package com.base.feature.settings

import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.LayoutDirection
import com.base.RootScreen
import com.base.feature.root.FakeRootComponent
import com.base.util.waitForExistence
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SettingsLanguageE2eUiTest {

  @Test
  fun testLanguageSelectionDialogInteraction() = runComposeUiTest {
    val component = FakeSettingsComponent()

    setContent { SettingsScreen(component = component) }

    onNode(hasTestTag("settings-language-picker-pill"))
        .waitForExistence()
        .performScrollTo()
        .performClick()

    component.state.value.isLanguageDialogVisible shouldBe true
    onNode(hasTestTag("language-selection-dialog")).waitForExistence().assertExists()

    onNode(hasTestTag("language-option-persian")).performClick()
    component.state.value.selectedLanguage shouldBe AppLanguage.PERSIAN
    component.state.value.isLanguageDialogVisible shouldBe false
  }

  @Test
  fun testLanguageSelectionDialogCancel() = runComposeUiTest {
    val component = FakeSettingsComponent()

    setContent { SettingsScreen(component = component) }

    onNode(hasTestTag("settings-language-picker-pill"))
        .waitForExistence()
        .performScrollTo()
        .performClick()

    onNode(hasTestTag("language-dialog-cancel-button")).performClick()
    component.state.value.isLanguageDialogVisible shouldBe false
  }

  @Test
  fun testRootLayoutDirectionSyncsWithAppLanguage() = runComposeUiTest {
    val rootComponent = FakeRootComponent(initialLanguage = AppLanguage.PERSIAN)
    var activeDirection: LayoutDirection? = null

    setContent {
      RootScreen(root = rootComponent) { activeDirection = LocalLayoutDirection.current }
    }

    activeDirection shouldBe LayoutDirection.Rtl
  }

  @Test
  fun testRootLayoutDirectionDefaultsToLtr() = runComposeUiTest {
    val rootComponent = FakeRootComponent(initialLanguage = AppLanguage.ENGLISH)
    var activeDirection: LayoutDirection? = null

    setContent {
      RootScreen(root = rootComponent) { activeDirection = LocalLayoutDirection.current }
    }

    activeDirection shouldBe LayoutDirection.Ltr
  }
}
