package com.base.feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.base.ui.BaseTheme
import com.base.ui.ScreenPreviews

@Composable
fun MainScreen(
    @Suppress("UnusedParameter") component: MainComponent,
    modifier: Modifier = Modifier,
) {
  Surface(modifier = modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
      Text("Hello, World!", style = MaterialTheme.typography.headlineMedium)
    }
  }
}

@Composable
@ScreenPreviews
@Suppress("UnusedPrivateFunction")
private fun MainScreenPreview() {
  BaseTheme { MainScreen(FakeMainComponent()) }
}
