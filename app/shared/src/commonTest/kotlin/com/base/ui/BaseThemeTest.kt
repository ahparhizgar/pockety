package com.base.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class BaseThemeTest {
  @Test
  fun testBaseThemeUsesCustomColorSchemeWhenProvided() = runComposeUiTest {
    val customPrimary = Color(0xFF123456)
    val customScheme = lightColorScheme(primary = customPrimary)

    var actualPrimary = Color.Unspecified

    setContent {
      BaseTheme(colorScheme = customScheme) {
        actualPrimary = MaterialTheme.colorScheme.primary
      }
    }

    assertEquals(customPrimary, actualPrimary)
  }
}
