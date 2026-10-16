package com.base.design.features.prototype

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.base.design.features.addcategory.AddCategoryScreen
import com.base.design.features.categorydetail.CategoryDetailScreen
import com.base.design.features.categorylist.CategoryListScreen
import com.base.design.features.editexpense.EditExpenseScreen
import com.base.design.features.home.HomeScreen
import com.base.design.features.home.rememberDefaultHomeScreenState
import com.base.design.features.inbox.CandidateExpense
import com.base.design.features.inbox.FakeCandidateExpenseFactory
import com.base.design.features.inbox.InboxScreen
import com.base.design.features.monthdetail.MonthDetailScreen
import com.base.design.features.pocketdetail.PocketDetailScreen
import com.base.design.features.pockets.FakePocketFactory
import com.base.design.features.pockets.PocketItem
import com.base.design.features.pockets.PocketsScreen
import com.base.design.features.pocketsettings.PocketSettingsScreen
import com.base.design.features.selectcategory.SelectCategoryScreen
import com.base.design.features.selectlabels.SelectLabelsScreen
import com.base.design.features.settings.SettingsScreen
import com.base.design.globalcomponents.PocketyTab
import com.base.design.ui.ScreenPreviews

sealed interface PrototypeDestination {
  data object Home : PrototypeDestination

  data object Inbox : PrototypeDestination

  data object Pockets : PrototypeDestination

  data object Analytics : PrototypeDestination

  data object Settings : PrototypeDestination

  data class PocketDetail(val pocketName: String = "Daily Cafe & Coffee") : PrototypeDestination

  data class PocketSettings(val pocketName: String = "Daily Cafe & Coffee") : PrototypeDestination

  data class EditExpense(
      val initialAmount: String = "320,000",
      val initialCategory: String = "Cafe & Dining",
      val initialDescription: String = "Latte & Croissant at Sam Cafe",
  ) : PrototypeDestination

  data object SelectCategory : PrototypeDestination

  data object SelectLabels : PrototypeDestination

  data object CategoryList : PrototypeDestination

  data class CategoryDetail(val categoryName: String = "Groceries & Supermarket") :
      PrototypeDestination

  data object AddCategory : PrototypeDestination
}

/**
 * Interactive Prototype Screen linking all 14 screens of Pockety into a cohesive, responsive,
 * semi-functional interactive app flow.
 */
