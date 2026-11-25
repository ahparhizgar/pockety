# Tasks

## 1. Design gate

- [x] 1.1 The Month Detail design already exists in `:design` (Catalog 05) and the
  product owner directed implementation from it on 2026-11-25, with the month
  picker explicitly deferred

## 2. Component and data

- [x] 2.1 Extend `AnalyticsState` with `previousMonthOutflow` and a
  `categorySpending` list (category id/name/icon/color, amount, percentage),
  aggregated in `DefaultAnalyticsComponent.buildState()` from the selected
  month's expenses, with an Uncategorized group; update the fake factories
- [x] 2.2 Component tests: trend inputs (previous month total) and category
  aggregation (ordering, percentages, uncategorized) in `AnalyticsTest`

## 3. Screen

- [x] 3.1 Rebuild the month switcher card per the design: named month pills,
  title + subtitle, large total, trend pill (hidden when previous total is zero)
- [x] 3.2 Add the Category Spending card: donut chart with TOP SPENT center and
  allocation rows; hidden for an empty month
- [x] 3.3 Add the Expenses ledger header with the item count; restyle date group
  headers (uppercase) and ledger rows (flat, tinted rounded-square icon tile,
  dividers within a group)
- [x] 3.4 New string resources (EN + FA): cash-flow subtitle, trend text, category
  spending title, top spent label, expenses title, items count
- [x] 3.5 Update previews (populated with donut data, empty, past month)

## 4. Verification

- [ ] 4.1 Existing analytics UI/localization tests keep passing (test tags kept)
- [ ] 4.2 CI green; screenshot diff reviewed and sent
