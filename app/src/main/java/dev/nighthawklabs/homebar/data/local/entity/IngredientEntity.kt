package dev.nighthawklabs.homebar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.nighthawklabs.homebar.domain.model.IngredientCategory

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: IngredientCategory,
    val inStock: Boolean,
    val runningLow: Boolean,
    val notes: String,
)
