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

package com.base.design.features

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.design.ui.ScreenPreviews

@Composable
fun SelectIconScreen(
    modifier: Modifier = Modifier,
    initialIconId: String = "restaurant",
    initialColorName: String = "Forest Sage",
    initialCategory: IconCategory = IconCategory.All,
    onCancelClick: () -> Unit = {},
    onApplyClick: (icon: IconItem, color: AccentColorOption) -> Unit = { _, _ -> },
) {
  val accentColors = defaultAccentColors()
  var selectedAccentColor by
      remember(initialColorName) {
        mutableStateOf(
            accentColors.firstOrNull { it.name == initialColorName } ?: accentColors.first()
        )
      }
  var selectedCategory by remember(initialCategory) { mutableStateOf(initialCategory) }
  var selectedIcon by
      remember(initialIconId) {
        mutableStateOf(defaultIcons.firstOrNull { it.id == initialIconId } ?: defaultIcons.first())
      }

  val filteredIcons =
      remember(selectedCategory) {
        if (selectedCategory == IconCategory.All) {
          defaultIcons
        } else {
          defaultIcons.filter { it.category == selectedCategory }
        }
      }

  Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.surface,
      topBar = {
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
          Surface(
              modifier = Modifier.size(width = 40.dp, height = 6.dp),
              shape = CircleShape,
              color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
          ) {}
        }
      },
  ) { innerPadding ->
    Box(
        modifier =
            Modifier.fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
      Surface(
          modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
          shape = RoundedCornerShape(28.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 8.dp,
      ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
          // Live Dynamic Preview & Header
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                  text = "CUSTOMIZER",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  letterSpacing = 0.04.sp,
                  fontWeight = FontWeight.SemiBold,
              )
              Text(
                  text = "Select Icon",
                  style = MaterialTheme.typography.headlineSmall,
                  color = MaterialTheme.colorScheme.onSurface,
                  fontWeight = FontWeight.SemiBold,
              )
            }
            IconPreviewBadge(
                icon = selectedIcon,
                accentColor = selectedAccentColor,
            )
          }

          // Section 1: Color Palette Selection
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                  text = "Accent Color",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface,
              )
              Text(
                  text = selectedAccentColor.name,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.SemiBold,
                  color = selectedAccentColor.color,
              )
            }
            ColorPaletteTrack(
                colors = accentColors,
                selectedColor = selectedAccentColor,
                onColorSelected = { selectedAccentColor = it },
            )
          }

          // Section 2: Category Chips Filter
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                  text = "Icon Category",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface,
              )
              Text(
                  text = "${filteredIcons.size} icons",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Medium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            IconCategoryChips(
                categories = IconCategory.entries,
                selectedCategory = selectedCategory,
                accentColor = selectedAccentColor,
                onCategorySelected = { selectedCategory = it },
            )
          }

          // Section 3: Icon Grid Canvas
          IconGrid(
              icons = filteredIcons,
              selectedIcon = selectedIcon,
              accentColor = selectedAccentColor,
              onIconSelected = { selectedIcon = it },
              modifier = Modifier.fillMaxWidth(),
          )

          // Action Buttons Pair
          Row(
              modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Surface(
                modifier = Modifier.weight(1f).height(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
                onClick = onCancelClick,
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
              }
            }
            Surface(
                modifier = Modifier.weight(1f).height(48.dp),
                shape = CircleShape,
                color = selectedAccentColor.color,
                contentColor = selectedAccentColor.onColor,
                shadowElevation = 2.dp,
                onClick = { onApplyClick(selectedIcon, selectedAccentColor) },
            ) {
              Row(
                  modifier = Modifier.fillMaxSize(),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Apply Icon",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
              }
            }
          }
        }
      }
    }
  }
}

@ScreenPreviews
@Composable
private fun SelectIconScreenPreview() {
  SelectIconScreen()
}

@ScreenPreviews
@Composable
private fun SelectIconScreenCustomSelectionPreview() {
  SelectIconScreen(
      initialIconId = "shopping_cart",
      initialColorName = "Crimson Berry",
      initialCategory = IconCategory.Shopping,
  )
}
