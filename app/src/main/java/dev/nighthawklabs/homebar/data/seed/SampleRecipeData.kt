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
        Recipe(
            id = "cuba-libre",
            name = "Cuba Libre",
            baseServingCount = 1,
            ingredients = listOf(
                RecipeIngredient("rum", 2.0, "oz", ""),
                RecipeIngredient("coke", 4.0, "oz", ""),
                RecipeIngredient("lime-juice", 0.5, "oz", "Freshly squeezed"),
            ),
            instructions = "Build over ice and gently stir.",
            glassware = "Highball glass",
            tools = listOf("Jigger", "Bar spoon"),
            garnish = listOf("Lime wedge"),
            tags = listOf("Classic", "Highball"),
            isFavorite = false,
            isCustom = false,
        ),
        Recipe(
            id = "whiskey-sour",
            name = "Whiskey Sour",
            baseServingCount = 1,
            ingredients = listOf(
                RecipeIngredient("bourbon", 2.0, "oz", ""),
                RecipeIngredient("lemon-juice", 0.75, "oz", "Freshly squeezed"),
                RecipeIngredient("simple-syrup", 0.75, "oz", ""),
            ),
            instructions = "Shake with ice and strain into a chilled glass.",
            glassware = "Rocks glass",
            tools = listOf("Shaker", "Jigger", "Strainer"),
            garnish = listOf("Lemon peel"),
            tags = listOf("Classic", "Sour"),
            isFavorite = false,
            isCustom = false,
        ),
    )
}
