# Design

## Context

The `:design` module already contains the approved Home design
(`design/features/home/Screen.kt` + `Component.kt`, Catalog items 01/01b):
`MonthlyOutflowHeroCard` (with `SegmentedProgressBar` and `CategoryBreakdownRow`),
`MonthOverMonthCard`, and `ActivePocketsSection` (with `ActivePocketCard`). The
app's `HomeScreen` renders only the banner; `HomeComponent` observes the SMS
permission and pending candidates only. `AnalyticsComponent` already solves the
same aggregation problem (expenses + categories + calendar type, month
filtering via `com.base.calendar.Calendar`), and `PocketRepository.observePockets()`
already emits `PocketItemModel` with remaining balances.

## Decisions

### Aggregation in the component, mirroring Analytics

- `DefaultHomeComponent` additionally observes
  `ExpenseRepository.observeExpenses()`, `CategoryRepository.observeCategories()`,
  `UserSettingsRepository.calendarType` and `PocketRepository.observePockets()`,
  combined with the existing permission/candidate flows.
- The active calendar follows the calendar-type setting exactly as in
  `AnalyticsComponent` (Gregorian/Persian implementations, system timezone).
  "Current month" is the calendar month containing now; "previous month" is
  `addMonths(current, -1)`.
- Category breakdown: group current-month expenses by `categoryId`, sum amounts,
  sort descending, take the top 4; the remainder (including any further
  categories) aggregates into one "Other" row. Uncategorized expenses
  (`categoryId == null`) form their own group with the localized Uncategorized
  name. Each row carries `share = amount / total` (0 when total is 0) and the
  category's own color (null for Uncategorized/Other, rendered as surfaceDim,
  matching the design's "Other" segment color).
- Month-over-month: `percent = round(|current - previous| * 100 / previous)`;
  `isSaved = current <= previous`. When `previous == 0` the state carries no
  percentage and the card renders without the badge.

### State and UI models

`HomeState` gains: `calendarType`, `currentYear`, `currentMonth`,
`previousYear`, `previousMonth`, `monthlyOutflow: Long`,
`previousMonthOutflow: Long`, `monthOverMonthPercent: Int?`,
`isMonthOverMonthSaved: Boolean`, `topCategories: List<HomeCategoryOutflow>`,
`pockets: List<HomePocketUi>`. UI models live next to the component:
`HomeCategoryOutflow(name, amount, share, color: Long?)` and
`HomePocketUi(id, name, balance, icon: String)`. The screen formats amounts with
the existing currency helpers (`moneyLabel` / `currencySymbol()`), month labels
with `com.base.calendar.getMonthName`, and maps pocket icon strings with the
same mapper the Pockets screen uses.

### Screen composition

`HomeContent` becomes a vertically scrolling column (max width 640dp as today):
banner, hero card, month-over-month card, pockets section, and a bottom spacer
so the FAB never covers content — the section order and visuals follow the
`:design` composables, ported with string resources and test tags.

### Responsive pockets grid

Per the product owner's decision, the pockets grid is responsive: the column
count derives from the available width (one column per ~176dp, at least 2,
at most 6), computed with `BoxWithConstraints` and rendered as chunked rows of
equal-weight cards (the Home column scrolls, so a lazy grid cannot be nested).
All pockets are shown; the section is hidden when the list is empty.

### Navigation

- `HomeComponentFactory.create` gains `onOpenAnalytics: () -> Unit = {}` and
  `onOpenPockets: () -> Unit = {}`. `MainComponent.createHomeChild` wires them
  to `mainNavigation.replaceAll(Home, Analytics)` / `replaceAll(Home, Pockets)`,
  mirroring the existing `onOpenInbox` wiring.
- Date pill and "All months" call `onAllMonthsClicked()` → `onOpenAnalytics`.
  "Manage All" calls `onManagePocketsClicked()` → `onOpenPockets`.
- A pocket card calls `onPocketClicked(id)`, which pushes
  `RootFlowDestination.PocketDetails(pocketId)` through the component's own
  navigation, mirroring `onAddExpenseClicked`.
