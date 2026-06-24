package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.IngredientSatisfaction
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SubstituteMatcherTest {
    private val colaGroup = SubstitutionGroup(
        id = "cola",
        name = "Cola",
        ingredientIds = listOf("coke", "pepsi"),
    )

    @Test
    fun `Pepsi satisfies a recipe requirement for out of stock Coke`() {
        val result = satisfyIngredient(
            requiredIngredientId = "coke",
            ingredients = listOf(ingredient("coke", inStock = false), ingredient("pepsi", inStock = true)),
            substitutionGroups = listOf(colaGroup),
        )

        assertTrue(result is IngredientSatisfaction.Substitute)
        val substitution = (result as IngredientSatisfaction.Substitute).substitution
        assertEquals("coke", substitution.requiredIngredientId)
        assertEquals("pepsi", substitution.substituteIngredientId)
    }

    @Test
    fun `Coke satisfies a recipe requirement for out of stock Pepsi`() {
        val result = satisfyIngredient(
            requiredIngredientId = "pepsi",
            ingredients = listOf(ingredient("coke", inStock = true), ingredient("pepsi", inStock = false)),
            substitutionGroups = listOf(colaGroup),
        )

        assertTrue(result is IngredientSatisfaction.Substitute)
        assertEquals(
            "coke",
            (result as IngredientSatisfaction.Substitute).substitution.substituteIngredientId,
        )
    }

    @Test
    fun `direct in stock ingredient is preferred over an available substitute`() {
        val result = satisfyIngredient(
            requiredIngredientId = "coke",
            ingredients = listOf(ingredient("coke", inStock = true), ingredient("pepsi", inStock = true)),
            substitutionGroups = listOf(colaGroup),
        )

        assertEquals(IngredientSatisfaction.Direct("coke"), result)
    }

    @Test
    fun `missing ingredient has no satisfaction when no substitute is available`() {
        val result = satisfyIngredient(
            requiredIngredientId = "coke",
            ingredients = listOf(ingredient("coke", inStock = false), ingredient("pepsi", inStock = false)),
            substitutionGroups = listOf(colaGroup),
        )

        assertEquals(IngredientSatisfaction.Missing("coke"), result)
    }

    private fun ingredient(id: String, inStock: Boolean) = Ingredient(
        id = id,
        name = id,
        category = IngredientCategory.MIXER,
        inStock = inStock,
        runningLow = false,
        notes = "",
    )
}
