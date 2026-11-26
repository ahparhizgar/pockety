package com.base.feature.pocket

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val ICON_SIZE_FRACTION = 0.5f
private const val STITCH_INSET_FRACTION = 0.09f
private const val STITCH_WIDTH_FRACTION = 0.03f
private const val STITCH_DASH_FRACTION = 0.09f
private const val STITCH_GAP_FRACTION = 0.06f
private const val STITCH_ALPHA = 0.6f
private const val ARC_START_ANGLE = 0f
private const val ARC_SWEEP_ANGLE = 180f

/** Pocket silhouette matching the app icon: flat top, straight sides, semicircular bottom. */
val PocketShape = GenericShape { size, _ ->
  val radius = size.width / 2f
  moveTo(0f, 0f)
  lineTo(size.width, 0f)
  lineTo(size.width, size.height - radius)
  arcTo(
      rect = Rect(0f, size.height - 2 * radius, size.width, size.height),
      startAngleDegrees = 0f,
      sweepAngleDegrees = 180f,
      forceMoveTo = false,
  )
  close()
}

/**
 * A pocket-shaped badge filled with the pocket's color, carrying the pocket's icon and a dashed
 * stitch along its sides and bottom, so a card reads as a pocket at a glance.
 */
@Suppress("LongParameterList")
@Composable
fun PocketShapeBadge(
    icon: ImageVector,
    color: Color,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    badgeSize: Dp = 40.dp,
    iconTint: Color = Color.White,
) {
  Surface(
      modifier = modifier.size(badgeSize),
      shape = PocketShape,
      color = color,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Canvas(modifier = Modifier.size(badgeSize)) {
        val inset = size.width * STITCH_INSET_FRACTION
        val centerY = size.height - size.width / 2f
        val stitchPath =
            Path().apply {
              moveTo(size.width - inset, inset)
              lineTo(size.width - inset, centerY)
              arcTo(
                  rect = Rect(inset, inset, size.width - inset, size.height - inset),
                  startAngleDegrees = ARC_START_ANGLE,
                  sweepAngleDegrees = ARC_SWEEP_ANGLE,
                  forceMoveTo = false,
              )
              lineTo(inset, inset)
            }
        drawPath(
            path = stitchPath,
            color = iconTint.copy(alpha = STITCH_ALPHA),
            style =
                Stroke(
                    width = size.width * STITCH_WIDTH_FRACTION,
                    pathEffect =
                        PathEffect.dashPathEffect(
                            floatArrayOf(
                                size.width * STITCH_DASH_FRACTION,
                                size.width * STITCH_GAP_FRACTION,
                            ),
                        ),
                ),
        )
      }
      Icon(
          imageVector = icon,
          contentDescription = contentDescription,
          tint = iconTint,
          modifier = Modifier.size(badgeSize * ICON_SIZE_FRACTION),
      )
    }
  }
}
