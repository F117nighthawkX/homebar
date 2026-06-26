package dev.nighthawklabs.homebar.domain.logic

import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup
import org.junit.Assert.assertEquals
import org.junit.Test

class SubstitutionGroupEditorTest {
    @Test
    fun `adding two ungrouped ingredients creates a new substitute group`() {
        val result = addSubstituteToGroups(
            groups = emptyList(),
            ingredientId = "coke",
            substituteIngredientId = "pepsi",
            newGroupId = "substitute-coke-pepsi",
            newGroupName = "Coke substitutes",
        )

        assertEquals(SubstituteAddStatus.UPDATED, result.status)
        assertEquals(
            listOf(
                SubstitutionGroup(
                    id = "substitute-coke-pepsi",
                    name = "Coke substitutes",
                    ingredientIds = listOf("coke", "pepsi"),
                ),
            ),
            result.groups,
        )
    }

    @Test
    fun `adding ungrouped substitute to selected ingredient group updates that group`() {
        val result = addSubstituteToGroups(
            groups = listOf(group("cola", "coke", "pepsi")),
            ingredientId = "coke",
            substituteIngredientId = "diet-cola",
            newGroupId = "unused",
            newGroupName = "Unused",
        )

        assertEquals(SubstituteAddStatus.UPDATED, result.status)
        assertEquals(listOf("coke", "pepsi", "diet-cola"), result.groups.single().ingredientIds)
    }

    @Test
    fun `adding ungrouped selected ingredient to substitute group updates that group`() {
        val result = addSubstituteToGroups(
            groups = listOf(group("cola", "coke", "pepsi")),
            ingredientId = "diet-cola",
            substituteIngredientId = "coke",
            newGroupId = "unused",
            newGroupName = "Unused",
        )

        assertEquals(SubstituteAddStatus.UPDATED, result.status)
        assertEquals(listOf("coke", "pepsi", "diet-cola"), result.groups.single().ingredientIds)
    }

    @Test
    fun `adding ingredients from different groups keeps groups unchanged`() {
        val groups = listOf(
            group("cola", "coke", "pepsi"),
            group("citrus", "lime-juice", "lemon-juice"),
        )

        val result = addSubstituteToGroups(
            groups = groups,
            ingredientId = "coke",
            substituteIngredientId = "lime-juice",
            newGroupId = "unused",
            newGroupName = "Unused",
        )

        assertEquals(SubstituteAddStatus.DIFFERENT_GROUP_CONFLICT, result.status)
        assertEquals(groups, result.groups)
    }

    @Test
    fun `removing a substitute deletes group when one ingredient remains`() {
        val result = removeSubstituteFromGroups(
            groups = listOf(group("cola", "coke", "pepsi")),
            ingredientId = "coke",
            substituteIngredientId = "pepsi",
        )

        assertEquals(emptyList<SubstitutionGroup>(), result)
    }

    @Test
    fun `removing a substitute from larger group keeps remaining substitutes`() {
        val result = removeSubstituteFromGroups(
            groups = listOf(group("cola", "coke", "pepsi", "diet-cola")),
            ingredientId = "coke",
            substituteIngredientId = "pepsi",
        )

        assertEquals(listOf("coke", "diet-cola"), result.single().ingredientIds)
    }

    private fun group(
        id: String,
        vararg ingredientIds: String,
    ) = SubstitutionGroup(
        id = id,
        name = id,
        ingredientIds = ingredientIds.toList(),
    )
}
