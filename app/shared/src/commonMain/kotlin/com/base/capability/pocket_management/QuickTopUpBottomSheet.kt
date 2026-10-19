package com.base.capability.pocket_management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.ui.ComponentPreviews
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.quick_top_up_amount_placeholder
import pockety.app.shared.generated.resources.quick_top_up_confirm_button
import pockety.app.shared.generated.resources.quick_top_up_custom_amount_label
import pockety.app.shared.generated.resources.quick_top_up_subtitle
import pockety.app.shared.generated.resources.quick_top_up_title

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickTopUpBottomSheet(
    component: PocketDetailsComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
      onDismissRequest = component::onDismissQuickTopUp,
      sheetState = sheetState,
      dragHandle = null,
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
      containerColor = MaterialTheme.colorScheme.surface,
      modifier = modifier.testTag("quick-top-up-bottom-sheet"),
  ) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .widthIn(max = 640.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      QuickTopUpHeader(onClose = component::onDismissQuickTopUp)
      QuickTopUpPresets(onSelectPreset = component::onQuickTopUpPresetSelected)

      OutlinedTextField(
          value = state.quickTopUpCustomAmount,
          onValueChange = component::onQuickTopUpCustomAmountChanged,
          label = { Text(stringResource(Res.string.quick_top_up_custom_amount_label)) },
          placeholder = { Text(stringResource(Res.string.quick_top_up_amount_placeholder)) },
          singleLine = true,
          keyboardOptions =
              KeyboardOptions(
                  keyboardType = KeyboardType.Number,
                  imeAction = ImeAction.Done,
              ),
          keyboardActions =
              KeyboardActions(
                  onDone = { component.onConfirmQuickTopUp() },
              ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth().testTag("top-up-amount-input"),
      )

      val customParsed = state.quickTopUpCustomAmount.replace(",", "").trim().toLongOrNull() ?: 0L
      Button(
          onClick = { component.onConfirmQuickTopUp() },
          enabled = customParsed > 0,
          shape = RoundedCornerShape(24.dp),
          colors =
              ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primary,
                  contentColor = MaterialTheme.colorScheme.onPrimary,
              ),
          modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm-top-up-button"),
      ) {
        Text(
            text = stringResource(Res.string.quick_top_up_confirm_button),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
      }
    }
  }
}

@Composable
private fun QuickTopUpHeader(onClose: () -> Unit) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Column {
      Text(
          text = stringResource(Res.string.quick_top_up_title),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = stringResource(Res.string.quick_top_up_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    IconButton(
        onClick = onClose,
        modifier = Modifier.testTag("close-sheet-button"),
    ) {
      Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Close",
      )
    }
  }
}

@Suppress("MagicNumber")
@Composable
private fun QuickTopUpPresets(onSelectPreset: (Long) -> Unit) {
  val presetAmounts = listOf(50000L, 100000L, 200000L, 500000L)
  LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth(),
  ) {
    items(presetAmounts) { amount ->
      SuggestionChip(
          onClick = { onSelectPreset(amount) },
          label = {
            Text(
                text = "+${formatAmount(amount)} T",
                fontWeight = FontWeight.SemiBold,
            )
          },
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.testTag("preset-chip-$amount"),
      )
    }
  }
}

@ComponentPreviews
@Composable
private fun QuickTopUpHeaderPreview() {
  QuickTopUpHeader(onClose = {})
}
