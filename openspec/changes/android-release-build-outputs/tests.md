# Tests Specification

## Workflow Verification

### Release Android workflow

- Given a manual `workflow_dispatch` run with the required release signing secrets configured
    - When the workflow completes
        - [ ] Then the debug Gradle task succeeds and a `pockety-debug-apk` artifact contains the debug APK
        - [ ] Then the release Gradle task succeeds and a `pockety-release-apk` artifact contains the release APK
        - [ ] Then an absent APK causes its upload step to fail because `if-no-files-found` is set to `error`
        - [ ] Then the temporary release keystore is removed even if a build step fails

- Given a workflow run without the release signing secrets
    - When the workflow reaches the release build
        - [ ] Then the release build fails and the temporary keystore cleanup still runs
