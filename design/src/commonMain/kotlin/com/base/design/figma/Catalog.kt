package com.base.design.figma

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewQuilt
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.base.design.features.addcategory.AddCategoryScreen
import com.base.design.features.addpocket.AddPocketScreen
import com.base.design.features.addpocket.ManualSetupToggleShowcase
import com.base.design.features.addpocket.SelectPocketCategoriesScreen
import com.base.design.features.categorydetail.CategoryDetailScreen
import com.base.design.features.categorylist.CategoryListScreen
import com.base.design.features.categorylist.defaultCategoryList
import com.base.design.features.editexpense.EditExpenseScreen
import com.base.design.features.home.HomeScreen
import com.base.design.features.home.rememberDefaultHomeScreenState
import com.base.design.features.inbox.InboxScreen
import com.base.design.features.monthdetail.MonthDetailScreen
import com.base.design.features.pocketdetail.PocketDetailScreen
import com.base.design.features.pockets.PocketsScreen
import com.base.design.features.pocketsettings.PocketSettingsScreen
import com.base.design.features.privacypolicy.PrivacyPolicyScreen
import com.base.design.features.prototype.PrototypeAppScreen
import com.base.design.features.selectcategory.SelectCategoryScreen
import com.base.design.features.selectlabels.SelectLabelsScreen
import com.base.design.features.selectpocket.SelectPocketScreen
import com.base.design.features.settings.SettingsScreen
import com.base.design.ui.FigmaBoardPreviews

/**
 * Aggregated Figma canvas board displaying screens of the Pockety application.
 *
 * Organized by flows:
 * 1. Primary Navigation Screens (Home, Inbox, Pockets, Month Detail)
 * 2. Pocket Management Flows (Pocket Detail, Pocket Settings, Settings)
 * 3. Expense & Tagging Flows (Edit Expense, Select Category, Select Labels)
 * 4. Taxonomy & Setup Flows (Category List, Category Detail, Add Category)
 */
enum class FigmaDesignPage(val label: String, val icon: ImageVector) {
  All("All Features", Icons.Default.Layers),
  PrimaryNav("01. Primary Navigation", Icons.Default.Dashboard),
  PocketSettings("02. Pockets & Settings", Icons.Default.Tune),
  ExpenseFlow("03. Expense & Tags", Icons.Default.LocalOffer),
  Categories("04. Categories & Taxonomy", Icons.Default.Category),
  Prototype("05. Interactive Prototype", Icons.Default.SmartDisplay),
}

