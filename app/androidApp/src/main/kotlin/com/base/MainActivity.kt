package com.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.retainedComponent
import com.base.component.defaultCContext
import com.base.permission.AndroidPermissionController
import com.base.permission.PermissionController
import com.base.capability.root_navigation.FakeRootComponent
import com.base.capability.root_navigation.RootComponentFactory
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
  private val permissionController: PermissionController by inject()

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

    setContent { RootScreen(root) }
  }

  override fun onResume() {
    super.onResume()
    (permissionController as? AndroidPermissionController)?.attach(this, permissionLauncher)
  }

  override fun onDestroy() {
    super.onDestroy()
    (permissionController as? AndroidPermissionController)?.detach()
  }
}

@Preview
@Composable
private fun RootScreenAndroidPreview() {
  RootScreen(FakeRootComponent())
}
