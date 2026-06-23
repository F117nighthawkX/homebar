# Bucket 3: Inventory Management Screens

## Purpose

Build the screens that let the user manage what is currently in the home bar.

The user should be able to open the app, view inventory, search for an ingredient, mark it in stock, mark it out of stock, mark it running low, manage substitutes from an ingredient detail screen, and quickly see missing or low ingredients for favorite drinks.

This bucket connects directly to the app's main question:

> What drinks can I make right now with my current home bar inventory?

Bucket 3 assumes Bucket 1 provides the ingredient model and inventory state, and Bucket 2 uses that state to classify recipes.

## Locked Decisions From Earlier Buckets

- Inventory starts with `in stock`, `not in stock`, and `running low`.
- Running low ingredients still count as in stock.
- Recipe matching depends on inventory state.
- Approved substitutes count as valid available ingredients.
- Substitutes work in both directions by default.
- Substitute information should stay quiet in the inventory list.
- Substitute details and substitute management belong on ingredient detail.
- The recipe list owns recipe browsing and recipe filtering.
- Ingredient detail should link to recipe list with an ingredient filter instead of listing recipes directly.
- Inventory views should not list affected recipes directly.
- Garnishes, tools, and glassware do not block makeability in the first version.
- Bucket 2 default recipe filter is `Makeable now`.
- Do not build a dedicated shopping list.
- Inventory can show missing and running-low ingredients, but it should not manage purchases.
- Restock-focused views should stay read-only apart from normal inventory status controls.

## Epic 1: Inventory List Screen

### Goal

Create the main screen where the user manages consumable ingredients.

### Screen Content

Each inventory item should show:

```text
Inventory Item
- Ingredient name
- Category
- In-stock status
- Running-low status, if applicable
```

Example items:

```text
Tequila
Spirit
In stock

Angostura bitters
Bitters
In stock
Running low

Coke
Mixer
Not in stock

Pepsi
Mixer
In stock
```

### Rules

- Keep the list focused on inventory status.
- Do not show substitute group labels on every row.
- Substitute details and substitute management belong on the ingredient detail screen.
- Tools and glassware do not belong in this inventory list.

### Tasks

- Create inventory list screen.
- Load ingredients from local data.
- Display ingredient name.
- Display ingredient category.
- Display in-stock status.
- Display running-low status.
- Allow the user to tap an ingredient to open an ingredient detail screen.

### Acceptance Criteria

- User can view all consumable ingredients.
- User can tell which ingredients are in stock.
- User can tell which ingredients are not in stock.
- User can tell which ingredients are running low.
- User can open an ingredient detail screen.
- Inventory rows do not over-explain substitute relationships.

## Epic 2: Inventory Search

### Goal

Let the user quickly find an ingredient.

### Search Behavior

Search should match against:

- Ingredient name
- Ingredient category

Optional search support:

- Substitute ingredient names
- Substitute group name

Example searches:

```text
"tequila" finds tequila.
"bitters" finds Angostura bitters and orange bitters.
"cola" can find Coke and Pepsi if they belong to a Cola substitute group.
```

### Tasks

- Add search field to inventory list screen.
- Filter ingredient list based on search text.
- Combine search with the selected category filter.
- Combine search with the selected inventory status filter.
- Show empty state when no ingredients match.
- Clear search and return to the filtered inventory list.

### Acceptance Criteria

- User can search by ingredient name.
- User can search by category.
- Empty search results show a clear message.
- Clearing search restores the current filtered list.
- Substitute-aware search is allowed, but substitute labels should not clutter the default list.

## Epic 3: Inventory Category Filters

### Goal

Let the user narrow inventory by consumable ingredient type.

### Filter Options

The inventory screen should support:

- All
- Spirits
- Liqueurs
- Mixers
- Juices
- Syrups
- Bitters
- Garnishes
- Other

### Tasks

- Add category filter controls.
- Set default category filter to `All`.
- Combine category filter with search.
- Combine category filter with inventory status filters.
- Preserve selected category while the user stays on the inventory screen.

### Acceptance Criteria

- User can filter inventory by category.
- Search respects the selected category.
- Inventory status filters respect the selected category.
- User can return to `All`.
- Category filtering does not change inventory data.
- Tools and glassware are not included in these category filters.

