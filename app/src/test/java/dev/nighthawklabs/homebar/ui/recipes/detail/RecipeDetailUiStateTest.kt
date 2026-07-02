package dev.nighthawklabs.homebar.ui.recipes.detail

import dev.nighthawklabs.homebar.domain.logic.RecipeServingState
import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient
import dev.nighthawklabs.homebar.domain.model.RecipeMatchStatus
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeDetailUiStateTest {
    @Test
    fun `detail uses the available substitute and scales its quantity`() {
        val state = createRecipeDetailUiState(
            servingState = RecipeServingState(cubaLibre()).increaseServings(),
            ingredients = listOf(
                ingredient("rum", "Rum", inStock = true),
                ingredient("coke", "Coke", inStock = false),
                ingredient("pepsi", "Pepsi", inStock = true, runningLow = true),
            ),
            substitutionGroups = listOf(SubstitutionGroup("cola", "Cola", listOf("coke", "pepsi"))),
        )

        assertEquals(RecipeMatchStatus.MAKEABLE, state.matchStatus)
        assertEquals(2, state.selectedServingCount)
        assertEquals("Pepsi", state.ingredientLines[1].ingredientName)
        assertEquals("Coke", state.ingredientLines[1].substituteForName)
        assertEquals(8.0, state.ingredientLines[1].quantity, 0.0)
        assertEquals(listOf("Pepsi"), state.runningLowIngredientNames)
    }

    @Test
    fun `detail exposes missing ingredient names`() {
        val state = createRecipeDetailUiState(
            servingState = RecipeServingState(
                cubaLibre().copy(ingredients = listOf(RecipeIngredient("lemon-juice", 1.0, "oz", ""))),
            ),
            ingredients = listOf(ingredient("lemon-juice", "Lemon juice", inStock = false)),
            substitutionGroups = emptyList(),
        )

        assertEquals(RecipeMatchStatus.MISSING_INGREDIENTS, state.matchStatus)
        assertEquals(listOf("Lemon juice"), state.missingIngredientNames)
    }

    @Test
    fun `classic recipe can be duplicated but not edited or deleted`() {
        val state = createRecipeDetailUiState(
            servingState = RecipeServingState(cubaLibre()),
            ingredients = listOf(
                ingredient("rum", "Rum", inStock = true),
                ingredient("coke", "Coke", inStock = true),
            ),
            substitutionGroups = emptyList(),
        )

        assertEquals(true, state.canDuplicate)
        assertEquals(false, state.canEdit)
        assertEquals(false, state.canDelete)
    }

    @Test
    fun `custom recipe can be duplicated edited and deleted`() {
        val state = createRecipeDetailUiState(
            servingState = RecipeServingState(cubaLibre().copy(isCustom = true)),
            ingredients = listOf(
                ingredient("rum", "Rum", inStock = true),
                ingredient("coke", "Coke", inStock = true),
            ),
            substitutionGroups = emptyList(),
        )

        assertEquals(true, state.canDuplicate)
        assertEquals(true, state.canEdit)
        assertEquals(true, state.canDelete)
    }

    @Test
    fun `custom recipe detail uses matching and serving scaling`() {
        val state = createRecipeDetailUiState(
            servingState = RecipeServingState(cubaLibre().copy(isCustom = true, isFavorite = true))
                .increaseServings(),
            ingredients = listOf(
                ingredient("rum", "Rum", inStock = true),
                ingredient("coke", "Coke", inStock = true),
            ),
            substitutionGroups = emptyList(),
        )

        assertEquals(RecipeMatchStatus.MAKEABLE, state.matchStatus)
        assertEquals(2, state.selectedServingCount)
        assertEquals(true, state.recipe?.isCustom)
        assertEquals(true, state.recipe?.isFavorite)
        assertEquals(4.0, state.ingredientLines[0].quantity, 0.0)
        assertEquals(8.0, state.ingredientLines[1].quantity, 0.0)
    }

    private fun cubaLibre() = Recipe(
        id = "cuba-libre",
        name = "Cuba Libre",
        baseServingCount = 1,
        ingredients = listOf(
            RecipeIngredient("rum", 2.0, "oz", ""),
            RecipeIngredient("coke", 4.0, "oz", ""),
        ),
        instructions = "Build over ice and stir.",
        glassware = "Highball glass",
        tools = listOf("Jigger"),
        garnish = listOf("Lime wedge"),
        tags = listOf("Classic"),
        isFavorite = false,
        isCustom = false,
    )

    private fun ingredient(
        id: String,
        name: String,
        inStock: Boolean,
        runningLow: Boolean = false,
    ) = Ingredient(
        id = id,
        name = name,
        category = IngredientCategory.OTHER,
        inStock = inStock,
        runningLow = runningLow,
        notes = "",
    )
}
