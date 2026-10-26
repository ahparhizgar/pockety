# Tasks

## 1. AddExpense Component Auto-Assignment & State

- [ ] 1.1 Inject `PocketRepository` into `DefaultAddExpenseComponent`, add `selectedPocket: PocketItemModel? = null` to `AddExpenseState`, and implement auto-assignment in `onCategorySelected` when a pocket matches `pocket.categoryId == category.id`. Verify with unit tests in `AddExpenseTest`.
- [ ] 1.2 Implement `onPocketSelected`, `onPocketCleared`, and `onSelectPocketClicked` in `AddExpenseComponent`, and pass `pocketId = currentState.selectedPocket?.id` to `expenseRepository.addExpense` in `onSaveClicked`. Verify with unit tests in `AddExpenseTest`.

## 2. Pocket Selection Component & Screen

- [ ] 2.1 Create `SelectPocketComponent` interface, state, default implementation, and fake factory supporting query search and "No Pocket" unlinking. Verify with unit tests in `SelectPocketTest`.
- [ ] 2.2 Create `SelectPocketScreen` Composable in `app/shared` matching `:design/features/selectpocket/Screen.kt` with search field, pocket option items, and empty state. Verify with component and screen previews.

## 3. UI Integration & Flow Navigation

- [ ] 3.1 Implement `PocketSelectorCard` in `app/shared` matching `:design/features/editexpense/Component.kt` and integrate it into `AddExpenseScreen` with linked and unlinked states and unlink action. Verify with previews and `AddViewExpenseE2eUiTest`.
- [ ] 3.2 Update `AddExpenseFlowComponent` to support `AddExpenseFlowChildConfig.PocketSelection`, navigate on pocket card tap, and return selected pocket to `AddExpenseComponent`. Verify with `AddExpenseFlowE2eTest`.

## 4. Verification Gate

- [ ] 4.1 Run Level 1-4 verification gates (`compileKotlinJvm`, `jvmTest`, `koverVerify`, `detektMain`, `ktfmtFormat`) and confirm all checks pass.
