package dev.nighthawklabs.homebar.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.nighthawklabs.homebar.HomeBarApplication
import dev.nighthawklabs.homebar.data.repository.IngredientRepository
import dev.nighthawklabs.homebar.domain.model.Ingredient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class InventoryUiState(
    val ingredients: List<Ingredient> = emptyList(),
    val searchText: String = "",
    val emptyStateMessage: String? = null,
)

fun filterInventoryIngredients(
    ingredients: List<Ingredient>,
    searchText: String,
): List<Ingredient> {
    val query = searchText.trim()
    if (query.isBlank()) return ingredients

    return ingredients.filter { ingredient ->
        ingredient.name.contains(query, ignoreCase = true) ||
            ingredient.category.name.replace('_', ' ').contains(query, ignoreCase = true)
    }
}

fun createInventoryUiState(
    ingredients: List<Ingredient>,
    searchText: String,
): InventoryUiState {
    val filteredIngredients = filterInventoryIngredients(ingredients, searchText)
    return InventoryUiState(
        ingredients = filteredIngredients,
        searchText = searchText,
        emptyStateMessage = searchText.trim()
            .takeIf { it.isNotBlank() && filteredIngredients.isEmpty() }
            ?.let { query -> "No ingredients match \"$query\"." },
    )
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

    val uiState: StateFlow<InventoryUiState> = combine(
        repository.observeIngredients(),
        searchText,
    ) { ingredients, currentSearchText -> createInventoryUiState(ingredients, currentSearchText) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InventoryUiState(),
        )

    fun updateSearchText(value: String) {
        searchText.value = value
    }

    fun markInStock(ingredientId: String) = update { repository.markInStock(ingredientId) }

    fun markNotInStock(ingredientId: String) = update { repository.markNotInStock(ingredientId) }

    fun markRunningLow(ingredientId: String) = update { repository.markRunningLow(ingredientId) }

    fun clearRunningLow(ingredientId: String) = update { repository.clearRunningLow(ingredientId) }

    private fun update(action: suspend () -> Unit) {
        viewModelScope.launch { action() }
    }
}
