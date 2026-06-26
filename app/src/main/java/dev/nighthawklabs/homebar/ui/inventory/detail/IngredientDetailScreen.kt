package dev.nighthawklabs.homebar.ui.inventory.detail

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import dev.nighthawklabs.homebar.data.repository.SubstitutionGroupRepository
import dev.nighthawklabs.homebar.domain.logic.SubstituteAddStatus
import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class IngredientDetailUiState(
    val ingredient: Ingredient? = null,
    val substitutes: List<Ingredient> = emptyList(),
    val substituteSearchVisible: Boolean = false,
    val substituteSearchText: String = "",
    val substituteSearchResults: List<Ingredient> = emptyList(),
    val selectedSubstituteIds: Set<String> = emptySet(),
    val substituteMessage: String? = null,
    val isLoading: Boolean = true,
)

fun createIngredientDetailUiState(
    ingredientId: String,
    ingredients: List<Ingredient>,
    substitutionGroups: List<SubstitutionGroup>,
    substituteSearchVisible: Boolean = false,
    substituteSearchText: String = "",
    selectedSubstituteIds: Set<String> = emptySet(),
    substituteMessage: String? = null,
): IngredientDetailUiState {
    val ingredient = ingredients.firstOrNull { it.id == ingredientId }
    val substituteIds = substitutionGroups
        .filter { group -> ingredientId in group.ingredientIds }
        .flatMap { group -> group.ingredientIds }
        .filterNot { id -> id == ingredientId }
        .distinct()
        .toSet()

    return IngredientDetailUiState(
        ingredient = ingredient,
        substitutes = ingredients
            .filter { candidate -> candidate.id in substituteIds }
            .sortedBy { candidate -> candidate.name },
        substituteSearchVisible = substituteSearchVisible,
        substituteSearchText = substituteSearchText,
        substituteSearchResults = createSubstituteSearchResults(
            ingredientId = ingredientId,
            ingredients = ingredients,
            substitutionGroups = substitutionGroups,
            searchText = substituteSearchText,
        ),
        selectedSubstituteIds = selectedSubstituteIds,
        substituteMessage = substituteMessage,
        isLoading = false,
    )
}

fun createSubstituteSearchResults(
    ingredientId: String,
    ingredients: List<Ingredient>,
    substitutionGroups: List<SubstitutionGroup>,
    searchText: String,
): List<Ingredient> {
    val excludedIngredientIds = substitutionGroups
        .filter { group -> ingredientId in group.ingredientIds }
        .flatMap { group -> group.ingredientIds }
        .toSet() + ingredientId
    val query = searchText.trim()

    return ingredients
        .filterNot { ingredient -> ingredient.id in excludedIngredientIds }
        .filter { ingredient ->
            query.isBlank() ||
                ingredient.name.contains(query, ignoreCase = true) ||
                ingredient.category.name.replace('_', ' ').contains(query, ignoreCase = true)
        }
        .sortedBy { ingredient -> ingredient.name }
}