## Epic 4: Quick Inventory Actions

### Goal

Let the user update inventory status without extra steps.

### Required Actions

From the inventory list, the user should be able to:

- Mark ingredient in stock
- Mark ingredient not in stock
- Mark ingredient running low
- Clear running low status

### Rules

- If an ingredient is marked not in stock, running low should be cleared.
- If an ingredient is marked running low, it should also be in stock.
- Running low means the ingredient can still be used for recipe matching.
- Recipe match status should be recalculated from current inventory after updates.

### Example

```text
Current state:
- Lime juice: in stock

User marks running low.

New state:
- Lime juice: in stock
- Lime juice: running low
```

```text
Current state:
- Coke: in stock
- Coke: running low

User marks not in stock.

New state:
- Coke: not in stock
- Coke: not running low
```

### Tasks

- Add in-stock toggle or action.
- Add running-low toggle or action.
- Apply inventory state rules.
- Save inventory status changes.
- Refresh recipe list and recipe detail data from current inventory.
- Refresh inventory status filters after inventory changes.

### Acceptance Criteria

- User can mark an ingredient in stock.
- User can mark an ingredient not in stock.
- User can mark an ingredient running low.
- User can clear running low status.
- Marking an ingredient not in stock clears running low.
- Marking an ingredient running low marks it in stock.
- Recipe matching updates after inventory changes.
- Inventory status filter results update after inventory changes.

## Epic 5: Ingredient Detail Screen

### Goal

Create a focused screen for a single ingredient.

The ingredient detail screen should manage the ingredient itself, manage substitutes, and route the user to the recipe list when they want to see drinks using that ingredient.

### Screen Content

The ingredient detail screen should show:

```text
- Ingredient name
- Category
- In-stock status
- Running-low status
- Substitute list, if applicable
- Add substitute action
- Remove substitute action, if substitutes exist
- Notes
- Link: View drinks using this ingredient
```

### Example

```text
Coke

Category:
Mixer

Inventory:
Not in stock

Substitutes:
- Pepsi [Remove]

Actions:
Add substitute
View drinks using this ingredient
```

### Recipe Search Link Behavior

Tapping `View drinks using this ingredient` should open the Recipe List screen with an ingredient filter applied.

For an ingredient filter, the recipe list should include recipes where the selected ingredient is relevant.

Example:

```text
Selected ingredient: Pepsi

Show:
- Recipes that directly call for Pepsi
- Recipes that call for Coke if Coke and Pepsi are approved substitutes
```

This keeps recipe browsing in one place.

### Tasks

- Create ingredient detail screen.
- Display ingredient data.
- Display inventory controls.
- Display substitute list, if applicable.
- Add `Add substitute` action.
- Add remove action for listed substitutes.
- Display notes field as read-only for now.
- Add `View drinks using this ingredient` action.
- Route to Recipe List with selected ingredient filter.
- Return to inventory list through normal back navigation.

### Acceptance Criteria

- User can view ingredient details.
- User can update inventory status from the detail screen.
- User can see substitute relationships without cluttering the inventory list.
- User can start the add-substitute flow from ingredient detail.
- User can remove a listed substitute from ingredient detail.
- User can tap `View drinks using this ingredient`.
- Tapping the link opens Recipe List with ingredient filter applied.
- Ingredient detail does not list recipes directly.

## Epic 6: Substitute Management From Ingredient Detail

### Goal

Let the user add and remove substitute relationships from an ingredient detail screen.

Substitutes are used by recipe matching. If a recipe calls for one ingredient and an approved substitute is in stock, the recipe should count as makeable.

### Entry Point

On the ingredient detail screen, show:

```text
Substitutes
- Add substitute
```

If substitutes already exist, show them in a simple list.

Example:

```text
Coke

Substitutes:
- Pepsi [Remove]

Add substitute
```

### Add Substitute Flow

When the user taps `Add substitute`, open a search flow.

The user should be able to:

```text
- Search existing ingredients
- Select one or more ingredients
- Add selected ingredients as substitutes
- Return to the ingredient detail screen
```

Example:

