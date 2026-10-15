package com.base

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.retainedComponent
import com.base.capability.root_navigation.FakeRootComponent
import com.base.capability.root_navigation.RootComponentFactory
import com.base.component.defaultCContext
import com.base.navigation.NavigationAction
import com.base.navigation.NavigationActionBus
import com.base.permission.AndroidPermissionController
import com.base.permission.PermissionController
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
  private val permissionController: PermissionController by inject()
  private val navigationActionBus: NavigationActionBus by inject()

  private val permissionLauncher =
      registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        (permissionController as? AndroidPermissionController)?.onPermissionResult(granted)
      }

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    (permissionController as? AndroidPermissionController)?.attach(this, permissionLauncher)

    val factory: RootComponentFactory by inject()
    val root = retainedComponent { factory.create(defaultCContext(it, permissionController)) }

    handleIntent(intent)

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
    (permissionController as? AndroidPermissionController)?.attach(this, permissionLauncher)
  }

  override fun onDestroy() {
    super.onDestroy()
    (permissionController as? AndroidPermissionController)?.detach()
  }

  companion object {
    const val ACTION_OPEN_ADD_EXPENSE = "com.base.action.OPEN_ADD_EXPENSE"
    const val EXTRA_NAV_CANDIDATE_ID = "extra_nav_candidate_id"
    const val EXTRA_NAV_AMOUNT = "extra_nav_amount"
  }
}

@Preview
@Composable
private fun RootScreenAndroidPreview() {
  RootScreen(FakeRootComponent())
}
