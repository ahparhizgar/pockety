package com.base.feature.expense

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.feature.expense.add.AddExpenseScreen
import com.base.feature.expense.add.AddExpenseState
import com.base.feature.expense.add.FakeAddExpenseComponent
import com.base.util.waitForExistence
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AddExpenseFocusUiTest {

  @Test
  fun testAmountFieldAutoFocusesWhenEmpty() = runComposeUiTest {
    val component = FakeAddExpenseComponent(initialState = AddExpenseState(amountText = ""))

    setContent { AddExpenseScreen(component = component) }

    onNode(hasTestTag("amount-field")).waitForExistence().assertIsFocused()
  }

  @Test
  fun testAmountFieldDoesNotAutoFocusWhenPrefilled() = runComposeUiTest {
    val component =
        FakeAddExpenseComponent(
            initialState = AddExpenseState(amountText = "50000", isEditMode = false),
        )

    setContent { AddExpenseScreen(component = component) }

    onNode(hasTestTag("amount-field")).waitForExistence().assertIsNotFocused()
  }

  @Test
  fun testAmountFieldDoesNotAutoFocusInEditMode() = runComposeUiTest {
    val component =
        FakeAddExpenseComponent(
            initialState = AddExpenseState(amountText = "", isEditMode = true),
        )

    setContent { AddExpenseScreen(component = component) }

    onNode(hasTestTag("amount-field")).waitForExistence().assertIsNotFocused()
  }
}
