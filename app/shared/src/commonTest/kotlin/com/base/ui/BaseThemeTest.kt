package com.base.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.ui.theme.darkScheme
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class BaseThemeTest {
  @Test
  fun testBaseThemeUsesCustomColorSchemeWhenProvided() = runComposeUiTest {
    var actualPrimary = Color.Unspecified

    setContent { BaseTheme(darkTheme = true) { actualPrimary = MaterialTheme.colorScheme.primary } }

    assertEquals(darkScheme.primary, actualPrimary)
  }
}
