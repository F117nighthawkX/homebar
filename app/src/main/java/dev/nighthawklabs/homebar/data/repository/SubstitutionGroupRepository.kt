package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import dev.nighthawklabs.homebar.domain.logic.SubstituteAddStatus
import kotlinx.coroutines.flow.Flow

interface SubstitutionGroupRepository {
    fun observeSubstitutionGroups(): Flow<List<SubstitutionGroup>>

    suspend fun addSubstitute(
        ingredientId: String,
        substituteIngredientId: String,
    ): SubstituteAddStatus

    suspend fun removeSubstitute(ingredientId: String, substituteIngredientId: String)
}
