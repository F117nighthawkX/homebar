package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient

/** Ephemeral recipe-detail state; it never changes the saved recipe. */
data class RecipeServingState(
    val recipe: Recipe,
    val selectedServingCount: Int = recipe.baseServingCount,
) {
    init {
        require(selectedServingCount >= 1) { "Selected serving count must be at least one." }
    }

    val displayedIngredients: List<RecipeIngredient>
        get() = recipe.ingredients.map { ingredient ->
            ingredient.copy(quantity = ingredient.quantity * servingMultiplier)
        }

    fun increaseServings(): RecipeServingState = copy(
        selectedServingCount = selectedServingCount + 1,
    )

    fun decreaseServings(): RecipeServingState = copy(
        selectedServingCount = (selectedServingCount - 1).coerceAtLeast(1),
    )

    private val servingMultiplier: Double
        get() = selectedServingCount.toDouble() / recipe.baseServingCount
}
