@file:Suppress(
    "UnusedPrivateFunction",
    "PreviewPublic",
)

package com.base.design.canvas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.design.AllScreensFigmaBoard
import com.base.design.FigmaDeviceArtboard
import com.base.design.screens.HomeScreen
import com.base.design.screens.InboxScreen
import com.base.design.screens.PocketsScreen
import com.base.design.ui.ComponentPreviews
import com.base.design.ui.FigmaBoardPreviews

@Composable
fun FigmaCanvasSample(
    modifier: Modifier = Modifier,
) {
  FigmaCanvas(
      modifier = modifier,
  ) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
      Row(
          modifier = Modifier.padding(48.dp),
          horizontalArrangement = Arrangement.spacedBy(36.dp),
      ) {
        FigmaDeviceArtboard(
            number = "01",
            title = "Home",
            tag = "Main Tab",
        ) {
          HomeScreen()
        }

        FigmaDeviceArtboard(
            number = "02",
            title = "Inbox",
            tag = "Smart Extraction",
        ) {
          InboxScreen()
        }

        FigmaDeviceArtboard(
            number = "03",
            title = "Pockets",
            tag = "Budget Overview",
        ) {
          PocketsScreen()
        }
      }
    }
  }
}

@FigmaBoardPreviews
@Composable
private fun FigmaCanvasSamplePreview() {
  FigmaCanvasSample()
}

@FigmaBoardPreviews
@Composable
private fun FigmaCanvasFullBoardPreview() {
  FigmaCanvas { AllScreensFigmaBoard() }
}

@ComponentPreviews
@Composable
private fun CanvasToolbarPreview() {
  CanvasToolbar(
      activeTool = CanvasTool.Select,
      onToolSelect = {},
      scale = 1.0f,
      onResetZoom = {},
      isDarkTheme = false,
      onToggleTheme = {},
  )
}
