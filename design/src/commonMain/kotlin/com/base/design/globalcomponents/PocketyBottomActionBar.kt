package com.base.design.globalcomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.base.design.ui.ComponentPreviews

/**
 * Reusable sticky bottom action bar for forms, settings, and transaction creation screens.
 *
 * Provides a prominent 56dp primary pill button with M3 Expressive elevation (4dp), anchored to a
 * semi-transparent surface.
 */
@Composable
fun PocketyBottomActionBar(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = Icons.Default.Check,
    enabled: Boolean = true,
    secondaryContent: (@Composable () -> Unit)? = null,
) {
  Surface(
      color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
      modifier = modifier.fillMaxWidth(),
  ) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
      Column(
          modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = CircleShape,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        ) {
          if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
          }
          Text(
              text = text,
              style = MaterialTheme.typography.labelLarge,
          )
        }
        if (secondaryContent != null) {
          secondaryContent()
        }
      }
    }
  }
}

@ComponentPreviews
@Composable
private fun PocketyBottomActionBarPreview() {
  PocketyBottomActionBar(
      text = "Save Changes",
      onClick = {},
  )
}
