package com.base.usermessage

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import com.base.ui.colorSchemeExtended
import com.base.usermessage.UserMessage.Tone.Error
import com.base.usermessage.UserMessage.Tone.Info
import com.base.usermessage.UserMessage.Tone.Success
import com.base.usermessage.UserMessage.Tone.Warning
import kotlin.math.roundToInt

private const val SHAKE_REPEAT_COUNT = 3
private const val SHAKE_OFFSET = 10f
private const val SHAKE_DURATION_MILLIS = 80

@Composable
fun CustomSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
) {
  val visuals = snackbarData.visuals as SnackbarMessage
  visuals.actionLabel = visuals.actionLabel2?.content()
  val offsetX = remember { Animatable(0f) }

  if (visuals.shouldShake) {
    LaunchedEffect(Unit) {
      repeat(SHAKE_REPEAT_COUNT) {
        offsetX.animateTo(SHAKE_OFFSET, tween(durationMillis = SHAKE_DURATION_MILLIS))
        offsetX.animateTo(-SHAKE_OFFSET, tween(durationMillis = SHAKE_DURATION_MILLIS))
      }
      offsetX.animateTo(0f)
    }
  }

  val colors = getColors(visuals.tone)
  Snackbar(
      modifier = modifier.offset { IntOffset(offsetX.value.roundToInt(), 0) },
      snackbarData = snackbarData,
      containerColor = colors.containerColor,
      contentColor = colors.contentColor,
      actionColor = colors.actionColor,
      actionContentColor = colors.actionContentColor,
      dismissActionContentColor = colors.dismissActionContentColor,
  )
}

private data class CustomSnackbarColors(
    val containerColor: Color,
    val contentColor: Color,
    val actionColor: Color,
    val actionContentColor: Color,
    val dismissActionContentColor: Color,
)

@Composable
private fun getColors(tone: UserMessage.Tone): CustomSnackbarColors =
    when (tone) {
      Success -> {
        CustomSnackbarColors(
            containerColor = MaterialTheme.colorSchemeExtended.success.colorContainer,
            contentColor = MaterialTheme.colorSchemeExtended.success.onColorContainer,
            actionColor = MaterialTheme.colorSchemeExtended.success.onColorContainer,
            actionContentColor = MaterialTheme.colorSchemeExtended.success.onColorContainer,
            dismissActionContentColor = MaterialTheme.colorSchemeExtended.success.onColorContainer,
        )
      }

      Info -> {
        CustomSnackbarColors(
            containerColor = MaterialTheme.colorSchemeExtended.success.colorContainer,
            contentColor = MaterialTheme.colorSchemeExtended.success.onColorContainer,
            actionColor = MaterialTheme.colorSchemeExtended.success.onColorContainer,
            actionContentColor = MaterialTheme.colorSchemeExtended.success.onColorContainer,
            dismissActionContentColor = MaterialTheme.colorSchemeExtended.success.onColorContainer,
        )
      }

      Warning -> {
        CustomSnackbarColors(
            containerColor = MaterialTheme.colorSchemeExtended.warning.colorContainer,
            contentColor = MaterialTheme.colorSchemeExtended.warning.onColorContainer,
            actionColor = MaterialTheme.colorSchemeExtended.warning.onColorContainer,
            actionContentColor = MaterialTheme.colorSchemeExtended.warning.onColorContainer,
            dismissActionContentColor = MaterialTheme.colorSchemeExtended.warning.onColorContainer,
        )
      }

      Error -> {
        CustomSnackbarColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            actionColor = MaterialTheme.colorScheme.onErrorContainer,
            actionContentColor = MaterialTheme.colorScheme.onErrorContainer,
            dismissActionContentColor = MaterialTheme.colorScheme.onErrorContainer,
        )
      }
    }
