package dev.nighthawklabs.homebar.ui.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InventoryScreen(
    onBack: () -> Unit,
    onIngredientSelected: (String) -> Unit,
    viewModel: InventoryViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Consumable ingredients", style = MaterialTheme.typography.headlineSmall)
                    OutlinedTextField(
                        value = uiState.searchText,
                        onValueChange = viewModel::updateSearchText,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Search ingredients") },
                        singleLine = true,
                        trailingIcon = {
                            if (uiState.searchText.isNotBlank()) {
                                TextButton(onClick = { viewModel.updateSearchText("") }) {
                                    Text("Clear")
                                }
                            }
                        },
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        InventoryCategoryFilter.entries.forEach { filter ->
                            FilterChip(
                                selected = filter == uiState.selectedCategoryFilter,
                                onClick = { viewModel.selectCategoryFilter(filter) },
                                label = { Text(filter.label) },
                            )
                        }
                    }
                    uiState.emptyStateMessage?.let { message ->
                        Text(message)
                    }
                }
            }
            items(uiState.ingredients, key = { it.id }) { ingredient ->
                IngredientCard(
                    ingredient = ingredient,
                    onClick = { onIngredientSelected(ingredient.id) },
                    onMarkInStock = { viewModel.markInStock(ingredient.id) },
                    onMarkNotInStock = { viewModel.markNotInStock(ingredient.id) },
                    onMarkRunningLow = { viewModel.markRunningLow(ingredient.id) },
                    onClearRunningLow = { viewModel.clearRunningLow(ingredient.id) },
                )
            }
        }
    }
}

@Composable
private fun IngredientCard(
    ingredient: Ingredient,
    onClick: () -> Unit,
    onMarkInStock: () -> Unit,
    onMarkNotInStock: () -> Unit,
    onMarkRunningLow: () -> Unit,
    onClearRunningLow: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(ingredient.name, style = MaterialTheme.typography.titleMedium)
            Text(categoryLabel(ingredient.category), style = MaterialTheme.typography.bodyMedium)
            Text(if (ingredient.inStock) "In stock" else "Not in stock")
            if (ingredient.runningLow) Text("Running low")
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
