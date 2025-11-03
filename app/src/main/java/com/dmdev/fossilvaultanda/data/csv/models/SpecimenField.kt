package com.dmdev.fossilvaultanda.data.csv.models

/**
 * All mappable specimen fields for CSV import
 * Excludes: isFavorite, imageUrls, isPublic, shareUrl (as per requirements)
 */
enum class SpecimenField(
    val displayName: String,
    val category: FieldCategory,
    val isRequired: Boolean = false
) {
    // Taxonomy (7 fields)
    KINGDOM("Kingdom", FieldCategory.TAXONOMY),
    PHYLUM("Phylum", FieldCategory.TAXONOMY),
    CLASS("Class", FieldCategory.TAXONOMY),
    ORDER("Order", FieldCategory.TAXONOMY),
    FAMILY("Family", FieldCategory.TAXONOMY),
    GENUS("Genus", FieldCategory.TAXONOMY),
    SPECIES("Species", FieldCategory.TAXONOMY, isRequired = true),

    // Identity (3 fields)
    ELEMENT("Fossil Element", FieldCategory.IDENTITY),
    INVENTORY_ID("Inventory ID", FieldCategory.IDENTITY),
    NOTES("Notes", FieldCategory.IDENTITY),

    // Geological Time (4 fields)
    ERA("Era", FieldCategory.GEOLOGICAL_TIME),
    PERIOD("Period", FieldCategory.GEOLOGICAL_TIME),
    EPOCH("Epoch", FieldCategory.GEOLOGICAL_TIME),
    AGE("Age", FieldCategory.GEOLOGICAL_TIME),

    // Location (5 fields)
    LOCATION("Location", FieldCategory.LOCATION),
    COUNTRY("Country", FieldCategory.LOCATION),
    FORMATION("Formation", FieldCategory.LOCATION),
    LATITUDE("Latitude", FieldCategory.LOCATION),
    LONGITUDE("Longitude", FieldCategory.LOCATION),

    // Dimensions (6 fields)
    WIDTH("Width", FieldCategory.DIMENSIONS),
    HEIGHT("Height", FieldCategory.DIMENSIONS),
    LENGTH("Length", FieldCategory.DIMENSIONS),
    SIZE_UNIT("Size Unit", FieldCategory.DIMENSIONS),
    WEIGHT("Weight", FieldCategory.DIMENSIONS),
    WEIGHT_UNIT("Weight Unit", FieldCategory.DIMENSIONS),

    // Acquisition (4 fields)
    COLLECTION_DATE("Collection Date", FieldCategory.ACQUISITION),
    ACQUISITION_DATE("Acquisition Date", FieldCategory.ACQUISITION),
    ACQUISITION_METHOD("Acquisition Method", FieldCategory.ACQUISITION),
    CONDITION("Condition", FieldCategory.ACQUISITION),

    // Financial (4 fields)
    PRICE_PAID("Price Paid", FieldCategory.FINANCIAL),
    PRICE_PAID_CURRENCY("Price Paid Currency", FieldCategory.FINANCIAL),
    ESTIMATED_VALUE("Estimated Value", FieldCategory.FINANCIAL),
    ESTIMATED_VALUE_CURRENCY("Estimated Value Currency", FieldCategory.FINANCIAL),

    // Metadata & Storage (5 fields)
    STORAGE_ROOM("Storage Room", FieldCategory.METADATA),
    STORAGE_CABINET("Storage Cabinet", FieldCategory.METADATA),
    STORAGE_DRAWER("Storage Drawer", FieldCategory.METADATA),
    TAG_NAMES("Tags", FieldCategory.METADATA);

    companion object {
        /**
         * Returns all fields in a specific category
         */
        fun fieldsInCategory(category: FieldCategory): List<SpecimenField> {
            return entries.filter { it.category == category }
        }

        /**
         * Returns all required fields
         */
        fun requiredFields(): List<SpecimenField> {
            return entries.filter { it.isRequired }
        }

        /**
         * Groups all fields by category
         */
        fun groupedByCategory(): Map<FieldCategory, List<SpecimenField>> {
            return entries.groupBy { it.category }
        }
    }
}
