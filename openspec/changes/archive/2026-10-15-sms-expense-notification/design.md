# Design

## Context

See `proposal.md` for motivation. Currently, incoming SMS bank messages are intercepted on Android by `SmsBroadcastReceiver`, parsed via `SmsExpenseParser`, and persisted in Room as `CandidateExpense` with `status = PENDING`. Users must manually open the app to see the pending count and navigate to the inbox.

We want to trigger a silent notification immediately when a candidate is saved, handle notification click by opening the Add Expense screen pre-filled, and provide a "Deny" action button in the notification allowing the user to mark the candidate as `REJECTED` without opening the app.

## Goals / Non-Goals

**Goals:**
- Provide a clean KMP interface abstraction (`ExpenseNotificationManager`) with Android implementation and no-op implementation for other targets (`desktop`, `ios`, `wasmJs`).
- Create a dedicated Android `NotificationChannel` (`"candidate_expense_channel"`) set to low importance (silent, no sound, no vibration).
- Deliver formatted notification strings encouraging expense saving with the detected amount in Rials.
- Add a "Deny" action button to the notification that triggers a background receiver (`DenyExpenseReceiver`), updates the candidate status to `REJECTED` in `CandidateExpenseRepository`, and cancels the notification.
- Handle notification tap to navigate into `AddExpenseComponent` with candidate ID and initial amount via an application navigation event / action stream.

**Non-Goals:**
- Requesting runtime notification permission (e.g. `POST_NOTIFICATIONS` runtime prompt dialog) in this vertical slice; notification posting must check permission and safely no-op if absent without throwing.
- Notification badge customization or custom expanded remote views.
- Cross-platform notification delivery (desktop/iOS notification daemons are non-goals for this slice).

## Decisions

### 1. KMP Abstraction & Platform Placement
- Interface `ExpenseNotificationManager` in `app/shared/src/commonMain/kotlin/com/base/notification/ExpenseNotificationManager.kt`.
- Has method `fun showExpenseCandidateNotification(candidateId: Long, amount: Long, sender: String, rawBody: String)` and `fun cancelNotification(candidateId: Long)`.
- No-op implementation in `app/shared/src/commonMain` or `desktopMain`/`iosMain` (`NoOpExpenseNotificationManager`).
- Android implementation `AndroidExpenseNotificationManager` in `app/androidApp/src/main/kotlin/com/base/notification/`. Accesses Android `Context` and Android `NotificationManagerCompat`.

### 2. Silent Channel & Notification Actions Configuration
- Channel ID: `"candidate_expense_channel"`.
- Importance: `NotificationManager.IMPORTANCE_LOW` (no sound, no vibration, shown in notification drawer and status bar).
- In Android 8.0+ (Oreo+), create channel in `onCreate` of Application or lazily inside `ExpenseNotificationManager`.
- Call `setSound(null, null)` and `enableVibration(false)`.
- **Notification Tap Content Intent**: `PendingIntent.getActivity` with `MainActivity` destination and extras (`EXTRA_NAV_CANDIDATE_ID`, `EXTRA_NAV_AMOUNT`).
- **Notification Deny Action Intent**: `PendingIntent.getBroadcast` targeting `DenyExpenseReceiver` with extras (`EXTRA_CANDIDATE_ID`).

### 3. Background Deny Handling (`DenyExpenseReceiver`)
- An Android `BroadcastReceiver` registered in `AndroidManifest.xml` (or dynamic / intent action).
- Injects `CandidateExpenseRepository` and `ExpenseNotificationManager` via Koin.
- When invoked:
  1. Extracts candidate ID from extras.
  2. Launches coroutine in `goAsync()`.
  3. Calls `candidateExpenseRepository.updateStatus(candidateId, CandidateExpenseStatus.REJECTED)`.
  4. Cancels the notification via `NotificationManagerCompat.cancel(notificationId)`.

### 4. Deep Link Navigation via Navigation Event Stream
- When notification is clicked, `PendingIntent.getActivity` launches `MainActivity`.
- Singleton/shared `NavigationActionBus` in `app/shared/src/commonMain`:
  ```kotlin
  sealed interface NavigationAction {
    data class OpenAddExpense(val candidateExpenseId: Long, val initialAmount: String) : NavigationAction
  }

  interface NavigationActionBus {
    val events: SharedFlow<NavigationAction>
    fun emit(action: NavigationAction)
  }
  ```
- `MainActivity` inspects intent in `onCreate` and `onNewIntent`, extracts extras, and calls `navigationActionBus.emit(NavigationAction.OpenAddExpense(...))`.
- `RootComponent` observes `navigationActionBus.events` in its component scope and executes `rootNavigation.pushNew(AddExpenseComponent.Config(initialAmount, candidateExpenseId))`.

## Risks / Trade-offs

- **[Risk] Notification permission on Android 13+ (POST_NOTIFICATIONS)**:
  - *Mitigation*: On Android 13+ (API 33+), check `NotificationManagerCompat.areNotificationsEnabled()` before posting. If permission isn't granted, posting gracefully no-ops.
- **[Risk] Background execution limits on Deny action**:
  - *Mitigation*: `BroadcastReceiver.goAsync()` allows quick async database update within the standard 10-second window.
