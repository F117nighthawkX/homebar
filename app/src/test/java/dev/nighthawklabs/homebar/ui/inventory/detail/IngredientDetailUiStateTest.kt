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
        ingredient("lime-juice", "Lime juice", IngredientCategory.JUICE),
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

    @Test
    fun `substitute search excludes current ingredient and existing substitutes`() {
        val results = createSubstituteSearchResults(
            ingredientId = "coke",
            ingredients = ingredients,
            substitutionGroups = listOf(
                SubstitutionGroup(
                    id = "cola",
                    name = "Cola",
                    ingredientIds = listOf("coke", "pepsi"),
                ),
            ),
            searchText = "",
        )

        assertEquals(listOf("Lime juice", "Tequila"), results.map { it.name })
    }

    @Test
    fun `substitute search matches ingredient category`() {
        val results = createSubstituteSearchResults(
            ingredientId = "coke",
            ingredients = ingredients,
            substitutionGroups = emptyList(),
            searchText = "spirit",
        )

        assertEquals(listOf("Tequila"), results.map { it.name })
    }

    @Test
    fun `detail state preserves substitute search controls`() {
        val state = createIngredientDetailUiState(
            ingredientId = "coke",
            ingredients = ingredients,
            substitutionGroups = emptyList(),
            substituteSearchVisible = true,
            substituteSearchText = "teq",
            selectedSubstituteIds = setOf("tequila"),
            substituteMessage = "Substitute added.",
        )

        assertEquals(true, state.substituteSearchVisible)
        assertEquals("teq", state.substituteSearchText)
        assertEquals(listOf("Tequila"), state.substituteSearchResults.map { it.name })
        assertEquals(setOf("tequila"), state.selectedSubstituteIds)
        assertEquals("Substitute added.", state.substituteMessage)
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
