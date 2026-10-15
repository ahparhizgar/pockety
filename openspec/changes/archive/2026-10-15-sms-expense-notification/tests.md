# Tests Specification

## Test Strategy & Capability Mapping

Conforming to `AGENTS.md`, testing for the `sms-expense-notification` capability is organized under `com.base.capability.sms_expense_notification`:
- **Capability Unit / Component Tests**: `SmsExpenseNotificationTest.kt` verifying receiver notification triggering with mock/fake notification manager, and deny receiver updating candidate status to `REJECTED`.
- **Root Navigation Integration Flow Tests**: `SmsExpenseNotificationFlowE2eTest.kt` verifying that dispatching a navigation action via `NavigationActionBus` transitions `RootComponent` to `AddExpenseComponent` with candidate ID and initial amount prefilled.
- **Android Platform Tests**: Unit verification of `AndroidExpenseNotificationManager` ensuring channel configuration, deny action button creation, and notification attributes.

## Capability Tests

### `com.base.capability.sms_expense_notification.SmsExpenseNotificationTest`
- **Target**: `SmsBroadcastReceiver` integration with `ExpenseNotificationManager` and `CandidateExpenseRepository`.
- **Scenarios**:
  - [ ] Given an incoming bank transaction SMS, When the receiver processes the SMS and saves a candidate expense, Then `ExpenseNotificationManager.showExpenseCandidateNotification` is invoked with the candidate ID, extracted amount, and sender.
  - [ ] Given an SMS that is not a bank transaction, When processed by the receiver, Then no notification is displayed.

### `com.base.capability.sms_expense_notification.DenyExpenseReceiverTest`
- **Target**: Background deny action processing in `DenyExpenseReceiver`.
- **Scenarios**:
  - [ ] Given a pending candidate expense and its active notification, When the deny action is received, Then `CandidateExpenseRepository.updateStatus` marks the candidate as `REJECTED` and the notification is cancelled.

## Integration & E2E Flow Tests

### `com.base.capability.sms_expense_notification.SmsExpenseNotificationFlowE2eTest`
- **Flow**: App cold/warm launch from notification click to `AddExpenseComponent` via `NavigationActionBus` and `RootComponent`.
- **Scenarios**:
  - [ ] Given the application is running at the home screen, When a `NavigationAction.OpenAddExpense` event is emitted on `NavigationActionBus`, Then the active child in `RootComponent` becomes `AddExpenseComponent` with `initialAmount` matching the candidate amount and `candidateExpenseId` set.
  - [ ] Given the user is in `AddExpenseComponent` opened from the notification, When the user saves the expense, Then the candidate expense is marked as `ACCEPTED` and the user returns to the previous screen.

## Android Platform Tests

### `com.base.sms.AndroidExpenseNotificationManagerTest`
- **Focus**: Channel creation, deny action intent, and notification parameters on Android.
- **Scenarios**:
  - [ ] Given Android API 26+, When the manager posts a candidate expense notification, Then the silent notification channel is created with `IMPORTANCE_LOW` and no vibration or sound.
  - [ ] Given a candidate expense notification, When built, Then it contains a content `PendingIntent` targeting `MainActivity` and an action `PendingIntent` with title "Deny" targeting `DenyExpenseReceiver`.
