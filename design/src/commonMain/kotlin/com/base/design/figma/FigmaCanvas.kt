package com.base.design.figma

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.StayCurrentLandscape
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isAltPressed
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.isShiftPressed
import androidx.compose.ui.input.pointer.isTertiaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.base.design.ui.ComponentPreviews
import com.base.design.ui.FigmaBoardPreviews
import com.base.design.ui.theme.PocketyTheme
import kotlin.math.exp
import kotlin.math.roundToInt

private const val SCROLL_MULTIPLIER = 24f
private const val ZOOM_FACTOR_PER_TICK = 0.1f

enum class CanvasTool {
  Select,
  Pan,
}

@Stable
class FigmaCanvasState(
    initialScale: Float = 1f,
    initialOffset: Offset = Offset.Zero,
    initialDarkTheme: Boolean = false,
    initialDeviceMode: FigmaDeviceMode = FigmaDeviceMode.Portrait,
    val minScale: Float = 0.05f,
    val maxScale: Float = 30f,
) {
  var scale by mutableFloatStateOf(initialScale)
  var offset by mutableStateOf(initialOffset)
  var activeTool by mutableStateOf(CanvasTool.Select)
  var isDarkTheme by mutableStateOf(initialDarkTheme)
  var deviceMode by mutableStateOf(initialDeviceMode)
  internal var isInitialFitDone = false

  fun toggleTheme() {
    isDarkTheme = !isDarkTheme
  }

  fun switchDeviceMode() {
    deviceMode = deviceMode.next()
  }

  fun panBy(delta: Offset) {
    offset += delta
  }

  fun zoomBy(zoomFactor: Float, centroid: Offset) {
    val newScale = (scale * zoomFactor).coerceIn(minScale, maxScale)
    if (newScale != scale) {
      val effectiveFactor = newScale / scale
      // Screen = Canvas * scale + offset => Canvas = (Centroid - offset) / scale
      // Centroid = Canvas * newScale + newOffset => newOffset = Centroid - (Centroid - offset) *
      // effectiveFactor
      offset = centroid - (centroid - offset) * effectiveFactor
      scale = newScale
    }
  }

  fun reset(newScale: Float = 1f, newOffset: Offset = Offset.Zero) {
    scale = newScale
    offset = newOffset
  }
}

@Composable
fun rememberFigmaCanvasState(
    initialScale: Float = 1f,
    initialOffset: Offset = Offset.Zero,
    initialDarkTheme: Boolean = false,
    initialDeviceMode: FigmaDeviceMode = FigmaDeviceMode.Portrait,
): FigmaCanvasState = remember {
  FigmaCanvasState(
      initialScale = initialScale,
      initialOffset = initialOffset,
      initialDarkTheme = initialDarkTheme,
      initialDeviceMode = initialDeviceMode,
  )
}

/**
 * A general composable container that can contain [FigmaDeviceArtboard] or any
 * other screens/elements, allowing Figma-like canvas pan & zoom interactions:
 * - Mouse wheel vertical scroll: pans vertically
 * - Shift + mouse wheel scroll: pans horizontally
 * - Ctrl / Cmd / Alt + mouse wheel: zooms centered at cursor
 * - Middle-click drag: pans canvas regardless of selected tool
 * - Hand tool (Pan): pans canvas on left-click drag
 * - Multi-touch gestures: pinch to zoom, two-finger pan
 */
@Composable
fun FigmaCanvas(
    modifier: Modifier = Modifier,
    state: FigmaCanvasState = rememberFigmaCanvasState(),
    showToolbar: Boolean = true,
    overlay: (@Composable () -> Unit)? = null,
    content: @Composable (deviceMode: FigmaDeviceMode) -> Unit,
) {
  PocketyTheme(darkTheme = state.isDarkTheme) {
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
      CanvasContentBox(state = state) { content(state.deviceMode) }
      if (overlay != null) {
        overlay()
      }
      if (showToolbar) {
        CanvasToolbar(
            activeTool = state.activeTool,
            onToolSelect = { state.activeTool = it },
            scale = state.scale,
            onResetZoom = { state.reset() },
            isDarkTheme = state.isDarkTheme,
            onToggleTheme = { state.toggleTheme() },
            deviceMode = state.deviceMode,
            onSwitchDeviceMode = { state.switchDeviceMode() },
            modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-20).dp),
        )
      }
    }
  }
}

@Composable
private fun CanvasContentBox(
    state: FigmaCanvasState,
    content: @Composable () -> Unit,
) {
  val density = LocalDensity.current

  Box(
      modifier =
          Modifier.fillMaxSize()
              .pointerInput(state) {
                awaitEachGesture {
                  while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    if (event.type == PointerEventType.Scroll) {
                      handleScrollEvent(event, state)
                    }
                  }
                }
              }
              .pointerInput(state) {
                awaitEachGesture {
                  while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    if (!event.changes.any { it.pressed }) continue
                    handleDragOrPinchEvent(event, state)
                  }
                }
              }
              .graphicsLayer {
                translationX = state.offset.x
                translationY = state.offset.y
                scaleX = state.scale
                scaleY = state.scale
                transformOrigin = TransformOrigin(0f, 0f)
              }
              .layout { measurable, constraints ->
                val placeable = measurable.measure(Constraints())

                if (!state.isInitialFitDone && constraints.maxWidth > 0 && placeable.width > 0) {
                  state.isInitialFitDone = true
                  val paddingPx = with(density) { 50.dp.toPx() }
                  val availableWidth = (constraints.maxWidth - paddingPx * 2).coerceAtLeast(0f)
                  val availableHeight = (constraints.maxHeight - paddingPx * 2).coerceAtLeast(0f)

                  val scaleX = availableWidth / placeable.width
                  val scaleY = availableHeight / placeable.height
                  val scale = minOf(scaleX, scaleY).coerceIn(state.minScale, state.maxScale)

                  val scaledContentWidth = placeable.width * scale
                  val scaledContentHeight = placeable.height * scale

                  val offsetX = (constraints.maxWidth - scaledContentWidth) / 2f
                  val offsetY = (constraints.maxHeight - scaledContentHeight) / 2f

                  state.reset(scale, Offset(offsetX, offsetY))
                }

                layout(constraints.maxWidth, constraints.maxHeight) {
                  placeable.placeRelative(0, 0)
                }
              },
  ) {
    Box { content() }
  }
}

