# Tasks

## 1. Data Layer & Shared Resources

- [x] 1.1 Add `topUpPocket(id: Long, amount: Long)` query and observation support to `PocketDao` and `PocketRepository` (`DefaultPocketRepository`, `FakePocketRepository`); verify with `PocketRepositoryTest`
- [x] 1.2 Add string resources in `values/strings.xml` for pocket details screen title, balance label, action buttons ("Top-up", "Configure"), quick top-up bottom sheet labels, preset chips, and confirmation messages

## 2. Component & Navigation

- [x] 2.1 Implement `PocketDetailsComponent`, `DefaultPocketDetailsComponent`, and `FakePocketDetailsComponent` with state holding pocket identity, top-up sheet visibility, custom amount input, confirmation banner, and mock transactions in `PocketDetailsComponent.kt`; verify with `PocketDetailsTest`
- [x] 2.2 Register `PocketDetailsComponentFactory` in `DecomposeModule.kt` using Koin `factoryOf(::DefaultPocketDetailsComponentFactory).bind<PocketDetailsComponentFactory>()`
- [x] 2.3 Wire `PocketDetailsComponent.Config` into `RootComponent`, update `MainComponent.kt` to navigate to `PocketDetailsComponent` when `onPocketClick` is invoked, and add assertion helpers; verify with `PocketDetailsFlowE2eTest`

## 3. UI Layer & Accessibility

- [x] 3.1 Implement `PocketDetailsScreen` with `PocketDetailsHeaderSection`, `PocketDetailsActionBar`, and `PocketTransactionRow` supporting adaptive centered layouts (`widthIn(max = 640.dp)`); verify with `@ScreenPreviews` and `@ComponentPreviews` via `PreviewScreenshotTest`
- [x] 3.2 Implement `QuickTopUpBottomSheet` modal bottom sheet with preset quick-amount chips (+50,000 T, +100,000 T, +200,000 T, +500,000 T), keyboard-accessible custom amount text field, and confirm button; verify with `@ComponentPreviews` via `PreviewScreenshotTest`
- [x] 3.3 Implement `PocketDetailsE2eUiTest` verifying keyboard accessibility and interactive top-up behaviors

## 4. Verification & Quality Gates

- [x] 4.1 Run verification gate (compilation, unit/E2E tests, kover coverage, detekt, and ktfmt) to ensure zero regressions
