package dev.nighthawklabs.homebar.ui.recipes.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.nighthawklabs.homebar.domain.model.RecipeMatchStatus
import java.math.BigDecimal

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RecipeDetailScreen(
    recipeId: String,
    onBack: () -> Unit,
    viewModel: RecipeDetailViewModel = viewModel(),
) {
    LaunchedEffect(recipeId) { viewModel.load(recipeId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recipe = uiState.recipe

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.name ?: "Recipe") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
                actions = {
                    if (recipe != null) {
                        TextButton(onClick = viewModel::toggleFavorite) {
                            Text(if (recipe.isFavorite) "Unfavorite" else "Favorite")
                        }
                    }
                },
            )
        },
    ) { contentPadding ->
        if (recipe == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp),
            ) {
                Text("Recipe not found.", style = MaterialTheme.typography.headlineSmall)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(recipe.name, style = MaterialTheme.typography.headlineMedium)
                        Text("Status: ${uiState.matchStatus.statusLabel()}")
                        ServingControl(
                            servingCount = uiState.selectedServingCount,
                            onDecrease = viewModel::decreaseServings,
                            onIncrease = viewModel::increaseServings,
                        )
                    }
                }
                item { SectionTitle("Ingredients") }
                items(uiState.ingredientLines) { ingredient ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            "${formatQuantity(ingredient.quantity)} ${ingredient.unit} " +
                                ingredient.ingredientName,
                        )
                        if (ingredient.note.isNotBlank()) Text(ingredient.note)
                        ingredient.substituteForName?.let { substituteForName ->
                            Text("Substitute for: $substituteForName")
                        }
                    }
                }
                if (uiState.runningLowIngredientNames.isNotEmpty()) {
                    item {
                        DetailListSection("Low inventory", uiState.runningLowIngredientNames)
                    }
                }
                if (uiState.missingIngredientNames.isNotEmpty()) {
                    item {
                        DetailListSection("Missing ingredients", uiState.missingIngredientNames)
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        SectionTitle("Instructions")
                        Text(recipe.instructions)
                    }
                }
                item { DetailListSection("Glassware", listOf(recipe.glassware)) }
                item { DetailListSection("Tools", recipe.tools) }
                item { DetailListSection("Garnish", recipe.garnish) }
                item { DetailListSection("Tags", recipe.tags) }
            }
        }
    }
}

@Composable
private fun ServingControl(
    servingCount: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Servings: $servingCount")
        TextButton(onClick = onDecrease) { Text("−") }
        TextButton(onClick = onIncrease) { Text("+") }
    }
}

@Composable
private fun DetailListSection(title: String, values: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SectionTitle(title)
        values.forEach { value -> Text("• $value") }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.headlineSmall)
}

private fun RecipeMatchStatus?.statusLabel(): String = when (this) {
    RecipeMatchStatus.MAKEABLE -> "Makeable"
    RecipeMatchStatus.MISSING_INGREDIENTS -> "Missing ingredients"
    null -> "Checking inventory"
}

private fun formatQuantity(quantity: Double): String =
    BigDecimal.valueOf(quantity).stripTrailingZeros().toPlainString()
