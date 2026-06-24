package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientSatisfaction
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeMatchResult
import dev.nighthawklabs.homebar.domain.model.RecipeMatchStatus
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import dev.nighthawklabs.homebar.domain.model.SubstitutionUsed

/** Calculates recipe availability from the current inventory and substitute groups. */
fun matchRecipe(
    recipe: Recipe,
    ingredients: Collection<Ingredient>,
    substitutionGroups: Collection<SubstitutionGroup>,
): RecipeMatchResult {
    val inventoryById = ingredients.associateBy(Ingredient::id)
    val missingIngredients = mutableListOf<String>()
    val substitutionsUsed = mutableListOf<SubstitutionUsed>()
    val runningLowIngredients = linkedSetOf<String>()

    recipe.ingredients.forEach { recipeIngredient ->
        when (
            val satisfaction = satisfyIngredient(
                requiredIngredientId = recipeIngredient.ingredientId,
                ingredients = ingredients,
                substitutionGroups = substitutionGroups,
            )
        ) {
            is IngredientSatisfaction.Direct -> {
                if (inventoryById[satisfaction.ingredientId]?.runningLow == true) {
                    runningLowIngredients += satisfaction.ingredientId
                }
            }

            is IngredientSatisfaction.Substitute -> {
                substitutionsUsed += satisfaction.substitution
                if (inventoryById[satisfaction.substitution.substituteIngredientId]?.runningLow == true) {
                    runningLowIngredients += satisfaction.substitution.substituteIngredientId
                }
            }

            is IngredientSatisfaction.Missing -> missingIngredients += satisfaction.ingredientId
        }
    }

    return RecipeMatchResult(
        status = if (missingIngredients.isEmpty()) {
            RecipeMatchStatus.MAKEABLE
        } else {
            RecipeMatchStatus.MISSING_INGREDIENTS
        },
        missingIngredients = missingIngredients.distinct(),
        substitutionsUsed = substitutionsUsed,
        runningLowIngredients = runningLowIngredients.toList(),
    )
}
