package dev.nighthawklabs.homebar.ui.recipes.list

import dev.nighthawklabs.homebar.domain.logic.matchRecipe
import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeMatchStatus
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup

data class RecipeListUiState(
    val recipes: List<RecipeListItem> = emptyList(),
)

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
    val ingredientNames = ingredients.associate { ingredient -> ingredient.id to ingredient.name }

    return recipes.map { recipe ->
        val matchResult = matchRecipe(recipe, ingredients, substitutionGroups)
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

private fun RecipeMatchStatus.toListLabel(missingIngredientCount: Int): String = when (this) {
    RecipeMatchStatus.MAKEABLE -> "Makeable"
    RecipeMatchStatus.MISSING_INGREDIENTS -> if (missingIngredientCount == 1) "Missing 1" else "Missing 2+"
}

private fun Map<String, String>.labelFor(ingredientId: String): String =
    get(ingredientId) ?: ingredientId.replace('-', ' ')
