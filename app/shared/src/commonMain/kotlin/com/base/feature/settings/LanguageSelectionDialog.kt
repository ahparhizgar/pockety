package com.base.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.base.ui.ComponentPreviews
import com.base.ui.ExcludeScreenshotTest
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.cancel_action
import pockety.app.shared.generated.resources.settings_language_dialog_title
import pockety.app.shared.generated.resources.settings_language_english
import pockety.app.shared.generated.resources.settings_language_persian

@Composable
fun LanguageSelectionDialog(
    selectedLanguage: AppLanguage,
    onLanguageSelect: (AppLanguage) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
  AlertDialog(
      modifier = modifier.testTag("language-selection-dialog"),
      onDismissRequest = onDismissRequest,
      title = { Text(text = stringResource(Res.string.settings_language_dialog_title)) },
      text = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          LanguageOptionItem(
              title = stringResource(Res.string.settings_language_english),
              selected = selectedLanguage == AppLanguage.ENGLISH,
              onClick = { onLanguageSelect(AppLanguage.ENGLISH) },
              testTag = "language-option-english",
          )
          LanguageOptionItem(
              title = stringResource(Res.string.settings_language_persian),
              selected = selectedLanguage == AppLanguage.PERSIAN,
              onClick = { onLanguageSelect(AppLanguage.PERSIAN) },
              testTag = "language-option-persian",
          )
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(
            onClick = onDismissRequest,
            modifier = Modifier.testTag("language-dialog-cancel-button"),
        ) {
          Text(text = stringResource(Res.string.cancel_action))
        }
      },
  )
}

@Composable
private fun LanguageOptionItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier =
          modifier
              .fillMaxWidth()
              .testTag(testTag)
              .clickable(role = Role.RadioButton, onClick = onClick)
              .padding(vertical = 8.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    RadioButton(
        selected = selected,
        onClick = null,
    )
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
  }
}

@ComponentPreviews
@ExcludeScreenshotTest
@Composable
private fun LanguageSelectionDialogPreview() {
  LanguageSelectionDialog(
      selectedLanguage = AppLanguage.ENGLISH,
      onLanguageSelect = {},
      onDismissRequest = {},
  )
}
