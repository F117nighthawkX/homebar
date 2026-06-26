package dev.nighthawklabs.homebar.data.repository

import dev.nighthawklabs.homebar.data.local.dao.SubstitutionGroupDao
import dev.nighthawklabs.homebar.data.local.entity.SubstitutionGroupEntity
import dev.nighthawklabs.homebar.data.local.entity.SubstitutionGroupIngredientCrossRef
import dev.nighthawklabs.homebar.data.local.entity.SubstitutionGroupWithIngredients
import dev.nighthawklabs.homebar.domain.logic.SubstituteAddStatus
import dev.nighthawklabs.homebar.domain.logic.addSubstituteToGroups
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

    override suspend fun addSubstitute(
        ingredientId: String,
        substituteIngredientId: String,
    ): SubstituteAddStatus {
        val currentGroups = substitutionGroupDao.getAllWithIngredients()
            .map(SubstitutionGroupWithIngredients::toDomain)
        val result = addSubstituteToGroups(
            groups = currentGroups,
            ingredientId = ingredientId,
            substituteIngredientId = substituteIngredientId,
            newGroupId = substitutionGroupId(ingredientId, substituteIngredientId),
            newGroupName = "Substitutes",
        )

        if (result.status == SubstituteAddStatus.UPDATED) {
            result.groups.forEach { group ->
                substitutionGroupDao.insertWithIngredients(group.toEntity(), group.toIngredientReferences())
            }
        }

        return result.status
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

private fun substitutionGroupId(
    ingredientId: String,
    substituteIngredientId: String,
): String = listOf(ingredientId, substituteIngredientId)
    .sorted()
    .joinToString(prefix = "substitute-", separator = "-")
