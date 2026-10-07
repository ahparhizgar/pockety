package com.base

import androidx.compose.ui.window.ComposeUIViewController
import com.base.feature.root.RootComponent

fun MainViewController(root: RootComponent) = ComposeUIViewController { RootScreen(root) }
