# Tasks

## 1. Data Layer & Persistence

- [x] 1.1 Create `CandidateExpenseStatus` enum, `CandidateExpenseEntity` entity, and fake factory; verify compilation with `./gradlew compileKotlinJvm`
- [x] 1.2 Implement Room DAO and Room migration `MIGRATION_2_3` in `AppDatabase`, update database version to 3; verify with database migration tests
- [x] 1.3 Create `CandidateExpenseRepository` interface and implementation with fake factory and unit tests in `CandidateExpenseRepositoryTest`; verify with `./gradlew jvmTest`

## 2. SMS Parsing & Android Background Receiver

- [x] 2.1 Implement `SmsExpenseParser` in shared common code supporting Persian bank withdrawal/purchase regexes and amount extraction; verify with comprehensive unit tests in `SmsExpenseParserTest`
- [x] 2.2 Implement Android `SmsBroadcastReceiver` registered in `AndroidManifest.xml` that parses incoming SMS and inserts pending candidate expenses via `CandidateExpenseRepository`; verify with Android integration/unit test

## 3. Inbox Decompose Component & UI Screen

- [x] 3.1 Implement `InboxComponent` and state model exposing pending candidates, dismissal (`REJECTED`), and selection actions; verify with component unit tests in `InboxComponentTest`
- [x] 3.2 Implement `InboxScreen` in `app/shared` adapting the UI from `:design/features/inbox/Screen.kt` using Pockety components, string resources, and component previews; verify with screenshot tests
- [x] 3.3 Register `InboxComponent` in Koin and add navigation child/config to `RootComponent`; verify navigation works from tests

## 4. AddExpense & Home Banner Integration

- [x] 4.1 Update `AddExpenseComponent.Config` to accept prefilled `initialAmount` and `candidateExpenseId`, marking the candidate as `ACCEPTED` upon saving expense; verify with unit tests in `AddExpenseTest`
- [x] 4.2 Update `HomeComponent` and `HomeScreen`'s `UndecidedExpensesBanner` to observe pending candidate count/sum and navigate to `InboxComponent` on click; verify in `SmsPermissionIntegrationUiTest`
- [x] 4.3 Run full verification gate (`./gradlew jvmTest test`, `koverVerify`, `detektMain`, `ktfmtCheck`); verify all checks pass