```text
Current ingredient:
Coke

User searches:
Pepsi

User selects:
Pepsi

Result:
Coke and Pepsi are now substitutes.
```

### Two-Way Behavior

Substitutes are two-way by default.

If the user adds Pepsi as a substitute for Coke:

```text
Coke detail shows:
- Pepsi

Pepsi detail shows:
- Coke
```

Recipe matching should also treat both directions as valid:

```text
Recipe calls for Coke.
Pepsi is in stock.
Coke is not in stock.

Result:
Recipe is makeable.
```

```text
Recipe calls for Pepsi.
Coke is in stock.
Pepsi is not in stock.

Result:
Recipe is makeable.
```

### Substitute Group Behavior

Substitutes should be stored as groups, not one-off directional links.

Example:

```text
Substitution group: Cola
- Coke
- Pepsi
```

If the user adds a substitute:

```text
Selected ingredient has no group.
Chosen substitute has no group.
```

Create a new group containing both ingredients.

```text
Selected ingredient has a group.
Chosen substitute has no group.
```

Add the chosen substitute to the selected ingredient's group.

```text
Selected ingredient has no group.
Chosen substitute has a group.
```

Add the selected ingredient to the chosen substitute's group.

```text
Selected ingredient has a group.
Chosen substitute has a different group.
```

For Bucket 3, do not automatically merge groups. Show a message explaining that both ingredients already belong to different substitute groups.

Suggested message:

```text
This ingredient already belongs to another substitute group. Group merging can be added later.
```

### Remove Substitute Flow

The ingredient detail screen should let the user remove an existing substitute.

Example:

```text
Coke

Substitutes:
- Pepsi [Remove]
```

If the user removes Pepsi from Coke's substitute list:

```text
- Pepsi is removed from Coke's substitute group.
- Coke no longer appears as a substitute on Pepsi's detail screen.
- Recipe matching updates immediately.
```

If removing a substitute leaves a group with only one ingredient, the group should be deleted.

### Search Rules

The substitute search should:

```text
- Search existing ingredients only
- Exclude the current ingredient
- Exclude ingredients already in the current ingredient's substitute group
- Show ingredient name and category
```

Quick-creating new ingredients from the substitute search is out of scope for this flow. Quick create belongs to the recipe editor flow.

### Recipe Matching Update

After adding or removing a substitute:

```text
- Recipe makeability should update.
- Recipe detail ingredient substitution display should update.
- Inventory status filters should continue to respect substitutes.
```

### Tasks

- Add substitute section to ingredient detail screen.
- Add add-substitute action.
- Add substitute search screen or modal.
- Search existing ingredients.
- Add selected ingredient to substitute group.
- Reflect substitute relationship on both ingredient detail screens.
- Add remove-substitute action.
- Delete empty substitute groups when needed.
- Recalculate recipe match status after substitute changes.
- Add unit tests for two-way substitute matching.
- Add unit tests for adding substitutes to groups.
- Add unit tests for removing substitutes from groups.

### Acceptance Criteria

- User can view substitutes from ingredient detail.
- User can search for an existing ingredient to add as a substitute.
- User can add a substitute from ingredient detail.
- Added substitutes appear on both ingredient detail screens.
- Added substitutes affect recipe matching.
- User can remove a substitute.
- Removed substitutes disappear from both ingredient detail screens.
- Removed substitutes no longer affect recipe matching.
- Search does not show the current ingredient as a substitute option.
- Search does not show ingredients already in the same substitute group.

## Epic 7: Inventory Status Filters

### Goal

Let the user quickly see inventory items by practical status without building a shopping list.

This epic replaces the earlier low-inventory-only view. The inventory screen should support filters for all missing ingredients, all running-low ingredients, and missing or low ingredients tied to favorited recipes.

### Filter Options

The inventory screen should support these status filters:

- All ingredients
- In stock
- Missing ingredients
- Running low
- Missing for favorite recipes
- Running low for favorite recipes
- Missing or running low for favorite recipes

### Rules

