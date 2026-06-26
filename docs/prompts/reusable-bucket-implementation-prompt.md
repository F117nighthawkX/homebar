# Reusable Codex Bucket Implementation Prompt

You are working in the Home Bar Android app repository.

Read first:

- `AGENTS.md`
- `[BUCKET_FILE]`

Then inspect any source files, tests, Gradle files, navigation files, models,
repositories, ViewModels, or screens needed for the selected Epic.

## Target

Bucket file:

```text
[BUCKET_FILE]
```

Epic to implement:

```text
[TARGET_EPIC]
```

If `[TARGET_EPIC]` is `next`, choose the first Epic from the bucket that is not
already implemented.

## Before Coding

Analyze the bucket as a whole first.

Include:

1. The bucket's purpose.
2. The Epics in the bucket.
3. The selected Epic.
4. Relevant earlier bucket decisions or source files.
5. Files likely to change.
6. Risks or ambiguities.
7. Tests and Pixel smoke test you will run.

## Implementation Rules

Follow all repo rules in `AGENTS.md`.

Implement only the selected Epic. Do not continue to the next Epic.

Do not add:

- Account creation
- Email login
- Cloud backup
- Cloud sync
- Firebase
- Shopping list behavior
- Direct SMS sending
- Contact lookup
- AI features
- Barcode scanning
- Cost tracking
- Unnecessary Android permissions

Use Room for structured app data.

Use DataStore Preferences only for small settings.

Keep domain logic testable outside the UI layer.

If the current codebase lacks support required by this Epic, add the smallest
reasonable support needed. Do not use that as a reason to build other Epics
early.

## Required Checks

After implementation, run the relevant checks from `AGENTS.md`, including:

- unit tests
- debug build
- `git diff --check`
- connected Android tests, only if relevant connected tests exist
- Pixel physical-device smoke test, when adb can see the connected device

On Windows, use `.\gradlew.bat` equivalents.

Do not claim a manual UI behavior was verified unless it was actually checked
on the device or through an automated test.

## Completion Report

When done, inspect the current diff and report using the commit-style format
defined in `AGENTS.md`.

The report must include:

- suggested commit message
- bucket file used
- Epic implemented
- files created
- files changed
- implemented changes
- checks run
- Pixel smoke test result
- manual Android Studio steps
- assumptions
- follow-up items

Use file names only, not absolute paths.

Do not run `git commit` unless explicitly asked.

End with exactly:

```text
Checkpoint: Epic complete. Please review before I continue.
```
