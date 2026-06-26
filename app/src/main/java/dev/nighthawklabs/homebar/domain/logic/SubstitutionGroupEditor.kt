package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup

enum class SubstituteAddStatus {
    UPDATED,
    ALREADY_GROUPED,
    DIFFERENT_GROUP_CONFLICT,
}

data class SubstituteAddResult(
    val groups: List<SubstitutionGroup>,
    val status: SubstituteAddStatus,
)

fun addSubstituteToGroups(
    groups: List<SubstitutionGroup>,
    ingredientId: String,
    substituteIngredientId: String,
    newGroupId: String,
    newGroupName: String,
): SubstituteAddResult {
    require(ingredientId != substituteIngredientId) {
        "An ingredient cannot be a substitute for itself."
    }

    val ingredientGroup = groups.firstOrNull { group -> ingredientId in group.ingredientIds }
    val substituteGroup = groups.firstOrNull { group -> substituteIngredientId in group.ingredientIds }

    return when {
        ingredientGroup == null && substituteGroup == null -> SubstituteAddResult(
            groups = groups + SubstitutionGroup(
                id = newGroupId,
                name = newGroupName,
                ingredientIds = listOf(ingredientId, substituteIngredientId),
            ),
            status = SubstituteAddStatus.UPDATED,
        )

        ingredientGroup != null && substituteGroup == null -> SubstituteAddResult(
            groups = groups.replaceGroup(
                ingredientGroup.copy(
                    ingredientIds = ingredientGroup.ingredientIds + substituteIngredientId,
                ),
            ),
            status = SubstituteAddStatus.UPDATED,
        )

        ingredientGroup == null && substituteGroup != null -> SubstituteAddResult(
            groups = groups.replaceGroup(
                substituteGroup.copy(
                    ingredientIds = substituteGroup.ingredientIds + ingredientId,
                ),
            ),
            status = SubstituteAddStatus.UPDATED,
        )

        ingredientGroup?.id == substituteGroup?.id -> SubstituteAddResult(
            groups = groups,
            status = SubstituteAddStatus.ALREADY_GROUPED,
        )

        else -> SubstituteAddResult(
            groups = groups,
            status = SubstituteAddStatus.DIFFERENT_GROUP_CONFLICT,
        )
    }
}

fun removeSubstituteFromGroups(
    groups: List<SubstitutionGroup>,
    ingredientId: String,
    substituteIngredientId: String,
): List<SubstitutionGroup> {
    val sharedGroup = groups.firstOrNull { group ->
        group.containsBoth(ingredientId, substituteIngredientId)
    } ?: return groups

    val remainingIngredientIds = sharedGroup.ingredientIds.filterNot { id ->
        id == substituteIngredientId
    }

    return if (remainingIngredientIds.size < 2) {
        groups.filterNot { group -> group.id == sharedGroup.id }
    } else {
        groups.replaceGroup(sharedGroup.copy(ingredientIds = remainingIngredientIds))
    }
}

private fun List<SubstitutionGroup>.replaceGroup(
    replacement: SubstitutionGroup,
): List<SubstitutionGroup> = map { group ->
    if (group.id == replacement.id) replacement else group
}
