package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeServingStateTest {
    @Test
    fun `four servings scales Margarita ingredient quantities`() {
        val state = RecipeServingState(margarita()).increaseServings().increaseServings().increaseServings()

        assertEquals(4, state.selectedServingCount)
        assertEquals(8.0, state.displayedIngredients.first().quantity, 0.0)
        assertEquals(3.0, state.displayedIngredients[1].quantity, 0.0)
    }

    @Test
    fun `decreasing servings never goes below one`() {
        val state = RecipeServingState(margarita())

        val updated = state.decreaseServings()

        assertEquals(1, updated.selectedServingCount)
    }

    @Test
    fun `scaling does not change saved recipe quantities`() {
        val recipe = margarita()

        RecipeServingState(recipe).increaseServings()

        assertEquals(2.0, recipe.ingredients.first().quantity, 0.0)
    }

    @Test
    fun `base serving count is preserved when scaling multi-serving recipe`() {
        val recipe = margarita().copy(baseServingCount = 2)

        val state = RecipeServingState(recipe)

        assertEquals(2.0, state.displayedIngredients.first().quantity, 0.0)
    }

    private fun margarita() = Recipe(
        id = "margarita",
        name = "Margarita",
        baseServingCount = 1,
        ingredients = listOf(
            RecipeIngredient("tequila", 2.0, "oz", ""),
            RecipeIngredient("orange-liqueur", 0.75, "oz", ""),
        ),
        instructions = "Shake with ice.",
        glassware = "Rocks glass",
        tools = emptyList(),
        garnish = emptyList(),
        tags = emptyList(),
        isFavorite = false,
        isCustom = false,
    )
}
