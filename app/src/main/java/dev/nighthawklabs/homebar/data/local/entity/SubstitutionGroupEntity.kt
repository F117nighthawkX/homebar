package dev.nighthawklabs.homebar.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "substitution_groups")
data class SubstitutionGroupEntity(
    @PrimaryKey val id: String,
    val name: String,
)

@Entity(
    tableName = "substitution_group_ingredients",
    primaryKeys = ["groupId", "ingredientId"],
    foreignKeys = [
        ForeignKey(
            entity = SubstitutionGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
        ),
    ],
    indices = [Index("ingredientId")],
)
data class SubstitutionGroupIngredientCrossRef(
    val groupId: String,
    val ingredientId: String,
)

data class SubstitutionGroupWithIngredients(
    @Embedded val group: SubstitutionGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = SubstitutionGroupIngredientCrossRef::class,
            parentColumn = "groupId",
            entityColumn = "ingredientId",
        ),
    )
    val ingredients: List<IngredientEntity>,
)
