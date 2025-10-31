package com.dmdev.fossilvaultanda.data.models.enums

/**
 * Weight units for fossil specimens
 * Matches iOS WeightUnit enum
 */
enum class WeightUnit(
    val serializedName: String,
    val displayString: String
) {
    GR("gr", "Grams"),
    KG("kg", "Kilograms");

    companion object {
        /**
         * Converts a serialized string back to a WeightUnit
         * Defaults to GR if the input is null or invalid
         */
        fun fromSerializedName(name: String?): WeightUnit {
            return when (name?.lowercase()) {
                "gr" -> GR
                "kg" -> KG
                else -> GR // Default to grams
            }
        }
    }
}
