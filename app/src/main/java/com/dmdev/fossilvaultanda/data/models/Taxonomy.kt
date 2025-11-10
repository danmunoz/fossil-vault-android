package com.dmdev.fossilvaultanda.data.models

import kotlinx.serialization.Serializable

/**
 * Represents the taxonomic classification of a fossil specimen
 * Following the standard biological taxonomy hierarchy
 * Matches iOS Taxonomy struct with all 13 fields
 */
@Serializable
data class Taxonomy(
    val kingdom: String? = null,        // e.g., "Animalia"
    val phylum: String? = null,         // e.g., "Chordata"
    val subPhylum: String? = null,      // e.g., "Vertebrata"
    val taxonomicClass: String? = null, // e.g., "Reptilia" (using taxonomicClass to avoid Kotlin keyword)
    val subClass: String? = null,       // e.g., "Diapsida"
    val superOrder: String? = null,     // e.g., "Dinosauria"
    val order: String? = null,          // e.g., "Saurischia"
    val infraOrder: String? = null,     // e.g., "Theropoda"
    val subOrder: String? = null,       // e.g., "Carnosauria"
    val superFamily: String? = null,    // e.g., "Tyrannosauroidea"
    val family: String? = null,         // e.g., "Tyrannosauridae"
    val genus: String? = null,          // e.g., "Tyrannosaurus"
    val subGenus: String? = null,       // e.g., "Manospondylus"
    val species: String = ""            // e.g., "rex" (required field, most commonly used)
) {

    /**
     * Returns the full scientific name in binomial nomenclature format
     * e.g., "Tyrannosaurus rex"
     */
    fun getScientificName(): String {
        return when {
            genus != null && species.isNotBlank() -> "$genus $species"
            species.isNotBlank() -> species
            genus != null -> genus
            else -> "Unknown"
        }
    }

    /**
     * Returns the common display name, prioritizing the most specific available classification
     */
    fun getDisplayName(): String {
        return getScientificName()
    }

    /**
     * Validates the taxonomy data
     */
    fun validate(): Result<Unit> {
        return when {
            species.isBlank() -> Result.failure(IllegalArgumentException("Species cannot be blank"))
            else -> Result.success(Unit)
        }
    }

    /**
     * Converts the taxonomy to a map for Firestore storage
     */
    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "kingdom" to kingdom,
            "phylum" to phylum,
            "subPhylum" to subPhylum,
            "className" to taxonomicClass,  // iOS uses "className"
            "subClass" to subClass,
            "superOrder" to superOrder,
            "order" to order,
            "infraOrder" to infraOrder,
            "subOrder" to subOrder,
            "superFamily" to superFamily,
            "family" to family,
            "genus" to genus,
            "subGenus" to subGenus,
            "species" to species
        )
    }

    companion object {
        /**
         * Creates a simple taxonomy object from just a species string
         * Used for backward compatibility with legacy data
         */
        fun fromSpeciesString(speciesString: String): Taxonomy {
            return Taxonomy(species = speciesString.trim())
        }

        /**
         * Creates taxonomy from Firestore map data
         */
        fun fromFirestoreMap(map: Map<String, Any?>): Taxonomy {
            return Taxonomy(
                kingdom = map["kingdom"] as? String,
                phylum = map["phylum"] as? String,
                subPhylum = map["subPhylum"] as? String,
                taxonomicClass = (map["className"] ?: map["class"]) as? String,  // Support both iOS and legacy format
                subClass = map["subClass"] as? String,
                superOrder = map["superOrder"] as? String,
                order = map["order"] as? String,
                infraOrder = map["infraOrder"] as? String,
                subOrder = map["subOrder"] as? String,
                superFamily = map["superFamily"] as? String,
                family = map["family"] as? String,
                genus = map["genus"] as? String,
                subGenus = map["subGenus"] as? String,
                species = (map["species"] as? String) ?: ""
            )
        }
    }
}