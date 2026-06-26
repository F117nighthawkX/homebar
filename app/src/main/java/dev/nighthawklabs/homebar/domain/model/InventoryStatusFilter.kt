package dev.nighthawklabs.homebar.domain.model

enum class InventoryStatusFilter {
    ALL,
    IN_STOCK,
    MISSING,
    RUNNING_LOW,
    MISSING_FOR_FAVORITES,
    RUNNING_LOW_FOR_FAVORITES,
    MISSING_OR_RUNNING_LOW_FOR_FAVORITES,
}
