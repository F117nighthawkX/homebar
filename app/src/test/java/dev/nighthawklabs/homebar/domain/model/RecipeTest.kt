package dev.nighthawklabs.homebar.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeTest {
    @Test
    fun `duplicate becomes a custom recipe with a separate identifier`() {
        val original = margarita()

        val duplicate = original.duplicatedAsCustom(newId = "custom-margarita")

        assertEquals("custom-margarita", duplicate.id)
        assertEquals("Margarita Copy", duplicate.name)
        assertTrue(duplicate.isCustom)
        assertFalse(duplicate.isFavorite)
        assertNotEquals(original.id, duplicate.id)
    }

    @Test
    fun `editing a duplicate does not change the classic recipe`() {
        val original = margarita()
        val duplicate = original.duplicatedAsCustom(newId = "custom-margarita")

        val editedDuplicate = duplicate.copy(
            name = "Spicy Margarita",
            ingredients = duplicate.ingredients.map { ingredient ->
                if (ingredient.ingredientId == "tequila") ingredient.copy(quantity = 1.5) else ingredient
            },
        )

        assertEquals("Margarita", original.name)
        assertEquals(2.0, original.ingredients.first().quantity, 0.0)
        assertEquals("Spicy Margarita", editedDuplicate.name)
        assertEquals(1.5, editedDuplicate.ingredients.first().quantity, 0.0)
    }

    private fun margarita() = Recipe(
        id = "margarita",
        name = "Margarita",
        baseServingCount = 1,
        ingredients = listOf(
            RecipeIngredient("tequila", 2.0, "oz", ""),
            RecipeIngredient("lime-juice", 1.0, "oz", ""),
        ),
        instructions = "Shake with ice.",
        glassware = "Rocks glass",
        tools = listOf("Shaker", "Jigger"),
        garnish = listOf("Lime wheel"),
        tags = listOf("Classic"),
        isFavorite = true,
        isCustom = false,
    )
}
