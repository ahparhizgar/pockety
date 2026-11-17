# Tests Specification

## Unit Tests (single component / class / function)

No new unit test suite is needed for three reversible resource-value corrections. Validate XML parsing, unchanged resource keys and placeholder signatures against the branch base, and successful resource compilation. Audit active source roots (app, design, server, core, excluding build outputs) for remaining «پاکت» substrings; historical documents and stored user content are excluded.

## E2E Flow Tests

No navigation or state changes require new component flow tests. Use the existing application to verify the affected screens below.

## Repository & Data Tests

No repository changes or migrations. Review the diff to confirm only intended localized values change and stored user content is untouched.

## UI & Accessibility Tests

### Settings version and privacy policy verification

- Given Persian locale and a known application version, such as 1.2.3
  - When settings is opened
    - [ ] Then the label displays «جیبک نسخه 1.2.3» (or the actual supplied version), with no literal placeholder or old brand.
- Given Persian locale
  - When the privacy policy is opened using keyboard navigation
    - [ ] Then the hero uses «جیبک» and the personal-data description uses «جیب‌ها», retaining all surrounding policy wording and half-spaces.
  - When the back control is activated using the keyboard
    - [ ] Then the prior screen is shown.
- Given English locale
  - When settings and privacy policy are opened
    - [ ] Then their existing English copy and supplied version remain unchanged.

These are explicit UI smoke checks; do not add automated tests that merely mirror the three replacement strings. Record checks performed and any environment limitation in the handoff.
