package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.data.local.dao.SubstitutionGroupDao
import dev.nighthawklabs.homebar.data.local.entity.SubstitutionGroupEntity
import dev.nighthawklabs.homebar.data.local.entity.SubstitutionGroupIngredientCrossRef
import dev.nighthawklabs.homebar.data.local.entity.SubstitutionGroupWithIngredients
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomSubstitutionGroupRepository(
    private val substitutionGroupDao: SubstitutionGroupDao,
) : SubstitutionGroupRepository {
    override fun observeSubstitutionGroups(): Flow<List<SubstitutionGroup>> =
        substitutionGroupDao.observeAllWithIngredients().map { groups ->
            groups.map(SubstitutionGroupWithIngredients::toDomain)
        }

    override suspend fun removeSubstitute(ingredientId: String, substituteIngredientId: String) {
        substitutionGroupDao.removeSubstitute(ingredientId, substituteIngredientId)
    }

    suspend fun insertGroupsIfAbsent(groups: List<SubstitutionGroup>) {
        groups.forEach { group ->
            substitutionGroupDao.insertWithIngredients(group.toEntity(), group.toIngredientReferences())
        }
    }
}

fun SubstitutionGroupWithIngredients.toDomain(): SubstitutionGroup = SubstitutionGroup(
    id = group.id,
    name = group.name,
    ingredientIds = ingredients.map { ingredient -> ingredient.id },
)

private fun SubstitutionGroup.toEntity(): SubstitutionGroupEntity = SubstitutionGroupEntity(
    id = id,
    name = name,
)

private fun SubstitutionGroup.toIngredientReferences(): List<SubstitutionGroupIngredientCrossRef> =
    ingredientIds.map { ingredientId ->
        SubstitutionGroupIngredientCrossRef(groupId = id, ingredientId = ingredientId)
    }
