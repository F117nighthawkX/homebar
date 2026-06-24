package dev.nighthawklabs.homebar.ui.recipes.detail

import androidx.lifecycle.ViewModel
import dev.nighthawklabs.homebar.data.repository.SampleRecipeRepository
import dev.nighthawklabs.homebar.domain.model.PlaceholderRecipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RecipeDetailViewModel(
    private val repository: SampleRecipeRepository = SampleRecipeRepository(),
) : ViewModel() {
    private val _recipe = MutableStateFlow<PlaceholderRecipe?>(null)
    val recipe: StateFlow<PlaceholderRecipe?> = _recipe

    fun load(recipeId: String) {
        _recipe.value = repository.getPlaceholderRecipe(recipeId)
    }
}
