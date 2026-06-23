# Bucket 4: Recipe Editor and Custom Recipes

## Purpose

Build the screens and rules that let the user create, duplicate, edit, and delete custom cocktail recipes.

This bucket gives the app personal value beyond a fixed recipe list. The user should be able to start from a classic recipe, copy it, adjust the spec, rename it, and save it as a custom drink.

This bucket assumes the app can already:

```md
- Store ingredients
- Store recipes
- Match recipes against inventory
- Scale recipe servings
- Browse recipes
- Open recipe details
- Manage inventory status
```

## Locked Decisions From Earlier Buckets

- Classic recipes should remain available as starter recipes.
- A copied classic recipe becomes a custom recipe.
- Editing a copied recipe should not change the original recipe.
- Serving multiplier changes displayed quantities only.
- Saved base recipe quantities do not change when the serving count changes in the recipe detail screen.
- Running low ingredients still count as in stock.
- Substitutes count as valid ingredients for recipe matching.
- Substitution information should be shown inline on the recipe detail screen, not treated as a separate recipe list category.
- Tools and glassware do not affect makeability.
- Garnishes do not block makeability in the first version.
- In the recipe editor, the user should choose measurement unit before entering quantity.

## Epic 1: Recipe Editor Entry Points

### Goal

Give the user clear ways to create or edit recipes.

### Entry Points

The app should support these paths:

```md
Recipe List
- Add new recipe

Recipe Detail for classic recipe
- Duplicate recipe

Recipe Detail for custom recipe
- Edit recipe
- Duplicate recipe
- Delete recipe
```

### Rules

```md
- Classic recipes cannot be edited directly.
- Classic recipes can be duplicated.
- Custom recipes can be edited.
- Custom recipes can be duplicated.
- Custom recipes can be deleted.
- Deleting a custom recipe should require confirmation.
```

### Tasks

- Add “Add new recipe” action to recipe list.
- Add “Duplicate recipe” action to recipe detail.
- Add “Edit recipe” action for custom recipes.
- Hide direct edit action for classic recipes.
- Add delete action for custom recipes.
- Add confirmation before deleting custom recipes.

### Acceptance Criteria

- User can start a new blank recipe.
- User can duplicate a classic recipe.
- User cannot directly edit a classic recipe.
- User can edit a custom recipe.
- User can delete a custom recipe after confirmation.
- Deleting a custom recipe removes it from the recipe list.

## Epic 2: Recipe Edit Screen

### Goal

Create a form for editing recipe fields.

### Editable Fields

The recipe editor should support:

```md
- Recipe name
- Base serving count
- Ingredients
- Instructions
- Glassware
- Tools
- Garnish
- Tags
- Favorite status
```

### Recipe Type Fields

The app should track these internally:

```md
- isCustom
- sourceRecipeId, if copied from another recipe
- createdAt
- updatedAt
```

The user does not need to edit these fields directly.

### Tasks

- Create recipe edit screen.
- Add recipe name input.
- Add base serving count input.
- Add ingredient editor section.
- Add instructions editor section.
- Add glassware input.
- Add tools input.
- Add garnish input.
- Add tags input.
- Add favorite toggle.
- Add save action.
- Add cancel action.

### Acceptance Criteria

- User can edit all recipe fields needed for a custom recipe.
- User can save a valid recipe.
- User can cancel without saving changes.
- Saved recipe appears in the recipe list.
- Saved recipe opens in recipe detail.
- Recipe matching runs against the saved recipe.

## Epic 3: Ingredient Lines

### Goal

Let the user add, remove, and reorder recipe ingredients.

### Ingredient Line Fields

Each ingredient line should support:

```md
- Ingredient
- Unit
- Quantity
- Optional note
- Display order
```

The editor should ask for measurement unit before quantity.

Recommended field order:

```md
1. Choose ingredient
2. Choose unit
3. Enter quantity
4. Add optional note
```

Example:

```md
Ingredient: lime juice
Unit: oz
Quantity: 1
Note: fresh preferred
```

### Units

Start with common cocktail units:

