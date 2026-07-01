package dev.nighthawklabs.homebar.ui.recipes.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.nighthawklabs.homebar.HomeBarApplication
import dev.nighthawklabs.homebar.data.repository.IngredientRepository
import dev.nighthawklabs.homebar.data.repository.RecipeRepository
import dev.nighthawklabs.homebar.data.repository.SubstitutionGroupRepository
import dev.nighthawklabs.homebar.domain.logic.RecipeServingState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    application: Application,
    private val recipeRepository: RecipeRepository,
    private val ingredientRepository: IngredientRepository,
    private val substitutionGroupRepository: SubstitutionGroupRepository,
) : AndroidViewModel(application) {
    constructor(application: Application) : this(
        application = application,
        recipeRepository = (application as HomeBarApplication).recipeRepository,
        ingredientRepository = application.ingredientRepository,
        substitutionGroupRepository = application.substitutionGroupRepository,
    )

    private val _servingState = MutableStateFlow<RecipeServingState?>(null)
    val uiState: StateFlow<RecipeDetailUiState> = combine(
        _servingState,
        ingredientRepository.observeIngredients(),
        substitutionGroupRepository.observeSubstitutionGroups(),
    ) { servingState, ingredients, substitutionGroups ->
        createRecipeDetailUiState(servingState, ingredients, substitutionGroups)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecipeDetailUiState(),
    )

    fun load(recipeId: String) {
        viewModelScope.launch {
            _servingState.value = recipeRepository.getRecipe(recipeId)?.let(::RecipeServingState)
        }
    }

    fun increaseServings() {
        _servingState.value = _servingState.value?.increaseServings()
    }

    fun decreaseServings() {
        _servingState.value = _servingState.value?.decreaseServings()
    }

    fun toggleFavorite() {
        val currentServingState = _servingState.value ?: return
        val updatedRecipe = currentServingState.recipe.copy(
            isFavorite = !currentServingState.recipe.isFavorite,
        )
        _servingState.value = currentServingState.copy(recipe = updatedRecipe)
        viewModelScope.launch {
            recipeRepository.updateRecipe(updatedRecipe)
        }
    }

    fun duplicateRecipe(onRecipeDuplicated: (String) -> Unit) {
        val recipeId = _servingState.value?.recipe?.id ?: return
        viewModelScope.launch {
            recipeRepository.duplicateRecipe(recipeId)?.let { duplicate ->
                onRecipeDuplicated(duplicate.id)
            }
        }
    }

    fun deleteCustomRecipe(onRecipeDeleted: () -> Unit) {
        val recipe = _servingState.value?.recipe ?: return
        if (!recipe.isCustom) return

        viewModelScope.launch {
            if (recipeRepository.deleteCustomRecipe(recipe.id)) {
                _servingState.value = null
                onRecipeDeleted()
            }
        }
    }
}