@OptIn(ExperimentalCoroutinesApi::class)
class IngredientDetailViewModel(
    application: Application,
    private val repository: IngredientRepository,
    private val substitutionGroupRepository: SubstitutionGroupRepository,
) : AndroidViewModel(application) {
    constructor(application: Application) : this(
        application = application,
        repository = (application as HomeBarApplication).ingredientRepository,
        substitutionGroupRepository = application.substitutionGroupRepository,
    )

    private val ingredientId = MutableStateFlow<String?>(null)
    private val substituteSearchVisible = MutableStateFlow(false)
    private val substituteSearchText = MutableStateFlow("")
    private val selectedSubstituteIds = MutableStateFlow<Set<String>>(emptySet())
    private val substituteMessage = MutableStateFlow<String?>(null)

    private val ingredientDetailSource = ingredientId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf<IngredientDetailSource?>(null)
            } else {
                combine(
                    repository.observeIngredients(),
                    substitutionGroupRepository.observeSubstitutionGroups(),
                ) { ingredients, substitutionGroups ->
                    IngredientDetailSource(
                        ingredientId = id,
                        ingredients = ingredients,
                        substitutionGroups = substitutionGroups,
                    )
                }
            }
        }

    val uiState: StateFlow<IngredientDetailUiState> = combine(
        ingredientDetailSource,
        substituteSearchVisible,
        substituteSearchText,
        selectedSubstituteIds,
        substituteMessage,
    ) { source, searchVisible, searchText, selectedIds, message ->
        if (source == null) {
            IngredientDetailUiState()
        } else {
            createIngredientDetailUiState(
                ingredientId = source.ingredientId,
                ingredients = source.ingredients,
                substitutionGroups = source.substitutionGroups,
                substituteSearchVisible = searchVisible,
                substituteSearchText = searchText,
                selectedSubstituteIds = selectedIds,
                substituteMessage = message,
            )
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

    fun markInStock(ingredientId: String) = update { repository.markInStock(ingredientId) }

    fun markNotInStock(ingredientId: String) = update { repository.markNotInStock(ingredientId) }

    fun markRunningLow(ingredientId: String) = update { repository.markRunningLow(ingredientId) }

    fun clearRunningLow(ingredientId: String) = update { repository.clearRunningLow(ingredientId) }

    fun removeSubstitute(ingredientId: String, substituteIngredientId: String) = update {
        substitutionGroupRepository.removeSubstitute(ingredientId, substituteIngredientId)
    }

    fun showSubstituteSearch() {
        substituteSearchVisible.value = true
        substituteMessage.value = null
    }

    fun hideSubstituteSearch() {
        substituteSearchVisible.value = false
        substituteSearchText.value = ""
        selectedSubstituteIds.value = emptySet()
        substituteMessage.value = null
    }

    fun updateSubstituteSearchText(value: String) {
        substituteSearchText.value = value
    }

    fun toggleSubstituteSelection(substituteIngredientId: String) {
        selectedSubstituteIds.value = selectedSubstituteIds.value.toggle(substituteIngredientId)
    }

    fun addSelectedSubstitutes(ingredientId: String) {
        val substituteIds = selectedSubstituteIds.value
        if (substituteIds.isEmpty()) return

        viewModelScope.launch {
            val statuses = substituteIds.map { substituteId ->
                substitutionGroupRepository.addSubstitute(ingredientId, substituteId)
            }
            selectedSubstituteIds.value = emptySet()
            substituteSearchText.value = ""
            substituteSearchVisible.value = statuses.any { status ->
                status == SubstituteAddStatus.DIFFERENT_GROUP_CONFLICT
            }
            substituteMessage.value = when {
                statuses.any { status -> status == SubstituteAddStatus.DIFFERENT_GROUP_CONFLICT } ->
                    "This ingredient already belongs to another substitute group. Group merging can be added later."
                statuses.any { status -> status == SubstituteAddStatus.UPDATED } ->
                    "Substitute added."
                else -> null
            }
        }
    }

    private fun update(action: suspend () -> Unit) {
        viewModelScope.launch { action() }
    }

    private data class IngredientDetailSource(
        val ingredientId: String,
        val ingredients: List<Ingredient>,
        val substitutionGroups: List<SubstitutionGroup>,
    )
}

private fun Set<String>.toggle(value: String): Set<String> =
    if (value in this) this - value else this + value

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun IngredientDetailScreen(
    ingredientId: String,
    onBack: () -> Unit,
    onViewRecipesUsingIngredient: (String) -> Unit,
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
                .padding(PaddingValues(16.dp))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when {
                uiState.isLoading -> Text("Loading ingredient")
                ingredient == null -> Text("Ingredient not found.")
                else -> IngredientDetailContent(
                    ingredient = ingredient,
                    substitutes = uiState.substitutes,
                    onMarkInStock = { viewModel.markInStock(ingredient.id) },
                    onMarkNotInStock = { viewModel.markNotInStock(ingredient.id) },
                    onMarkRunningLow = { viewModel.markRunningLow(ingredient.id) },
                    onClearRunningLow = { viewModel.clearRunningLow(ingredient.id) },
                    onRemoveSubstitute = { substituteId ->
                        viewModel.removeSubstitute(ingredient.id, substituteId)
                    },
                    substituteSearchVisible = uiState.substituteSearchVisible,
                    substituteSearchText = uiState.substituteSearchText,
                    substituteSearchResults = uiState.substituteSearchResults,
                    selectedSubstituteIds = uiState.selectedSubstituteIds,
                    substituteMessage = uiState.substituteMessage,
                    onShowSubstituteSearch = viewModel::showSubstituteSearch,
                    onHideSubstituteSearch = viewModel::hideSubstituteSearch,
                    onSubstituteSearchTextChanged = viewModel::updateSubstituteSearchText,
                    onSubstituteSelected = viewModel::toggleSubstituteSelection,
                    onAddSelectedSubstitutes = {
                        viewModel.addSelectedSubstitutes(ingredient.id)
                    },
                    onViewRecipesUsingIngredient = {
                        onViewRecipesUsingIngredient(ingredient.id)
                    },
                )
            }
        }
    }
}

