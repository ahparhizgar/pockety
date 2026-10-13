package com.base.design

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.base.design.canvas.FigmaCanvas
import com.base.design.ui.theme.PocketyTheme

fun main() {
  application {
    val windowState = rememberWindowState()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Pockety — Figma Canvas",
        state = windowState,
    ) {
      PocketyTheme {
        Surface(modifier = Modifier.fillMaxSize()) { FigmaCanvas { AllScreensFigmaBoard() } }
      }
    }
  }
}
