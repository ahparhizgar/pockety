package com.base.feature.expense.add

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.base.db.FakeCategoryFactory
import com.base.db.FakePocketItemModelFactory
import com.base.ui.ScreenPreviews
import com.base.ui.components.PocketyTopAppBar
import com.base.usermessage.UiString
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.add_expense_title
import pockety.app.shared.generated.resources.cancel_action
import pockety.app.shared.generated.resources.currency_symbol
import pockety.app.shared.generated.resources.delete_expense_action
import pockety.app.shared.generated.resources.description_label
import pockety.app.shared.generated.resources.description_placeholder
import pockety.app.shared.generated.resources.edit_expense_title
import pockety.app.shared.generated.resources.error_empty_description
import pockety.app.shared.generated.resources.error_invalid_amount
import pockety.app.shared.generated.resources.save_action

@Composable
fun AddExpenseScreen(
    component: AddExpenseComponent,
    modifier: Modifier = Modifier,
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.testTag("add-expense-screen"),
      topBar = { AddExpenseTopAppBar(component = component) },
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
        AddExpenseForm(
            component = component,
            state = state,
        )
      }
    }
  }
}

@Composable
private fun AddExpenseTopAppBar(
    component: AddExpenseComponent,
) {
  val state by component.state.subscribeAsState()
  val titleText =
      if (state.isEditMode) {
        stringResource(Res.string.edit_expense_title)
      } else {
        stringResource(Res.string.add_expense_title)
      }
  PocketyTopAppBar(
      title = titleText,
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
      actions = {
        if (state.isEditMode) {
          IconButton(
              onClick = { component.onDeleteClicked() },
              modifier = Modifier.testTag("delete-button"),
          ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(Res.string.delete_expense_action),
                tint = MaterialTheme.colorScheme.error,
            )
          }
        }
        IconButton(
            onClick = component::onCancelClicked,
        ) {
          Icon(
              imageVector = Icons.Default.Close,
              contentDescription = stringResource(Res.string.cancel_action),
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      },
  )
}

@Composable
private fun AddExpenseForm(
    component: AddExpenseComponent,
    state: AddExpenseState,
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

    CategorySelectorCard(
        component = component,
        selectedCategory = state.selectedCategory,
    )

    PocketSelectorSection(
        component = component,
        selectedPocket = state.selectedPocket,
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
    component: AddExpenseComponent,
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

private const val GLOW_RADIUS_PRIMARY_FRACTION = 0.35f
private const val GLOW_CENTER_X_PRIMARY_FRACTION = 0.9f
private const val GLOW_RADIUS_SECONDARY_FRACTION = 0.3f
private const val GLOW_CENTER_X_SECONDARY_FRACTION = 0.1f

@Composable
private fun AmountInputField(
    amountText: String,
    amountError: UiString?,
    onAmountChange: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    AmountHeroSurface(
        amountText = amountText,
        onAmountChange = onAmountChange,
        onNext = onNext,
    )

    amountError?.let {
      Text(
          text = it.content(),
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(start = 16.dp).testTag("amount-error"),
      )
    }
  }
}

@Composable
private fun AmountHeroSurface(
    amountText: String,
    onAmountChange: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val glowColor1 = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
  val glowColor2 = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)

  Surface(
      modifier =
          modifier.fillMaxWidth().drawBehind {
            drawCircle(
                color = glowColor1,
                radius = size.width * GLOW_RADIUS_PRIMARY_FRACTION,
                center = Offset(size.width * GLOW_CENTER_X_PRIMARY_FRACTION, 0f),
            )
            drawCircle(
                color = glowColor2,
                radius = size.width * GLOW_RADIUS_SECONDARY_FRACTION,
                center = Offset(size.width * GLOW_CENTER_X_SECONDARY_FRACTION, size.height),
            )
          },
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      BasicTextField(
          value = amountText,
          onValueChange = onAmountChange,
          modifier = Modifier.widthIn(min = 60.dp, max = 280.dp).testTag("amount-field"),
          textStyle =
              MaterialTheme.typography.displayLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface,
                  textAlign = TextAlign.Center,
              ),
          singleLine = true,
          cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
          keyboardOptions =
              KeyboardOptions(
                  keyboardType = KeyboardType.Number,
                  imeAction = ImeAction.Next,
              ),
          keyboardActions = KeyboardActions(onNext = { onNext() }),
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
          text = stringResource(Res.string.currency_symbol),
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

@Composable
private fun DescriptionInputField(
    descriptionText: String,
    descriptionError: UiString?,
    onDescriptionChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Text(
        text = stringResource(Res.string.description_label),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )

    TextField(
        value = descriptionText,
        onValueChange = onDescriptionChange,
        modifier = Modifier.fillMaxWidth().testTag("description-field"),
        placeholder = {
          Text(
              text = stringResource(Res.string.description_placeholder),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        },
        leadingIcon = {
          Icon(
              imageVector = Icons.Default.EditNote,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(20.dp),
          )
        },
        shape = RoundedCornerShape(16.dp),
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        singleLine = true,
        isError = descriptionError != null,
    )

    descriptionError?.let {
      Text(
          text = it.content(),
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(start = 16.dp).testTag("description-error"),
      )
    }
  }
}

@ScreenPreviews
@Composable
private fun AddExpenseScreenDefaultPreview() {
  AddExpenseScreen(
      component =
          FakeAddExpenseComponent(
              initialState =
                  AddExpenseState(
                      amountText = "45000",
                      descriptionText = "Groceries",
                      selectedCategory =
                          FakeCategoryFactory.create(name = "Groceries", icon = "shopping_basket"),
                  ),
          ),
  )
}

@ScreenPreviews
@Composable
private fun AddExpenseScreenWithPocketPreview() {
  AddExpenseScreen(
      component =
          FakeAddExpenseComponent(
              initialState =
                  AddExpenseState(
                      amountText = "75000",
                      descriptionText = "Dinner",
                      selectedCategory =
                          FakeCategoryFactory.create(name = "Dining Out", icon = "restaurant"),
                      selectedPocket = FakePocketItemModelFactory.create(name = "Dining Pocket"),
                  ),
          ),
  )
}

@ScreenPreviews
@Composable
private fun EditExpenseScreenPreview() {
  AddExpenseScreen(
      component =
          FakeAddExpenseComponent(
              initialState =
                  AddExpenseState(
                      isEditMode = true,
                      amountText = "75000",
                      descriptionText = "Dinner",
                      selectedCategory =
                          FakeCategoryFactory.create(name = "Dining Out", icon = "restaurant"),
                      selectedPocket = FakePocketItemModelFactory.create(name = "Dining Pocket"),
                  ),
          ),
  )
}

@ScreenPreviews
@Composable
private fun AddExpenseScreenErrorPreview() {
  AddExpenseScreen(
      component =
          FakeAddExpenseComponent(
              initialState =
                  AddExpenseState(
                      amountText = "0",
                      descriptionText = "",
                      amountError = UiString(Res.string.error_invalid_amount),
                      descriptionError = UiString(Res.string.error_empty_description),
                  ),
          ),
  )
}