private fun handleScrollEvent(event: PointerEvent, state: FigmaCanvasState) {
  val change = event.changes.firstOrNull() ?: return
  if (change.isConsumed) return
  val delta = change.scrollDelta
  val keyboard = event.keyboardModifiers
  val isZoom = keyboard.isCtrlPressed || keyboard.isMetaPressed || keyboard.isAltPressed

  when {
    isZoom -> {
      val factor = exp(-delta.y * ZOOM_FACTOR_PER_TICK)
      state.zoomBy(factor, change.position)
    }
    keyboard.isShiftPressed -> {
      val deltaX = -(delta.y + delta.x) * SCROLL_MULTIPLIER
      state.panBy(Offset(deltaX, 0f))
    }
    else -> {
      state.panBy(Offset(-delta.x * SCROLL_MULTIPLIER, -delta.y * SCROLL_MULTIPLIER))
    }
  }
  change.consume()
}

private fun handleDragOrPinchEvent(event: PointerEvent, state: FigmaCanvasState) {
  if (event.changes.any { it.isConsumed }) return

  val isMiddleClick = event.buttons.isTertiaryPressed
  val isPanTool = state.activeTool == CanvasTool.Pan

  if (isMiddleClick || isPanTool) {
    val pan = event.calculatePan()
    if (pan != Offset.Zero) {
      state.panBy(pan)
      event.changes.forEach { it.consume() }
    }
  } else if (event.changes.size > 1) {
    val zoom = event.calculateZoom()
    val pan = event.calculatePan()
    val centroid = event.calculateCentroid()
    if (zoom != 1f) state.zoomBy(zoom, centroid)
    if (pan != Offset.Zero) state.panBy(pan)
    event.changes.forEach { it.consume() }
  }
}

@Composable
fun CanvasToolbar(
    activeTool: CanvasTool,
    onToolSelect: (CanvasTool) -> Unit,
    scale: Float,
    onResetZoom: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    deviceMode: FigmaDeviceMode = FigmaDeviceMode.Portrait,
    onSwitchDeviceMode: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier,
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
      tonalElevation = 6.dp,
      shadowElevation = 8.dp,
      border =
          BorderStroke(
              1.dp,
              MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
          ),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      ToolIconButton(
          icon = Icons.Default.NearMe,
          description = "Select Tool (Pointer)",
          isSelected = activeTool == CanvasTool.Select,
          onClick = { onToolSelect(CanvasTool.Select) },
      )
      ToolIconButton(
          icon = Icons.Default.PanTool,
          description = "Pan Tool (Hand)",
          isSelected = activeTool == CanvasTool.Pan,
          onClick = { onToolSelect(CanvasTool.Pan) },
      )
      VerticalDivider(
          modifier = Modifier.height(24.dp),
          color = MaterialTheme.colorScheme.outlineVariant,
      )
      ToolIconButton(
          icon = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
          description = if (isDarkTheme) "Switch to Light Theme" else "Switch to Dark Theme",
          isSelected = false,
          onClick = onToggleTheme,
      )
      ToolIconButton(
          icon =
              if (deviceMode == FigmaDeviceMode.Landscape) Icons.Default.StayCurrentLandscape
              else Icons.Default.StayCurrentPortrait,
          description = "Device view: ${deviceMode.title} (Click to toggle)",
          isSelected = deviceMode == FigmaDeviceMode.Landscape,
          onClick = onSwitchDeviceMode,
      )
      VerticalDivider(
          modifier = Modifier.height(24.dp),
          color = MaterialTheme.colorScheme.outlineVariant,
      )
      TextButton(onClick = onResetZoom) {
        Text(
            text = "${(scale * 100).roundToInt()}%",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
      }
    }
  }
}

@Composable
private fun ToolIconButton(
    icon: ImageVector,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
  IconButton(
      onClick = onClick,
      colors =
          IconButtonDefaults.iconButtonColors(
              containerColor =
                  if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
              contentColor =
                  if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                  else MaterialTheme.colorScheme.onSurfaceVariant,
          ),
  ) {
    Icon(
        imageVector = icon,
        contentDescription = description,
        modifier = Modifier.size(20.dp),
    )
  }
}

@Composable
fun FigmaCanvasSample(
    modifier: Modifier = Modifier,
) {
  FigmaCanvas(
      modifier = modifier,
  ) { deviceMode ->
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
            deviceMode = deviceMode,
        ) {
          Text("Home Screen")
        }

        FigmaDeviceArtboard(
            number = "02",
            title = "Inbox",
            tag = "Smart Extraction",
            deviceMode = deviceMode,
        ) {
          Text("Inbox Screen")
        }

        FigmaDeviceArtboard(
            number = "03",
            title = "Pockets",
            tag = "Budget Overview",
            deviceMode = deviceMode,
        ) {
          Text("Pockets Screen")
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
      deviceMode = FigmaDeviceMode.Portrait,
      onSwitchDeviceMode = {},
  )
}
