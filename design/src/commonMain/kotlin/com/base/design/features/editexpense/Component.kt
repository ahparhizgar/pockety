package com.base.design.features.editexpense

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.design.ui.ComponentPreviews

data class CategoryQuickChip(
    val id: String,
    val name: String,
    val icon: ImageVector? = null,
)

val defaultQuickCategories =
    listOf(
        CategoryQuickChip("cafe", "Cafe", Icons.Default.Star),
        CategoryQuickChip("groceries", "Groceries", Icons.Default.ShoppingBasket),
        CategoryQuickChip("commute", "Commute", Icons.Default.DirectionsCar),
        CategoryQuickChip("bills", "Bills", Icons.AutoMirrored.Filled.ReceiptLong),
        CategoryQuickChip("shopping", "Shopping", Icons.Default.Storefront),
        CategoryQuickChip("other", "Other...", Icons.Default.MoreHoriz),
    )

@Composable
fun AmountHeroSection(
    amount: String,
    modifier: Modifier = Modifier,
    currency: String = "T",
    onAmountChange: (String) -> Unit = {},
) {
  val glowColor1 = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
  val glowColor2 = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)

  Surface(
      modifier =
          modifier.fillMaxWidth().drawBehind {
            drawCircle(
                color = glowColor1,
                radius = size.width * 0.35f,
                center = Offset(size.width * 0.9f, 0f),
            )
            drawCircle(
                color = glowColor2,
                radius = size.width * 0.3f,
                center = Offset(size.width * 0.1f, size.height),
            )
          },
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      BasicTextField(
          value = amount,
          onValueChange = onAmountChange,
          modifier = Modifier.widthIn(min = 60.dp, max = 280.dp),
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
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
          text = currency,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

@Composable
fun CategorySelectorSection(
    selectedCategoryName: String?,
    modifier: Modifier = Modifier,
    selectedCategoryIcon: ImageVector = Icons.Default.LocalCafe,
    quickChips: List<CategoryQuickChip> = defaultQuickCategories,
    selectedChipId: String? = "cafe",
    onCategoryClick: () -> Unit = {},
    onClearCategoryClick: () -> Unit = {},
    onQuickChipClick: (CategoryQuickChip) -> Unit = {},
) {
  val hasCategory = !selectedCategoryName.isNullOrBlank()

  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Icon(
              imageVector = Icons.Default.LocalOffer,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp),
          )
          Text(
              text = "Category",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }

      Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 1.dp,
          onClick = onCategoryClick,
      ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color =
                    if (hasCategory) {
                      MaterialTheme.colorScheme.secondaryContainer
                    } else {
                      MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                contentColor =
                    if (hasCategory) {
                      MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                      MaterialTheme.colorScheme.onSurfaceVariant
                    },
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (hasCategory) selectedCategoryIcon else Icons.Default.Category,
                    contentDescription = selectedCategoryName,
                    modifier = Modifier.size(24.dp),
                )
              }
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                  text = if (hasCategory) selectedCategoryName else "Tap to select category",
                  style = MaterialTheme.typography.titleMedium,
                  color =
                      if (hasCategory) {
                        MaterialTheme.colorScheme.onSurface
                      } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                      },
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
              )
              Text(
                  text =
                      if (hasCategory) {
                        "Auto-deduct transactions"
                      } else {
                        "Assign a category to this expense"
                      },
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
              )
            }
          }

          if (hasCategory) {
            IconButton(onClick = onClearCategoryClick) {
              Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Clear category",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(20.dp),
              )
            }
          }
        }
      }

      Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        quickChips.forEach { chip ->
          val isSelected = chip.id == selectedChipId
          Surface(
              onClick = { onQuickChipClick(chip) },
              shape = CircleShape,
              color =
                  if (isSelected) {
                    MaterialTheme.colorScheme.primary
                  } else {
                    MaterialTheme.colorScheme.surfaceContainerLowest
                  },
              contentColor =
                  if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                  } else {
                    MaterialTheme.colorScheme.onSurface
                  },
              shadowElevation = 1.dp,
          ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              if (chip.icon != null) {
                Icon(
                    imageVector = chip.icon,
                    contentDescription = null,
                    tint =
                        if (isSelected) {
                          MaterialTheme.colorScheme.onPrimary
                        } else {
                          MaterialTheme.colorScheme.secondary
                        },
                    modifier = Modifier.size(16.dp),
                )
              }
              Text(
                  text = chip.name,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun LabelChipRow(
    labels: List<String>,
    modifier: Modifier = Modifier,
    onRemoveLabel: (String) -> Unit = {},
    onAddLabelClick: () -> Unit = {},
) {
  Row(
      modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    labels.forEach { label ->
      Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceContainerHigh,
          contentColor = MaterialTheme.colorScheme.onSurface,
      ) {
        Row(
            modifier =
                Modifier.padding(
                    start = 12.dp,
                    top = 4.dp,
                    end = 6.dp,
                    bottom = 4.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
              text = label,
              style = MaterialTheme.typography.labelMedium,
          )
          IconButton(
              onClick = { onRemoveLabel(label) },
              modifier = Modifier.size(20.dp),
          ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove $label",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp),
            )
          }
        }
      }
    }

    Surface(
        onClick = onAddLabelClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
      Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = "Add Label",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
      }
    }
  }
}

