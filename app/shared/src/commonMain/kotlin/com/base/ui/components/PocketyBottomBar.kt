package com.base.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.feature.pocket.PocketGlyphIcon
import com.base.ui.ComponentPreviews
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.tab_analytics
import pockety.app.shared.generated.resources.tab_home
import pockety.app.shared.generated.resources.tab_inbox
import pockety.app.shared.generated.resources.tab_pockets
import pockety.app.shared.generated.resources.tab_settings

@Composable
fun PocketyBottomBar(
    selectedTab: PocketyTab,
    modifier: Modifier = Modifier,
    inboxBadgeCount: Int = 0,
    onTabSelect: (PocketyTab) -> Unit = {},
) {
  NavigationBar(
      modifier = modifier.fillMaxWidth().testTag("pockety-bottom-bar"),
      containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
      tonalElevation = 8.dp,
  ) {
    PocketyTab.entries.forEach { tab ->
      PocketyBottomBarItem(
          tab = tab,
          selected = tab == selectedTab,
          inboxBadgeCount = inboxBadgeCount,
          onTabSelect = onTabSelect,
      )
    }
  }
}

@Composable
private fun RowScope.PocketyBottomBarItem(
    tab: PocketyTab,
    selected: Boolean,
    inboxBadgeCount: Int,
    onTabSelect: (PocketyTab) -> Unit,
) {
  val label = stringResource(tab.labelRes)
  NavigationBarItem(
      modifier = Modifier.testTag("tab-${tab.name.lowercase()}"),
      selected = selected,
      onClick = { onTabSelect(tab) },
      colors =
          NavigationBarItemDefaults.colors(
              indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
              selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
              selectedTextColor = MaterialTheme.colorScheme.primary,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
          ),
      icon = {
        Box {
          if (tab == PocketyTab.Pockets) {
            PocketGlyphIcon(
                tint = LocalContentColor.current,
                stitchColor =
                    if (selected) {
                      MaterialTheme.colorScheme.secondaryContainer
                    } else {
                      MaterialTheme.colorScheme.surface
                    },
                modifier = Modifier.size(24.dp),
            )
          } else {
            Icon(
                imageVector = tab.icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
            )
          }
          if (tab == PocketyTab.Inbox && inboxBadgeCount > 0) {
            InboxBadge(count = inboxBadgeCount)
          }
        }
      },
      label = {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
        )
      },
  )
}

@Composable
private fun InboxBadge(count: Int) {
  Surface(
      color = MaterialTheme.colorScheme.primary,
      shape = CircleShape,
      modifier = Modifier.offset(x = 6.dp, y = (-4).dp).testTag("inbox-badge"),
  ) {
    Text(
        text = "$count",
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

enum class PocketyTab(
    val labelRes: StringResource,
    val icon: ImageVector,
) {
  Home(Res.string.tab_home, Icons.Default.Home),
  Inbox(Res.string.tab_inbox, Icons.Default.Inbox),
  Pockets(Res.string.tab_pockets, Icons.Default.AccountBalanceWallet),
  Analytics(Res.string.tab_analytics, Icons.Default.Insights),
  Settings(Res.string.tab_settings, Icons.Default.Settings),
}

@ComponentPreviews
@Composable
private fun PocketyBottomBarHomePreview() {
  PocketyBottomBar(selectedTab = PocketyTab.Home, inboxBadgeCount = 3)
}

@ComponentPreviews
@Composable
private fun PocketyBottomBarInboxPreview() {
  PocketyBottomBar(selectedTab = PocketyTab.Inbox, inboxBadgeCount = 0)
}

@ComponentPreviews
@Composable
private fun PocketyBottomBarPocketsPreview() {
  PocketyBottomBar(selectedTab = PocketyTab.Pockets, inboxBadgeCount = 0)
}
