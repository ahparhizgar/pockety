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

package com.base.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.base.design.ui.ComponentPreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketyTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    showSearch: Boolean = true,
    showNotifications: Boolean = true,
    notificationCount: Int = 1,
    actions: @Composable RowScope.() -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
  TopAppBar(
      modifier = modifier,
      colors =
          TopAppBarDefaults.topAppBarColors(
              containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
              titleContentColor = MaterialTheme.colorScheme.onSurface,
          ),
      navigationIcon = { navigationIcon?.invoke() },
      title = {
        Column {
          if (subtitle != null) {
            Text(
                text = subtitle.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
            )
          }
          Text(
              text = title,
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurface,
          )
        }
      },
      actions = {
        if (showSearch) {
          IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
        if (showNotifications) {
          Box {
            IconButton(onClick = onNotificationsClick) {
              Icon(
                  imageVector = Icons.Default.Notifications,
                  contentDescription = "Notifications",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            if (notificationCount > 0) {
              Box(
                  modifier =
                      Modifier.align(Alignment.TopEnd)
                          .padding(top = 8.dp, end = 8.dp)
                          .size(8.dp)
                          .clip(CircleShape)
                          .background(MaterialTheme.colorScheme.primaryContainer),
              )
            }
          }
        }
        actions()
        Spacer(modifier = Modifier.width(4.dp))
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            onClick = onProfileClick,
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp),
            )
          }
        }
        Spacer(modifier = Modifier.width(12.dp))
      },
  )
}

@ComponentPreviews
@Composable
private fun PocketyTopBarPreview() {
  PocketyTopBar(
      title = "Home",
      subtitle = "Pockety",
  )
}
