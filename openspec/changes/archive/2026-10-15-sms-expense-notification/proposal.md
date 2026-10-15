# Proposal

## Why

Now that bank transaction SMS messages are received and stored as undecided candidate expenses in the background, users need an immediate, frictionless path to record the expense right as the SMS arrives. By issuing a system notification upon detecting and storing a candidate expense, the user can tap the notification to directly open the add-expense screen prefilled with the parsed amount and linked candidate expense, or dismiss/deny it right from the notification drawer without launching the app.

## What Changes

- Introduce a KMP notification manager abstraction (`ExpenseNotificationManager` / `ExpenseNotificationService`) with a no-op implementation on non-Android platforms (desktop, iOS, web) and a real Android implementation.
- On Android, configure a dedicated, silent notification channel (importance low/none, no sound, no vibration) specifically for incoming expense SMS notifications.
- When `SmsBroadcastReceiver` extracts and saves an undecided `CandidateExpense`, trigger the notification with the parsed amount, a prompt encouraging the user to save the expense, and a direct "Deny" action button.
- Tapping the notification body launches the app (`MainActivity`) with deep link / action intent extras identifying the candidate expense and prefilled amount.
- Tapping the "Deny" action triggers a background broadcast receiver that updates the candidate expense status to `REJECTED` and cancels the notification without launching the app.
- Introduce an application-level navigation event / action stream in KMP observed by `RootComponent` (or connected via `MainActivity`) to handle deep links and push `AddExpenseComponent` with the candidate details prefilled.

## Capabilities

### New Capabilities
- `sms-expense-notification`: Handling system notifications for candidate bank expenses upon SMS arrival, including silent channel management on Android, KMP notification service abstraction, direct notification "Deny" action without opening the UI, and notification tap handling via action event stream into `AddExpenseComponent`.

### Modified Capabilities
None.

## Impact

- **app/shared (commonMain)**: New notification abstraction interface (`ExpenseNotificationManager`) and deep link / navigation action stream (`NavigationActionBus` / `DeepLinkHandler`).
- **app/shared (desktopMain, iosMain, etc.)**: No-op implementation for `ExpenseNotificationManager`.
- **app/androidApp**:
  - Android implementation of `ExpenseNotificationManager` using Android `NotificationManager`, dedicated silent `NotificationChannel`, `PendingIntent.getActivity` targeting `MainActivity`, and `PendingIntent.getBroadcast` for the "Deny" action.
  - A broadcast receiver (`DenyExpenseReceiver`) to handle the "Deny" action in the background, updating `CandidateExpenseRepository.updateStatus(id, REJECTED)` and cancelling the notification.
- **MainActivity**: Handle incoming intent extras in `onCreate` and `onNewIntent`, dispatching navigation actions to the action stream.
- **RootComponent**: Observe the navigation action stream and push `AddExpenseComponent.Config(initialAmount, candidateExpenseId)` onto the Decompose navigation stack.
