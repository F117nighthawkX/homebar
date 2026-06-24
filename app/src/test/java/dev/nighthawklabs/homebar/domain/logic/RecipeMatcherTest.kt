package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient
import dev.nighthawklabs.homebar.domain.model.RecipeMatchStatus
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import dev.nighthawklabs.homebar.domain.model.SubstitutionUsed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeMatcherTest {
    @Test
    fun `running low ingredient keeps a recipe makeable and adds a warning`() {
        val result = matchRecipe(
            recipe = recipe(RecipeIngredient("tequila", 2.0, "oz", "")),
            ingredients = listOf(ingredient("tequila", inStock = true, runningLow = true)),
            substitutionGroups = emptyList(),
        )

        assertEquals(RecipeMatchStatus.MAKEABLE, result.status)
        assertTrue(result.missingIngredients.isEmpty())
        assertEquals(listOf("tequila"), result.runningLowIngredients)
    }

    @Test
    fun `in stock substitute makes recipe makeable and records its use`() {
        val result = matchRecipe(
            recipe = recipe(RecipeIngredient("coke", 4.0, "oz", "")),
            ingredients = listOf(
                ingredient("coke", inStock = false),
                ingredient("pepsi", inStock = true, runningLow = true),
            ),
            substitutionGroups = listOf(
                SubstitutionGroup("cola", "Cola", listOf("coke", "pepsi")),
            ),
        )

        assertEquals(RecipeMatchStatus.MAKEABLE, result.status)
        assertEquals(listOf(SubstitutionUsed("coke", "pepsi")), result.substitutionsUsed)
        assertEquals(listOf("pepsi"), result.runningLowIngredients)
    }

    @Test
    fun `first available substitute is selected when a group has multiple options`() {
        val result = matchRecipe(
            recipe = recipe(RecipeIngredient("coke", 4.0, "oz", "")),
            ingredients = listOf(
                ingredient("coke", inStock = false),
                ingredient("pepsi", inStock = true),
                ingredient("rc-cola", inStock = true),
            ),
            substitutionGroups = listOf(
                SubstitutionGroup("cola", "Cola", listOf("coke", "pepsi", "rc-cola")),
            ),
        )

        assertEquals(listOf(SubstitutionUsed("coke", "pepsi")), result.substitutionsUsed)
    }

    @Test
    fun `unavailable required ingredient is returned as missing`() {
        val result = matchRecipe(
            recipe = recipe(
                RecipeIngredient("tequila", 2.0, "oz", ""),
                RecipeIngredient("lime-juice", 1.0, "oz", ""),
            ),
            ingredients = listOf(ingredient("tequila", inStock = true)),
            substitutionGroups = emptyList(),
        )

        assertEquals(RecipeMatchStatus.MISSING_INGREDIENTS, result.status)
        assertEquals(listOf("lime-juice"), result.missingIngredients)
        assertTrue(result.substitutionsUsed.isEmpty())
    }

    @Test
    fun `tools glassware and garnish do not affect availability`() {
        val recipe = recipe(RecipeIngredient("tequila", 2.0, "oz", "")).copy(
            glassware = "Rocks glass",
            tools = listOf("Shaker", "Jigger"),
            garnish = listOf("Lime wheel"),
        )

        val result = matchRecipe(
            recipe = recipe,
            ingredients = listOf(ingredient("tequila", inStock = true)),
            substitutionGroups = emptyList(),
        )

        assertEquals(RecipeMatchStatus.MAKEABLE, result.status)
    }

    private fun recipe(vararg ingredients: RecipeIngredient) = Recipe(
        id = "test-recipe",
        name = "Test recipe",
        baseServingCount = 1,
        ingredients = ingredients.toList(),
        instructions = "",
        glassware = "",
        tools = emptyList(),
        garnish = emptyList(),
        tags = emptyList(),
        isFavorite = false,
        isCustom = false,
    )

    private fun ingredient(
        id: String,
        inStock: Boolean,
        runningLow: Boolean = false,
    ) = Ingredient(
        id = id,
        name = id,
        category = IngredientCategory.OTHER,
        inStock = inStock,
        runningLow = runningLow,
        notes = "",
    )
}
