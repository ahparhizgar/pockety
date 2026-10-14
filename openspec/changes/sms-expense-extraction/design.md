# Design

## Context

See `proposal.md` for motivation. The application is a Kotlin Multiplatform app using Room database for persistence and Decompose for screen navigation. SMS permission handling has been introduced, but incoming SMS messages are not yet intercepted. The Design module contains an `InboxScreen` preview that showcases the visual UI for reviewing smart-extracted candidate expenses.

## Goals / Non-Goals

**Goals:**
- Intercept incoming SMS messages on Android using a manifest-registered `BroadcastReceiver` that triggers in the background.
- Extract bank transaction amounts formatted in Rials using regex patterns in shared code.
- Store extracted transactions in Room (`CandidateExpenseEntity`) with `PENDING`, `REJECTED`, or `ACCEPTED` status.
- Implement `InboxComponent` and `InboxScreen` in `app/shared` adapted from `:design/features/inbox/Screen.kt`.
- Update `AddExpenseComponent` to support optional pre-filled amount and linking to candidate expense IDs so saving sets status to `ACCEPTED`.
- Link the home screen's `UndecidedExpensesBanner` to live pending candidate count/sum and navigation.

**Non-Goals:**
- Currency conversion (e.g. Rials to Tomans): as decided, currency conversion will only be handled in the presentation layer when user currency settings are implemented later.
- Historical SMS query/scan (only live incoming SMSs via receiver are processed).
- Complex categorization or merchant extraction: only the amount, sender, raw body, and timestamp are captured.

## Decisions

### 1. Database Schema & Migration (Room v3)
- **Entity**: `CandidateExpenseEntity`
  - `id`: Long (PK, autoincrement)
  - `sender`: String
  - `rawBody`: String
  - `amount`: Long
  - `status`: `CandidateExpenseStatus` (Enum: `PENDING`, `REJECTED`, `ACCEPTED`)
  - `receivedAt`: `Instant`
- **Migration**: `MIGRATION_2_3` creates `candidate_expenses` table with appropriate columns and indices.
- *Alternative considered*: Storing directly into `transactions` table with an unconfirmed flag. *Rejected* because candidate expenses represent unconfirmed external suggestions with raw message context, separate from confirmed user expenses.

### 2. Multiplatform Regex Parser (`SmsExpenseParser`)
- Placed in `app/shared/src/commonMain/kotlin/com/base/sms/SmsExpenseParser.kt`.
- Extracts amounts matching Persian banking conventions (e.g. `مبلغ`, `برداشت`, `خرید`, `انتقال` followed by comma-delimited numeric figures and `ریال`).
- Strips Persian numeral separators and comma formatting into a standard integer `Long`.
- *Alternative considered*: Regex parsing inside Android receiver. *Rejected* because placing it in `commonMain` enables fast unit testing without Android emulator/robolectric overhead.

### 3. Android Background SMS Receiver
- Create `SmsBroadcastReceiver : BroadcastReceiver` in `app/androidApp` (or `app/shared/src/androidMain`).
- Registered in `AndroidManifest.xml` with `android.provider.Telephony.SMS_RECEIVED` intent filter.
- Injects `CandidateExpenseRepository` using Koin, parses PDU messages, runs `SmsExpenseParser`, and launches a coroutine to insert valid matches as `PENDING`.
- *Alternative considered*: WorkManager periodic job. *Rejected* because background broadcast receiver responds immediately upon SMS receipt.

### 4. Decompose Navigation & State Integration
- **`InboxComponent`**:
  - Exposes `Value<InboxState>` with list of pending candidates.
  - Callbacks: `onDismiss(id: Long)`, `onSelectCandidate(candidate: CandidateExpense)`.
- **`AddExpenseComponent.Config`**:
  - Updated from `data object Config` to `data class Config(val initialAmount: String = "", val candidateExpenseId: Long? = null)`.
  - When saving succeeds and `candidateExpenseId` is provided, `CandidateExpenseRepository.updateStatus(id, ACCEPTED)` is called.
- **`RootComponent`**:
  - Adds `Child.Inbox(InboxComponent)` to the navigation stack.

## Risks / Trade-offs

- [Risk: SMS format variance across banks] → Mitigation: Design `SmsExpenseParser` with a configurable list of regex patterns and comprehensive unit tests against diverse real-world SMS samples.
- [Risk: Android process death / receiver constraints on newer Android OS versions] → Mitigation: `SMS_RECEIVED` is an explicit broadcast allowed for receivers declared in manifest when permission is granted. Database insertion is fast and lightweight.
