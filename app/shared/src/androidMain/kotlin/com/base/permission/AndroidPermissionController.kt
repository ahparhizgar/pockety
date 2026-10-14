package com.base.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import java.lang.ref.WeakReference
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidPermissionController(
    private val context: Context,
) : PermissionController {

  private val prefs = context.getSharedPreferences("sms_permission_prefs", Context.MODE_PRIVATE)

  private val _isSmsPermissionGranted = MutableStateFlow(checkIsSmsPermissionGranted(context))
  override val isSmsPermissionGranted: StateFlow<Boolean> = _isSmsPermissionGranted.asStateFlow()

  private var activityRef: WeakReference<ComponentActivity>? = null
  private var launcherRef: ActivityResultLauncher<String>? = null
  private var pendingResult: CompletableDeferred<Boolean>? = null

  fun attach(activity: ComponentActivity, launcher: ActivityResultLauncher<String>) {
    activityRef = WeakReference(activity)
    launcherRef = launcher
    refreshPermissionState()
  }

  fun detach() {
    activityRef = null
    launcherRef = null
    pendingResult?.cancel()
    pendingResult = null
  }

  fun refreshPermissionState() {
    val granted = checkIsSmsPermissionGranted(context)
    _isSmsPermissionGranted.value = granted
  }

  fun onPermissionResult(granted: Boolean) {
    _isSmsPermissionGranted.value = granted
    pendingResult?.complete(granted)
    pendingResult = null
  }

  override suspend fun requestReceiveSmsPermission(): Boolean {
    if (checkIsSmsPermissionGranted(context)) {
      _isSmsPermissionGranted.value = true
      return true
    }

    return performPermissionRequest()
  }

  private suspend fun performPermissionRequest(): Boolean {
    val activity = activityRef?.get()
    val launcher = launcherRef
    val wasRequestedBefore = prefs.getBoolean(KEY_SMS_REQUESTED_BEFORE, false)

    val isPermanentlyDenied =
        activity != null &&
            wasRequestedBefore &&
            !ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.RECEIVE_SMS,
            )

    if (isPermanentlyDenied || launcher == null) {
      openAppSettings()
      return false
    }

    prefs.edit { putBoolean(KEY_SMS_REQUESTED_BEFORE, true) }
    val deferred = CompletableDeferred<Boolean>()
    pendingResult = deferred
    launcher.launch(Manifest.permission.RECEIVE_SMS)
    return deferred.await()
  }

  private fun openAppSettings() {
    val settingsIntent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
          data = Uri.fromParts("package", context.packageName, null)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    context.startActivity(settingsIntent)
  }

  companion object {
    private const val KEY_SMS_REQUESTED_BEFORE = "sms_permission_requested_before"

    fun checkIsSmsPermissionGranted(context: Context): Boolean {
      return ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.RECEIVE_SMS,
      ) == PackageManager.PERMISSION_GRANTED
    }
  }
}
