package com.base.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.add_expense_title
import pockety.app.shared.generated.resources.currency_symbol
import pockety.app.shared.generated.resources.expenses_title

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
            title = stringResource(Res.string.expenses_title),
        )
      },
      floatingActionButton = {
        ExtendedFloatingActionButton(
            onClick = component::onAddExpenseClicked,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = {
              Text(
                  text = stringResource(Res.string.add_expense_title),
                  style =
                      MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
              )
            },
            modifier = Modifier.testTag("add-expense-fab"),
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
      Column(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        HomeBannerItem(component = component, state = state)
      }
    }
  }
}

@Composable
private fun HomeBannerItem(
    component: HomeComponent,
    state: HomeState,
) {
  if (!state.isSmsPermissionGranted) {
    SmsPermissionBanner(
        onClick = component::onEnableSmsTrackingClicked,
    )
  } else {
    val currency = stringResource(Res.string.currency_symbol)
    UndecidedExpensesBanner(
        count = state.pendingCandidateCount,
        amountFormatted = "${state.pendingCandidateSum} $currency",
        onClick = component::onUndecidedExpensesClicked,
    )
  }
}

@ScreenPreviews
@Composable
private fun HomeScreenPermissionDeniedPreview() {
  HomeScreen(
      component =
          FakeHomeComponent(
              initialState =
                  HomeState(
                      isSmsPermissionGranted = false,
                  ),
          ),
  )
}

@ScreenPreviews
@Composable
private fun HomeScreenPermissionGrantedPreview() {
  HomeScreen(
      component =
          FakeHomeComponent(
              initialState =
                  HomeState(
                      isSmsPermissionGranted = true,
                      pendingCandidateCount = 3,
                      pendingCandidateSum = 125000L,
                  ),
          ),
  )
}

@ScreenPreviews
@Composable
private fun HomeScreenPermissionGrantedEmptyPreview() {
  HomeScreen(
      component =
          FakeHomeComponent(
              initialState =
                  HomeState(
                      isSmsPermissionGranted = true,
                      pendingCandidateCount = 0,
                      pendingCandidateSum = 0L,
                  ),
          ),
  )
}
