package com.base.feature.money

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.feature.settings.CurrencyType
import com.base.ui.money.LocalCurrency
import com.base.ui.money.MoneyTextField
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class MoneyE2eUiTest {
  @Test
  fun kiloTomanInputFormatsAndEmitsExactRials() = runComposeUiTest {
    var rials by mutableStateOf("123456789")
    setContent {
      CompositionLocalProvider(LocalCurrency provides CurrencyType.KILO_TOMANS) {
        MoneyTextField(
            value = rials,
            onValueChange = { rials = it },
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier.testTag("money"),
        )
      }
    }
    val field = onNodeWithTag("money")
    field.assertTextContains("12,345.6789")
    field.performTextReplacement("۱۲۳۴٫۵۶۷۸")
    field.assertTextContains("1,234.5678")
    assertEquals("12345678", rials)
    field.performTextReplacement("1.00001")
    assertEquals("12345678", rials)
    field.performKeyInput { pressKey(Key.Tab) }
  }

  @Test
  fun switchingUnitsPreservesPrefilledRials() = runComposeUiTest {
    var currency by mutableStateOf(CurrencyType.TOMANS)
    var rials by mutableStateOf("1001")
    setContent {
      CompositionLocalProvider(LocalCurrency provides currency) {
        MoneyTextField(
            rials,
            { rials = it },
            KeyboardOptions.Default,
            KeyboardActions.Default,
            modifier = Modifier.testTag("money"),
        )
      }
    }
    onNodeWithTag("money").assertTextContains("100.1")
    runOnIdle { currency = CurrencyType.KILO_TOMANS }
    onNodeWithTag("money").assertTextContains("0.1001")
    runOnIdle { currency = CurrencyType.RIALS }
    onNodeWithTag("money").assertTextContains("1,001")
    assertEquals("1001", rials)
  }
}
