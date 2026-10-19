package com.base.design.globalcomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.base.design.ui.ComponentPreviews

/**
 * Top app bar for primary screens and sub-screens.
 *
 * Features a clean, single-line title and optional action items (e.g. profile avatar,
 * notifications).
 */
@Composable
fun PocketyTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
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
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
      },
      actions = actions,
  )
}

/**
 * Top app bar for sub-screens, detail views, and creation flows (Add Expense, Pocket Settings,
 * Category Detail).
 *
 * Always displays a back button (ArrowBack) on the left to navigate back to the previous screen.
 */
@Composable
fun PocketyTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
  TopAppBar(
      modifier = modifier,
      colors =
          TopAppBarDefaults.topAppBarColors(
              containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
              titleContentColor = MaterialTheme.colorScheme.onSurface,
          ),
      navigationIcon = {
        IconButton(onClick = onBackClick) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Go back",
              tint = MaterialTheme.colorScheme.onSurface,
          )
        }
      },
      title = {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
      },
      actions = actions,
  )
}

/** Reusable user profile avatar button for top bar actions. */
@Composable
fun PocketyProfileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  IconButton(
      onClick = onClick,
      modifier = modifier,
  ) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.size(32.dp),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile & Settings",
            modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}

@ComponentPreviews
@Composable
private fun PocketyTopAppBarTabPreview() {
  PocketyTopAppBar(
      title = "Pockety",
      actions = { PocketyProfileButton(onClick = {}) },
  )
}

@ComponentPreviews
@Composable
private fun PocketyTopAppBarDetailPreview() {
  PocketyTopAppBar(
      title = "Pocket Settings",
      onBackClick = {},
  )
}
