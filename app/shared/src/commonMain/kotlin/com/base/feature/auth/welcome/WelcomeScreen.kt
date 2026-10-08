package com.base.feature.auth.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.base.ui.ScreenPreviews

@Composable
fun WelcomeScreen(component: WelcomeComponent, modifier: Modifier = Modifier) {
  Surface(modifier = modifier) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(modifier = Modifier.weight(1f))
      WelcomeHeader()
      Spacer(modifier = Modifier.weight(1f))
      WelcomeActions(component)
      Spacer(modifier = Modifier.height(48.dp))
      WelcomeFooter()
    }
  }
}

@Composable
private fun WelcomeHeader() {
  Box(contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
          modifier =
              Modifier.size(80.dp)
                  .background(
                      MaterialTheme.colorScheme.primary,
                      RoundedCornerShape(12.dp),
                  )
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
          "OpenSplit",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.headlineSmall,
      )
    }
  }
}

@Composable
private fun WelcomeActions(component: WelcomeComponent) {
  Column {
    Button(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        onClick = component::onSignUpClicked,
        shape = RoundedCornerShape(8.dp),
    ) {
      Text("Sign up", style = MaterialTheme.typography.titleMedium)
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedButton(
        modifier = Modifier.fillMaxWidth().height(56.dp).testTag("welcome-login-button"),
        onClick = component::onLoginClicked,
        shape = RoundedCornerShape(8.dp),
    ) {
      Text("Log in", style = MaterialTheme.typography.titleMedium)
    }
  }
}

@Composable
private fun WelcomeFooter() {
  Row(
      modifier = Modifier.padding(bottom = 16.dp),
      horizontalArrangement = Arrangement.Center,
  ) {
    Text(
        modifier = Modifier.clickable {},
        text = "Terms",
        style = MaterialTheme.typography.bodySmall,
        textDecoration = TextDecoration.Underline,
    )
    Text(" | ", style = MaterialTheme.typography.bodySmall)
    Text(
        modifier = Modifier.clickable {},
        text = "Privacy Policy",
        style = MaterialTheme.typography.bodySmall,
        textDecoration = TextDecoration.Underline,
    )
    Text(" | ", style = MaterialTheme.typography.bodySmall)
    Text(
        modifier = Modifier.clickable {},
        text = "Contact us",
        style = MaterialTheme.typography.bodySmall,
        textDecoration = TextDecoration.Underline,
    )
  }
}

@Composable
@ScreenPreviews
private fun WelcomeScreenPreview() {
  Surface { WelcomeScreen(FakeWelcomeComponent()) }
}
