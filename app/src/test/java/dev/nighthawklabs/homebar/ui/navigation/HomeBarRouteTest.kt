package dev.nighthawklabs.homebar.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeBarRouteTest {
    @Test
    fun `recipe list route supports an optional ingredient filter`() {
        assertEquals("recipe_list", HomeBarRoute.recipeList())
        assertEquals("recipe_list?ingredientId=lime-juice", HomeBarRoute.recipeList("lime-juice"))
    }
}
