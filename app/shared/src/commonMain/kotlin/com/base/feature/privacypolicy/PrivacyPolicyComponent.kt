package com.base.feature.privacypolicy

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.base.Fake
import com.base.component.CContext

interface PrivacyPolicyComponent {
  val state: Value<PrivacyPolicyState>

  fun onBack()
}

data class PrivacyPolicyState(
    val versionNote: String = "Nov 2026",
)

interface PrivacyPolicyComponentFactory {
  fun create(
      cContext: CContext,
      onBack: () -> Unit,
  ): PrivacyPolicyComponent
}

class DefaultPrivacyPolicyComponent(
    cContext: CContext,
    private val onBackClick: () -> Unit,
) : PrivacyPolicyComponent, CContext by cContext {
  private val _state = MutableValue(PrivacyPolicyState())
  override val state: Value<PrivacyPolicyState> = _state

  override fun onBack() {
    onBackClick()
  }
}

class DefaultPrivacyPolicyComponentFactory : PrivacyPolicyComponentFactory {
  override fun create(
      cContext: CContext,
      onBack: () -> Unit,
  ): PrivacyPolicyComponent =
      DefaultPrivacyPolicyComponent(
          cContext = cContext,
          onBackClick = onBack,
      )
}

@Fake
class FakePrivacyPolicyComponent(
    initialState: PrivacyPolicyState = FakePrivacyPolicyStateFactory.create(),
    private val onBackClick: () -> Unit = {},
) : PrivacyPolicyComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<PrivacyPolicyState> = _state

  override fun onBack() {
    onBackClick()
  }
}

class FakePrivacyPolicyComponentFactory : PrivacyPolicyComponentFactory {
  override fun create(
      cContext: CContext,
      onBack: () -> Unit,
  ): PrivacyPolicyComponent =
      FakePrivacyPolicyComponent(
          onBackClick = onBack,
      )
}

object FakePrivacyPolicyStateFactory {
  fun create(
      versionNote: String = "Nov 2026",
  ): PrivacyPolicyState =
      PrivacyPolicyState(
          versionNote = versionNote,
      )
}
