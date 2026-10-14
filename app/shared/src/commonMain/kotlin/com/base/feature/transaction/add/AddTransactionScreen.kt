package com.base.feature.transaction.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import com.base.usermessage.UiString
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.add_transaction_title
import pockety.app.shared.generated.resources.amount_label
import pockety.app.shared.generated.resources.cancel_action
import pockety.app.shared.generated.resources.description_label
import pockety.app.shared.generated.resources.error_empty_description
import pockety.app.shared.generated.resources.error_invalid_amount
import pockety.app.shared.generated.resources.save_action

@Composable
fun AddTransactionScreen(
    component: AddTransactionComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.testTag("add-transaction-screen"),
      topBar = {
        PocketyTopAppBar(
            title = stringResource(Res.string.add_transaction_title),
            navigationIcon = {
              IconButton(
                  onClick = component::onCancelClicked,
                  modifier = Modifier.testTag("back-button"),
              ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.cancel_action),
                )
              }
            },
        )
      },
  ) { paddingValues ->
    Box(
        modifier =
            Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter,
    ) {
      Column(
          modifier =
              Modifier.widthIn(max = 640.dp)
                  .fillMaxWidth()
                  .padding(horizontal = 20.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        AddTransactionForm(
            component = component,
            state = state,
        )
      }
    }
  }
}

@Composable
private fun AddTransactionForm(
    component: AddTransactionComponent,
    state: AddTransactionState,
    modifier: Modifier = Modifier,
) {
  val focusManager = LocalFocusManager.current

  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(20.dp),
  ) {
    AmountInputField(
        amountText = state.amountText,
        amountError = state.amountError,
        onAmountChange = component::onAmountChanged,
        onNext = { focusManager.moveFocus(FocusDirection.Down) },
    )

    DescriptionInputField(
        descriptionText = state.descriptionText,
        descriptionError = state.descriptionError,
        onDescriptionChange = component::onDescriptionChanged,
        onDone = {
          focusManager.clearFocus()
          component.onSaveClicked()
        },
    )

    FormActions(
        component = component,
        isSaving = state.isSaving,
    )
  }
}

@Composable
private fun FormActions(
    component: AddTransactionComponent,
    isSaving: Boolean,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Button(
        onClick = { component.onSaveClicked() },
        enabled = !isSaving,
        shape = CircleShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save-button"),
    ) {
      if (isSaving) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = MaterialTheme.colorScheme.onPrimary,
        )
      } else {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(Res.string.save_action),
            style = MaterialTheme.typography.labelLarge,
        )
      }
    }

    TextButton(
        onClick = component::onCancelClicked,
        modifier = Modifier.fillMaxWidth().testTag("cancel-button"),
    ) {
      Text(
          text = stringResource(Res.string.cancel_action),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun AmountInputField(
    amountText: String,
    amountError: UiString?,
    onAmountChange: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
  OutlinedTextField(
      value = amountText,
      onValueChange = onAmountChange,
      label = { Text(stringResource(Res.string.amount_label)) },
      singleLine = true,
      keyboardOptions =
          KeyboardOptions(
              keyboardType = KeyboardType.Number,
              imeAction = ImeAction.Next,
          ),
      keyboardActions = KeyboardActions(onNext = { onNext() }),
      isError = amountError != null,
      supportingText = {
        amountError?.let {
          Text(
              text = it.content(),
              color = MaterialTheme.colorScheme.error,
              modifier = Modifier.testTag("amount-error"),
          )
        }
      },
      modifier = modifier.fillMaxWidth().testTag("amount-field"),
  )
}

@Composable
private fun DescriptionInputField(
    descriptionText: String,
    descriptionError: UiString?,
    onDescriptionChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
  OutlinedTextField(
      value = descriptionText,
      onValueChange = onDescriptionChange,
      label = { Text(stringResource(Res.string.description_label)) },
      singleLine = true,
      keyboardOptions =
          KeyboardOptions(
              keyboardType = KeyboardType.Text,
              imeAction = ImeAction.Done,
          ),
      keyboardActions = KeyboardActions(onDone = { onDone() }),
      isError = descriptionError != null,
      supportingText = {
        descriptionError?.let {
          Text(
              text = it.content(),
              color = MaterialTheme.colorScheme.error,
              modifier = Modifier.testTag("description-error"),
          )
        }
      },
      modifier = modifier.fillMaxWidth().testTag("description-field"),
  )
}

@ScreenPreviews
@Composable
private fun AddTransactionScreenDefaultPreview() {
  AddTransactionScreen(
      component = FakeAddTransactionComponent(),
  )
}

@ScreenPreviews
@Composable
private fun AddTransactionScreenErrorPreview() {
  AddTransactionScreen(
      component =
          FakeAddTransactionComponent(
              initialState =
                  AddTransactionState(
                      amountText = "0",
                      descriptionText = "",
                      amountError = UiString(Res.string.error_invalid_amount),
                      descriptionError = UiString(Res.string.error_empty_description),
                  ),
          ),
  )
}
