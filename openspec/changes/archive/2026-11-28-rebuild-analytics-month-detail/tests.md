# Tests

- `AnalyticsTest` (component): previous-month outflow is computed for the selected
  month; category spending aggregates per category ordered by amount desc with
  percentages of the month total; uncategorized expenses form their own group;
  empty month yields an empty category list.
- Existing UI tests (`AnalyticsLocalizationE2eUiTest`, expense flow E2E tests)
  keep passing against the rebuilt screen via the preserved test tags.
- Screenshot previews cover the populated month (donut + ledger), the empty
  month (no donut card) and a past month (next pill enabled, trend pill visible).
