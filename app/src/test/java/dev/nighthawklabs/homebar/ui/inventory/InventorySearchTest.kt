package dev.nighthawklabs.homebar.ui.inventory

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.InventoryStatusFilter
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import org.junit.Assert.assertEquals
import org.junit.Test

class InventorySearchTest {
    private val ingredients = listOf(
        ingredient("tequila", "Tequila", IngredientCategory.SPIRIT),
        ingredient("angostura-bitters", "Angostura bitters", IngredientCategory.BITTERS),
        ingredient("lime-juice", "Lime juice", IngredientCategory.JUICE),
    )

    @Test
    fun `search matches an ingredient name without regard to case or surrounding whitespace`() {
        assertEquals(
            listOf(ingredients[0]),
            filterInventoryIngredients(ingredients, "  TEQUILA  "),
        )
    }

    @Test
    fun `search matches an ingredient category`() {
        assertEquals(
            listOf(ingredients[1]),
            filterInventoryIngredients(ingredients, "bitters"),
        )
    }

    @Test
    fun `clearing the search returns every ingredient`() {
        assertEquals(ingredients, filterInventoryIngredients(ingredients, "   "))
    }

    @Test
    fun `all category filter returns every ingredient`() {
        assertEquals(
            ingredients,
            filterInventoryIngredients(
                ingredients = ingredients,
                searchText = "",
                categoryFilter = InventoryCategoryFilter.ALL,
            ),
        )
    }

    @Test
    fun `category filter narrows ingredients by selected category`() {
        assertEquals(
            listOf(ingredients[2]),
            filterInventoryIngredients(
                ingredients = ingredients,
                searchText = "",
                categoryFilter = InventoryCategoryFilter.JUICES,
            ),
        )
    }

    @Test
    fun `search respects selected category filter`() {
        assertEquals(
            emptyList<Ingredient>(),
            filterInventoryIngredients(
                ingredients = ingredients,
                searchText = "tequila",
                categoryFilter = InventoryCategoryFilter.JUICES,
            ),
        )
    }

    @Test
    fun `presentation state keeps selected category filter`() {
        assertEquals(
            InventoryCategoryFilter.BITTERS,
            createInventoryUiState(
                ingredients = ingredients,
                searchText = "",
                categoryFilter = InventoryCategoryFilter.BITTERS,
            ).selectedCategoryFilter,
        )
    }

    @Test
    fun `presentation state keeps selected status filter`() {
        assertEquals(
            InventoryStatusFilter.RUNNING_LOW,
            createInventoryUiState(
                ingredients = ingredients,
                searchText = "",
                statusFilter = InventoryStatusFilter.RUNNING_LOW,
            ).selectedStatusFilter,
        )
    }

    @Test
    fun `status filter narrows ingredients by inventory state`() {
        val stockedIngredients = listOf(
            ingredient("tequila", "Tequila", IngredientCategory.SPIRIT, inStock = true),
            ingredient("coke", "Coke", IngredientCategory.MIXER, inStock = false),
        )

        assertEquals(
            listOf("Coke"),
            filterInventoryIngredients(
                ingredients = stockedIngredients,
                searchText = "",
                statusFilter = InventoryStatusFilter.MISSING,
            ).map { ingredient -> ingredient.name },
        )
    }

    @Test
    fun `search and category filters combine with selected status filter`() {
        val filteredIngredients = listOf(
            ingredient("lime-juice", "Lime juice", IngredientCategory.JUICE, runningLow = true),
            ingredient("lemon-juice", "Lemon juice", IngredientCategory.JUICE, runningLow = false),
            ingredient("angostura-bitters", "Angostura bitters", IngredientCategory.BITTERS, runningLow = true),
        )

        assertEquals(
            listOf("Lime juice"),
            filterInventoryIngredients(
                ingredients = filteredIngredients,
                searchText = "lime",
                categoryFilter = InventoryCategoryFilter.JUICES,
                statusFilter = InventoryStatusFilter.RUNNING_LOW,
            ).map { ingredient -> ingredient.name },
        )
    }

    @Test
    fun `favorite status filters use recipes and substitutes`() {
        val filteredIngredients = listOf(
            ingredient("coke", "Coke", IngredientCategory.MIXER, inStock = false),
            ingredient("pepsi", "Pepsi", IngredientCategory.MIXER, inStock = true),
        )

        assertEquals(
            emptyList<Ingredient>(),
            filterInventoryIngredients(
                ingredients = filteredIngredients,
                searchText = "",
                statusFilter = InventoryStatusFilter.MISSING_FOR_FAVORITES,
                recipes = listOf(favoriteRecipe("cuba-libre", "coke")),
                substitutionGroups = listOf(
                    SubstitutionGroup(
                        id = "cola",
                        name = "Cola",
                        ingredientIds = listOf("coke", "pepsi"),
                    ),
                ),
            ),
        )
    }

    @Test
    fun `empty search results retain a clear message in presentation state`() {
        assertEquals(
            "No ingredients match \"vodka\".",
            createInventoryUiState(ingredients, "vodka").emptyStateMessage,
        )
    }

    @Test
    fun `empty category results retain a clear message in presentation state`() {
        assertEquals(
            "No ingredients match Syrups.",
            createInventoryUiState(
                ingredients = ingredients,
                searchText = "",
                categoryFilter = InventoryCategoryFilter.SYRUPS,
            ).emptyStateMessage,
        )
    }

    @Test
    fun `empty status results retain a clear message in presentation state`() {
        assertEquals(
            "No ingredients match Running low.",
            createInventoryUiState(
                ingredients = ingredients,
                searchText = "",
                statusFilter = InventoryStatusFilter.RUNNING_LOW,
            ).emptyStateMessage,
        )
    }

    private fun ingredient(
        id: String,
        name: String,
        category: IngredientCategory,
        inStock: Boolean = true,
        runningLow: Boolean = false,
    ) = Ingredient(
        id = id,
        name = name,
        category = category,
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
            RecipeIngredient(ingredientId, 1.0, "oz", "")
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
