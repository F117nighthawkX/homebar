package dev.nighthawklabs.homebar.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PlaceholderRecipeTest {
    @Test
    fun `placeholder recipe retains its identifier`() {
        val recipe = PlaceholderRecipe(
            id = "example",
            name = "Example",
            description = "Placeholder",
        )

        assertEquals("example", recipe.id)
    }
}

