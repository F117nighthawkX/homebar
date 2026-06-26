package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import kotlinx.coroutines.flow.Flow

interface SubstitutionGroupRepository {
    fun observeSubstitutionGroups(): Flow<List<SubstitutionGroup>>

    suspend fun removeSubstitute(ingredientId: String, substituteIngredientId: String)
}
