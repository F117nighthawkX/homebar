package dev.nighthawklabs.homebar.ui.recipes.editor

import dev.nighthawklabs.homebar.domain.logic.formatInstructionSteps
import dev.nighthawklabs.homebar.domain.logic.parseInstructionSteps
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
    val instructionSteps: List<RecipeEditorInstructionStepUiState> = listOf(
        RecipeEditorInstructionStepUiState(),
    ),
    val glassware: String = "",
    val tools: String = "",
    val garnish: String = "",
    val tags: String = "",
    val isFavorite: Boolean = false,
    val canSave: Boolean = false,
    val ingredientOptions: List<RecipeEditorIngredientOption> = emptyList(),
    val validation: RecipeEditorValidation = RecipeEditorValidation(),
    val hasUnsavedChanges: Boolean = false,
)

data class RecipeEditorIngredientOption(
    val id: String,
    val name: String,
)

data class RecipeEditorInstructionStepUiState(
    val text: String = "",
)

data class RecipeEditorValidation(
    val nameError: String? = null,
    val ingredientSectionError: String? = null,
    val instructionSectionError: String? = null,
    val ingredientLineErrors: List<RecipeEditorIngredientLineValidation> = emptyList(),
) {
    val isValid: Boolean
        get() = nameError == null &&
            ingredientSectionError == null &&
            instructionSectionError == null &&
            ingredientLineErrors.none(RecipeEditorIngredientLineValidation::hasErrors)
}

data class RecipeEditorIngredientLineValidation(
    val ingredientError: String? = null,
    val unitError: String? = null,
    val quantityError: String? = null,
) {
    val hasErrors: Boolean
        get() = ingredientError != null || unitError != null || quantityError != null
}

data class RecipeEditorContentSnapshot(
    val recipeId: String?,
    val name: String,
    val baseServingCount: String,
    val ingredientLines: List<RecipeEditorIngredientLineUiState>,
    val instructionSteps: List<RecipeEditorInstructionStepUiState>,
    val glassware: String,
    val tools: String,
    val garnish: String,
    val tags: String,
    val isFavorite: Boolean,
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
        instructionSteps = recipe.instructions.toInstructionStepUiStates(),
        glassware = recipe.glassware,
        tools = recipe.tools.joinToString(),
        garnish = recipe.garnish.joinToString(),
        tags = recipe.tags.joinToString(),
        isFavorite = recipe.isFavorite,
    )
}

fun RecipeEditorUiState.withIngredientOptionsAndSaveAvailability(
    ingredients: List<Ingredient>,
    initialSnapshot: RecipeEditorContentSnapshot? = null,
): RecipeEditorUiState {
    val validation = validateRecipeEditorState(ingredients)
    return copy(
        canSave = validation.isValid && baseServingCount.trim().toIntOrNull()?.let { it >= 1 } == true,
        ingredientOptions = ingredients.map { ingredient ->
            RecipeEditorIngredientOption(
                id = ingredient.id,
                name = ingredient.name,
            )
        },
        validation = validation,
        hasUnsavedChanges = initialSnapshot?.let { snapshot ->
            toContentSnapshot() != snapshot
        } ?: false,
    )
}

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

fun RecipeEditorUiState.addInstructionStep(): RecipeEditorUiState =
    copy(instructionSteps = instructionSteps + RecipeEditorInstructionStepUiState())

fun RecipeEditorUiState.removeInstructionStep(index: Int): RecipeEditorUiState {
    if (index !in instructionSteps.indices) return this

    val remainingSteps = instructionSteps.filterIndexed { stepIndex, _ -> stepIndex != index }
    return copy(
        instructionSteps = remainingSteps.ifEmpty { listOf(RecipeEditorInstructionStepUiState()) },
    )
}

fun RecipeEditorUiState.moveInstructionStepUp(index: Int): RecipeEditorUiState =
    moveInstructionStep(fromIndex = index, toIndex = index - 1)

fun RecipeEditorUiState.moveInstructionStepDown(index: Int): RecipeEditorUiState =
    moveInstructionStep(fromIndex = index, toIndex = index + 1)

fun filterIngredientOptions(
    ingredientOptions: List<RecipeEditorIngredientOption>,
    searchText: String,
): List<RecipeEditorIngredientOption> {
    val trimmedSearchText = searchText.trim()
    if (trimmedSearchText.isBlank()) return ingredientOptions

    return ingredientOptions.filter { ingredient ->
        ingredient.name.contains(trimmedSearchText, ignoreCase = true)
    }
}

