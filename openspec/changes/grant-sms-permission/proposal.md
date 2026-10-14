# Proposal

## Why

Automating transaction recording from bank SMS requires access to SMS messages on Android. To deliver the "Auto-track bank SMS" feature seamlessly while complying with modern Android permission best practices, the home screen needs to prompt the user for `RECEIVE_SMS` permission, gracefully handle multiple rejections (navigating to system settings when permanently denied), and switch to displaying the "Undecided expenses" component once permission is granted.

## What Changes

- Add permission controller abstraction to `CContext` (with real implementation on Android, and fakes on desktop/iOS/tests).
- Declare `android.permission.RECEIVE_SMS` in Android manifest and manage runtime permission requesting, status checks, and settings navigation without leaking Activity contexts.
- Update `HomeComponent` and `HomeScreen` to:
  - Display the `SmsPermissionBanner` ("Auto-track bank SMS") when permission is not granted.
  - Request `RECEIVE_SMS` permission when the banner is tapped, directing users to App Settings if auto-rejected/permanently denied.
  - Display the `UndecidedExpensesBanner` ("Undecided expenses") showing a 0 count when permission is granted.

## Capabilities

### New Capabilities
- `sms-permission`: Manages SMS permission lifecycle, requests, permanent rejection settings navigation, and permission status exposure via `CContext`.

### Modified Capabilities
- `expense-management`: Add requirement to display SMS tracking promotion when permission is not granted and display undecided expenses review summary once permission is granted.

## Impact

- `CContext`: Extended with `permissionController` (or `smsPermissionController`).
- `MainActivity`: Registers activity result launcher for `RECEIVE_SMS` and delegates to the Android permission controller without lifecycle memory leaks.
- `app/shared/src/commonMain/kotlin/com/base/feature/home/`: `HomeComponent`, `HomeScreen`, and `HomeState` updated with permission states and banner composables.
- Manifest: `android.permission.RECEIVE_SMS` added.
