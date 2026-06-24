package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.domain.model.PlaceholderRecipe
import kotlinx.coroutines.flow.Flow

/** Repository boundary for the recipe data that will be introduced in later buckets. */
interface RecipeRepository {
    fun observePlaceholderRecipes(): Flow<List<PlaceholderRecipe>>
    fun getPlaceholderRecipe(id: String): PlaceholderRecipe?
}

