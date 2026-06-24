package dev.nighthawklabs.homebar.domain.model

enum class RecipeMatchStatus {
    MAKEABLE,
    MISSING_INGREDIENTS,
}

/** Derived availability data for one recipe. This is never persisted. */
data class RecipeMatchResult(
    val status: RecipeMatchStatus,
    val missingIngredients: List<String>,
    val substitutionsUsed: List<SubstitutionUsed>,
    val runningLowIngredients: List<String>,
)
