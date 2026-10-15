package com.base.capability.category_management

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.base.ui.ScreenPreviews

@Composable
fun CategorySelectionScreen(
    component: CategorySelectionComponent,
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier.fillMaxSize().testTag("category-selection-screen"),
      contentAlignment = Alignment.Center,
  ) {
    Text("Category Selection Screen Stub")
  }
}

@ScreenPreviews
@Composable
private fun CategorySelectionScreenPreview() {
  CategorySelectionScreen(FakeCategorySelectionComponent())
}
