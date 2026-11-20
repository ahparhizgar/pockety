package com.base

import android.app.LocaleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.retainedComponent
import com.base.component.defaultCContext
import com.base.feature.root.FakeRootComponent
import com.base.feature.root.RootComponentFactory
import com.base.feature.settings.AppLanguage
import com.base.feature.settings.UserSettingsRepository
import com.base.navigation.NavigationAction
import com.base.navigation.NavigationActionBus
import com.base.permission.AndroidPermissionController
import com.base.permission.PermissionController
import com.base.ui.ScreenPreviews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
  private val permissionController: PermissionController by inject()
  private val navigationActionBus: NavigationActionBus by inject()
  private val userSettingsRepository: UserSettingsRepository by inject()

  private val permissionLauncher =
      registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        (permissionController as? AndroidPermissionController)?.onPermissionResult()
      }

  private val notificationPermissionLauncher =
      registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        (permissionController as? AndroidPermissionController)?.onNotificationPermissionResult(
            granted
        )
      }

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    (permissionController as? AndroidPermissionController)?.attach(
        this,
        permissionLauncher,
        notificationPermissionLauncher,
    )

    val factory: RootComponentFactory by inject()
    val root = retainedComponent { factory.create(defaultCContext(it, permissionController)) }

    handleIntent(intent)
    syncSystemLocale()

    setContent { RootScreen(root) }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleIntent(intent)
  }

  private fun handleIntent(intent: Intent?) {
    when (intent?.action) {
      ACTION_OPEN_ADD_EXPENSE -> {
        val candidateId = intent.getLongExtra(EXTRA_NAV_CANDIDATE_ID, -1L)
        val amount = intent.getStringExtra(EXTRA_NAV_AMOUNT)
        if (candidateId != -1L && !amount.isNullOrBlank()) {
          navigationActionBus.emit(
              NavigationAction.OpenAddExpense(
                  candidateExpenseId = candidateId,
                  initialAmount = amount,
              )
          )
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    (permissionController as? AndroidPermissionController)?.attach(
        this,
        permissionLauncher,
        notificationPermissionLauncher,
    )
    syncSystemLocale()
  }

  override fun onDestroy() {
    super.onDestroy()
    (permissionController as? AndroidPermissionController)?.detach()
  }

  private fun syncSystemLocale() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val localeManager = getSystemService(LocaleManager::class.java)
      val locales = localeManager?.applicationLocales
      if (locales != null && !locales.isEmpty) {
        val primaryLocale = locales.get(0)
        val languageCode = primaryLocale?.language
        if (languageCode != null) {
          val appLanguage = AppLanguage.fromCode(languageCode)
          CoroutineScope(Dispatchers.Main).launch {
            userSettingsRepository.setAppLanguage(appLanguage)
          }
        }
      }
    }
  }

  companion object {
    const val ACTION_OPEN_ADD_EXPENSE = "com.base.action.OPEN_ADD_EXPENSE"
    const val EXTRA_NAV_CANDIDATE_ID = "extra_nav_candidate_id"
    const val EXTRA_NAV_AMOUNT = "extra_nav_amount"
  }
}

@ScreenPreviews
@Composable
private fun RootScreenAndroidPreview() {
  RootScreen(FakeRootComponent())
}
