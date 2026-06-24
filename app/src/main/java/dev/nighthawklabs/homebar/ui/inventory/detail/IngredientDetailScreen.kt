package dev.nighthawklabs.homebar.ui.inventory.detail

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import dev.nighthawklabs.homebar.HomeBarApplication
import dev.nighthawklabs.homebar.data.repository.IngredientRepository
import dev.nighthawklabs.homebar.domain.model.Ingredient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class IngredientDetailUiState(
    val ingredient: Ingredient? = null,
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
class IngredientDetailViewModel(
    application: Application,
    private val repository: IngredientRepository,
) : AndroidViewModel(application) {
    constructor(application: Application) : this(
        application = application,
        repository = (application as HomeBarApplication).ingredientRepository,
    )

    private val ingredientId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<IngredientDetailUiState> = ingredientId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(IngredientDetailUiState())
            } else {
                repository.observeIngredients().map { ingredients ->
                    IngredientDetailUiState(
                        ingredient = ingredients.firstOrNull { it.id == id },
                        isLoading = false,
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = IngredientDetailUiState(),
        )

    fun loadIngredient(ingredientId: String) {
        this.ingredientId.value = ingredientId
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun IngredientDetailScreen(
    ingredientId: String,
    onBack: () -> Unit,
    viewModel: IngredientDetailViewModel = viewModel(),
) {
    LaunchedEffect(ingredientId) {
        viewModel.loadIngredient(ingredientId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ingredient = uiState.ingredient

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingredient") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when {
                uiState.isLoading -> Text("Loading ingredient")
                ingredient == null -> Text("Ingredient not found.")
                else -> Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            ingredient.name,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                }
            }
        }
    }
}
