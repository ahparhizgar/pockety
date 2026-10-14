# Design

## Context

See `proposal.md` for motivation.
The application uses Decompose and `CContext` for multiplatform component lifecycle, state, navigation, and messaging across Android, JVM Desktop, and iOS.
The design module already provides `SmsPermissionBanner` ("Auto-track bank SMS") and `UndecidedExpensesBanner` ("Undecided expenses") composables.
On Android, `RECEIVE_SMS` is a dangerous/restricted runtime permission. Handling permissions in Decompose without leaking `Activity` context across configuration changes requires a decoupled lifecycle-bound permission handler contract.

## Goals / Non-Goals

**Goals:**
- Provide a clean `PermissionController` interface accessible via `CContext`.
- Implement `AndroidPermissionController` on Android using `ActivityResultLauncher` registered on `MainActivity`, handling:
  - Checking permission status via `ContextCompat.checkSelfPermission`.
  - Checking `ActivityCompat.shouldShowRequestPermissionRationale` to detect auto-rejection / permanent denial.
  - Opening application settings via `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)` when auto-rejected.
  - Lifecycle unbinding to prevent memory leaks across Activity recreate/destroy.
- Provide a `FakePermissionController` in common/test code for JVM Desktop, iOS, and unit tests with configurable state.
- Update `HomeComponent` and `HomeScreen` to reactively render `SmsPermissionBanner` when permission is not granted, and switch to `UndecidedExpensesBanner` (count = 0) when granted.

**Non-Goals:**
- Implementing the incoming SMS broadcast receiver or SMS parsing logic (deferred to a follow-up feature).
- Fetching historical SMS or requesting `READ_SMS` (this slice focuses on `RECEIVE_SMS`).
- Real-time undecided expense database storage or categorization flows.

## Decisions

### 1. `PermissionController` interface on `CContext`
- **Decision**: Add `permissionController: PermissionController` property to `CContext` (with default provided in `DefaultCContext` and `TestCContext`), with `suspend fun requestReceiveSmsPermission(): Boolean`.
- **Rationale**: `CContext` is the standard ambient carrier across all Decompose components in this codebase (carrying navigation, messageShower, windowSizeHolder). Placing `permissionController` on `CContext` allows any component to access permission status and dispatch requests without constructor bloat, while keeping platform details hidden. Making the request method `suspend` returning `Boolean` allows callers (e.g., `HomeComponent`) to await the result and immediately initiate subsequent workflows (like auto-navigation) when granted.
- **Alternatives Considered**: Injected via Koin constructor parameter into `DefaultHomeComponent`. Rejected to maintain uniformity with `CContext` extensions (like `messageShower` and `navigation`) and seamless testing via `TestCContext`.

### 2. Leaking-free Android Activity Binding
- **Decision**: `AndroidPermissionController` is a long-lived class (or Koin singleton/activity-scoped delegate) that holds a weak or clearable delegate to the active Activity's `ActivityResultLauncher` and context.
  - `MainActivity` registers `registerForActivityResult(ActivityResultContracts.RequestPermission())` during `onCreate`.
  - In `MainActivity.onResume()` / `onCreate()`, it attaches its launcher to `AndroidPermissionController`.
  - In `MainActivity.onDestroy()`, it detaches / clears the launcher reference.
  - Asynchronous request outcomes bridge from the Activity callback into the suspending call via `CompletableDeferred<Boolean>`.
- **Rationale**: Decompose retained components can survive activity recreation (e.g. rotation). Never hold a strong reference to `Activity` inside `CContext` or `HomeComponent`.

### 3. Handling Permanent Denial & Settings Redirection
- **Decision**: In `AndroidPermissionController`, check platform rationale status and tracking of prior requests.
  - If `shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)` is `false` AND we have already prompted the user before, the system has permanently rejected / auto-blocked the dialog.
  - In this case, `requestReceiveSmsPermission()` constructs an intent with `Settings.ACTION_APPLICATION_DETAILS_SETTINGS` targeting `Uri.fromParts("package", packageName, null)` and launches settings, returning `false`.
  - When the user returns to the app, `MainActivity.onResume()` triggers a check and refreshes `permissionController` state.

### 4. UI Transition on Home Screen
- **Decision**: In `HomeScreen`, display either `SmsPermissionBanner` or `UndecidedExpensesBanner` above the expense list.
  - When `isSmsPermissionGranted == false`: Render `SmsPermissionBanner(onClick = component::onEnableSmsTrackingClicked)`.
  - When `isSmsPermissionGranted == true`: Render `UndecidedExpensesBanner(count = 0, amountFormatted = "$0")`.

## Risks / Trade-offs

- **[Risk] User changes permission in System Settings while app is backgrounded** → **Mitigation**: Refresh permission state in `onResume()` of `MainActivity`, updating the `StateFlow`/`Value` on `PermissionController`.
- **[Risk] Target platform without Android APIs (Desktop, iOS)** → **Mitigation**: Shared module provides `FakePermissionController` with in-memory state toggle for JVM desktop/iOS, ensuring zero Android dependency in commonMain.
