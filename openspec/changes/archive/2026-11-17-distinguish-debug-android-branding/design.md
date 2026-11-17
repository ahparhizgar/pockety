# Design

## Context

See proposal.md for motivation. At main e09240a, the manifest references `app_name`, `ic_launcher` and `ic_launcher_round`. Resources include English/Persian strings, five legacy densities, adaptive XML, monochrome artwork and both unqualified and `drawable-v24` foregrounds. No debug resources exist. Debug already uses `.debug` and the checked-in debug signing configuration. The existing project-name requirement needs the explicit exception captured in this change.

## Goals / Non-Goals

**Goals:** Resolve resource qualifier precedence and produce reproducible badge artwork without changing production assets.

**Non-Goals:** Runtime branding switches, notification icon changes, new UI or Gradle identity/signing changes.

## Decisions

- Use Android debug source-set overlays with the existing manifest resource names. Override English/default and Persian `app_name`. Runtime flags or manifest placeholders add unnecessary logic and risk localization divergence.
- Keep main resources untouched. Generate debug legacy, round, adaptive foreground and monochrome resources using the icon-resource skill, retaining an editable vector source and regeneration instructions. A color-only distinction would disappear in themed icons; the bug must be a recognizable shape.
- Resolve every applicable qualifier: main's `drawable-v24/ic_launcher_foreground.xml` can outrank an unqualified debug foreground. Override that qualifier too, or use debug-specific foreground names referenced by debug adaptive XML. Inspect merged and packaged resources to establish the result.
- Place the badge within the adaptive 66dp safe zone on the 108dp canvas; review circle, squircle and rounded-square masks and small legacy previews. Preserve Pockety's principal shapes. Treat monochrome artwork separately if a direct conversion obscures the badge.
- Verify final APK resource resolution in both locales and variants, rather than relying solely on source-file assertions. Preserve a baseline comparison for release icon assets and record application IDs/signing checks.

## Risks / Trade-offs

- Badge becomes illegible at small sizes → inspect density previews and simplify the bug silhouette as needed.
- Resource qualifier shadowing hides the badge → check merged/packaged foreground selection, including API 24/26+ paths.
- RTL ordering makes prefix confusing → inspect Persian launcher rendering as well as packaged text.
- Generic generator overwrites production icons → target only `src/debug/res` and compare release resources against baseline.

## Migration Plan

No data migration. Rebuild/install debug to update branding; rollback removes debug resource overlays and their source artwork. Release resources remain the baseline.
