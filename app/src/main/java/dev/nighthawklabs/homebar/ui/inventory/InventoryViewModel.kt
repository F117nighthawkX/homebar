package dev.nighthawklabs.homebar.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.nighthawklabs.homebar.HomeBarApplication
import dev.nighthawklabs.homebar.data.repository.IngredientRepository
import dev.nighthawklabs.homebar.domain.model.Ingredient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class InventoryUiState(
    val ingredients: List<Ingredient> = emptyList(),
)

class InventoryViewModel(
    application: Application,
    private val repository: IngredientRepository =
        (application as HomeBarApplication).ingredientRepository,
) : AndroidViewModel(application) {
    val uiState: StateFlow<InventoryUiState> = repository.observeIngredients()
        .map(::InventoryUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InventoryUiState(),
        )

    fun markInStock(ingredientId: String) = update { repository.markInStock(ingredientId) }

    fun markNotInStock(ingredientId: String) = update { repository.markNotInStock(ingredientId) }

    fun markRunningLow(ingredientId: String) = update { repository.markRunningLow(ingredientId) }

    fun clearRunningLow(ingredientId: String) = update { repository.clearRunningLow(ingredientId) }

    private fun update(action: suspend () -> Unit) {
        viewModelScope.launch { action() }
    }
}
