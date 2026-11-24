package com.base.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.base.ui.theme.ExtendedColorScheme
import com.base.ui.theme.appTypography
import com.base.ui.theme.darkScheme
import com.base.ui.theme.extendedDark
import com.base.ui.theme.extendedLight
import com.base.ui.theme.lightScheme

@Composable
fun BaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
  val targetColorScheme =
      (if (useDynamicTheme) dynamicColorScheme(darkTheme) else null)
          ?: defaultColorScheme(darkTheme)

  val extendedColorScheme = if (darkTheme) extendedDark else extendedLight

  CompositionLocalProvider(
      ExtendedTheme.LocalExtendedTheme provides extendedColorScheme,
  ) {
    MaterialTheme(
        colorScheme = targetColorScheme,
        typography = appTypography(),
        content = content,
    )
  }
}

fun defaultColorScheme(darkTheme: Boolean): ColorScheme {
  return if (darkTheme) darkScheme else lightScheme
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
