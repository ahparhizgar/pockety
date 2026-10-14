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
import java.lang.ref.WeakReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidPermissionController(
    private val context: Context,
) : PermissionController {

  private val _isSmsPermissionGranted = MutableStateFlow(checkIsSmsPermissionGranted(context))
  override val isSmsPermissionGranted: StateFlow<Boolean> = _isSmsPermissionGranted.asStateFlow()

  private var activityRef: WeakReference<ComponentActivity>? = null
  private var launcherRef: ActivityResultLauncher<String>? = null

  fun attach(activity: ComponentActivity, launcher: ActivityResultLauncher<String>) {
    activityRef = WeakReference(activity)
    launcherRef = launcher
    refreshPermissionState()
  }

  fun detach() {
    activityRef = null
    launcherRef = null
  }

  fun refreshPermissionState() {
    val granted = checkIsSmsPermissionGranted(context)
    _isSmsPermissionGranted.value = granted
  }

  fun onPermissionResult(granted: Boolean) {
    _isSmsPermissionGranted.value = granted
    if (!granted) {
      val activity = activityRef?.get()
      if (activity != null) {
        val shouldShowRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.RECEIVE_SMS,
            )
        // If permission was denied and rationale is false right after the request result,
        // it means the user permanently denied it ("Don't ask again") or the OS auto-rejected it.
        if (!shouldShowRationale) {
          openAppSettings()
        }
      }
    }
  }

  override fun requestReceiveSmsPermission() {
    val granted = checkIsSmsPermissionGranted(context)
    if (granted) {
      _isSmsPermissionGranted.value = true
      return
    }

    val launcher = launcherRef
    if (launcher != null) {
      launcher.launch(Manifest.permission.RECEIVE_SMS)
    } else {
      openAppSettings()
    }
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
    fun checkIsSmsPermissionGranted(context: Context): Boolean {
      return ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.RECEIVE_SMS,
      ) == PackageManager.PERMISSION_GRANTED
    }
  }
}
