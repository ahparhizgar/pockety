package com.base.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.base.feature.item.ItemListScreen
import com.base.ui.BaseTheme
import com.base.ui.ScreenPreviews

@Composable
fun MainScreen(
    component: MainComponent,
    modifier: Modifier = Modifier,
) {
  ItemListScreen(
      component = component.itemListComponent,
      modifier = modifier,
  )
}

@Composable
@ScreenPreviews
@Suppress("UnusedPrivateFunction")
private fun MainScreenPreview() {
  BaseTheme { MainScreen(FakeMainComponent()) }
}
