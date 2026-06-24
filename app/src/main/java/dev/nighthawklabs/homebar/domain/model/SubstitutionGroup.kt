package dev.nighthawklabs.homebar.domain.model

data class SubstitutionGroup(
    val id: String,
    val name: String,
    val ingredientIds: List<String>,
) {
    init {
        require(ingredientIds.size >= 2) { "A substitution group needs at least two ingredients." }
        require(ingredientIds.distinct().size == ingredientIds.size) {
            "A substitution group cannot contain an ingredient more than once."
        }
    }

    fun containsBoth(firstIngredientId: String, secondIngredientId: String): Boolean =
        firstIngredientId != secondIngredientId &&
            firstIngredientId in ingredientIds &&
            secondIngredientId in ingredientIds
}

data class SubstitutionUsed(
    val requiredIngredientId: String,
    val substituteIngredientId: String,
)

sealed interface IngredientSatisfaction {
    data class Direct(val ingredientId: String) : IngredientSatisfaction

    data class Substitute(val substitution: SubstitutionUsed) : IngredientSatisfaction

    data class Missing(val ingredientId: String) : IngredientSatisfaction
}
