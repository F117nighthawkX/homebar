# Reusable Codex Bucket Implementation Prompt

You are working in the Home Bar Android app repository.

Read first:

- `AGENTS.md`
- `docs/agent/homebar-project-guidance.md`
- `[BUCKET_FILE]`

Then inspect any source files, tests, Gradle files, navigation files, models, repositories, ViewModels, or screens needed for the selected Epic.

## Target

Bucket file:

```text
[BUCKET_FILE]
```

Epic to implement:

```text
[TARGET_EPIC]
```

If `[TARGET_EPIC]` is `next`, choose the first Epic from the bucket that is not already implemented.

## Before Coding

Follow the planning rules in `AGENTS.md`. Analyze the bucket as a whole before editing code.

Include:

1. The intended outcome in your own words.
2. The bucket's purpose.
3. The Epics in the bucket.
4. The selected Epic.
5. Relevant earlier bucket decisions or source files.
6. Files likely to change.
7. Nearby areas you plan to leave alone.
8. Risks, ambiguities, or assumptions.
9. Tests and device or emulator functional UI verification you will run.

## Implementation Rules

Follow:

- `AGENTS.md` for agent behavior, planning, implementation discipline, simplification, and verification.
- `docs/agent/homebar-project-guidance.md` for Home Bar product scope, stack choices, data ownership, Android checks, and device or emulator functional UI verification.

Implement only the selected Epic. Do not continue to the next Epic.

If the current codebase lacks support required by this Epic, add the smallest reasonable support needed. Do not use that as a reason to build other Epics early.

Keep domain logic testable outside the UI layer.

## Required Checks

After implementation, run the relevant checks from `AGENTS.md` and `docs/agent/homebar-project-guidance.md`, including:

- Unit tests
- Debug build
- `git diff --check`
- Connected Android tests, only if relevant connected tests exist
- Device or emulator functional UI verification for the implemented Epic

On Windows, use `./gradlew` equivalents from the Home Bar guidance file.

Do not treat launching the app as sufficient verification unless the Epic only changed startup behavior. Exercise the implemented behavior through one concrete path on the connected Pixel or an emulator. Use connected UI tests when available. Otherwise use adb-assisted verification with `adb shell input`, UI Automator dumps, screenshots, and logcat as described in `docs/agent/homebar-project-guidance.md`.

Do not claim a manual UI behavior was verified unless the changed behavior was actually exercised on a device/emulator or through an automated UI test.

## Completion Report

When done, inspect the current diff and report using the Epic completion report format in `docs/agent/homebar-project-guidance.md`. Use the `$commit-report` skill if available and appropriate.

The report must include:

- Suggested commit message
- Bucket file used
- Epic implemented
- Files created
- Files changed
- Implemented changes
- Simplification pass result
- Checks run
- Device or emulator functional verification result
- Manual Android Studio steps
- Assumptions
- Follow-up items

Use file names only, not absolute paths.

Do not run `git commit` unless explicitly asked.

End with exactly:

```text
Checkpoint: Epic complete. Please review before I continue.
```
