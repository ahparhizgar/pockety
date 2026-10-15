# Tasks

## 1. Notification Abstraction & Action Stream in Shared Module

- [ ] 1.1 Create `NavigationActionBus` (or `DeepLinkHandler`) and `NavigationAction` sealed interface in `app/shared/src/commonMain/kotlin/com/base/navigation/` with replay capability, verifying unit test dispatches and receives events
- [ ] 1.2 Create `ExpenseNotificationManager` interface in `app/shared/src/commonMain/kotlin/com/base/notification/ExpenseNotificationManager.kt` with `showExpenseCandidateNotification` and `cancelNotification` methods
- [ ] 1.3 Implement `NoOpExpenseNotificationManager` in commonMain/desktopMain/iosMain and register in shared Koin module, verifying JVM compilation succeeds

## 2. Navigation Integration in RootComponent & MainActivity

- [ ] 2.1 Update `RootComponent` to observe `NavigationActionBus.events` in its component scope and push `AddExpenseComponent.Config(initialAmount, candidateExpenseId)`, verifying integration test `com.base.capability.sms_expense_notification.SmsExpenseNotificationFlowE2eTest`
- [ ] 2.2 Wire `MainActivity` `onCreate` and `onNewIntent` to extract notification deep link extras and emit `NavigationAction.OpenAddExpense` onto `NavigationActionBus`

## 3. Android Notification Channel, Deny Action & Delivery

- [ ] 3.1 Implement `DenyExpenseReceiver` in `app/androidApp/src/main/kotlin/com/base/notification/DenyExpenseReceiver.kt` to update candidate status to `REJECTED` and cancel notification, with receiver test verification
- [ ] 3.2 Implement `AndroidExpenseNotificationManager` in `app/androidApp/src/main/kotlin/com/base/notification/AndroidExpenseNotificationManager.kt` checking notification permissions (`NotificationManagerCompat.areNotificationsEnabled()`) to safely no-op when missing, creating silent notification channel (`IMPORTANCE_LOW`, no sound, no vibration), "Deny" action button (`PendingIntent.getBroadcast`), and tap intent (`PendingIntent.getActivity`)
- [ ] 3.3 Add string resources for notification title, body prompt, and "Deny" button text
- [ ] 3.4 Register `DenyExpenseReceiver` and `<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />` in `AndroidManifest.xml`, and bind `AndroidExpenseNotificationManager` in Android Koin module

## 4. SmsBroadcastReceiver Integration & Capability Verification

- [ ] 4.1 Update `SmsBroadcastReceiver` to inject `ExpenseNotificationManager` and trigger `showExpenseCandidateNotification(...)` after inserting the candidate into `CandidateExpenseRepository`
- [ ] 4.2 Write and run capability test `com.base.capability.sms_expense_notification.SmsExpenseNotificationTest` verifying that parsed bank SMS triggers candidate creation and notification manager
- [ ] 4.3 Run full verification gate (`compileKotlinJvm`, `jvmTest`, `ktfmtCheck` / `ktfmtFormat`) to verify everything passes
