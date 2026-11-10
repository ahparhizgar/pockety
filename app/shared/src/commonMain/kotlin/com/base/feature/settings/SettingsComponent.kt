package com.base.feature.settings

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.calendar.CalendarType
import com.base.component.CContext
import com.base.component.componentScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class SettingsState(
    val selectedCalendarType: CalendarType = CalendarType.GREGORIAN,
)

interface SettingsComponent {
  val state: Value<SettingsState>

  fun onCalendarTypeSelected(type: CalendarType)
}

interface SettingsComponentFactory {
  fun create(cContext: CContext): SettingsComponent
}

class DefaultSettingsComponent(
    cContext: CContext,
    private val userSettingsRepository: UserSettingsRepository,
) : SettingsComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state = MutableValue(SettingsState())
  override val state: Value<SettingsState> = _state

  init {
    userSettingsRepository.calendarType
        .onEach { type -> _state.update { it.copy(selectedCalendarType = type) } }
        .launchIn(scope)
  }

  override fun onCalendarTypeSelected(type: CalendarType) {
    scope.launch { userSettingsRepository.setCalendarType(type) }
  }
}

class DefaultSettingsComponentFactory(
    private val userSettingsRepository: UserSettingsRepository,
) : SettingsComponentFactory {
  override fun create(cContext: CContext): SettingsComponent =
      DefaultSettingsComponent(
          cContext = cContext,
          userSettingsRepository = userSettingsRepository,
      )
}

@Fake
class FakeSettingsComponent(
    initialState: SettingsState = FakeSettingsStateFactory.create(),
) : SettingsComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<SettingsState> = _state

  override fun onCalendarTypeSelected(type: CalendarType) {
    _state.update { it.copy(selectedCalendarType = type) }
  }
}

class FakeSettingsComponentFactory : SettingsComponentFactory {
  override fun create(cContext: CContext): SettingsComponent = FakeSettingsComponent()
}

object FakeSettingsStateFactory {
  fun create(
      selectedCalendarType: CalendarType = CalendarType.GREGORIAN,
  ): SettingsState =
      SettingsState(
          selectedCalendarType = selectedCalendarType,
      )
}
