package dev.nighthawklabs.homebar.data.seed

import dev.nighthawklabs.homebar.domain.model.Recipe
import dev.nighthawklabs.homebar.domain.model.RecipeIngredient

object SampleRecipeData {
    val recipes = listOf(
        Recipe(
            id = "margarita",
            name = "Margarita",
            baseServingCount = 1,
            ingredients = listOf(
                RecipeIngredient("tequila", 2.0, "oz", ""),
                RecipeIngredient("lime-juice", 1.0, "oz", "Freshly squeezed"),
                RecipeIngredient("orange-liqueur", 0.75, "oz", ""),
                RecipeIngredient("agave-syrup", 0.5, "oz", ""),
            ),
            instructions = "Shake with ice and strain over fresh ice.",
            glassware = "Rocks glass",
            tools = listOf("Shaker", "Jigger", "Strainer"),
            garnish = listOf("Lime wheel", "Salt rim"),
            tags = listOf("Classic", "Citrus"),
            isFavorite = true,
            isCustom = false,
        ),
        Recipe(
            id = "old-fashioned",
            name = "Old Fashioned",
            baseServingCount = 1,
            ingredients = listOf(
                RecipeIngredient("bourbon", 2.0, "oz", ""),
                RecipeIngredient("simple-syrup", 0.25, "oz", ""),
                RecipeIngredient("angostura-bitters", 2.0, "dashes", ""),
            ),
            instructions = "Stir with ice, strain over a large ice cube, and garnish.",
            glassware = "Rocks glass",
            tools = listOf("Mixing glass", "Bar spoon", "Jigger", "Strainer"),
            garnish = listOf("Orange twist"),
            tags = listOf("Classic", "Spirit-forward"),
            isFavorite = false,
            isCustom = false,
        ),
    )
}
