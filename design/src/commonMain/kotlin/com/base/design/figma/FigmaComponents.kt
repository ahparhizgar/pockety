package com.base.design.figma

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class FigmaDeviceMode(
    val title: String,
    val width: Dp,
    val height: Dp,
) {
  Portrait("Portrait", 390.dp, 844.dp),
  Landscape("Landscape", 844.dp, 390.dp),
  ;

  fun next(): FigmaDeviceMode {
    val values = entries
    val nextIndex = (ordinal + 1) % values.size
    return values[nextIndex]
  }
}

/**
 * A device frame container that renders a screen within a standardized phone mockup with an
 * artboard title, step badge, and flow tag like in Figma.
 */
@Composable()
fun FigmaDeviceArtboard(
    number: String,
    title: String,
    tag: String,
    modifier: Modifier = Modifier,
    deviceMode: FigmaDeviceMode = FigmaDeviceMode.Portrait,
    content: @Composable () -> Unit,
) {
  val frameWidth = deviceMode.width
  val frameHeight = deviceMode.height

  Column(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    ArtboardHeader(number = number, title = title, tag = tag, width = frameWidth)

    // Phone Frame Mockup
    Surface(
        modifier = Modifier.width(frameWidth).height(frameHeight),
        shape = RoundedCornerShape(32.dp),
        color = Color.Transparent,
        border =
            BorderStroke(
                1.5.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
            ),
    ) {
      Box(
          modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(32.dp)),
      ) {
        content()
      }
    }
  }
}

@Composable
fun FigmaSectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Surface(
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
        )
      }
    }
    Column {
      Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun ArtboardHeader(
    number: String,
    title: String,
    tag: String,
    width: Dp = 390.dp,
) {
  Row(
      modifier = Modifier.width(width),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
          color = MaterialTheme.colorScheme.primary,
          shape = RoundedCornerShape(8.dp),
      ) {
        Text(
            text = number,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
      }
      Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
      )
    }

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = CircleShape,
    ) {
      Text(
          text = tag,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSecondaryContainer,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
      )
    }
  }
}
