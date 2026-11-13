package com.base.feature.settings

enum class AppLanguage(val code: String) {
  ENGLISH("en"),
  PERSIAN("fa");

  companion object {
    fun fromCode(code: String): AppLanguage =
        entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
  }
}
