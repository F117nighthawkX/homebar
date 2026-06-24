package dev.nighthawklabs.homebar.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val baseServingCount: Int,
    val instructions: String,
    val glassware: String,
    val tools: List<String>,
    val garnish: List<String>,
    val tags: List<String>,
    val isFavorite: Boolean,
    val isCustom: Boolean,
)

@Entity(
    tableName = "recipe_ingredients",
    primaryKeys = ["recipeId", "position"],
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
        ),
    ],
    indices = [Index("recipeId"), Index("ingredientId")],
)
data class RecipeIngredientEntity(
    val recipeId: String,
    val ingredientId: String,
    val quantity: Double,
    val unit: String,
    val note: String,
    val position: Int,
)

data class RecipeWithIngredients(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
    )
    val ingredients: List<RecipeIngredientEntity>,
)
