package dev.nighthawklabs.homebar.ui.recipes.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
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
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient
import java.math.BigDecimal

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RecipeDetailScreen(
    recipeId: String,
    onBack: () -> Unit,
    viewModel: RecipeDetailViewModel = viewModel(),
) {
    LaunchedEffect(recipeId) { viewModel.load(recipeId) }
    val servingState by viewModel.servingState.collectAsStateWithLifecycle()
    val recipe = servingState?.recipe

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.name ?: "Recipe") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val currentServingState = servingState
            if (currentServingState == null) {
                Text("Recipe not found.", style = MaterialTheme.typography.headlineSmall)
            } else {
                Text("Servings: ${currentServingState.selectedServingCount}")
                Column {
                    TextButton(onClick = viewModel::decreaseServings) { Text("−") }
                    TextButton(onClick = viewModel::increaseServings) { Text("+") }
                }
                Text("Ingredients", style = MaterialTheme.typography.headlineSmall)
                currentServingState.displayedIngredients.forEach { ingredient ->
                    Text(ingredient.displayText())
                }
            }
        }
    }
}

private fun RecipeIngredient.displayText(): String =
    "${formatQuantity(quantity)} $unit ${ingredientId.replace("-", " ")}".trim()

private fun formatQuantity(quantity: Double): String =
    BigDecimal.valueOf(quantity).stripTrailingZeros().toPlainString()
