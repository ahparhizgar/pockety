package com.base.design.features.prototype

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
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
import com.base.design.features.selectpocket.SelectPocketScreen
import com.base.design.features.settings.SettingsScreen
import com.base.design.globalcomponents.PocketyTab
import com.base.design.ui.ScreenPreviews

sealed interface PrototypeDestination : NavKey {
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

  data object SelectPocket : PrototypeDestination

  data object SelectLabels : PrototypeDestination

  data object CategoryList : PrototypeDestination

  data class CategoryDetail(val categoryName: String = "Groceries & Supermarket") :
      PrototypeDestination

  data object AddCategory : PrototypeDestination
}

private fun <T : NavKey> SnapshotStateList<T>.navigateTo(key: T) {
  add(key)
}

private fun <T : NavKey> SnapshotStateList<T>.navigateBack(): Boolean {
  if (size > 1) {
    removeAt(lastIndex)
    return true
  }
  return false
}

private fun <T : NavKey> SnapshotStateList<T>.setRoot(key: T) {
  clear()
  add(key)
}

/**
 * Interactive Prototype Screen linking all 14 screens of Pockety into a cohesive, responsive,
 * semi-functional interactive app flow.
 */
@Composable
fun PrototypeAppScreen(
    modifier: Modifier = Modifier,
) {
  val backStack = remember { mutableStateListOf<PrototypeDestination>(PrototypeDestination.Home) }

  fun switchTab(tab: PocketyTab) {
    val dest =
        when (tab) {
          PocketyTab.Home -> PrototypeDestination.Home
          PocketyTab.Inbox -> PrototypeDestination.Inbox
          PocketyTab.Pockets -> PrototypeDestination.Pockets
          PocketyTab.Analytics -> PrototypeDestination.Analytics
        }
    backStack.setRoot(dest)
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
  var selectedExpensePocket by remember { mutableStateOf<String?>("Coffee Pocket") }

  NavDisplay(
      backStack = backStack,
      modifier = modifier,
      entryProvider =
          entryProvider {
            entry<PrototypeDestination.Home> {
              HomeScreen(
                  state =
                      homeState.copy(
                          undecidedExpenseCount = candidates.size,
                          inboxBadgeCount = candidates.size,
                      ),
                  onTabSelected = ::switchTab,
                  onUndecidedExpensesClick = { backStack.navigateTo(PrototypeDestination.Inbox) },
                  onGrantSmsPermissionClick = { backStack.navigateTo(PrototypeDestination.Inbox) },
                  onDateClick = { backStack.navigateTo(PrototypeDestination.Analytics) },
                  onAllMonthsClick = { backStack.navigateTo(PrototypeDestination.Analytics) },
                  onManagePocketsClick = { backStack.navigateTo(PrototypeDestination.Pockets) },
                  onPocketClick = { pocket ->
                    backStack.navigateTo(PrototypeDestination.PocketDetail(pocket.name))
                  },
                  onAddExpenseClick = {
                    backStack.navigateTo(
                        PrototypeDestination.EditExpense(
                            initialAmount = "320,000",
                            initialCategory = selectedExpenseCategory,
                        ),
                    )
                  },
                  onProfileClick = { backStack.navigateTo(PrototypeDestination.Settings) },
              )
            }

            entry<PrototypeDestination.Inbox> {
              InboxScreen(
                  candidates = candidates,
                  selectedTab = PocketyTab.Inbox,
                  totalCount = candidates.size,
                  onTabSelected = ::switchTab,
                  onProfileClick = { backStack.navigateTo(PrototypeDestination.Settings) },
                  onDismissClick = { candidate -> candidates.remove(candidate) },
                  onCategoryClick = { candidate, _ -> candidates.remove(candidate) },
                  onOpenEditClick = { candidate ->
                    backStack.navigateTo(
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

            entry<PrototypeDestination.Pockets> {
              PocketsScreen(
                  pockets = pockets,
                  selectedTab = PocketyTab.Pockets,
                  onTabSelected = ::switchTab,
                  onProfileClick = { backStack.navigateTo(PrototypeDestination.Settings) },
                  onAddPocketClick = {
                    backStack.navigateTo(
                        PrototypeDestination.PocketSettings("New Budget Pocket"),
                    )
                  },
                  onHistoryClick = { pocket ->
                    backStack.navigateTo(PrototypeDestination.PocketDetail(pocket.name))
                  },
                  onSettingsClick = { pocket ->
                    backStack.navigateTo(PrototypeDestination.PocketSettings(pocket.name))
                  },
                  onTopUpClick = { pocket ->
                    backStack.navigateTo(PrototypeDestination.PocketDetail(pocket.name))
                  },
              )
            }

            entry<PrototypeDestination.Analytics> {
              MonthDetailScreen(
                  selectedTab = PocketyTab.Analytics,
                  onTabSelected = ::switchTab,
                  onProfileClick = { backStack.navigateTo(PrototypeDestination.Settings) },
                  onCategoryClick = { category ->
                    backStack.navigateTo(PrototypeDestination.CategoryDetail(category.name))
                  },
                  onExpenseClick = { expense ->
                    backStack.navigateTo(
                        PrototypeDestination.EditExpense(
                            initialAmount = expense.amountFormatted,
                            initialCategory = expense.title,
                            initialDescription = expense.subtitle,
                        ),
                    )
                  },
              )
            }

            entry<PrototypeDestination.Settings> {
              SettingsScreen(
                  onBackClick =
                      if (backStack.size > 1) {
                        { backStack.navigateBack() }
                      } else {
                        null
                      },
                  onTabSelected = ::switchTab,
                  onCategoriesClick = { backStack.navigateTo(PrototypeDestination.CategoryList) },
                  onLabelsClick = { backStack.navigateTo(PrototypeDestination.SelectLabels) },
              )
            }

            entry<PrototypeDestination.PocketDetail> { destination ->
              PocketDetailScreen(
                  pocketTitle = destination.pocketName,
                  onBackClick = { backStack.navigateBack() },
                  onSettingsClick = {
                    backStack.navigateTo(
                        PrototypeDestination.PocketSettings(destination.pocketName)
                    )
                  },
                  onConfigureClick = {
                    backStack.navigateTo(
                        PrototypeDestination.PocketSettings(destination.pocketName)
                    )
                  },
                  onProfileClick = { backStack.navigateTo(PrototypeDestination.Settings) },
              )
            }

            entry<PrototypeDestination.PocketSettings> { destination ->
              PocketSettingsScreen(
                  initialPocketName = destination.pocketName,
                  onBackClick = { backStack.navigateBack() },
                  onProfileClick = { backStack.navigateTo(PrototypeDestination.Settings) },
                  onChangeCategoryClick = {
                    backStack.navigateTo(PrototypeDestination.SelectCategory)
                  },
                  onSaveChangesClick = { _, _, _ -> backStack.navigateBack() },
                  onDeletePocketClick = { backStack.navigateBack() },
              )
            }

            entry<PrototypeDestination.EditExpense> { destination ->
              var editAmount by remember { mutableStateOf(destination.initialAmount) }
              var editDesc by remember { mutableStateOf(destination.initialDescription) }

              EditExpenseScreen(
                  amount = editAmount,
                  selectedCategoryName = selectedExpenseCategory,
                  selectedCategoryIcon = selectedExpenseIcon,
                  description = editDesc,
                  labels = selectedExpenseLabels,
                  pocketName = selectedExpensePocket,
                  onAmountChange = { editAmount = it },
                  onDescriptionChange = { editDesc = it },
                  onBackClick = { backStack.navigateBack() },
                  onCloseClick = { backStack.navigateBack() },
                  onChangeCategoryClick = {
                    backStack.navigateTo(PrototypeDestination.SelectCategory)
                  },
                  onClearCategoryClick = { selectedExpenseCategory = "" },
                  onPocketClick = { backStack.navigateTo(PrototypeDestination.SelectPocket) },
                  onClearPocketClick = { selectedExpensePocket = null },
                  onAddLabelClick = { backStack.navigateTo(PrototypeDestination.SelectLabels) },
                  onRemoveLabel = { label ->
                    selectedExpenseLabels = selectedExpenseLabels.filter { it != label }
                  },
                  onSaveExpense = { backStack.navigateBack() },
                  onDismissSms = { backStack.navigateBack() },
              )
            }

            entry<PrototypeDestination.SelectPocket> {
              SelectPocketScreen(
                  initialSelectedPocketId = "cafe",
                  onDismissRequest = { backStack.navigateBack() },
                  onPocketSelected = { pocket ->
                    selectedExpensePocket = if (pocket.id == "no_pocket") null else pocket.name
                    backStack.navigateBack()
                  },
                  onCreatePocketClick = {
                    backStack.navigateTo(PrototypeDestination.PocketSettings("New Pocket"))
                  },
              )
            }

            entry<PrototypeDestination.SelectCategory> {
              SelectCategoryScreen(
                  initialSelectedCategoryId = "dining",
                  onDismissRequest = { backStack.navigateBack() },
                  onCategorySelected = { category ->
                    selectedExpenseCategory = category.name
                    selectedExpenseIcon = category.icon
                    backStack.navigateBack()
                  },
                  onCreateCategoryClick = {
                    backStack.navigateTo(PrototypeDestination.AddCategory)
                  },
              )
            }

            entry<PrototypeDestination.SelectLabels> {
              SelectLabelsScreen(
                  onClose = { backStack.navigateBack() },
                  onDone = { backStack.navigateBack() },
                  onCancel = { backStack.navigateBack() },
                  onApply = { chosenLabels ->
                    selectedExpenseLabels = chosenLabels.map { "#${it.name.removePrefix("#")}" }
                    backStack.navigateBack()
                  },
                  onProfileClick = { backStack.navigateTo(PrototypeDestination.Settings) },
              )
            }

            entry<PrototypeDestination.CategoryList> {
              CategoryListScreen(
                  onBackClick = { backStack.navigateBack() },
                  onProfileClick = { backStack.navigateTo(PrototypeDestination.Settings) },
                  onCategoryClick = { cat ->
                    backStack.navigateTo(PrototypeDestination.CategoryDetail(cat.name))
                  },
              )
            }

            entry<PrototypeDestination.CategoryDetail> { destination ->
              CategoryDetailScreen(
                  categoryName = destination.categoryName,
                  onBackClick = { backStack.navigateBack() },
                  onProfileClick = { backStack.navigateTo(PrototypeDestination.Settings) },
                  onRenameClick = { backStack.navigateTo(PrototypeDestination.AddCategory) },
                  onChangeIconClick = { backStack.navigateTo(PrototypeDestination.AddCategory) },
              )
            }

            entry<PrototypeDestination.AddCategory> {
              AddCategoryScreen(
                  onBackClick = { backStack.navigateBack() },
                  onProfileClick = { backStack.navigateTo(PrototypeDestination.Settings) },
                  onSaveCategory = { _, _, _ -> backStack.navigateBack() },
              )
            }
          },
  )
}

@ScreenPreviews
@Composable
private fun PrototypeAppScreenPreview() {
  PrototypeAppScreen()
}
