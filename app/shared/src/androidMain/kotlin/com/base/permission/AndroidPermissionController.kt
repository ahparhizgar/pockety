package com.base.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
  private var launcherRef: ActivityResultLauncher<Array<String>>? = null
  private var notificationLauncher: ActivityResultLauncher<String>? = null
  private var pendingNotification: CompletableDeferred<Boolean>? = null
  private var pendingResult: CompletableDeferred<Boolean>? = null

  fun attach(
      activity: ComponentActivity,
      launcher: ActivityResultLauncher<Array<String>>,
      notificationLauncher: ActivityResultLauncher<String>,
  ) {
    this.notificationLauncher = notificationLauncher
    activityRef = WeakReference(activity)
    launcherRef = launcher
    refreshPermissionState()
  }

  fun detach() {
    activityRef = null
    launcherRef = null
    notificationLauncher = null
    pendingNotification?.cancel()
    pendingNotification = null
    pendingResult?.cancel()
    pendingResult = null
  }

  fun refreshPermissionState() {
    val granted = checkIsSmsPermissionGranted(context)
    _isSmsPermissionGranted.value = granted
  }

  fun onPermissionResult() {
    val granted = checkIsSmsPermissionGranted(context)
    _isSmsPermissionGranted.value = granted
    pendingResult?.complete(granted)
    pendingResult = null
  }

  fun onNotificationPermissionResult(granted: Boolean) {
    pendingNotification?.complete(granted)
    pendingNotification = null
  }

  override suspend fun requestNotificationPermission(): Boolean {
    if (
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    )
        return true
    return pendingNotification?.await() ?: launchNotificationPermissionRequest()
  }

  private suspend fun launchNotificationPermissionRequest(): Boolean {
    val launcher = notificationLauncher
    return if (prefs.getBoolean(KEY_NOTIFICATION_REQUESTED, false) || launcher == null) {
      false
    } else {
      prefs.edit { putBoolean(KEY_NOTIFICATION_REQUESTED, true) }
      val result = CompletableDeferred<Boolean>()
      pendingNotification = result
      launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
      result.await()
    }
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
            listOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS).any {
              ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED &&
                  prefs.getBoolean("requested_$it", false) &&
                  !ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
            }

    if (isPermanentlyDenied || launcher == null) {
      openAppSettings()
      return false
    }

    prefs.edit {
      putBoolean(KEY_SMS_REQUESTED_BEFORE, true)
      putBoolean("requested_${Manifest.permission.RECEIVE_SMS}", true)
      putBoolean("requested_${Manifest.permission.READ_SMS}", true)
    }
    val deferred = CompletableDeferred<Boolean>()
    pendingResult = deferred
    launcher.launch(arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS))
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
    private const val KEY_NOTIFICATION_REQUESTED = "notification_permission_requested"
    private const val KEY_SMS_REQUESTED_BEFORE = "sms_permission_requested_before"

    fun checkIsSmsPermissionGranted(context: Context): Boolean {
      return listOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS).all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
      }
    }
  }
}