val figmaPageSections: Map<FigmaDesignPage, List<FigmaDesignSection>> =
    mapOf(
        FigmaDesignPage.PrimaryNav to
            listOf(
                FigmaDesignSection(
                    title = "01. Primary Navigation Hub",
                    subtitle =
                        "Main bottom navigation destinations with overview analytics and incoming SMS feed",
                    icon = Icons.AutoMirrored.Filled.ViewQuilt,
                    screens =
                        listOf(
                            FigmaScreenItem("01", "Home", "Main Tab") { HomeScreen() },
                            FigmaScreenItem("01b", "Home (Permission Prompt)", "SMS Permission") {
                              HomeScreen(
                                  state =
                                      rememberDefaultHomeScreenState()
                                          .copy(
                                              isSmsPermissionGranted = false,
                                          ),
                              )
                            },
                            FigmaScreenItem("02", "Inbox (SMS Extraction)", "Smart Extraction") {
                              InboxScreen()
                            },
                            FigmaScreenItem("02b", "Inbox (Clean / Empty)", "All Processed") {
                              InboxScreen(candidates = emptyList())
                            },
                            FigmaScreenItem("04", "Pockets", "Budget Overview") { PocketsScreen() },
                            FigmaScreenItem("05", "Month Detail (Expenses)", "Analytics Tab") {
                              MonthDetailScreen()
                            },
                        ),
                ),
            ),
        FigmaDesignPage.PocketSettings to
            listOf(
                FigmaDesignSection(
                    title = "02. Pocket Management & App Settings",
                    subtitle =
                        "Granular balance ledger, monthly top-up auto refill configuration, and preferences",
                    icon = Icons.Default.AutoAwesome,
                    screens =
                        listOf(
                            FigmaScreenItem("07", "Pocket Detail", "Ledger View") {
                              PocketDetailScreen()
                            },
                            FigmaScreenItem("07b", "Pocket Detail (Empty)", "No Transactions") {
                              PocketDetailScreen(items = emptyList())
                            },
                            FigmaScreenItem("08", "Pocket Settings", "Configuration") {
                              PocketSettingsScreen(isNewPocket = false)
                            },
                            FigmaScreenItem("08b", "New Pocket", "Creation") {
                              PocketSettingsScreen(
                                  initialPocketName = "",
                                  initialCurrentBalance = "0",
                                  isNewPocket = true,
                              )
                            },
                            FigmaScreenItem(
                                "08c",
                                "New Pocket (Select Categories)",
                                "Step 1 \u00B7 None Picked",
                            ) {
                              SelectPocketCategoriesScreen()
                            },
                            FigmaScreenItem(
                                "08d",
                                "New Pocket (Select Categories, One)",
                                "Step 1 \u00B7 Simple Next",
                            ) {
                              SelectPocketCategoriesScreen(initialSelectedIds = setOf("cafe"))
                            },
                            FigmaScreenItem(
                                "08e",
                                "New Pocket (Select Categories, Many)",
                                "Step 1 \u00B7 Advanced Next",
                            ) {
                              SelectPocketCategoriesScreen(
                                  initialSelectedIds = setOf("cafe", "groceries"),
                              )
                            },
                            FigmaScreenItem(
                                "08f",
                                "New Pocket (Simple)",
                                "Step 2 \u00B7 Inherits Identity",
                            ) {
                              AddPocketScreen(initialCategoryId = "cafe")
                            },
                            FigmaScreenItem(
                                "08g",
                                "New Pocket (Advanced)",
                                "Step 2 \u00B7 Multi-Category",
                            ) {
                              AddPocketScreen(
                                  manualExpanded = true,
                                  initialLinkedCategoryIds = setOf("cafe", "groceries"),
                              )
                            },
                            FigmaScreenItem("08h", "New Pocket (Manual Toggle)", "Quiet Row") {
                              ManualSetupToggleShowcase()
                            },
                            FigmaScreenItem(
                                "08i",
                                "New Pocket (Manual Confirm)",
                                "Enter Advanced",
                            ) {
                              AddPocketScreen(
                                  initialCategoryId = "cafe",
                                  manualConfirmShown = true,
                              )
                            },
                            FigmaScreenItem("06", "App Settings", "Preferences") {
                              SettingsScreen()
                            },
                            FigmaScreenItem("15", "Privacy Policy", "Legal & Security") {
                              PrivacyPolicyScreen()
                            },
                        ),
                ),
            ),
        FigmaDesignPage.ExpenseFlow to
            listOf(
                FigmaDesignSection(
                    title = "03. Expense Creation & Taxonomy Selection",
                    subtitle =
                        "Add / Edit transaction modal with category selector and custom label checklist",
                    icon = Icons.Default.Palette,
                    screens =
                        listOf(
                            FigmaScreenItem("03", "Add / Edit Expense", "Transaction Editor") {
                              EditExpenseScreen()
                            },
                            FigmaScreenItem("09", "Select Category", "Full Screen") {
                              SelectCategoryScreen()
                            },
                            FigmaScreenItem("09b", "Select Pocket", "Full Screen") {
                              SelectPocketScreen()
                            },
                            FigmaScreenItem("14", "Select Labels", "Full Screen") {
                              SelectLabelsScreen()
                            },
                            FigmaScreenItem(
                                "14b",
                                "Select Labels (Search)",
                                "Live Filter & Create",
                            ) {
                              SelectLabelsScreen(initialSearchQuery = "sdfs")
                            },
                        ),
                ),
            ),
        FigmaDesignPage.Categories to
            listOf(
                FigmaDesignSection(
                    title = "04. Category Management & Icon Selection",
                    subtitle =
                        "Custom category list, spending allocation breakdown, and new category wizard with icon/color customization",
                    icon = Icons.AutoMirrored.Filled.ViewQuilt,
                    screens =
                        listOf(
                            FigmaScreenItem("10", "Category List", "Taxonomy") {
                              CategoryListScreen()
                            },
                            FigmaScreenItem("10b", "Category List (Empty)", "No Categories") {
                              CategoryListScreen(initialCategories = emptyList())
                            },
                            FigmaScreenItem("10c", "Category List (Search)", "Live Filter") {
                              CategoryListScreen(initialSearchQuery = "gro")
                            },
                            FigmaScreenItem("10d", "Category List (No Results)", "Search Empty") {
                              CategoryListScreen(initialSearchQuery = "unknownxyz")
                            },
                            FigmaScreenItem(
                                "10e",
                                "Category List (Delete)",
                                "Delete Confirmation",
                            ) {
                              val categories = defaultCategoryList()
                              CategoryListScreen(
                                  initialCategories = categories,
                                  initialCategoryPendingDeletion = categories.first(),
                              )
                            },
                            FigmaScreenItem(
                                "10f",
                                "Category List (Reorder)",
                                "Drag to Reorder",
                            ) {
                              val categories = defaultCategoryList()
                              CategoryListScreen(
                                  initialCategories = categories,
                                  initialDraggingCategory = categories[1],
                              )
                            },
                            FigmaScreenItem("11", "Category Detail", "Spend History") {
                              CategoryDetailScreen()
                            },
                            FigmaScreenItem("13", "Add Category", "Creation Form") {
                              AddCategoryScreen()
                            },
                        ),
                ),
            ),
        FigmaDesignPage.Prototype to
            listOf(
                FigmaDesignSection(
                    title = "05. Interactive Semi-Functional Prototype",
                    subtitle =
                        "Fully clickable end-to-end prototype linking all 14 screens, bottom tabs, expense creation, and budget pockets",
                    icon = Icons.Default.SmartDisplay,
                    screens =
                        listOf(
                            FigmaScreenItem("00", "Interactive App Prototype", "Interactive") {
                              PrototypeAppScreen()
                            },
                        ),
                ),
            ),
    )

@FigmaBoardPreviews
@Composable
private fun FigmaBoardPreview() {
  FigmaBoard()
}
