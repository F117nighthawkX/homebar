# AGENTS.md

## Project

This repository contains a native Android app for a home bar cocktail app.

The app's main purpose is to answer:

> What drinks can I make right now with my current home bar inventory?

The app is local-first. Do not add account creation, email login,
cloud backup, cloud sync, or Firebase unless a future task explicitly
asks for that decision to be revisited.

## Product Scope

Current planned buckets:

- Bucket 0: Android Project Setup and Stack Decisions
- Bucket 1: Inventory, Recipes, and Makeability Logic
- Bucket 2: Recipe List and Recipe Detail Screens
- Bucket 3: Inventory Management Screens
- Bucket 4: Recipe Editor and Custom Recipes
- Bucket 5: Recipe Sharing by Text Message
- Bucket 6: App Settings and Preferences

Do not add a shopping list. Inventory can show missing and running-low
ingredients, including missing and running-low ingredients for favorited
recipes, but the app should not manage purchases.

## Stack

Use:

- Kotlin
- Native Android
- Jetpack Compose
- Material 3
- Single-activity architecture
- ViewModels
- Repository layer
- Room
- DataStore Preferences
- Kotlin coroutines
- Flow / StateFlow
- Navigation Compose
- JUnit tests

Use this package name:

```text
dev.nighthawklabs.homebar
```

Use this working app name:

```text
Home Bar
```

## Data Ownership

Use Room for structured app data:

- Recipes
- Ingredients
- Inventory state
- Substitute groups
- Custom recipe data

Use DataStore Preferences only for small settings:

- Default recipe filter
- Default inventory filter
- Measurement display preference
- Theme preference
- Recipe sharing preferences

Do not store recipes, ingredients, inventory, or substitute data in
DataStore.

## Core Product Decisions

- Serving multiplier changes displayed ingredient quantities only.
- Serving multiplier does not change the saved base recipe.
- Running low ingredients still count as in stock.
- Substitutes count as valid ingredients for recipe matching.
- Substitutes are two-way by default.
- Substitution details should appear inline in recipe detail ingredient lines.
- Recipe list should not separate substitute-based recipes into a different
  category.
- Recipe match status should be calculated from current recipe, inventory,
  and substitute data.
- Recipe match status should not be stored as stale derived data.
- Garnishes do not block makeability in the first version.
- Tools and glassware do not block makeability.
- Tools and glassware are not consumable inventory.
- Bucket 2 default recipe filter is `Makeable now`.
- Bucket 3 inventory filters include missing and running-low ingredients for
  favorited recipes.
- Recipe sharing uses the Android share sheet.
- Recipe sharing should not send SMS directly.
- Recipe sharing should not request SMS or contacts permissions.
- Account creation and cloud backup are out of scope for the current app plan.

## Development Rules

- Keep changes focused on the current bucket and Epic.
- Do not build future buckets early.
- Do not add dependencies unless they are needed for the current bucket.
- Do not add Android permissions unless the current bucket requires them.
- Keep domain logic separate from UI code.
- Make recipe matching, serving scaling, substitute matching, and share
  formatting testable with plain unit tests.
- Prefer simple, readable Kotlin over clever abstractions.
- Use clear names for models, state, and actions.
- Avoid large rewrites unless the current bucket requires them.
- Do not create branches, commit, push, or run destructive git commands unless
  explicitly asked.
- Run relevant tests before reporting completion.
- Report files created, files changed, commands run, build status, test status,
  and any manual Android Studio steps.

## Documentation Routing

Product docs should live under:

```text
docs/product/
```

Expected files:

```text
docs/product/bucket-0-android-project-setup.md
docs/product/bucket-1-inventory-recipes-makeability.md
docs/product/bucket-2-recipe-list-detail-screens.md
docs/product/bucket-3-inventory-management-screens.md
docs/product/bucket-4-recipe-editor-custom-recipes.md
docs/product/bucket-5-recipe-sharing-by-text-message.md
docs/product/bucket-6-app-settings-and-preferences.md
```

