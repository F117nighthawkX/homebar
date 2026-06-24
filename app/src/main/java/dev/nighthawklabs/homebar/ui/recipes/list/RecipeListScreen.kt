package dev.nighthawklabs.homebar.ui.recipes.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RecipeListScreen(
    onRecipeSelected: (String) -> Unit,
    onInventorySelected: () -> Unit,
    onSettingsSelected: () -> Unit,
    viewModel: RecipeListViewModel = viewModel(),
) {
    val recipes by viewModel.recipes.collectAsStateWithLifecycle()

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
                Text(
                    text = "Recipes",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            items(recipes, key = { it.id }) { recipe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRecipeSelected(recipe.id) },
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(recipe.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${recipe.baseServingCount} serving" +
                                if (recipe.baseServingCount == 1) "" else "s",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}
