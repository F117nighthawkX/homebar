package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun observeRecipes(): Flow<List<Recipe>>

    suspend fun getRecipe(recipeId: String): Recipe?

    suspend fun duplicateRecipe(recipeId: String): Recipe?

    suspend fun insertCustomRecipe(recipe: Recipe)

    suspend fun updateRecipe(recipe: Recipe)

    suspend fun updateCustomRecipe(recipe: Recipe): Boolean

    suspend fun deleteCustomRecipe(recipeId: String): Boolean
}
