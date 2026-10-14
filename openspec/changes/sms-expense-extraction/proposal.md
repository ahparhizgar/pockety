# Proposal

## Why

With SMS reception permission granted, bank transaction SMS messages arrive on the device but are not automatically captured or processed. Users currently must manually enter amounts and details for each expense. By detecting incoming bank transaction SMS messages in the background, extracting Rial amounts using regex, and surfacing them as pending candidate expenses in an Inbox screen, users can review, dismiss, or quickly convert bank notifications into recorded expenses without manual data entry.

## What Changes

- Add background reception of incoming SMS messages on Android via a `BroadcastReceiver`.
- Add regex-based parsing to detect bank debit/transaction SMS patterns and extract the transaction amount (in Rials).
- Store extracted SMS candidates in Room database (`candidate_expenses` table) with status `PENDING`, `REJECTED`, or `ACCEPTED`.
- Provide an Inbox screen (based on the design module) listing all `PENDING` candidate expenses.
- Update the home screen's undecided expenses banner to display real-time counts and totals of pending candidate expenses, and navigate to the Inbox screen upon clicking.
- Support dismissing a candidate expense from the Inbox screen, marking its status as `REJECTED`.
- Support tapping a candidate expense to open `AddExpenseComponent` with the extracted amount pre-filled.
- When an expense created from a candidate is saved, update the candidate expense's status to `ACCEPTED`.

## Capabilities

### New Capabilities
- `sms-expense-inbox`: Background SMS receipt, regex parsing of bank transaction amounts, storage in Room database with status tracking (`PENDING`, `REJECTED`, `ACCEPTED`), an Inbox review screen, and conversion flow into accepted expenses.

### Modified Capabilities
- `expense-management`: The undecided expenses banner on the home screen must reflect the actual count and total of pending candidate expenses and navigate to the Inbox screen when clicked.

## Impact

- **Database**: Room schema bump to version 3 with new `candidate_expenses` table and migration `MIGRATION_2_3`.
- **Android Manifest & Receiver**: Add `BroadcastReceiver` for `android.provider.Telephony.SMS_RECEIVED`.
- **Navigation & Components**: Add `InboxComponent`, update `HomeComponent` and `RootComponent` navigation stack, and update `AddExpenseComponent` to accept prefilled amount and candidate ID.
- **Dependencies**: No external dependencies added; relies on standard Android SMS broadcast and Compose/Decompose libraries.
