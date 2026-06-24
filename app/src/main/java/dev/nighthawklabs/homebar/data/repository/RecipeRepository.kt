package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun observeRecipes(): Flow<List<Recipe>>

    suspend fun getRecipe(recipeId: String): Recipe?

    suspend fun duplicateRecipe(recipeId: String): Recipe?

    suspend fun updateRecipe(recipe: Recipe)
}
