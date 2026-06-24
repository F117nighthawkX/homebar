package dev.nighthawklabs.homebar.data.seed

import dev.nighthawklabs.homebar.data.local.entity.IngredientEntity
import dev.nighthawklabs.homebar.domain.model.IngredientCategory

object SampleIngredientData {
    val ingredients = listOf(
        IngredientEntity(
            id = "tequila",
            name = "Tequila",
            category = IngredientCategory.SPIRIT,
            inStock = true,
            runningLow = false,
            notes = "",
        ),
        IngredientEntity(
            id = "orange-liqueur",
            name = "Orange liqueur",
            category = IngredientCategory.LIQUEUR,
            inStock = true,
            runningLow = false,
            notes = "",
        ),
        IngredientEntity(
            id = "lime-juice",
            name = "Lime juice",
            category = IngredientCategory.JUICE,
            inStock = true,
            runningLow = true,
            notes = "",
        ),
        IngredientEntity(
            id = "coke",
            name = "Coke",
            category = IngredientCategory.MIXER,
            inStock = false,
            runningLow = false,
            notes = "",
        ),
        IngredientEntity(
            id = "angostura-bitters",
            name = "Angostura bitters",
            category = IngredientCategory.BITTERS,
            inStock = true,
            runningLow = true,
            notes = "",
        ),
    )
}
