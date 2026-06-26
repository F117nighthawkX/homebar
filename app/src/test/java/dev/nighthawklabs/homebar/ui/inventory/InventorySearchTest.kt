package dev.nighthawklabs.homebar.ui.inventory

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
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

    private fun ingredient(
        id: String,
        name: String,
        category: IngredientCategory,
    ) = Ingredient(
        id = id,
        name = name,
        category = category,
        inStock = true,
        runningLow = false,
        notes = "",
    )
}
