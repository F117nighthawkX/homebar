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
    fun `empty search results retain a clear message in presentation state`() {
        assertEquals(
            "No ingredients match \"vodka\".",
            createInventoryUiState(ingredients, "vodka").emptyStateMessage,
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
