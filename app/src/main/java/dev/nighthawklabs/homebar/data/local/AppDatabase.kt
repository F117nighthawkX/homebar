package dev.nighthawklabs.homebar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.nighthawklabs.homebar.data.local.dao.IngredientDao
import dev.nighthawklabs.homebar.data.local.entity.IngredientEntity

@Database(
    entities = [IngredientEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(IngredientConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
}
