package dev.nighthawklabs.homebar.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.nighthawklabs.homebar.HomeBarApplication
import dev.nighthawklabs.homebar.data.repository.IngredientRepository
import dev.nighthawklabs.homebar.data.repository.RecipeRepository
import dev.nighthawklabs.homebar.data.repository.SubstitutionGroupRepository
import dev.nighthawklabs.homebar.domain.logic.filterIngredientsByInventoryStatus
import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.InventoryStatusFilter
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class InventoryUiState(
    val ingredients: List<Ingredient> = emptyList(),
    val searchText: String = "",
    val selectedCategoryFilter: InventoryCategoryFilter = InventoryCategoryFilter.ALL,
    val selectedStatusFilter: InventoryStatusFilter = InventoryStatusFilter.ALL,
    val emptyStateMessage: String? = null,
)

enum class InventoryCategoryFilter(
    val label: String,
    val category: IngredientCategory?,
) {
    ALL("All", null),
    SPIRITS("Spirits", IngredientCategory.SPIRIT),
    LIQUEURS("Liqueurs", IngredientCategory.LIQUEUR),
    MIXERS("Mixers", IngredientCategory.MIXER),
    JUICES("Juices", IngredientCategory.JUICE),
    SYRUPS("Syrups", IngredientCategory.SYRUP),
    BITTERS("Bitters", IngredientCategory.BITTERS),
    GARNISHES("Garnishes", IngredientCategory.GARNISH),
    OTHER("Other", IngredientCategory.OTHER),
}

val InventoryStatusFilter.label: String
    get() = when (this) {
        InventoryStatusFilter.ALL -> "All ingredients"
        InventoryStatusFilter.IN_STOCK -> "In stock"
        InventoryStatusFilter.MISSING -> "Missing ingredients"
        InventoryStatusFilter.RUNNING_LOW -> "Running low"
        InventoryStatusFilter.MISSING_FOR_FAVORITES -> "Missing for favorite recipes"
        InventoryStatusFilter.RUNNING_LOW_FOR_FAVORITES -> "Running low for favorite recipes"
        InventoryStatusFilter.MISSING_OR_RUNNING_LOW_FOR_FAVORITES ->
            "Missing or running low for favorite recipes"
    }

fun filterInventoryIngredients(
    ingredients: List<Ingredient>,
    searchText: String,
    categoryFilter: InventoryCategoryFilter = InventoryCategoryFilter.ALL,
    statusFilter: InventoryStatusFilter = InventoryStatusFilter.ALL,
    recipes: List<Recipe> = emptyList(),
    substitutionGroups: List<SubstitutionGroup> = emptyList(),
): List<Ingredient> {
    val query = searchText.trim()
    val statusFilteredIngredients = filterIngredientsByInventoryStatus(
        ingredients = ingredients,
        recipes = recipes,
        substitutionGroups = substitutionGroups,
        statusFilter = statusFilter,
    )
    val categoryFilteredIngredients = categoryFilter.category?.let { category ->
        statusFilteredIngredients.filter { ingredient -> ingredient.category == category }
    } ?: statusFilteredIngredients

    if (query.isBlank()) return categoryFilteredIngredients

    return categoryFilteredIngredients.filter { ingredient ->
        ingredient.name.contains(query, ignoreCase = true) ||
            ingredient.category.name.replace('_', ' ').contains(query, ignoreCase = true)
    }
}

fun createInventoryUiState(
    ingredients: List<Ingredient>,
    searchText: String,
    categoryFilter: InventoryCategoryFilter = InventoryCategoryFilter.ALL,
    statusFilter: InventoryStatusFilter = InventoryStatusFilter.ALL,
    recipes: List<Recipe> = emptyList(),
    substitutionGroups: List<SubstitutionGroup> = emptyList(),
): InventoryUiState {
    val filteredIngredients = filterInventoryIngredients(
        ingredients = ingredients,
        searchText = searchText,
        categoryFilter = categoryFilter,
        statusFilter = statusFilter,
        recipes = recipes,
        substitutionGroups = substitutionGroups,
    )
    return InventoryUiState(
        ingredients = filteredIngredients,
        searchText = searchText,
        selectedCategoryFilter = categoryFilter,
        selectedStatusFilter = statusFilter,
        emptyStateMessage = emptyInventoryMessage(
            searchText = searchText,
            categoryFilter = categoryFilter,
            statusFilter = statusFilter,
            hasNoIngredients = filteredIngredients.isEmpty(),
        ),
    )
}

