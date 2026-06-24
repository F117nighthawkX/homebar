package dev.nighthawklabs.homebar.domain.model

/** A consumable item that can be available for a cocktail recipe. */
data class Ingredient(
    val id: String,
    val name: String,
    val category: IngredientCategory,
    val inStock: Boolean,
    val runningLow: Boolean,
    val notes: String,
) {
    /** Running low is a warning; it does not make an ingredient unavailable. */
    val isAvailableForRecipeMatching: Boolean
        get() = inStock

    fun markedInStock(): Ingredient = copy(inStock = true)

    fun markedNotInStock(): Ingredient = copy(inStock = false, runningLow = false)

    fun markedRunningLow(): Ingredient = copy(inStock = true, runningLow = true)

    fun clearedRunningLow(): Ingredient = copy(runningLow = false)
}

enum class IngredientCategory {
    SPIRIT,
    LIQUEUR,
    MIXER,
    JUICE,
    SYRUP,
    BITTERS,
    GARNISH,
    OTHER,
}
