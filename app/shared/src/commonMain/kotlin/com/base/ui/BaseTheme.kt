package com.base.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.base.ui.theme.AppTypography
import com.base.ui.theme.ExtendedColorScheme
import com.base.ui.theme.darkScheme
import com.base.ui.theme.extendedDark
import com.base.ui.theme.extendedLight
import com.base.ui.theme.lightScheme

@Composable
fun BaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
  val defaultColorScheme = if (darkTheme) darkScheme else lightScheme
  val targetColorScheme = dynamicColorScheme(darkTheme) ?: defaultColorScheme
  val extendedColorScheme = if (darkTheme) extendedDark else extendedLight

  CompositionLocalProvider(
      ExtendedTheme.LocalExtendedTheme provides extendedColorScheme,
  ) {
    MaterialTheme(
        colorScheme = targetColorScheme,
        typography = AppTypography,
        content = content,
    )
  }
}

internal object ExtendedTheme {
  val colors: ExtendedColorScheme
    @Composable @ReadOnlyComposable get() = LocalExtendedTheme.current

  val LocalExtendedTheme: ProvidableCompositionLocal<ExtendedColorScheme> =
      staticCompositionLocalOf {
        extendedLight
      }
}

val MaterialTheme.colorSchemeExtended: ExtendedColorScheme
  @Composable @ReadOnlyComposable get() = ExtendedTheme.colors