```md
- oz
- dash
- barspoon
- tsp
- tbsp
- ml
- drop
- pinch
- wedge
- leaf
- splash
- top
```

### Rules

```md
- Unit should be selected before quantity.
- Quantity input should be shown after the user chooses a unit.
- Quantity is required for measured liquid ingredients.
- Some garnish-style units may allow no numeric quantity later, but Bucket 4 should keep the model simple.
- Ingredient order should be preserved.
- Empty ingredient lines should not be saved.
```

### Tasks

- Add ingredient picker.
- Add unit picker.
- Add quantity input after unit selection.
- Add note input.
- Add add-ingredient-line action.
- Add remove-ingredient-line action.
- Add move ingredient up action.
- Add move ingredient down action.
- Preserve ingredient order after saving.

### Acceptance Criteria

- User can add ingredient lines.
- User can remove ingredient lines.
- User can reorder ingredient lines.
- User can choose an ingredient.
- User chooses a unit before entering quantity.
- User can enter quantity after choosing a unit.
- User can save ingredient notes.
- Recipe detail shows ingredients in the saved order.

## Epic 4: Ingredient Selection and Quick Create

### Goal

Let the user use existing ingredients while still allowing new custom recipes that need ingredients not already in the app.

### Behavior

The ingredient picker should search existing ingredients first.

If the needed ingredient does not exist, the user can create a basic ingredient record from the editor.

### Quick Create Fields

Quick create should support:

```md
- Ingredient name
- Category
```

Default inventory state for a quick-created ingredient:

```md
- inStock: false
- runningLow: false
```

### Rules

```md
- Quick-created ingredients should be added to the ingredient database.
- Quick-created ingredients should appear in inventory management screens.
- Quick-created ingredients should not be marked in stock automatically.
- Full ingredient editing remains out of scope.
- Substitute group editing remains out of scope.
```

### Tasks

- Add ingredient search to ingredient picker.
- Show matching existing ingredients.
- Add “Create ingredient” action when no matching ingredient exists.
- Create ingredient with name and category.
- Save the new ingredient.
- Select the new ingredient for the recipe line.

### Acceptance Criteria

- User can find an existing ingredient.
- User can create a missing ingredient from the recipe editor.
- Newly created ingredient appears in inventory.
- Newly created ingredient starts as not in stock.
- Recipe can use the newly created ingredient.

## Epic 5: Instructions Editor

### Goal

Let the user write recipe steps in a way that is easy to read later.

### Behavior

Start with ordered instruction steps.

Example:

```md
1. Add tequila, lime juice, orange liqueur, and agave syrup to a shaker with ice.
2. Shake until chilled.
3. Strain into a rocks glass over fresh ice.
4. Garnish with a lime wedge.
```

### Tasks

- Add instruction step list.
- Add new step action.
- Edit step text.
- Remove step action.
- Move step up action.
- Move step down action.
- Preserve step order after saving.

### Acceptance Criteria

- User can add instruction steps.
- User can edit instruction steps.
- User can remove instruction steps.
- User can reorder instruction steps.
- Recipe detail shows instructions in the saved order.

## Epic 6: Duplicate Recipe Flow

### Goal

Let the user copy an existing recipe and edit the copy.

### Behavior

When duplicating a recipe, the app should create a new custom recipe.

Example:

```md
Original:
Margarita

Duplicate name:
Margarita Copy
```

The user can then rename it.

Example:

```md
Spicy Pineapple Margarita
```

### Copied Fields

The duplicate should copy:

```md
- Recipe name
- Base serving count
- Ingredients
- Instructions
- Glassware
- Tools
- Garnish
- Tags
- Favorite status
```

The duplicate should set:

```md
- isCustom: true
- sourceRecipeId: original recipe id
- createdAt: current time
- updatedAt: current time
```

### Tasks

- Add duplicate action.
- Copy recipe fields.
- Assign new recipe id.
- Mark duplicate as custom.
- Preserve source recipe id.
- Open duplicate in recipe editor after creation.

### Acceptance Criteria

