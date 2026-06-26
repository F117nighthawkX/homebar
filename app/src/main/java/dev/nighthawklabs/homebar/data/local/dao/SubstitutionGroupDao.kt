package dev.nighthawklabs.homebar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.nighthawklabs.homebar.data.local.entity.SubstitutionGroupEntity
import dev.nighthawklabs.homebar.data.local.entity.SubstitutionGroupIngredientCrossRef
import dev.nighthawklabs.homebar.data.local.entity.SubstitutionGroupWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SubstitutionGroupDao {
    @Transaction
    @Query("SELECT * FROM substitution_groups ORDER BY name")
    abstract fun observeAllWithIngredients(): Flow<List<SubstitutionGroupWithIngredients>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertGroup(group: SubstitutionGroupEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract suspend fun insertIngredientReferences(
        references: List<SubstitutionGroupIngredientCrossRef>,
    )

    @Query("SELECT groupId FROM substitution_group_ingredients WHERE ingredientId = :ingredientId")
    protected abstract suspend fun getGroupIdsForIngredient(ingredientId: String): List<String>

    @Query("SELECT ingredientId FROM substitution_group_ingredients WHERE groupId = :groupId")
    protected abstract suspend fun getIngredientIdsForGroup(groupId: String): List<String>

    @Query(
        "DELETE FROM substitution_group_ingredients " +
            "WHERE groupId = :groupId AND ingredientId = :ingredientId",
    )
    protected abstract suspend fun deleteIngredientReference(groupId: String, ingredientId: String)

    @Query("DELETE FROM substitution_groups WHERE id = :groupId")
    protected abstract suspend fun deleteGroup(groupId: String)

    @Transaction
    open suspend fun insertWithIngredients(
        group: SubstitutionGroupEntity,
        references: List<SubstitutionGroupIngredientCrossRef>,
    ) {
        insertGroup(group)
        insertIngredientReferences(references)
    }

    @Transaction
    open suspend fun removeSubstitute(
        ingredientId: String,
        substituteIngredientId: String,
    ) {
        val ingredientGroupIds = getGroupIdsForIngredient(ingredientId).toSet()
        val substituteGroupIds = getGroupIdsForIngredient(substituteIngredientId).toSet()
        val sharedGroupId = ingredientGroupIds.intersect(substituteGroupIds).firstOrNull() ?: return

        deleteIngredientReference(sharedGroupId, substituteIngredientId)

        if (getIngredientIdsForGroup(sharedGroupId).size < 2) {
            deleteGroup(sharedGroupId)
        }
    }
}
