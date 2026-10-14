package com.base.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.db.FakeTransactionFactory
import com.base.db.TransactionEntity
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.add_transaction_title
import pockety.app.shared.generated.resources.no_transactions
import pockety.app.shared.generated.resources.transactions_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    component: HomeComponent,
    modifier: Modifier = Modifier,
) {
  Scaffold(
      modifier = modifier.testTag("home-screen"),
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.transactions_title),
        )
      },
      floatingActionButton = {
        ExtendedFloatingActionButton(
            onClick = component::onAddTransactionClicked,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = {
              Text(
                  text = stringResource(Res.string.add_transaction_title),
                  style =
                      MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
              )
            },
            modifier = Modifier.testTag("add-transaction-fab"),
        )
      },
  ) { paddingValues ->
    HomeContent(
        component = component,
        modifier = Modifier.padding(paddingValues),
    )
  }
}

@Composable
private fun HomeContent(
    component: HomeComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.TopCenter,
  ) {
    Box(
        modifier = Modifier.widthIn(max = 640.dp).fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
      when {
        state.isLoading && state.transactions.isEmpty() -> {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.testTag("home-loading"))
          }
        }

        state.transactions.isEmpty() -> {
          Box(
              modifier = Modifier.fillMaxSize().padding(16.dp),
              contentAlignment = Alignment.Center,
          ) {
            Text(
                text = stringResource(Res.string.no_transactions),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("empty-transactions-message"),
            )
          }
        }

        else -> {
          LazyColumn(
              modifier = Modifier.fillMaxSize().testTag("transaction-list"),
              contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            items(state.transactions, key = { it.id }) { transaction ->
              TransactionItemCard(
                  transaction = transaction,
                  modifier = Modifier.fillMaxWidth().testTag("transaction-item-${transaction.id}"),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun TransactionItemCard(
    transaction: TransactionEntity,
    modifier: Modifier = Modifier,
) {
  Surface(
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = modifier,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
          modifier = Modifier.size(44.dp),
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondaryContainer,
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.size(22.dp),
          )
        }
      }

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
            text = transaction.description,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("transaction-description"),
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      Text(
          text = "${transaction.amount} T",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.testTag("transaction-amount"),
      )
    }
  }
}

@ScreenPreviews
@Composable
private fun HomeScreenPopulatedPreview() {
  HomeScreen(
      component =
          FakeHomeComponent(
              initialState =
                  HomeState(
                      transactions = FakeTransactionFactory.createList(4),
                      isLoading = false,
                  ),
          ),
  )
}

@ScreenPreviews
@Composable
private fun HomeScreenEmptyPreview() {
  HomeScreen(
      component =
          FakeHomeComponent(
              initialState =
                  HomeState(
                      transactions = emptyList(),
                      isLoading = false,
                  ),
          ),
  )
}
