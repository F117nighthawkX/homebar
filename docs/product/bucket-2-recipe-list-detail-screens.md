# Bucket 2: Recipe List and Recipe Detail Screens

## Purpose

Build the first usable recipe browsing flow.

The user should be able to open the app, see which drinks are available, filter the list, open a recipe, adjust the number of drinks, and read the ingredients and steps without digging through menus.

This bucket assumes Bucket 1 provides inventory data, recipe data, match status, substitute details, running low warnings, favorites, and serving multiplier logic.

## Locked Decisions From Earlier Buckets

- Serving multiplier changes displayed ingredient quantities only.
- Serving multiplier does not change the saved base recipe.
- Running low ingredients still count as in stock.
- Approved substitutes count as valid available ingredients.
- The recipe list should not separate drinks that use substitutes.
- Substituted ingredients should be marked inline on the recipe detail screen.
- Inventory tracking starts with `in stock`, `not in stock`, and `running low`.
- Garnishes, tools, and glassware do not block makeability in the first version.
- The default recipe filter is `Makeable now`.
- The app should stay centered on this question: What drinks can I make right now?

## Epic 1: Recipe List Screen

### Goal

Create the main screen where the user browses drinks.

### Screen Content

Each recipe card should show:

```text
Recipe Card
- Recipe name
- Makeability status
- Favorite indicator
- Short ingredient summary
- Missing ingredient count, if relevant
- Running low warning, if relevant
```

Example cards:

```text
Margarita
Makeable
Tequila, lime juice, orange liqueur, agave syrup

Whiskey Sour
Missing 1 ingredient
Missing: lemon juice

Old Fashioned
Makeable
Low: Angostura bitters
```

### Makeability Status Labels

Use simple labels:

- Makeable
- Missing 1
- Missing 2+

A recipe that uses an approved substitute still appears as `Makeable`.

### Tasks

- Create recipe list screen.
- Load recipes from sample recipe data.
- Load match status from recipe matching logic.
- Display recipe cards.
- Show favorite state on each card.
- Show running low warning when a required ingredient is marked running low.
- Show missing ingredient count when the recipe cannot be made.
- Open recipe detail screen when the user taps a recipe.

### Acceptance Criteria

- User can see a list of recipes.
- User can tell which recipes are makeable.
- User can tell which recipes are missing ingredients.
- User can tell when a makeable recipe uses an ingredient that is running low.
- Substitute-based recipes appear as makeable.
- Tapping a recipe opens the detail screen.

## Epic 2: Recipe Filters

### Goal

Let the user narrow the recipe list without extra setup.

### Filter Options

The list screen should support:

- Makeable now
- Missing 1 ingredient
- All recipes
- Favorites

### Filter Behavior

```text
Makeable now
Recipes where every required ingredient is satisfied by direct inventory or an approved substitute.

Missing 1 ingredient
Recipes where exactly one required ingredient is unavailable and has no available substitute.

All recipes
Every recipe, regardless of inventory status.

Favorites
Recipes marked as favorite.
```

### Default Filter

The default filter is:

```text
Makeable now
```

### Tasks

- Add filter controls to recipe list screen.
- Set default filter to `Makeable now`.
- Apply selected filter to recipe list.
- Preserve selected filter while the user stays on the recipe list screen.
- Show an empty state if a filter has no matching recipes.

### Acceptance Criteria

- User can switch between recipe filters.
- Recipe list updates when the filter changes.
- Empty filter results show a clear message.
- The default filter shows drinks that can be made now using direct ingredients or approved substitutes.

## Epic 3: Recipe Search

### Goal

Let the user quickly find a known recipe.

### Search Behavior

Search should match against:

- Recipe name
- Recipe tags
- Ingredient names

Example searches:

```text
"margarita" finds Margarita.
"tequila" finds recipes that use tequila.
"sour" finds Whiskey Sour and other sour-style drinks.
```

### Tasks

- Add search field to recipe list screen.
- Filter recipe list based on search text.
- Combine search with the selected makeability filter.
- Show empty state when no recipe matches the search and filter.

### Acceptance Criteria

- User can search by recipe name.
- User can search by ingredient.
- User can search by tag.
- Search respects the selected filter.
- Clearing search returns the filtered recipe list.

## Epic 4: Explicit Ingredient Filter

### Goal

Allow other screens to open the recipe list with an ingredient filter already applied.

This supports flows like:

```text
Ingredient Detail
→ View drinks using this ingredient
→ Recipe List with ingredient filter applied
```

### Filter Behavior

The ingredient filter should match recipes where the selected ingredient is relevant.

For a selected ingredient, include recipes where:

- The recipe directly lists that ingredient.
- The selected ingredient can satisfy a required ingredient through an approved substitute relationship.

Example:

```text
Selected ingredient: Pepsi

Show:
- Recipes that directly call for Pepsi
- Recipes that call for Coke if Coke and Pepsi are approved substitutes
```

### Combination Rule

Recipe list results must satisfy all active filters:

- selected makeability filter
- search text, if present
- ingredient filter, if present
- favorite filter, if active

Example:

```text
Makeable now + Ingredient: tequila
```

Shows makeable drinks that use tequila directly or through a valid substitute relationship.

### Tasks

- Add ingredient filter state to recipe list.
- Support navigation argument or equivalent app state for ingredient filter.
- Display active ingredient filter near the search or filter controls.
- Add action to clear the active ingredient filter.
- Combine ingredient filter with makeability filter.
- Combine ingredient filter with search.
- Combine ingredient filter with favorites.

