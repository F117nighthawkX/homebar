package dev.nighthawklabs.homebar.ui.recipes.list

import androidx.lifecycle.ViewModel
import dev.nighthawklabs.homebar.data.repository.SampleRecipeRepository
import dev.nighthawklabs.homebar.domain.model.PlaceholderRecipe
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted

class RecipeListViewModel(
    repository: SampleRecipeRepository = SampleRecipeRepository(),
) : ViewModel() {
    val recipes: StateFlow<List<PlaceholderRecipe>> = repository.observePlaceholderRecipes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )
}

