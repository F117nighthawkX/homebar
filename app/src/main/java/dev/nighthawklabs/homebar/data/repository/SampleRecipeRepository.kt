package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.domain.model.PlaceholderRecipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** Bucket 0 presentation placeholder retained until Bucket 2 replaces its screens. */
class SampleRecipeRepository {
    private val recipes = listOf(
        PlaceholderRecipe(
            id = "placeholder-old-fashioned",
            name = "Recipe placeholder",
            description = "Recipe content arrives in Bucket 1.",
        ),
    )

    fun observePlaceholderRecipes(): Flow<List<PlaceholderRecipe>> = flowOf(recipes)

    fun getPlaceholderRecipe(id: String): PlaceholderRecipe? = recipes.find { it.id == id }
}
