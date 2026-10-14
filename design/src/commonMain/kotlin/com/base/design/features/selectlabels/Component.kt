package com.base.design.features.selectlabels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.base.design.ui.ComponentPreviews

data class LabelItem(
    val id: String,
    val name: String,
    val isSelected: Boolean = false,
)

@Composable
fun LabelCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
  val containerColor =
      if (checked) {
        MaterialTheme.colorScheme.primary
      } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
      }

  if (onCheckedChange != null) {
    Surface(
        modifier = modifier.size(24.dp),
        shape = CircleShape,
        color = containerColor,
        onClick = { onCheckedChange(!checked) },
    ) {
      Box(contentAlignment = Alignment.Center) {
        if (checked) {
          Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Selected",
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(16.dp),
          )
        }
      }
    }
  } else {
    Surface(
        modifier = modifier.size(24.dp),
        shape = CircleShape,
        color = containerColor,
    ) {
      Box(contentAlignment = Alignment.Center) {
        if (checked) {
          Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Selected",
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(16.dp),
          )
        }
      }
    }
  }
}

@Composable
fun LabelCheckItem(
    label: LabelItem,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      onClick = onToggle,
      shape = RoundedCornerShape(12.dp),
      color = Color.Transparent,
      modifier = modifier.fillMaxWidth(),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
          text = label.name,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f, fill = false),
      )
      LabelCheckbox(
          checked = label.isSelected,
          onCheckedChange = null,
      )
    }
  }
}

@Composable
fun LabelSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search labels...",
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(
            imeAction = ImeAction.Search,
            keyboardType = KeyboardType.Text,
        ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
  TextField(
      value = query,
      onValueChange = onQueryChange,
      modifier = modifier.fillMaxWidth(),
      placeholder = {
        Text(
            text = placeholder,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      },
      leadingIcon = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
      },
      trailingIcon = {
        if (query.isNotEmpty()) {
          IconButton(onClick = onClearQuery) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      colors =
          TextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
              unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
              disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent,
              disabledIndicatorColor = Color.Transparent,
          ),
      keyboardOptions = keyboardOptions,
      keyboardActions = keyboardActions,
      textStyle = MaterialTheme.typography.bodyMedium,
  )
}

@Composable
fun CreateLabelRow(
    labelName: String,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      onClick = onCreateClick,
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      modifier = modifier.fillMaxWidth(),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Icon(
          imageVector = Icons.Default.AddCircle,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp),
      )
      Text(
          text = "Create \"$labelName\"",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

@Composable
fun SelectLabelsCardHeader(
    selectedCount: Int,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Select Labels",
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondaryContainer,
      ) {
        Text(
            text = "$selectedCount selected",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
      }
    }
    TextButton(onClick = onClearAll) {
      Text(
          text = "Clear all",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

@Composable
fun SelectLabelsBottomBar(
    selectedCount: Int,
    onCancel: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Button(
        onClick = onCancel,
        modifier = Modifier.weight(1f).height(48.dp),
        shape = CircleShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
    ) {
      Text(
          text = "Cancel",
          style = MaterialTheme.typography.labelLarge,
      )
    }
    Button(
        onClick = onApply,
        modifier = Modifier.weight(1f).height(48.dp),
        shape = CircleShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
    ) {
      Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          modifier = Modifier.size(18.dp),
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
          text = "Apply ($selectedCount)",
          style = MaterialTheme.typography.labelLarge,
      )
    }
  }
}

object FakeLabelsFactory {
  fun create(
      id: String = "work",
      name: String = "Work",
      isSelected: Boolean = false,
  ): LabelItem = LabelItem(id = id, name = name, isSelected = isSelected)

  fun createList(): List<LabelItem> =
      listOf(
          create(id = "work", name = "Work", isSelected = true),
          create(id = "coffee", name = "Coffee", isSelected = true),
          create(id = "tax", name = "Tax Deductible", isSelected = true),
          create(id = "personal", name = "Personal", isSelected = false),
          create(id = "vacation", name = "Vacation", isSelected = true),
          create(id = "recurring", name = "Recurring", isSelected = false),
          create(id = "shared", name = "Shared", isSelected = false),
          create(id = "grocery", name = "Grocery", isSelected = false),
          create(id = "health", name = "Health", isSelected = false),
      )
}

@ComponentPreviews
@Composable
private fun LabelCheckboxCheckedPreview() {
  LabelCheckbox(checked = true)
}

@ComponentPreviews
@Composable
private fun LabelCheckboxUncheckedPreview() {
  LabelCheckbox(checked = false)
}

@ComponentPreviews
@Composable
private fun LabelCheckItemPreview() {
  Column {
    LabelCheckItem(
        label = FakeLabelsFactory.create(name = "Work", isSelected = true),
        onToggle = {},
    )
    LabelCheckItem(
        label = FakeLabelsFactory.create(name = "Personal", isSelected = false),
        onToggle = {},
    )
  }
}

@ComponentPreviews
@Composable
private fun LabelSearchFieldPreview() {
  Column {
    LabelSearchField(
        query = "",
        onQueryChange = {},
        onClearQuery = {},
    )
    Spacer(modifier = Modifier.height(8.dp))
    LabelSearchField(
        query = "Work",
        onQueryChange = {},
        onClearQuery = {},
    )
  }
}

@ComponentPreviews
@Composable
private fun CreateLabelRowPreview() {
  CreateLabelRow(
      labelName = "sdfs",
      onCreateClick = {},
  )
}

@ComponentPreviews
@Composable
private fun SelectLabelsCardHeaderPreview() {
  SelectLabelsCardHeader(
      selectedCount = 4,
      onClearAll = {},
  )
}

@ComponentPreviews
@Composable
private fun SelectLabelsBottomBarPreview() {
  SelectLabelsBottomBar(
      selectedCount = 4,
      onCancel = {},
      onApply = {},
  )
}
