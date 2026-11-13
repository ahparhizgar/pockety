package com.base.feature.settings

interface LanguageSettingsHandler {
  val supportsSystemLocaleSettings: Boolean

  fun openSystemLocaleSettings()
}

class DefaultLanguageSettingsHandler(
    override val supportsSystemLocaleSettings: Boolean = false,
    private val onOpenSystemLocaleSettings: () -> Unit = {},
) : LanguageSettingsHandler {
  override fun openSystemLocaleSettings() {
    onOpenSystemLocaleSettings()
  }
}

class FakeLanguageSettingsHandler(
    override var supportsSystemLocaleSettings: Boolean = false,
) : LanguageSettingsHandler {
  var openSystemLocaleSettingsCalled = false
    private set

  override fun openSystemLocaleSettings() {
    openSystemLocaleSettingsCalled = true
  }
}
