package dev.nighthawklabs.homebar.data.seed

import dev.nighthawklabs.homebar.domain.model.SubstitutionGroup

object SampleSubstitutionGroupData {
    val groups = listOf(
        SubstitutionGroup(
            id = "cola",
            name = "Cola",
            ingredientIds = listOf("coke", "pepsi"),
        ),
    )
}
