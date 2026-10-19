# Design

## Context

See `proposal.md` for motivation. Currently, `PocketEntity` holds `budgetAmount: Long`, and `topUpPocket(id, amount)` runs an update query mutating that column directly. `PocketDetailsComponent` renders hardcoded mock transactions from `defaultPocketTransactions()`.

In this change, `budgetAmount` is eliminated from `pockets`, a dedicated `pocket_top_ups` table stores all top-up events, and `PocketDetailsComponent` dynamically loads and merges real expenses linked to the pocket with real top-up records into an integrated timeline.

## Goals / Non-Goals

**Goals:**
- Create `PocketTopUpEntity` and table `pocket_top_ups` in Room (schema version 8).
- Write `MIGRATION_7_8` migrating schema and copying existing pocket `budgetAmount` values as initial top-up rows.
- Update `PocketRepository` and `PocketDao` to observe pocket balance derived from top-ups minus expenses.
- Update `PocketRepository.addPocket` to atomically record an initial top-up transaction.
- Update `PocketRepository.topUpPocket` to insert a top-up transaction with optional description.
- Update `PocketDetailsComponent` to observe real transactions for `pocketId` (both expenses and top-ups) and merge them chronologically descending.
- Maintain existing top-up bottom sheet UI without changes to its visual appearance or inputs.

**Non-Goals:**
- Editing or custom description inputs on the quick top-up bottom sheet UI (repository and data layer will support optional descriptions, but top-up UI remains unchanged per instructions).
- Deletion/reversal of top-ups from the UI in this slice (delete button remains exclusively for expenses).
- Re-architecting the general `transactions` table into a polymorphic transaction table.

## Decisions

### Decision 1: Dedicated `pocket_top_ups` table vs Unified `transactions` table
- **Choice**: Dedicated `pocket_top_ups` table:
  ```kotlin
  @Entity(
      tableName = "pocket_top_ups",
      foreignKeys = [
          ForeignKey(
              entity = PocketEntity::class,
              parentColumns = ["id"],
              childColumns = ["pocketId"],
              onDelete = ForeignKey.CASCADE,
          ),
      ],
      indices = [Index("pocketId")],
  )
  data class PocketTopUpEntity(
      @PrimaryKey(autoGenerate = true) val id: Long = 0,
      val pocketId: Long,
      val amount: Long,
      val description: String? = null,
      val createdAt: Instant,
  )
  ```
- **Rationale**: Keeps expense transactions focused on general spendings and preserves existing queries, foreign keys, and indexes on `transactions`. Top-ups belong directly to the pocket domain.
- **Alternatives considered**:
  - Unified `transactions` table with `TransactionType.EXPENSE | TOP_UP`: Rejected to avoid mutating the core expense table schema and polluting general expense queries with non-expense deposits.

### Decision 2: Schema Migration from v7 to v8 (`MIGRATION_7_8`)
- **Choice**:
  1. Create `pocket_top_ups` table with foreign key to `pockets(id)` and index on `pocketId`.
  2. Create temporary table `pockets_new` without `budgetAmount` column.
  3. Populate `pocket_top_ups` with initial entries from existing `pockets`:
     `INSERT INTO pocket_top_ups (pocketId, amount, description, createdAt) SELECT id, budgetAmount, 'Initial Balance', createdAt FROM pockets WHERE budgetAmount > 0`
  4. Copy pocket records to `pockets_new` (without `budgetAmount`), drop `pockets`, rename `pockets_new` to `pockets`, and recreate index on `categoryId`.
- **Rationale**: Guarantees zero data loss for existing users and maintains database referential integrity.

### Decision 3: Reactive Pocket Balance and Transaction Observation
- **Choice**:
  - In `PocketDao`:
    - `observePocketsWithBalances()` joins `pockets` with top-up sums and expense sums:
      ```sql
      SELECT 
          p.id, 
          p.name, 
          p.categoryTag, 
          p.icon, 
          p.createdAt,
          p.color,
          p.categoryId,
          COALESCE(topups.totalTopUp, 0) AS totalTopUpAmount,
          COALESCE(expenses.totalSpent, 0) AS spentAmount
      FROM pockets p
      LEFT JOIN (
          SELECT pocketId, SUM(amount) AS totalTopUp 
          FROM pocket_top_ups 
          GROUP BY pocketId
      ) topups ON topups.pocketId = p.id
      LEFT JOIN (
          SELECT pocketId, SUM(amount) AS totalSpent 
          FROM transactions 
          GROUP BY pocketId
      ) expenses ON expenses.pocketId = p.id
      ORDER BY p.createdAt DESC
      ```
    - `observePocketTopUps(pocketId: Long): Flow<List<PocketTopUpEntity>>`
    - `observePocketExpenses(pocketId: Long): Flow<List<ExpenseEntity>>` (or query via `ExpenseDao`)
  - In `PocketDetailsComponent`:
    - Combine pocket details, top-ups flow, and expenses flow using `combine(...)` in `componentScope()`.
    - Map each `ExpenseEntity` to `PocketTransactionUiModel(isTopUp = false, amount = expense.amount, title = expense.description, ...)`
    - Map each `PocketTopUpEntity` to `PocketTransactionUiModel(isTopUp = true, amount = topUp.amount, title = topUp.description ?: "Top-up", ...)`
    - Sort combined list by `createdAt` descending.
- **Rationale**: Clean reactive updates: whenever a top-up is confirmed or an expense is recorded/deleted, the balance and transaction feed update automatically in real time.

## Risks / Trade-offs

- **[Risk]** Room schema version bump and migration validation failure.  
  → **Mitigation**: Update `AppDatabase` version to 8, add `MIGRATION_7_8`, provide schema JSON for version 8, and verify with a dedicated test in `MigrationTest.kt`.
- **[Risk]** Breaking existing tests relying on `PocketEntity.budgetAmount` or fake factories.  
  → **Mitigation**: Update `FakePocketRepository` to track `topUpsFlow` and mirror the database balance calculation `sum(top-ups) - sum(expenses)`. Update fake factories with backward-compatible helper parameters where needed.
