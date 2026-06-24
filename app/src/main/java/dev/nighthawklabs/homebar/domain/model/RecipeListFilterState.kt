package dev.nighthawklabs.homebar.domain.model

/** Filter choices for the future recipe-list UI. This state is not persisted yet. */
data class RecipeListFilterState(
    val makeabilityFilter: RecipeMakeabilityFilter = RecipeMakeabilityFilter.MAKEABLE_NOW,
    val searchText: String = "",
    val ingredientFilter: String? = null,
    val favoriteOnly: Boolean = false,
)

enum class RecipeMakeabilityFilter {
    MAKEABLE_NOW,
    MISSING_ONE_INGREDIENT,
    ALL_RECIPES,
}

data class RecipeWithMatchResult(
    val recipe: Recipe,
    val matchResult: RecipeMatchResult,
)
