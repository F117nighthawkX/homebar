# Bucket 6: App Settings and Preferences

## Purpose

Build a simple settings area for user preferences.

This bucket should cover defaults and display choices that affect the app experience, while keeping recipe data, ingredient data, inventory data, and substitute data separate.

Use DataStore Preferences for this bucket. Do not store recipes, ingredients, inventory, or substitutes in DataStore.

## Locked Decisions From Earlier Buckets

- The app’s main purpose is to answer: What drinks can I make right now with my current home bar inventory?
- Native Android is the first target.
- Kotlin and Jetpack Compose are the app stack.
- Room stores structured app data.
- DataStore stores small app preferences.
- Recipe matching uses current inventory and substitute data.
- Substitutes count as valid ingredients for recipe matching.
- Recipe sharing uses the Android share sheet.
- No dedicated shopping list will be built.
- Account creation and cloud backup remain out of scope for this bucket.

## Epic 1: Settings Screen

### Goal

Create a settings screen where the user can view and update app preferences.

### Entry Point

The app should expose settings from the main app navigation.

Suggested entry point:

```md
Recipe List
- Settings action in top app bar or overflow menu
```

Settings should not interrupt the core recipe browsing flow.

### Screen Content

Settings should include sections for:

```md
- Recipe list defaults
- Display preferences
- Sharing preferences
- Data and backup placeholder
- About app
```

### Tasks

- Create settings screen.
- Add navigation to settings.
- Add back navigation.
- Add settings sections.
- Read current preferences from DataStore.
- Save updated preferences to DataStore.
- Apply preferences where relevant.

### Acceptance Criteria

- User can open settings.
- User can return to the previous screen.
- Settings values persist after app restart.
- Settings changes update the related app behavior.

## Epic 2: Recipe List Default Filter

### Goal

Let the user choose the default filter used when opening the recipe list.

### Options

The app should support these default recipe list filters:

```md
- Makeable now
- Missing 1 ingredient
- All recipes
- Favorites
```

Default value:

```md
Makeable now
```

### Rules

```md
- The default filter applies when the recipe list is first opened.
- If the user changes the filter while browsing, that screen state can remain active during the current session.
- The saved default should not change unless the user changes it in settings.
```

### Tasks

- Add default recipe filter preference.
- Store selected value in DataStore.
- Load saved value when recipe list is first opened.
- Fall back to `Makeable now` if no saved value exists.
- Add UI control for selecting the default filter.

### Acceptance Criteria

- User can set default recipe filter.
- Saved default persists after app restart.
- Recipe list uses saved default on first load.
- Default value is `Makeable now`.

## Epic 3: Inventory Default Filter

### Goal

Let the user choose the default filter used when opening the inventory screen.

### Options

The app should support these default inventory filters:

```md
- All ingredients
- Missing ingredients
- Running low
- Missing for favorite recipes
- Running low for favorite recipes
- Missing or running low for favorite recipes
```

Default value:

```md
All ingredients
```

### Rules

```md
- The default filter applies when the inventory screen is first opened.
- Search text should not persist by default.
- Category filters should not persist by default unless a later bucket adds that behavior.
```

### Tasks

- Add default inventory filter preference.
- Store selected value in DataStore.
- Load saved value when inventory screen is first opened.
- Fall back to `All ingredients` if no saved value exists.
- Add UI control for selecting the default inventory filter.

### Acceptance Criteria

- User can set default inventory filter.
- Saved default persists after app restart.
- Inventory screen uses saved default on first load.
- Default value is `All ingredients`.

## Epic 4: Measurement Display Preference

### Goal

Let the user choose the default measurement style for recipe display and sharing.

### Options

Start with these options:

```md
- Original recipe units
- US cocktail units
- Metric
```

Default value:

```md
Original recipe units
```

### Rules

```md
- Original recipe units show quantities using the units saved on the recipe.
- US cocktail units should prefer ounces, dashes, teaspoons, tablespoons, and similar cocktail units.
- Metric should prefer milliliters for liquid measurements.
- This bucket should not require full unit conversion if the app does not already have conversion logic.
```

### Recommended Scope

For Bucket 6, store the preference and apply it only where conversion is already safe.

If full unit conversion is not implemented yet, show this setting as saved but apply it later.

### Tasks

- Add measurement display preference.
- Store selected value in DataStore.
- Add settings UI control.
- Use `Original recipe units` as default.
- Add TODO or follow-up task for full unit conversion if needed.

### Acceptance Criteria

- User can choose measurement display preference.
- Preference persists after app restart.
- App safely avoids incorrect conversions.
- No recipe quantity data is changed by this setting.

## Epic 5: Theme Preference

### Goal

Let the user choose the app appearance.

### Options

Support:

```md
- Follow system
- Light
- Dark
```

Default value:

