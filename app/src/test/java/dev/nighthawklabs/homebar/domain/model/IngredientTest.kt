package dev.nighthawklabs.homebar.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IngredientTest {
    @Test
    fun `running low ingredient remains available for recipe matching`() {
        val ingredient = sampleIngredient(inStock = true, runningLow = true)

        assertTrue(ingredient.isAvailableForRecipeMatching)
    }

    @Test
    fun `marking an ingredient not in stock clears running low`() {
        val ingredient = sampleIngredient(inStock = true, runningLow = true)

        val updated = ingredient.markedNotInStock()

        assertFalse(updated.inStock)
        assertFalse(updated.runningLow)
        assertFalse(updated.isAvailableForRecipeMatching)
    }

    @Test
    fun `marking an ingredient in stock keeps running low unchanged`() {
        val ingredient = sampleIngredient(inStock = false, runningLow = false)

        val updated = ingredient.markedInStock()

        assertTrue(updated.inStock)
        assertFalse(updated.runningLow)
    }

    @Test
    fun `marking an ingredient running low also marks it in stock`() {
        val ingredient = sampleIngredient(inStock = false, runningLow = false)

        val updated = ingredient.markedRunningLow()

        assertTrue(updated.inStock)
        assertTrue(updated.runningLow)
    }

    @Test
    fun `clearing running low keeps the ingredient in stock`() {
        val ingredient = sampleIngredient(inStock = true, runningLow = true)

        val updated = ingredient.clearedRunningLow()

        assertTrue(updated.inStock)
        assertFalse(updated.runningLow)
    }

    private fun sampleIngredient(inStock: Boolean, runningLow: Boolean) = Ingredient(
        id = "tequila",
        name = "Tequila",
        category = IngredientCategory.SPIRIT,
        inStock = inStock,
        runningLow = runningLow,
        notes = "",
    )
}
