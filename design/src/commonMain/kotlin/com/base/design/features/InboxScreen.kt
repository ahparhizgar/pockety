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

package com.base.design.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.base.design.globalcomponents.PocketyBottomBar
import com.base.design.globalcomponents.PocketyTab
import com.base.design.globalcomponents.PocketyTopBar
import com.base.design.ui.ScreenPreviews

/**
 * Screen 2: InboxScreen (SMS Inbox / Smart Extraction) for the Pockety app.
 *
 * Displays pending financial SMS candidates extracted via Smart Extraction, allowing users to
 * review raw payloads, quickly categorize them into pockets, dismiss candidates, or open full edit
 * details.
 */
@Composable
fun InboxScreen(
    modifier: Modifier = Modifier,
    candidates: List<CandidateExpense> = FakeCandidateExpenseFactory.createList(),
    selectedTab: PocketyTab = PocketyTab.Inbox,
    totalCount: Int = 8,
    isAutoSyncOn: Boolean = true,
    notificationCount: Int = 1,
    onTabSelected: (PocketyTab) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCategoryClick: (CandidateExpense, String) -> Unit = { _, _ -> },
    onDismissClick: (CandidateExpense) -> Unit = {},
    onOpenEditClick: (CandidateExpense) -> Unit = {},
) {
  val candidateList = remember(candidates) { mutableStateListOf(*candidates.toTypedArray()) }

  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        PocketyTopBar(
            title = "Inbox",
            subtitle = "Pockety",
            navigationIcon = {
              Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                  contentColor = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(start = 12.dp).size(36.dp),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                      imageVector = Icons.Default.Insights,
                      contentDescription = "Pockety logo",
                      modifier = Modifier.size(20.dp),
                  )
                }
              }
            },
        )
      },
      bottomBar = {
        PocketyBottomBar(
            selectedTab = selectedTab,
            inboxBadgeCount = candidateList.size,
            onTabSelected = onTabSelected,
        )
      },
  ) { innerPadding ->
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
          modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).padding(horizontal = 20.dp),
          contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        item(key = "header") {
          SmartExtractionHeaderCard(
              remainingCount = candidateList.size,
              totalCount = totalCount,
              isAutoSyncOn = isAutoSyncOn,
          )
        }

        if (candidateList.isEmpty()) {
          item(key = "empty_state") { InboxCleanCard() }
        } else {
          items(
              items = candidateList,
              key = { it.id },
          ) { candidate ->
            CandidateExpenseCard(
                candidate = candidate,
                onCategoryClick = { category ->
                  candidateList.remove(candidate)
                  onCategoryClick(candidate, category)
                },
                onDismissClick = {
                  candidateList.remove(candidate)
                  onDismissClick(candidate)
                },
                onOpenEditClick = { onOpenEditClick(candidate) },
            )
          }
        }
      }
    }
  }
}

@ScreenPreviews
@Composable
private fun InboxScreenPreview() {
  InboxScreen()
}

@ScreenPreviews
@Composable
private fun InboxScreenCleanPreview() {
  InboxScreen(
      candidates = emptyList(),
  )
}
