package com.dmdev.fossilvaultanda.data.csv.models

/**
 * Categories for grouping specimen fields in the field mapping UI
 * Matches the iOS implementation for consistency
 */
enum class FieldCategory(val displayName: String) {
    TAXONOMY("Taxonomy"),
    IDENTITY("Identity"),
    GEOLOGICAL_TIME("Geological Time"),
    LOCATION("Location"),
    DIMENSIONS("Dimensions"),
    ACQUISITION("Acquisition"),
    FINANCIAL("Financial"),
    METADATA("Metadata & Storage")
}
