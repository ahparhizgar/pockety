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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.base.design.ui.ComponentPreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketyTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
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
private fun PocketyTopBarPreview() {
  PocketyTopBar(
      title = "Home",
      subtitle = "Pockety",
  )
}
