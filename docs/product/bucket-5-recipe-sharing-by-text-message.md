# Bucket 5: Recipe Sharing by Text Message

## Purpose

Let the user share a readable recipe from the recipe detail screen.

The app should generate a clean text version of a recipe and open the Android share sheet. The user can then choose Messages or another installed app.

This bucket should not build a messaging system. The app should not send texts directly, store contacts, request SMS permissions, track sent messages, show a custom share preview, or implement its own clipboard flow.

## Locked Decisions From Earlier Buckets

- The app’s main purpose is to answer: What drinks can I make right now with my current home bar inventory?
- Recipe sharing is a convenience feature, not a social feature.
- The app should avoid unnecessary Android permissions.
- Recipe detail already shows scaled ingredient quantities.
- Serving multiplier changes displayed quantities only.
- Saved base recipe quantities do not change when sharing a scaled recipe.
- Substitutes count as valid ingredients for recipe matching.
- Substituted ingredients should be marked inline in recipe detail.
- Custom recipes behave like normal recipes in recipe detail.
- No dedicated shopping list will be built.
- No share preview screen or modal should be built for this bucket.
- No in-app copy text action should be built for this bucket.
- The app should rely on Android’s share sheet for available copy behavior.

## Epic 1: Share Entry Point

### Goal

Add a share action to the recipe detail screen.

### Entry Point

The recipe detail screen should include:

```md
Share recipe
```

This action should be available for:

```md
- Classic recipes
- Custom recipes
- Duplicated recipes
- Favorite recipes
- Recipes with substitutions
- Recipes with scaled servings
```

### Rules

```md
- Sharing should use the currently selected serving count.
- Sharing should not modify the saved recipe.
- Sharing should not require an account.
- Sharing should not require contact access.
- Sharing should not require SMS permissions.
- Sharing should open the Android share sheet directly.
- Sharing should not first open an in-app preview screen.
```

### Tasks

- Add share action to recipe detail screen.
- Connect share action to current recipe detail state.
- Use the current serving count when generating share text.
- Use the displayed ingredient quantities, not the saved base quantities.
- Include substitutions if substitutions are currently used.
- Open Android share sheet with the generated text.

### Acceptance Criteria

- User can share a recipe from recipe detail.
- Share action uses the current serving count.
- Share action does not change the saved recipe.
- Android share sheet opens directly.
- Messages appears as an option if the device has a compatible messaging app.
- No SMS or contact permissions are added.
- No custom preview screen or modal appears before the share sheet.

## Epic 2: Share Text Formatter

### Goal

Create a plain-text formatter that turns a recipe into a message-friendly format.

### Required Output

The shared text should include:

```md
- Recipe name
- Serving count
- Ingredient list with scaled quantities
- Inline substitute notes, if applicable
- Instructions
- Glassware, if present
- Garnish, if present
```

Tools should be optional. They are useful inside the app, but may make a text message feel too long.

### Default Format

Use this structure:

```md
Margarita
Makes 2 drinks

Ingredients:
- 4 oz tequila
- 2 oz lime juice
- 1.5 oz orange liqueur
- 1 oz agave syrup

Instructions:
1. Add ingredients to a shaker with ice.
2. Shake until chilled.
3. Strain into a rocks glass over fresh ice.
4. Garnish with a lime wedge.

Glassware:
Rocks glass

Garnish:
Lime wedge
```

### Substitute Format

If a recipe is makeable because of a substitute, show it inline.

Example:

```md
Cuba Libre
Makes 1 drink

Ingredients:
- 2 oz rum
- 4 oz Pepsi (substitute for Coke)
- 0.5 oz lime juice

Instructions:
1. Build in a highball glass over ice.
2. Stir.
3. Garnish with lime.
```

### Rules

```md
- Use plain text only.
- Do not use markdown formatting in the shared output.
- Do not include internal recipe ids.
- Do not include inventory status unless needed for substitutions.
- Do not include running-low warnings.
- Do not include missing ingredient warnings.
- Do not include app debug data.
- Do not include share preview metadata.
```

### Tasks

- Create recipe share formatter.
- Format recipe name.
- Format selected serving count.
- Format scaled ingredient lines.
- Format substitute notes inline.
- Format ordered instructions.
- Format glassware when present.
- Format garnish when present.
- Omit empty sections.
- Add unit tests for formatter output.

### Acceptance Criteria

