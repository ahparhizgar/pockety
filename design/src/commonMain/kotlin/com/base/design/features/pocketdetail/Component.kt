package com.base.design.features.pocketdetail

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.design.globalcomponents.PocketyTopBar
import com.base.design.ui.ComponentPreviews

sealed interface PocketLedgerItem {
  val id: String
  val title: String
  val date: String
  val note: String?
  val amount: String

  data class Expense(
      override val id: String,
      override val title: String,
      override val date: String,
      override val amount: String,
      override val note: String? = null,
      val icon: ImageVector = Icons.Default.Coffee,
  ) : PocketLedgerItem

  data class TopUp(
      override val id: String,
      override val title: String,
      override val date: String,
      override val amount: String,
      override val note: String? = "Auto-deposit",
      val icon: ImageVector = Icons.Default.ArrowDownward,
  ) : PocketLedgerItem
}

val samplePocketLedgerItems: List<PocketLedgerItem> =
    listOf(
        PocketLedgerItem.Expense(
            id = "1",
            title = "Sam Cafe Vanak",
            date = "Today, 10:15 AM",
            note = "Meeting with Ali",
            amount = "-85,000 T",
            icon = Icons.Default.Coffee,
        ),
        PocketLedgerItem.Expense(
            id = "2",
            title = "Lamiz Coffee Jordan",
            date = "Yesterday, 4:30 PM",
            note = "Iced Latte & Cookie",
            amount = "-120,000 T",
            icon = Icons.Default.LocalCafe,
        ),
        PocketLedgerItem.TopUp(
            id = "3",
            title = "Monthly Pocket Allocation",
            date = "23 Oct, 09:00 AM",
            note = "Auto-deposit",
            amount = "+500,000 T",
            icon = Icons.Default.ArrowDownward,
        ),
        PocketLedgerItem.Expense(
            id = "4",
            title = "Reza Bakery & Espresso",
            date = "22 Oct, 11:20 AM",
            note = "Morning Americano",
            amount = "-65,000 T",
            icon = Icons.Default.BakeryDining,
        ),
        PocketLedgerItem.Expense(
            id = "5",
            title = "Cafe Kariz Garden",
            date = "20 Oct, 6:00 PM",
            note = "Herbal tea & cheesecake",
            amount = "-95,000 T",
            icon = Icons.Default.LocalCafe,
        ),
        PocketLedgerItem.Expense(
            id = "6",
            title = "Bread & Salt Bakery",
            date = "18 Oct, 08:10 AM",
            note = "Sourdough & filter roast",
            amount = "-45,000 T",
            icon = Icons.Default.BakeryDining,
        ),
    )

@Composable
fun PocketDetailTopBar(
    title: String = "Pocket Details",
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onMoreOptionsClick: () -> Unit = {},
) {
  PocketyTopBar(
      modifier = modifier,
      title = title,
      navigationIcon = {
        IconButton(onClick = onBackClick) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Go back",
              tint = MaterialTheme.colorScheme.onSurface,
          )
        }
      },
      actions = {
        IconButton(onClick = onSettingsClick) {
          Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "Pocket settings",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        IconButton(onClick = onMoreOptionsClick) {
          Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "More options",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      },
  )
}

@Composable
fun PocketHeroBalance(
    title: String,
    balance: String,
    modifier: Modifier = Modifier,
    currencySymbol: String = "T",
    balanceLabel: String = "Balance",
    icon: ImageVector = Icons.Default.LocalCafe,
) {
  Column(
      modifier = modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Surface(
        modifier = Modifier.size(64.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shadowElevation = 1.dp,
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp),
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
    ) {
      Text(
          text = balance,
          style =
              MaterialTheme.typography.displayLarge.copy(
                  fontSize = 36.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = (-0.02).sp,
              ),
          color = MaterialTheme.colorScheme.onSurface,
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
          text = currencySymbol,
          style =
              MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.SemiBold,
              ),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(bottom = 4.dp),
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    Text(
        text = balanceLabel,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun PocketActionBar(
    modifier: Modifier = Modifier,
    topUpText: String = "Top-up",
    configureText: String = "Configure",
    onTopUpClick: () -> Unit = {},
    onConfigureClick: () -> Unit = {},
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Button(
        onClick = onTopUpClick,
        shape = CircleShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        modifier = Modifier.weight(1f).height(48.dp),
    ) {
      Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
          text = topUpText,
          style = MaterialTheme.typography.labelLarge,
      )
    }

    FilledTonalButton(
        onClick = onConfigureClick,
        shape = CircleShape,
        colors =
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        modifier = Modifier.weight(1f).height(48.dp),
    ) {
      Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
          text = configureText,
          style = MaterialTheme.typography.labelLarge,
      )
    }
  }
}

@Composable
fun PocketFeedbackBanner(
    message: String,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
      }
      IconButton(
          onClick = onDismissClick,
          modifier = Modifier.size(28.dp),
      ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Dismiss",
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}

@Composable
fun PocketExpensesHeader(
    title: String = "Expenses",
    countText: String? = null,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )
    if (countText != null) {
      Text(
          text = countText,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
fun PocketTransactionRow(
    title: String,
    date: String,
    amount: String,
    modifier: Modifier = Modifier,
    note: String? = null,
    icon: ImageVector = Icons.Default.Coffee,
    onDeleteClick: (() -> Unit)? = null,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      color = MaterialTheme.colorScheme.surface,
      shape = RoundedCornerShape(12.dp),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
          }
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
              text = title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
          )
          Text(
              text = if (note != null) "$date • $note" else date,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
          )
        }
      }

      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
            text = amount,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (onDeleteClick != null) {
          IconButton(
              onClick = onDeleteClick,
              modifier = Modifier.size(36.dp),
          ) {
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = "Delete expense",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
fun PocketTopUpEventRow(
    title: String,
    date: String,
    amount: String,
    modifier: Modifier = Modifier,
    note: String? = "Auto-deposit",
    icon: ImageVector = Icons.Default.ArrowDownward,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f),
      shape = RoundedCornerShape(12.dp),
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
          }
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
              text = title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
          )
          Text(
              text = if (note != null) "$date • $note" else date,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
          )
        }
      }

      Text(
          text = amount,
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(end = 8.dp),
      )
    }
  }
}

@ComponentPreviews
@Composable
private fun PocketDetailTopBarPreview() {
  PocketDetailTopBar()
}

@ComponentPreviews
@Composable
private fun PocketHeroBalancePreview() {
  PocketHeroBalance(
      title = "Daily Cafe & Coffee",
      balance = "850,000",
  )
}

@ComponentPreviews
@Composable
private fun PocketActionBarPreview() {
  PocketActionBar()
}

@ComponentPreviews
@Composable
private fun PocketFeedbackBannerPreview() {
  PocketFeedbackBanner(
      message = "Expense removed. +85,000 T restored to pocket.",
      onDismissClick = {},
  )
}

@ComponentPreviews
@Composable
private fun PocketExpensesHeaderPreview() {
  PocketExpensesHeader(
      countText = "4 expenses",
  )
}

@ComponentPreviews
@Composable
private fun PocketTransactionRowPreview() {
  PocketTransactionRow(
      title = "Sam Cafe Vanak",
      date = "Today, 10:15 AM",
      note = "Meeting with Ali",
      amount = "-85,000 T",
      onDeleteClick = {},
  )
}

@ComponentPreviews
@Composable
private fun PocketTopUpEventRowPreview() {
  PocketTopUpEventRow(
      title = "Monthly Pocket Allocation",
      date = "23 Oct, 09:00 AM",
      note = "Auto-deposit",
      amount = "+500,000 T",
  )
}