private fun emptyInventoryMessage(
    searchText: String,
    categoryFilter: InventoryCategoryFilter,
    statusFilter: InventoryStatusFilter,
    hasNoIngredients: Boolean,
): String? {
    if (!hasNoIngredients) return null

    val query = searchText.trim()
    return when {
        query.isNotBlank() -> "No ingredients match \"$query\"."
        categoryFilter != InventoryCategoryFilter.ALL &&
            statusFilter != InventoryStatusFilter.ALL ->
            "No ingredients match ${categoryFilter.label} with ${statusFilter.label}."
        categoryFilter != InventoryCategoryFilter.ALL -> "No ingredients match ${categoryFilter.label}."
        statusFilter != InventoryStatusFilter.ALL -> "No ingredients match ${statusFilter.label}."
        else -> null
    }
}

class InventoryViewModel(
    application: Application,
    private val repository: IngredientRepository,
    private val recipeRepository: RecipeRepository,
    private val substitutionGroupRepository: SubstitutionGroupRepository,
) : AndroidViewModel(application) {
    constructor(application: Application) : this(
        application = application,
        repository = (application as HomeBarApplication).ingredientRepository,
        recipeRepository = (application as HomeBarApplication).recipeRepository,
        substitutionGroupRepository = (application as HomeBarApplication).substitutionGroupRepository,
    )

    private val searchText = MutableStateFlow("")
    private val selectedCategoryFilter = MutableStateFlow(InventoryCategoryFilter.ALL)
    private val selectedStatusFilter = MutableStateFlow(InventoryStatusFilter.ALL)

    private val inventorySource = combine(
        repository.observeIngredients(),
        recipeRepository.observeRecipes(),
        substitutionGroupRepository.observeSubstitutionGroups(),
    ) { ingredients, recipes, substitutionGroups ->
        InventorySource(
            ingredients = ingredients,
            recipes = recipes,
            substitutionGroups = substitutionGroups,
        )
    }

    val uiState: StateFlow<InventoryUiState> = combine(
        inventorySource,
        searchText,
        selectedCategoryFilter,
        selectedStatusFilter,
    ) { source, currentSearchText, currentCategoryFilter, currentStatusFilter ->
        createInventoryUiState(
            ingredients = source.ingredients,
            searchText = currentSearchText,
            categoryFilter = currentCategoryFilter,
            statusFilter = currentStatusFilter,
            recipes = source.recipes,
            substitutionGroups = source.substitutionGroups,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InventoryUiState(),
        )

    fun updateSearchText(value: String) {
        searchText.value = value
    }

    fun selectCategoryFilter(filter: InventoryCategoryFilter) {
        selectedCategoryFilter.value = filter
    }

    fun selectStatusFilter(filter: InventoryStatusFilter) {
        selectedStatusFilter.value = filter
    }

    fun markInStock(ingredientId: String) = update { repository.markInStock(ingredientId) }

    fun markNotInStock(ingredientId: String) = update { repository.markNotInStock(ingredientId) }

    fun markRunningLow(ingredientId: String) = update { repository.markRunningLow(ingredientId) }

    fun clearRunningLow(ingredientId: String) = update { repository.clearRunningLow(ingredientId) }

    private fun update(action: suspend () -> Unit) {
        viewModelScope.launch { action() }
    }

    private data class InventorySource(
        val ingredients: List<Ingredient>,
        val recipes: List<Recipe>,
        val substitutionGroups: List<SubstitutionGroup>,
    )
}
