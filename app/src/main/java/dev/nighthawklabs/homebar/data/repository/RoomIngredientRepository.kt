package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.data.local.dao.IngredientDao
import dev.nighthawklabs.homebar.data.local.entity.IngredientEntity
import dev.nighthawklabs.homebar.domain.model.Ingredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomIngredientRepository(
    private val ingredientDao: IngredientDao,
) : IngredientRepository {
    override fun observeIngredients(): Flow<List<Ingredient>> =
        ingredientDao.observeAll().map { ingredients -> ingredients.map(IngredientEntity::toDomain) }

    override suspend fun markInStock(ingredientId: String) {
        ingredientDao.setInStock(ingredientId, inStock = true)
    }

    override suspend fun markNotInStock(ingredientId: String) {
        ingredientDao.setInStock(ingredientId, inStock = false)
    }

    override suspend fun markRunningLow(ingredientId: String) {
        ingredientDao.setRunningLow(ingredientId, runningLow = true)
    }

    override suspend fun clearRunningLow(ingredientId: String) {
        ingredientDao.setRunningLow(ingredientId, runningLow = false)
    }
}

fun IngredientEntity.toDomain(): Ingredient = Ingredient(
    id = id,
    name = name,
    category = category,
    inStock = inStock,
    runningLow = runningLow,
    notes = notes,
)