### Acceptance Criteria

- Another screen can open the recipe list with an ingredient filter applied.
- User can tell when an ingredient filter is active.
- User can clear the ingredient filter.
- Ingredient filter respects substitute relationships.
- Ingredient filter combines with the current makeability filter.

## Epic 5: Recipe Detail Screen

### Goal

Create a readable recipe screen that helps the user make the drink.

### Screen Content

The recipe detail screen should show:

```text
- Recipe name
- Favorite toggle
- Makeability status
- Serving count selector
- Scaled ingredient list
- Inline substitution markers
- Running low warnings
- Missing ingredients
- Instructions
- Glassware
- Tools
- Garnish
- Tags
```

### Example: Makeable Recipe

```text
Margarita

Status: Makeable
Servings: 1 [-] [+]

Ingredients:
- 2 oz tequila
- 1 oz lime juice
- 0.75 oz orange liqueur
- 0.5 oz agave syrup

Instructions:
1. Add ingredients to shaker with ice.
2. Shake.
3. Strain into rocks glass.
4. Garnish with lime.

Glassware:
- Rocks glass

Tools:
- Shaker
- Jigger
- Strainer

Garnish:
- Lime wedge
- Salt rim
```

### Example: Recipe Using Substitute

```text
Cuba Libre

Status: Makeable
Servings: 1 [-] [+]

Ingredients:
- 2 oz rum
- 4 oz Pepsi
  Substitute for: Coke
- 0.5 oz lime juice
```

### Tasks

- Create recipe detail screen.
- Display selected recipe data.
- Display makeability status.
- Display scaled ingredient quantities.
- Add serving count control.
- Show substituted ingredients inline.
- Show running low warnings.
- Show missing ingredients.
- Show instructions.
- Show glassware, tools, garnish, and tags.
- Add favorite toggle.

### Acceptance Criteria

- User can open a recipe.
- User can read ingredients and instructions.
- User can increase or decrease serving count.
- Ingredient quantities update when serving count changes.
- Saved recipe quantities do not change.
- User can see substituted ingredients inline.
- User can see running low warnings.
- User can favorite or unfavorite a recipe.

## Epic 6: Detail Screen States

### Goal

Make each recipe status understandable on the detail screen.

### Makeable Recipe

Show:

```text
Status: Makeable
```

### Makeable Recipe With Inline Substitute

Show the recipe as makeable, then mark substituted ingredients in the ingredient list.

```text
Status: Makeable

Ingredients:
- 4 oz Pepsi
  Substitute for: Coke
```

### Missing Ingredient

Show:

```text
Status: Missing ingredients

Missing:
- Lemon juice
```

### Running Low

Show:

```text
Low inventory:
- Angostura bitters
```

### Tasks

- Add status section to detail screen.
- Add inline substitution display for substituted ingredients.
- Add missing ingredients section when ingredients are missing.
- Add running low section when required ingredients are low.
- Avoid blocking the recipe because of missing tools or glassware.

### Acceptance Criteria

- User can tell whether the drink can be made.
- User can tell what substitute is being used without leaving the ingredient list.
- User can tell what ingredient is missing.
- User can tell what required ingredient is running low.

## Epic 7: Basic Navigation

### Goal

Support the recipe browsing flow without designing the whole app shell yet.

### Flow

```text
Recipe List Screen
→ Recipe Detail Screen
→ Back to Recipe List Screen
```

Additional flow:

```text
Other Screen
→ Recipe List Screen with ingredient filter applied
```

### Tasks

- Add navigation from recipe list to recipe detail.
- Add back navigation.
- Preserve current filter when returning to recipe list.
- Preserve current search text when returning to recipe list.
- Preserve active ingredient filter when returning to recipe list.
- Reset recipe serving count when opening a recipe unless later persistence is added.
- Support route or app state for opening recipe list with an ingredient filter.

### Acceptance Criteria

- User can move from list to detail.
- User can return to the list.
- Returning to the list keeps the same filter.
- Returning to the list keeps the same search text.
- Returning to the list keeps the same ingredient filter.
- Opening a recipe starts with the base serving count.
- Other screens can route to the recipe list with an ingredient filter.

## Out of Scope for Bucket 2

These should not be built in this bucket:

- Full inventory management screen
- Recipe editor screen
- Account creation
- Cloud backup
- AI recipe generation
- Text message sharing
- Shopping list screen
- Barcode scanning
- Cost tracking
- App store release work
- Final visual design
- Substitute management screens

## Checkpoint

Bucket 2 is complete when the app can:

- Show a recipe list
- Default to `Makeable now`
- Filter recipes by makeability
- Search recipes by name, tag, or ingredient
- Apply an explicit ingredient filter from another screen
- Open a recipe detail screen
- Show scaled ingredient quantities
- Show substituted ingredients inline
- Show running low warnings
- Show missing ingredients
- Favorite and unfavorite recipes
- Navigate back to the recipe list without losing filter, search, or ingredient filter state

## Next Verification Point

Before starting Bucket 3, confirm:

- The recipe list should keep substitutes quiet.
- Substitute information belongs inline on the recipe detail screen.
- Ingredient detail should link to recipe list with an ingredient filter rather than listing recipes itself.
- Low inventory should link to recipe list with an ingredient filter rather than listing affected recipes itself.
