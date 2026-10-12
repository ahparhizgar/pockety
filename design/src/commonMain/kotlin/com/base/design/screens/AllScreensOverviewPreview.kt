@file:Suppress(
    "LongMethod",
    "LongParameterList",
    "TooManyFunctions",
    "MagicNumber",
    "MatchingDeclarationName",
    "SpreadOperator",
    "MaxLineLength",
    "UnusedPrivateFunction",
    "ComposableParamOrder",
    "PreviewNaming",
    "PreviewPublic",
    "ParameterNaming",
    "UnstableCollections",
)

package com.base.design.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.base.design.ui.FigmaBoardPreviews

/**
 * A device frame container that renders a screen within a standardized 390x844dp phone mockup with
 * an artboard title, step badge, and flow tag like in Figma.
 */
@Composable()
fun FigmaDeviceArtboard(
    number: String,
    title: String,
    tag: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
  Column(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    // Artboard Header
    Row(
        modifier = Modifier.width(390.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(8.dp),
        ) {
          Text(
              text = number,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
          )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
      }

      Surface(
          color = MaterialTheme.colorScheme.secondaryContainer,
          shape = CircleShape,
      ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
      }
    }

    // Phone Frame Mockup
    Surface(
        modifier = Modifier.width(390.dp).height(844.dp),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface,
        border =
            BorderStroke(
                1.5.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
            ),
        shadowElevation = 8.dp,
    ) {
      Box(
          modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(32.dp)),
      ) {
        content()
      }
    }
  }
}

