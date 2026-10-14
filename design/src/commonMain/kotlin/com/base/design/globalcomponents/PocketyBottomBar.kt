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

package com.base.design.globalcomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.design.ui.ComponentPreviews

enum class PocketyTab(
    val label: String,
    val icon: ImageVector,
) {
  Home("Home", Icons.Default.Home),
  Inbox("Inbox", Icons.Default.Inbox),
  Pockets("Pockets", Icons.Default.AccountBalanceWallet),
  Analytics("Analytics", Icons.Default.Insights),
}

@Composable
fun PocketyBottomBar(
    selectedTab: PocketyTab,
    modifier: Modifier = Modifier,
    inboxBadgeCount: Int = 8,
    onTabSelected: (PocketyTab) -> Unit = {},
) {
  NavigationBar(
      modifier = modifier.fillMaxWidth(),
      containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
      tonalElevation = 8.dp,
  ) {
    PocketyTab.entries.forEach { tab ->
      val selected = tab == selectedTab
      NavigationBarItem(
          selected = selected,
          onClick = { onTabSelected(tab) },
          colors =
              NavigationBarItemDefaults.colors(
                  indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                  selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                  unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                  selectedTextColor = MaterialTheme.colorScheme.primaryContainer,
                  unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
              ),
          icon = {
            Box {
              Icon(
                  imageVector = tab.icon,
                  contentDescription = tab.label,
                  modifier = Modifier.size(24.dp),
              )
              if (tab == PocketyTab.Inbox && inboxBadgeCount > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-4).dp),
                ) {
                  Text(
                      text = "$inboxBadgeCount",
                      style =
                          MaterialTheme.typography.labelSmall.copy(
                              fontSize = 10.sp,
                              lineHeight = 12.sp,
                          ),
                      color = MaterialTheme.colorScheme.onPrimary,
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                  )
                }
              }
            }
          },
          label = {
            Text(
                text = tab.label,
                style = MaterialTheme.typography.labelSmall,
            )
          },
      )
    }
  }
}

@ComponentPreviews
@Composable
private fun PocketyBottomBarPreview() {
  PocketyBottomBar(selectedTab = PocketyTab.Home)
}
