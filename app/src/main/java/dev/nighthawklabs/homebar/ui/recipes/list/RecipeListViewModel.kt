package dev.nighthawklabs.homebar.ui.recipes.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import dev.nighthawklabs.homebar.HomeBarApplication
import dev.nighthawklabs.homebar.data.repository.RecipeRepository
import dev.nighthawklabs.homebar.domain.model.Recipe
import kotlinx.coroutines.flow.SharingStarted

class RecipeListViewModel(
    application: Application,
    repository: RecipeRepository,
) : AndroidViewModel(application) {
    constructor(application: Application) : this(
        application = application,
        repository = (application as HomeBarApplication).recipeRepository,
    )

    val recipes: StateFlow<List<Recipe>> = repository.observeRecipes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )
}
