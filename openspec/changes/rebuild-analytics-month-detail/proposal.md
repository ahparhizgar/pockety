# Proposal

## Why

The Analytics tab's month view predates the approved design. The `:design` module
(Catalog item 05 "Month Detail (Expenses)", Analytics Tab) specifies a richer
month-detail layout: a month switcher card with named previous/next month pills
and a trend pill against the previous month, a category spending donut with
per-category allocation rows, an expenses ledger header with the item count, and
flat ledger rows grouped by date. The app has none of the trend or category
breakdown data, and its ledger rows are heavy cards that do not match the design.

## What Changes

- The month switcher card follows the design: previous/next **pills labelled with
  the adjacent month names**, the selected month title with a "Monthly Cash Flow"
  subtitle, the month total in large type, and a **trend pill** comparing the
  selected month's outflow with the previous month's (percentage plus the previous
  total; hidden when the previous month's total is zero). The month title keeps a
  decorative expand chevron; a month picker is explicitly out of scope for now
  (product owner decision, 2026-11-25).
- A **Category Spending** card is added: a donut chart of the selected month's
  per-category shares with a "TOP SPENT" center label (top category's percentage
  and name), and one allocation row per category group (color dot, name,
  percentage pill, amount). Uncategorized expenses form their own group. The card
  is hidden when the month has no expenses.
- An **Expenses ledger header** shows the section title and the total item count
  for the selected month.
- Ledger rows follow the design: flat rows (no card background) with the category
  icon on a rounded-square tile tinted with the category color, the category name
  as the title, the description as the subtitle, the amount right-aligned, and a
  thin divider between rows of the same date group. Date group headers are
  uppercase with the group total.
- `AnalyticsComponent` state gains the previous month's outflow and the
  per-category spending aggregation for the selected month, computed from the
  same expense/category flows, honoring the user's calendar type.
- All new copy ships as string resources in English and Persian; figures use the
  user's display currency like the rest of the app.

## Impact

- `feature/analytics`: `AnalyticsComponent` (state + aggregation + fakes) and a
  rebuilt `AnalyticsScreen`; previews updated for populated/empty/past-month.
- New string resources (EN + FA) for the cash-flow subtitle, trend text, donut
  section and ledger header.
- Tests: component tests for the trend and category aggregation; the existing
  analytics UI/localization tests keep their test tags and keep passing.