- User can duplicate a classic recipe.
- User can duplicate a custom recipe.
- Duplicate has a separate id.
- Duplicate is marked custom.
- Editing duplicate does not change the original.
- Duplicate appears in recipe list.

## Epic 7: Recipe Validation

### Goal

Prevent empty or unusable recipes from being saved.

### Required Fields

A recipe must have:

```md
- Name
- At least one ingredient line
- At least one instruction step
```

Each ingredient line must have:

```md
- Ingredient
- Unit
- Quantity
```

### Validation Messages

Use direct messages:

```md
Recipe name is required.
Add at least one ingredient.
Add at least one instruction step.
Choose an ingredient.
Choose a unit.
Enter a quantity.
```

### Tasks

- Validate recipe name.
- Validate ingredient lines.
- Validate instruction steps.
- Show validation messages.
- Prevent save when validation fails.

### Acceptance Criteria

- Empty recipe cannot be saved.
- Recipe without ingredients cannot be saved.
- Recipe without instructions cannot be saved.
- Invalid ingredient lines are marked.
- User can fix validation issues and save.

## Epic 8: Unsaved Changes Handling

### Goal

Prevent accidental loss while editing.

### Behavior

If the user changes a recipe and tries to leave without saving, show a confirmation.

Options:

```md
- Keep editing
- Discard changes
```

### Rules

```md
- Cancel should ask for confirmation if changes exist.
- Back navigation should ask for confirmation if changes exist.
- Leaving without changes should not show confirmation.
```

### Tasks

- Track initial editor state.
- Track current editor state.
- Detect unsaved changes.
- Add discard confirmation.
- Apply confirmation to cancel action.
- Apply confirmation to back navigation.

### Acceptance Criteria

- User is warned before losing unsaved changes.
- User can keep editing.
- User can discard changes.
- User is not warned when no changes exist.

## Epic 9: Recipe List and Detail Integration

### Goal

Make custom recipes behave like normal recipes everywhere else in the app.

### Required Behavior

After a custom recipe is saved:

```md
- It appears in the recipe list.
- It can be searched by name.
- It can be searched by ingredient.
- It can be filtered by makeability.
- It can be marked as favorite.
- It opens in recipe detail.
- Its servings can be scaled.
- Its ingredients participate in recipe matching.
```

### Tasks

- Connect saved custom recipes to recipe repository.
- Recalculate recipe match status for custom recipes.
- Include custom recipes in recipe list.
- Include custom recipes in search results.
- Include custom recipes in makeability filters.
- Confirm recipe detail works for custom recipes.

### Acceptance Criteria

- Custom recipes appear in normal recipe browsing.
- Custom recipes use inventory matching.
- Custom recipes use serving multiplier.
- Custom recipes can be favorited.
- Custom recipes can be edited from detail screen.

## Out of Scope for Bucket 4

Do not build these in this bucket:

```md
- Editing classic recipes directly
- Full ingredient management
- Deleting ingredients
- Editing substitute groups
- AI recipe generation
- Account creation
- Cloud backup
- Text message sharing
- Shopping list
- Barcode scanning
- Cost tracking
- Recipe import from websites
- Image upload
- Final visual design
```

## Checkpoint

Bucket 4 is complete when the app can:

```md
- Create a new custom recipe
- Duplicate a classic recipe into a custom recipe
- Duplicate a custom recipe
- Edit a custom recipe
- Delete a custom recipe after confirmation
- Add recipe ingredients
- Remove recipe ingredients
- Reorder recipe ingredients
- Create a basic missing ingredient from the recipe editor
- Choose measurement unit before entering quantity
- Add and reorder instruction steps
- Validate recipes before saving
- Warn before discarding unsaved changes
- Show saved custom recipes in the recipe list
- Match custom recipes against inventory
- Open custom recipes in recipe detail
- Scale custom recipe serving quantities
```

## Next Verification Point

Before starting Bucket 5, confirm what the next bucket should focus on:

```md
Option A: Shopping list
Option B: Substitute management screens
Option C: Account and cloud backup decision
Option D: Recipe sharing by text message
```