When working on a bucket:

1. Read this `AGENTS.md`.
2. Read the requested bucket file.
3. Read earlier bucket files only as needed for locked decisions and
   dependencies.
4. Do not implement later bucket behavior unless the current task says to.

## Reusable Prompts

Reusable task prompts should live under:

```text
docs/prompts/
```

Expected prompt files:

```text
docs/prompts/initial-codex-prompt.md
docs/prompts/reusable-bucket-implementation-prompt.md
docs/prompts/commit-report-prompt.md
```

Use `docs/prompts/reusable-bucket-implementation-prompt.md` when implementing
one Epic from a bucket.

Use `docs/prompts/commit-report-prompt.md` when asked to summarize the current
diff or generate a suggested commit message without making code changes.

Do not automatically run the commit report prompt after every task unless the
current task asks for it. For normal Epic implementation, use the commit-style
report rules in this file.

## Checks After Each Epic

Run relevant local checks after each Epic:

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
git diff --check
```

On Windows, use the Gradle wrapper equivalents:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
git diff --check
```

If connected Android tests exist and are relevant, run:

```bash
./gradlew connectedDebugAndroidTest
```

If connected tests do not exist or are not relevant, report that clearly.

## Physical Device Smoke Test

A physical Android device may be connected:

```text
Device: Pixel 10 Pro
Developer mode: enabled
Expected connection: USB debugging through adb
```

Use it for a smoke test after each Epic when available.

Start with:

```bash
adb devices
```

If the device is listed as `device`, continue.

Clear logs:

```bash
adb logcat -c
```

Install the debug build:

```bash
./gradlew installDebug
```

On Windows, use:

```powershell
.\gradlew.bat installDebug
```

Launch the app:

```bash
adb shell monkey -p dev.nighthawklabs.homebar -c android.intent.category.LAUNCHER 1
```

Check the app process:

```bash
adb shell pidof dev.nighthawklabs.homebar
```

Inspect recent logs for crashes:

```bash
adb logcat -d -t 300 | grep -iE "FATAL EXCEPTION|AndroidRuntime|dev.nighthawklabs.homebar"
```

If `grep` is unavailable, use an equivalent command or inspect recent logcat
output manually.

Do not claim a manual UI behavior was verified unless it was actually checked
on the device AND through an automated test.

If the device is not visible, unauthorized, offline, or unavailable, do not
block the whole task. Report the exact command output that prevented device
testing.

## Commit Message and Report Rules

After each Epic, provide a suggested commit message and completion report.

When asked to generate a commit message at any time, base it on the current
diff. Inspect the diff before writing the message. Do not invent changes that
are not present.

Use the 50/72 rule:

- Commit subject should be 50 characters or fewer when practical.
- Wrap body lines at 72 characters when practical.
- If the bucket or Epic name is long, keep the subject short and put bucket
  details in the body.

Use conventional commit style for the subject when it fits:

```text
feat: add recipe ingredient filter
fix: correct serving quantity scaling
test: cover substitute matching
docs: update bucket instructions
refactor: split recipe matching logic
```

Do not include absolute file paths in reports or commit messages.

Use file names only in backticks for file lists:

```text
- `RecipeListViewModel.kt`
- `RecipeFilterLogicTest.kt`
```

Do not use full paths like:

```text
C:\Users\Kevin\Documents\VS-Code-Projects\homebar\app\src\...
```

If a report section has no entries, write a short explicit line instead of
omitting important context.

Do not run `git commit` unless explicitly asked. Suggested commit messages are
for the user to review and copy.

## Review Guidelines

Before finishing a task, check:

- The change matches the requested bucket and Epic.
- No out-of-scope feature was added.
- No unnecessary permission was added.
- Room and DataStore responsibilities are not mixed.
- Derived recipe match data is recalculated instead of saved as stale state.
- Unit tests cover domain logic where practical.
- The app still builds.
- The connected Pixel smoke test was attempted when available.
- The completion report is based on the current diff.
- File lists use file names only, not absolute paths.
