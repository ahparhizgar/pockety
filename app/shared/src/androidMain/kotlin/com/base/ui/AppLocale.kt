package com.base.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.base.feature.settings.AppLanguage
import java.util.Locale

@Composable
internal actual fun AppLocale(language: AppLanguage, content: @Composable () -> Unit) {
  val configuration = LocalConfiguration.current
  val context = LocalContext.current
  val localizedConfiguration =
      remember(configuration, language) {
        Configuration(configuration).apply { setLocale(Locale.forLanguageTag(language.code)) }
      }
  val localizedContext =
      remember(context, localizedConfiguration) {
        context.createConfigurationContext(localizedConfiguration)
      }
  CompositionLocalProvider(
      LocalConfiguration provides localizedConfiguration,
      LocalContext provides localizedContext,
  ) {
    key(language) { content() }
  }
}