@Composable
fun PrototypeAppScreen(
    modifier: Modifier = Modifier,
) {
  var backStack by remember {
    mutableStateOf(listOf<PrototypeDestination>(PrototypeDestination.Home))
  }
  val currentDestination = backStack.lastOrNull() ?: PrototypeDestination.Home

  fun navigateTo(dest: PrototypeDestination) {
    backStack = backStack + dest
  }

  fun navigateBack() {
    if (backStack.size > 1) {
      backStack = backStack.dropLast(1)
    }
  }

  fun switchTab(tab: PocketyTab) {
    val dest =
        when (tab) {
          PocketyTab.Home -> PrototypeDestination.Home
          PocketyTab.Inbox -> PrototypeDestination.Inbox
          PocketyTab.Pockets -> PrototypeDestination.Pockets
          PocketyTab.Analytics -> PrototypeDestination.Analytics
        }
    backStack = listOf(dest)
  }

  // Live mutable prototype state
  val homeState = rememberDefaultHomeScreenState()
  val candidates = remember {
    mutableStateListOf<CandidateExpense>().apply {
      addAll(FakeCandidateExpenseFactory.createList())
    }
  }
  val pockets = remember {
    mutableStateListOf<PocketItem>().apply { addAll(FakePocketFactory.createList()) }
  }

  var selectedExpenseCategory by remember { mutableStateOf("Cafe & Dining") }
  var selectedExpenseIcon by remember { mutableStateOf<ImageVector>(Icons.Default.LocalCafe) }
  var selectedExpenseLabels by remember { mutableStateOf(listOf("#WorkMeeting", "#Personal")) }

  Box(modifier = modifier.fillMaxSize()) {
    AnimatedContent(
        targetState = currentDestination,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "PrototypeNavigation",
    ) { destination ->
      when (destination) {
        is PrototypeDestination.Home -> {
          HomeScreen(
              state =
                  homeState.copy(
                      undecidedExpenseCount = candidates.size,
                      inboxBadgeCount = candidates.size,
                  ),
              onTabSelected = ::switchTab,
              onUndecidedExpensesClick = { navigateTo(PrototypeDestination.Inbox) },
              onGrantSmsPermissionClick = { navigateTo(PrototypeDestination.Inbox) },
              onDateClick = { navigateTo(PrototypeDestination.Analytics) },
              onAllMonthsClick = { navigateTo(PrototypeDestination.Analytics) },
              onManagePocketsClick = { navigateTo(PrototypeDestination.Pockets) },
              onPocketClick = { pocket ->
                navigateTo(PrototypeDestination.PocketDetail(pocket.name))
              },
              onAddExpenseClick = {
                navigateTo(
                    PrototypeDestination.EditExpense(
                        initialAmount = "320,000",
                        initialCategory = selectedExpenseCategory,
                    ),
                )
              },
              onProfileClick = { navigateTo(PrototypeDestination.Settings) },
          )
        }

        is PrototypeDestination.Inbox -> {
          InboxScreen(
              candidates = candidates,
              selectedTab = PocketyTab.Inbox,
              totalCount = candidates.size,
              onTabSelected = ::switchTab,
              onProfileClick = { navigateTo(PrototypeDestination.Settings) },
              onDismissClick = { candidate -> candidates.remove(candidate) },
              onCategoryClick = { candidate, _ -> candidates.remove(candidate) },
              onOpenEditClick = { candidate ->
                navigateTo(
                    PrototypeDestination.EditExpense(
                        initialAmount =
                            candidate.amountFormatted.filter { it.isDigit() || it == ',' },
                        initialCategory = "Cafe & Dining",
                        initialDescription = "SMS from ${candidate.bankName}",
                    ),
                )
              },
          )
        }

        is PrototypeDestination.Pockets -> {
          PocketsScreen(
              pockets = pockets,
              selectedTab = PocketyTab.Pockets,
              onTabSelected = ::switchTab,
              onProfileClick = { navigateTo(PrototypeDestination.Settings) },
              onAddPocketClick = {
                navigateTo(PrototypeDestination.PocketSettings("New Budget Pocket"))
              },
              onHistoryClick = { pocket ->
                navigateTo(PrototypeDestination.PocketDetail(pocket.name))
              },
              onSettingsClick = { pocket ->
                navigateTo(PrototypeDestination.PocketSettings(pocket.name))
              },
              onTopUpClick = { pocket ->
                navigateTo(PrototypeDestination.PocketDetail(pocket.name))
              },
          )
        }

        is PrototypeDestination.Analytics -> {
          MonthDetailScreen(
              selectedTab = PocketyTab.Analytics,
              onTabSelected = ::switchTab,
              onProfileClick = { navigateTo(PrototypeDestination.Settings) },
              onCategoryClick = { category ->
                navigateTo(PrototypeDestination.CategoryDetail(category.name))
              },
              onExpenseClick = { expense ->
                navigateTo(
                    PrototypeDestination.EditExpense(
                        initialAmount = expense.amountFormatted,
                        initialCategory = expense.title,
                        initialDescription = expense.subtitle,
                    ),
                )
              },
          )
        }

        is PrototypeDestination.Settings -> {
          SettingsScreen(
              onTabSelected = ::switchTab,
              onCategoriesClick = { navigateTo(PrototypeDestination.CategoryList) },
              onLabelsClick = { navigateTo(PrototypeDestination.SelectLabels) },
          )
        }

        is PrototypeDestination.PocketDetail -> {
          PocketDetailScreen(
              pocketTitle = destination.pocketName,
              onBackClick = ::navigateBack,
              onSettingsClick = {
                navigateTo(PrototypeDestination.PocketSettings(destination.pocketName))
              },
              onConfigureClick = {
                navigateTo(PrototypeDestination.PocketSettings(destination.pocketName))
              },
              onProfileClick = { navigateTo(PrototypeDestination.Settings) },
          )
        }

        is PrototypeDestination.PocketSettings -> {
          PocketSettingsScreen(
              initialPocketName = destination.pocketName,
              onBackClick = ::navigateBack,
              onProfileClick = { navigateTo(PrototypeDestination.Settings) },
              onChangeCategoryClick = { navigateTo(PrototypeDestination.SelectCategory) },
              onSaveChangesClick = { name, icon, color -> navigateBack() },
              onDeletePocketClick = ::navigateBack,
          )
        }

        is PrototypeDestination.EditExpense -> {
          var editAmount by remember { mutableStateOf(destination.initialAmount) }
          var editDesc by remember { mutableStateOf(destination.initialDescription) }

          EditExpenseScreen(
              amount = editAmount,
              selectedCategoryName = selectedExpenseCategory,
              selectedCategoryIcon = selectedExpenseIcon,
              description = editDesc,
              labels = selectedExpenseLabels,
              onAmountChange = { editAmount = it },
              onDescriptionChange = { editDesc = it },
              onBackClick = ::navigateBack,
              onCloseClick = ::navigateBack,
              onChangeCategoryClick = { navigateTo(PrototypeDestination.SelectCategory) },
              onAddLabelClick = { navigateTo(PrototypeDestination.SelectLabels) },
              onRemoveLabel = { label ->
                selectedExpenseLabels = selectedExpenseLabels.filter { it != label }
              },
              onSaveExpense = ::navigateBack,
              onDismissSms = ::navigateBack,
          )
        }

        is PrototypeDestination.SelectCategory -> {
          SelectCategoryScreen(
              initialSelectedCategoryId = "dining",
              onDismissRequest = ::navigateBack,
              onCategorySelected = { category ->
                selectedExpenseCategory = category.name
                selectedExpenseIcon = category.icon
                navigateBack()
              },
              onCreateCategoryClick = { navigateTo(PrototypeDestination.AddCategory) },
          )
        }

        is PrototypeDestination.SelectLabels -> {
          SelectLabelsScreen(
              onClose = ::navigateBack,
              onDone = ::navigateBack,
              onCancel = ::navigateBack,
              onApply = { chosenLabels ->
                selectedExpenseLabels = chosenLabels.map { "#${it.name.removePrefix("#")}" }
                navigateBack()
              },
              onProfileClick = { navigateTo(PrototypeDestination.Settings) },
          )
        }

        is PrototypeDestination.CategoryList -> {
          CategoryListScreen(
              onBackClick = ::navigateBack,
              onProfileClick = { navigateTo(PrototypeDestination.Settings) },
              onCategoryClick = { cat ->
                navigateTo(PrototypeDestination.CategoryDetail(cat.name))
              },
          )
        }

        is PrototypeDestination.CategoryDetail -> {
          CategoryDetailScreen(
              categoryName = destination.categoryName,
              onBackClick = ::navigateBack,
              onProfileClick = { navigateTo(PrototypeDestination.Settings) },
              onRenameClick = { navigateTo(PrototypeDestination.AddCategory) },
              onChangeIconClick = { navigateTo(PrototypeDestination.AddCategory) },
          )
        }

        is PrototypeDestination.AddCategory -> {
          AddCategoryScreen(
              onBackClick = ::navigateBack,
              onProfileClick = { navigateTo(PrototypeDestination.Settings) },
              onSaveCategory = { name, color, icon -> navigateBack() },
          )
        }
      }
    }
  }
}

@ScreenPreviews
@Composable
private fun PrototypeAppScreenPreview() {
  PrototypeAppScreen()
}
