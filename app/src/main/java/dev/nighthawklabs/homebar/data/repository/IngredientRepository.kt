package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import kotlinx.coroutines.flow.Flow

interface IngredientRepository {
    fun observeIngredients(): Flow<List<Ingredient>>

    suspend fun createIngredient(
        name: String,
        category: IngredientCategory,
    ): Ingredient

    suspend fun markInStock(ingredientId: String)

    suspend fun markNotInStock(ingredientId: String)

    suspend fun markRunningLow(ingredientId: String)

    suspend fun clearRunningLow(ingredientId: String)
}