fun canCreateIngredientOption(
    ingredientOptions: List<RecipeEditorIngredientOption>,
    searchText: String,
): Boolean = searchText.trim().isNotBlank() &&
    filterIngredientOptions(ingredientOptions, searchText).isEmpty()

fun RecipeEditorUiState.validateRecipeEditorState(
    ingredients: List<Ingredient>,
): RecipeEditorValidation {
    val ingredientLineErrors = ingredientLines.map { line ->
        line.validate(ingredients)
    }
    val hasSavedIngredientLine = ingredientLines.any { line -> line.toRecipeIngredient(ingredients) != null }
    val hasInstructionStep = instructionSteps.any { step -> step.text.isNotBlank() }

    return RecipeEditorValidation(
        nameError = if (name.isBlank()) "Recipe name is required." else null,
        ingredientSectionError = if (hasSavedIngredientLine) null else "Add at least one ingredient.",
        instructionSectionError = if (hasInstructionStep) null else "Add at least one instruction step.",
        ingredientLineErrors = ingredientLineErrors,
    )
}

fun RecipeEditorUiState.toContentSnapshot(): RecipeEditorContentSnapshot = RecipeEditorContentSnapshot(
    recipeId = recipeId,
    name = name,
    baseServingCount = baseServingCount,
    ingredientLines = ingredientLines,
    instructionSteps = instructionSteps,
    glassware = glassware,
    tools = tools,
    garnish = garnish,
    tags = tags,
    isFavorite = isFavorite,
)

fun RecipeEditorUiState.toCustomRecipe(
    existingRecipe: Recipe?,
    ingredients: List<Ingredient>,
    recipeId: String,
    nowMillis: Long,
): Recipe? {
    val trimmedName = name.trim()
    val servingCount = baseServingCount.trim().toIntOrNull()
    val recipeIngredients = ingredientLines.mapNotNull { line -> line.toRecipeIngredient(ingredients) }
    val trimmedInstructions = formatInstructionSteps(instructionSteps.map { step -> step.text })
    val validation = validateRecipeEditorState(ingredients)

    if (trimmedName.isBlank() || servingCount == null || servingCount < 1) return null
    if (!validation.isValid || recipeIngredients.isEmpty() || trimmedInstructions.isBlank()) return null

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

private fun RecipeEditorIngredientLineUiState.validate(
    ingredients: List<Ingredient>,
): RecipeEditorIngredientLineValidation {
    if (isBlankLine()) return RecipeEditorIngredientLineValidation()

    return RecipeEditorIngredientLineValidation(
        ingredientError = if (resolvedIngredientId(ingredients) == null) "Choose an ingredient." else null,
        unitError = if (unit.isBlank()) "Choose a unit." else null,
        quantityError = if (quantity.trim().toDoubleOrNull()?.let { it > 0.0 } == true) {
            null
        } else {
            "Enter a quantity."
        },
    )
}

private fun RecipeEditorIngredientLineUiState.toRecipeIngredient(
    ingredients: List<Ingredient>,
): RecipeIngredient? {
    val ingredientId = resolvedIngredientId(ingredients) ?: return null
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

private fun RecipeEditorIngredientLineUiState.resolvedIngredientId(
    ingredients: List<Ingredient>,
): String? = ingredientId ?: ingredients.idFor(ingredientName.trim())

private fun RecipeEditorIngredientLineUiState.isBlankLine(): Boolean =
    ingredientId == null &&
        ingredientName.isBlank() &&
        unit.isBlank() &&
        quantity.isBlank() &&
        note.isBlank()

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

private fun RecipeEditorUiState.moveInstructionStep(
    fromIndex: Int,
    toIndex: Int,
): RecipeEditorUiState {
    if (fromIndex !in instructionSteps.indices || toIndex !in instructionSteps.indices) return this

    val reorderedSteps = instructionSteps.toMutableList()
    val movedStep = reorderedSteps.removeAt(fromIndex)
    reorderedSteps.add(toIndex, movedStep)
    return copy(instructionSteps = reorderedSteps)
}

private fun String.toInstructionStepUiStates(): List<RecipeEditorInstructionStepUiState> =
    parseInstructionSteps(this)
        .map { step -> RecipeEditorInstructionStepUiState(step) }
        .ifEmpty { listOf(RecipeEditorInstructionStepUiState()) }

private fun String.toTextList(): List<String> =
    split(",")
        .map(String::trim)
        .filter(String::isNotBlank)

private fun Double.formatForEditing(): String {
    val asLong = toLong()
    return if (this == asLong.toDouble()) asLong.toString() else toString()
}
