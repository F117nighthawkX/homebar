package dev.nighthawklabs.homebar.data.local

import androidx.room.TypeConverter
import dev.nighthawklabs.homebar.domain.model.IngredientCategory

class IngredientConverters {
    @TypeConverter
    fun categoryToString(category: IngredientCategory): String = category.name

    @TypeConverter
    fun stringToCategory(value: String): IngredientCategory = IngredientCategory.valueOf(value)
}
