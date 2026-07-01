package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.data.local.dao.RecipeDao
import dev.nighthawklabs.homebar.data.local.entity.RecipeEntity
import dev.nighthawklabs.homebar.data.local.entity.RecipeIngredientEntity
import dev.nighthawklabs.homebar.data.local.entity.RecipeWithIngredients
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomRecipeRepository(
    private val recipeDao: RecipeDao,
    private val nowMillis: () -> Long = System::currentTimeMillis,
) : RecipeRepository {
    override fun observeRecipes(): Flow<List<Recipe>> =
        recipeDao.observeAllWithIngredients().map { records -> records.map(RecipeWithIngredients::toDomain) }

    override suspend fun getRecipe(recipeId: String): Recipe? = recipeDao.getWithIngredients(recipeId)?.toDomain()

    override suspend fun duplicateRecipe(recipeId: String): Recipe? {
        val duplicate = getRecipe(recipeId)?.duplicatedAsCustom(
            newId = UUID.randomUUID().toString(),
            nowMillis = nowMillis(),
        )
            ?: return null
        recipeDao.insertWithIngredients(duplicate.toEntity(), duplicate.toIngredientEntities())
        return duplicate
    }

    override suspend fun insertCustomRecipe(recipe: Recipe) {
        require(recipe.isCustom) { "Only custom recipes can be inserted from the editor." }
        recipeDao.insertWithIngredients(recipe.toEntity(), recipe.toIngredientEntities())
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateWithIngredients(recipe.toEntity(), recipe.toIngredientEntities())
    }

    override suspend fun updateCustomRecipe(recipe: Recipe): Boolean {
        require(recipe.isCustom) { "Only custom recipes can be updated from the editor." }
        val existingRecipe = getRecipe(recipe.id) ?: return false
        if (!existingRecipe.isCustom) return false

        recipeDao.updateWithIngredients(recipe.toEntity(), recipe.toIngredientEntities())
        return true
    }

    override suspend fun deleteCustomRecipe(recipeId: String): Boolean {
        val recipe = getRecipe(recipeId) ?: return false
        if (!recipe.isCustom) return false

        recipeDao.deleteWithIngredients(recipeId)
        return true
    }

    suspend fun insertRecipesIfAbsent(recipes: List<Recipe>) {
        recipes.forEach { recipe ->
            recipeDao.insertWithIngredients(recipe.toEntity(), recipe.toIngredientEntities())
        }
    }
}

fun RecipeWithIngredients.toDomain(): Recipe = Recipe(
    id = recipe.id,
    name = recipe.name,
    baseServingCount = recipe.baseServingCount,
    ingredients = ingredients
        .sortedBy(RecipeIngredientEntity::position)
        .map { ingredient ->
            RecipeIngredient(
                ingredientId = ingredient.ingredientId,
                quantity = ingredient.quantity,
                unit = ingredient.unit,
                note = ingredient.note,
            )
        },
    instructions = recipe.instructions,
    glassware = recipe.glassware,
    tools = recipe.tools,
    garnish = recipe.garnish,
    tags = recipe.tags,
    isFavorite = recipe.isFavorite,
    isCustom = recipe.isCustom,
    sourceRecipeId = recipe.sourceRecipeId,
    createdAt = recipe.createdAt,
    updatedAt = recipe.updatedAt,
)

private fun Recipe.toEntity(): RecipeEntity = RecipeEntity(
    id = id,
    name = name,
    baseServingCount = baseServingCount,
    instructions = instructions,
    glassware = glassware,
    tools = tools,
    garnish = garnish,
    tags = tags,
    isFavorite = isFavorite,
    isCustom = isCustom,
    sourceRecipeId = sourceRecipeId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun Recipe.toIngredientEntities(): List<RecipeIngredientEntity> =
    ingredients.mapIndexed { position, ingredient ->
        RecipeIngredientEntity(
            recipeId = id,
            ingredientId = ingredient.ingredientId,
            quantity = ingredient.quantity,
            unit = ingredient.unit,
            note = ingredient.note,
            position = position,
        )
    }
