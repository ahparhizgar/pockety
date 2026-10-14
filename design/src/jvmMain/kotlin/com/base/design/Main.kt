package com.base.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CanvasPagesBar(
    selectedPage: FigmaDesignPage,
    onSelectPage: (FigmaDesignPage) -> Unit,
    modifier: Modifier = Modifier,
) {
  var isHovered by remember { mutableStateOf(false) }

  Surface(
      modifier =
          modifier
              .onPointerEvent(PointerEventType.Enter) { isHovered = true }
              .onPointerEvent(PointerEventType.Exit) { isHovered = false }
              .widthIn(min = 48.dp, max = 220.dp),
      shape = RoundedCornerShape(18.dp),
      color =
          MaterialTheme.colorScheme.surfaceContainerHigh.copy(
              alpha = if (isHovered) 0.96f else 0.85f
          ),
      tonalElevation = if (isHovered) 8.dp else 4.dp,
      shadowElevation = if (isHovered) 10.dp else 4.dp,
      border =
          BorderStroke(
              1.dp,
              MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (isHovered) 0.6f else 0.35f),
          ),
  ) {
    if (!isHovered) {
      // Collapsed state: compact icon + selected page tag
      Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
            imageVector = selectedPage.icon,
            contentDescription = "Pages (Hover to expand)",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = selectedPage.label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
        )
      }
    } else {
      // Expanded state: full list of pages
      Column(
          modifier = Modifier.width(220.dp).padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
              text = "PAGES",
              style =
                  MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.2.sp,
                  ),
              color = MaterialTheme.colorScheme.secondary,
          )
          Icon(
              imageVector = Icons.AutoMirrored.Filled.MenuOpen,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(16.dp),
          )
        }

        FigmaDesignPage.entries.forEach { page ->
          val isSelected = page == selectedPage
          FilterChip(
              selected = isSelected,
              onClick = { onSelectPage(page) },
              modifier = Modifier.fillMaxWidth(),
              leadingIcon = {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
              },
              label = {
                Text(
                    text = page.label,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
              },
              shape = RoundedCornerShape(10.dp),
              colors =
                  FilterChipDefaults.filterChipColors(
                      selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                      selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                      selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                      containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                      labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                      iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                  ),
              border = null,
          )
        }
      }
    }
  }
}
