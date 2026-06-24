package dev.nighthawklabs.homebar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.nighthawklabs.homebar.data.local.entity.RecipeEntity
import dev.nighthawklabs.homebar.data.local.entity.RecipeIngredientEntity
import dev.nighthawklabs.homebar.data.local.entity.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecipeDao {
    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name")
    abstract fun observeAllWithIngredients(): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    abstract suspend fun getWithIngredients(recipeId: String): RecipeWithIngredients?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertRecipeIngredients(ingredients: List<RecipeIngredientEntity>)

    @Update
    protected abstract suspend fun updateRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    protected abstract suspend fun deleteRecipeIngredients(recipeId: String)

    @Transaction
    open suspend fun insertWithIngredients(
        recipe: RecipeEntity,
        ingredients: List<RecipeIngredientEntity>,
    ) {
        insertRecipe(recipe)
        insertRecipeIngredients(ingredients)
    }

    @Transaction
    open suspend fun updateWithIngredients(
        recipe: RecipeEntity,
        ingredients: List<RecipeIngredientEntity>,
    ) {
        updateRecipe(recipe)
        deleteRecipeIngredients(recipe.id)
        insertRecipeIngredients(ingredients)
    }
}
