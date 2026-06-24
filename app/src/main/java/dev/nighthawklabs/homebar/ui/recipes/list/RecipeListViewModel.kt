package dev.nighthawklabs.homebar.ui.recipes.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.nighthawklabs.homebar.HomeBarApplication
import dev.nighthawklabs.homebar.data.repository.IngredientRepository
import dev.nighthawklabs.homebar.data.repository.RecipeRepository
import dev.nighthawklabs.homebar.data.repository.SubstitutionGroupRepository
import dev.nighthawklabs.homebar.domain.logic.filterRecipes
import dev.nighthawklabs.homebar.domain.logic.matchRecipe
import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.RecipeWithMatchResult
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val selectedFilter = MutableStateFlow(RecipeListFilterOption.MAKEABLE_NOW)
    private val searchText = MutableStateFlow("")
    private val ingredientFilter = MutableStateFlow<String?>(null)

    private val recipeListSource = combine(
        recipeRepository.observeRecipes(),
        ingredientRepository.observeIngredients(),
        substitutionGroupRepository.observeSubstitutionGroups(),
    ) { recipes, ingredients, substitutionGroups ->
        RecipeListSource(
            recipeMatches = recipes.map { recipe ->
                RecipeWithMatchResult(
                    recipe = recipe,
                    matchResult = matchRecipe(recipe, ingredients, substitutionGroups),
                )
            },
            ingredients = ingredients,
            substitutionGroups = substitutionGroups,
        )
    }

    val uiState: StateFlow<RecipeListUiState> = combine(
        recipeListSource,
        selectedFilter,
        searchText,
        ingredientFilter,
    ) { source, filter, currentSearchText, currentIngredientId ->
        val filteredRecipes = filterRecipes(
            recipes = source.recipeMatches,
            filterState = filter.toFilterState(
                searchText = currentSearchText,
                ingredientId = currentIngredientId,
            ),
            substitutionGroups = source.substitutionGroups,
            ingredients = source.ingredients,
        )
        RecipeListUiState(
            recipes = createRecipeListItems(filteredRecipes, source.ingredients),
            selectedFilter = filter,
            searchText = currentSearchText,
            activeIngredientName = currentIngredientId?.let { ingredientId ->
                source.ingredients.firstOrNull { it.id == ingredientId }?.name
                    ?: ingredientId.replace('-', ' ')
            },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecipeListUiState(),
    )

    fun selectFilter(filter: RecipeListFilterOption) {
        selectedFilter.value = filter
    }

    fun updateSearchText(value: String) {
        searchText.value = value
    }

    fun setIngredientFilter(ingredientId: String?) {
        ingredientFilter.value = ingredientId
    }

    private data class RecipeListSource(
        val recipeMatches: List<RecipeWithMatchResult>,
        val ingredients: List<Ingredient>,
        val substitutionGroups: List<SubstitutionGroup>,
    )
}
