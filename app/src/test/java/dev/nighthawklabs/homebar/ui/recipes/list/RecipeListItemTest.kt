package dev.nighthawklabs.homebar.ui.recipes.list

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient
import dev.nighthawklabs.homebar.domain.model.RecipeMakeabilityFilter
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeListItemTest {
    @Test
    fun `empty filter result has a clear message`() {
        assertEquals(
            "No recipes match the favorites filter.",
            RecipeListUiState(selectedFilter = RecipeListFilterOption.FAVORITES).emptyStateMessage(),
        )
    }

    @Test
    fun `filter options map to the expected domain filter state`() {
        assertEquals(
            RecipeMakeabilityFilter.MAKEABLE_NOW,
            RecipeListFilterOption.MAKEABLE_NOW.toFilterState().makeabilityFilter,
        )
        assertEquals(
            RecipeMakeabilityFilter.MISSING_ONE_INGREDIENT,
            RecipeListFilterOption.MISSING_ONE_INGREDIENT.toFilterState().makeabilityFilter,
        )
        assertEquals(
            RecipeMakeabilityFilter.ALL_RECIPES,
            RecipeListFilterOption.ALL_RECIPES.toFilterState().makeabilityFilter,
        )
        assertEquals(
            RecipeMakeabilityFilter.ALL_RECIPES,
            RecipeListFilterOption.FAVORITES.toFilterState().makeabilityFilter,
        )
        assertEquals(true, RecipeListFilterOption.FAVORITES.toFilterState().favoriteOnly)
    }

    @Test
    fun `cards show makeable low inventory missing and substitute based states`() {
        val items = createRecipeListItems(
            recipes = listOf(
                recipe("margarita", true, "tequila", "lime-juice"),
                recipe("cuba-libre", false, "rum", "coke"),
                recipe("whiskey-sour", false, "bourbon", "lemon-juice"),
            ),
            ingredients = listOf(
                ingredient("tequila", "Tequila", inStock = true),
                ingredient("lime-juice", "Lime juice", inStock = true, runningLow = true),
                ingredient("rum", "Rum", inStock = true),
                ingredient("coke", "Coke", inStock = false),
                ingredient("pepsi", "Pepsi", inStock = true),
                ingredient("bourbon", "Bourbon", inStock = true),
                ingredient("lemon-juice", "Lemon juice", inStock = false),
            ),
            substitutionGroups = listOf(SubstitutionGroup("cola", "Cola", listOf("coke", "pepsi"))),
        )

        assertEquals(
            RecipeListItem(
                id = "margarita",
                name = "Margarita",
                isFavorite = true,
                ingredientSummary = "Tequila, Lime juice",
                makeabilityLabel = "Makeable",
                missingIngredientNames = emptyList(),
                runningLowIngredientNames = listOf("Lime juice"),
            ),
            items.single { it.id == "margarita" },
        )
        assertEquals("Makeable", items.single { it.id == "cuba-libre" }.makeabilityLabel)
        assertEquals("Missing 1", items.single { it.id == "whiskey-sour" }.makeabilityLabel)
        assertEquals(
            listOf("Lemon juice"),
            items.single { it.id == "whiskey-sour" }.missingIngredientNames,
        )
    }

    private fun recipe(
        id: String,
        isFavorite: Boolean = false,
        vararg ingredientIds: String,
    ) = Recipe(
        id = id,
        name = id.replace('-', ' ').split(' ').joinToString(" ") { it.replaceFirstChar(Char::uppercase) },
        baseServingCount = 1,
        ingredients = ingredientIds.map { ingredientId ->
            RecipeIngredient(ingredientId, 1.0, "oz", "")
        },
        instructions = "",
        glassware = "",
        tools = emptyList(),
        garnish = emptyList(),
        tags = emptyList(),
        isFavorite = isFavorite,
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
