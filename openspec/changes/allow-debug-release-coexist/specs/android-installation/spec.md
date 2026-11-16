# Spec Delta

## Purpose

Defines Android application identities so development and production installations can coexist on the same device.

## ADDED Requirements

### Requirement: Variant application identities
Android debug builds SHALL use application ID `ir.ahparhizgar.pockety.debug`, and release builds SHALL retain application ID `ir.ahparhizgar.pockety`.

#### Scenario: Debug APK identity
- **WHEN** a debug APK is built
- **THEN** its packaged application ID is `ir.ahparhizgar.pockety.debug`

#### Scenario: Release APK identity
- **WHEN** a release APK is built
- **THEN** its packaged application ID is `ir.ahparhizgar.pockety`

### Requirement: Simultaneous installation
A user SHALL be able to install debug and release APKs together on the same supported Android device and launch either application independently.

#### Scenario: Debug installed alongside release
- **GIVEN** the release application is installed
- **WHEN** the debug APK is installed
- **THEN** installation succeeds and both applications remain independently launchable

#### Scenario: Release installed alongside debug
- **GIVEN** the debug application is installed
- **WHEN** the release APK is installed
- **THEN** installation succeeds and both applications remain independently launchable
