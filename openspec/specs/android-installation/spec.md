# android-installation Specification

## Purpose
Defines Android application identities so development and production installations can coexist on the same device.

## Requirements

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

### Requirement: Debug launcher artwork
Android debug launcher icons SHALL retain recognizable Pockety artwork and add a clearly visible bug badge in legacy density, round, adaptive and monochrome/themed representations wherever supported. The badge SHALL remain recognizable inside common launcher masks and safe zones.

#### Scenario: Legacy launcher icons
- **WHEN** a launcher displays a debug legacy square or round icon at any supported density
- **THEN** recognizable Pockety artwork includes a visible bug badge

#### Scenario: Adaptive launcher masks
- **WHEN** a launcher displays a debug adaptive icon using a circle, squircle or rounded-square mask
- **THEN** the bug badge and Pockety identity remain recognizable without badge clipping

#### Scenario: Themed launcher icons
- **WHEN** a supported launcher enables monochrome/themed icons for the debug application
- **THEN** the themed icon includes a recognizable bug badge and Pockety identity

### Requirement: Production branding isolation
Release Android builds SHALL retain their existing launcher artwork and localized labels. Debug branding SHALL preserve existing variant application identities and signing behavior and SHALL NOT alter desktop or web branding.

#### Scenario: Release packaging after debug branding
- **WHEN** a release Android artifact is built after debug branding is added
- **THEN** its launcher artwork and localized labels match the production baseline
- **AND** debug and release application identities and signing behavior remain unchanged

#### Scenario: Other platform branding
- **WHEN** desktop or web applications are built
- **THEN** their branding remains unchanged
