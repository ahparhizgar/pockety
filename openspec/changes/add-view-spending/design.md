# Design: Add and View Transaction

## Context

The application currently has boilerplate authentication flows and a placeholder item list backed by an HTTP API. In order to transform Pockety into an agile, offline-first personal finance app, we bypass login and replace the default screen with a minimal transaction viewing and recording flow backed by Room database.

## Goals / Non-Goals

**Goals:**
- Direct navigation into `MainComponent` / `HomeScreen` without auth or token checks.
- Store transactions locally using Room (`TransactionEntity`, `TransactionDao`).
- Provide an `AddTransactionComponent` and screen containing only amount and description inputs with validation.
- Provide end-to-end integration tests combining Add and View operations independent of internal database details.
- Provide unit/component tests for `AddTransactionComponent` verifying input validations and error states.

**Non-Goals:**
- Backend synchronization or server endpoints.
- Transaction categories, labels, attachments, SMS parsing, or pocket management in this initial slice.
- Complex analytics or monthly charts.

## Decisions

### 1. Remove Auth from Root Routing
- **Decision**: Update `DefaultRootComponent` to route directly to `MainComponent.Config` on startup without checking `TokenStorage`.
- **Alternatives considered**: Setting a mock token permanently in storage. Direct routing is preferred because it cleanly removes unnecessary auth machinery from runtime execution.

### 2. Local Persistence with Room
- **Decision**: Introduce `TransactionEntity` (`id: Long`, `amount: Long`, `description: String`, `createdAt: Instant`) and `TransactionDao` in `app/shared/src/commonMain/kotlin/com/base/db/`.
- **Alternatives considered**: In-memory repository only. Room provides persistent app restarts and follows existing project database conventions.

### 3. Decompose Component Structure
- **Decision**: `MainComponent` hosts a Decompose stack containing:
  - `HomeScreen`: Shows recorded transactions and has an action to open the add form.
  - `AddTransactionScreen`: Allows entering amount and description, with save/cancel callbacks.
- **Alternatives considered**: Dialog/bottom sheet outside Decompose navigation. Using Decompose child stack provides back-handling, state preservation, and consistent navigation lifecycle.

### 4. Testing Architecture (AGENTS.md Compliance)
- **Decision**:
  - `AddViewTransactionE2eTest.kt`: Drives the user flow through `RootComponent` / `MainComponent`, adds a transaction, and verifies its appearance in the transaction list.
  - `AddTransactionComponentTest.kt`: Directly tests `AddTransactionComponent` verifying validation behavior (e.g., zero/empty amount, error messages).
  - Use `FakeTransactionFactory` with `create()` and `createList()` defaults.

## Risks / Trade-offs

- **[Room Schema Migration]** → Adding a new entity requires incrementing Room database version or adjusting schema in `AppDatabase.kt`. We will add the entity cleanly and handle database versioning.
- **[Decoupling Existing Auth Tests]** → Existing auth tests may fail if auth routes are altered. We will adjust or isolate auth references to keep the build green.
