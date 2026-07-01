package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.data.local.dao.IngredientDao
import dev.nighthawklabs.homebar.data.local.entity.IngredientEntity
import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomIngredientRepository(
    private val ingredientDao: IngredientDao,
) : IngredientRepository {
    override fun observeIngredients(): Flow<List<Ingredient>> =
        ingredientDao.observeAll().map { ingredients -> ingredients.map(IngredientEntity::toDomain) }

    override suspend fun createIngredient(
        name: String,
        category: IngredientCategory,
    ): Ingredient {
        val trimmedName = name.trim()
        require(trimmedName.isNotBlank()) { "Ingredient name is required." }

        val ingredient = Ingredient(
            id = UUID.randomUUID().toString(),
            name = trimmedName,
            category = category,
            inStock = false,
            runningLow = false,
            notes = "",
        )
        ingredientDao.insert(ingredient.toEntity())
        return ingredient
    }

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

private fun Ingredient.toEntity(): IngredientEntity = IngredientEntity(
    id = id,
    name = name,
    category = category,
    inStock = inStock,
    runningLow = runningLow,
    notes = notes,
)
