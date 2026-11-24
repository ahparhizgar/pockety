# Tasks

## 1. Design gate

- [x] 1.1 The Home design already exists in `:design` (Catalog 01/01b) and the
  product owner directed implementation from it on 2026-11-24, choosing a
  responsive-width grid for the pockets section

## 2. Component and data

- [ ] 2.1 Extend `HomeState` with the overview fields and UI models; extend
  `DefaultHomeComponent` to observe expenses, categories, calendar type and
  pockets and aggregate current/previous month figures, mirroring
  `AnalyticsComponent` — verify with the new `HomeComponentTest`
- [ ] 2.2 Extend the factory and DI (`DecomposeModule`) with the new
  repositories and the `onOpenAnalytics` / `onOpenPockets` callbacks; mirror
  everything in `FakeHomeComponent` / `FakeHomeComponentFactory`

## 3. Screen

- [ ] 3.1 Port `MonthlyOutflowHeroCard`, `SegmentedProgressBar`,
  `CategoryBreakdownRow`, `MonthOverMonthCard`, `ActivePocketCard` and
  `ActivePocketsSection` from `:design` into the app with string resources
  (EN + FA) and test tags; make the Home content scroll with a bottom spacer
- [ ] 3.2 Render the pockets as a responsive grid (columns from available
  width, 2 minimum) and hide the section when empty — verify with the new
  `HomeE2eUiTest`

## 4. Navigation wiring

- [ ] 4.1 Wire `MainComponent.createHomeChild`: date pill / All months →
  Analytics tab, Manage All → Pockets tab, pocket card → push
  `PocketDetails` — covered by the UI test callback assertions

## 5. Verification

- [ ] 5.1 Run the full verification gates via CI, push, pull, and run the
  screenshot-diff routine; the Home previews gain the new sections
