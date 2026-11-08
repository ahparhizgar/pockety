package com.base.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester

@Composable
internal fun RequestFocusOnce(focusRequester: FocusRequester) {
  var hasRequestedFocus by remember { mutableStateOf(false) }
  SideEffect {
    if (!hasRequestedFocus) {
      focusRequester.requestFocus()
      hasRequestedFocus = true
    }
  }
}
