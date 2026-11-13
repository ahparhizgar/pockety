# Tasks

## 1. String Resources

- [ ] 1.1 Add descriptive subtitles and calendar option string resources to `strings.xml` in `:app:shared` and verify compilation.

## 2. Settings UI Components

- [ ] 2.1 Implement `CalendarOptionCard` in `com.base.feature.settings` in `:app:shared` matching the `:design` module specifications (badge symbol, title, subtitle, checkmark/radio indicator, elevated selection container) with keyboard accessibility.
- [ ] 2.2 Implement `DisplayCalendarSystemCard` in `com.base.feature.settings` in `:app:shared` containing header icon, title, and side-by-side `CalendarOptionCard`s mapped to `CalendarType`.
- [ ] 2.3 Add component previews (`@ComponentPreviews`) for `CalendarOptionCard` (selected and unselected) and `DisplayCalendarSystemCard`.

## 3. Screen Integration & UI Tests

- [ ] 3.1 Update `SettingsScreen.kt` in `:app:shared` to use `DisplayCalendarSystemCard`, applying test tags `calendar-option-gregorian` and `calendar-option-persian`.
- [ ] 3.2 Update screen previews (`@ScreenPreviews`) in `SettingsScreen.kt` for default (Gregorian) and Persian states.
- [ ] 3.3 Verify existing UI tests (`SettingsE2eUiTest`) and flow tests (`SettingsFlowE2eTest`) pass cleanly.

## 4. Verification Gate

- [ ] 4.1 Run full verification suite (`./gradlew compileKotlinJvm compileKotlin`, `./gradlew jvmTest test`, `./gradlew :detektMain`, `./gradlew ktfmtFormat`) and ensure all gates pass.

## Workflow follow-up

- Archive the change after Developer implementation and CI sign-off.
- Reassign to CTO for final review.
