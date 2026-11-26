package com.base.feature.pocket

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val ICON_SIZE_FRACTION = 0.5f

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
 * A pocket-shaped badge filled with the pocket's color, carrying the pocket's icon and the app
 * icon's top slit detail, so a card reads as a pocket at a glance.
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
      Icon(
          imageVector = icon,
          contentDescription = contentDescription,
          tint = iconTint,
          modifier = Modifier.size(badgeSize * ICON_SIZE_FRACTION),
      )
    }
  }
}
