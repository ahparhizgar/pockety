package com.base.design.features.categorylist

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.base.design.features.selectcategory.CategoryOptionItem
import com.base.design.ui.ComponentPreviews
import com.base.design.ui.theme.colorSchemeExtended

data class CategoryItemUiModel(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val iconContainerColor: Color,
    val iconContentColor: Color,
    val subtitle: String? = null,
)

@Composable
fun defaultCategoryList(): List<CategoryItemUiModel> {
  val cs = MaterialTheme.colorScheme
  val ext = MaterialTheme.colorSchemeExtended
  return listOf(
      CategoryItemUiModel(
          id = "food-dining",
          name = "Food & Dining",
          icon = Icons.Default.Restaurant,
          iconContainerColor = cs.secondaryContainer,
          iconContentColor = cs.onSecondaryContainer,
      ),
      CategoryItemUiModel(
          id = "groceries",
          name = "Groceries",
          icon = Icons.Default.ShoppingCart,
          iconContainerColor = cs.tertiaryContainer,
          iconContentColor = cs.onTertiaryContainer,
      ),
      CategoryItemUiModel(
          id = "transportation-fuel",
          name = "Transportation & Fuel",
          icon = Icons.Default.DirectionsCar,
          iconContainerColor = ext.primaryFixed,
          iconContentColor = ext.onPrimaryFixed,
      ),
      CategoryItemUiModel(
          id = "bills-utilities",
          name = "Bills & Utilities",
          icon = Icons.Default.Bolt,
          iconContainerColor = ext.secondaryFixed,
          iconContentColor = ext.onSecondaryFixed,
      ),
      CategoryItemUiModel(
          id = "entertainment",
          name = "Entertainment",
          icon = Icons.Default.SportsEsports,
          iconContainerColor = ext.tertiaryFixed,
          iconContentColor = ext.onTertiaryFixed,
      ),
      CategoryItemUiModel(
          id = "shopping",
          name = "Shopping",
          icon = Icons.Default.Checkroom,
          iconContainerColor = cs.secondaryContainer,
          iconContentColor = cs.onSecondaryContainer,
      ),
  )
}

val selectableCategoryIcons =
    listOf(
        Icons.Default.FitnessCenter,
        Icons.Default.Flight,
        Icons.Default.MedicalServices,
        Icons.Default.Pets,
        Icons.Default.School,
        Icons.Default.Savings,
    )

@Composable
fun CategoryManagementRow(
    category: CategoryItemUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDelete: (() -> Unit)? = null,
    showDragHandle: Boolean = true,
    isDragging: Boolean = false,
) {
  CategoryOptionItem(
      name = category.name,
      description = category.subtitle,
      icon = category.icon,
      iconBackgroundColor = category.iconContainerColor,
      iconContentColor = category.iconContentColor,
      onClick = onClick,
      modifier = modifier,
      isDragging = isDragging,
      leadingContent =
          if (showDragHandle) {
            {
              Icon(
                  imageVector = Icons.Default.DragHandle,
                  contentDescription = "Reorder ${category.name}",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(24.dp),
              )
            }
          } else {
            null
          },
      trailingContent =
          onDelete?.let { delete ->
            {
              IconButton(onClick = delete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete ${category.name}",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(22.dp),
                )
              }
            }
          },
  )
}

@Composable
fun DeleteCategoryDialog(
    category: CategoryItemUiModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
  AlertDialog(
      onDismissRequest = onDismiss,
      modifier = modifier,
      title = { Text("Delete category?") },
      text = {
        Text(
            "\"${category.name}\" will be deleted. Expenses in this category will become " +
                "uncategorized."
        )
      },
      confirmButton = {
        TextButton(onClick = onConfirm) { Text("Delete", color = MaterialTheme.colorScheme.error) }
      },
      dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
  )
}

@Composable
fun CategoryEmptyState(
    modifier: Modifier = Modifier,
    onAddCategoryClick: (() -> Unit)? = null,
) {
  Column(
      modifier = modifier.fillMaxWidth().padding(vertical = 48.dp, horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    Surface(
        modifier = Modifier.size(80.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Default.FolderOff,
            contentDescription = null,
            modifier = Modifier.size(36.dp),
        )
      }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "No Categories Yet",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Create your first custom category to start tracking your spending effortlessly.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(max = 280.dp),
    )
    if (onAddCategoryClick != null) {
      Spacer(modifier = Modifier.height(24.dp))
      Button(
          onClick = onAddCategoryClick,
          shape = CircleShape,
          colors =
              ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primary,
                  contentColor = MaterialTheme.colorScheme.onPrimary,
              ),
      ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Add Category")
      }
    }
  }
}

@Composable
fun CategoryIconOption(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
  val ext = MaterialTheme.colorSchemeExtended
  val cs = MaterialTheme.colorScheme
  Surface(
      modifier = modifier.size(48.dp),
      shape = RoundedCornerShape(16.dp),
      color = if (isSelected) ext.primaryFixed else cs.surfaceContainerLow,
      contentColor = if (isSelected) ext.onPrimaryFixed else cs.onSurface,
      onClick = onClick,
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
          imageVector = icon,
          contentDescription = contentDescription,
          modifier = Modifier.size(24.dp),
      )
    }
  }
}

