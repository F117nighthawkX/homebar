package dev.nighthawklabs.homebar.data.local

import androidx.room.TypeConverter
import java.util.Base64

/** Stores recipe text lists without reserving any user-visible character as a separator. */
class RecipeConverters {
    @TypeConverter
    fun stringsToStorage(values: List<String>): String = values.joinToString(",") { value ->
        Base64.getEncoder().encodeToString(value.toByteArray(Charsets.UTF_8))
    }

    @TypeConverter
    fun storageToStrings(value: String): List<String> = if (value.isEmpty()) {
        emptyList()
    } else {
        value.split(",").map { encoded ->
            Base64.getDecoder().decode(encoded).toString(Charsets.UTF_8)
        }
    }
}
