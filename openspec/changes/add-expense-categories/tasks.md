# Tasks

## 1. Database Schema & Category Repository

- [x] 1.1 Implement `CategoryEntity`, `CategoryDao`, and `CategoryRepository` (with fake factory and tests in `CategoryRepositoryTest.kt`)
- [x] 1.2 Update `ExpenseEntity` to include nullable `categoryId` and foreign key reference
- [x] 1.3 Implement Room migration `MIGRATION_4_5`, bump database version to 5, and verify migration test passes

## 2. Category Creation Component & Screen

- [x] 2.1 Implement `AddCategoryComponent` and `FakeAddCategoryComponent` in `app/shared` with validation and tests in `AddCategoryTest.kt`
- [x] 2.2 Implement `AddCategoryScreen` in `app/shared` matching `:design/features/addcategory` with string resources and previews
- [x] 2.3 Verify `AddCategoryE2eUiTest.kt` for keyboard accessibility and state updates

## 3. Category Selection Component & Screen

- [x] 3.1 Implement `CategorySelectionComponent` and `FakeCategorySelectionComponent` supporting search and uncategorized selection, with tests in `CategorySelectionTest.kt`
- [x] 3.2 Implement `CategorySelectionScreen` (and modal sheet content) in `app/shared` matching `:design/features/selectcategory` with previews
- [x] 3.3 Verify `CategorySelectionE2eUiTest.kt` for search filtering and keyboard accessibility

## 4. Add Expense Flow & Screen Integration

- [x] 4.1 Implement `AddExpenseFlowComponent` wrapping `AddExpenseComponent`, `CategorySelectionComponent`, and `AddCategoryComponent`
- [x] 4.2 Update `AddExpenseComponent` and `AddExpenseScreen` with the Category section card and "Change" action from `:design/features/editexpense`
- [x] 4.3 Update `RootComponent` to route `AddExpense` requests through `AddExpenseFlowComponent`
- [x] 4.4 Implement `AddExpenseFlowE2eTest.kt` verifying the full navigation and expense saving journey

## 5. Verification Gate

- [x] 5.1 Run compilation gate: `./gradlew compileKotlinJvm compileKotlin --offline`
- [x] 5.2 Run test suite: `./gradlew jvmTest test --offline`
- [x] 5.3 Run static analysis: `./gradlew :detektMain` and code formatting `./gradlew ktfmtFormat`
