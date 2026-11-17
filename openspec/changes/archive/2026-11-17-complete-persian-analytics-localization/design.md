# Design

## Context
See proposal.md for motivation. AnalyticsComponent preformats headers with literal Today/Yesterday, gets English/transliterated names from core getMonthName, and appends literal T at all amount levels. AnalyticsScreen already resolves five static resource keys; four lack Persian entries. Existing tests live in com.base.feature.expense.analytics. RootScreen observes appLanguage for RTL; Android MainActivity syncs system locales on resume, while the fallback Settings flow only persists appLanguage. Resource-locale refresh needs direct verification.

## Goals / Non-Goals
**Goals:** Separate calendar/expense computation from resource-backed presentation and keep language updates correct without resetting selection.
**Non-Goals:** Calendar arithmetic changes, global localization cleanup, digit-style redesign, currency conversion, or new financial behavior.

## Decisions
1. Resolve dynamic UI prose with Compose resources in the presentation layer. Expose typed date/relative-day and numeric amount information from Analytics state rather than storing English sentences. Retain Value-based state, existing component navigation, and IDs/test tags. This avoids asynchronous resource lookup in buildState and stale cached strings. Updating component-owned English strings on each language emission was considered but duplicates resource-lifecycle concerns.
2. Add bilingual resource patterns for month/year, Today/date, Yesterday/date, and older dates, plus Gregorian and Persian month names as needed. Keep core calendar arithmetic and its existing getMonthName contract intact; add a shared-client resource month-name resolver rather than changing core's English results for other callers. Resource patterns own word order and punctuation. Keep existing numeric/time conventions unless an existing localized helper already applies.
3. Replace literal T with the existing currency_symbol resource (English T, Persian تومان), consistent with Home and AddExpense. Audit found currencyType stored and exposed in Settings, but no consumption by expense displays or shared currency conversion formatter; formatCurrency also hard-codes T. Therefore no new Rials conversion/preference behavior is introduced in this slice. Preserve numeric amounts and negative expense signs. If concurrent main introduces a real shared preference-aware formatter, reuse it rather than duplicating it, while retaining its semantics.
4. Verify resource selection through the real supported language flow, including fallback settings. Reuse the existing application locale mechanism; if fallback only changes RTL, add the smallest shared locale coordination needed for Analytics resources to follow appLanguage. Do not infer locale from layout direction, force a device-wide locale, or reset calendar/month on language emissions. System settings resume follows Android's existing sync path.
5. Maintain empty/populated @ScreenPreviews and any affected public @ComponentPreviews; no layout redesign. Existing keyboard targets and semantic descriptions remain accessible.

## Risks / Trade-offs
- Resource locale differs from persisted language → test actual rendered text after a supported switch, not only repository state or RTL.
- Calendar and locale accidentally coupled → cover all four language/calendar combinations and year-boundary navigation.
- UI model changes affect fakes and existing assertions → update reusable fake factories and behavioral assertions together; preserve observable financial/navigation behavior.
- Shared strings edited concurrently by POC-37/38/39 → work only in this worktree and rebase linearly, preserving others' keys.
- Currency preference currently has no expense formatting integration → record this limitation; a broader currency feature needs its own specification.

## Migration Plan
No data migration. Ship resource/presentation changes together; rollback by reverting the implementation commit. Developer runs required local gates and verifies GitHub Actions before returning to Analyst for archive/sync and linear main integration.
