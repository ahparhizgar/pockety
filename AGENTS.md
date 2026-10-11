# UI - JetBrains Compose

use Surface (color, contentColor), or Scaffold instead of background modifier where possible.  
Use TextButton instead of `clickable` modifier on a Text most of the time.  
Use IconButton instead of `clickable` modifier on an Icon most of the time.  
Always write previews for screens or public composable components.  
Write multiple previews for different important states of the component/screen.  
Use string resources instead of hard-coded strings.  
Use one of `@ScreenPreviews` or `@ComponentPreviews`. Don't use `@Preview` directly.

## Large screens

Support large screens - read `adaptive` skill if needed.  
Use of Decompose panels and `CContext.windowSizeHolder`.  
Use `AdaptiveTopAppbar` if you want to use `LargeTopAppBar`.  
Use centered cards content on large screens.  
Use Grid/LazyVerticalGrid instead of list with adaptive columns to support large screens.

## Keyboard accessibility

Be keyboard-accessible. every functionality should be accessible via keyboard.  
Always set keyboardOptions and keyboardActions for text fields.

# Using Decompose

Don't make functions of Decompose components `suspend`. instead launch in them and return the
`Job`.  
It's a good practice to keep screen functions small by extracting private sections.  
Pass the whole decompose component to extracted private composables. don't split states and
callbacks in them.  
But for public components which there is no specific component, pass states and callbacks instead of
decompose component.  
Always use Value class for states.  
Use the concept of flows. flows are separate navigations from the main root navigation. e.g. when
you want to creat a group of users, you create a flow component for that because creating a group
involve multiple screens and components. So when there is a related set of components, create a flow
component for them. In this way, removing the whole flow form the root navigations is so easy.

# Using kotlin

Always use Clock.System.now () for time, don't use `System.currentTimeMillis()` or
`Instant.now()`.  
Don't Use `Instant.now()`, it's deprecated.  
If you need millis use `Clock.System.now().toEpochMilliseconds()`.  
Don't use fully qualified name. Write simple name, and let the compiler give error to you.  
Prefer enums over strings. Use enums in DTOs and DAOs and domain layer where possible.  
when using delay use the overload delay (`10.milliseconds`) not the one with Long parameter
(deprecated).

# Using KOIN

Define dependencies like this:
`factoryOf(::DefaultRootComponentFactory).bind<RootComponentFactory>()` So when the signature
changes, no need to change the binding.

# Testing

Write UI tests using keyboard actions so keyboard accessibility is verified.  
Use default value of fake factories if it doesn't matter.

## Testing UI

## Testing integration of Decompose components

## Unit testing

## Testing on backend

## Testing multi-user on backend

# How to structure classes into files

Put fake factories after the real classes in the same file.  
Don't create file such as Dtos.kt or Models.kt. Instead, create a file for each group of related
DTOs or model classes. For example, create GroupDto.kt for GroupDto class and Group.kt for Group
model class. Group.kt may contain GroupParticipants class etc. And must contain their fake
factories.  
In a single file, order like this: 1. the main class/interface, 2. extension functions (if any) 3.
related classes, 4. fake factories (in the same order as classes).  
In repository file, you can place enums that are used by the repository functions.

# Fake factories

Create meaningful factory functions.  
Almost always should be a general create ().  
Write createList () in fake factories if appropriate.  
Always use fake factories in tests and `Fake*Api`s. Never call constructor of data classes in tests.
Instead, use `Fake*Factory.create()` with minimal parameters. pass a parameter value only if it's
important to the test.  
Fake factories should use each other. so FakeGroupFactory should use FakeParticipantFactory to
create participants. Don't create participants in FakeGroupFactory.

# DTOs

Always use enums in DTOs where possible. Don't use strings for enums in DTOs. Use enums instead.  
Fully model data structure using sealed interfaces. we use kotlin serialization and shared module
for DTOs. So it creates a live documentation.

# Using Room

always write migrations for Room when schema changes.

# Using Exposed

Use the DSL not the DAO.  
Always use `timestamp("name")` instead of `long("name")` for timestamp columns.

# Using Git

Commit messages start with upper-case verb like: Fix Add Implement.

# Error Handling

Only catch ApiCallError. Never catch general `Exception` or `Throwable`.  
Always use `ApiCallError.userMessage` for user-facing error messages.

## Permanent style

```kotlin
try {
    _state.update { it.copy(isLoading = true, errorMessage = null) }
    val items = itemApi.getItems()
    _state.update { it.copy(items = items, isLoading = false) }
} catch (e: ApiCallError) {
    _state.update {
        it.copy(
            isLoading = false,
            errorMessage = e.userMessage,
        )
    }
}

```

## Snackbar style

no need to catch ApiCallError, it's caught automatically by `componentScope()`.

```kotlin
val scope = componentScope()
fun loadItems() = scope.launch {
    try {
        _state.update { it.copy(isLoading = true) }
        val items = itemApi.getItems()
        _state.update { it.copy(items = items) }
    } finally {
        _state.update { it.copy(isLoading = false,) }
    }
}
```

# Project "big picture" (what talks to what)

- Modules:
    - `server` — Ktor backend (main class `ApplicationKt`) configured in `server/build.gradle.kts`.
      Uses Exposed + Hikari + PostgreSQL (or H2 for tests).
    - `core` — shared core libraries used across targets (`core/src/commonMain`).
    - `app/shared` — Kotlin Multiplatform UI and client code used by `androidApp`, `desktopApp` and
      `webApp`. Targets: JVM, Android, iOS simulator, wasmJs. Common code under
      `app/shared/src/commonMain`.
    - `app/androidApp`, `app/desktopApp`, `app/webApp` — platform entry points.

# Verification

Run verification gate after finishing a task:

## Level 1: compilation

```bash
unset ANDROID_PREFS_ROOT; ./gradlew compileKotlinJvm compileKotlin --offline --continue 2>&1 | grep -E "e: |FAILED"
```

## Level 2: tests

Run often.

```bash
unset ANDROID_PREFS_ROOT; ./gradlew jvmTest test --offline --continue 2>&1 | grep -E "e: |FAILED"
```

## Level 3: Detekt:

`./gradlew :detektMain`

## Level 4: Ktfmt:

Run after all code changes are done.  
`./gradlew ktfmtFormat`

## Level 5: Screenshot testing

Run only if you touched UI.  
`./gradlew jvmTest -Proborazzi.test.compare=true`

GEMINI: use `run_shell_command` tool ./gradlew instead of `gradle_build` tool call. this prevents
blocking by an already running Gradle task in the IDE. So always use run_shell_command tool.
