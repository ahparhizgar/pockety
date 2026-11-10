# Proposal

## Why

Pockety requires multi-calendar support to serve users in regions relying on the Solar Hijri (Persian) calendar alongside standard Gregorian calendar systems. Currently, date manipulation and calendar arithmetic lack a unified abstraction, preventing date conversions, localized period arithmetic, and multi-calendar representations. Providing a dedicated calendar abstraction in the `core` infrastructure module enables consistent calendar operations across all targets.

## What Changes

- Introduce a cohesive `calendar` infrastructure abstraction in `core/src/commonMain` defining unified calendar date representation and calendar arithmetic operations.
- Implement the standard Gregorian calendar provider supporting conversions to and from epoch milliseconds/instants, month and day navigation, and leap year calculations.
- Implement the Persian (Solar Hijri) calendar provider supporting accurate Jalali/Solar Hijri conversions, month and day navigation, leap year calculations, and period arithmetic, utilizing algorithms compatible with Kotlin Multiplatform.
- Provide comprehensive unit tests and fake factories in `core` validating calendar conversions, bidirectional mappings, and boundary cases.

## Capabilities

### New Capabilities
- `calendar-system`: Provides calendar interfaces and implementations for Gregorian and Persian (Solar Hijri) calendars, including date representations, epoch instant conversions, and date arithmetic.

### Modified Capabilities
<!-- None: purely new infrastructure capability -->

## Impact

- **Core Module (`core`)**: New interfaces, data classes, and implementations in `com.base.calendar` (or subpackages under `com.base`). Addition of `kotlinx-datetime` dependency in `core/build.gradle.kts` if needed for instant interoperability.
- **Future Feature Adoption**: Pockety features (such as expense analytics, pocket cycles, and monthly budgets) will be able to depend on this unified calendar interface across JVM and Android targets.
