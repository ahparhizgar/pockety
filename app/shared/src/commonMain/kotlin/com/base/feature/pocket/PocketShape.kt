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
import androidx.compose.ui.geometry.Size
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

/** The full pocket outline as a path, for canvas drawing. */
private fun pocketOutlinePath(size: Size): Path {
  val centerY = size.height - size.width / 2f
  return Path().apply {
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, centerY)
    arcTo(
        rect = Rect(0f, size.height - size.width, size.width, size.height),
        startAngleDegrees = ARC_START_ANGLE,
        sweepAngleDegrees = ARC_SWEEP_ANGLE,
        forceMoveTo = false,
    )
    close()
  }
}

/** The stitch path: down one side, around the bottom, up the other side. The top stays open. */
private fun pocketStitchPath(size: Size, inset: Float): Path {
  val centerY = size.height - size.width / 2f
  return Path().apply {
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
}

private fun stitchStroke(size: Size): Stroke =
    Stroke(
        width = size.width * STITCH_WIDTH_FRACTION,
        pathEffect =
            PathEffect.dashPathEffect(
                floatArrayOf(
                    size.width * STITCH_DASH_FRACTION,
                    size.width * STITCH_GAP_FRACTION,
                ),
            ),
    )

/**
 * A pocket-shaped badge filled with the pocket's color, carrying the pocket's icon and a dashed
 * stitch along its sides and bottom, so a card reads as a pocket at a glance. A null [icon] renders
 * an empty pocket.
 */
@Suppress("LongParameterList")
@Composable
fun PocketShapeBadge(
    icon: ImageVector?,
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
        drawPath(
            path = pocketStitchPath(size, size.width * STITCH_INSET_FRACTION),
            color = iconTint.copy(alpha = STITCH_ALPHA),
            style = stitchStroke(size),
        )
      }
      if (icon != null) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(badgeSize * ICON_SIZE_FRACTION),
        )
      }
    }
  }
}

/**
 * The pocket as a standalone glyph for icon slots (tabs, headers): a filled pocket in [tint] with
 * its stitch knocked out in [stitchColor].
 */
@Composable
fun PocketGlyphIcon(
    tint: Color,
    stitchColor: Color,
    modifier: Modifier = Modifier,
    glyphSize: Dp = 24.dp,
) {
  Canvas(modifier = modifier.size(glyphSize)) {
    drawPath(path = pocketOutlinePath(size), color = tint)
    drawPath(
        path = pocketStitchPath(size, size.width * STITCH_INSET_FRACTION),
        color = stitchColor,
        style = stitchStroke(size),
    )
  }
}
