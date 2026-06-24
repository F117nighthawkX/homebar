package dev.nighthawklabs.homebar.ui.recipes.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RecipeListScreen(
    onRecipeSelected: (String) -> Unit,
    onInventorySelected: () -> Unit,
    onSettingsSelected: () -> Unit,
    viewModel: RecipeListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Bar") },
                actions = {
                    TextButton(onClick = onInventorySelected) { Text("Inventory") }
                    TextButton(onClick = onSettingsSelected) { Text("Settings") }
                },
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
                    Text(
                        text = "Recipes",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RecipeListFilterOption.entries.forEach { filter ->
                            FilterChip(
                                selected = filter == uiState.selectedFilter,
                                onClick = { viewModel.selectFilter(filter) },
                                label = { Text(filter.label) },
                            )
                        }
                    }
                }
            }
            uiState.emptyStateMessage()?.let { message ->
                item {
                    Text(message)
                }
            }
            items(uiState.recipes, key = { it.id }) { recipe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRecipeSelected(recipe.id) },
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(recipe.name, style = MaterialTheme.typography.titleMedium)
                        if (recipe.isFavorite) Text("Favorite")
                        Text(recipe.makeabilityLabel, style = MaterialTheme.typography.bodyMedium)
                        Text(recipe.ingredientSummary, style = MaterialTheme.typography.bodyMedium)
                        if (recipe.missingIngredientNames.isNotEmpty()) {
                            Text("Missing: ${recipe.missingIngredientNames.joinToString()}")
                        }
                        if (recipe.runningLowIngredientNames.isNotEmpty()) {
                            Text("Low: ${recipe.runningLowIngredientNames.joinToString()}")
                        }
                    }
                }
            }
        }
    }
}
