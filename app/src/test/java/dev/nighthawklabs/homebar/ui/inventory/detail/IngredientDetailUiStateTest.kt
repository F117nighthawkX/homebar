package dev.nighthawklabs.homebar.ui.inventory.detail

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class IngredientDetailUiStateTest {
    private val ingredients = listOf(
        ingredient("coke", "Coke", IngredientCategory.MIXER),
        ingredient("pepsi", "Pepsi", IngredientCategory.MIXER),
        ingredient("tequila", "Tequila", IngredientCategory.SPIRIT),
    )

    @Test
    fun `detail state includes selected ingredient`() {
        val state = createIngredientDetailUiState(
            ingredientId = "coke",
            ingredients = ingredients,
            substitutionGroups = emptyList(),
        )

        assertEquals("Coke", state.ingredient?.name)
        assertFalse(state.isLoading)
    }

    @Test
    fun `detail state includes substitutes from matching groups`() {
        val state = createIngredientDetailUiState(
            ingredientId = "coke",
            ingredients = ingredients,
            substitutionGroups = listOf(
                SubstitutionGroup(
                    id = "cola",
                    name = "Cola",
                    ingredientIds = listOf("coke", "pepsi"),
                ),
            ),
        )

        assertEquals(listOf("Pepsi"), state.substitutes.map { it.name })
    }

    @Test
    fun `detail state omits selected ingredient from substitute list`() {
        val state = createIngredientDetailUiState(
            ingredientId = "pepsi",
            ingredients = ingredients,
            substitutionGroups = listOf(
                SubstitutionGroup(
                    id = "cola",
                    name = "Cola",
                    ingredientIds = listOf("coke", "pepsi"),
                ),
            ),
        )

        assertEquals(listOf("Coke"), state.substitutes.map { it.name })
    }

    @Test
    fun `missing ingredient creates loaded not found state`() {
        val state = createIngredientDetailUiState(
            ingredientId = "unknown",
            ingredients = ingredients,
            substitutionGroups = emptyList(),
        )

        assertEquals(null, state.ingredient)
        assertEquals(emptyList<Ingredient>(), state.substitutes)
        assertFalse(state.isLoading)
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
