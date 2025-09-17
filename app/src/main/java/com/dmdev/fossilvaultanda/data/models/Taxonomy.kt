package com.dmdev.fossilvaultanda.data.models

import kotlinx.serialization.Serializable

/**
 * Represents the taxonomic classification of a fossil specimen
 * Following the standard biological taxonomy hierarchy
 */
@Serializable
data class Taxonomy(
    val kingdom: String? = null,        // e.g., "Animalia"
    val phylum: String? = null,         // e.g., "Chordata"
    val taxonomicClass: String? = null, // e.g., "Reptilia" (using taxonomicClass to avoid Kotlin keyword)
    val order: String? = null,          // e.g., "Saurischia"
    val family: String? = null,         // e.g., "Tyrannosauridae"
    val genus: String? = null,          // e.g., "Tyrannosaurus"
    val species: String = "",           // e.g., "rex" (required field, most commonly used)
    val subspecies: String? = null      // e.g., "tyrannicus" (rare, for subspecies classification)
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
            "class" to taxonomicClass,
            "order" to order,
            "family" to family,
            "genus" to genus,
            "species" to species,
            "subspecies" to subspecies
        )
    }

    companion object {
        /**
         * Creates a simple taxonomy object from just a species string
         * Used for backward compatibility with legacy data
         */
        fun fromSpeciesString(speciesString: String): Taxonomy {
            // Try to parse genus and species if it looks like "Genus species"
            val parts = speciesString.trim().split(" ")
            return if (parts.size >= 2) {
                Taxonomy(
                    genus = parts[0],
                    species = parts.drop(1).joinToString(" ")
                )
            } else {
                Taxonomy(species = speciesString.trim())
            }
        }

        /**
         * Creates taxonomy from Firestore map data
         */
        fun fromFirestoreMap(map: Map<String, Any?>): Taxonomy {
            return Taxonomy(
                kingdom = map["kingdom"] as? String,
                phylum = map["phylum"] as? String,
                taxonomicClass = map["class"] as? String,
                order = map["order"] as? String,
                family = map["family"] as? String,
                genus = map["genus"] as? String,
                species = (map["species"] as? String) ?: "",
                subspecies = map["subspecies"] as? String
            )
        }
    }
}