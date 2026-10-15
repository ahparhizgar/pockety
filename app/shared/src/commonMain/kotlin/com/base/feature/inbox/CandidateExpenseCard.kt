package com.base.feature.inbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.base.db.CandidateExpenseEntity
import com.base.db.FakeCandidateExpenseEntityFactory
import com.base.ui.ComponentPreviews
import org.jetbrains.compose.resources.stringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.currency_symbol
import pockety.app.shared.generated.resources.inbox_dismiss_action
import pockety.app.shared.generated.resources.inbox_edit_open_action
import pockety.app.shared.generated.resources.inbox_parsed_sms

@Composable
fun CandidateExpenseCard(
    candidate: CandidateExpenseEntity,
    modifier: Modifier = Modifier,
    onDismissClick: () -> Unit = {},
    onOpenEditClick: () -> Unit = {},
) {
  val currency = stringResource(Res.string.currency_symbol)

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
      CandidateExpenseHeader(candidate = candidate, currency = currency)

      ParsedSmsBox(rawSms = candidate.rawBody)

      CandidateExpenseActions(
          onDismissClick = onDismissClick,
          onOpenEditClick = onOpenEditClick,
      )
    }
  }
}

@Composable
private fun CandidateExpenseHeader(
    candidate: CandidateExpenseEntity,
    currency: String,
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
              contentDescription = candidate.sender,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp),
          )
        }
      }

      Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceContainer,
      ) {
        Text(
            text = candidate.sender,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
      }
    }

    Text(
        text = "${candidate.amount} $currency",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.testTag("candidate-amount"),
    )
  }
}

@Composable
private fun CandidateExpenseActions(
    onDismissClick: () -> Unit,
    onOpenEditClick: () -> Unit,
) {
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
        modifier = Modifier.testTag("candidate-dismiss-button"),
    ) {
      Icon(
          imageVector = Icons.Default.Close,
          contentDescription = stringResource(Res.string.inbox_dismiss_action),
          modifier = Modifier.size(16.dp),
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
          text = stringResource(Res.string.inbox_dismiss_action),
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
        modifier = Modifier.testTag("candidate-open-button"),
    ) {
      Text(
          text = stringResource(Res.string.inbox_edit_open_action),
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

@Composable
private fun ParsedSmsBox(
    rawSms: String,
    modifier: Modifier = Modifier,
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
            text = stringResource(Res.string.inbox_parsed_sms),
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

@ComponentPreviews
@Composable
private fun CandidateExpenseCardPreview() {
  CandidateExpenseCard(
      candidate = FakeCandidateExpenseEntityFactory.create(),
  )
}
