package dev.nighthawklabs.homebar.ui.recipes.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.nighthawklabs.homebar.HomeBarApplication
import dev.nighthawklabs.homebar.data.repository.RecipeRepository
import dev.nighthawklabs.homebar.domain.logic.RecipeServingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    application: Application,
    private val repository: RecipeRepository = (application as HomeBarApplication).recipeRepository,
) : AndroidViewModel(application) {
    private val _servingState = MutableStateFlow<RecipeServingState?>(null)
    val servingState: StateFlow<RecipeServingState?> = _servingState

    fun load(recipeId: String) {
        viewModelScope.launch {
            _servingState.value = repository.getRecipe(recipeId)?.let(::RecipeServingState)
        }
    }

    fun increaseServings() {
        _servingState.value = _servingState.value?.increaseServings()
    }

    fun decreaseServings() {
        _servingState.value = _servingState.value?.decreaseServings()
    }
}
