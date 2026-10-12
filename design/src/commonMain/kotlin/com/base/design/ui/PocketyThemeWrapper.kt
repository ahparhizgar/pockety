package com.base.design.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.base.design.ui.theme.PocketyTheme

class PocketyThemeWrapper : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) {
    PocketyTheme { content() }
  }
}
