package dev.nighthawklabs.homebar.ui.recipes.detail

import dev.nighthawklabs.homebar.domain.logic.RecipeServingState
import dev.nighthawklabs.homebar.domain.logic.matchRecipe
import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeMatchStatus
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val selectedServingCount: Int = 0,
    val matchStatus: RecipeMatchStatus? = null,
    val ingredientLines: List<RecipeDetailIngredientLine> = emptyList(),
    val missingIngredientNames: List<String> = emptyList(),
    val runningLowIngredientNames: List<String> = emptyList(),
) {
    val canDuplicate: Boolean = recipe != null
    val canEdit: Boolean = recipe?.isCustom == true
    val canDelete: Boolean = recipe?.isCustom == true
}

data class RecipeDetailIngredientLine(
    val quantity: Double,
    val unit: String,
    val ingredientName: String,
    val note: String,
    val substituteForName: String? = null,
)

fun createRecipeDetailUiState(
    servingState: RecipeServingState?,
    ingredients: List<Ingredient>,
    substitutionGroups: List<SubstitutionGroup>,
): RecipeDetailUiState {
    val recipe = servingState?.recipe ?: return RecipeDetailUiState()
    val matchResult = matchRecipe(recipe, ingredients, substitutionGroups)
    val ingredientNames = ingredients.associate { ingredient -> ingredient.id to ingredient.name }
    val substitutionsByRequiredIngredient = matchResult.substitutionsUsed.associateBy {
        it.requiredIngredientId
    }

    return RecipeDetailUiState(
        recipe = recipe,
        selectedServingCount = servingState.selectedServingCount,
        matchStatus = matchResult.status,
        ingredientLines = servingState.displayedIngredients.map { displayedIngredient ->
            val substitution = substitutionsByRequiredIngredient[displayedIngredient.ingredientId]
            RecipeDetailIngredientLine(
                quantity = displayedIngredient.quantity,
                unit = displayedIngredient.unit,
                ingredientName = ingredientNames.nameFor(
                    substitution?.substituteIngredientId ?: displayedIngredient.ingredientId,
                ),
                note = displayedIngredient.note,
                substituteForName = substitution?.requiredIngredientId?.let(ingredientNames::nameFor),
            )
        },
        missingIngredientNames = matchResult.missingIngredients.map(ingredientNames::nameFor),
        runningLowIngredientNames = matchResult.runningLowIngredients.map(ingredientNames::nameFor),
    )
}

private fun Map<String, String>.nameFor(ingredientId: String): String =
    get(ingredientId) ?: ingredientId.replace('-', ' ')