- `All ingredients` shows all consumable ingredients.
- `In stock` shows ingredients where `inStock = true`.
- `Missing ingredients` shows ingredients where `inStock = false`.
- `Running low` shows ingredients where `runningLow = true`.
- `Missing for favorite recipes` shows ingredients that block at least one favorited recipe from being makeable.
- `Running low for favorite recipes` shows in-stock ingredients marked running low that are used by at least one favorited recipe.
- `Missing or running low for favorite recipes` shows both groups in one view.
- Missing ingredient logic must respect approved substitutes.
- Inventory status filters should combine with search and category filters.
- These filters must not create a shopping list or purchase workflow.

### Missing Ingredient Logic

An ingredient is missing for a recipe only when:

```text
- the recipe requires that ingredient,
- the ingredient is not in stock,
- and no approved substitute is in stock.
```

Example:

```text
Recipe:
Cuba Libre requires Coke.

Inventory:
Coke is not in stock.
Pepsi is in stock.
Coke and Pepsi are approved substitutes.

Result:
Coke should not appear as missing for that recipe.
```

### Missing for Favorite Recipes

`Missing for favorite recipes` means ingredients that prevent at least one favorited recipe from being makeable.

Example:

```text
Favorite recipes:
- Margarita
- Old Fashioned

Inventory:
- Tequila: in stock
- Lime juice: not in stock
- Orange liqueur: in stock
- Agave syrup: in stock
- Bourbon: in stock
- Angostura bitters: in stock

Result:
Missing for favorite recipes:
- Lime juice
```

If an approved substitute is available, the ingredient should not appear as missing.

### Running Low for Favorite Recipes

`Running low for favorite recipes` means in-stock ingredients marked running low that are used by at least one favorited recipe.

Example:

```text
Favorite recipe:
Old Fashioned

Inventory:
- Bourbon: in stock
- Angostura bitters: in stock, running low
- Simple syrup: in stock

Result:
Running low for favorite recipes:
- Angostura bitters
```

For this filter, include ingredients that are directly listed in favorited recipes. Do not add every possible substitute unless that substitute is actually being used to make a favorited recipe available.

### Combined Favorite Restock Filter

`Missing or running low for favorite recipes` should show ingredients that are either:

```text
- missing and blocking at least one favorited recipe, or
- running low and used by at least one favorited recipe.
```

Example:

```text
Favorite recipes:
- Margarita
- Old Fashioned

Inventory:
- Tequila: in stock
- Lime juice: not in stock
- Orange liqueur: in stock
- Agave syrup: in stock
- Bourbon: in stock
- Angostura bitters: running low

Result:
Missing or running low for favorite recipes:
- Lime juice
- Angostura bitters
```

### Screen Behavior

Each filtered inventory row should keep the same available actions:

```text
- Open ingredient detail
- Mark in stock
- Mark not in stock
- Mark running low
- Clear running low
```

The filtered inventory view should not show affected recipe lists. To view drinks using an ingredient, the user should open ingredient detail and use the `View drinks using this ingredient` action.

### Tasks

- Add inventory status filter state.
- Add filter control for inventory status.
- Implement `All ingredients` filter.
- Implement `In stock` filter.
- Implement `Missing ingredients` filter.
- Implement `Running low` filter.
- Implement `Missing for favorite recipes` filter.
- Implement `Running low for favorite recipes` filter.
- Implement `Missing or running low for favorite recipes` filter.
- Combine status filter with search.
- Combine status filter with category filter.
- Show empty state when a filter has no matching ingredients.
- Confirm missing ingredient logic respects approved substitutes.
- Confirm no shopping-list-specific UI is introduced.

### Acceptance Criteria

- User can filter inventory to all ingredients.
- User can filter inventory to in-stock ingredients.
- User can filter inventory to missing ingredients.
- User can filter inventory to running-low ingredients.
- User can filter inventory to missing ingredients for favorite recipes.
- User can filter inventory to running-low ingredients for favorite recipes.
- User can filter inventory to ingredients that are missing or running low for favorite recipes.
- Missing ingredient logic respects approved substitutes.
- Search works with inventory status filters.
- Category filtering works with inventory status filters.
- Empty filter results show a clear message.
- Filtered rows keep normal inventory actions.
- Inventory status filters do not create shopping list behavior.

## Epic 8: Inventory-to-Recipe Refresh

### Goal

Make inventory and substitute changes visible in recipe recommendations and inventory status filters.

