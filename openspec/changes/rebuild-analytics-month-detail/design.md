# Design

Source of truth: `:design` module, `features/monthdetail` (Catalog item 05
"Month Detail (Expenses)"). The app screen re-implements that design with real
data, following the app's existing analytics plumbing:

- Month pills reuse the design's pill shape with the adjacent month names from
  the existing localized month-name tables (calendar type x app language).
- The design's month-picker affordance is rendered as a decorative chevron only;
  the picker itself is deferred (product owner, 2026-11-25).
- Money figures use the app's `moneyLabel` (display currency) rather than the
  design's split amount/currency texts, matching Home's overview cards.
- Donut arcs use each category's own color; the Uncategorized group uses the
  theme's surfaceDim, mirroring Home's breakdown.
- Percentages are whole-number shares of the selected month's outflow, rounded,
  computed in the component so the screen stays declarative.