```md
Follow system
```

### Rules

```md
- Follow system uses the device theme.
- Light always uses light theme.
- Dark always uses dark theme.
- Theme setting should apply across the app.
```

### Tasks

- Add theme preference.
- Store selected value in DataStore.
- Update app theme to read preference.
- Add settings UI control.
- Apply selected theme across Compose screens.

### Acceptance Criteria

- User can select theme behavior.
- Theme preference persists after app restart.
- App follows selected theme behavior.
- Default value is `Follow system`.

## Epic 6: Recipe Sharing Preferences

### Goal

Let the user choose what optional content appears in shared recipe text.

### Options

Sharing preferences should include:

```md
- Include glassware
- Include garnish
- Include tools
```

Default values:

```md
- Include glassware: true
- Include garnish: true
- Include tools: false
```

### Rules

```md
- Sharing should still use Android share sheet.
- Sharing should still use the current serving count.
- Sharing should still include recipe name, serving count, ingredients, and instructions.
- These preferences should only affect optional sections.
```

### Tasks

- Add sharing preference values.
- Store values in DataStore.
- Add settings toggles.
- Update share formatter to read sharing preferences.
- Add formatter tests for enabled and disabled optional sections.

### Acceptance Criteria

- User can choose whether shared text includes glassware.
- User can choose whether shared text includes garnish.
- User can choose whether shared text includes tools.
- Share formatter respects saved preferences.
- Default shared text includes glassware and garnish, but not tools.

## Epic 7: Data and Backup Placeholder

### Goal

Prepare the settings screen for future account and backup decisions without building them in this bucket.

### Display

Add a section called:

```md
Data and backup
```

For now, show:

```md
Local data only

Recipes, inventory, and settings are stored on this device.
Cloud backup can be added later.
```

### Rules

```md
- Do not add account creation.
- Do not add email login.
- Do not add Firebase.
- Do not add cloud sync.
- Do not add export or import unless a later bucket asks for it.
```

### Tasks

- Add Data and backup section to settings.
- Add local-data-only message.
- Do not add interactive account controls.

### Acceptance Criteria

- Settings screen clearly states that data is local only.
- User is not shown inactive login controls.
- No cloud dependencies are added.

## Epic 8: About App Section

### Goal

Add basic app information.

### Display

The About section should show:

```md
- App name
- App version name
- App version code, optional
- Package name, optional
```

Optional text:

```md
Home Bar helps you find cocktails you can make with your current home bar inventory.
```

### Tasks

- Add About section.
- Read app version name from build config or package info.
- Display app name.
- Display version name.
- Add short app description.

### Acceptance Criteria

- User can view app version.
- User can view app name.
- About section does not include legal text yet.
- About section does not include privacy policy yet.

## Epic 9: Preferences Repository

### Goal

Create a clean preference access layer so screens do not read DataStore directly.

### Data Model

Create app preference models such as:

```md
AppPreferences
- defaultRecipeFilter
- defaultInventoryFilter
- measurementDisplay
- themePreference
- shareGlassware
- shareGarnish
- shareTools
```

### Rules

```md
- UI screens should read preferences through a repository or settings data source.
- DataStore keys should stay in one place.
- Defaults should be defined in one place.
- Invalid stored values should fall back to safe defaults.
```

### Tasks

- Create preferences data source.
- Create preferences repository.
- Define DataStore keys.
- Define default preference values.
- Expose preferences as Flow or StateFlow.
- Add update methods for each preference.
- Add unit tests where practical.

### Acceptance Criteria

- Settings screen reads preferences through a repository.
- Settings screen updates preferences through a repository.
- Preference defaults are centralized.
- Invalid values fall back to defaults.

## Out of Scope for Bucket 6

Do not build these in this bucket:

```md
- Account creation
- Email login
- Cloud backup
- Cloud sync
- Firebase
- Import or export
- Shopping list
- Direct SMS sending
- Contact lookup
- AI recipe generation
- Full unit conversion if not already safely supported
- Privacy policy
- Terms of service
- Google Play listing
```

## Checkpoint

Bucket 6 is complete when the app can:

```md
- Open a settings screen
- Save and load default recipe filter
- Save and load default inventory filter
- Save and load measurement display preference
- Save and load theme preference
- Save and load recipe sharing preferences
- Apply theme preference across the app
- Apply share formatting preferences
- Show local-data-only backup message
- Show app name and version
- Persist settings after app restart
- Keep settings in DataStore
- Keep recipes, ingredients, inventory, and substitutes out of DataStore
```

## Next Verification Point

Before starting Bucket 7, confirm what the next bucket should focus on:

```md
Option A: Account and cloud backup decision
Option B: AI recipe helper
Option C: Google Play readiness
Option D: Polish pass for recipe and inventory flows
Option E: Import and export
```
