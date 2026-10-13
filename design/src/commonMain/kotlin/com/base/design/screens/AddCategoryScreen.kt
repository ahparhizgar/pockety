@file:Suppress(
    "LongMethod",
    "LongParameterList",
    "TooManyFunctions",
    "MagicNumber",
    "MatchingDeclarationName",
    "SpreadOperator",
    "MaxLineLength",
    "UnusedPrivateFunction",
    "ComposableParamOrder",
    "PreviewNaming",
    "PreviewPublic",
    "ParameterNaming",
    "UnstableCollections",
)

package com.base.design.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.base.design.components.PocketyTopBar
import com.base.design.ui.ScreenPreviews
import kotlinx.coroutines.launch

/**
 * Screen 13: AddCategoryScreen for Pockety.
 *
 * Provides a Delightful Category Creation experience with Live Preview, Name input, Accent Color
 * selector, categorized icon picker, and action button.
 */
@Composable
fun AddCategoryScreen(
    modifier: Modifier = Modifier,
    initialName: String = "Dining Out",
    initialColor: CategoryColorOption = defaultCategoryColors.first(),
    initialIcon: CategoryIconOption = defaultCategoryIcons.first(),
    onBackClick: () -> Unit = {},
    onSaveCategory: (String, CategoryColorOption, CategoryIconOption) -> Unit = { _, _, _ -> },
    onProfileClick: () -> Unit = {},
) {
  var categoryName by remember { mutableStateOf(initialName) }
  var selectedColor by remember { mutableStateOf(initialColor) }
  var selectedIcon by remember { mutableStateOf(initialIcon) }
  var selectedFilter by remember { mutableStateOf("All") }

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val focusManager = LocalFocusManager.current

  val handleSave: () -> Unit = {
    focusManager.clearFocus()
    onSaveCategory(categoryName, selectedColor, selectedIcon)
    scope.launch { snackbarHostState.showSnackbar("Category created successfully!") }
  }

  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopBar(
            title = "Add a Category",
            navigationIcon = {
              IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
              }
            },
        )
      },
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
  ) { innerPadding ->
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
      Column(
          modifier =
              Modifier.fillMaxWidth()
                  .widthIn(max = 640.dp)
                  .verticalScroll(rememberScrollState())
                  .padding(horizontal = 20.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        // Hero / Live Preview
        CategoryLivePreviewCard(
            categoryName = categoryName,
            selectedIcon = selectedIcon.icon,
            selectedColor = selectedColor.color,
            onColor = selectedColor.onColor,
        )

        // Category Name Input Field
        CategoryNameInputField(
            name = categoryName,
            onNameChange = { categoryName = it },
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                ),
            keyboardActions = KeyboardActions(onDone = { handleSave() }),
        )

        // Accent Color Section
        AccentColorPicker(
            colors = defaultCategoryColors,
            selectedColor = selectedColor,
            onColorSelected = { selectedColor = it },
        )

        // Select Icon Section with category filters and 5-column grid
        CategoryIconSection(
            icons = defaultCategoryIcons,
            selectedIcon = selectedIcon,
            onIconSelected = { selectedIcon = it },
            filters = defaultCategoryFilters,
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it },
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Save Category Button
        SaveCategoryButton(
            onClick = handleSave,
            text = "Save Category",
            enabled = categoryName.isNotBlank(),
        )

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

// ==================== Screen Previews ====================

@ScreenPreviews
@Composable
private fun AddCategoryScreenPreview() {
  AddCategoryScreen()
}

@ScreenPreviews
@Composable
private fun AddCategoryScreenCustomStatePreview() {
  AddCategoryScreen(
      initialName = "Groceries & Supermarket",
      initialColor = defaultCategoryColors[1], // Emerald
      initialIcon = defaultCategoryIcons[1], // Shopping Cart
  )
}
