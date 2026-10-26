package com.base.feature.pocket

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.ui.ComponentPreviews
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.pockets_add_pocket_action
import pockety.app.shared.generated.resources.pockets_empty_desc
import pockety.app.shared.generated.resources.pockets_empty_title
import pockety.app.shared.generated.resources.pockets_header_title
import pockety.app.shared.generated.resources.pockets_title

@Composable
fun PocketsScreen(
    component: PocketsComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.testTag("pockets-screen"),
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.pockets_title),
        )
      },
      floatingActionButton = {
        ExtendedFloatingActionButton(
            onClick = component::onAddPocketClick,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            icon = {
              Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = null,
                  modifier = Modifier.size(24.dp),
              )
            },
            text = {
              Text(
                  text = stringResource(Res.string.pockets_add_pocket_action),
                  style =
                      MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
              )
            },
            modifier = Modifier.testTag("add-pocket-fab"),
        )
      },
  ) { paddingValues ->
    PocketsContent(
        state = state,
        component = component,
        modifier = Modifier.padding(paddingValues),
    )
  }
}

@Composable
private fun PocketsContent(
    state: PocketsState,
    component: PocketsComponent,
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.TopCenter,
  ) {
    Box(
        modifier = Modifier.widthIn(max = 640.dp).fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
      if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
      } else if (state.pockets.isEmpty()) {
        PocketsEmptyState(
            modifier = Modifier.fillMaxSize().testTag("pockets-empty-state"),
        )
      } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          item {
            PocketsSectionHeader(
                title = stringResource(Res.string.pockets_header_title),
            )
          }

          items(state.pockets, key = { it.id }) { pocket ->
            PocketBudgetCard(
                pocket = pocket,
                onClick = { component.onPocketClick(pocket.id) },
                onTopUpClick = {},
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
}

@Composable
fun PocketsSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )
  }
}

@Composable
fun PocketsEmptyState(
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier.padding(24.dp),
      contentAlignment = Alignment.Center,
  ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Surface(
          modifier = Modifier.size(64.dp),
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceContainerHigh,
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = Icons.Default.FolderOpen,
              contentDescription = null,
              modifier = Modifier.size(32.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      Text(
          text = stringResource(Res.string.pockets_empty_title),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center,
      )

      Text(
          text = stringResource(Res.string.pockets_empty_desc),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.widthIn(max = 280.dp),
      )
    }
  }
}

@ScreenPreviews
@Composable
private fun PocketsScreenNormalPreview() {
  PocketsScreen(FakePocketsComponent())
}

@ScreenPreviews
@Composable
private fun PocketsScreenEmptyPreview() {
  PocketsScreen(FakePocketsComponent(initialState = PocketsState(pockets = emptyList())))
}

@ScreenPreviews
@Composable
private fun PocketsScreenLoadingPreview() {
  PocketsScreen(FakePocketsComponent(initialState = PocketsState(isLoading = true)))
}

@ComponentPreviews
@Composable
private fun PocketsEmptyStatePreview() {
  PocketsEmptyState()
}
