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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.design.ui.ComponentPreviews

data class CandidateExpense(
    val id: String,
    val bankName: String,
    val senderOrCard: String,
    val timestamp: String,
    val amountFormatted: String,
    val rawSms: String,
    val quickCategories: List<String>,
)

/**
 * Status overview card for Smart SMS Extraction showing auto-sync status, pending review count,
 * description, and review progress bar.
 */
@Composable
fun SmartExtractionHeaderCard(
    remainingCount: Int,
    modifier: Modifier = Modifier,
    totalCount: Int = 8,
    isAutoSyncOn: Boolean = true,
) {
  val progress =
      if (totalCount > 0) {
        ((totalCount - remainingCount).toFloat() / totalCount).coerceIn(0.08f, 1f)
      } else {
        1f
      }

  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
              text = "SMART EXTRACTION",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.secondary,
              letterSpacing = 0.5.sp,
          )

          if (isAutoSyncOn) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
              Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Surface(
                    modifier = Modifier.size(6.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                ) {}
                Text(
                    text = "Auto-Sync ON",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Medium,
                )
              }
            }
          }
        }

        Text(
            text = "$remainingCount remaining",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
        )
      }

      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "$remainingCount Candidates Pending Review",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text =
                "$remainingCount bank SMS candidates awaiting action. Items disappear permanently once confirmed or dismissed.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp,
        )
      }

      LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
          color = MaterialTheme.colorScheme.primaryContainer,
          trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
      )
    }
  }
}

/** Parsed payload container rendering raw SMS text with monospace font. */
@Composable
fun ParsedSmsBox(
    rawSms: String,
    modifier: Modifier = Modifier,
    title: String = "Parsed SMS",
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Icon(
            imageVector = Icons.Default.Sms,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary,
        )
      }
      Text(
          text = rawSms,
          style =
              MaterialTheme.typography.bodySmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  lineHeight = 16.sp,
              ),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

/** Clickable category chip with an icon and label for quick categorization. */
@Composable
fun QuickCategoryChip(
    category: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Star,
) {
  Surface(
      onClick = onClick,
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
      modifier = modifier,
  ) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(14.dp),
      )
      Text(
          text = category,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSecondaryContainer,
      )
    }
  }
}

/**
 * Candidate expense card representing a single parsed bank SMS transaction with raw payload,
 * category chips, and action buttons.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CandidateExpenseCard(
    candidate: CandidateExpense,
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit = {},
    onDismissClick: () -> Unit = {},
    onOpenEditClick: () -> Unit = {},
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shadowElevation = 1.dp,
  ) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top,
      ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Surface(
              modifier = Modifier.size(40.dp),
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.secondaryContainer,
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                  imageVector = Icons.Default.AccountBalance,
                  contentDescription = candidate.bankName,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp),
              )
            }
          }

          Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
              Text(
                  text = candidate.senderOrCard,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              )
            }
            Text(
                text = "${candidate.bankName} • ${candidate.timestamp}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )
          }
        }

        Text(
            text = candidate.amountFormatted,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
      }

      ParsedSmsBox(
          rawSms = candidate.rawSms,
          title = if (candidate.bankName.contains("Mellat")) "Parsed SMS" else "Parsed SMS Stream",
      )

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Quick categorize:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          candidate.quickCategories.forEach { category ->
            QuickCategoryChip(
                category = category,
                onClick = { onCategoryClick(category) },
            )
          }
        }
      }

      Row(
          modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        TextButton(
            onClick = onDismissClick,
            colors =
                ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary,
                ),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        ) {
          Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Dismiss",
              modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
              text = "Dismiss",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
          )
        }

        Button(
            onClick = onOpenEditClick,
            shape = CircleShape,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
          Text(
              text = "Edit / Open",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
          )
        }
      }
    }
  }
}

/** Empty state card shown when all candidate SMS items have been processed. */
@Composable
fun InboxCleanCard(
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondaryContainer,
          modifier = Modifier.size(56.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = Icons.Default.Verified,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(28.dp),
          )
        }
      }
      Text(
          text = "Inbox Clean!",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text =
              "All candidates have been processed. New financial SMS will be caught automatically.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
      )
    }
  }
}

object FakeCandidateExpenseFactory {
  fun create(
      id: String = "mellat-1",
      bankName: String = "Bank Mellat",
      senderOrCard: String = "+9820000",
      timestamp: String = "Today, 14:32",
      amountFormatted: String = "320,000 T",
      rawSms: String =
          "Sender: +9820000 | Withdrawal: 3,200,000 Rials - Purchase: Kourosh Hypermarket - Remaining: 4,120,000 Rials",
      quickCategories: List<String> = listOf("Groceries", "Cafe", "Commute"),
  ): CandidateExpense =
      CandidateExpense(
          id = id,
          bankName = bankName,
          senderOrCard = senderOrCard,
          timestamp = timestamp,
          amountFormatted = amountFormatted,
          rawSms = rawSms,
          quickCategories = quickCategories,
      )

  fun createList(): List<CandidateExpense> =
      listOf(
          create(
              id = "mellat-1",
              bankName = "Bank Mellat",
              senderOrCard = "+9820000",
              timestamp = "Today, 14:32",
              amountFormatted = "320,000 T",
              rawSms =
                  "Sender: +9820000 | Withdrawal: 3,200,000 Rials - Purchase: Kourosh Hypermarket - Remaining: 4,120,000 Rials",
              quickCategories = listOf("Groceries", "Cafe", "Commute"),
          ),
          create(
              id = "saman-2",
              bankName = "Bank Saman",
              senderOrCard = "8290••••4419",
              timestamp = "Today, 11:15",
              amountFormatted = "1,450,000 T",
              rawSms = "Sender: BankSaman | Withdrawal: 14,500,000 Rials - Hyperstar Eram",
              quickCategories = listOf("Groceries", "Bills", "Shopping"),
          ),
          create(
              id = "pasargad-3",
              bankName = "Bank Pasargad",
              senderOrCard = "5022••••9011",
              timestamp = "Yesterday, 19:40",
              amountFormatted = "65,000 T",
              rawSms = "Sender: Pasargad | Snapp Food order: 650,000 Rials",
              quickCategories = listOf("Cafe", "Transport", "Other"),
          ),
          create(
              id = "blubank-4",
              bankName = "BluBank",
              senderOrCard = "6219••••7702",
              timestamp = "Yesterday, 10:05",
              amountFormatted = "520,000 T",
              rawSms = "Sender: BluBank | Transfer: 5,200,000 Rials to Saman Hosseini",
              quickCategories = listOf("Shopping", "Entertainment", "Other"),
          ),
      )
}

@ComponentPreviews
@Composable
private fun SmartExtractionHeaderCardPreview() {
  SmartExtractionHeaderCard(
      remainingCount = 8,
      totalCount = 8,
  )
}

@ComponentPreviews
@Composable
private fun CandidateExpenseCardPreview() {
  CandidateExpenseCard(
      candidate = FakeCandidateExpenseFactory.create(),
  )
}

@ComponentPreviews
@Composable
private fun ParsedSmsBoxPreview() {
  ParsedSmsBox(
      rawSms =
          "Sender: +9820000 | Withdrawal: 3,200,000 Rials - Purchase: Kourosh Hypermarket - Remaining: 4,120,000 Rials",
  )
}

@ComponentPreviews
@Composable
private fun QuickCategoryChipPreview() {
  QuickCategoryChip(
      category = "Groceries",
      onClick = {},
  )
}

@ComponentPreviews
@Composable
private fun InboxCleanCardPreview() {
  InboxCleanCard()
}
