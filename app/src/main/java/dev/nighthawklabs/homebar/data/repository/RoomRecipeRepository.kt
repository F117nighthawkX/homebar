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
) : RecipeRepository {
    override fun observeRecipes(): Flow<List<Recipe>> =
        recipeDao.observeAllWithIngredients().map { records -> records.map(RecipeWithIngredients::toDomain) }

    override suspend fun getRecipe(recipeId: String): Recipe? = recipeDao.getWithIngredients(recipeId)?.toDomain()

    override suspend fun duplicateRecipe(recipeId: String): Recipe? {
        val duplicate = getRecipe(recipeId)?.duplicatedAsCustom(newId = UUID.randomUUID().toString())
            ?: return null
        recipeDao.insertWithIngredients(duplicate.toEntity(), duplicate.toIngredientEntities())
        return duplicate
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateWithIngredients(recipe.toEntity(), recipe.toIngredientEntities())
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
