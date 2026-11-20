package com.base.ui

import androidx.compose.runtime.Composable
import com.base.feature.settings.AppLanguage

// Platform locale adapters follow Compose Multiplatform's resource-environment guidance.
@Composable internal expect fun AppLocale(language: AppLanguage, content: @Composable () -> Unit)
