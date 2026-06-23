# Bucket 1: Inventory, Recipes, and Makeability Logic

## Purpose

Build the core logic that answers the app’s main question:

> What drinks can I make right now with my current home bar inventory?

This bucket should prove the app’s data model and matching rules before polished screens, accounts, sharing, AI, or app store work.

## Locked Decisions

- Serving multiplier changes displayed ingredient quantities only.
- Serving multiplier does not change the saved base recipe.
- Running low ingredients still count as in stock.
- Substitutes work in both directions by default.
- A substitute counts as a valid available ingredient for recipe matching.
- Inventory tracking starts simple: in stock, not in stock, and running low.
- Recipe match status should describe availability only. UI filters are handled separately.
- The first goal is accurate recipe matching, not polished UI.

## Epic 1: Consumable Ingredient Inventory

### Goal

Create a simple inventory model that tracks what the user has available.

### Data Model

Each ingredient should support:

```text
Ingredient
- id
- name
- category
- inStock
- runningLow
- notes
```

Suggested consumable categories:

- spirit
- liqueur
- mixer
- juice
- syrup
- bitters
- garnish
- other

### Rules

- `inStock = true` means the ingredient can be used for recipe matching.
- `runningLow = true` does not block recipe matching.
- An ingredient can be both `inStock = true` and `runningLow = true`.
- If `inStock = false`, the ingredient should not count as available unless an in-stock substitute exists.
- Tools, glassware, and ice types are not consumable ingredients.
- Tools, glassware, and ice types should not affect makeability in the first version.

### Tasks

- Create ingredient data type.
- Create sample inventory data.
- Add ability to mark ingredient as in stock.
- Add ability to mark ingredient as not in stock.
- Add ability to mark ingredient as running low.
- Add ability to remove running low status.

### Acceptance Criteria

- User can view a list of ingredients.
- User can tell which ingredients are in stock.
- User can tell which ingredients are running low.
- Running low ingredients still count as usable in recipe matching.
- Tools and glassware do not affect recipe makeability.

## Epic 2: Recipe Model

### Goal

Create a recipe structure that can support classic recipes, custom recipes, scaled servings, tools, glassware, garnish, and favorites.

### Data Model

Each recipe should support:

```text
Recipe
- id
- name
- baseServingCount
- ingredients
- instructions
- glassware
- tools
- garnish
- tags
- isFavorite
- isCustom
```

Each recipe ingredient should support:

```text
RecipeIngredient
- ingredientId
- quantity
- unit
- note
```

Example:

```text
Recipe: Margarita

Base serving count: 1

Ingredients:
- 2 oz tequila
- 1 oz lime juice
- 0.75 oz orange liqueur
- 0.5 oz agave syrup

Glassware:
- rocks glass

Tools:
- shaker
- jigger
- strainer

Garnish:
- lime wedge
- salt rim
```

### Rules

- Classic recipes can be included as starter data.
- Custom recipes should use the same structure as classic recipes.
- A copied classic recipe becomes a custom recipe.
- Editing a copied recipe should not change the original recipe.
- Glassware and tools should be displayed as recipe information, not consumed by matching logic.

### Tasks

- Create recipe data type.
- Create recipe ingredient data type.
- Add sample recipes.
- Add favorite flag.
- Add custom recipe flag.
- Add duplicate recipe behavior.
- Confirm duplicated recipe can be edited separately from original.

### Acceptance Criteria

- App can store recipe ingredients with quantities and units.
- App can distinguish classic recipes from custom recipes.
- App can duplicate a recipe.
- App can edit the duplicate without changing the original.
- App can show tools and glassware without treating them as required inventory.

## Epic 3: Serving Multiplier

### Goal

Let the user scale a recipe based on the number of drinks being made.

### Rule

The saved recipe does not change. The displayed quantities change.

Formula:

```text
displayed quantity = base quantity × selected drink count
```

Example:

```text
Margarita, 1 drink
- 2 oz tequila
- 1 oz lime juice
- 0.75 oz orange liqueur
- 0.5 oz agave syrup
```

If the user selects 4 drinks:

```text
Margarita, 4 drinks
- 8 oz tequila
- 4 oz lime juice
- 3 oz orange liqueur
- 2 oz agave syrup
```

### Tasks

- Add selected serving count to recipe view state.
- Default selected serving count to recipe base serving count.
- Add increase serving count action.
- Add decrease serving count action.
- Calculate displayed quantities from base quantities.
- Prevent serving count from going below 1.

### Acceptance Criteria

- User can change recipe serving count.
- Ingredient quantities update correctly.
- Saved base recipe stays unchanged.
- Returning to the recipe starts from the base serving count unless a later bucket adds persistence.

## Epic 4: Ingredient Substitutes

### Goal

Allow approved substitutes to count as valid available ingredients during recipe matching.

### Product Rule

A substitute is not a second-class ingredient. For recipe matching, an approved in-stock substitute satisfies the required ingredient.

The recipe list should not create a separate substitute category or over-explain substitute usage. The recipe detail screen can show substituted ingredients inline so the user knows what they are making.

