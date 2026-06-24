package dev.nighthawklabs.homebar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.nighthawklabs.homebar.data.local.dao.IngredientDao
import dev.nighthawklabs.homebar.data.local.dao.RecipeDao
import dev.nighthawklabs.homebar.data.local.entity.IngredientEntity
import dev.nighthawklabs.homebar.data.local.entity.RecipeEntity
import dev.nighthawklabs.homebar.data.local.entity.RecipeIngredientEntity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [IngredientEntity::class, RecipeEntity::class, RecipeIngredientEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(IngredientConverters::class, RecipeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao

    abstract fun recipeDao(): RecipeDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `recipes` (" +
                        "`id` TEXT NOT NULL, `name` TEXT NOT NULL, " +
                        "`baseServingCount` INTEGER NOT NULL, `instructions` TEXT NOT NULL, " +
                        "`glassware` TEXT NOT NULL, `tools` TEXT NOT NULL, " +
                        "`garnish` TEXT NOT NULL, `tags` TEXT NOT NULL, " +
                        "`isFavorite` INTEGER NOT NULL, `isCustom` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))",
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `recipe_ingredients` (" +
                        "`recipeId` TEXT NOT NULL, `ingredientId` TEXT NOT NULL, " +
                        "`quantity` REAL NOT NULL, `unit` TEXT NOT NULL, `note` TEXT NOT NULL, " +
                        "`position` INTEGER NOT NULL, PRIMARY KEY(`recipeId`, `position`), " +
                        "FOREIGN KEY(`recipeId`) REFERENCES `recipes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`ingredientId`) REFERENCES `ingredients`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_recipe_ingredients_recipeId` " +
                        "ON `recipe_ingredients` (`recipeId`)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_recipe_ingredients_ingredientId` " +
                        "ON `recipe_ingredients` (`ingredientId`)",
                )
            }
        }
    }
}
