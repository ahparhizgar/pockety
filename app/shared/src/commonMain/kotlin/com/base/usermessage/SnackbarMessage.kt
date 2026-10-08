package com.base.usermessage

import com.base.usermessage.UserMessage.DismissMode
import com.base.usermessage.UserMessage.DismissMode.AutoAndManual
import com.base.usermessage.UserMessage.DismissMode.AutoOnly
import com.base.usermessage.UserMessage.DismissMode.Manual
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class SnackbarMessage(
    val content: UiString,
    val tone: UserMessage.Tone = UserMessage.Tone.Info,
    val dismissMode: DismissMode = AutoAndManual(),
    val actionLabel: UiString? = null,
    var shouldShake: Boolean = false,
) {
  constructor(
      content: String,
      tone: UserMessage.Tone = UserMessage.Tone.Info,
      dismissMode: DismissMode = AutoAndManual(),
      actionLabel: UiString? = null,
      shouldShake: Boolean = false,
  ) : this(
      content = UiString(content),
      tone = tone,
      dismissMode = dismissMode,
      actionLabel = actionLabel,
      shouldShake = shouldShake,
  )

  val withDismissAction: Boolean
    get() =
        when (dismissMode) {
          is Manual -> true
          is AutoAndManual -> true
          is AutoOnly -> false
        }

  val duration: Duration?
    get() =
        when (dismissMode) {
          is Manual -> null
          is AutoAndManual -> dismissMode.duration.toDuration()
          is AutoOnly -> dismissMode.duration.toDuration()
        }
}

private fun UserMessage.Duration.toDuration(): Duration =
    when (this) {
      UserMessage.Duration.Short -> 4.seconds
      UserMessage.Duration.Long -> 10.seconds
    }
