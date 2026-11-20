package com.base.feature.pocket.details

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.feature.pocket.QuickTopUpBottomSheet
import com.base.ui.ExcludeScreenshotTest
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import com.base.ui.money.moneyLabel
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.pocket_details_back_action
import pockety.app.shared.generated.resources.pocket_details_expenses_header
import pockety.app.shared.generated.resources.pocket_details_no_expenses
import pockety.app.shared.generated.resources.pocket_details_title
import pockety.app.shared.generated.resources.pocket_top_up_feedback
import pockety.app.shared.generated.resources.pocket_unlink_feedback

@Composable
fun PocketDetailsScreen(
    component: PocketDetailsComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()
  Scaffold(
      modifier = modifier.fillMaxSize().testTag("pocket-details-screen"),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = { PocketDetailsTopAppBar(component = component) },
  ) { innerPadding ->
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
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
        } else {
          PocketDetailsContent(component = component)
        }
      }
    }
  }

  if (state.isQuickTopUpVisible) {
    QuickTopUpBottomSheet(component = component)
  }
}

@Composable
private fun PocketDetailsTopAppBar(
    component: PocketDetailsComponent,
    modifier: Modifier = Modifier,
) {
  PocketyTopAppBar(
      title = stringResource(Res.string.pocket_details_title),
      subtitle = null,
      modifier = modifier,
      navigationIcon = {
        IconButton(
            onClick = component::onBackClicked,
            modifier = Modifier.testTag("back-button"),
        ) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.pocket_details_back_action),
          )
        }
      },
  )
}

@Composable
private fun PocketDetailsContent(
    component: PocketDetailsComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  LazyColumn(
      modifier = modifier.fillMaxSize().padding(horizontal = 20.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    item {
      PocketDetailsHeaderSection(
          name = state.name,
          balanceAmount = state.balanceAmount,
          icon = state.icon,
          color = state.color,
      )
    }

    item {
      PocketDetailsActionBar(
          onTopUpClick = component::onTopUpClicked,
          onConfigureClick = component::onConfigureClicked,
      )
    }

    val feedback = state.confirmationMessage
    if (feedback != null) {
      item {
        PocketFeedbackBanner(
            message =
                feedback.let { feedback ->
                  if (feedback.unlinkedTitle == null)
                      stringResource(Res.string.pocket_top_up_feedback, moneyLabel(feedback.amount))
                  else
                      stringResource(
                          Res.string.pocket_unlink_feedback,
                          feedback.unlinkedTitle,
                          moneyLabel(feedback.amount),
                      )
                },
            onDismiss = component::onDismissConfirmation,
        )
      }
    }

    item { PocketExpensesHeader() }

    if (state.transactions.isEmpty()) {
      item { PocketEmptyTransactionsPlaceholder() }
    } else {
      items(state.transactions, key = { it.id }) { tx ->
        PocketTransactionRow(
            item = tx,
            onUnlink = { component.onUnlinkTransaction(tx.id) },
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
      }
    }
  }
}

@Composable
private fun PocketExpensesHeader(modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
          text = stringResource(Res.string.pocket_details_expenses_header),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

@Composable
private fun PocketEmptyTransactionsPlaceholder(modifier: Modifier = Modifier) {
  Box(
      modifier = modifier.fillMaxWidth().padding(vertical = 32.dp),
      contentAlignment = Alignment.Center,
  ) {
    Text(
        text = stringResource(Res.string.pocket_details_no_expenses),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@ScreenPreviews
@Composable
private fun PocketDetailsScreenNormalPreview() {
  PocketDetailsScreen(FakePocketDetailsComponent())
}

@ScreenPreviews
@ExcludeScreenshotTest
@Composable
private fun PocketDetailsScreenTopUpOpenPreview() {
  PocketDetailsScreen(
      FakePocketDetailsComponent(
          initialState =
              FakePocketDetailsStateFactory.create(
                  isQuickTopUpVisible = true,
                  quickTopUpCustomAmount = "100000",
              )
      )
  )
}

@ScreenPreviews
@Composable
private fun PocketDetailsScreenEmptyTransactionsPreview() {
  PocketDetailsScreen(
      FakePocketDetailsComponent(
          initialState =
              FakePocketDetailsStateFactory.create(
                  transactions = emptyList(),
                  confirmationMessage = PocketMoneyFeedback(100_000, null),
              )
      )
  )
}
