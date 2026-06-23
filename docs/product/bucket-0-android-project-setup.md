# Bucket 0: Android Project Setup and Stack Decisions

## Purpose

Set up the Android project foundation before building app features.

This bucket defines the stack, project structure, build settings, local data approach, and Google Play release direction. It exists so future feature prompts can assume a stable foundation instead of choosing packages or architecture as they go.

## Product Direction

Build the first version as a native Android app.

The app should eventually be eligible for Google Play Store release, but early development should focus on local functionality first:

- Browse recipes
- Match recipes against inventory
- View recipe details
- Scale serving quantities
- Track in-stock and running-low ingredients
- Support substitutes as valid available ingredients

Cloud backup, accounts, AI features, and public release work should come later.

## Stack Decisions

Use this stack for the first Android version:

- Platform: Native Android
- Language: Kotlin
- UI: Jetpack Compose
- Design system: Material 3
- Architecture: Single-activity app
- State management: ViewModels and UI state objects
- Data access: Repository layer
- Local database: Room
- Small preferences: DataStore Preferences
- Async and reactive state: Kotlin coroutines, Flow, and StateFlow
- Navigation: Jetpack Navigation for Compose
- Testing: JUnit for domain logic, Compose UI tests later

## Rationale

This stack fits the project because:

- The first target device is Android.
- The app needs structured local data for recipes, ingredients, inventory, and substitutes.
- The first version should work without accounts or cloud backup.
- Recipe matching logic should be easy to test without launching the UI.
- Jetpack Compose keeps the UI close to app state.
- Room gives the project a real local database instead of temporary sample data.
- DataStore is enough for small settings like default filter or theme.

## Google Play Direction

Set up the project with Google Play release in mind, even before publishing.

The project should support:

- Android App Bundle generation
- Release build configuration
- App signing later
- Target SDK configured for current Google Play requirements at release time
- Version code and version name
- App icon placeholder
- App name placeholder
- Privacy-conscious permissions

Do not add unnecessary permissions.

For the early version, the app should not require:

- Camera
- Location
- Contacts
- Microphone
- Bluetooth
- External storage

## Recommended Package Name

Use a stable package name early because changing it later can affect release planning.

Recommended:

```text
dev.nighthawklabs.homebar
```

Alternative options:

```text
dev.nighthawklabs.barbook
dev.nighthawklabs.mixbook
dev.nighthawklabs.cocktailcellar
```

## Recommended Working App Name

Use a placeholder name until branding is finalized.

Recommended placeholder:

```text
Home Bar
```

Other possible names:

```text
Bar Book
Mix Book
Cocktail Cellar
Shelf & Shaker
```

## Project Structure

Use a simple package structure that can grow without becoming overbuilt.

```text
app/
  src/main/java/dev/nighthawklabs/homebar/
    MainActivity.kt

    data/
      local/
        AppDatabase.kt
        dao/
        entity/
      repository/
      seed/

    domain/
      model/
      logic/

    ui/
      navigation/
      recipes/
        list/
        detail/
      inventory/
      theme/

    util/
```

## Layer Responsibilities

### Data Layer

Owns storage and persistence.

- Room database
- DAOs
- Database entities
- Seed data
- Repository implementations

### Domain Layer

Owns app rules.

- Recipe matching logic
- Serving multiplier logic
- Substitute matching logic
- Match status models

The domain layer should avoid Android UI dependencies so it can be tested with normal unit tests.

### UI Layer

Owns screens and user interaction.

- Compose screens
- ViewModels
- UI state
- Navigation
- User actions

## Initial Dependencies

Use current stable versions when creating the project.

Expected dependency groups:

- AndroidX Core KTX
- Jetpack Compose BOM
- Compose Material 3
- Compose Preview tooling
- AndroidX Lifecycle ViewModel Compose
- AndroidX Lifecycle Runtime Compose
- Navigation Compose
- Room Runtime
- Room KTX
- Room Compiler through KSP
- DataStore Preferences
- Kotlin Coroutines
- JUnit
- AndroidX Test
- Compose UI Test

## Build Setup Tasks

- Install Android Studio.
- Create a new Android project using Kotlin and Jetpack Compose.
- Set package name.
- Set app name placeholder.
- Confirm Gradle build uses Kotlin DSL if available.
- Add Compose Material 3.
- Add Navigation Compose.
- Add Lifecycle ViewModel Compose.
- Add Room.
- Add KSP for Room compiler.
- Add DataStore Preferences.
- Add test dependencies.
- Create initial package folders.
- Confirm the app builds.
- Confirm the app runs on Android emulator.
- Confirm the app runs on a physical Android phone.
- Confirm release Android App Bundle generation works locally.

## App Foundation Tasks

- Create `MainActivity`.
- Create app theme.
- Create placeholder navigation graph.
- Create placeholder recipe list screen.
- Create placeholder recipe detail screen.
- Add simple navigation from list to detail.
- Add a placeholder repository returning sample recipes.
- Add a placeholder ViewModel for recipe list state.
- Add a placeholder ViewModel for recipe detail state.

## Testing Setup Tasks

- Add unit test folder.
- Add first unit test for serving multiplier logic.
- Add first unit test for recipe makeability logic.
- Add first unit test for substitute matching logic.
- Confirm tests run from Android Studio.
- Confirm tests run from terminal.

## Out of Scope for Bucket 0

Do not build these yet:

- Full recipe matching implementation
- Full Room schema
- Inventory screen
- Recipe editor
- Account creation
- Email login
- Cloud backup
- AI recipe generation
- Shopping list
- Text message sharing
- App store listing
- Final branding

## Checkpoint

Bucket 0 is complete when:

- Android project exists
- Kotlin and Jetpack Compose are configured
- App opens on emulator
- App opens on physical Android device
- Navigation from placeholder recipe list to placeholder recipe detail works
- Room dependency is installed
- DataStore dependency is installed
- Test dependencies are installed
- At least one unit test passes
- A release Android App Bundle can be generated locally

## Next Verification Point

Before starting Bucket 1, confirm:

- Native Android remains the first target
- Kotlin remains the app language
- Jetpack Compose remains the UI toolkit
- Room remains the local database choice
- No account system will be added during Buckets 0, 1, or 2
- Account and cloud backup will be revisited in a later bucket
