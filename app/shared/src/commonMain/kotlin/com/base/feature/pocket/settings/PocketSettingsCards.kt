package com.base.feature.pocket.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.base.db.CategoryEntity
import com.base.db.FakeCategoryFactory
import com.base.feature.category.add.resolveCategoryIcon
import com.base.ui.ComponentPreviews
import com.base.ui.money.MoneyTextField
import com.base.ui.money.currencySymbol
import com.base.ui.money.moneyAmount
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.associated_category_subtitle
import pockety.app.shared.generated.resources.associated_category_title
import pockety.app.shared.generated.resources.associated_category_unlinked_subtitle
import pockety.app.shared.generated.resources.cancel_action
import pockety.app.shared.generated.resources.clear_association_action
import pockety.app.shared.generated.resources.pocket_adjust_allocation_action
import pockety.app.shared.generated.resources.pocket_adjust_allocation_confirm
import pockety.app.shared.generated.resources.pocket_adjust_allocation_dialog_title
import pockety.app.shared.generated.resources.pocket_allocated_refill_label
import pockety.app.shared.generated.resources.pocket_auto_refill_desc
import pockety.app.shared.generated.resources.pocket_auto_refill_title
import pockety.app.shared.generated.resources.pocket_danger_zone_delete_action
import pockety.app.shared.generated.resources.pocket_danger_zone_desc
import pockety.app.shared.generated.resources.pocket_danger_zone_title
import pockety.app.shared.generated.resources.pocket_monthly_top_up_title
import pockety.app.shared.generated.resources.tap_to_select_category

@Composable
fun PocketAssociatedCategoryCard(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      PocketAssociatedCategoryHeader()

      PocketAssociatedCategoryItemSurface(
          category = state.selectedCategory,
          onSelectCategory = component::onSelectCategoryClicked,
          onClearCategory = component::onCategoryCleared,
      )
    }
  }
}

@Composable
private fun PocketAssociatedCategoryHeader(
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Icon(
          imageVector = Icons.Default.LocalOffer,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp),
      )
      Text(
          text = stringResource(Res.string.associated_category_title),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

@Composable
private fun PocketAssociatedCategoryItemSurface(
    category: CategoryEntity?,
    onSelectCategory: () -> Unit,
    onClearCategory: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val hasCategory = category != null

  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shadowElevation = 1.dp,
      onClick = onSelectCategory,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        PocketCategoryIconBadge(category = category)
        PocketCategoryTextDetails(category = category)
      }

      if (hasCategory) {
        IconButton(
            onClick = onClearCategory,
            modifier = Modifier.testTag("clear-category-btn"),
        ) {
          Icon(
              imageVector = Icons.Default.Close,
              contentDescription = stringResource(Res.string.clear_association_action),
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun PocketCategoryIconBadge(
    category: CategoryEntity?,
    modifier: Modifier = Modifier,
) {
  val hasCategory = category != null
  Surface(
      modifier = modifier.size(48.dp),
      shape = CircleShape,
      color =
          if (hasCategory) {
            MaterialTheme.colorScheme.secondaryContainer
          } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
          },
      contentColor =
          if (hasCategory) {
            MaterialTheme.colorScheme.onSecondaryContainer
          } else {
            MaterialTheme.colorScheme.onSurfaceVariant
          },
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
          imageVector =
              if (hasCategory) {
                resolveCategoryIcon(category.icon)
              } else {
                Icons.Default.Category
              },
          contentDescription = null,
          modifier = Modifier.size(24.dp),
      )
    }
  }
}

@Composable
private fun PocketCategoryTextDetails(
    category: CategoryEntity?,
    modifier: Modifier = Modifier,
) {
  val hasCategory = category != null
  Column(modifier = modifier) {
    Text(
        text = category?.name ?: stringResource(Res.string.tap_to_select_category),
        style = MaterialTheme.typography.titleMedium,
        color =
            if (hasCategory) {
              MaterialTheme.colorScheme.onSurface
            } else {
              MaterialTheme.colorScheme.onSurfaceVariant
            },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    Text(
        text =
            if (hasCategory) {
              stringResource(Res.string.associated_category_subtitle)
            } else {
              stringResource(Res.string.associated_category_unlinked_subtitle)
            },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
  }
}

@ComponentPreviews
@Composable
private fun PocketAssociatedCategoryCardPreview() {
  PocketAssociatedCategoryCard(
      component = FakePocketSettingsComponent(),
      state =
          FakePocketSettingsStateFactory.create(
              selectedCategory = FakeCategoryFactory.create(name = "Cafe & Dining"),
          ),
  )
}

@ComponentPreviews
@Composable
private fun PocketAssociatedCategoryCardUnlinkedPreview() {
  PocketAssociatedCategoryCard(
      component = FakePocketSettingsComponent(),
      state =
          FakePocketSettingsStateFactory.create(
              selectedCategory = null,
          ),
  )
}

@Composable
fun PocketMonthlyAllocationCard(
    component: PocketSettingsComponent,
    state: PocketSettingsState,
    modifier: Modifier = Modifier,
) {
  var showAdjustDialog by remember { mutableStateOf(false) }

  Surface(
      modifier = modifier.fillMaxWidth().testTag("monthly-allocation-card"),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      MonthlyAllocationHeader()

      MonthlyAllocationAmountDisplay(
          amount = state.monthlyTopUpAmount,
          onAdjustClick = { showAdjustDialog = true },
      )

      MonthlyAllocationAutoRefillRow(
          enabled = state.autoTopUpEnabled,
          onEnabledChange = { component.onAutoTopUpEnabledChanged(it) },
      )
    }
  }

  if (showAdjustDialog) {
    AdjustAllocationDialog(
        currentAmount = state.monthlyTopUpAmount,
        onConfirm = { newAmount ->
          component.onMonthlyTopUpAmountChanged(newAmount)
          showAdjustDialog = false
        },
        onDismiss = { showAdjustDialog = false },
    )
  }
}

@Composable
private fun MonthlyAllocationHeader(modifier: Modifier = Modifier) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Icon(
        imageVector = Icons.Default.EventRepeat,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(22.dp),
    )
    Text(
        text = stringResource(Res.string.pocket_monthly_top_up_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
  }
}

@Composable
private fun MonthlyAllocationAmountDisplay(
    amount: Long,
    onAdjustClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shadowElevation = 1.dp,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Column {
        Text(
            text = stringResource(Res.string.pocket_allocated_refill_label),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
              text = moneyAmount(amount),
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.testTag("allocated-refill-amount"),
          )
          Text(
              text = currencySymbol(),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
          )
        }
      }

      IconButton(
          onClick = onAdjustClick,
          modifier =
              Modifier.size(40.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surfaceContainer)
                  .testTag("adjust-allocation-btn"),
      ) {
        Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = stringResource(Res.string.pocket_adjust_allocation_action),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
      }
    }
  }
}

