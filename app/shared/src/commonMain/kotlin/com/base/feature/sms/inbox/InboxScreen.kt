package com.base.feature.sms.inbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.base.db.FakeCandidateExpenseEntityFactory
import com.base.ui.ComponentPreviews
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.back_action
import pockety.app.shared.generated.resources.inbox_clean_desc
import pockety.app.shared.generated.resources.inbox_clean_title
import pockety.app.shared.generated.resources.inbox_title

@Composable
fun InboxScreen(
    component: InboxComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.testTag("inbox-screen"),
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.inbox_title),
            navigationIcon = {
              IconButton(
                  onClick = component::onBackClicked,
                  modifier = Modifier.testTag("inbox-back-button"),
              ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.back_action),
                )
              }
            },
        )
      },
  ) { innerPadding ->
    InboxContent(
        component = component,
        state = state,
        modifier = Modifier.padding(innerPadding),
    )
  }
}

@Composable
private fun InboxContent(
    component: InboxComponent,
    state: InboxState,
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.TopCenter,
  ) {
    if (state.isLoading && state.candidates.isEmpty()) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.testTag("inbox-loading"))
      }
    } else {
      LazyColumn(
          modifier =
              Modifier.fillMaxWidth()
                  .widthIn(max = 640.dp)
                  .padding(horizontal = 20.dp)
                  .testTag("inbox-candidates-list"),
          contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        item(key = "header") {
          SmartExtractionHeaderCard(
              remainingCount = state.candidates.size,
          )
        }

        if (state.candidates.isEmpty()) {
          item(key = "empty_state") { InboxCleanCard() }
        } else {
          items(
              items = state.candidates,
              key = { it.id },
          ) { candidate ->
            CandidateExpenseCard(
                candidate = candidate,
                onDismissClick = { component.onDismiss(candidate.id) },
                onOpenEditClick = { component.onSelectCandidate(candidate) },
                modifier = Modifier.testTag("candidate-item-${candidate.id}"),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun InboxCleanCard(
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth().testTag("inbox-clean-card"),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondaryContainer,
          modifier = Modifier.size(56.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = Icons.Default.Verified,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(28.dp),
          )
        }
      }
      Text(
          text = stringResource(Res.string.inbox_clean_title),
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = stringResource(Res.string.inbox_clean_desc),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
      )
    }
  }
}

@ScreenPreviews
@Composable
private fun InboxScreenPopulatedPreview() {
  InboxScreen(
      component =
          FakeInboxComponent(
              initialState =
                  InboxState(
                      candidates = FakeCandidateExpenseEntityFactory.createList(3),
                      isLoading = false,
                  ),
          ),
  )
}

@ScreenPreviews
@Composable
private fun InboxScreenEmptyPreview() {
  InboxScreen(
      component =
          FakeInboxComponent(
              initialState =
                  InboxState(
                      candidates = emptyList(),
                      isLoading = false,
                  ),
          ),
  )
}

@ComponentPreviews
@Composable
private fun InboxCleanCardPreview() {
  InboxCleanCard()
}
