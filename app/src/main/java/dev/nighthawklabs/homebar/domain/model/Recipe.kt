package dev.nighthawklabs.homebar.domain.model

data class Recipe(
    val id: String,
    val name: String,
    val baseServingCount: Int,
    val ingredients: List<RecipeIngredient>,
    val instructions: String,
    val glassware: String,
    val tools: List<String>,
    val garnish: List<String>,
    val tags: List<String>,
    val isFavorite: Boolean,
    val isCustom: Boolean,
) {
    init {
        require(baseServingCount >= 1) { "A recipe must make at least one serving." }
    }

    /** A duplicate is always independently editable custom recipe data. */
    fun duplicatedAsCustom(newId: String, newName: String = "$name Copy"): Recipe = copy(
        id = newId,
        name = newName,
        ingredients = ingredients.map { it.copy() },
        isFavorite = false,
        isCustom = true,
    )
}

data class RecipeIngredient(
    val ingredientId: String,
    val quantity: Double,
    val unit: String,
    val note: String,
) {
    init {
        require(quantity > 0) { "Recipe ingredient quantity must be greater than zero." }
    }
}
