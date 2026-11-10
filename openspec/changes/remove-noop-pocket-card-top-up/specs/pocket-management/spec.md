# Spec Delta

## MODIFIED Requirements

### Requirement: Pocket Action Handlers
The system SHALL handle user interactions on pocket cards and list controls gracefully, delegating card taps to pocket detail navigation and avoiding non-functional card-level actions.

#### Scenario: User clicks pocket card
- **WHEN** user taps a pocket budget card
- **THEN** the interaction is handled without crashing (delegating to pocket detail navigation handler)

#### Scenario: User taps Add Pocket action
- **WHEN** user taps the Add Pocket floating action button
- **THEN** the interaction is handled without error as an initial placeholder
