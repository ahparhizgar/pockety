package com.base.design.features.pocketsettings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.base.design.ui.ComponentPreviews

@Composable
fun PocketIdentitySection(
    pocketName: String,
    onPocketNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.LocalCafe,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    onEditIconClick: () -> Unit = {},
) {
  val focusManager = LocalFocusManager.current

  Column(
      modifier = modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Box(
        modifier = Modifier.padding(top = 8.dp),
        contentAlignment = Alignment.BottomEnd,
    ) {
      Surface(
          modifier = Modifier.size(96.dp),
          shape = CircleShape,
          color = containerColor,
          contentColor = contentColor,
          shadowElevation = 2.dp,
          onClick = onEditIconClick,
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = icon,
              contentDescription = "Pocket icon",
              modifier = Modifier.size(44.dp),
              tint = contentColor,
          )
        }
      }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Text(
          text = "Pocket Name",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(start = 4.dp),
      )
      OutlinedTextField(
          value = pocketName,
          onValueChange = onPocketNameChange,
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          textStyle = MaterialTheme.typography.titleMedium,
          shape = RoundedCornerShape(16.dp),
          colors =
              OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                  unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                  focusedBorderColor = MaterialTheme.colorScheme.primary,
                  unfocusedBorderColor = Color.Transparent,
              ),
          keyboardOptions =
              KeyboardOptions(
                  keyboardType = KeyboardType.Text,
                  imeAction = ImeAction.Done,
              ),
          keyboardActions =
              KeyboardActions(
                  onDone = { focusManager.clearFocus() },
              ),
      )
    }
  }
}

@Composable
fun MonthlyAllocationCard(
    allocatedAmount: String,
    modifier: Modifier = Modifier,
    currencySymbol: String = "T",
    autoRefillEnabled: Boolean = true,
    onAutoRefillChange: (Boolean) -> Unit = {},
    onTuneClick: () -> Unit = {},
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
            imageVector = Icons.Default.EventRepeat,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = "Monthly Top-Up Allocation",
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
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
                text = "Allocated refill",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Text(
                  text = allocatedAmount,
                  style = MaterialTheme.typography.headlineMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface,
              )
              Text(
                  text = currencySymbol,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
              )
            }
          }

          IconButton(
              onClick = onTuneClick,
              modifier =
                  Modifier.size(40.dp)
                      .clip(CircleShape)
                      .background(MaterialTheme.colorScheme.surfaceContainer),
          ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Adjust allocation",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
          }
        }
      }

      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(
            modifier = Modifier.weight(1f).padding(end = 12.dp),
        ) {
          Text(
              text = "Auto-refill on 1st of month",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
              text =
                  "This amount is automatically allocated to this pocket at the start of each month.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        Switch(
            checked = autoRefillEnabled,
            onCheckedChange = onAutoRefillChange,
            thumbContent =
                if (autoRefillEnabled) {
                  {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                  }
                } else null,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    checkedIconColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                ),
        )
      }
    }
  }
}

@Composable
fun AssociatedCategoryCard(
    categoryName: String,
    modifier: Modifier = Modifier,
    categorySubtitle: String = "Auto-deduct transactions",
    categoryIcon: ImageVector = Icons.Default.Restaurant,
    onChangeCategoryClick: () -> Unit = {},
    onClearCategoryClick: () -> Unit = {},
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
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
            text = "Associated Category",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        TextButton(onClick = onChangeCategoryClick) {
          Text(
              text = "Change",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.primary,
          )
        }
      }

      Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 1.dp,
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
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
              }
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                  text = categoryName,
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
              )
              Text(
                  text = categorySubtitle,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
              )
            }
          }

          IconButton(onClick = onClearCategoryClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear association",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
fun ManualTopUpCard(
    currentBalance: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    onConfirmTopUp: () -> Unit,
    modifier: Modifier = Modifier,
    currencySymbol: String = "T",
) {
  val focusManager = LocalFocusManager.current

  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      shadowElevation = 1.dp,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
              text = "Current Balance",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Row(
              verticalAlignment = Alignment.Bottom,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
                text = currentBalance,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = currencySymbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
          }
        }

        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp),
            )
          }
        }
      }

      Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            placeholder = {
              Text(
                  text = "Enter custom amount in T...",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.outline,
              )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(16.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent,
                ),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                      focusManager.clearFocus()
                      onConfirmTopUp()
                    },
                ),
        )

        Button(
            onClick = onConfirmTopUp,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = CircleShape,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
        ) {
          Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
              text = "Confirm Top-up",
              style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}

@Composable
fun DangerZoneCard(
    onDeletePocketClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = "Danger Zone",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.error,
        )
      }

      Text(
          text =
              "Deleting this pocket will unlink all associated expenses. Expense history will be preserved in your general transactions.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(modifier = Modifier.height(4.dp))

      Surface(
          onClick = onDeletePocketClick,
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          contentColor = MaterialTheme.colorScheme.error,
          shadowElevation = 1.dp,
          modifier = Modifier.fillMaxWidth().height(48.dp),
      ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
          Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
              text = "Delete Pocket",
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.error,
          )
        }
      }
    }
  }
}

@ComponentPreviews
@Composable
private fun PocketIdentitySectionPreview() {
  PocketIdentitySection(
      pocketName = "Daily Cafe & Coffee",
      onPocketNameChange = {},
  )
}

@ComponentPreviews
@Composable
private fun MonthlyAllocationCardEnabledPreview() {
  MonthlyAllocationCard(
      allocatedAmount = "1,500,000",
      autoRefillEnabled = true,
  )
}

@ComponentPreviews
@Composable
private fun MonthlyAllocationCardDisabledPreview() {
  MonthlyAllocationCard(
      allocatedAmount = "1,500,000",
      autoRefillEnabled = false,
  )
}

@ComponentPreviews
@Composable
private fun AssociatedCategoryCardPreview() {
  AssociatedCategoryCard(
      categoryName = "Cafe & Dining",
      categorySubtitle = "Auto-deduct transactions",
  )
}

@ComponentPreviews
@Composable
private fun ManualTopUpCardPreview() {
  ManualTopUpCard(
      currentBalance = "850,000",
      amount = "250,000",
      onAmountChange = {},
      onConfirmTopUp = {},
  )
}

@ComponentPreviews
@Composable
private fun DangerZoneCardPreview() {
  DangerZoneCard(
      onDeletePocketClick = {},
  )
}
