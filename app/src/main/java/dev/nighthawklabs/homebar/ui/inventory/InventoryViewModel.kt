package dev.nighthawklabs.homebar.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.nighthawklabs.homebar.HomeBarApplication
import dev.nighthawklabs.homebar.data.repository.IngredientRepository
import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
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

fun filterInventoryIngredients(
    ingredients: List<Ingredient>,
    searchText: String,
    categoryFilter: InventoryCategoryFilter = InventoryCategoryFilter.ALL,
): List<Ingredient> {
    val query = searchText.trim()
    val categoryFilteredIngredients = categoryFilter.category?.let { category ->
        ingredients.filter { ingredient -> ingredient.category == category }
    } ?: ingredients

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
): InventoryUiState {
    val filteredIngredients = filterInventoryIngredients(ingredients, searchText, categoryFilter)
    return InventoryUiState(
        ingredients = filteredIngredients,
        searchText = searchText,
        selectedCategoryFilter = categoryFilter,
        emptyStateMessage = emptyInventoryMessage(
            searchText = searchText,
            categoryFilter = categoryFilter,
            hasNoIngredients = filteredIngredients.isEmpty(),
        ),
    )
}

private fun emptyInventoryMessage(
    searchText: String,
    categoryFilter: InventoryCategoryFilter,
    hasNoIngredients: Boolean,
): String? {
    if (!hasNoIngredients) return null

    val query = searchText.trim()
    return when {
        query.isNotBlank() -> "No ingredients match \"$query\"."
        categoryFilter != InventoryCategoryFilter.ALL -> "No ingredients match ${categoryFilter.label}."
        else -> null
    }
}

class InventoryViewModel(
    application: Application,
    private val repository: IngredientRepository,
) : AndroidViewModel(application) {
    constructor(application: Application) : this(
        application = application,
        repository = (application as HomeBarApplication).ingredientRepository,
    )

    private val searchText = MutableStateFlow("")
    private val selectedCategoryFilter = MutableStateFlow(InventoryCategoryFilter.ALL)

    val uiState: StateFlow<InventoryUiState> = combine(
        repository.observeIngredients(),
        searchText,
        selectedCategoryFilter,
    ) { ingredients, currentSearchText, currentCategoryFilter ->
        createInventoryUiState(ingredients, currentSearchText, currentCategoryFilter)
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

    fun markInStock(ingredientId: String) = update { repository.markInStock(ingredientId) }

    fun markNotInStock(ingredientId: String) = update { repository.markNotInStock(ingredientId) }

    fun markRunningLow(ingredientId: String) = update { repository.markRunningLow(ingredientId) }

    fun clearRunningLow(ingredientId: String) = update { repository.clearRunningLow(ingredientId) }

    private fun update(action: suspend () -> Unit) {
        viewModelScope.launch { action() }
    }
}
