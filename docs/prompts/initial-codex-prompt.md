# Initial Codex Prompt

You are working in a new Android app repository for a home bar cocktail app.

Read these files before making changes:

- `AGENTS.md`
- `docs/product/bucket-0-android-project-setup.md`
- `docs/product/bucket-1-inventory-recipes-makeability.md`
- `docs/product/bucket-2-recipe-list-detail-screens.md`
- `docs/product/bucket-3-inventory-management-screens.md`
- `docs/product/bucket-4-recipe-editor-custom-recipes.md`
- `docs/product/bucket-5-recipe-sharing-by-text-message.md`
- `docs/product/bucket-6-app-settings-and-preferences.md`

Your task is to implement Bucket 0 only.

Do not implement Buckets 1 through 6 yet. Read them only so the project foundation does not conflict with planned work.

## Project Goal

Build a native Android app that answers:

> What drinks can I make right now with my current home bar inventory?

The app is local-first for now. Do not add account creation, email login, cloud backup, cloud sync, Firebase, or shopping-list behavior.

## Required Stack

Create or update the Android project foundation using:

- Kotlin
- Jetpack Compose
- Material 3
- Single-activity architecture
- Navigation Compose
- ViewModels
- Repository layer
- Room
- DataStore Preferences
- Kotlin coroutines
- Flow / StateFlow
- JUnit tests

Use this package name:

```text
dev.nighthawklabs.homebar
```

Use this working app name:

```text
Home Bar
```

## Scope for This Task

Implement only the project foundation needed for later buckets.

Required work:

1. Confirm or create the Android project structure.
2. Configure Kotlin and Jetpack Compose.
3. Configure Material 3.
4. Configure Navigation Compose.
5. Configure Room.
6. Configure KSP for Room.
7. Configure DataStore Preferences.
8. Configure Kotlin coroutines.
9. Configure Flow / StateFlow support.
10. Configure JUnit tests.
11. Create the main package structure:
    - `data/local`
    - `data/local/dao`
    - `data/local/entity`
    - `data/repository`
    - `data/seed`
    - `domain/model`
    - `domain/logic`
    - `ui/navigation`
    - `ui/recipes/list`
    - `ui/recipes/detail`
    - `ui/inventory`
    - `ui/settings`
    - `ui/theme`
    - `util`
12. Create `MainActivity`.
13. Create the app theme.
14. Create a placeholder navigation graph.
15. Create a placeholder recipe list screen.
16. Create a placeholder recipe detail screen.
17. Create a placeholder inventory screen.
18. Create a placeholder settings screen.
19. Add basic navigation between placeholder screens.
20. Add back navigation where needed.
21. Add a placeholder repository or sample state only if needed to support the placeholder screens.
22. Add at least one simple unit test.
23. Confirm the app builds.
24. Confirm tests pass.
25. Confirm a release Android App Bundle can be generated locally.

## Do Not Add

Do not add these in Bucket 0:

- Recipe matching implementation
- Full Room schema
- Real seed recipe catalog
- Inventory management behavior
- Recipe editor
- Substitute management
- Recipe sharing implementation
- Settings persistence beyond any minimal placeholder needed for setup
- Account creation
- Email login
- Cloud backup
- Cloud sync
- Firebase
- Shopping list
- AI recipe generation
- Direct SMS sending
- Contact lookup
- Barcode scanning
- Cost tracking
- Final branding
- Extra Android permissions

## Implementation Rules

- Keep changes focused on Bucket 0.
- Do not build future bucket features early.
- Keep domain logic separate from UI code.
- Do not add unnecessary dependencies.
- Do not add unnecessary Android permissions.
- Prefer simple, readable Kotlin.
- Do not commit, branch, push, or run destructive git commands unless I explicitly ask.
- Run relevant build and test commands before reporting completion.

## Completion Report

When finished, report:

- Files created
- Files changed
- Commands run
- Whether the app builds
- Whether tests pass
- Whether release bundle generation works
- Any manual Android Studio steps I still need to complete
- Any assumptions made
