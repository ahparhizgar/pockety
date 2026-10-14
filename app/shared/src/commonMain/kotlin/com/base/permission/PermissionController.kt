package com.base.permission

import com.base.Fake
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface PermissionController {
  val isSmsPermissionGranted: StateFlow<Boolean>

  fun requestReceiveSmsPermission()
}

@Fake
class FakePermissionController(
    initialSmsPermissionGranted: Boolean = false,
) : PermissionController {
  private val _isSmsPermissionGranted = MutableStateFlow(initialSmsPermissionGranted)
  override val isSmsPermissionGranted: StateFlow<Boolean> = _isSmsPermissionGranted.asStateFlow()

  var onRequestReceiveSmsPermission: (() -> Unit)? = null

  fun setSmsPermissionGranted(granted: Boolean) {
    _isSmsPermissionGranted.value = granted
  }

  override fun requestReceiveSmsPermission() {
    onRequestReceiveSmsPermission?.invoke()
  }
}