@Composable
fun AddCategorySheetContent(
    onClose: () -> Unit,
    onCreate: (String, ImageVector) -> Unit,
    modifier: Modifier = Modifier,
    initialName: String = "",
) {
  var name by remember { mutableStateOf(initialName) }
  var selectedIcon by remember { mutableStateOf(selectableCategoryIcons.first()) }

  Box(
      modifier = modifier.fillMaxWidth(),
      contentAlignment = Alignment.Center,
  ) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .widthIn(max = 640.dp)
                .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
            text = "New Category",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        IconButton(
            onClick = onClose,
            modifier = Modifier.size(40.dp),
        ) {
          Surface(
              modifier = Modifier.fillMaxSize(),
              shape = CircleShape,
              color = MaterialTheme.colorScheme.surfaceContainerHigh,
              contentColor = MaterialTheme.colorScheme.onSurface,
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Close modal",
                  modifier = Modifier.size(20.dp),
              )
            }
          }
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Category Name",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
              Text(
                  text = "e.g., Fitness & Health",
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                      if (name.isNotBlank()) {
                        onCreate(name.trim(), selectedIcon)
                      }
                    },
                ),
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Select Icon",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          selectableCategoryIcons.forEach { icon ->
            CategoryIconOption(
                icon = icon,
                isSelected = icon == selectedIcon,
                onClick = { selectedIcon = icon },
            )
          }
        }
      }

      Row(
          modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        TextButton(
            onClick = onClose,
            modifier = Modifier.weight(1f).height(56.dp),
        ) {
          Text("Cancel")
        }
        Button(
            onClick = {
              if (name.isNotBlank()) {
                onCreate(name.trim(), selectedIcon)
              }
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.weight(2f).height(56.dp),
            shape = CircleShape,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
        ) {
          Text(
              text = "Create Category",
              style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryBottomSheet(
    onDismissRequest: () -> Unit,
    onCategoryCreated: (String, ImageVector) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
  ModalBottomSheet(
      onDismissRequest = onDismissRequest,
      sheetState = sheetState,
      modifier = modifier,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
  ) {
    AddCategorySheetContent(
        onClose = onDismissRequest,
        onCreate = onCategoryCreated,
    )
  }
}

object FakeCategoryFactory {
  fun create(
      id: String = "1",
      name: String = "Food & Dining",
      icon: ImageVector = Icons.Default.Restaurant,
      iconContainerColor: Color = Color(0xFFCCE9DA),
      iconContentColor: Color = Color(0xFF184E3F),
      subtitle: String? = null,
  ): CategoryItemUiModel =
      CategoryItemUiModel(
          id = id,
          name = name,
          icon = icon,
          iconContainerColor = iconContainerColor,
          iconContentColor = iconContentColor,
          subtitle = subtitle,
      )

  fun createList(): List<CategoryItemUiModel> =
      listOf(
          CategoryItemUiModel(
              id = "food-dining",
              name = "Food & Dining",
              icon = Icons.Default.Restaurant,
              iconContainerColor = Color(0xFFCCE9DA),
              iconContentColor = Color(0xFF184E3F),
          ),
          CategoryItemUiModel(
              id = "groceries",
              name = "Groceries",
              icon = Icons.Default.ShoppingCart,
              iconContainerColor = Color(0xFFFFDCC1),
              iconContentColor = Color(0xFF432C14),
          ),
          CategoryItemUiModel(
              id = "transportation-fuel",
              name = "Transportation & Fuel",
              icon = Icons.Default.DirectionsCar,
              iconContainerColor = Color(0xFFB9EFC5),
              iconContentColor = Color(0xFF00532B),
          ),
          CategoryItemUiModel(
              id = "bills-utilities",
              name = "Bills & Utilities",
              icon = Icons.Default.Bolt,
              iconContainerColor = Color(0xFFCCE9DA),
              iconContentColor = Color(0xFF184E3F),
          ),
          CategoryItemUiModel(
              id = "entertainment",
              name = "Entertainment",
              icon = Icons.Default.SportsEsports,
              iconContainerColor = Color(0xFFFFDCC1),
              iconContentColor = Color(0xFF432C14),
          ),
          CategoryItemUiModel(
              id = "shopping",
              name = "Shopping",
              icon = Icons.Default.Checkroom,
              iconContainerColor = Color(0xFFCCE9DA),
              iconContentColor = Color(0xFF184E3F),
          ),
      )
}

@ComponentPreviews
@Composable
fun CategoryManagementRowPreview() {
  Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
    CategoryManagementRow(
        category = FakeCategoryFactory.create(),
        onDelete = {},
    )
  }
}

@ComponentPreviews
@Composable
fun DeleteCategoryDialogPreview() {
  DeleteCategoryDialog(
      category = FakeCategoryFactory.create(),
      onConfirm = {},
      onDismiss = {},
  )
}

@ComponentPreviews
@Composable
private fun CategoryEmptyStateSecondaryPreview() {
  CategoryEmptyState(
      onAddCategoryClick = {},
  )
}

@ComponentPreviews
@Composable
private fun AddCategorySheetContentPreview() {
  Surface(color = MaterialTheme.colorScheme.surface) {
    AddCategorySheetContent(
        onClose = {},
        onCreate = { _, _ -> },
    )
  }
}
