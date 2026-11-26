package com.base.feature.pocket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
@Composable
fun PocketShapeBadge(
    icon: ImageVector,
    color: Color,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    badgeSize: Dp = 40.dp,
    iconSize: Dp = 20.dp,
) {
  Surface(
      modifier = modifier.size(badgeSize),
      shape = PocketShape,
      color = color,
  ) {
    Box {
      Box(
          modifier =
              Modifier.align(Alignment.TopCenter)
                  .padding(top = badgeSize * 0.18f)
                  .width(badgeSize * 0.36f)
                  .height(badgeSize * 0.07f)
                  .background(
                      color = Color.White.copy(alpha = 0.55f),
                      shape = RoundedCornerShape(percent = 50),
                  ),
      )
      Icon(
          imageVector = icon,
          contentDescription = contentDescription,
          tint = Color.White,
          modifier =
              Modifier.align(Alignment.Center).padding(top = badgeSize * 0.1f).size(iconSize),
      )
    }
  }
}
