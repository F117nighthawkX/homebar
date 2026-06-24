package dev.nighthawklabs.homebar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.nighthawklabs.homebar.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredients ORDER BY name")
    fun observeAll(): Flow<List<IngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(ingredients: List<IngredientEntity>)

    @Query(
        "UPDATE ingredients SET inStock = :inStock, " +
            "runningLow = CASE WHEN :inStock THEN runningLow ELSE 0 END " +
            "WHERE id = :ingredientId",
    )
    suspend fun setInStock(ingredientId: String, inStock: Boolean)

    @Query(
        "UPDATE ingredients SET inStock = CASE WHEN :runningLow THEN 1 ELSE inStock END, " +
            "runningLow = :runningLow WHERE id = :ingredientId",
    )
    suspend fun setRunningLow(ingredientId: String, runningLow: Boolean)
}