### Required Behavior

When the user changes inventory state or substitute relationships:

- Recipe makeability should update.
- Running low warnings should update.
- Missing ingredient counts should update.
- Substitute-based availability should update as part of normal makeability.
- Inline substitution display should update on recipe detail.
- Inventory status filter results should update.
- Favorite-recipe inventory filters should update.

### Example

```text
Before:
Cuba Libre
Missing 1 ingredient: Coke

Inventory change:
Pepsi marked in stock.
Coke and Pepsi are in the Cola substitute group.

After:
Cuba Libre
Makeable
```

Inside recipe detail, the ingredient list should show:

```text
- Pepsi
  Substitute for: Coke
```

For favorite-recipe inventory filters:

```text
Before:
Favorite recipe Margarita is blocked by missing lime juice.
Missing for favorite recipes shows lime juice.

Inventory change:
Lime juice marked in stock.

After:
Margarita is makeable.
Missing for favorite recipes no longer shows lime juice.
```

For substitute changes:

```text
Before:
Cuba Libre requires Coke.
Coke is not in stock.
Pepsi is in stock.
Cuba Libre is missing 1 ingredient.

Substitute change:
User adds Pepsi as a substitute for Coke.

After:
Cuba Libre is makeable.
Recipe detail shows Pepsi as the ingredient used in place of Coke.
```

### Tasks

- Connect inventory updates to repository or state holder.
- Connect substitute updates to repository or state holder.
- Recalculate recipe match status from current inventory and substitutes after changes.
- Confirm recipe list uses the latest inventory and substitute state.
- Confirm recipe detail uses the latest inventory and substitute state.
- Confirm ingredient-filtered recipe list uses the latest inventory and substitute state.
- Confirm inventory status filters use the latest inventory and substitute state.
- Confirm favorite-recipe inventory filters use the latest inventory and substitute state.

### Acceptance Criteria

- Inventory changes affect recipe list results.
- Inventory changes affect recipe detail status.
- Substitute changes affect recipe list results.
- Substitute changes affect recipe detail status.
- Substitute-based makeability updates after inventory or substitute changes.
- Running low warnings update after inventory changes.
- Inventory status filters update after inventory or substitute changes.
- Favorite-recipe inventory filters update after inventory or substitute changes.
- Recipe match status is calculated from current inventory and substitutes and not stored as stale state.

## Out of Scope for Bucket 3

These should not be built in this bucket:

- Adding new ingredients from scratch
- Editing ingredient names or categories
- Deleting ingredients
- Naming substitute groups
- Merging separate substitute groups
- Creating new ingredients from substitute search
- Advanced substitute rules
- One-way substitutes
- Dedicated shopping list screen
- Purchase tracking
- Needed purchase quantities
- Store grouping
- Recipe editor
- Account creation
- Cloud backup
- AI recipe generation
- Barcode scanning
- Cost tracking
- Precise bottle volume tracking
- Final visual design
- Tools and glassware management

## Checkpoint

Bucket 3 is complete when the app can:

- Show an inventory list
- Search inventory
- Filter inventory by category
- Filter inventory by inventory status
- Filter inventory to missing ingredients
- Filter inventory to running-low ingredients
- Filter inventory to missing ingredients for favorite recipes
- Filter inventory to running-low ingredients for favorite recipes
- Filter inventory to ingredients that are missing or running low for favorite recipes
- Open ingredient details
- Mark ingredients in stock
- Mark ingredients not in stock
- Mark ingredients running low
- Clear running low status
- Show substitutes on ingredient detail
- Add substitutes from ingredient detail by searching existing ingredients
- Remove substitutes from ingredient detail
- Reflect substitute changes on both ingredient detail screens
- Apply substitute changes to recipe makeability
- Link from ingredient detail to recipe list with ingredient filter applied
- Refresh recipe makeability after inventory changes
- Refresh recipe makeability after substitute changes
- Refresh inventory status filters after inventory or substitute changes

## Next Verification Point

Before starting the next bucket, confirm what the next bucket should focus on:

- Option A: Account and cloud backup decision
- Option B: Recipe sharing by text message
- Option C: AI recipe helper
- Option D: App settings and preferences
