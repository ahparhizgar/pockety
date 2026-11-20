package com.base.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import com.base.feature.settings.AppLanguage
import java.util.Locale

@Composable
internal actual fun AppLocale(language: AppLanguage, content: @Composable () -> Unit) {
  val original = remember { Locale.getDefault() }
  Locale.setDefault(Locale.forLanguageTag(language.code))
  DisposableEffect(Unit) { onDispose { Locale.setDefault(original) } }
  key(language) { content() }
}
