package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.InventoryStatusFilter
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup

fun filterIngredientsByInventoryStatus(
    ingredients: List<Ingredient>,
    recipes: List<Recipe>,
    substitutionGroups: List<SubstitutionGroup>,
    statusFilter: InventoryStatusFilter,
): List<Ingredient> {
    val favoriteRecipeInventoryStatus = favoriteRecipeInventoryStatus(
        ingredients = ingredients,
        recipes = recipes,
        substitutionGroups = substitutionGroups,
    )

    return ingredients.filter { ingredient ->
        when (statusFilter) {
            InventoryStatusFilter.ALL -> true
            InventoryStatusFilter.IN_STOCK -> ingredient.inStock
            InventoryStatusFilter.MISSING -> !ingredient.inStock
            InventoryStatusFilter.RUNNING_LOW -> ingredient.runningLow
            InventoryStatusFilter.MISSING_FOR_FAVORITES ->
                ingredient.id in favoriteRecipeInventoryStatus.missingIngredientIds
            InventoryStatusFilter.RUNNING_LOW_FOR_FAVORITES ->
                ingredient.id in favoriteRecipeInventoryStatus.runningLowIngredientIds
            InventoryStatusFilter.MISSING_OR_RUNNING_LOW_FOR_FAVORITES ->
                ingredient.id in favoriteRecipeInventoryStatus.missingIngredientIds ||
                    ingredient.id in favoriteRecipeInventoryStatus.runningLowIngredientIds
        }
    }
}

private data class FavoriteRecipeInventoryStatus(
    val missingIngredientIds: Set<String>,
    val runningLowIngredientIds: Set<String>,
)

private fun favoriteRecipeInventoryStatus(
    ingredients: List<Ingredient>,
    recipes: List<Recipe>,
    substitutionGroups: List<SubstitutionGroup>,
): FavoriteRecipeInventoryStatus {
    val missingIngredientIds = linkedSetOf<String>()
    val runningLowIngredientIds = linkedSetOf<String>()

    recipes
        .filter { recipe -> recipe.isFavorite }
        .forEach { recipe ->
            val matchResult = matchRecipe(
                recipe = recipe,
                ingredients = ingredients,
                substitutionGroups = substitutionGroups,
            )
            missingIngredientIds += matchResult.missingIngredients
            runningLowIngredientIds += matchResult.runningLowIngredients
        }

    return FavoriteRecipeInventoryStatus(
        missingIngredientIds = missingIngredientIds,
        runningLowIngredientIds = runningLowIngredientIds,
    )
}
