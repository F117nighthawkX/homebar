package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
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
    fun `ingredient filter includes direct and approved substitute relationships`() {
        val cubaLibre = matchedRecipe(
            id = "cuba-libre",
            name = "Cuba Libre",
            ingredientIds = listOf("rum", "coke"),
            status = RecipeMatchStatus.MAKEABLE,
        )
        val pepsiHighball = matchedRecipe(
            id = "pepsi-highball",
            name = "Pepsi Highball",
            ingredientIds = listOf("pepsi"),
            status = RecipeMatchStatus.MAKEABLE,
        )

        val result = filterRecipes(
            recipes = listOf(cubaLibre, pepsiHighball),
            filterState = RecipeListFilterState(ingredientFilter = "pepsi"),
            substitutionGroups = listOf(colaGroup),
        )

        assertEquals(listOf("cuba-libre", "pepsi-highball"), result.map { it.recipe.id })
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

    @Test
    fun `search matches recipe names tags and ingredient names`() {
        val margarita = matchedRecipe(
            id = "margarita",
            name = "Margarita",
            ingredientIds = listOf("tequila"),
            status = RecipeMatchStatus.MAKEABLE,
            tags = listOf("Citrus"),
        )
        val whiskeySour = matchedRecipe(
            id = "whiskey-sour",
            name = "Whiskey Sour",
            ingredientIds = listOf("lemon-juice"),
            status = RecipeMatchStatus.MAKEABLE,
            tags = listOf("Sour"),
        )
        val recipes = listOf(margarita, whiskeySour)
        val ingredients = listOf(
            ingredient("tequila", "Tequila"),
            ingredient("lemon-juice", "Lemon juice"),
        )

        assertEquals(
            listOf("margarita"),
            filterRecipes(
                recipes = recipes,
                filterState = RecipeListFilterState(searchText = "margarita"),
                substitutionGroups = emptyList(),
                ingredients = ingredients,
            ).map { it.recipe.id },
        )
        assertEquals(
            listOf("margarita"),
            filterRecipes(
                recipes = recipes,
                filterState = RecipeListFilterState(searchText = "citrus"),
                substitutionGroups = emptyList(),
                ingredients = ingredients,
            ).map { it.recipe.id },
        )
        assertEquals(
            listOf("margarita"),
            filterRecipes(
                recipes = recipes,
                filterState = RecipeListFilterState(searchText = "tequila"),
                substitutionGroups = emptyList(),
                ingredients = ingredients,
            ).map { it.recipe.id },
        )
    }

    @Test
    fun `search combines with the selected makeability filter`() {
        val makeableMargarita = matchedRecipe(
            id = "margarita",
            name = "Margarita",
            ingredientIds = listOf("tequila"),
            status = RecipeMatchStatus.MAKEABLE,
        )
        val missingTequilaSour = matchedRecipe(
            id = "tequila-sour",
            name = "Tequila Sour",
            ingredientIds = listOf("tequila"),
            status = RecipeMatchStatus.MISSING_INGREDIENTS,
            missingIngredients = listOf("tequila"),
        )

        val result = filterRecipes(
            recipes = listOf(makeableMargarita, missingTequilaSour),
            filterState = RecipeListFilterState(searchText = "tequila"),
            substitutionGroups = emptyList(),
            ingredients = listOf(ingredient("tequila", "Tequila")),
        )

        assertEquals(listOf("margarita"), result.map { it.recipe.id })
    }

    @Test
    fun `ingredient filter combines with search and favorites`() {
        val favoriteCubaLibre = matchedRecipe(
            id = "cuba-libre",
            name = "Cuba Libre",
            ingredientIds = listOf("coke"),
            status = RecipeMatchStatus.MAKEABLE,
            isFavorite = true,
        )
        val nonFavoriteCubaLibre = matchedRecipe(
            id = "cuba-libre-variation",
            name = "Cuba Libre Variation",
            ingredientIds = listOf("coke"),
            status = RecipeMatchStatus.MAKEABLE,
        )

        val result = filterRecipes(
            recipes = listOf(favoriteCubaLibre, nonFavoriteCubaLibre),
            filterState = RecipeListFilterState(
                searchText = "cuba",
                ingredientFilter = "pepsi",
                favoriteOnly = true,
            ),
            substitutionGroups = listOf(colaGroup),
        )

        assertEquals(listOf("cuba-libre"), result.map { it.recipe.id })
    }

    @Test
    fun `custom recipes participate in list filters like classic recipes`() {
        val customMargarita = matchedRecipe(
            id = "house-margarita",
            name = "House Margarita",
            ingredientIds = listOf("tequila", "lime-juice"),
            status = RecipeMatchStatus.MAKEABLE,
            isFavorite = true,
            tags = listOf("Custom"),
            isCustom = true,
        )
        val classicMartini = matchedRecipe(
            id = "martini",
            name = "Martini",
            ingredientIds = listOf("gin"),
            status = RecipeMatchStatus.MISSING_INGREDIENTS,
            missingIngredients = listOf("gin"),
        )
        val recipes = listOf(customMargarita, classicMartini)
        val ingredients = listOf(
            ingredient("tequila", "Tequila"),
            ingredient("lime-juice", "Lime juice"),
            ingredient("gin", "Gin"),
        )

        assertEquals(
            listOf("house-margarita"),
            filterRecipes(
                recipes = recipes,
                filterState = RecipeListFilterState(searchText = "house"),
                substitutionGroups = emptyList(),
                ingredients = ingredients,
            ).map { it.recipe.id },
        )
        assertEquals(
            listOf("house-margarita"),
            filterRecipes(
                recipes = recipes,
                filterState = RecipeListFilterState(favoriteOnly = true),
                substitutionGroups = emptyList(),
                ingredients = ingredients,
            ).map { it.recipe.id },
        )
        assertEquals(
            listOf("house-margarita"),
            filterRecipes(
                recipes = recipes,
                filterState = RecipeListFilterState(ingredientFilter = "tequila"),
                substitutionGroups = emptyList(),
                ingredients = ingredients,
            ).map { it.recipe.id },
        )
        assertEquals(
            listOf("house-margarita"),
            filterRecipes(
                recipes = recipes,
                filterState = RecipeListFilterState(),
                substitutionGroups = emptyList(),
                ingredients = ingredients,
            ).map { it.recipe.id },
        )
    }

    private fun matchedRecipe(
        id: String,
        name: String,
        ingredientIds: List<String>,
        status: RecipeMatchStatus,
        missingIngredients: List<String> = emptyList(),
        isFavorite: Boolean = false,
        tags: List<String> = emptyList(),
        isCustom: Boolean = false,
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
            tags = tags,
            isFavorite = isFavorite,
            isCustom = isCustom,
        ),
        matchResult = RecipeMatchResult(
            status = status,
            missingIngredients = missingIngredients,
            substitutionsUsed = emptyList(),
            runningLowIngredients = emptyList(),
        ),
    )

    private fun ingredient(id: String, name: String) = Ingredient(
        id = id,
        name = name,
        category = IngredientCategory.OTHER,
        inStock = true,
        runningLow = false,
        notes = "",
    )
}
