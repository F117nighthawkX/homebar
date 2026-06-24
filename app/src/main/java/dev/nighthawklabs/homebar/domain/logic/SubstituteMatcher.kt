package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientSatisfaction
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import dev.nighthawklabs.homebar.domain.model.SubstitutionUsed

/** Resolves one required ingredient against direct inventory and approved substitute groups. */
fun satisfyIngredient(
    requiredIngredientId: String,
    ingredients: Collection<Ingredient>,
    substitutionGroups: Collection<SubstitutionGroup>,
): IngredientSatisfaction {
    val inventoryById = ingredients.associateBy(Ingredient::id)
    if (inventoryById[requiredIngredientId]?.isAvailableForRecipeMatching == true) {
        return IngredientSatisfaction.Direct(requiredIngredientId)
    }

    val substituteIngredientId = substitutionGroups
        .asSequence()
        .filter { group -> requiredIngredientId in group.ingredientIds }
        .flatMap { group -> group.ingredientIds.asSequence() }
        .firstOrNull { ingredientId ->
            ingredientId != requiredIngredientId &&
                inventoryById[ingredientId]?.isAvailableForRecipeMatching == true
        }

    return if (substituteIngredientId == null) {
        IngredientSatisfaction.Missing(requiredIngredientId)
    } else {
        IngredientSatisfaction.Substitute(
            SubstitutionUsed(
                requiredIngredientId = requiredIngredientId,
                substituteIngredientId = substituteIngredientId,
            ),
        )
    }
}
