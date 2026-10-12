package com.base.design.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
fun PocketyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
  val targetColorScheme = if (darkTheme) darkScheme else lightScheme
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

object ExtendedTheme {
  val colors: ExtendedColorScheme
    @Composable @ReadOnlyComposable get() = LocalExtendedTheme.current

  val LocalExtendedTheme: ProvidableCompositionLocal<ExtendedColorScheme> =
      staticCompositionLocalOf {
        extendedLight
      }
}

val MaterialTheme.colorSchemeExtended: ExtendedColorScheme
  @Composable @ReadOnlyComposable get() = ExtendedTheme.colors
