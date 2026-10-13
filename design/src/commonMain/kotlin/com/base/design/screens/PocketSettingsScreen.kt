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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.base.design.components.PocketyTopBar
import com.base.design.ui.ScreenPreviews

@Composable
fun PocketSettingsScreen(
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSaveChangesClick: () -> Unit = {},
    onDeletePocketClick: () -> Unit = {},
    onChangeCategoryClick: () -> Unit = {},
    onClearCategoryClick: () -> Unit = {},
    onTuneClick: () -> Unit = {},
    onConfirmTopUp: (String) -> Unit = {},
    onEditIconClick: () -> Unit = {},
    initialPocketName: String = "Daily Cafe & Coffee",
    initialAllocatedAmount: String = "1,500,000",
    initialCurrentBalance: String = "850,000",
    initialCategoryName: String = "Cafe & Dining",
    initialCategorySubtitle: String = "Auto-deduct transactions",
    currencySymbol: String = "T",
    modifier: Modifier = Modifier,
) {
  var pocketName by remember { mutableStateOf(initialPocketName) }
  var autoRefillEnabled by remember { mutableStateOf(true) }
  var topUpAmount by remember { mutableStateOf("") }

  PocketSettingsScreenContent(
      pocketName = pocketName,
      onPocketNameChange = { pocketName = it },
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
      onProfileClick = onProfileClick,
      onSaveChangesClick = onSaveChangesClick,
      onDeletePocketClick = onDeletePocketClick,
      onChangeCategoryClick = onChangeCategoryClick,
      onClearCategoryClick = onClearCategoryClick,
      onTuneClick = onTuneClick,
      onConfirmTopUp = {
        onConfirmTopUp(topUpAmount)
        topUpAmount = ""
      },
      onEditIconClick = onEditIconClick,
      modifier = modifier,
  )
}

@Composable
fun PocketSettingsScreenContent(
    pocketName: String,
    onPocketNameChange: (String) -> Unit,
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
    onProfileClick: () -> Unit = {},
    onSaveChangesClick: () -> Unit = {},
    onDeletePocketClick: () -> Unit = {},
    onChangeCategoryClick: () -> Unit = {},
    onClearCategoryClick: () -> Unit = {},
    onTuneClick: () -> Unit = {},
    onConfirmTopUp: () -> Unit = {},
    onEditIconClick: () -> Unit = {},
) {
  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopBar(
            title = "Pocket Settings",
            navigationIcon = {
              IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
              }
            },
        )
      },
      bottomBar = {
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            modifier = Modifier.fillMaxWidth(),
        ) {
          Box(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
              contentAlignment = Alignment.Center,
          ) {
            Button(
                onClick = onSaveChangesClick,
                modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).height(56.dp),
                shape = CircleShape,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            ) {
              Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  modifier = Modifier.size(22.dp),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                  text = "Save Changes",
                  style = MaterialTheme.typography.labelLarge,
              )
            }
          }
        }
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
            onEditIconClick = onEditIconClick,
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
