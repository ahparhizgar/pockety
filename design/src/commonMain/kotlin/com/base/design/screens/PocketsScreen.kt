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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.base.design.components.PocketyBottomBar
import com.base.design.components.PocketyTab
import com.base.design.components.PocketyTopBar
import com.base.design.ui.ScreenPreviews

@Composable
fun PocketsScreen(
    modifier: Modifier = Modifier,
    pockets: List<PocketItem> = FakePocketFactory.createList(),
    selectedTab: PocketyTab = PocketyTab.Pockets,
    onTabSelected: (PocketyTab) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onAddPocketClick: () -> Unit = {},
    onTopUpClick: (PocketItem) -> Unit = {},
    onHistoryClick: (PocketItem) -> Unit = {},
    onSettingsClick: (PocketItem) -> Unit = {},
) {
  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopBar(
            title = "Pockets",
            subtitle = "Pockety",
            onSearchClick = onSearchClick,
            onNotificationsClick = onNotificationsClick,
            onProfileClick = onProfileClick,
        )
      },
      bottomBar = {
        PocketyBottomBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
        )
      },
      floatingActionButton = {
        ExtendedFloatingActionButton(
            onClick = onAddPocketClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(16.dp),
            icon = {
              Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = null,
                  modifier = Modifier.size(24.dp),
              )
            },
            text = {
              Text(
                  text = "Add Pocket",
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.SemiBold,
              )
            },
        )
      },
  ) { innerPadding ->
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
          modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).padding(horizontal = 20.dp),
          contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        item {
          PocketsSectionHeader(
              title = "Your Budget Pockets",
              onFilterClick = onFilterClick,
          )
        }

        items(pockets, key = { it.id }) { pocket ->
          PocketBudgetCard(
              pocket = pocket,
              onTopUpClick = { onTopUpClick(pocket) },
              onHistoryClick = { onHistoryClick(pocket) },
              onSettingsClick = { onSettingsClick(pocket) },
          )
        }

        item {
          Spacer(modifier = Modifier.height(4.dp))
          DynamicBalanceSyncBanner()
        }
      }
    }
  }
}

@ScreenPreviews
@Composable
private fun PocketsScreenPreview() {
  PocketsScreen()
}
