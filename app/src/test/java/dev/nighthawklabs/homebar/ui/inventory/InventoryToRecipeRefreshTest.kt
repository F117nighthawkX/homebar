package dev.nighthawklabs.homebar.ui.inventory

import dev.nighthawklabs.homebar.domain.logic.RecipeServingState
import dev.nighthawklabs.homebar.domain.logic.filterIngredientsByInventoryStatus
import dev.nighthawklabs.homebar.domain.logic.filterRecipes
import dev.nighthawklabs.homebar.domain.logic.matchRecipe
import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.InventoryStatusFilter
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient
import dev.nighthawklabs.homebar.domain.model.RecipeListFilterState
import dev.nighthawklabs.homebar.domain.model.RecipeMakeabilityFilter
import dev.nighthawklabs.homebar.domain.model.RecipeMatchStatus
import dev.nighthawklabs.homebar.domain.model.RecipeWithMatchResult
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import dev.nighthawklabs.homebar.ui.recipes.detail.createRecipeDetailUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class InventoryToRecipeRefreshTest {
    private val colaGroup = SubstitutionGroup("cola", "Cola", listOf("coke", "pepsi"))

    @Test
    fun `recipe list makeability refreshes after inventory changes`() {
        val recipe = cubaLibre()
        val beforeIngredients = listOf(
            ingredient("rum", inStock = true),
            ingredient("coke", inStock = false),
            ingredient("pepsi", inStock = false),
        )
        val afterIngredients = beforeIngredients.map { ingredient ->
            if (ingredient.id == "pepsi") ingredient.markedInStock() else ingredient
        }

        assertEquals(emptyList<String>(), makeableRecipeIds(listOf(recipe), beforeIngredients, listOf(colaGroup)))
        assertEquals(listOf("cuba-libre"), makeableRecipeIds(listOf(recipe), afterIngredients, listOf(colaGroup)))
    }

    @Test
    fun `ingredient-filtered recipe list refreshes after substitute changes`() {
        val recipe = cubaLibre()
        val ingredients = listOf(
            ingredient("rum", inStock = true),
            ingredient("coke", inStock = false),
            ingredient("pepsi", inStock = true),
        )

        assertEquals(
            emptyList<String>(),
            ingredientFilteredRecipeIds(
                recipes = listOf(recipe),
                ingredients = ingredients,
                substitutionGroups = emptyList(),
                ingredientFilter = "pepsi",
            ),
        )
        assertEquals(
            listOf("cuba-libre"),
            ingredientFilteredRecipeIds(
                recipes = listOf(recipe),
                ingredients = ingredients,
                substitutionGroups = listOf(colaGroup),
                ingredientFilter = "pepsi",
            ),
        )
    }

    @Test
    fun `recipe detail refreshes substitution lines after substitute changes`() {
        val recipe = cubaLibre()
        val ingredients = listOf(
            ingredient("rum", "Rum", inStock = true),
            ingredient("coke", "Coke", inStock = false),
            ingredient("pepsi", "Pepsi", inStock = true),
        )

        val beforeState = createRecipeDetailUiState(
            servingState = RecipeServingState(recipe),
            ingredients = ingredients,
            substitutionGroups = emptyList(),
        )
        val afterState = createRecipeDetailUiState(
            servingState = RecipeServingState(recipe),
            ingredients = ingredients,
            substitutionGroups = listOf(colaGroup),
        )

        assertEquals(RecipeMatchStatus.MISSING_INGREDIENTS, beforeState.matchStatus)
        assertEquals(listOf("Coke"), beforeState.missingIngredientNames)
        assertEquals(RecipeMatchStatus.MAKEABLE, afterState.matchStatus)
        assertEquals("Pepsi", afterState.ingredientLines[1].ingredientName)
        assertEquals("Coke", afterState.ingredientLines[1].substituteForName)
        assertEquals(emptyList<String>(), afterState.missingIngredientNames)
    }

    @Test
    fun `favorite inventory filters refresh after inventory changes`() {
        val recipe = favoriteRecipe("margarita", "tequila", "lime-juice")
        val beforeIngredients = listOf(
            ingredient("tequila", inStock = true),
            ingredient("lime-juice", inStock = false),
        )
        val afterIngredients = beforeIngredients.map { ingredient ->
            if (ingredient.id == "lime-juice") ingredient.markedInStock() else ingredient
        }

        assertEquals(
            listOf("lime-juice"),
            missingForFavoriteIds(beforeIngredients, listOf(recipe), emptyList()),
        )
        assertEquals(
            emptyList<String>(),
            missingForFavoriteIds(afterIngredients, listOf(recipe), emptyList()),
        )
    }

    @Test
    fun `favorite inventory filters refresh after substitute changes`() {
        val recipe = favoriteRecipe("cuba-libre", "rum", "coke")
        val ingredients = listOf(
            ingredient("rum", inStock = true),
            ingredient("coke", inStock = false),
            ingredient("pepsi", inStock = true),
        )

        assertEquals(
            listOf("coke"),
            missingForFavoriteIds(ingredients, listOf(recipe), emptyList()),
        )
        assertEquals(
            emptyList<String>(),
            missingForFavoriteIds(ingredients, listOf(recipe), listOf(colaGroup)),
        )
    }

    private fun makeableRecipeIds(
        recipes: List<Recipe>,
        ingredients: List<Ingredient>,
        substitutionGroups: List<SubstitutionGroup>,
    ): List<String> = filterRecipes(
        recipes = recipes.withMatches(ingredients, substitutionGroups),
        filterState = RecipeListFilterState(),
        substitutionGroups = substitutionGroups,
        ingredients = ingredients,
    ).map { recipeWithMatch -> recipeWithMatch.recipe.id }

    private fun ingredientFilteredRecipeIds(
        recipes: List<Recipe>,
        ingredients: List<Ingredient>,
        substitutionGroups: List<SubstitutionGroup>,
        ingredientFilter: String,
    ): List<String> = filterRecipes(
        recipes = recipes.withMatches(ingredients, substitutionGroups),
        filterState = RecipeListFilterState(
            makeabilityFilter = RecipeMakeabilityFilter.ALL_RECIPES,
            ingredientFilter = ingredientFilter,
        ),
        substitutionGroups = substitutionGroups,
        ingredients = ingredients,
    ).map { recipeWithMatch -> recipeWithMatch.recipe.id }

    private fun missingForFavoriteIds(
        ingredients: List<Ingredient>,
        recipes: List<Recipe>,
        substitutionGroups: List<SubstitutionGroup>,
    ): List<String> = filterIngredientsByInventoryStatus(
        ingredients = ingredients,
        recipes = recipes,
        substitutionGroups = substitutionGroups,
        statusFilter = InventoryStatusFilter.MISSING_FOR_FAVORITES,
    ).map { ingredient -> ingredient.id }

    private fun List<Recipe>.withMatches(
        ingredients: List<Ingredient>,
        substitutionGroups: List<SubstitutionGroup>,
    ): List<RecipeWithMatchResult> = map { recipe ->
        RecipeWithMatchResult(
            recipe = recipe,
            matchResult = matchRecipe(recipe, ingredients, substitutionGroups),
        )
    }

    private fun cubaLibre() = Recipe(
        id = "cuba-libre",
        name = "Cuba Libre",
        baseServingCount = 1,
        ingredients = listOf(
            RecipeIngredient("rum", 2.0, "oz", ""),
            RecipeIngredient("coke", 4.0, "oz", ""),
        ),
        instructions = "",
        glassware = "",
        tools = emptyList(),
        garnish = emptyList(),
        tags = emptyList(),
        isFavorite = false,
        isCustom = false,
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

    private fun ingredient(
        id: String,
        name: String = id,
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
