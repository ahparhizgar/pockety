# Tasks

## 1. Database Schema & Category Repository

- [ ] 1.1 Implement `CategoryEntity`, `CategoryDao`, and `CategoryRepository` (with fake factory and tests in `CategoryRepositoryTest.kt`)
- [ ] 1.2 Update `ExpenseEntity` to include nullable `categoryId` and foreign key reference
- [ ] 1.3 Implement Room migration `MIGRATION_4_5`, bump database version to 5, and verify migration test passes

## 2. Category Creation Component & Screen

- [ ] 2.1 Implement `AddCategoryComponent` and `FakeAddCategoryComponent` in `app/shared` with validation and tests in `AddCategoryTest.kt`
- [ ] 2.2 Implement `AddCategoryScreen` in `app/shared` matching `:design/features/addcategory` with string resources and previews
- [ ] 2.3 Verify `AddCategoryE2eUiTest.kt` for keyboard accessibility and state updates

## 3. Category Selection Component & Screen

- [ ] 3.1 Implement `CategorySelectionComponent` and `FakeCategorySelectionComponent` supporting search and uncategorized selection, with tests in `CategorySelectionTest.kt`
- [ ] 3.2 Implement `CategorySelectionScreen` (and modal sheet content) in `app/shared` matching `:design/features/selectcategory` with previews
- [ ] 3.3 Verify `CategorySelectionE2eUiTest.kt` for search filtering and keyboard accessibility

## 4. Add Expense Flow & Screen Integration

- [ ] 4.1 Implement `AddExpenseFlowComponent` wrapping `AddExpenseComponent`, `CategorySelectionComponent`, and `AddCategoryComponent`
- [ ] 4.2 Update `AddExpenseComponent` and `AddExpenseScreen` with the Category section card and "Change" action from `:design/features/editexpense`
- [ ] 4.3 Update `RootComponent` to route `AddExpense` requests through `AddExpenseFlowComponent`
- [ ] 4.4 Implement `AddExpenseFlowE2eTest.kt` verifying the full navigation and expense saving journey

## 5. Verification Gate

- [ ] 5.1 Run compilation gate: `./gradlew compileKotlinJvm compileKotlin --offline`
- [ ] 5.2 Run test suite: `./gradlew jvmTest test --offline`
- [ ] 5.3 Run static analysis: `./gradlew :detektMain` and code formatting `./gradlew ktfmtFormat`
