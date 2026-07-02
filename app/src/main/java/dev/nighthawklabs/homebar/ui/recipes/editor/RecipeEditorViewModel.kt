package dev.nighthawklabs.homebar.ui.recipes.editor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.nighthawklabs.homebar.HomeBarApplication
import dev.nighthawklabs.homebar.data.repository.IngredientRepository
import dev.nighthawklabs.homebar.data.repository.RecipeRepository
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.Recipe
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeEditorViewModel(
    application: Application,
    private val recipeRepository: RecipeRepository,
    private val ingredientRepository: IngredientRepository,
    private val nowMillis: () -> Long = System::currentTimeMillis,
) : AndroidViewModel(application) {
    constructor(application: Application) : this(
        application = application,
        recipeRepository = (application as HomeBarApplication).recipeRepository,
        ingredientRepository = application.ingredientRepository,
    )

    private val loadedRecipe = MutableStateFlow<Recipe?>(null)
    private val initialEditorSnapshot = MutableStateFlow<RecipeEditorContentSnapshot?>(null)
    private val editorState = MutableStateFlow(RecipeEditorUiState(isLoading = true))
    private val ingredients = ingredientRepository.observeIngredients().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val uiState: StateFlow<RecipeEditorUiState> = combine(
        editorState,
        ingredients,
        initialEditorSnapshot,
    ) { state, currentIngredients, currentInitialSnapshot ->
        if (state.isLoading || state.isNotFound) {
            state
        } else {
            state.withIngredientOptionsAndSaveAvailability(
                ingredients = currentIngredients,
                initialSnapshot = currentInitialSnapshot,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecipeEditorUiState(isLoading = true),
    )

    fun load(recipeId: String?) {
        viewModelScope.launch {
            if (recipeId == null) {
                loadedRecipe.value = null
                val state = createRecipeEditorUiState(recipe = null, ingredients = ingredients.value)
                editorState.value = state
                initialEditorSnapshot.value = state.toContentSnapshot()
                return@launch
            }

            val recipe = recipeRepository.getRecipe(recipeId)
            if (recipe?.isCustom != true) {
                loadedRecipe.value = null
                initialEditorSnapshot.value = null
                editorState.value = RecipeEditorUiState(isNotFound = true)
                return@launch
            }

            loadedRecipe.value = recipe
            val state = createRecipeEditorUiState(recipe = recipe, ingredients = ingredients.value)
            editorState.value = state
            initialEditorSnapshot.value = state.toContentSnapshot()
        }
    }

    fun updateName(value: String) = update { copy(name = value) }

    fun updateBaseServingCount(value: String) = update { copy(baseServingCount = value) }

    fun updateIngredient(index: Int, ingredientId: String, ingredientName: String) = updateIngredient(index) {
        copy(
            ingredientId = ingredientId,
            ingredientName = ingredientName,
        )
    }

    fun updateIngredientSearchText(index: Int, ingredientName: String) = updateIngredient(index) {
        copy(
            ingredientId = null,
            ingredientName = ingredientName,
        )
    }

    fun updateIngredientUnit(index: Int, value: String) = updateIngredient(index) {
        copy(unit = value, quantity = if (unit == value) quantity else "")
    }

    fun updateIngredientQuantity(index: Int, value: String) = updateIngredient(index) {
        copy(quantity = value)
    }

    fun updateIngredientNote(index: Int, value: String) = updateIngredient(index) {
        copy(note = value)
    }

    fun addIngredientLine() = update { addIngredientLine() }

    fun removeIngredientLine(index: Int) = update { removeIngredientLine(index) }

    fun moveIngredientLineUp(index: Int) = update { moveIngredientLineUp(index) }

    fun moveIngredientLineDown(index: Int) = update { moveIngredientLineDown(index) }

    fun updateInstructionStep(index: Int, value: String) = updateInstructionStep(index) {
        copy(text = value)
    }

    fun addInstructionStep() = update { addInstructionStep() }

    fun removeInstructionStep(index: Int) = update { removeInstructionStep(index) }

    fun moveInstructionStepUp(index: Int) = update { moveInstructionStepUp(index) }

    fun moveInstructionStepDown(index: Int) = update { moveInstructionStepDown(index) }

    fun createIngredientForLine(
        index: Int,
        name: String,
        category: IngredientCategory,
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return

        viewModelScope.launch {
            val ingredient = ingredientRepository.createIngredient(trimmedName, category)
            updateIngredient(index, ingredient.id, ingredient.name)
        }
    }

    fun updateGlassware(value: String) = update { copy(glassware = value) }

    fun updateTools(value: String) = update { copy(tools = value) }

    fun updateGarnish(value: String) = update { copy(garnish = value) }

    fun updateTags(value: String) = update { copy(tags = value) }

    fun updateFavorite(value: Boolean) = update { copy(isFavorite = value) }

    fun save(onRecipeSaved: (String) -> Unit) {
        val currentState = editorState.value
        val existingRecipe = loadedRecipe.value
        val recipeId = currentState.recipeId ?: UUID.randomUUID().toString()
        val recipe = currentState.toCustomRecipe(
            existingRecipe = existingRecipe,
            ingredients = ingredients.value,
            recipeId = recipeId,
            nowMillis = nowMillis(),
        ) ?: return

        viewModelScope.launch {
            if (existingRecipe == null) {
                recipeRepository.insertCustomRecipe(recipe)
                loadedRecipe.value = recipe
                val state = createRecipeEditorUiState(recipe, ingredients.value)
                editorState.value = state
                initialEditorSnapshot.value = state.toContentSnapshot()
                onRecipeSaved(recipe.id)
            } else if (recipeRepository.updateCustomRecipe(recipe)) {
                loadedRecipe.value = recipe
                val state = createRecipeEditorUiState(recipe, ingredients.value)
                editorState.value = state
                initialEditorSnapshot.value = state.toContentSnapshot()
                onRecipeSaved(recipe.id)
            }
        }
    }

    private fun update(transform: RecipeEditorUiState.() -> RecipeEditorUiState) {
        editorState.value = editorState.value.transform()
    }

    private fun updateIngredient(
        index: Int,
        transform: RecipeEditorIngredientLineUiState.() -> RecipeEditorIngredientLineUiState,
    ) {
        update {
            copy(
                ingredientLines = ingredientLines.mapIndexed { lineIndex, line ->
                    if (lineIndex == index) line.transform() else line
                },
            )
        }
    }

    private fun updateInstructionStep(
        index: Int,
        transform: RecipeEditorInstructionStepUiState.() -> RecipeEditorInstructionStepUiState,
    ) {
        update {
            copy(
                instructionSteps = instructionSteps.mapIndexed { stepIndex, step ->
                    if (stepIndex == index) step.transform() else step
                },
            )
        }
    }
}
