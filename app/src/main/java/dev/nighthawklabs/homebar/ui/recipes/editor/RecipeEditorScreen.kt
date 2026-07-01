package dev.nighthawklabs.homebar.ui.recipes.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RecipeEditorScreen(
    recipeId: String?,
    onCancel: () -> Unit,
    onRecipeSaved: (String) -> Unit,
    viewModel: RecipeEditorViewModel = viewModel(),
) {
    LaunchedEffect(recipeId) { viewModel.load(recipeId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val title = if (recipeId == null) "New recipe" else "Edit recipe"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { TextButton(onClick = onCancel) { Text("Cancel") } },
                actions = {
                    TextButton(
                        enabled = uiState.canSave,
                        onClick = { viewModel.save(onRecipeSaved) },
                    ) {
                        Text("Save")
                    }
                },
            )
        },
    ) { contentPadding ->
        when {
            uiState.isLoading -> EditorMessage(contentPadding, "Loading recipe.")
            uiState.isNotFound -> EditorMessage(contentPadding, "Recipe cannot be edited.")
            else -> RecipeEditorForm(
                uiState = uiState,
                viewModel = viewModel,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun RecipeEditorForm(
    uiState: RecipeEditorUiState,
    viewModel: RecipeEditorViewModel,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Recipe")
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Recipe name") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = uiState.baseServingCount,
                    onValueChange = viewModel::updateBaseServingCount,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Base serving count") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                FavoriteToggle(
                    isFavorite = uiState.isFavorite,
                    onFavoriteChange = viewModel::updateFavorite,
                )
            }
        }
        item { SectionTitle("Ingredients") }
        itemsIndexed(uiState.ingredientLines) { index, ingredient ->
            IngredientLineEditor(
                lineNumber = index + 1,
                ingredient = ingredient,
                onIngredientNameChange = { viewModel.updateIngredientName(index, it) },
                onUnitChange = { viewModel.updateIngredientUnit(index, it) },
                onQuantityChange = { viewModel.updateIngredientQuantity(index, it) },
                onNoteChange = { viewModel.updateIngredientNote(index, it) },
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Instructions")
                OutlinedTextField(
                    value = uiState.instructions,
                    onValueChange = viewModel::updateInstructions,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Instructions") },
                    minLines = 4,
                )
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("Details")
                OutlinedTextField(
                    value = uiState.glassware,
                    onValueChange = viewModel::updateGlassware,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Glassware") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = uiState.tools,
                    onValueChange = viewModel::updateTools,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Tools") },
                )
                OutlinedTextField(
                    value = uiState.garnish,
                    onValueChange = viewModel::updateGarnish,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Garnish") },
                )
                OutlinedTextField(
                    value = uiState.tags,
                    onValueChange = viewModel::updateTags,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Tags") },
                )
            }
        }
    }
}

@Composable
private fun FavoriteToggle(
    isFavorite: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = isFavorite,
            onCheckedChange = onFavoriteChange,
        )
        Text("Favorite")
    }
}

@Composable
private fun IngredientLineEditor(
    lineNumber: Int,
    ingredient: RecipeEditorIngredientLineUiState,
    onIngredientNameChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Ingredient $lineNumber", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = ingredient.ingredientName,
            onValueChange = onIngredientNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Ingredient") },
            singleLine = true,
        )
        OutlinedTextField(
            value = ingredient.unit,
            onValueChange = onUnitChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Unit") },
            singleLine = true,
        )
        OutlinedTextField(
            value = ingredient.quantity,
            onValueChange = onQuantityChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Quantity") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )
        OutlinedTextField(
            value = ingredient.note,
            onValueChange = onNoteChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Note") },
            singleLine = true,
        )
    }
}

@Composable
private fun EditorMessage(
    contentPadding: PaddingValues,
    message: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
    ) {
        Text(message, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.headlineSmall)
}
