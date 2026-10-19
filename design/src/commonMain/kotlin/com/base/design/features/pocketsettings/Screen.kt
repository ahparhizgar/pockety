package com.base.design.features.pocketsettings

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.design.globalcomponents.AccentColorPicker
import com.base.design.globalcomponents.IconPickerSection
import com.base.design.globalcomponents.ItemColorOption
import com.base.design.globalcomponents.ItemIconOption
import com.base.design.globalcomponents.PocketyBottomActionBar
import com.base.design.globalcomponents.PocketyTopAppBar
import com.base.design.globalcomponents.defaultIconCategoryFilters
import com.base.design.globalcomponents.defaultItemColors
import com.base.design.globalcomponents.defaultItemIcons
import com.base.design.ui.ScreenPreviews

@Composable
fun PocketSettingsScreen(
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSaveChangesClick: (pocketName: String, icon: ItemIconOption, color: ItemColorOption) -> Unit =
        { _, _, _ ->
        },
    onDeletePocketClick: () -> Unit = {},
    onChangeCategoryClick: () -> Unit = {},
    onClearCategoryClick: () -> Unit = {},
    onTuneClick: () -> Unit = {},
    onConfirmTopUp: (String) -> Unit = {},
    onEditIconClick: () -> Unit = {},
    initialPocketName: String = "Daily Cafe & Coffee",
    initialIcon: ItemIconOption =
        defaultItemIcons.firstOrNull { it.id == "coffee" } ?: defaultItemIcons.first(),
    initialColor: ItemColorOption = defaultItemColors.first(),
    initialAllocatedAmount: String = "1,500,000",
    initialCurrentBalance: String = "850,000",
    initialCategoryName: String = "Cafe & Dining",
    initialCategorySubtitle: String = "Auto-deduct transactions",
    currencySymbol: String = "T",
    modifier: Modifier = Modifier,
) {
  var pocketName by remember { mutableStateOf(initialPocketName) }
  var selectedIcon by remember { mutableStateOf(initialIcon) }
  var selectedColor by remember { mutableStateOf(initialColor) }
  var selectedIconFilter by remember { mutableStateOf("All") }
  var autoRefillEnabled by remember { mutableStateOf(true) }
  var topUpAmount by remember { mutableStateOf("") }

  PocketSettingsScreenContent(
      pocketName = pocketName,
      onPocketNameChange = { pocketName = it },
      selectedIcon = selectedIcon,
      onIconSelected = { selectedIcon = it },
      selectedColor = selectedColor,
      onColorSelected = { selectedColor = it },
      selectedIconFilter = selectedIconFilter,
      onIconFilterSelected = { selectedIconFilter = it },
      allocatedAmount = initialAllocatedAmount,
      currencySymbol = currencySymbol,
      autoRefillEnabled = autoRefillEnabled,
      onAutoRefillChange = { autoRefillEnabled = it },
      currentBalance = initialCurrentBalance,
      topUpAmount = topUpAmount,
      onTopUpAmountChange = { topUpAmount = it },
      categoryName = initialCategoryName,
      categorySubtitle = initialCategorySubtitle,
      onBackClick = onBackClick,
      onSaveChangesClick = { onSaveChangesClick(pocketName, selectedIcon, selectedColor) },
      onDeletePocketClick = onDeletePocketClick,
      onChangeCategoryClick = onChangeCategoryClick,
      onClearCategoryClick = onClearCategoryClick,
      onTuneClick = onTuneClick,
      onConfirmTopUp = {
        onConfirmTopUp(topUpAmount)
        topUpAmount = ""
      },
      modifier = modifier,
  )
}

@Composable
fun PocketSettingsScreenContent(
    pocketName: String,
    onPocketNameChange: (String) -> Unit,
    selectedIcon: ItemIconOption,
    onIconSelected: (ItemIconOption) -> Unit,
    selectedColor: ItemColorOption,
    onColorSelected: (ItemColorOption) -> Unit,
    selectedIconFilter: String,
    onIconFilterSelected: (String) -> Unit,
    allocatedAmount: String,
    currencySymbol: String,
    autoRefillEnabled: Boolean,
    onAutoRefillChange: (Boolean) -> Unit,
    currentBalance: String,
    topUpAmount: String,
    onTopUpAmountChange: (String) -> Unit,
    categoryName: String,
    categorySubtitle: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSaveChangesClick: () -> Unit = {},
    onDeletePocketClick: () -> Unit = {},
    onChangeCategoryClick: () -> Unit = {},
    onClearCategoryClick: () -> Unit = {},
    onTuneClick: () -> Unit = {},
    onConfirmTopUp: () -> Unit = {},
) {
  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopAppBar(
            title = "Pocket Settings",
            onBackClick = onBackClick,
        )
      },
      bottomBar = {
        PocketyBottomActionBar(
            text = "Save Changes",
            onClick = onSaveChangesClick,
        )
      },
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
        PocketIdentitySection(
            pocketName = pocketName,
            onPocketNameChange = onPocketNameChange,
            icon = selectedIcon.icon,
            containerColor = selectedColor.color,
            contentColor = selectedColor.onColor,
        )

        // Accent Color Selection
        AccentColorPicker(
            colors = defaultItemColors,
            selectedColor = selectedColor,
            onColorSelected = onColorSelected,
        )

        // Icon Selection Section
        IconPickerSection(
            icons = defaultItemIcons,
            selectedIcon = selectedIcon,
            onIconSelected = onIconSelected,
            filters = defaultIconCategoryFilters,
            selectedFilter = selectedIconFilter,
            onFilterSelected = onIconFilterSelected,
            accentColor = selectedColor.color,
        )

        MonthlyAllocationCard(
            allocatedAmount = allocatedAmount,
            currencySymbol = currencySymbol,
            autoRefillEnabled = autoRefillEnabled,
            onAutoRefillChange = onAutoRefillChange,
            onTuneClick = onTuneClick,
        )

        AssociatedCategoryCard(
            categoryName = categoryName,
            categorySubtitle = categorySubtitle,
            onChangeCategoryClick = onChangeCategoryClick,
            onClearCategoryClick = onClearCategoryClick,
        )

        ManualTopUpCard(
            currentBalance = currentBalance,
            currencySymbol = currencySymbol,
            amount = topUpAmount,
            onAmountChange = onTopUpAmountChange,
            onConfirmTopUp = onConfirmTopUp,
        )

        DangerZoneCard(
            onDeletePocketClick = onDeletePocketClick,
        )

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@ScreenPreviews
@Composable
private fun PocketSettingsScreenPreview() {
  PocketSettingsScreen()
}
