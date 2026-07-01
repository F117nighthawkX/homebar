package dev.nighthawklabs.homebar.ui.recipes.editor

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeEditorStateTest {
    @Test
    fun `blank editor state cannot be saved`() {
        val state = RecipeEditorUiState().withSaveAvailability(listOf(tequila()))

        assertFalse(state.canSave)
        assertNull(state.toCustomRecipe(null, listOf(tequila()), "custom", 100L))
    }

    @Test
    fun `editor state creates a custom recipe from valid fields`() {
        val state = RecipeEditorUiState(
            name = "House Margarita",
            baseServingCount = "2",
            ingredientLines = listOf(
                RecipeEditorIngredientLineUiState(
                    ingredientName = "Tequila",
                    unit = "oz",
                    quantity = "3",
                    note = "blanco",
                ),
            ),
            instructions = "Shake with ice.",
            glassware = "Rocks glass",
            tools = "Shaker, Jigger",
            garnish = "Lime wheel",
            tags = "Custom, Citrus",
            isFavorite = true,
        )

        val recipe = state.toCustomRecipe(
            existingRecipe = null,
            ingredients = listOf(tequila()),
            recipeId = "house-margarita",
            nowMillis = 100L,
        )

        assertEquals("house-margarita", recipe?.id)
        assertEquals("House Margarita", recipe?.name)
        assertEquals(2, recipe?.baseServingCount)
        assertEquals(listOf(RecipeIngredient("tequila", 3.0, "oz", "blanco")), recipe?.ingredients)
        assertEquals(listOf("Shaker", "Jigger"), recipe?.tools)
        assertEquals(listOf("Lime wheel"), recipe?.garnish)
        assertEquals(listOf("Custom", "Citrus"), recipe?.tags)
        assertEquals(true, recipe?.isFavorite)
        assertEquals(true, recipe?.isCustom)
        assertNull(recipe?.sourceRecipeId)
        assertEquals(100L, recipe?.createdAt)
        assertEquals(100L, recipe?.updatedAt)
    }

    @Test
    fun `editor state preserves custom metadata while updating recipe fields`() {
        val existingRecipe = customRecipe()
        val state = createRecipeEditorUiState(existingRecipe, listOf(tequila())).copy(
            name = "Spicy Margarita",
            instructions = "Shake hard.",
        )

        val recipe = state.toCustomRecipe(
            existingRecipe = existingRecipe,
            ingredients = listOf(tequila()),
            recipeId = existingRecipe.id,
            nowMillis = 200L,
        )

        assertEquals("classic-margarita", recipe?.sourceRecipeId)
        assertEquals(100L, recipe?.createdAt)
        assertEquals(200L, recipe?.updatedAt)
        assertEquals("Spicy Margarita", recipe?.name)
        assertEquals("Shake hard.", recipe?.instructions)
    }

    @Test
    fun `editor state exposes existing ingredient names for editing`() {
        val state = createRecipeEditorUiState(customRecipe(), listOf(tequila()))

        assertTrue(state.ingredientLines.single().ingredientName == "Tequila")
        assertEquals("2", state.ingredientLines.single().quantity)
    }

    private fun customRecipe() = Recipe(
        id = "custom-margarita",
        name = "Custom Margarita",
        baseServingCount = 1,
        ingredients = listOf(RecipeIngredient("tequila", 2.0, "oz", "")),
        instructions = "Shake with ice.",
        glassware = "Rocks glass",
        tools = listOf("Shaker"),
        garnish = listOf("Lime wheel"),
        tags = listOf("Custom"),
        isFavorite = false,
        isCustom = true,
        sourceRecipeId = "classic-margarita",
        createdAt = 100L,
        updatedAt = 100L,
    )

    private fun tequila() = Ingredient(
        id = "tequila",
        name = "Tequila",
        category = IngredientCategory.SPIRIT,
        inStock = true,
        runningLow = false,
        notes = "",
    )
}
