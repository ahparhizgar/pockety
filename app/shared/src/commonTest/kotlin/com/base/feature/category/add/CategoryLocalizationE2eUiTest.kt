package com.base.feature.category.add

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.v2.runComposeUiTest
import com.base.RootScreen
import com.base.feature.root.FakeRootComponent
import com.base.feature.settings.AppLanguage
import com.base.util.waitForExistence
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class CategoryLocalizationE2eUiTest {
  @Test
  fun persianAppLanguageLocalizesFormFiltersAndAccessibleChoices() = runComposeUiTest {
    setContent {
      RootScreen(FakeRootComponent(initialLanguage = AppLanguage.PERSIAN)) {
        AddCategoryScreen(FakeAddCategoryComponent())
      }
    }
    onNode(hasText("افزودن دسته‌بندی")).waitForExistence().assertExists()
    onNode(hasText("پیش‌نمایش")).assertExists()
    onNode(hasText("همه")).assertExists()
    onNode(hasText("خرید")).assertExists()
    onNode(hasContentDescription("قرمز مرجانی")).assertExists()
    onNode(hasContentDescription("خواربار")).assertExists()
    onNode(hasText("LIVE PREVIEW")).assertDoesNotExist()
    onNode(hasText("Shopping")).assertDoesNotExist()
  }

  @Test
  fun englishAppLanguageUsesEnglishResources() = runComposeUiTest {
    setContent {
      RootScreen(FakeRootComponent(initialLanguage = AppLanguage.ENGLISH)) {
        AddCategoryScreen(FakeAddCategoryComponent())
      }
    }
    onNode(hasText("Add a Category")).waitForExistence().assertExists()
    onNode(hasText("Preview")).assertExists()
    onNode(hasText("Shopping")).assertExists()
    onNode(hasContentDescription("Groceries")).assertExists()
  }
}
