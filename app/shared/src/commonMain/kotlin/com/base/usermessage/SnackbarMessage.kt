package com.base.usermessage

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import com.base.usermessage.UserMessage.DismissMode
import com.base.usermessage.UserMessage.DismissMode.AutoAndManual
import com.base.usermessage.UserMessage.DismissMode.AutoOnly
import com.base.usermessage.UserMessage.DismissMode.Manual

data class SnackbarMessage(
    val content: String,
    val tone: UserMessage.Tone = UserMessage.Tone.Info,
    val dismissMode: DismissMode = AutoAndManual(),
    val actionLabel2: UiString? = null,
    var shouldShake: Boolean = false,
    override var actionLabel: String? = null,
    val uiContent: UiString = UiString(content),
) : SnackbarVisuals {
  constructor(
      content: UiString,
      tone: UserMessage.Tone = UserMessage.Tone.Info,
      dismissMode: DismissMode = AutoAndManual(),
      actionLabel2: UiString? = null,
      shouldShake: Boolean = false,
      actionLabel: String? = null,
  ) : this(
      content = (content as? UiString.StringLiteral)?.value ?: "",
      tone = tone,
      dismissMode = dismissMode,
      actionLabel2 = actionLabel2,
      shouldShake = shouldShake,
      actionLabel = actionLabel,
      uiContent = content,
  )

  override val message: String
    get() = content

  override val withDismissAction: Boolean
    get() =
        when (dismissMode) {
          is Manual -> true
          is AutoAndManual -> true
          is AutoOnly -> false
        }

  override val duration: SnackbarDuration
    get() =
        when (dismissMode) {
          is Manual -> SnackbarDuration.Indefinite
          is AutoAndManual -> dismissMode.duration.toSnackbarDuration()
          is AutoOnly -> dismissMode.duration.toSnackbarDuration()
        }
}

private fun UserMessage.Duration.toSnackbarDuration(): SnackbarDuration =
    when (this) {
      UserMessage.Duration.Short -> SnackbarDuration.Short
      UserMessage.Duration.Long -> SnackbarDuration.Long
    }
