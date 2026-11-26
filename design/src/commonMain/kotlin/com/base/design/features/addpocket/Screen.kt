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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
 * Step 1 of new-pocket creation: pick zero or more categories for the pocket.
 *
 * The pick decides the next step: exactly one category leads to the simple setup (the pocket
 * follows that category), while zero or several lead to the advanced setup.
 */
@Composable
fun SelectPocketCategoriesScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    initialSelectedIds: Set<String> = emptySet(),
    modifier: Modifier = Modifier,
) {
  var selectedIds by remember { mutableStateOf(initialSelectedIds) }

  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = { PocketyTopAppBar(title = "New Pocket", onBackClick = onBackClick) },
      bottomBar = { PocketyBottomActionBar(text = "Continue", onClick = onContinueClick) },
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
        Text(
            text = "Which categories belong to this pocket?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text =
                "Pick one category for a simple pocket that takes its name, icon and " +
                    "color \u2014 or pick none or several for an advanced pocket you set " +
                    "up yourself.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Skipping categories is a first-class choice: it goes straight to the next step.
          NoCategoryRow(onClick = onContinueClick)
          HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
          samplePocketCategories.forEach { category ->
            LinkedCategoryRow(
                category = category,
                isLinked = category.id in selectedIds,
                onClick = {
                  selectedIds =
                      if (category.id in selectedIds) selectedIds - category.id
                      else selectedIds + category.id
                },
            )
          }
        }

        Text(
            text =
                when (selectedIds.size) {
                  0 -> "No category picked \u2014 you will set the pocket up manually."
                  1 ->
                      "Simple pocket \u2014 it follows " +
                          "\u201C${samplePocketCategories.first { it.id in selectedIds }.name}\u201D."
                  else -> "Advanced pocket with ${selectedIds.size} categories."
                },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

/**
 * Step 2 of new-pocket creation, in two modes decided by the step 1 pick:
 * - Simple mode (exactly one category picked): the pocket inherits the category's name, icon and
 *   color and keeps following it. The quiet manual-setup row still offers the advanced form.
 * - Advanced mode (zero or several picked, or entered from simple mode through the confirmation
 *   dialog): the full classic form, plus linking several categories to one pocket.
 */
@Composable
fun AddPocketScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    initialCategoryId: String? = null,
    manualExpanded: Boolean = false,
    manualConfirmShown: Boolean = false,
    initialLinkedCategoryIds: Set<String> = emptySet(),
    currencySymbol: String = "T",
    modifier: Modifier = Modifier,
) {
  var selectedCategory by remember {
    mutableStateOf(samplePocketCategories.firstOrNull { it.id == initialCategoryId })
  }
  var manual by remember { mutableStateOf(manualExpanded) }
  var showManualConfirm by remember { mutableStateOf(manualConfirmShown) }
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
      topBar = {
        PocketyTopAppBar(
            title = "New Pocket",
            // In the real flow Back returns to the step 1 category pick; in this design
            // mock, Back in manual mode first returns to the simple setup.
            onBackClick = { if (manual) manual = false else onBackClick() },
        )
      },
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
              text =
                  "This pocket follows its category \u2014 the name, icon and color come from it.",
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

            AddPocketSectionTitle(text = "Category")
            AddPocketCategoryRow(
                category = selectedCategory!!,
                isSelected = true,
                onClick = {},
            )
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

        if (!manual) {
          ManualSetupToggle(
              onToggle = { showManualConfirm = true },
          )
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }

  if (showManualConfirm) {
    ManualSetupConfirmDialog(
        onConfirm = {
          showManualConfirm = false
          manual = true
        },
        onDismiss = { showManualConfirm = false },
    )
  }
}

/** Close-up of the quiet manual-setup row in both states, for design review. */
@Composable
fun ManualSetupToggleShowcase(modifier: Modifier = Modifier) {
  Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      AddPocketSectionTitle(text = "The row at the bottom of the category screen")
      ManualSetupToggle(onToggle = {})
    }
  }
}

@ScreenPreviews
@Composable
fun SelectPocketCategoriesScreenPreview() {
  SelectPocketCategoriesScreen()
}

@ScreenPreviews
@Composable
fun SelectPocketCategoriesScreenOnePreview() {
  SelectPocketCategoriesScreen(initialSelectedIds = setOf("cafe"))
}

@ScreenPreviews
@Composable
fun SelectPocketCategoriesScreenManyPreview() {
  SelectPocketCategoriesScreen(initialSelectedIds = setOf("cafe", "groceries"))
}

@ScreenPreviews
@Composable
fun AddPocketScreenSimplePreview() {
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
