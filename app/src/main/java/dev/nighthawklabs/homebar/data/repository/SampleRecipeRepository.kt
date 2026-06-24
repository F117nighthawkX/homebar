package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.domain.model.PlaceholderRecipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SampleRecipeRepository : RecipeRepository {
    private val recipes = listOf(
        PlaceholderRecipe(
            id = "placeholder-old-fashioned",
            name = "Recipe placeholder",
            description = "Recipe content arrives in Bucket 1.",
        ),
    )

    override fun observePlaceholderRecipes(): Flow<List<PlaceholderRecipe>> = flowOf(recipes)

    override fun getPlaceholderRecipe(id: String): PlaceholderRecipe? = recipes.find { it.id == id }
}

