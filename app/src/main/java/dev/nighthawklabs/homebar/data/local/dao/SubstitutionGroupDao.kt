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

    @Transaction
    open suspend fun insertWithIngredients(
        group: SubstitutionGroupEntity,
        references: List<SubstitutionGroupIngredientCrossRef>,
    ) {
        insertGroup(group)
        insertIngredientReferences(references)
    }
}
