package com.base.usermessage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.base.ui.ComponentPreviews
import com.base.ui.colorSchemeExtended
import com.base.usermessage.UserMessage.Tone.Error
import com.base.usermessage.UserMessage.Tone.Info
import com.base.usermessage.UserMessage.Tone.Success
import com.base.usermessage.UserMessage.Tone.Warning
import kotlin.coroutines.resume
import kotlin.math.roundToInt
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.dismiss_action

private const val SHAKE_REPEAT_COUNT = 3
private const val SHAKE_OFFSET = 10f
private const val SHAKE_DURATION_MILLIS = 80

enum class SnackbarResult {
  Dismissed,
  ActionPerformed,
}

interface SnackbarData {
  val message: SnackbarMessage

  fun performAction()

  fun dismiss()
}

@Stable
class SnackbarHostState {
  private val mutex = Mutex()

  var currentSnackbarData: SnackbarData? by mutableStateOf(null)
    private set

  suspend fun showSnackbar(message: SnackbarMessage): SnackbarResult = mutex.withLock {
    try {
      return suspendCancellableCoroutine { continuation ->
        currentSnackbarData = SnackbarDataImpl(message, continuation)
      }
    } finally {
      currentSnackbarData = null
    }
  }

  private class SnackbarDataImpl(
      override val message: SnackbarMessage,
      private val continuation: CancellableContinuation<SnackbarResult>,
  ) : SnackbarData {
    override fun performAction() {
      if (continuation.isActive) continuation.resume(SnackbarResult.ActionPerformed)
    }

    override fun dismiss() {
      if (continuation.isActive) continuation.resume(SnackbarResult.Dismissed)
    }
  }
}

@Composable
fun SnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackbarData) -> Unit = { CustomSnackbar(snackbarData = it) },
) {
  val currentData = hostState.currentSnackbarData
  LaunchedEffect(currentData) {
    if (currentData != null) {
      val duration = currentData.message.duration
      if (duration != null) {
        delay(duration)
        currentData.dismiss()
      }
    }
  }

  AnimatedVisibility(
      visible = currentData != null,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = modifier,
  ) {
    currentData?.let { snackbar(it) }
  }
}

@Composable
fun CustomSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
) {
  val message = snackbarData.message
  val offsetX = remember { Animatable(0f) }

  if (message.shouldShake) {
    LaunchedEffect(message) {
      repeat(SHAKE_REPEAT_COUNT) {
        offsetX.animateTo(SHAKE_OFFSET, tween(durationMillis = SHAKE_DURATION_MILLIS))
        offsetX.animateTo(-SHAKE_OFFSET, tween(durationMillis = SHAKE_DURATION_MILLIS))
      }
      offsetX.animateTo(0f)
    }
  }

  val colors = getColors(message.tone)
  Surface(
      modifier =
          modifier.fillMaxWidth().padding(16.dp).offset {
            IntOffset(offsetX.value.roundToInt(), 0)
          },
      shape = RoundedCornerShape(8.dp),
      color = colors.containerColor,
      contentColor = colors.contentColor,
      shadowElevation = 6.dp,
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
          modifier = Modifier.weight(1f).padding(vertical = 12.dp),
          text = message.content.content(),
          style = MaterialTheme.typography.bodyMedium,
          color = colors.contentColor,
      )

      val actionLabel = message.actionLabel
      if (actionLabel != null) {
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = { snackbarData.performAction() }) {
          Text(text = actionLabel.content(), color = colors.actionColor)
        }
      }

      if (message.withDismissAction) {
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = { snackbarData.dismiss() }) {
          Icon(
              imageVector = Icons.Default.Close,
              contentDescription = stringResource(Res.string.dismiss_action),
              tint = colors.dismissActionContentColor,
          )
        }
      }
    }
  }
}

private data class CustomSnackbarColors(
    val containerColor: Color,
    val contentColor: Color,
    val actionColor: Color,
    val dismissActionContentColor: Color,
)

@Composable
private fun getColors(tone: UserMessage.Tone): CustomSnackbarColors =
    when (tone) {
      Info -> {
        CustomSnackbarColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            actionColor = MaterialTheme.colorScheme.inverseOnSurface,
            dismissActionContentColor = MaterialTheme.colorScheme.inverseOnSurface,
        )
      }
      Success -> {
        CustomSnackbarColors(
            containerColor = MaterialTheme.colorSchemeExtended.success.colorContainer,
            contentColor = MaterialTheme.colorSchemeExtended.success.onColorContainer,
            actionColor = MaterialTheme.colorSchemeExtended.success.onColorContainer,
            dismissActionContentColor = MaterialTheme.colorSchemeExtended.success.onColorContainer,
        )
      }

      Warning -> {
        CustomSnackbarColors(
            containerColor = MaterialTheme.colorSchemeExtended.warning.colorContainer,
            contentColor = MaterialTheme.colorSchemeExtended.warning.onColorContainer,
            actionColor = MaterialTheme.colorSchemeExtended.warning.onColorContainer,
            dismissActionContentColor = MaterialTheme.colorSchemeExtended.warning.onColorContainer,
        )
      }

      Error -> {
        CustomSnackbarColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            actionColor = MaterialTheme.colorScheme.onErrorContainer,
            dismissActionContentColor = MaterialTheme.colorScheme.onErrorContainer,
        )
      }
    }

@ComponentPreviews
@Composable
private fun CustomSnackbarPreview() {
  CustomSnackbar(
      snackbarData =
          object : SnackbarData {
            override val message =
                SnackbarMessage(
                    content = UiString("An error occurred"),
                    tone = Error,
                    actionLabel = UiString("Retry"),
                )

            override fun performAction() {}

            override fun dismiss() {}
          }
  )
}
