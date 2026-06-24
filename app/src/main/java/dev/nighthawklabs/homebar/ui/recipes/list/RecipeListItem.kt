package dev.nighthawklabs.homebar.ui.recipes.list

import dev.nighthawklabs.homebar.domain.logic.matchRecipe
import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeListFilterState
import dev.nighthawklabs.homebar.domain.model.RecipeMakeabilityFilter
import dev.nighthawklabs.homebar.domain.model.RecipeMatchStatus
import dev.nighthawklabs.homebar.domain.model.RecipeWithMatchResult
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup

data class RecipeListUiState(
    val recipes: List<RecipeListItem> = emptyList(),
    val selectedFilter: RecipeListFilterOption = RecipeListFilterOption.MAKEABLE_NOW,
    val searchText: String = "",
    val activeIngredientName: String? = null,
)

fun RecipeListUiState.emptyStateMessage(): String? =
    if (recipes.isNotEmpty()) {
        null
    } else if (searchText.isBlank() && activeIngredientName == null) {
        "No recipes match the ${selectedFilter.label.lowercase()} filter."
    } else if (searchText.isBlank()) {
        "No recipes use $activeIngredientName with the ${selectedFilter.label.lowercase()} filter."
    } else if (activeIngredientName == null) {
        "No recipes match your search with the ${selectedFilter.label.lowercase()} filter."
    } else {
        "No recipes match your search and $activeIngredientName with the " +
            "${selectedFilter.label.lowercase()} filter."
    }

enum class RecipeListFilterOption(val label: String) {
    MAKEABLE_NOW("Makeable now"),
    MISSING_ONE_INGREDIENT("Missing 1 ingredient"),
    ALL_RECIPES("All recipes"),
    FAVORITES("Favorites"),
}

/** Presentation data for one recipe card. Match data is calculated from current inventory. */
data class RecipeListItem(
    val id: String,
    val name: String,
    val isFavorite: Boolean,
    val ingredientSummary: String,
    val makeabilityLabel: String,
    val missingIngredientNames: List<String>,
    val runningLowIngredientNames: List<String>,
)

fun createRecipeListItems(
    recipes: List<Recipe>,
    ingredients: List<Ingredient>,
    substitutionGroups: List<SubstitutionGroup>,
): List<RecipeListItem> {
    val recipeMatches = recipes.map { recipe ->
        RecipeWithMatchResult(
            recipe = recipe,
            matchResult = matchRecipe(recipe, ingredients, substitutionGroups),
        )
    }

    return createRecipeListItems(recipeMatches, ingredients)
}

fun createRecipeListItems(
    recipeMatches: List<RecipeWithMatchResult>,
    ingredients: List<Ingredient>,
): List<RecipeListItem> {
    val ingredientNames = ingredients.associate { ingredient -> ingredient.id to ingredient.name }

    return recipeMatches.map { recipeWithMatch ->
        val recipe = recipeWithMatch.recipe
        val matchResult = recipeWithMatch.matchResult
        RecipeListItem(
            id = recipe.id,
            name = recipe.name,
            isFavorite = recipe.isFavorite,
            ingredientSummary = recipe.ingredients.joinToString { recipeIngredient ->
                ingredientNames.labelFor(recipeIngredient.ingredientId)
            },
            makeabilityLabel = matchResult.status.toListLabel(matchResult.missingIngredients.size),
            missingIngredientNames = matchResult.missingIngredients.map(ingredientNames::labelFor),
            runningLowIngredientNames = matchResult.runningLowIngredients.map(ingredientNames::labelFor),
        )
    }
}

fun RecipeListFilterOption.toFilterState(
    searchText: String = "",
    ingredientId: String? = null,
): RecipeListFilterState = when (this) {
    RecipeListFilterOption.MAKEABLE_NOW -> RecipeListFilterState(
        searchText = searchText,
        ingredientFilter = ingredientId,
    )
    RecipeListFilterOption.MISSING_ONE_INGREDIENT -> RecipeListFilterState(
        makeabilityFilter = RecipeMakeabilityFilter.MISSING_ONE_INGREDIENT,
        searchText = searchText,
        ingredientFilter = ingredientId,
    )
    RecipeListFilterOption.ALL_RECIPES -> RecipeListFilterState(
        makeabilityFilter = RecipeMakeabilityFilter.ALL_RECIPES,
        searchText = searchText,
        ingredientFilter = ingredientId,
    )
    RecipeListFilterOption.FAVORITES -> RecipeListFilterState(
        makeabilityFilter = RecipeMakeabilityFilter.ALL_RECIPES,
        searchText = searchText,
        ingredientFilter = ingredientId,
        favoriteOnly = true,
    )
}

private fun RecipeMatchStatus.toListLabel(missingIngredientCount: Int): String = when (this) {
    RecipeMatchStatus.MAKEABLE -> "Makeable"
    RecipeMatchStatus.MISSING_INGREDIENTS -> if (missingIngredientCount == 1) "Missing 1" else "Missing 2+"
}

private fun Map<String, String>.labelFor(ingredientId: String): String =
    get(ingredientId) ?: ingredientId.replace('-', ' ')
