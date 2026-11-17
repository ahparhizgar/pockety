# Proposal

## Why
Settings → Categories currently invokes a default empty callback, violating the existing Settings navigation requirement and leaving users without a way to browse categories independently of expense entry.

## What Changes
- Wire Settings Categories to a standalone browse-and-add category flow.
- Show existing category names, icons and colors, an appropriate empty state, and the existing add-category form; update the list and Settings count after creation.
- Preserve Settings on return and keep expense/pocket category selection unchanged.
- Require the production Categories navigation callback explicitly.
- Provide keyboard access, English/Persian resources, RTL and adaptive previews and regression coverage.
- Exclude editing, deletion, migrations, broad localization audit and redesign.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `category-management`: Standalone category browsing and creation from Settings, without selection side effects.

## Impact
Shared client Main/Settings navigation, root flow registration/rendering, category feature components, dependency injection, localized resources and feature tests. Reuses CategoryRepository and AddCategoryComponent; no backend, schema or dependency changes. Existing user-settings navigation requirement remains unchanged and is fulfilled by this implementation. Coordinate strings with POC-41 through linear rebasing.
