package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient
import dev.nighthawklabs.homebar.domain.model.RecipeListFilterState
import dev.nighthawklabs.homebar.domain.model.RecipeMakeabilityFilter
import dev.nighthawklabs.homebar.domain.model.RecipeMatchResult
import dev.nighthawklabs.homebar.domain.model.RecipeMatchStatus
import dev.nighthawklabs.homebar.domain.model.RecipeWithMatchResult
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeFilterLogicTest {
    private val colaGroup = SubstitutionGroup("cola", "Cola", listOf("coke", "pepsi"))

    @Test
    fun `default makeable filter includes substitute-based recipes`() {
        val cubaLibre = matchedRecipe(
            id = "cuba-libre",
            name = "Cuba Libre",
            ingredientIds = listOf("rum", "coke"),
            status = RecipeMatchStatus.MAKEABLE,
        )
        val missingMargarita = matchedRecipe(
            id = "margarita",
            name = "Margarita",
            ingredientIds = listOf("tequila", "lime-juice"),
            status = RecipeMatchStatus.MISSING_INGREDIENTS,
            missingIngredients = listOf("lime-juice"),
        )

        val result = filterRecipes(
            recipes = listOf(cubaLibre, missingMargarita),
            filterState = RecipeListFilterState(),
            substitutionGroups = listOf(colaGroup),
        )

        assertEquals(listOf("cuba-libre"), result.map { it.recipe.id })
    }

    @Test
    fun `missing one ingredient filter uses the derived missing count`() {
        val missingOne = matchedRecipe(
            id = "margarita",
            name = "Margarita",
            ingredientIds = listOf("tequila", "lime-juice"),
            status = RecipeMatchStatus.MISSING_INGREDIENTS,
            missingIngredients = listOf("lime-juice"),
        )
        val missingTwo = matchedRecipe(
            id = "sidecar",
            name = "Sidecar",
            ingredientIds = listOf("cognac", "lemon-juice", "orange-liqueur"),
            status = RecipeMatchStatus.MISSING_INGREDIENTS,
            missingIngredients = listOf("cognac", "lemon-juice"),
        )

        val result = filterRecipes(
            recipes = listOf(missingOne, missingTwo),
            filterState = RecipeListFilterState(
                makeabilityFilter = RecipeMakeabilityFilter.MISSING_ONE_INGREDIENT,
            ),
            substitutionGroups = emptyList(),
        )

        assertEquals(listOf("margarita"), result.map { it.recipe.id })
    }

    @Test
    fun `ingredient filter includes approved substitute relationships`() {
        val cubaLibre = matchedRecipe(
            id = "cuba-libre",
            name = "Cuba Libre",
            ingredientIds = listOf("rum", "coke"),
            status = RecipeMatchStatus.MAKEABLE,
        )

        val result = filterRecipes(
            recipes = listOf(cubaLibre),
            filterState = RecipeListFilterState(ingredientFilter = "pepsi"),
            substitutionGroups = listOf(colaGroup),
        )

        assertEquals(listOf("cuba-libre"), result.map { it.recipe.id })
    }

    @Test
    fun `active filters combine with search and favorites`() {
        val favoriteMargarita = matchedRecipe(
            id = "margarita",
            name = "Margarita",
            ingredientIds = listOf("tequila"),
            status = RecipeMatchStatus.MAKEABLE,
            isFavorite = true,
        )
        val nonFavoriteMartini = matchedRecipe(
            id = "martini",
            name = "Martini",
            ingredientIds = listOf("gin"),
            status = RecipeMatchStatus.MAKEABLE,
            isFavorite = false,
        )

        val result = filterRecipes(
            recipes = listOf(favoriteMargarita, nonFavoriteMartini),
            filterState = RecipeListFilterState(
                searchText = "m",
                favoriteOnly = true,
            ),
            substitutionGroups = emptyList(),
        )

        assertEquals(listOf("margarita"), result.map { it.recipe.id })
    }

    private fun matchedRecipe(
        id: String,
        name: String,
        ingredientIds: List<String>,
        status: RecipeMatchStatus,
        missingIngredients: List<String> = emptyList(),
        isFavorite: Boolean = false,
    ) = RecipeWithMatchResult(
        recipe = Recipe(
            id = id,
            name = name,
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
        ),
        matchResult = RecipeMatchResult(
            status = status,
            missingIngredients = missingIngredients,
            substitutionsUsed = emptyList(),
            runningLowIngredients = emptyList(),
        ),
    )
}