@Composable
private fun MonthlyAllocationAutoRefillRow(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(
        modifier = Modifier.weight(1f).padding(end = 12.dp),
    ) {
      Text(
          text = stringResource(Res.string.pocket_auto_refill_title),
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = stringResource(Res.string.pocket_auto_refill_desc),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    Switch(
        checked = enabled,
        onCheckedChange = onEnabledChange,
        thumbContent =
            if (enabled) {
              {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
              }
            } else null,
        colors =
            SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedIconColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
        modifier = Modifier.testTag("auto-refill-switch"),
    )
  }
}

@Composable
private fun AdjustAllocationDialog(
    currentAmount: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
  var textValue by remember {
    mutableStateOf(if (currentAmount > 0) currentAmount.toString() else "")
  }

  AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text(text = stringResource(Res.string.pocket_adjust_allocation_dialog_title)) },
      text = {
        MoneyTextField(
            value = textValue,
            onValueChange = { textValue = it },
            label = { Text(stringResource(Res.string.pocket_allocated_refill_label)) },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                      val parsed = textValue.toLongOrNull() ?: 0L
                      onConfirm(parsed)
                    },
                ),
            modifier = Modifier.fillMaxWidth().testTag("adjust-allocation-input"),
        )
      },
      confirmButton = {
        Button(
            onClick = {
              val parsed = textValue.toLongOrNull() ?: 0L
              onConfirm(parsed)
            },
            modifier = Modifier.testTag("confirm-allocation-btn"),
        ) {
          Text(text = stringResource(Res.string.pocket_adjust_allocation_confirm))
        }
      },
      dismissButton = {
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("cancel-allocation-btn"),
        ) {
          Text(text = stringResource(Res.string.cancel_action))
        }
      },
  )
}

@ComponentPreviews
@Composable
private fun PocketMonthlyAllocationCardEnabledPreview() {
  PocketMonthlyAllocationCard(
      component = FakePocketSettingsComponent(),
      state =
          FakePocketSettingsStateFactory.create(
              monthlyTopUpAmount = 500_000L,
              autoTopUpEnabled = true,
          ),
  )
}

@ComponentPreviews
@Composable
private fun PocketMonthlyAllocationCardDisabledPreview() {
  PocketMonthlyAllocationCard(
      component = FakePocketSettingsComponent(),
      state =
          FakePocketSettingsStateFactory.create(
              monthlyTopUpAmount = 300_000L,
              autoTopUpEnabled = false,
          ),
  )
}

@Composable
fun PocketDangerZoneCard(
    onDeletePocketClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = stringResource(Res.string.pocket_danger_zone_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.error,
        )
      }

      Text(
          text = stringResource(Res.string.pocket_danger_zone_desc),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(modifier = Modifier.height(4.dp))

      DangerZoneDeleteButton(onClick = onDeletePocketClick)
    }
  }
}

@Composable
private fun DangerZoneDeleteButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  Surface(
      onClick = onClick,
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      contentColor = MaterialTheme.colorScheme.error,
      shadowElevation = 1.dp,
      modifier = modifier.fillMaxWidth().height(48.dp).testTag("delete-pocket-btn"),
  ) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
      Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
          text = stringResource(Res.string.pocket_danger_zone_delete_action),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.error,
      )
    }
  }
}

@ComponentPreviews
@Composable
private fun PocketDangerZoneCardPreview() {
  PocketDangerZoneCard(
      onDeletePocketClick = {},
  )
}