@Composable
private fun FigmaSectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Surface(
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
        )
      }
    }
    Column {
      Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

/**
 * Aggregated Figma canvas board displaying all 14 screens of the Pockety application.
 *
 * Organized by flows:
 * 1. Primary Navigation Screens (Home, Inbox, Pockets, Month Detail)
 * 2. Pocket Management Flows (Pocket Detail, Pocket Settings, Settings)
 * 3. Expense & Tagging Flows (Edit Expense, Select Category, Select Labels)
 * 4. Taxonomy & Setup Flows (Category List, Category Detail, Add Category, Select Icon)
 */
@Composable
fun AllScreensFigmaBoard(
    modifier: Modifier = Modifier,
) {
  val horizontalScrollState = rememberScrollState()
  val verticalScrollState = rememberScrollState()

  Surface(
      modifier = modifier.fillMaxSize(),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
  ) {
    Column(
        modifier =
            Modifier.fillMaxSize()
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState)
                .padding(40.dp),
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

      // SECTION 1: Main Application Hub
      Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        FigmaSectionHeader(
            title = "01. Primary Navigation Hub",
            subtitle =
                "Main bottom navigation destinations with overview analytics and incoming SMS feed",
            icon = Icons.AutoMirrored.Filled.ViewQuilt,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(36.dp)) {
          FigmaDeviceArtboard(
              number = "01",
              title = "Home",
              tag = "Main Tab",
          ) {
            HomeScreen()
          }

          FigmaDeviceArtboard(
              number = "02",
              title = "Inbox (SMS Extraction)",
              tag = "Smart Extraction",
          ) {
            InboxScreen()
          }

          FigmaDeviceArtboard(
              number = "04",
              title = "Pockets",
              tag = "Budget Overview",
          ) {
            PocketsScreen()
          }

          FigmaDeviceArtboard(
              number = "05",
              title = "Month Detail (Expenses)",
              tag = "Analytics Tab",
          ) {
            MonthDetailScreen()
          }
        }
      }

      // SECTION 2: Pocket & Settings Flows
      Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        FigmaSectionHeader(
            title = "02. Pocket Management & App Settings",
            subtitle =
                "Granular balance ledger, monthly top-up auto refill configuration, and preferences",
            icon = Icons.Default.AutoAwesome,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(36.dp)) {
          FigmaDeviceArtboard(
              number = "07",
              title = "Pocket Detail",
              tag = "Ledger View",
          ) {
            PocketDetailScreen()
          }

          FigmaDeviceArtboard(
              number = "08",
              title = "Pocket Settings",
              tag = "Configuration",
          ) {
            PocketSettingsScreen()
          }

          FigmaDeviceArtboard(
              number = "06",
              title = "App Settings",
              tag = "Preferences",
          ) {
            SettingsScreen()
          }
        }
      }

      // SECTION 3: Expense Logging & Tagging Flow
      Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        FigmaSectionHeader(
            title = "03. Expense Creation & Taxonomy Selection",
            subtitle =
                "Add / Edit transaction modal with category bottom sheet and custom label checklist",
            icon = Icons.Default.Palette,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(36.dp)) {
          FigmaDeviceArtboard(
              number = "03",
              title = "Add / Edit Expense",
              tag = "Transaction Editor",
          ) {
            EditExpenseScreen()
          }

          FigmaDeviceArtboard(
              number = "09",
              title = "Select Category",
              tag = "Bottom Sheet",
          ) {
            SelectCategoryScreen()
          }

          FigmaDeviceArtboard(
              number = "14",
              title = "Select Labels",
              tag = "Tag Checklist",
          ) {
            SelectLabelsScreen()
          }
        }
      }

      // SECTION 4: Category Management & Icon Studio
      Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        FigmaSectionHeader(
            title = "04. Category Management & Icon Selection",
            subtitle =
                "Custom category list, spending allocation breakdown, new category wizard, and icon picker",
            icon = Icons.AutoMirrored.Filled.ViewQuilt,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(36.dp)) {
          FigmaDeviceArtboard(
              number = "10",
              title = "Category List",
              tag = "Taxonomy",
          ) {
            CategoryListScreen()
          }

          FigmaDeviceArtboard(
              number = "11",
              title = "Category Detail",
              tag = "Spend History",
          ) {
            CategoryDetailScreen()
          }

          FigmaDeviceArtboard(
              number = "13",
              title = "Add Category",
              tag = "Creation Form",
          ) {
            AddCategoryScreen()
          }

          FigmaDeviceArtboard(
              number = "12",
              title = "Select Icon",
              tag = "Icon Studio",
          ) {
            SelectIconScreen()
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

@FigmaBoardPreviews
@Composable
private fun PrimaryTabsOverviewPreview() {
  Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest) {
    Row(
        modifier = Modifier.padding(24.dp).horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      FigmaDeviceArtboard(number = "01", title = "Home", tag = "Main Tab") { HomeScreen() }
      FigmaDeviceArtboard(number = "02", title = "Inbox", tag = "Smart Extraction") {
        InboxScreen()
      }
      FigmaDeviceArtboard(number = "04", title = "Pockets", tag = "Budget Overview") {
        PocketsScreen()
      }
      FigmaDeviceArtboard(number = "05", title = "Month Detail", tag = "Analytics Tab") {
        MonthDetailScreen()
      }
    }
  }
}

@FigmaBoardPreviews
@Composable
private fun PocketFlowOverviewPreview() {
  Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest) {
    Row(
        modifier = Modifier.padding(24.dp).horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      FigmaDeviceArtboard(number = "07", title = "Pocket Detail", tag = "Ledger View") {
        PocketDetailScreen()
      }
      FigmaDeviceArtboard(number = "08", title = "Pocket Settings", tag = "Configuration") {
        PocketSettingsScreen()
      }
      FigmaDeviceArtboard(number = "06", title = "App Settings", tag = "Preferences") {
        SettingsScreen()
      }
    }
  }
}

@FigmaBoardPreviews
@Composable
private fun ExpenseFlowOverviewPreview() {
  Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest) {
    Row(
        modifier = Modifier.padding(24.dp).horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      FigmaDeviceArtboard(number = "03", title = "Edit Expense", tag = "Transaction Editor") {
        EditExpenseScreen()
      }
      FigmaDeviceArtboard(number = "09", title = "Select Category", tag = "Bottom Sheet") {
        SelectCategoryScreen()
      }
      FigmaDeviceArtboard(number = "14", title = "Select Labels", tag = "Tag Checklist") {
        SelectLabelsScreen()
      }
    }
  }
}

@FigmaBoardPreviews
@Composable
private fun TaxonomyFlowOverviewPreview() {
  Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest) {
    Row(
        modifier = Modifier.padding(24.dp).horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      FigmaDeviceArtboard(number = "10", title = "Category List", tag = "Taxonomy") {
        CategoryListScreen()
      }
      FigmaDeviceArtboard(number = "11", title = "Category Detail", tag = "Spend History") {
        CategoryDetailScreen()
      }
      FigmaDeviceArtboard(number = "13", title = "Add Category", tag = "Creation Form") {
        AddCategoryScreen()
      }
      FigmaDeviceArtboard(number = "12", title = "Select Icon", tag = "Icon Studio") {
        SelectIconScreen()
      }
    }
  }
}
