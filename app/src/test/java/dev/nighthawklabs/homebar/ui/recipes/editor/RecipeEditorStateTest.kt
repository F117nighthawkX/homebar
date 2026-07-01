package dev.nighthawklabs.homebar.ui.recipes.editor

import dev.nighthawklabs.homebar.domain.model.Ingredient
import dev.nighthawklabs.homebar.domain.model.IngredientCategory
import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeEditorStateTest {
    @Test
    fun `blank editor state cannot be saved`() {
        val state = RecipeEditorUiState().withIngredientOptionsAndSaveAvailability(listOf(tequila()))

        assertFalse(state.canSave)
        assertNull(state.toCustomRecipe(null, listOf(tequila()), "custom", 100L))
        assertEquals("Recipe name is required.", state.validation.nameError)
        assertEquals("Add at least one ingredient.", state.validation.ingredientSectionError)
        assertEquals("Add at least one instruction step.", state.validation.instructionSectionError)
    }

    @Test
    fun `editor state creates a custom recipe from valid fields`() {
        val state = RecipeEditorUiState(
            name = "House Margarita",
            baseServingCount = "2",
            ingredientLines = listOf(
                RecipeEditorIngredientLineUiState(
                    ingredientName = "Tequila",
                    unit = "oz",
                    quantity = "3",
                    note = "blanco",
                ),
            ),
            instructionSteps = listOf(
                RecipeEditorInstructionStepUiState("Shake with ice."),
            ),
            glassware = "Rocks glass",
            tools = "Shaker, Jigger",
            garnish = "Lime wheel",
            tags = "Custom, Citrus",
            isFavorite = true,
        )

        val recipe = state.toCustomRecipe(
            existingRecipe = null,
            ingredients = listOf(tequila()),
            recipeId = "house-margarita",
            nowMillis = 100L,
        )

        assertEquals("house-margarita", recipe?.id)
        assertEquals("House Margarita", recipe?.name)
        assertEquals(2, recipe?.baseServingCount)
        assertEquals(listOf(RecipeIngredient("tequila", 3.0, "oz", "blanco")), recipe?.ingredients)
        assertEquals(listOf("Shaker", "Jigger"), recipe?.tools)
        assertEquals(listOf("Lime wheel"), recipe?.garnish)
        assertEquals(listOf("Custom", "Citrus"), recipe?.tags)
        assertEquals(true, recipe?.isFavorite)
        assertEquals(true, recipe?.isCustom)
        assertNull(recipe?.sourceRecipeId)
        assertEquals(100L, recipe?.createdAt)
        assertEquals(100L, recipe?.updatedAt)
    }

    @Test
    fun `valid editor state has no validation messages`() {
        val state = validEditorState().withIngredientOptionsAndSaveAvailability(listOf(tequila()))

        assertTrue(state.canSave)
        assertTrue(state.validation.isValid)
        assertNull(state.validation.nameError)
        assertNull(state.validation.ingredientSectionError)
        assertNull(state.validation.instructionSectionError)
        assertFalse(state.validation.ingredientLineErrors.single().hasErrors)
    }

    @Test
    fun `editor state preserves custom metadata while updating recipe fields`() {
        val existingRecipe = customRecipe()
        val state = createRecipeEditorUiState(existingRecipe, listOf(tequila())).copy(
            name = "Spicy Margarita",
            instructionSteps = listOf(
                RecipeEditorInstructionStepUiState("Shake hard."),
            ),
        )

        val recipe = state.toCustomRecipe(
            existingRecipe = existingRecipe,
            ingredients = listOf(tequila()),
            recipeId = existingRecipe.id,
            nowMillis = 200L,
        )

        assertEquals("classic-margarita", recipe?.sourceRecipeId)
        assertEquals(100L, recipe?.createdAt)
        assertEquals(200L, recipe?.updatedAt)
        assertEquals("Spicy Margarita", recipe?.name)
        assertEquals("Shake hard.", recipe?.instructions)
    }

    @Test
    fun `editor state exposes existing ingredient names for editing`() {
        val state = createRecipeEditorUiState(customRecipe(), listOf(tequila()))

        assertTrue(state.ingredientLines.single().ingredientName == "Tequila")
        assertEquals("2", state.ingredientLines.single().quantity)
    }

    @Test
    fun `editor state exposes existing instructions as ordered steps`() {
        val recipe = customRecipe().copy(
            instructions = "Add tequila and lime juice.\nShake with ice.\nStrain into a rocks glass.",
        )

        val state = createRecipeEditorUiState(recipe, listOf(tequila()))

        assertEquals(
            listOf(
                "Add tequila and lime juice.",
                "Shake with ice.",
                "Strain into a rocks glass.",
            ),
            state.instructionSteps.map { it.text },
        )
    }

    @Test
    fun `adding and removing ingredient lines keeps a blank line available`() {
        val state = RecipeEditorUiState()
            .addIngredientLine()
            .removeIngredientLine(0)
            .removeIngredientLine(0)

        assertEquals(1, state.ingredientLines.size)
        assertEquals("", state.ingredientLines.single().ingredientName)
    }

    @Test
    fun `moving ingredient lines changes saved order`() {
        val state = RecipeEditorUiState(
            name = "Split Base Sour",
            baseServingCount = "1",
            ingredientLines = listOf(
                ingredientLine("tequila", "Tequila"),
                ingredientLine("rum", "Rum"),
            ),
            instructionSteps = listOf(RecipeEditorInstructionStepUiState("Shake with ice.")),
        ).moveIngredientLineDown(0)

        val recipe = state.toCustomRecipe(
            existingRecipe = null,
            ingredients = listOf(tequila(), rum()),
            recipeId = "split-base-sour",
            nowMillis = 100L,
        )

        assertEquals(listOf("rum", "tequila"), recipe?.ingredients?.map { it.ingredientId })
    }

    @Test
    fun `empty ingredient lines are not saved`() {
        val state = RecipeEditorUiState(
            name = "House Margarita",
            baseServingCount = "1",
            ingredientLines = listOf(
                RecipeEditorIngredientLineUiState(),
                ingredientLine("tequila", "Tequila"),
            ),
            instructionSteps = listOf(RecipeEditorInstructionStepUiState("Shake with ice.")),
        )

        val recipe = state.toCustomRecipe(
            existingRecipe = null,
            ingredients = listOf(tequila()),
            recipeId = "house-margarita",
            nowMillis = 100L,
        )

        assertEquals(listOf("tequila"), recipe?.ingredients?.map { it.ingredientId })
    }

    @Test
    fun `partially filled ingredient lines expose field validation messages`() {
        val state = RecipeEditorUiState(
            name = "House Margarita",
            baseServingCount = "1",
            ingredientLines = listOf(
                RecipeEditorIngredientLineUiState(
                    ingredientName = "Unknown ingredient",
                    unit = "",
                    quantity = "",
                ),
            ),
            instructionSteps = listOf(RecipeEditorInstructionStepUiState("Shake with ice.")),
        ).withIngredientOptionsAndSaveAvailability(listOf(tequila()))

        val lineError = state.validation.ingredientLineErrors.single()
        assertFalse(state.canSave)
        assertEquals("Add at least one ingredient.", state.validation.ingredientSectionError)
        assertEquals("Choose an ingredient.", lineError.ingredientError)
        assertEquals("Choose a unit.", lineError.unitError)
        assertEquals("Enter a quantity.", lineError.quantityError)
    }

    @Test
    fun `invalid ingredient lines block save even when another line is valid`() {
        val state = RecipeEditorUiState(
            name = "House Margarita",
            baseServingCount = "1",
            ingredientLines = listOf(
                ingredientLine("tequila", "Tequila"),
                RecipeEditorIngredientLineUiState(
                    ingredientName = "Rum",
                    unit = "oz",
                    quantity = "",
                ),
            ),
            instructionSteps = listOf(RecipeEditorInstructionStepUiState("Shake with ice.")),
        ).withIngredientOptionsAndSaveAvailability(listOf(tequila(), rum()))

        val recipe = state.toCustomRecipe(
            existingRecipe = null,
            ingredients = listOf(tequila(), rum()),
            recipeId = "house-margarita",
            nowMillis = 100L,
        )

        assertFalse(state.canSave)
        assertNull(recipe)
        assertNull(state.validation.ingredientSectionError)
        assertEquals("Enter a quantity.", state.validation.ingredientLineErrors[1].quantityError)
    }

    @Test
    fun `moving instruction steps changes saved order`() {
        val state = RecipeEditorUiState(
            name = "House Margarita",
            baseServingCount = "1",
            ingredientLines = listOf(ingredientLine("tequila", "Tequila")),
            instructionSteps = listOf(
                RecipeEditorInstructionStepUiState("Shake with ice."),
                RecipeEditorInstructionStepUiState("Strain over fresh ice."),
            ),
        ).moveInstructionStepDown(0)

        val recipe = state.toCustomRecipe(
            existingRecipe = null,
            ingredients = listOf(tequila()),
            recipeId = "house-margarita",
            nowMillis = 100L,
        )

        assertEquals("Strain over fresh ice.\nShake with ice.", recipe?.instructions)
    }

    @Test
    fun `empty instruction steps are not saved`() {
        val state = RecipeEditorUiState(
            name = "House Margarita",
            baseServingCount = "1",
            ingredientLines = listOf(ingredientLine("tequila", "Tequila")),
            instructionSteps = listOf(
                RecipeEditorInstructionStepUiState(),
                RecipeEditorInstructionStepUiState("Shake with ice."),
            ),
        )

        val recipe = state.toCustomRecipe(
            existingRecipe = null,
            ingredients = listOf(tequila()),
            recipeId = "house-margarita",
            nowMillis = 100L,
        )

        assertEquals("Shake with ice.", recipe?.instructions)
    }

    @Test
    fun `adding and removing instruction steps keeps a blank step available`() {
        val state = RecipeEditorUiState()
            .addInstructionStep()
            .removeInstructionStep(0)
            .removeInstructionStep(0)

        assertEquals(1, state.instructionSteps.size)
        assertEquals("", state.instructionSteps.single().text)
    }

    @Test
    fun `quantity is available only after unit selection`() {
        val blankLine = RecipeEditorIngredientLineUiState(ingredientId = "tequila", ingredientName = "Tequila")
        val measuredLine = blankLine.copy(unit = "oz")

        assertFalse(blankLine.hasUnit)
        assertTrue(measuredLine.hasUnit)
    }

    @Test
    fun `ingredient option search matches existing ingredients by partial name`() {
        val options = listOf(
            RecipeEditorIngredientOption(id = "tequila", name = "Tequila"),
            RecipeEditorIngredientOption(id = "lime-juice", name = "Lime Juice"),
        )

        val matches = filterIngredientOptions(options, " lime ")

        assertEquals(listOf("Lime Juice"), matches.map { it.name })
    }

    @Test
    fun `ingredient option search is case insensitive`() {
        val options = listOf(
            RecipeEditorIngredientOption(id = "angostura-bitters", name = "Angostura Bitters"),
            RecipeEditorIngredientOption(id = "orange-bitters", name = "Orange Bitters"),
        )

        val matches = filterIngredientOptions(options, "BITTERS")

        assertEquals(listOf("Angostura Bitters", "Orange Bitters"), matches.map { it.name })
    }

    @Test
    fun `ingredient quick create is available only when search has no matches`() {
        val options = listOf(
            RecipeEditorIngredientOption(id = "tequila", name = "Tequila"),
        )

        assertFalse(canCreateIngredientOption(options, ""))
        assertFalse(canCreateIngredientOption(options, "teq"))
        assertTrue(canCreateIngredientOption(options, "Mezcal"))
    }

    private fun customRecipe() = Recipe(
        id = "custom-margarita",
        name = "Custom Margarita",
        baseServingCount = 1,
        ingredients = listOf(RecipeIngredient("tequila", 2.0, "oz", "")),
        instructions = "Shake with ice.",
        glassware = "Rocks glass",
        tools = listOf("Shaker"),
        garnish = listOf("Lime wheel"),
        tags = listOf("Custom"),
        isFavorite = false,
        isCustom = true,
        sourceRecipeId = "classic-margarita",
        createdAt = 100L,
        updatedAt = 100L,
    )

    private fun tequila() = Ingredient(
        id = "tequila",
        name = "Tequila",
        category = IngredientCategory.SPIRIT,
        inStock = true,
        runningLow = false,
        notes = "",
    )

    private fun rum() = Ingredient(
        id = "rum",
        name = "Rum",
        category = IngredientCategory.SPIRIT,
        inStock = true,
        runningLow = false,
        notes = "",
    )

    private fun validEditorState() = RecipeEditorUiState(
        name = "House Margarita",
        baseServingCount = "1",
        ingredientLines = listOf(ingredientLine("tequila", "Tequila")),
        instructionSteps = listOf(RecipeEditorInstructionStepUiState("Shake with ice.")),
    )

    private fun ingredientLine(
        ingredientId: String,
        ingredientName: String,
    ) = RecipeEditorIngredientLineUiState(
        ingredientId = ingredientId,
        ingredientName = ingredientName,
        unit = "oz",
        quantity = "1",
        note = "",
    )
}
