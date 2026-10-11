# Design

## Context
See proposal.md for motivation. We are tailoring a base KMP project to a specific app named "Pockety". We need to aggressively trim the iOS and Web targets to focus on the Android setup in the short term, without permanently breaking the iOS source code.

## Goals / Non-Goals

**Goals:**
- Rename the project from the generic base to "Pockety" (`جیبک`).
- Remove the `webApp` target and related source sets completely.
- Disable iOS targets in Gradle temporarily by commenting them out to speed up the development process.
- Ensure the backend (Ktor) is retained and remains functional.

**Non-Goals:**
- Completely deleting iOS source code. We just want to comment it out from the build setup.
- Touching backend code beyond necessary renames.

## Decisions

**Decision 1: How to disable iOS temporarily**
- *Approach:* Comment out the iOS target blocks (like `iosX64()`, `iosArm64()`, `iosSimulatorArm64()`) and their source set dependencies in `app/shared/build.gradle.kts` and settings.
- *Rationale:* Keeps the code around for later reactivation without slowing down current Gradle syncs and builds.

**Decision 2: How to handle the Web target**
- *Approach:* Remove `app/webApp` folder, remove from `settings.gradle.kts`. Also remove Web-specific source sets (e.g. `wasmJsMain`) in `app/shared/build.gradle.kts`.
- *Rationale:* The user explicitly stated to "remove web target completely", meaning we don't plan to use it.

## Risks / Trade-offs
- **Risk:** Commenting out iOS might cause IDE red squiggles in iOS specific code.
- *Mitigation:* We will ignore iOS source errors for now since the Android and Desktop targets will compile fine.
