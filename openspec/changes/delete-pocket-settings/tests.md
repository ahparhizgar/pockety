# Tests Specification

## Unit Tests (single component / class / function)

### `pocket_management/PocketSettingsTest`

**Target**: `PocketSettingsComponent` and `PocketRepository`

**Scenarios**:

- Given pocket settings component initialized for an existing pocket (`pocketId = 1`)
    - When `onDeleteClicked()` is invoked
        - [ ] Then pocket repository deletes pocket with id 1
        - [ ] Then `onDeleted` callback is invoked

- Given `PocketRepository` with an existing pocket having linked transactions and top-ups
    - When `deletePocket(id)` is executed
        - [ ] Then pocket entity is deleted
        - [ ] Then associated transactions have their `pocketId` set to `null`
        - [ ] Then associated pocket top-up entries are deleted

## E2E Flow Tests

### `pocket_management/PocketSettingsFlowE2eTest`

**Flow**: Navigation and flow completion upon pocket deletion

**Scenarios**:

- Given pocket settings flow opened in edit mode for an existing pocket
    - When delete action is triggered on the settings component
        - [ ] Then the flow completes and invokes `onCompleted` callback

## UI & Accessibility Tests

### `pocket_management.PocketSettingsE2eUiTest`

**Scenarios**:

1. Given pocket settings opened in creation mode (`pocketId == null`), verify that the "Danger Zone" card and "Delete Pocket" button are not displayed.
2. Given pocket settings opened in edit mode (`pocketId != null`), verify that the "Danger Zone" card is displayed with the warning text and the "Delete Pocket" button.
3. Given pocket settings opened in edit mode, focus and click "Delete Pocket" button via keyboard / UI actions, verifying the delete callback is triggered.
