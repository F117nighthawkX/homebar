package dev.nighthawklabs.homebar.ui.recipes.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.nighthawklabs.homebar.domain.model.IngredientCategory

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
                ingredientOptions = uiState.ingredientOptions,
                canMoveUp = index > 0,
                canMoveDown = index < uiState.ingredientLines.lastIndex,
                onIngredientSelected = { ingredientId, ingredientName ->
                    viewModel.updateIngredient(index, ingredientId, ingredientName)
                },
                onIngredientSearchTextChange = { ingredientName ->
                    viewModel.updateIngredientSearchText(index, ingredientName)
                },
                onCreateIngredient = { ingredientName, category ->
                    viewModel.createIngredientForLine(index, ingredientName, category)
                },
                onUnitChange = { viewModel.updateIngredientUnit(index, it) },
                onQuantityChange = { viewModel.updateIngredientQuantity(index, it) },
                onNoteChange = { viewModel.updateIngredientNote(index, it) },
                onRemove = { viewModel.removeIngredientLine(index) },
                onMoveUp = { viewModel.moveIngredientLineUp(index) },
                onMoveDown = { viewModel.moveIngredientLineDown(index) },
            )
        }
        item {
            TextButton(onClick = viewModel::addIngredientLine) {
                Text("Add ingredient line")
            }
        }
        item {
            SectionTitle("Instructions")
        }
        itemsIndexed(uiState.instructionSteps) { index, step ->
            InstructionStepEditor(
                stepNumber = index + 1,
                step = step,
                canMoveUp = index > 0,
                canMoveDown = index < uiState.instructionSteps.lastIndex,
                onTextChange = { viewModel.updateInstructionStep(index, it) },
                onRemove = { viewModel.removeInstructionStep(index) },
                onMoveUp = { viewModel.moveInstructionStepUp(index) },
                onMoveDown = { viewModel.moveInstructionStepDown(index) },
            )
        }
        item {
            TextButton(onClick = viewModel::addInstructionStep) {
                Text("Add instruction step")
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
private fun InstructionStepEditor(
    stepNumber: Int,
    step: RecipeEditorInstructionStepUiState,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onTextChange: (String) -> Unit,
    onRemove: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = step.text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Step $stepNumber") },
            minLines = 2,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(
                enabled = canMoveUp,
                onClick = onMoveUp,
            ) {
                Text("Move up")
            }
            TextButton(
                enabled = canMoveDown,
                onClick = onMoveDown,
            ) {
                Text("Move down")
            }
            TextButton(onClick = onRemove) {
                Text("Remove")
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
    ingredientOptions: List<RecipeEditorIngredientOption>,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onIngredientSelected: (String, String) -> Unit,
    onIngredientSearchTextChange: (String) -> Unit,
    onCreateIngredient: (String, IngredientCategory) -> Unit,
    onUnitChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onRemove: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Ingredient $lineNumber", style = MaterialTheme.typography.titleMedium)
        IngredientPicker(
            selectedIngredientName = ingredient.ingredientName,
            ingredientOptions = ingredientOptions,
            onIngredientSelected = onIngredientSelected,
            onIngredientSearchTextChange = onIngredientSearchTextChange,
            onCreateIngredient = onCreateIngredient,
        )
        UnitPicker(
            selectedUnit = ingredient.unit,
            onUnitSelected = onUnitChange,
        )
        if (ingredient.hasUnit) {
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(
                enabled = canMoveUp,
                onClick = onMoveUp,
            ) {
                Text("Move up")
            }
            TextButton(
                enabled = canMoveDown,
                onClick = onMoveDown,
            ) {
                Text("Move down")
            }
            TextButton(onClick = onRemove) {
                Text("Remove")
            }
        }
    }
}

@Composable
private fun IngredientPicker(
    selectedIngredientName: String,
    ingredientOptions: List<RecipeEditorIngredientOption>,
    onIngredientSelected: (String, String) -> Unit,
    onIngredientSearchTextChange: (String) -> Unit,
    onCreateIngredient: (String, IngredientCategory) -> Unit,
) {
    var searchText by remember(selectedIngredientName) { mutableStateOf(selectedIngredientName) }
    var quickCreateName by remember { mutableStateOf<String?>(null) }
    val matchingIngredients = filterIngredientOptions(ingredientOptions, searchText)
    val canCreateIngredient = canCreateIngredientOption(ingredientOptions, searchText)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { value ->
                searchText = value
                onIngredientSearchTextChange(value)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search ingredients") },
            singleLine = true,
        )
        if (searchText.isNotBlank()) {
            matchingIngredients.forEach { ingredient ->
                TextButton(
                    onClick = {
                        searchText = ingredient.name
                        onIngredientSelected(ingredient.id, ingredient.name)
                    },
                ) {
                    Text(ingredient.name)
                }
            }
            if (canCreateIngredient) {
                TextButton(onClick = { quickCreateName = searchText.trim() }) {
                    Text("Create ingredient")
                }
            }
        }
    }

    quickCreateName?.let { initialName ->
        CreateIngredientDialog(
            initialName = initialName,
            onDismiss = { quickCreateName = null },
            onCreate = { name, category ->
                quickCreateName = null
                onCreateIngredient(name, category)
            },
        )
    }
}

@Composable
private fun CreateIngredientDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onCreate: (String, IngredientCategory) -> Unit,
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var category by remember { mutableStateOf(IngredientCategory.OTHER) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create ingredient") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ingredient name") },
                    singleLine = true,
                )
                CategoryPicker(
                    selectedCategory = category,
                    onCategorySelected = { category = it },
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.trim().isNotBlank(),
                onClick = { onCreate(name, category) },
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun CategoryPicker(
    selectedCategory: IngredientCategory,
    onCategorySelected: (IngredientCategory) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { isExpanded = true }) {
            Text(categoryLabel(selectedCategory))
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            IngredientCategory.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text(categoryLabel(category)) },
                    onClick = {
                        isExpanded = false
                        onCategorySelected(category)
                    },
                )
            }
        }
    }
}

@Composable
private fun UnitPicker(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val unitOptions = if (selectedUnit.isBlank() || selectedUnit in RecipeIngredientUnitOptions) {
        RecipeIngredientUnitOptions
    } else {
        listOf(selectedUnit) + RecipeIngredientUnitOptions
    }

    Box {
        TextButton(onClick = { isExpanded = true }) {
            Text(selectedUnit.ifBlank { "Choose unit" })
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            unitOptions.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit) },
                    onClick = {
                        isExpanded = false
                        onUnitSelected(unit)
                    },
                )
            }
        }
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
