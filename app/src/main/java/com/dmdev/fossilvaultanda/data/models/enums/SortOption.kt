package com.dmdev.fossilvaultanda.data.models.enums

import kotlinx.serialization.Serializable

@Serializable
enum class SortOption(val displayName: String) {
    RECENT("Recent"),
    OLDEST("Oldest"),
    NAME_A_Z("Name A-Z"),
    NAME_Z_A("Name Z-A")
}