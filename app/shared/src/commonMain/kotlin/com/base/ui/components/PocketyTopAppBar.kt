package com.base.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.base.ui.ComponentPreviews

@Suppress("LongParameterList")
@Composable
fun PocketyTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = "POCKETY",
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
private fun PocketyTopAppBarCustomSubtitlePreview() {
  PocketyTopAppBar(
      title = "Expenses",
      subtitle = "Overview",
  )
}