@Composable
fun DescriptionSection(
    description: String,
    labels: List<String>,
    modifier: Modifier = Modifier,
    onDescriptionChange: (String) -> Unit = {},
    onRemoveLabel: (String) -> Unit = {},
    onAddLabelClick: () -> Unit = {},
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text,
        ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Text(
        text = "Description & Labels",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp),
    )

    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
          Text(
              text = "Add transaction note...",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.outline,
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
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent,
            ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        textStyle = MaterialTheme.typography.titleMedium,
    )

    Spacer(modifier = Modifier.height(2.dp))

    LabelChipRow(
        labels = labels,
        onRemoveLabel = onRemoveLabel,
        onAddLabelClick = onAddLabelClick,
    )
  }
}

@Composable
fun PocketSelectorCard(
    pocketName: String?,
    modifier: Modifier = Modifier,
    pocketBalance: String = "400,000 T",
    pocketIcon: ImageVector = Icons.Default.LocalCafe,
    onPocketClick: () -> Unit = {},
    onClearPocketClick: () -> Unit = {},
) {
  val hasPocket = !pocketName.isNullOrBlank()

  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Icon(
              imageVector = Icons.Default.AccountBalanceWallet,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.tertiary,
              modifier = Modifier.size(20.dp),
          )
          Text(
              text = "Pocket",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }

      Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 1.dp,
          onClick = onPocketClick,
      ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Surface(
                shape = CircleShape,
                color =
                    if (hasPocket) {
                      MaterialTheme.colorScheme.secondaryContainer
                    } else {
                      MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                contentColor =
                    if (hasPocket) {
                      MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                      MaterialTheme.colorScheme.onSurfaceVariant
                    },
                modifier = Modifier.size(48.dp),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (hasPocket) pocketIcon else Icons.Default.AccountBalanceWallet,
                    contentDescription = pocketName,
                    modifier = Modifier.size(24.dp),
                )
              }
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(
                  text = if (hasPocket) pocketName else "Tap to link a pocket",
                  style = MaterialTheme.typography.titleMedium,
                  color =
                      if (hasPocket) {
                        MaterialTheme.colorScheme.onSurface
                      } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                      },
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
              )
              Text(
                  text =
                      if (hasPocket) {
                        "Balance: $pocketBalance"
                      } else {
                        "Auto-deduct from pocket budget"
                      },
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
              )
            }
          }

          if (hasPocket) {
            IconButton(onClick = onClearPocketClick) {
              Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Unlink pocket",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(20.dp),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun ExpenseTimeCard(
    modifier: Modifier = Modifier,
    timeText: String = "October 16, 2026 • 14:32",
    onEditClick: () -> Unit = {},
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp),
            )
          }
        }
        Column {
          Text(
              text = "EXPENSE TIME",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              letterSpacing = 0.5.sp,
          )
          Text(
              text = timeText,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }
      TextButton(onClick = onEditClick) {
        Text(
            text = "Edit",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
      }
    }
  }
}

