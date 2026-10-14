# Tasks

## 1. Permission Controller Foundation & CContext Integration

- [ ] 1.1 Define `PermissionController` interface and `FakePermissionController` in `commonMain`, exposing `isSmsPermissionGranted: StateFlow<Boolean>` and `requestReceiveSmsPermission()`
- [ ] 1.2 Integrate `permissionController` into `CContext`, `DefaultCContext`, and `TestCContext` and verify existing common tests pass
- [ ] 1.3 Add unit tests for `FakePermissionController` verifying state updates and request callbacks

## 2. Android Manifest & Android Permission Handling

- [ ] 2.1 Add `android.permission.RECEIVE_SMS` to `app/androidApp/src/main/AndroidManifest.xml`
- [ ] 2.2 Implement `AndroidPermissionController` in `app/shared/src/androidMain` handling `RECEIVE_SMS`, permission status checking via `ContextCompat`, rationale checking, and opening app settings on permanent denial
- [ ] 2.3 Wire `AndroidPermissionController` in `MainActivity` with `registerForActivityResult`, ensuring launcher references are bound in `onCreate`/`onResume` and unbound in `onDestroy` to prevent memory leaks

## 3. UI Implementation in HomeComponent and HomeScreen

- [ ] 3.1 Extract/implement `SmsPermissionBanner` and `UndecidedExpensesBanner` in `app/shared` (following designs from `:design` module)
- [ ] 3.2 Update `HomeComponent` and `HomeState` to observe `permissionController.isSmsPermissionGranted` and handle `onEnableSmsTrackingClicked`
- [ ] 3.3 Update `HomeScreen` to render `SmsPermissionBanner` when permission is not granted, and `UndecidedExpensesBanner` with count 0 when granted
- [ ] 3.4 Add `@ScreenPreviews` and `@ComponentPreviews` for both permission granted and denied states in `HomeScreen.kt`

## 4. Testing & Verification

- [ ] 4.1 Write integration tests in `HomeScreenTest` / `HomeComponentTest` asserting the toggle between `SmsPermissionBanner` and `UndecidedExpensesBanner` when permission state changes
- [ ] 4.2 Run Gradle verification: `./gradlew compileKotlinJvm compileKotlin --offline` and `./gradlew jvmTest test --offline`
- [ ] 4.3 Run code formatting: `./gradlew ktfmtFormat` and detekt: `./gradlew :detektMain`