### Data Model

Substitutes should be stored as groups.

```text
SubstitutionGroup
- id
- name
- ingredientIds
```

Example:

```text
Substitution group: Cola
- Coke
- Pepsi
```

### Matching Rule

Substitutes are two-way by default.

```text
If a recipe needs Ingredient A,
and Ingredient A is not in stock,
but Ingredient B is in the same substitute group and is in stock,
then Ingredient B satisfies the recipe requirement.
```

Example:

```text
Recipe: Cuba Libre

Required:
- rum
- Coke
- lime juice

Inventory:
- rum: in stock
- Coke: not in stock
- Pepsi: in stock
- lime juice: in stock

Result:
Makeable

Substitution detail:
Use Pepsi for Coke.
```

### Tasks

- Create substitution group data type.
- Add sample substitution group.
- Add function to check whether one ingredient can substitute for another.
- Add match result data that records which substitute is being used.
- Make substitute matching two-way within the group.

### Acceptance Criteria

- App can define Coke and Pepsi as substitutes.
- Recipe requiring Coke can match against Pepsi.
- Recipe requiring Pepsi can match against Coke.
- Recipe is treated as makeable when an approved substitute is available.
- App can pass substitution details to the recipe detail screen.

## Epic 5: Recipe Match Result

### Goal

Classify every recipe based on the user’s current inventory.

### Match Statuses

Use only these match statuses:

```text
Makeable
All required ingredients are satisfied by direct inventory or approved substitutes.

Missing ingredients
One or more required ingredients are missing and have no available substitute.
```

### Match Result Data

The match result should still carry detail data for the UI.

```text
RecipeMatchResult
- status
- missingIngredients
- substitutionsUsed
- runningLowIngredients
```

### Rules

- Running low ingredients still count as in stock.
- An approved in-stock substitute satisfies a missing required ingredient.
- If more than one substitute exists, the app can choose the first available substitute for now.
- Missing garnish should count as missing only if the garnish is modeled as a required consumable ingredient.
- Missing tools or glassware should not block makeability in the first version.
- Recipe match status should be calculated from current inventory. Do not store stale match status as manual state.

### Tasks

- Write recipe matching function.
- Check each required ingredient against inventory.
- Check substitutes when required ingredient is not in stock.
- Return match status.
- Return missing ingredients.
- Return substitution details.
- Return running low warnings.

### Acceptance Criteria

- App can identify makeable recipes.
- App can identify recipes with missing ingredients.
- App treats approved substitutes as valid available ingredients.
- App returns substitution details without making substitute usage a separate recipe status.
- App returns running low warnings without blocking the recipe.

## Epic 6: Recipe Filters Needed by Logic

### Goal

Define the recipe filter data needed by the UI, without building the UI yet.

### Filter State

The recipe list should eventually support this filter state:

```text
RecipeListFilterState
- makeabilityFilter
- searchText
- ingredientFilter
- favoriteOnly
```

### Makeability Filters

Recommended filters:

```text
Makeable now
Recipes where every required ingredient is satisfied by direct inventory or approved substitutes.

Missing 1 ingredient
Recipes where exactly one required ingredient is unavailable and has no available substitute.

All recipes
Every recipe, regardless of inventory status.

Favorites
Recipes marked as favorite.
```

### Combination Rule

Recipe list results should satisfy all active filters:

- selected makeability filter
- search text, if present
- ingredient filter, if present
- favorite filter, if active

Example:

```text
Makeable now + Ingredient: tequila
```

This should show makeable drinks that use tequila directly or through a valid substitute relationship.

### Tasks

- Define filter state model.
- Define makeability filter enum or equivalent model.
- Define helper logic to check missing ingredient count.
- Define helper logic for ingredient-based filtering.

### Acceptance Criteria

- Domain logic can support the planned recipe list filters.
- Substitute-based availability is included in `Makeable now`.
- Ingredient filtering can be set explicitly by another screen later.

## Out of Scope for Bucket 1

These should not be built in this bucket:

- Account creation
- Email login
- Cloud backup
- AI recipe generation
- Text message recipe sharing
- Shopping list screen
- Barcode scanning
- Cost tracking
- Precise bottle volume tracking
- Full inventory management UI
- App store release work
- Polished visual design
- Substitute management screens

## Checkpoint

Bucket 1 is complete when the app can:

- Store consumable ingredients
- Mark ingredients in stock or not in stock
- Mark ingredients as running low
- Store recipes
- Duplicate and edit recipes at the model level
- Scale recipe quantities by serving count
- Define two-way substitutes
- Treat approved substitutes as valid available ingredients
- Classify recipes as makeable or missing ingredients
- Return missing ingredient data
- Return substitution details
- Return running low warnings
- Support recipe filter state needed by later screens

## Next Verification Point

Before starting Bucket 2, confirm:

- Substitutes still count as valid ingredients for makeability.
- Recipe list should not split substitute-based drinks into a separate status.
- Recipe detail should show substituted ingredients inline.
- `Makeable now` should include drinks made possible by approved substitutes.
