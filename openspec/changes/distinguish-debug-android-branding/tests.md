# Tests Specification

## Unit Tests (single component / class / function)

No runtime logic is added. Use artifact/resource integration checks below; source XML assertions alone do not prove Android qualifier resolution. If reusable automated verification is added, follow `com.base.feature.android.branding` per AGENTS.md rather than introducing component-based packages.

## E2E Flow Tests

### Android branding artifact verification

**Target:** Debug and release APKs and their resolved resource tables.

- Given English and Persian supported launcher locales
  - When the debug artifact is built and its labels are resolved for each locale
    - [ ] Then labels are `Debug Pockety` and `Debug جیبک` respectively (project-name debug scenarios).
- Given the production baseline resources from main
  - When the release artifact is built
    - [ ] Then labels are `Pockety` and `جیبک` and launcher artwork matches the baseline (project-name production and production branding isolation).
- Given debug and release artifacts
  - When packaged application IDs and signing information are inspected
    - [ ] Then IDs remain `ir.ahparhizgar.pockety.debug` and `ir.ahparhizgar.pockety`, and signing behavior is unchanged.
- Given the implementation diff
  - When affected platform resources are reviewed
    - [ ] Then desktop/web branding and production source assets are unchanged (other platform branding).

## Repository & Data Tests

Not applicable: no persistence, network or migration changes.

## UI & Accessibility Tests

### Launcher branding visual verification

- Given debug square and round legacy icons at mdpi through xxxhdpi
  - When previews are rendered at their native sizes
    - [ ] Then a visible bug badge and recognizable Pockety identity are present in each (legacy launcher icons).
- Given debug adaptive artwork
  - When circle, squircle and rounded-square masks are applied to resolved foregrounds
    - [ ] Then the bug is recognizable without clipping, including qualifier-selected artwork (adaptive launcher masks).
- Given themed icons enabled on a supported launcher
  - When debug monochrome artwork is rendered in light and dark themes
    - [ ] Then the bug and Pockety identity are recognizable (themed launcher icons).
- Given both installed variants in English and Persian launcher locales
  - When the launcher displays them
    - [ ] Then debug can be distinguished by label and icon, with the Persian Debug prefix visible.

Deliver a contact sheet/mask previews and packaged-resource inspection evidence. Record device/emulator availability and any manual validation limitation explicitly. No app UI actions or inputs are introduced, so new Compose keyboard tests are unnecessary.

## Implementation Evidence

Validated on implementation commit `4a71b3fbf3780bd06c56fa06930d446e73825071`:

- `./gradlew ktfmtFormat` completed successfully locally; no local compilation, test, or APK build was run.
- GitHub Actions [CI run 44124824071](https://github.com/ahparhizgar/pockety/actions/runs/44124824071) passed compilation, JVM tests, tests, coverage, Detekt, ktfmt check, and design web build.
- GitHub Actions [Android build run 44124824219](https://github.com/ahparhizgar/pockety/actions/runs/44124824219) built and uploaded debug and release APKs.
- The downloaded APK `resources.arsc` contains `Debug Pockety` and `Debug جیبک` for debug, and `Pockety` and `جیبک` for release. Binary manifest string pools contain application IDs `ir.ahparhizgar.pockety.debug` and `ir.ahparhizgar.pockety` respectively.
- The debug resource table contains adaptive icons, the monochrome drawable, the API 24 foreground override, and all ten legacy square/round density files. All ten packaged legacy PNGs match the generated debug source files byte for byte. Extracted packaged square and round xxxhdpi previews were visually inspected.
- Both APKs contain an APK Signing Block. The build configuration and main/release icon resources are unchanged by this change; desktop and web source files are unchanged. Release APK packaging succeeded with the existing release signing configuration.
- `app/androidApp/src/debug/icons/preview.html` shows the generated icons under circle, squircle, and rounded-square masks plus light/dark monochrome previews. No device/emulator launcher session was available, so device-specific launcher presentation was not manually tested.
