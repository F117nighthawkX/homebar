package dev.nighthawklabs.homebar.ui.recipes.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.nighthawklabs.homebar.HomeBarApplication
import dev.nighthawklabs.homebar.data.repository.IngredientRepository
import dev.nighthawklabs.homebar.data.repository.RecipeRepository
import dev.nighthawklabs.homebar.data.repository.SubstitutionGroupRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class RecipeListViewModel(
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

    val uiState: StateFlow<RecipeListUiState> = combine(
        recipeRepository.observeRecipes(),
        ingredientRepository.observeIngredients(),
        substitutionGroupRepository.observeSubstitutionGroups(),
    ) { recipes, ingredients, substitutionGroups ->
        RecipeListUiState(
            recipes = createRecipeListItems(recipes, ingredients, substitutionGroups),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecipeListUiState(),
    )
}
