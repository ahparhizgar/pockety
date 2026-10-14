@file:Suppress(
    "LongMethod",
    "LongParameterList",
    "TooManyFunctions",
    "MagicNumber",
    "MatchingDeclarationName",
    "SpreadOperator",
    "MaxLineLength",
    "UnusedPrivateFunction",
    "ComposableParamOrder",
    "PreviewNaming",
    "PreviewPublic",
    "ParameterNaming",
    "UnstableCollections",
)

package com.base.design.features

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
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
import com.base.design.ui.ComponentPreviews
import com.base.design.ui.theme.colorSchemeExtended
import com.base.design.ui.theme.onPrimaryFixed
import com.base.design.ui.theme.onSecondaryContainerLight
import com.base.design.ui.theme.onSecondaryFixed
import com.base.design.ui.theme.onTertiaryContainerLight
import com.base.design.ui.theme.onTertiaryFixed
import com.base.design.ui.theme.primaryFixed
import com.base.design.ui.theme.secondaryContainerLight
import com.base.design.ui.theme.secondaryFixed
import com.base.design.ui.theme.tertiaryContainerLight
import com.base.design.ui.theme.tertiaryFixed

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
fun CategoryListItem(
    category: CategoryItemUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDelete: (() -> Unit)? = null,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      contentColor = MaterialTheme.colorScheme.onSurface,
      onClick = onClick,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = category.iconContainerColor,
            contentColor = category.iconContentColor,
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                modifier = Modifier.size(24.dp),
            )
          }
        }
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
          Text(
              text = category.name,
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface,
          )
          if (category.subtitle != null) {
            Text(
                text = category.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        if (onDelete != null) {
          IconButton(
              onClick = onDelete,
              modifier = Modifier.size(32.dp),
          ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete ${category.name}",
                    modifier = Modifier.size(18.dp),
                )
              }
            }
          }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
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
      iconContainerColor: Color = secondaryContainerLight,
      iconContentColor: Color = onSecondaryContainerLight,
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
              iconContainerColor = secondaryContainerLight,
              iconContentColor = onSecondaryContainerLight,
          ),
          CategoryItemUiModel(
              id = "groceries",
              name = "Groceries",
              icon = Icons.Default.ShoppingCart,
              iconContainerColor = tertiaryContainerLight,
              iconContentColor = onTertiaryContainerLight,
          ),
          CategoryItemUiModel(
              id = "transportation-fuel",
              name = "Transportation & Fuel",
              icon = Icons.Default.DirectionsCar,
              iconContainerColor = primaryFixed,
              iconContentColor = onPrimaryFixed,
          ),
          CategoryItemUiModel(
              id = "bills-utilities",
              name = "Bills & Utilities",
              icon = Icons.Default.Bolt,
              iconContainerColor = secondaryFixed,
              iconContentColor = onSecondaryFixed,
          ),
          CategoryItemUiModel(
              id = "entertainment",
              name = "Entertainment",
              icon = Icons.Default.SportsEsports,
              iconContainerColor = tertiaryFixed,
              iconContentColor = onTertiaryFixed,
          ),
          CategoryItemUiModel(
              id = "shopping",
              name = "Shopping",
              icon = Icons.Default.Checkroom,
              iconContainerColor = secondaryContainerLight,
              iconContentColor = onSecondaryContainerLight,
          ),
      )
}

@ComponentPreviews
@Composable
private fun CategoryListItemPreview() {
  CategoryListItem(
      category = FakeCategoryFactory.create(),
      onDelete = {},
  )
}

@ComponentPreviews
@Composable
private fun CategoryListItemWithSubtitlePreview() {
  CategoryListItem(
      category =
          FakeCategoryFactory.create(
              name = "Bills & Utilities",
              icon = Icons.Default.Bolt,
              subtitle = "4 transactions • $210.00",
          ),
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
