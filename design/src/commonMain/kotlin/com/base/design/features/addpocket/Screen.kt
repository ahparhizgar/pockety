package com.base.design.features.addpocket

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.design.features.pocketsettings.MonthlyAllocationCard
import com.base.design.features.pocketsettings.PocketIdentitySection
import com.base.design.globalcomponents.AccentColorPicker
import com.base.design.globalcomponents.IconPickerSection
import com.base.design.globalcomponents.PocketyBottomActionBar
import com.base.design.globalcomponents.PocketyTopAppBar
import com.base.design.globalcomponents.defaultIconCategoryFilters
import com.base.design.globalcomponents.defaultItemColors
import com.base.design.globalcomponents.defaultItemIcons
import com.base.design.ui.ScreenPreviews

/**
 * New-pocket creation with two modes:
 * - Category mode (default): pick one category and the pocket inherits its name, icon and color.
 * - Manual mode (tucked behind a quiet toggle): the full classic form, plus linking several
 *   categories to one pocket.
 */
@Composable
fun AddPocketScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    initialCategoryId: String? = null,
    manualExpanded: Boolean = false,
    initialLinkedCategoryIds: Set<String> = emptySet(),
    currencySymbol: String = "T",
    modifier: Modifier = Modifier,
) {
  var selectedCategory by remember {
    mutableStateOf(samplePocketCategories.firstOrNull { it.id == initialCategoryId })
  }
  var manual by remember { mutableStateOf(manualExpanded) }
  var pocketName by remember { mutableStateOf(selectedCategory?.name ?: "") }
  var selectedIcon by remember {
    mutableStateOf(
        selectedCategory?.let { cat -> defaultItemIcons.firstOrNull { it.icon == cat.icon } }
            ?: defaultItemIcons.first()
    )
  }
  var selectedColor by remember {
    mutableStateOf(selectedCategory?.color ?: defaultItemColors.first())
  }
  var selectedIconFilter by remember { mutableStateOf("All") }
  var linkedIds by remember { mutableStateOf(initialLinkedCategoryIds) }
  var allocatedAmount by remember { mutableStateOf("1,500,000") }
  var autoRefillEnabled by remember { mutableStateOf(true) }

  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = { PocketyTopAppBar(title = "New Pocket", onBackClick = onBackClick) },
      bottomBar = { PocketyBottomActionBar(text = "Save", onClick = onSaveClick) },
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
                  .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        if (!manual) {
          Text(
              text = "Start from a category \u2014 the pocket takes its name, icon and color.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
          )

          if (selectedCategory != null) {
            PocketIdentitySection(
                pocketName = pocketName,
                onPocketNameChange = { pocketName = it },
                icon = selectedIcon.icon,
                containerColor = selectedColor.color,
                contentColor = selectedColor.onColor,
            )
            InheritedFromCategoryNote(categoryName = selectedCategory!!.name)
          }

          AddPocketSectionTitle(text = "Category")
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            samplePocketCategories.forEach { category ->
              AddPocketCategoryRow(
                  category = category,
                  isSelected = selectedCategory?.id == category.id,
                  onClick = {
                    selectedCategory = category
                    pocketName = category.name
                    selectedColor = category.color
                    selectedIcon =
                        defaultItemIcons.firstOrNull { it.icon == category.icon } ?: selectedIcon
                  },
              )
            }
          }

          MonthlyAllocationCard(
              allocatedAmount = allocatedAmount,
              currencySymbol = currencySymbol,
              autoRefillEnabled = autoRefillEnabled,
              onAutoRefillChange = { autoRefillEnabled = it },
              onTuneClick = {},
          )
        } else {
          PocketIdentitySection(
              pocketName = pocketName,
              onPocketNameChange = { pocketName = it },
              icon = selectedIcon.icon,
              containerColor = selectedColor.color,
              contentColor = selectedColor.onColor,
          )

          AddPocketSectionTitle(text = "Linked categories")
          Text(
              text = "Expenses in these categories count toward this pocket.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            samplePocketCategories.forEach { category ->
              LinkedCategoryRow(
                  category = category,
                  isLinked = category.id in linkedIds,
                  onClick = {
                    linkedIds =
                        if (category.id in linkedIds) linkedIds - category.id
                        else linkedIds + category.id
                  },
              )
            }
          }

          AccentColorPicker(
              colors = defaultItemColors,
              selectedColor = selectedColor,
              onColorSelected = { selectedColor = it },
          )

          IconPickerSection(
              icons = defaultItemIcons,
              selectedIcon = selectedIcon,
              onIconSelected = { selectedIcon = it },
              filters = defaultIconCategoryFilters,
              selectedFilter = selectedIconFilter,
              onFilterSelected = { selectedIconFilter = it },
              accentColor = selectedColor.color,
          )

          MonthlyAllocationCard(
              allocatedAmount = allocatedAmount,
              currencySymbol = currencySymbol,
              autoRefillEnabled = autoRefillEnabled,
              onAutoRefillChange = { autoRefillEnabled = it },
              onTuneClick = {},
          )
        }

        ManualSetupToggle(
            expanded = manual,
            onToggle = { manual = !manual },
        )

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

/** Close-up of the quiet manual-setup row in both states, for design review. */
@Composable
fun ManualSetupToggleShowcase(modifier: Modifier = Modifier) {
  Column(
      modifier =
          modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp, vertical = 24.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    AddPocketSectionTitle(text = "Collapsed (at the bottom of the screen)")
    ManualSetupToggle(expanded = false, onToggle = {})
    Spacer(modifier = Modifier.height(12.dp))
    AddPocketSectionTitle(text = "Expanded")
    ManualSetupToggle(expanded = true, onToggle = {})
  }
}

@ScreenPreviews
@Composable
fun AddPocketScreenCategoryPreview() {
  AddPocketScreen()
}

@ScreenPreviews
@Composable
fun AddPocketScreenCategorySelectedPreview() {
  AddPocketScreen(initialCategoryId = "cafe")
}

@ScreenPreviews
@Composable
fun AddPocketScreenManualPreview() {
  AddPocketScreen(
      manualExpanded = true,
      initialLinkedCategoryIds = setOf("cafe", "groceries"),
  )
}
