package com.base.ui.money

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.base.ui.ComponentPreviews

/** The value and callback use integer Rials; editing and formatting use the display currency. */
@Suppress("LongParameterList")
@Composable
fun MoneyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
    outlined: Boolean = true,
    label: (@Composable () -> Unit)? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
) {
  val currency = LocalCurrency.current
  var emitted by remember(currency) { mutableStateOf(value) }
  var editing by
      remember(currency) {
        mutableStateOf(value.toLongOrNull()?.let(currency::amountText).orEmpty())
      }
  if (value != emitted) {
    emitted = value
    editing = value.toLongOrNull()?.let(currency::amountText).orEmpty()
  }
  val onEdit: (String) -> Unit = { input ->
    val normalized = normalizeMoneyInput(input)
    val parsed = currency.parseRials(normalized)
    if (normalized.isEmpty() || parsed != null) {
      editing = normalized
      emitted = parsed?.toString().orEmpty()
      onValueChange(emitted)
    }
  }
  val options = keyboardOptions.copy(keyboardType = KeyboardType.Decimal)
  if (outlined) {
    OutlinedTextField(
        value = editing,
        onValueChange = onEdit,
        label = label,
        suffix = { Text(currencySymbol()) },
        textStyle = textStyle,
        singleLine = true,
        keyboardOptions = options,
        keyboardActions = keyboardActions,
        visualTransformation = MoneyGrouping,
        shape = shape,
        modifier = modifier,
    )
  } else {
    BasicTextField(
        value = editing,
        onValueChange = onEdit,
        textStyle = textStyle,
        singleLine = true,
        keyboardOptions = options,
        keyboardActions = keyboardActions,
        visualTransformation = MoneyGrouping,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        modifier = modifier,
    )
  }
}

private object MoneyGrouping : VisualTransformation {
  override fun filter(text: AnnotatedString): TransformedText {
    val grouped = groupMoneyDigits(text.text)
    val mapping =
        object : OffsetMapping {
          override fun originalToTransformed(offset: Int): Int {
            var digits = 0
            grouped.forEachIndexed { index, character ->
              if (character != ',') digits++
              if (digits == offset) return index + 1
            }
            return if (offset == 0) 0 else grouped.length
          }

          override fun transformedToOriginal(offset: Int): Int =
              grouped.take(offset).count { it != ',' }
        }
    return TransformedText(AnnotatedString(grouped), mapping)
  }
}

@ComponentPreviews
@Composable
private fun MoneyTextFieldEmptyPreview() {
  MoneyTextField("", {}, KeyboardOptions.Default, KeyboardActions.Default)
}

@ComponentPreviews
@Composable
private fun MoneyTextFieldAmountPreview() {
  MoneyTextField("123456789", {}, KeyboardOptions.Default, KeyboardActions.Default)
}
