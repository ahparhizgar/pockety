package com.base.feature.category.add

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.component.TestCContext
import com.base.util.uiKoin
import com.base.util.waitForExistence
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AddCategoryE2eUiTest {

  @Test
  fun testCreateCategoryFormWithKeyboardAccessibility() = runComposeUiTest {
    val context = TestCContext()
    val koin = uiKoin()

    var completed = false
    val component =
        koin
            .get<AddCategoryComponentFactory>()
            .create(
                cContext = context,
                onCompleted = { completed = true },
                onCancelled = {},
            )

    setContent { AddCategoryScreen(component = component) }

    // click on category, search for one, select it.

    onNode(isRoot()).performKeyInput { pressKey(Key.Tab) }

    onNode(hasTestTag("save-category-button")).waitForExistence().performClick()
  }
}
