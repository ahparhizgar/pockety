package com.base.feature.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.base.ui.money.moneyLabel
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.add_expense_title
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
          modifier =
              Modifier.fillMaxWidth()
                  .verticalScroll(rememberScrollState())
                  .padding(horizontal = 20.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        HomeBannerItem(component = component, state = state)

        MonthlyOutflowHeroCard(
            state = state,
            onDateClick = component::onAllMonthsClicked,
        )

        if (state.showMonthOverMonthCard) {
          MonthOverMonthCard(
              state = state,
              onAllMonthsClick = component::onAllMonthsClicked,
          )
        }

        if (state.pockets.isNotEmpty()) {
          ActivePocketsSection(
              state = state,
              onManageAllClick = component::onManagePocketsClicked,
              onPocketClick = component::onPocketClicked,
          )
        }

        Spacer(modifier = Modifier.height(72.dp))
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
    UndecidedExpensesBanner(
        count = state.pendingCandidateCount,
        amountFormatted = moneyLabel(state.pendingCandidateSum),
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

private fun populatedHomeState(): HomeState =
    HomeState(
        isSmsPermissionGranted = true,
        pendingCandidateCount = 8,
        pendingCandidateSum = 2_340_000L,
        currentYear = 2026,
        currentMonth = 10,
        previousYear = 2026,
        previousMonth = 9,
        monthlyOutflow = 14_850_000L,
        previousMonthOutflow = 16_900_000L,
        monthOverMonthPercent = 12,
        isMonthOverMonthSaved = true,
        topCategories =
            listOf(
                HomeCategoryOutflow(
                    kind = HomeCategoryKind.CATEGORY,
                    name = "Food & Groceries",
                    amount = 6_200_000L,
                    share = 0.42f,
                    color = 0xFF316342,
                ),
                HomeCategoryOutflow(
                    kind = HomeCategoryKind.CATEGORY,
                    name = "Transportation",
                    amount = 3_100_000L,
                    share = 0.21f,
                    color = 0xFF7A9B76,
                ),
                HomeCategoryOutflow(
                    kind = HomeCategoryKind.CATEGORY,
                    name = "Cafe & Dining",
                    amount = 2_450_000L,
                    share = 0.16f,
                    color = 0xFFB08968,
                ),
                HomeCategoryOutflow(
                    kind = HomeCategoryKind.OTHER,
                    amount = 3_100_000L,
                    share = 0.21f,
                ),
            ),
        pockets =
            listOf(
                HomePocketUi(id = 1, name = "Cafe", balance = 85_000L, icon = "coffee"),
                HomePocketUi(
                    id = 2,
                    name = "Groceries",
                    balance = 450_000L,
                    icon = "shopping_cart",
                ),
            ),
    )

@ScreenPreviews
@Composable
private fun HomeScreenPopulatedPreview() {
  HomeScreen(component = FakeHomeComponent(initialState = populatedHomeState()))
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