@Composable
fun SourceSmsCard(
    modifier: Modifier = Modifier,
    sender: String = "+9820000",
    time: String = "14:32:18",
    rawSms: String =
        "Withdrawal from acct: 3,200,000 Rials | Card: 1234****6104 | Terminal: Sam Cafe Fereshteh | Remaining: 18,450,000 Rials",
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = "Source SMS",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
      }

      Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 1.dp,
      ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                  text = "Sender:",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface,
              )
              Text(
                  text = sender,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                  text = "Time:",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface,
              )
              Text(
                  text = time,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }

          Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.inverseSurface,
              contentColor = MaterialTheme.colorScheme.inverseOnSurface,
          ) {
            Text(
                text = rawSms,
                modifier = Modifier.padding(12.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
          }
        }
      }
    }
  }
}

@Composable
fun EditExpenseActionButtons(
    modifier: Modifier = Modifier,
    onSaveExpense: () -> Unit = {},
    onDismissSms: () -> Unit = {},
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Button(
        onClick = onSaveExpense,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = CircleShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
    ) {
      Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          modifier = Modifier.size(22.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
          text = "Save Expense",
          style = MaterialTheme.typography.labelLarge,
      )
    }

    TextButton(
        onClick = onDismissSms,
        modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
          text = "Dismiss SMS",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@ComponentPreviews
@Composable
private fun AmountHeroSectionPreview() {
  AmountHeroSection(
      amount = "320,000",
      currency = "T",
  )
}

@ComponentPreviews
@Composable
private fun CategorySelectorSectionPreview() {
  CategorySelectorSection(
      selectedCategoryName = "Cafe & Dining",
      selectedChipId = "cafe",
  )
}

@ComponentPreviews
@Composable
private fun CategorySelectorSectionUncategorizedPreview() {
  CategorySelectorSection(
      selectedCategoryName = null,
      selectedChipId = null,
  )
}

@ComponentPreviews
@Composable
private fun CategorySelectorSectionGroceriesPreview() {
  CategorySelectorSection(
      selectedCategoryName = "Groceries",
      selectedCategoryIcon = Icons.Default.ShoppingBasket,
      selectedChipId = "groceries",
  )
}

@ComponentPreviews
@Composable
private fun DescriptionSectionPreview() {
  DescriptionSection(
      description = "Latte & Croissant at Sam Cafe",
      labels = listOf("#WorkMeeting", "#Personal"),
  )
}

@ComponentPreviews
@Composable
private fun DescriptionSectionEmptyLabelsPreview() {
  DescriptionSection(
      description = "",
      labels = emptyList(),
  )
}

@ComponentPreviews
@Composable
private fun PocketSelectorCardPreview() {
  PocketSelectorCard(
      pocketName = "Coffee Pocket",
      pocketBalance = "400,000 T",
  )
}

@ComponentPreviews
@Composable
private fun PocketSelectorCardUnlinkedPreview() {
  PocketSelectorCard(
      pocketName = null,
  )
}

@ComponentPreviews
@Composable
private fun ExpenseTimeCardPreview() {
  ExpenseTimeCard(
      timeText = "October 16, 2026 • 14:32",
  )
}

@ComponentPreviews
@Composable
private fun SourceSmsCardPreview() {
  SourceSmsCard()
}

@ComponentPreviews
@Composable
private fun EditExpenseActionButtonsPreview() {
  EditExpenseActionButtons()
}
