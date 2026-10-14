# Design: Add and View Expense

## Context

The application currently has boilerplate authentication flows and a placeholder item list backed by an HTTP API. In order to transform Pockety into an agile, offline-first personal finance app, we bypass login and replace the default screen with a minimal expense viewing and recording flow backed by Room database.

## Goals / Non-Goals

**Goals:**
- Direct navigation into `MainComponent` / `HomeScreen` without auth or token checks.
- Store expenses locally using Room (`ExpenseEntity`, `ExpenseDao`).
- Provide an `AddExpenseComponent` and screen containing only amount and description inputs with validation.
- Provide end-to-end integration tests combining Add and View operations independent of internal database details.
- Provide unit/component tests for `AddExpenseComponent` verifying input validations and error states.

**Non-Goals:**
- Backend synchronization or server endpoints.
- Expense categories, labels, attachments, SMS parsing, or pocket management in this initial slice.
- Complex analytics or monthly charts.

## Decisions

### 1. Remove Auth from Root Routing
- **Decision**: Update `DefaultRootComponent` to route directly to `MainComponent.Config` on startup without checking `TokenStorage`.
- **Alternatives considered**: Setting a mock token permanently in storage. Direct routing is preferred because it cleanly removes unnecessary auth machinery from runtime execution.

### 2. Local Persistence with Room
- **Decision**: Introduce `ExpenseEntity` (`id: Long`, `amount: Long`, `description: String`, `createdAt: Instant`) and `ExpenseDao` in `app/shared/src/commonMain/kotlin/com/base/db/`.
- **Alternatives considered**: In-memory repository only. Room provides persistent app restarts and follows existing project database conventions.

### 3. Decompose Component Structure
- **Decision**: `MainComponent` hosts a Decompose stack containing:
  - `HomeScreen`: Shows recorded expenses and has an action to open the add form.
  - `AddExpenseScreen`: Allows entering amount and description, with save/cancel callbacks.
- **Alternatives considered**: Dialog/bottom sheet outside Decompose navigation. Using Decompose child stack provides back-handling, state preservation, and consistent navigation lifecycle.

### 4. Testing Architecture (AGENTS.md Compliance)
- **Decision**:
  - `AddViewExpenseFlowE2eTest.kt`: Drives the user flow through `RootComponent` / `MainComponent`, adds an expense, and verifies its appearance in the expense list.
  - `AddExpenseTest.kt`: Directly tests `AddExpenseComponent` verifying validation behavior (e.g., zero/empty amount, error messages).
  - Use `FakeExpenseFactory` with `create()` and `createList()` defaults.

## Risks / Trade-offs

- **[Room Schema Migration]** → Adding a new entity requires incrementing Room database version or adjusting schema in `AppDatabase.kt`. We will add the entity cleanly and handle database versioning.
- **[Decoupling Existing Auth Tests]** → Existing auth tests may fail if auth routes are altered. We will adjust or isolate auth references to keep the build green.