- Formatter produces readable plain text.
- Formatter uses scaled quantities.
- Formatter includes substitute notes when needed.
- Formatter omits empty sections.
- Formatter output does not include app-only data.
- Formatter has unit tests.

## Epic 3: Serving Count and Shared Quantities

### Goal

Ensure shared recipes match what the user is viewing.

### Behavior

If the user opens a recipe and changes the serving count, sharing should use that serving count.

Example:

```md
Recipe detail:
Margarita
Servings: 4

Shared text:
Margarita
Makes 4 drinks
```

The ingredient quantities should match the scaled recipe detail screen.

### Rules

```md
- Shared quantities should come from the same scaling logic used by recipe detail.
- Sharing should not create a new recipe.
- Sharing should not save the selected serving count.
- Returning to the recipe later should still use the normal base serving behavior.
```

### Tasks

- Reuse serving multiplier logic from recipe detail.
- Pass selected serving count into share formatter.
- Confirm shared ingredient quantities match recipe detail quantities.
- Add unit test for sharing a recipe at 1 serving.
- Add unit test for sharing a recipe at 4 servings.

### Acceptance Criteria

- Sharing at 1 serving produces base quantities.
- Sharing at 4 servings produces quantities multiplied by 4.
- Saved recipe quantities remain unchanged.
- Recipe detail and shared text show matching quantities.

## Epic 4: Android Share Sheet Integration

### Goal

Use the Android system share sheet to let the user choose where to send the recipe.

### Behavior

The app should create a text share intent.

Expected intent behavior:

```md
- Action: ACTION_SEND
- Type: text/plain
- Extra text: generated recipe share text
- Chooser title: Share recipe
```

### Rules

```md
- Do not send SMS directly.
- Do not choose the recipient inside the app.
- Do not read contacts.
- Do not request SMS permissions.
- Do not request contacts permissions.
- Do not store recipient information.
- Do not track share history.
- Do not build a custom share preview.
- Do not build an in-app clipboard copy action.
- Do not add custom Android sharesheet actions in this bucket.
```

### Tasks

- Create share intent helper.
- Pass generated recipe text to share intent.
- Use `text/plain`.
- Open system chooser.
- Add error handling if no compatible apps are available.

### Acceptance Criteria

- Android share sheet opens with generated recipe text.
- User can choose Messages if available.
- User can choose other compatible apps.
- No direct SMS implementation is added.
- No contacts permission is added.
- No SMS permission is added.
- No in-app clipboard feature is added.

## Epic 5: Share Formatting Tests

### Goal

Protect the share output from regressions.

### Test Cases

Add formatter tests for:

```md
- Basic recipe at 1 serving
- Recipe scaled to 4 servings
- Recipe with substitute ingredient
- Recipe with no garnish
- Recipe with no glassware
- Custom recipe
```

### Example Test Expectation

```md
Input:
Margarita at 2 servings

Expected:
- "Margarita"
- "Makes 2 drinks"
- "4 oz tequila"
- "2 oz lime juice"
```

### Tasks

- Add test fixtures for recipe share formatting.
- Add formatter unit tests.
- Confirm tests do not require Android UI runtime.
- Confirm tests run from terminal.

### Acceptance Criteria

- Formatter tests pass.
- Scaling tests pass.
- Substitute formatting tests pass.
- Empty section omission tests pass.

## Out of Scope for Bucket 5

Do not build these in this bucket:

```md
- Direct SMS sending
- Recipient picker
- Contact lookup
- Contact storage
- Share history
- Social feed
- Public recipe links
- QR codes
- Image sharing
- PDF export
- Recipe import
- Account creation
- Cloud backup
- AI recipe generation
- Shopping list
- Share preview screen or modal
- In-app clipboard copy action
- Custom Android sharesheet actions
```

## Checkpoint

Bucket 5 is complete when the app can:

```md
- Generate plain-text recipe share output
- Use current serving count in shared text
- Include scaled ingredient quantities
- Mark substituted ingredients inline
- Open Android share sheet directly
- Share without SMS permissions
- Share without contacts permissions
- Pass formatter unit tests
```

## Next Verification Point

Before starting Bucket 6, confirm what the next bucket should focus on:

```md
Option A: Account and cloud backup decision
Option B: AI recipe helper
Option C: App settings and preferences
Option D: Polish pass for recipe and inventory flows
Option E: Google Play readiness
```
