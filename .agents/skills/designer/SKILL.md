---
name: designer
description: Guide for designing UI in Compose Multiplatform. Use when touching :design module (Figma-like previews). Don't use when implemening UI in the app.
---

## 1. Module & Directory Conventions

- **Isolated Design Module**: Design modules (`:design`) must remain decoupled from production
  business logic, networking, and repositories.
- **Feature Subdirectories**: Group UI by feature under `features/<feature_name>/`:
    - `Screen.kt`: Assembles the full screen layout and top-level preview boards.
    - `Component.kt`: Houses feature-specific building blocks, fabs, cards.
- **Global Components**: Place reusable cross-feature components in `globalcomponents/` (e.g., top
  bars, custom chips, badges, inputs).
- **Theme & Tokens**: Keep typography, color palettes, spacing, and shapes in `ui/theme/`.

## 2. Componentization & Zero Copy-Paste

- **Always Extract & Re-use**: Never duplicate composable code across multiple screens. Extract
  shared cards, list items, pill badges, and input rows into shared components.
- **Single Source of Truth**: When 2+ screens need similar UI (e.g., transaction rows, category
  chips, balance cards), parameterize one reusable component instead of copying.
- **Component Signatures**:
    - Expose stateless parameters (`title`, `icon`, `onClick`, `modifier`).
    - Provide default for modifiers (`modifier: Modifier = Modifier`) at last parameter.
    - Pass slots (`content: @Composable () -> Unit`) for customizable inner areas.
- **Compose Semantics**:
    - Prefer `Surface` (with color/contentColor) or `Scaffold` over manual `Modifier.background`.
    - Use `IconButton` and `TextButton` instead of adding `.clickable` to `Icon` or `Text`.
    - Prefer tokenized dimensions (`MaterialTheme.colorScheme`, `MaterialTheme.typography`) over
      hardcoded values.

## 3. Interactive Figma-Like tool (`Catalog.kt`)

This tool is an interactive desktop workspace to preview, pan, and zoom across all app screens
simultaneously.

Always register the new screen/state in the `Catalog.kt` and keep it complete.  
Put the new screen/state in the appropriate feature group and provide a descriptive name.  
Create a new section if the feature is totally new.

## 4. Previews & Verification

- **Previews Rule**:
    - Annotate with `@ComponentPreviews` or `@ScreenPreviews` (do not use raw `@Preview` directly).
    - Provide multiple preview states: default, empty, loading, error, and dark mode.
- **Fast Quality Gate**:
    - Run JVM compilation check after changes:
      ```bash
      unset ANDROID_PREFS_ROOT; ./gradlew :design:compileKotlinJvm :design:ktfmtFormat --offline
      ```