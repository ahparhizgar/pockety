package com.base.permission

import com.base.Fake
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface PermissionController {
  val isSmsPermissionGranted: StateFlow<Boolean>

  suspend fun requestNotificationPermission(): Boolean

  suspend fun requestReceiveSmsPermission(): Boolean
}

@Fake
class FakePermissionController(
    initialSmsPermissionGranted: Boolean = false,
) : PermissionController {
  private val _isSmsPermissionGranted = MutableStateFlow(initialSmsPermissionGranted)
  override val isSmsPermissionGranted: StateFlow<Boolean> = _isSmsPermissionGranted.asStateFlow()

  var requestReceiveSmsPermissionCallCount: Int = 0
    private set

  var onRequestReceiveSmsPermission: (suspend () -> Boolean)? = null

  var onRequestNotificationPermission: (suspend () -> Boolean)? = null

  override suspend fun requestNotificationPermission(): Boolean =
      onRequestNotificationPermission?.invoke() ?: true

  fun setSmsPermissionGranted(granted: Boolean) {
    _isSmsPermissionGranted.value = granted
  }

  override suspend fun requestReceiveSmsPermission(): Boolean {
    requestReceiveSmsPermissionCallCount++
    val customResult = onRequestReceiveSmsPermission?.invoke()
    return if (customResult != null) {
      _isSmsPermissionGranted.value = customResult
      customResult
    } else {
      _isSmsPermissionGranted.value
    }
  }
}