@Composable
private fun IngredientDetailContent(
    ingredient: Ingredient,
    substitutes: List<Ingredient>,
    onMarkInStock: () -> Unit,
    onMarkNotInStock: () -> Unit,
    onMarkRunningLow: () -> Unit,
    onClearRunningLow: () -> Unit,
    onRemoveSubstitute: (String) -> Unit,
    substituteSearchVisible: Boolean,
    substituteSearchText: String,
    substituteSearchResults: List<Ingredient>,
    selectedSubstituteIds: Set<String>,
    substituteMessage: String?,
    onShowSubstituteSearch: () -> Unit,
    onHideSubstituteSearch: () -> Unit,
    onSubstituteSearchTextChanged: (String) -> Unit,
    onSubstituteSelected: (String) -> Unit,
    onAddSelectedSubstitutes: () -> Unit,
    onViewRecipesUsingIngredient: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                ingredient.name,
                style = MaterialTheme.typography.headlineSmall,
            )
            DetailSection(label = "Category", value = categoryLabel(ingredient.category))
            InventorySection(
                ingredient = ingredient,
                onMarkInStock = onMarkInStock,
                onMarkNotInStock = onMarkNotInStock,
                onMarkRunningLow = onMarkRunningLow,
                onClearRunningLow = onClearRunningLow,
            )
            SubstituteSection(
                substitutes = substitutes,
                onRemoveSubstitute = onRemoveSubstitute,
            )
            substituteMessage?.let { message ->
                Text(message)
            }
            if (substituteSearchVisible) {
                SubstituteSearchSection(
                    searchText = substituteSearchText,
                    searchResults = substituteSearchResults,
                    selectedSubstituteIds = selectedSubstituteIds,
                    onSearchTextChanged = onSubstituteSearchTextChanged,
                    onSubstituteSelected = onSubstituteSelected,
                    onAddSelectedSubstitutes = onAddSelectedSubstitutes,
                    onCancel = onHideSubstituteSearch,
                )
            }
            DetailSection(
                label = "Notes",
                value = ingredient.notes.ifBlank { "No notes." },
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onShowSubstituteSearch) {
                    Text("Add substitute")
                }
                TextButton(onClick = onViewRecipesUsingIngredient) {
                    Text("View drinks using this ingredient")
                }
            }
        }
    }
}

@Composable
private fun SubstituteSearchSection(
    searchText: String,
    searchResults: List<Ingredient>,
    selectedSubstituteIds: Set<String>,
    onSearchTextChanged: (String) -> Unit,
    onSubstituteSelected: (String) -> Unit,
    onAddSelectedSubstitutes: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Add substitute", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search existing ingredients") },
            singleLine = true,
        )
        if (searchResults.isEmpty()) {
            Text("No ingredients match this search.")
        } else {
            searchResults.forEach { ingredient ->
                SubstituteSearchRow(
                    ingredient = ingredient,
                    selected = ingredient.id in selectedSubstituteIds,
                    onClick = { onSubstituteSelected(ingredient.id) },
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(
                onClick = onAddSelectedSubstitutes,
                enabled = selectedSubstituteIds.isNotEmpty(),
            ) {
                Text("Add selected")
            }
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun SubstituteSearchRow(
    ingredient: Ingredient,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            checked = selected,
            onCheckedChange = null,
        )
        Column {
            Text(ingredient.name, style = MaterialTheme.typography.bodyLarge)
            Text(categoryLabel(ingredient.category))
        }
    }
}

@Composable
private fun InventorySection(
    ingredient: Ingredient,
    onMarkInStock: () -> Unit,
    onMarkNotInStock: () -> Unit,
    onMarkRunningLow: () -> Unit,
    onClearRunningLow: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Inventory", style = MaterialTheme.typography.titleMedium)
        Text(if (ingredient.inStock) "In stock" else "Not in stock")
        Text(if (ingredient.runningLow) "Running low" else "Not running low")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (ingredient.inStock) {
                TextButton(onClick = onMarkNotInStock) {
                    Text("Mark not in stock")
                }
            } else {
                TextButton(onClick = onMarkInStock) {
                    Text("Mark in stock")
                }
            }
            if (ingredient.runningLow) {
                TextButton(onClick = onClearRunningLow) {
                    Text("Clear low")
                }
            } else {
                TextButton(onClick = onMarkRunningLow) {
                    Text("Mark running low")
                }
            }
        }
    }
}

@Composable
private fun SubstituteSection(
    substitutes: List<Ingredient>,
    onRemoveSubstitute: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Substitutes", style = MaterialTheme.typography.titleMedium)
        if (substitutes.isEmpty()) {
            Text("No substitutes.")
        } else {
            substitutes.forEach { substitute ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("${substitute.name} (${categoryLabel(substitute.category)})")
                    TextButton(onClick = { onRemoveSubstitute(substitute.id) }) {
                        Text("Remove")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Text(value)
    }
}

private fun categoryLabel(category: IngredientCategory): String = when (category) {
    IngredientCategory.SPIRIT -> "Spirit"
    IngredientCategory.LIQUEUR -> "Liqueur"
    IngredientCategory.MIXER -> "Mixer"
    IngredientCategory.JUICE -> "Juice"
    IngredientCategory.SYRUP -> "Syrup"
    IngredientCategory.BITTERS -> "Bitters"
    IngredientCategory.GARNISH -> "Garnish"
    IngredientCategory.OTHER -> "Other"
}
