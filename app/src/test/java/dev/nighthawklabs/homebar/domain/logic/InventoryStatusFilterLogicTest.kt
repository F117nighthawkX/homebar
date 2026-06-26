package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.InventoryStatusFilter
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import org.junit.Assert.assertEquals
import org.junit.Test

class InventoryStatusFilterLogicTest {
    @Test
    fun `in stock filter returns available ingredients`() {
        val result = filterIngredientsByInventoryStatus(
            ingredients = listOf(
                ingredient("tequila", inStock = true),
                ingredient("coke", inStock = false),
            ),
            recipes = emptyList(),
            substitutionGroups = emptyList(),
            statusFilter = InventoryStatusFilter.IN_STOCK,
        )

        assertEquals(listOf("tequila"), result.map { ingredient -> ingredient.id })
    }

    @Test
    fun `missing filter returns unavailable ingredients`() {
        val result = filterIngredientsByInventoryStatus(
            ingredients = listOf(
                ingredient("tequila", inStock = true),
                ingredient("coke", inStock = false),
            ),
            recipes = emptyList(),
            substitutionGroups = emptyList(),
            statusFilter = InventoryStatusFilter.MISSING,
        )

        assertEquals(listOf("coke"), result.map { ingredient -> ingredient.id })
    }

    @Test
    fun `running low filter returns low ingredients`() {
        val result = filterIngredientsByInventoryStatus(
            ingredients = listOf(
                ingredient("tequila", inStock = true),
                ingredient("lime-juice", inStock = true, runningLow = true),
            ),
            recipes = emptyList(),
            substitutionGroups = emptyList(),
            statusFilter = InventoryStatusFilter.RUNNING_LOW,
        )

        assertEquals(listOf("lime-juice"), result.map { ingredient -> ingredient.id })
    }

    @Test
    fun `missing for favorites excludes ingredients satisfied by substitutes`() {
        val result = filterIngredientsByInventoryStatus(
            ingredients = listOf(
                ingredient("coke", inStock = false),
                ingredient("pepsi", inStock = true),
            ),
            recipes = listOf(favoriteRecipe("cuba-libre", "coke")),
            substitutionGroups = listOf(SubstitutionGroup("cola", "Cola", listOf("coke", "pepsi"))),
            statusFilter = InventoryStatusFilter.MISSING_FOR_FAVORITES,
        )

        assertEquals(emptyList<String>(), result.map { ingredient -> ingredient.id })
    }

    @Test
    fun `missing for favorites returns ingredients blocking favorite recipes`() {
        val result = filterIngredientsByInventoryStatus(
            ingredients = listOf(
                ingredient("tequila", inStock = true),
                ingredient("lime-juice", inStock = false),
            ),
            recipes = listOf(favoriteRecipe("margarita", "tequila", "lime-juice")),
            substitutionGroups = emptyList(),
            statusFilter = InventoryStatusFilter.MISSING_FOR_FAVORITES,
        )

        assertEquals(listOf("lime-juice"), result.map { ingredient -> ingredient.id })
    }

    @Test
    fun `running low for favorites includes direct and used substitute ingredients`() {
        val result = filterIngredientsByInventoryStatus(
            ingredients = listOf(
                ingredient("bourbon", inStock = true, runningLow = true),
                ingredient("coke", inStock = false),
                ingredient("pepsi", inStock = true, runningLow = true),
                ingredient("rc-cola", inStock = true, runningLow = true),
            ),
            recipes = listOf(favoriteRecipe("favorite", "bourbon", "coke")),
            substitutionGroups = listOf(
                SubstitutionGroup("cola", "Cola", listOf("coke", "pepsi", "rc-cola")),
            ),
            statusFilter = InventoryStatusFilter.RUNNING_LOW_FOR_FAVORITES,
        )

        assertEquals(listOf("bourbon", "pepsi"), result.map { ingredient -> ingredient.id })
    }

    @Test
    fun `combined favorite filter returns missing and running low favorite ingredients`() {
        val result = filterIngredientsByInventoryStatus(
            ingredients = listOf(
                ingredient("tequila", inStock = true),
                ingredient("lime-juice", inStock = false),
                ingredient("angostura-bitters", inStock = true, runningLow = true),
            ),
            recipes = listOf(
                favoriteRecipe("margarita", "tequila", "lime-juice"),
                favoriteRecipe("old-fashioned", "angostura-bitters"),
            ),
            substitutionGroups = emptyList(),
            statusFilter = InventoryStatusFilter.MISSING_OR_RUNNING_LOW_FOR_FAVORITES,
        )

        assertEquals(
            listOf("lime-juice", "angostura-bitters"),
            result.map { ingredient -> ingredient.id },
        )
    }

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

    private fun favoriteRecipe(
        id: String,
        vararg ingredientIds: String,
    ) = Recipe(
        id = id,
        name = id,
        baseServingCount = 1,
        ingredients = ingredientIds.map { ingredientId ->
            RecipeIngredient(ingredientId = ingredientId, quantity = 1.0, unit = "oz", note = "")
        },
        instructions = "",
        glassware = "",
        tools = emptyList(),
        garnish = emptyList(),
        tags = emptyList(),
        isFavorite = true,
        isCustom = false,
    )
}
