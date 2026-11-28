package com.base.sms

import kotlinx.serialization.Serializable

/** Placeholder location of the remote SMS rules file; replace with the real URL. */
const val SMS_REMOTE_CONFIG_URL =
    "https://raw.githubusercontent.com/ahparhizgar/pockety/main/sms-remote-config.json"

/**
 * Remotely hosted overrides for [SmsExpenseParser], so a bank template the local
 * logic mishandles can be fixed without an app release. A message matching an
 * [exclude] pattern is never an expense, even when the local logic accepts it;
 * a message matching an [include] pattern is an expense whenever an amount can
 * be extracted, even when the local logic rejects it. Exclude wins when both
 * match. Patterns run against the parser's normalized text, are compiled
 * case-insensitively, and invalid patterns are ignored.
 */
@Serializable
data class SmsRemoteConfig(
    val include: List<String> = emptyList(),
    val exclude: List<String> = emptyList(),
)

/** The compiled form of a [SmsRemoteConfig]; empty rules change nothing. */
class SmsRemoteRules(config: SmsRemoteConfig = SmsRemoteConfig()) {
  private val includeRegexes = config.include.mapNotNull(::compilePattern)
  private val excludeRegexes = config.exclude.mapNotNull(::compilePattern)

  fun isIncluded(text: String): Boolean = includeRegexes.any { it.containsMatchIn(text) }

  fun isExcluded(text: String): Boolean = excludeRegexes.any { it.containsMatchIn(text) }

  private companion object {
    fun compilePattern(pattern: String): Regex? =
        runCatching { Regex(pattern, RegexOption.IGNORE_CASE) }.getOrNull()
  }
}
