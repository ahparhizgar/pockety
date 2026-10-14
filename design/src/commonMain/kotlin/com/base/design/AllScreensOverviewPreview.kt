package com.base.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewQuilt
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.base.design.features.addcategory.AddCategoryScreen
import com.base.design.features.categorydetail.CategoryDetailScreen
import com.base.design.features.categorylist.CategoryListScreen
import com.base.design.features.editexpense.EditExpenseScreen
import com.base.design.features.home.HomeScreen
import com.base.design.features.inbox.InboxScreen
import com.base.design.features.monthdetail.MonthDetailScreen
import com.base.design.features.pocketdetail.PocketDetailScreen
import com.base.design.features.pockets.PocketsScreen
import com.base.design.features.pocketsettings.PocketSettingsScreen
import com.base.design.features.selectcategory.SelectCategoryScreen
import com.base.design.features.selecticon.SelectIconScreen
import com.base.design.features.selectlabels.SelectLabelsScreen
import com.base.design.features.settings.SettingsScreen
import com.base.design.ui.FigmaBoardPreviews

/**
 * Aggregated Figma canvas board displaying all 14 screens of the Pockety application.
 *
 * Organized by flows:
 * 1. Primary Navigation Screens (Home, Inbox, Pockets, Month Detail)
 * 2. Pocket Management Flows (Pocket Detail, Pocket Settings, Settings)
 * 3. Expense & Tagging Flows (Edit Expense, Select Category, Select Labels)
 * 4. Taxonomy & Setup Flows (Category List, Category Detail, Add Category, Select Icon)
 */
enum class FigmaDesignPage(val label: String) {
  All("All Features"),
  PrimaryNav("01. Primary Navigation"),
  PocketSettings("02. Pockets & Settings"),
  ExpenseFlow("03. Expense & Tags"),
  Categories("04. Categories & Icons"),
}

data class FigmaScreenItem(
    val number: String,
    val title: String,
    val tag: String,
    val content: @Composable () -> Unit,
)

data class FigmaDesignSection(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val screens: List<FigmaScreenItem>,
)

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
                            FigmaScreenItem("02", "Inbox (SMS Extraction)", "Smart Extraction") {
                              InboxScreen()
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
                            FigmaScreenItem("08", "Pocket Settings", "Configuration") {
                              PocketSettingsScreen()
                            },
                            FigmaScreenItem("06", "App Settings", "Preferences") {
                              SettingsScreen()
                            },
                        ),
                ),
            ),
        FigmaDesignPage.ExpenseFlow to
            listOf(
                FigmaDesignSection(
                    title = "03. Expense Creation & Taxonomy Selection",
                    subtitle =
                        "Add / Edit transaction modal with category bottom sheet and custom label checklist",
                    icon = Icons.Default.Palette,
                    screens =
                        listOf(
                            FigmaScreenItem("03", "Add / Edit Expense", "Transaction Editor") {
                              EditExpenseScreen()
                            },
                            FigmaScreenItem("09", "Select Category", "Bottom Sheet") {
                              SelectCategoryScreen()
                            },
                            FigmaScreenItem("14", "Select Labels", "Tag Checklist") {
                              SelectLabelsScreen()
                            },
                        ),
                ),
            ),
        FigmaDesignPage.Categories to
            listOf(
                FigmaDesignSection(
                    title = "04. Category Management & Icon Selection",
                    subtitle =
                        "Custom category list, spending allocation breakdown, new category wizard, and icon picker",
                    icon = Icons.AutoMirrored.Filled.ViewQuilt,
                    screens =
                        listOf(
                            FigmaScreenItem("10", "Category List", "Taxonomy") {
                              CategoryListScreen()
                            },
                            FigmaScreenItem("11", "Category Detail", "Spend History") {
                              CategoryDetailScreen()
                            },
                            FigmaScreenItem("13", "Add Category", "Creation Form") {
                              AddCategoryScreen()
                            },
                            FigmaScreenItem("12", "Select Icon", "Icon Studio") {
                              SelectIconScreen()
                            },
                        ),
                ),
            ),
    )

@Composable
fun AllScreensFigmaBoard(
    modifier: Modifier = Modifier,
    deviceMode: FigmaDeviceMode = FigmaDeviceMode.Portrait,
    selectedPage: FigmaDesignPage = FigmaDesignPage.All,
) {
  val visibleSections =
      remember(selectedPage) {
        if (selectedPage == FigmaDesignPage.All) {
          figmaPageSections.values.flatten()
        } else {
          figmaPageSections[selectedPage] ?: emptyList()
        }
      }

  Surface(
      modifier = modifier,
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
  ) {
    Column(
        modifier = Modifier.padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(48.dp),
    ) {
      // Board Canvas Header
      Column(
          verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(56.dp),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                  imageVector = Icons.AutoMirrored.Filled.ViewQuilt,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.size(32.dp),
              )
            }
          }

          Column {
            Text(
                text = "Pockety — Design System & Screens Overview",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Verdant Clarity (M3 Expressive) • All 14 Screens UI Only",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
          }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 4.dp),
        ) {
          Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.secondaryContainer,
          ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSecondaryContainer,
                  modifier = Modifier.size(16.dp),
              )
              Text(
                  text = "14 Total Screens",
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSecondaryContainer,
              )
            }
          }

          Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
          ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(
                  imageVector = Icons.Default.Palette,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp),
              )
              Text(
                  text = "Forest Sage & Celadon Theme",
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.primary,
              )
            }
          }

          Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.surfaceContainerHigh,
          ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(16.dp),
              )
              Text(
                  text = "Keyboard Accessible • Tonal Layering",
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }

      // Render dynamically from map
      visibleSections.forEach { section ->
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
          FigmaSectionHeader(
              title = section.title,
              subtitle = section.subtitle,
              icon = section.icon,
          )

          Row(horizontalArrangement = Arrangement.spacedBy(36.dp)) {
            section.screens.forEach { screen ->
              FigmaDeviceArtboard(
                  number = screen.number,
                  title = screen.title,
                  tag = screen.tag,
                  deviceMode = deviceMode,
              ) {
                screen.content()
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

// -----------------------------------------------------------------------------
// Previews
// -----------------------------------------------------------------------------

@FigmaBoardPreviews
@Composable
private fun AllScreensFigmaBoardPreview() {
  AllScreensFigmaBoard()
}
