# Proposal

> DRAFT — written 2026-11-26 from Amir's question in WhatsApp
> (2026-11-26 11:51 Asia/Tehran). No decision yet, no app code changed.
> Amir said: «پیاده سازی نکن. فقط نظرت رو بگو.» — this change only
> records the decision-ready analysis so implementation can start
> immediately once he picks an option.

## Why

Pockets and categories are two overlapping selectors today, and the
overlap already confused a real user flow:

- On 2026-11-25 Amir asked why «تفریح» appeared twice on a pocket card:
  the badge is the pocket's linked category name and the title is the
  pocket's own name — he had named both the same. Fixed then with a tag
  icon, but the root cause (name/icon/color picked twice, once per
  category and once per pocket) is still there.
- Recording an expense asks for both a category and a pocket, and the
  two can disagree: an expense can have a category different from its
  pocket's category, which muddles both Analytics and the pocket balance.

Amir's idea: when creating a pocket, the user first picks a category
and the pocket inherits that category's icon and name; when recording
an expense, the user no longer picks a pocket — they must pick the
category, and the pocket follows from it.

## What is already true (checked 2026-11-26, no code changed)

- A pocket already has an optional category link:
  `PocketEntity.categoryId` (nullable FK to categories, `SET_NULL` on
  category delete) plus a denormalized `categoryTag` string, and its own
  `name` / `icon` / `color`, all edited independently in
  `PocketSettingsComponent` (`onCategoryChanged` today only sets
  `selectedCategory` — it does not inherit name/icon/color).
- Category → pocket routing already exists in Add Expense:
  `AddExpenseComponent.onCategorySelected` sets
  `selectedPocket = availablePockets.firstOrNull { it.categoryId == category.id }`
  (from the 2026-10-28 auto-pocket-assignment change). The pocket picker
  is still there as a manual override, and `onPocketCleared` can unlink it.
- An expense already carries both `categoryId` and `pocketId`
  (both nullable, `ExpenseEntity`).
- There is NO uniqueness on `PocketEntity.categoryId`: two pockets can
  share one category today, and `firstOrNull` then picks one arbitrarily.
- Room database version is 10 (`AppDatabase`); any uniqueness or
  non-null constraint needs a 10 → 11 migration.

So this change is mostly a tightening of a model that half-exists, not
a new routing build. The decision is product, not technical.

## Options

### Option A — Full 1:1 (Amir's proposal as stated)

- Pocket creation starts from category selection; the pocket inherits
  the category's name, icon and color (locked, or editable — decision
  needed). One pocket per category, enforced.
- Add Expense drops the pocket picker entirely; picking a category is
  mandatory and the pocket is derived from it.
- A pocket IS the budget for one category.

Costs, verified against the current code:

- Multi-category pockets die: e.g. a «سفر» pocket holding food,
  transport and hotel expenses is no longer possible.
- One pocket per category: two same-topic pockets (e.g. «سفر شمال» /
  «سفر جنوب») are no longer possible.
- Uncategorized expenses never touch a pocket, and SMS-parsed expenses
  only reach a pocket when their category is detected correctly.
- Existing data needs a migration decision: pockets with
  `categoryId = null`, pockets sharing a category, and expenses whose
  `categoryId` disagrees with their pocket's `categoryId`.
- Category deletion today sets the pocket's `categoryId` to NULL
  (FK `SET_NULL`) — under A that orphans the pocket, so deletion needs a
  new rule (block / force re-link / delete the pocket too).

### Option B — Middle path (recommended by Yavar in chat)

- Pocket creation starts from a category and inherits its name/icon/
  color as defaults, but they stay editable afterwards.
- Add Expense keeps auto-selecting the pocket from the category
  (already the behaviour) and the pocket picker stays, but only as an
  override; the common path is one tap (category) instead of two.
- Keeps multi-category pockets and same-topic duplicate pockets working.

### Option C — Keep the current model

- No change. The tag icon (2026-11-25) already disambiguates the badge,
  and auto-assignment already routes most expenses correctly.
- The duplicate-name confusion and the category/pocket mismatch stay
  possible.

## Decisions needed from Amir (before implementation)

1. A, B or C?
2. If A: are inherited name/icon/color locked, or editable after creation?
3. If A: what happens to existing pockets with no category, pockets
   sharing a category, and expenses whose category disagrees with their
   pocket? (Keep as-is / merge / unlink?)
4. If A: what happens when a linked category is deleted?
5. If A: are uncategorized expenses allowed at all, or must every
   expense have a category?

## Impact (Option A, for sizing)

- `feature/pocket/settings`: category-first creation flow; inherit
  name/icon/color on category pick; enforce one pocket per category
  (unique index on `categoryId`, Room migration 10 → 11).
- `feature/expense/add`: remove `PocketSelectorCard` / pocket-selection
  navigation from Add Expense; category becomes mandatory; pocket
  derived at save time.
- `feature/category`: deletion rule for categories linked to a pocket.
- `db`: migration 10 → 11 + data repair for the legacy cases above.
- `feature/sms`: SMS candidates without a detected category get no
  pocket (already effectively true via auto-assignment, but now explicit).
- Tests: component tests for inheritance, uniqueness, derivation and
  the legacy-data rules; E2E tests for the new creation and add-expense
  flows; screenshots for the new pocket-creation flow.
- Design gate: no `:design` screen exists for category-first pocket
  creation — build it in `:design` and get Amir's approval before
  implementing in the app, per the standing rule for big features.

## Impact (Option B, for sizing)

- `feature/pocket/settings`: prefill name/icon/color from the picked
  category (editable); keep everything else.
- `feature/expense/add`: no structural change — auto-assignment already
  exists; optionally hide the pocket card until the user taps to change it.
- No migration, no uniqueness enforcement, no data repair.
