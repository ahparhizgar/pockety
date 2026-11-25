# Proposal

## Why

The Home tab currently shows only the SMS-permission / undecided-expenses banner.
The approved design in the `:design` module (Catalog item 01 "Home") specifies a
complete overview: a monthly outflow hero card with a per-category breakdown, a
month-over-month comparison card, and an active pockets section. None of the three
exists in the app, so the first screen users see carries almost no information.

## What Changes

- Home gains a **Monthly Outflow hero card**: the current month's total outflow
  (in the user's display currency), a date pill with the current month label that
  opens the Analytics tab, a segmented progress bar, and a category breakdown
  (top 4 categories by amount plus an aggregated "Other" row) with each
  category's share and amount.
- Home gains a **Month-over-Month card**: comparison against the previous month
  with a saved/more percentage badge, the previous month's total, and an
  "All months" action that opens the Analytics tab. The badge is hidden when the
  previous month's total is zero (no meaningful comparison).
- Home gains an **Active Pockets section**: every pocket as a card (icon, name,
  remaining balance) in a responsive grid whose column count adapts to the
  available width; a "Manage All" action opens the Pockets tab and tapping a
  pocket opens its details. The section is hidden when there are no pockets.
- All figures follow the user's calendar setting (Gregorian/Persian) and display
  currency, aggregated from the same expense/category flows the Analytics tab
  uses. Category colors are the categories' own colors, as in Analytics.
- The Home content becomes scrollable, keeping the existing banner on top and
  the add-expense FAB.

## Capabilities

- New capability `home-overview`: the Home tab overview sections (monthly
  outflow, category breakdown, month-over-month comparison, active pockets)
  and their navigation.
