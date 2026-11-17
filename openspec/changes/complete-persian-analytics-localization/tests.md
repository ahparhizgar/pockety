# Tests Specification

Use com.base.feature.expense.analytics, following AGENTS.md and existing tests rather than the schema's generic capability package examples. Use reusable fake factories, real data layer with fake services, and separate Given/When/Then. Assert behavior and rendered resources, never service invocation counts.

## Unit Tests (single component / class / function)

### `expense/analytics/AnalyticsLocalizationTest`
**Target:** Analytics public state and presentation behavior; extend existing AnalyticsTest/AnalyticsCalendarTest where appropriate.

- Given expenses created today, yesterday, and an older date using a controlled reference date/time and time zone
  - When Analytics groups them under either calendar
    - [ ] Then date values and relative-day classifications are correct, including yesterday across month/year boundaries.
- Given a selected historical month with expenses, custom category names, and descriptions containing English/Persian text
  - When language changes in either direction
    - [ ] Then selected year/month, calendar, amounts, groups, IDs, and user content remain unchanged.
- Given either calendar at a year boundary
  - When previous month and then next month are selected
    - [ ] Then selected dates follow that calendar, and future navigation remains disabled at the current month.

## E2E Flow Tests

### `expense/analytics/AnalyticsLocalizationFlowE2eTest`
**Flow:** Root → Analytics → Settings language selection → Analytics.

- Given the real root with fake services and an expense in a selected historical month
  - When the supported fallback language flow changes English to Persian and returns to Analytics
    - [ ] Then active language is Persian with the same calendar, month, totals, groups, and original user content.
  - When the language is changed back to English and Analytics reopened
    - [ ] Then active language is English and the same state remains intact.
- Given either calendar selection and either stored currency selection
  - When language changes
    - [ ] Then neither preference changes and numeric amounts remain unchanged; currency selection does not introduce new conversion behavior in this slice.

## Repository & Data Tests
No new schema, migration, API, or repository behavior. Existing repository suites remain the regression gate.

## UI & Accessibility Tests

### `expense/analytics/AnalyticsLocalizationE2eUiTest`
- Given English/Persian × Gregorian/Persian calendar and controlled populated data
  - When Analytics renders
    - [ ] Then title, monthly label, Today/Yesterday/older headers, month/year, and all three amount levels use expected resources; assert actual Persian words (including امروز/دیروز and a Persian month name), not merely absence of English.
    - [ ] Then English retains English Gregorian/transliterated Persian month names; numeric totals and negative expense signs are unchanged.
- Given an empty month in either language
  - When Analytics renders
    - [ ] Then localized empty state and localized zero total are present.
- Given the supported settings fallback flow with Analytics already visited
  - When language is changed English → Persian → English through keyboard actions
    - [ ] Then rendered static/dynamic labels update with no stale language; RTL/LTR and user-entered names remain correct.
- Given Analytics in either language
  - When previous/next month and an expense are activated using keyboard focus/activation
    - [ ] Then navigation and opening work; accessibility descriptions are localized and next is disabled at the current month; existing semantic tags remain available.
- Given Android API 33+ with per-app language support
  - When the system per-app language changes and the app resumes
    - [ ] Then populated and empty Analytics use the active language, including dynamic headers. Record device/emulator evidence if this flow cannot be automated locally; do not substitute a state-only test.

Maintain populated and empty screen previews using @ScreenPreviews and affected public components using @ComponentPreviews. Run compile, tests, coverage, Detekt and ktfmt gates from AGENTS.md and verify GitHub Actions against the implementation commit; report any unavailable platform validation explicitly.
