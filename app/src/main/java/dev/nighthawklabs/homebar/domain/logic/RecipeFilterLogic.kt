package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.Ingredient
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
    ingredients: Collection<Ingredient> = emptyList(),
): List<RecipeWithMatchResult> {
    val ingredientNames = ingredients.associate { ingredient -> ingredient.id to ingredient.name }
    return recipes.filter { recipeWithMatch ->
        recipeWithMatch.matchesMakeabilityFilter(filterState.makeabilityFilter) &&
            recipeWithMatch.recipe.matchesSearch(filterState.searchText, ingredientNames) &&
            recipeWithMatch.recipe.matchesIngredientFilter(
                ingredientId = filterState.ingredientFilter,
                substitutionGroups = substitutionGroups,
            ) &&
            (!filterState.favoriteOnly || recipeWithMatch.recipe.isFavorite)
    }
}

private fun RecipeWithMatchResult.matchesMakeabilityFilter(
    filter: RecipeMakeabilityFilter,
): Boolean = when (filter) {
    RecipeMakeabilityFilter.MAKEABLE_NOW -> matchResult.status == RecipeMatchStatus.MAKEABLE
    RecipeMakeabilityFilter.MISSING_ONE_INGREDIENT -> matchResult.missingIngredientCount() == 1
    RecipeMakeabilityFilter.ALL_RECIPES -> true
}

private fun Recipe.matchesSearch(
    searchText: String,
    ingredientNames: Map<String, String>,
): Boolean {
    val query = searchText.trim()
    return query.isEmpty() || name.contains(query, ignoreCase = true) ||
        tags.any { tag -> tag.contains(query, ignoreCase = true) } ||
        ingredients.any { recipeIngredient ->
            ingredientNames[recipeIngredient.ingredientId]?.contains(query, ignoreCase = true) == true
        }
}

private fun Recipe.matchesIngredientFilter(
    ingredientId: String?,
    substitutionGroups: Collection<SubstitutionGroup>,
): Boolean = ingredientId == null || ingredients.any { recipeIngredient ->
    recipeIngredient.ingredientId == ingredientId || substitutionGroups.any { group ->
        group.containsBoth(recipeIngredient.ingredientId, ingredientId)
    }
}
