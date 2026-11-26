# Design

> DRAFT — no `:design` screen exists yet for category-first pocket
> creation. Per the standing rule for big features, build the design in
> `:design` and get Amir's approval before implementing it in the app.

## Current flows (as built today)

- Pocket creation (`PocketSettingsComponent`, creation mode): name field
  first; icon and color picked freely; category picked optionally via
  the category-selection sub-screen (`onCategoryChanged` only sets
  `selectedCategory`); save writes `name` / `icon` / `color` /
  `categoryId` / `categoryTag` independently.
- Add Expense (`AddExpenseComponent`): category section, then pocket
  selector card. Picking a category auto-selects the first pocket whose
  `categoryId` matches (`availablePockets.firstOrNull { it.categoryId
  == category.id }`); the user can still open the pocket picker and
  choose any pocket, or unlink it.

## Option A — full 1:1

- Pocket creation becomes a category-first flow: step 1 pick a
  category (from the existing category-selection screen, or create a
  new category inline); step 2 shows the pocket prefilled with the
  category's name, icon and color, plus the budget/top-up fields.
  Whether name/icon/color stay editable after prefill is decision #2
  in the proposal.
- Add Expense loses the pocket selector card and the pocket-selection
  navigation entirely. The category section becomes mandatory (save is
  blocked without one); the derived pocket is shown read-only, e.g. as
  a line under the category («از جیب تفریح کم می‌شود»), or not shown
  at all — design decision.
- Pocket cards keep their current look; the category tag badge becomes
  redundant under A (badge == pocket identity) and should be removed
  from `PocketBudgetCard` once the model is 1:1.
- Category deletion needs a designed state: the linked pocket must be
  re-linked, kept unlinked, or deleted with the category — see
  proposal decision #4.

## Option B — middle path

- Pocket creation keeps its current layout; picking a category prefills
  name/icon/color (editable), nothing else changes.
- Add Expense keeps the pocket card, but it is auto-filled from the
  category (already true) — optionally collapsed to a single line until
  the user taps to change it.
- No `:design` work strictly required; the prefill is a behaviour
  change on existing screens.

## Notes for the designer

- Money figures use `moneyLabel` (display currency), as elsewhere.
- All new copy ships as string resources in English and Persian.
- Previews: category-first creation (prefilled), Add Expense with a
  derived pocket shown, Add Expense with no category yet (save blocked).
