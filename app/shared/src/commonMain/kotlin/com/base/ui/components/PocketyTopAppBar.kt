package com.base.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.base.ui.ComponentPreviews
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.back_action
import pockety.app.shared.generated.resources.settings_title

@Suppress("LongParameterList")
@Composable
fun PocketyTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
) {
  TopAppBar(
      modifier = modifier,
      colors = colors,
      navigationIcon = navigationIcon,
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
 * Top app bar for sub-screens, detail views, and creation flows.
 *
 * Always displays a back button (ArrowBack) on the left to navigate back to the previous screen.
 */
@Composable
fun PocketyTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    backActionTestTag: String = "back-button",
    actions: @Composable RowScope.() -> Unit = {},
) {
  PocketyTopAppBar(
      title = title,
      modifier = modifier,
      navigationIcon = {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.testTag(backActionTestTag),
        ) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.back_action),
              tint = MaterialTheme.colorScheme.onSurface,
          )
        }
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
            contentDescription = stringResource(Res.string.settings_title),
            modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}

/** Backward-compatible overload supporting subtitle. */
@Suppress("LongParameterList")
@Composable
fun PocketyTopAppBar(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
) {
  TopAppBar(
      modifier = modifier,
      colors = colors,
      navigationIcon = navigationIcon,
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
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
          )
        }
      },
      actions = actions,
  )
}

@ComponentPreviews
@Composable
private fun PocketyTopAppBarPreview() {
  PocketyTopAppBar(
      title = "Home",
  )
}

@ComponentPreviews
@Composable
private fun PocketyTopAppBarBackPreview() {
  PocketyTopAppBar(
      title = "Pocket Settings",
      onBackClick = {},
  )
}
