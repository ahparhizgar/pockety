package com.base

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DecomposeSettings
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.base.component.DefaultCContext
import com.base.di.appModule
import com.base.feature.root.RootComponentFactory
import javax.swing.SwingUtilities
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

fun main(vararg args: String) {
  val lifecycle = LifecycleRegistry()
  println(args.joinToString())
  val koin = startKoin {
    modules(appModule())
    modules(module { single { DataDir(".") } })
  }
      .koin

  val backDispatcher = BackDispatcher()
  val context = DefaultCContext(lifecycle = lifecycle, backHandler = backDispatcher)
  val root = runOnUiThread {
    koin.get<RootComponentFactory>().create(context)
  }
  DecomposeSettings.settings = DecomposeSettings(duplicateConfigurationsEnabled = true)

  application {
    val windowState = rememberWindowState()
    LifecycleController(lifecycle, windowState)
    Window(
        onCloseRequest = ::exitApplication,
        title = "Base App",
        onKeyEvent = {
          if (it.key in setOf(Key.Escape, Key.Back) && it.type == KeyEventType.KeyDown) {
            backDispatcher.back()
            true
          } else {
            false
          }
        },
        state = windowState,
    ) {
      MenuBar {
        Menu("Navigation") {
          Item("Back", shortcut = KeyShortcut(Key.Escape)) { backDispatcher.back() }
        }
      }
      RootScreen(root = root)
    }
  }
}

internal fun <T> runOnUiThread(block: () -> T): T {
  if (SwingUtilities.isEventDispatchThread()) {
    return block()
  }

  var error: Throwable? = null
  var result: T? = null

  SwingUtilities.invokeAndWait {
    try {
      result = block()
    } catch (@Suppress("TooGenericExceptionCaught") e: Throwable) {
      error = e
    }
  }

  error?.also { throw it }

  @Suppress("UNCHECKED_CAST")
  return result as T
}
