package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeListFilterState
import dev.nighthawklabs.homebar.domain.model.RecipeMakeabilityFilter
import dev.nighthawklabs.homebar.domain.model.RecipeMatchResult
import dev.nighthawklabs.homebar.domain.model.RecipeMatchStatus
import dev.nighthawklabs.homebar.domain.model.RecipeWithMatchResult
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup

fun RecipeMatchResult.missingIngredientCount(): Int = missingIngredients.size

/** Applies every active recipe-list filter; a recipe must satisfy all of them. */
fun filterRecipes(
    recipes: Collection<RecipeWithMatchResult>,
    filterState: RecipeListFilterState,
    substitutionGroups: Collection<SubstitutionGroup>,
): List<RecipeWithMatchResult> = recipes.filter { recipeWithMatch ->
    recipeWithMatch.matchesMakeabilityFilter(filterState.makeabilityFilter) &&
        recipeWithMatch.recipe.matchesSearch(filterState.searchText) &&
        recipeWithMatch.recipe.matchesIngredientFilter(
            ingredientId = filterState.ingredientFilter,
            substitutionGroups = substitutionGroups,
        ) &&
        (!filterState.favoriteOnly || recipeWithMatch.recipe.isFavorite)
}

private fun RecipeWithMatchResult.matchesMakeabilityFilter(
    filter: RecipeMakeabilityFilter,
): Boolean = when (filter) {
    RecipeMakeabilityFilter.MAKEABLE_NOW -> matchResult.status == RecipeMatchStatus.MAKEABLE
    RecipeMakeabilityFilter.MISSING_ONE_INGREDIENT -> matchResult.missingIngredientCount() == 1
    RecipeMakeabilityFilter.ALL_RECIPES -> true
}

private fun Recipe.matchesSearch(searchText: String): Boolean {
    val query = searchText.trim()
    return query.isEmpty() || name.contains(query, ignoreCase = true) ||
        tags.any { tag -> tag.contains(query, ignoreCase = true) }
}

private fun Recipe.matchesIngredientFilter(
    ingredientId: String?,
    substitutionGroups: Collection<SubstitutionGroup>,
): Boolean = ingredientId == null || ingredients.any { recipeIngredient ->
    recipeIngredient.ingredientId == ingredientId || substitutionGroups.any { group ->
        group.containsBoth(recipeIngredient.ingredientId, ingredientId)
    }
}
