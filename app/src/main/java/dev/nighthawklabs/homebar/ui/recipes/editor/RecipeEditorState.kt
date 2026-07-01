package dev.nighthawklabs.homebar.ui.recipes.editor

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient

data class RecipeEditorUiState(
    val isLoading: Boolean = false,
    val isNotFound: Boolean = false,
    val recipeId: String? = null,
    val name: String = "",
    val baseServingCount: String = "1",
    val ingredientLines: List<RecipeEditorIngredientLineUiState> = listOf(
        RecipeEditorIngredientLineUiState(),
    ),
    val instructions: String = "",
    val glassware: String = "",
    val tools: String = "",
    val garnish: String = "",
    val tags: String = "",
    val isFavorite: Boolean = false,
    val canSave: Boolean = false,
    val ingredientOptions: List<RecipeEditorIngredientOption> = emptyList(),
)

data class RecipeEditorIngredientOption(
    val id: String,
    val name: String,
)

data class RecipeEditorIngredientLineUiState(
    val ingredientId: String? = null,
    val ingredientName: String = "",
    val unit: String = "",
    val quantity: String = "",
    val note: String = "",
) {
    val hasUnit: Boolean
        get() = unit.isNotBlank()
}

val RecipeIngredientUnitOptions: List<String> = listOf(
    "oz",
    "dash",
    "barspoon",
    "tsp",
    "tbsp",
    "ml",
    "drop",
    "pinch",
    "wedge",
    "leaf",
    "splash",
    "top",
)

fun createRecipeEditorUiState(
    recipe: Recipe?,
    ingredients: List<Ingredient>,
): RecipeEditorUiState {
    if (recipe == null) return RecipeEditorUiState()

    val ingredientNames = ingredients.associate { ingredient -> ingredient.id to ingredient.name }
    return RecipeEditorUiState(
        recipeId = recipe.id,
        name = recipe.name,
        baseServingCount = recipe.baseServingCount.toString(),
        ingredientLines = recipe.ingredients.ifEmpty {
            listOf(RecipeIngredient("", 1.0, "oz", ""))
        }.map { ingredient ->
            RecipeEditorIngredientLineUiState(
                ingredientId = ingredient.ingredientId,
                ingredientName = ingredientNames[ingredient.ingredientId] ?: ingredient.ingredientId,
                unit = ingredient.unit,
                quantity = ingredient.quantity.formatForEditing(),
                note = ingredient.note,
            )
        },
        instructions = recipe.instructions,
        glassware = recipe.glassware,
        tools = recipe.tools.joinToString(),
        garnish = recipe.garnish.joinToString(),
        tags = recipe.tags.joinToString(),
        isFavorite = recipe.isFavorite,
    )
}

fun RecipeEditorUiState.withIngredientOptionsAndSaveAvailability(
    ingredients: List<Ingredient>,
): RecipeEditorUiState = copy(
    canSave = toCustomRecipe(
        existingRecipe = null,
        ingredients = ingredients,
        recipeId = "preview",
        nowMillis = 0L,
    ) != null,
    ingredientOptions = ingredients.map { ingredient ->
        RecipeEditorIngredientOption(
            id = ingredient.id,
            name = ingredient.name,
        )
    },
)

fun RecipeEditorUiState.addIngredientLine(): RecipeEditorUiState =
    copy(ingredientLines = ingredientLines + RecipeEditorIngredientLineUiState())

fun RecipeEditorUiState.removeIngredientLine(index: Int): RecipeEditorUiState {
    if (index !in ingredientLines.indices) return this

    val remainingLines = ingredientLines.filterIndexed { lineIndex, _ -> lineIndex != index }
    return copy(
        ingredientLines = remainingLines.ifEmpty { listOf(RecipeEditorIngredientLineUiState()) },
    )
}

fun RecipeEditorUiState.moveIngredientLineUp(index: Int): RecipeEditorUiState =
    moveIngredientLine(fromIndex = index, toIndex = index - 1)

fun RecipeEditorUiState.moveIngredientLineDown(index: Int): RecipeEditorUiState =
    moveIngredientLine(fromIndex = index, toIndex = index + 1)

fun RecipeEditorUiState.toCustomRecipe(
    existingRecipe: Recipe?,
    ingredients: List<Ingredient>,
    recipeId: String,
    nowMillis: Long,
): Recipe? {
    val trimmedName = name.trim()
    val servingCount = baseServingCount.trim().toIntOrNull()
    val recipeIngredients = ingredientLines.mapNotNull { line -> line.toRecipeIngredient(ingredients) }
    val trimmedInstructions = instructions.trim()

    if (trimmedName.isBlank() || servingCount == null || servingCount < 1) return null
    if (recipeIngredients.isEmpty() || trimmedInstructions.isBlank()) return null

    return Recipe(
        id = recipeId,
        name = trimmedName,
        baseServingCount = servingCount,
        ingredients = recipeIngredients,
        instructions = trimmedInstructions,
        glassware = glassware.trim(),
        tools = tools.toTextList(),
        garnish = garnish.toTextList(),
        tags = tags.toTextList(),
        isFavorite = isFavorite,
        isCustom = true,
        sourceRecipeId = existingRecipe?.sourceRecipeId,
        createdAt = existingRecipe?.createdAt ?: nowMillis,
        updatedAt = nowMillis,
    )
}

private fun RecipeEditorIngredientLineUiState.toRecipeIngredient(
    ingredients: List<Ingredient>,
): RecipeIngredient? {
    val ingredientId = this.ingredientId ?: ingredients.idFor(ingredientName.trim()) ?: return null
    val trimmedUnit = unit.trim()
    val parsedQuantity = quantity.trim().toDoubleOrNull()

    if (trimmedUnit.isBlank() || parsedQuantity == null || parsedQuantity <= 0.0) return null

    return RecipeIngredient(
        ingredientId = ingredientId,
        quantity = parsedQuantity,
        unit = trimmedUnit,
        note = note.trim(),
    )
}

private fun List<Ingredient>.idFor(value: String): String? =
    firstOrNull { ingredient ->
        ingredient.id.equals(value, ignoreCase = true) ||
            ingredient.name.equals(value, ignoreCase = true)
    }?.id

private fun RecipeEditorUiState.moveIngredientLine(
    fromIndex: Int,
    toIndex: Int,
): RecipeEditorUiState {
    if (fromIndex !in ingredientLines.indices || toIndex !in ingredientLines.indices) return this

    val reorderedLines = ingredientLines.toMutableList()
    val movedLine = reorderedLines.removeAt(fromIndex)
    reorderedLines.add(toIndex, movedLine)
    return copy(ingredientLines = reorderedLines)
}

private fun String.toTextList(): List<String> =
    split(",")
        .map(String::trim)
        .filter(String::isNotBlank)

private fun Double.formatForEditing(): String {
    val asLong = toLong()
    return if (this == asLong.toDouble()) asLong.toString() else toString()
}
