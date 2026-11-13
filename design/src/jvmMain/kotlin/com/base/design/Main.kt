package com.base.design

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.base.design.figma.FigmaBoard
import com.base.design.figma.FigmaCanvas
import com.base.design.figma.FigmaDesignPage
import com.base.design.figma.rememberFigmaCanvasState
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
        Surface(modifier = Modifier.fillMaxSize()) {
          var selectedPage by remember { mutableStateOf(FigmaDesignPage.All) }
          val canvasState = rememberFigmaCanvasState()

          FigmaCanvas(
              state = canvasState,
              overlay = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopStart,
                ) {
                  CanvasPagesBar(
                      selectedPage = selectedPage,
                      onSelectPage = {
                        selectedPage = it
                        canvasState.isInitialFitDone = false
                      },
                      modifier = Modifier.padding(start = 20.dp, top = 20.dp),
                  )
                }
              },
          ) { deviceMode ->
            FigmaBoard(
                deviceMode = deviceMode,
                selectedPage = selectedPage,
            )
          }
        }
      }
    }
  }
}
