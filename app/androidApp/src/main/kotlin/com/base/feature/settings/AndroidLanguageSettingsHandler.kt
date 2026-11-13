package com.base.feature.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

class AndroidLanguageSettingsHandler(
    private val context: Context,
) : LanguageSettingsHandler {

  override val supportsSystemLocaleSettings: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

  override fun openSystemLocaleSettings() {
    if (supportsSystemLocaleSettings) {
      val intent =
          Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
      context.startActivity(intent)
    }
  }
}
