package com.dmdev.fossilvaultanda.data.csv.services

import com.dmdev.fossilvaultanda.data.csv.models.CsvMappingConfiguration
import com.dmdev.fossilvaultanda.data.csv.models.CsvParsingResult
import com.dmdev.fossilvaultanda.data.csv.models.FieldMapping
import com.dmdev.fossilvaultanda.data.csv.models.SpecimenField
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

/**
 * Service for automatically mapping CSV columns to specimen fields
 * Uses comprehensive alias dictionary and fuzzy matching (Levenshtein distance)
 */
@Singleton
class FieldMappingService @Inject constructor() {

    /**
     * Generates automatic field mappings based on CSV headers
     * @param csvResult The parsed CSV data
     * @return Complete mapping configuration with confidence scores
     */
    fun generateAutoMappings(csvResult: CsvParsingResult): CsvMappingConfiguration {
        val mappings = SpecimenField.entries.map { field ->
            findBestMatch(field, csvResult.headers)
        }

        return CsvMappingConfiguration(
            mappings = mappings,
            csvResult = csvResult
        )
    }

    /**
     * Finds the best matching CSV column for a specimen field
     * Returns FieldMapping with confidence score
     */
    private fun findBestMatch(field: SpecimenField, headers: List<String>): FieldMapping {
        val aliases = getAliasesForField(field)
        var bestMatch: String? = null
        var bestConfidence = 0.0

        for (header in headers) {
            for (alias in aliases) {
                val confidence = calculateMatchConfidence(header, alias)
                if (confidence > bestConfidence) {
                    bestConfidence = confidence
                    bestMatch = header
                }
            }
        }

        return FieldMapping(
            specimenField = field,
            csvColumns = if (bestMatch != null) listOf(bestMatch) else emptyList(),
            isConfirmed = bestConfidence >= 0.9, // Auto-confirm high confidence matches
            confidence = bestConfidence
        )
    }

    /**
     * Calculates match confidence between a header and an alias
     * Uses case-insensitive exact match and fuzzy matching (Levenshtein distance)
     * Returns value between 0.0 (no match) and 1.0 (perfect match)
     */
    private fun calculateMatchConfidence(header: String, alias: String): Double {
        val headerNormalized = header.trim().lowercase()
        val aliasNormalized = alias.lowercase()

        // Exact match = perfect score
        if (headerNormalized == aliasNormalized) return 1.0

        // Contains match = high score
        if (headerNormalized.contains(aliasNormalized) ||
            aliasNormalized.contains(headerNormalized)
        ) {
            return 0.85
        }

        // Fuzzy match using Levenshtein distance
        val distance = levenshteinDistance(headerNormalized, aliasNormalized)
        val maxLength = max(headerNormalized.length, aliasNormalized.length)

        if (maxLength == 0) return 0.0

        val similarity = 1.0 - (distance.toDouble() / maxLength.toDouble())

        // Only consider it a match if similarity > 0.6
        return if (similarity > 0.6) similarity else 0.0
    }

    /**
     * Calculates Levenshtein distance between two strings
     * Used for fuzzy string matching
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val len1 = s1.length
        val len2 = s2.length

        val dp = Array(len1 + 1) { IntArray(len2 + 1) }

        for (i in 0..len1) dp[i][0] = i
        for (j in 0..len2) dp[0][j] = j

        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }

        return dp[len1][len2]
    }

    /**
     * Returns comprehensive list of aliases for each specimen field
     * Based on iOS implementation with Android-specific additions
     */
    private fun getAliasesForField(field: SpecimenField): List<String> {
        return when (field) {
            // Taxonomy
            SpecimenField.KINGDOM -> listOf("kingdom", "regnum")
            SpecimenField.PHYLUM -> listOf("phylum", "division")
            SpecimenField.CLASS -> listOf("class", "classis")
            SpecimenField.ORDER -> listOf("order", "ordo")
            SpecimenField.FAMILY -> listOf("family", "familia")
            SpecimenField.GENUS -> listOf("genus", "genera")
            SpecimenField.SPECIES -> listOf(
                "species", "taxon", "scientific name", "sci name", "sci_name",
                "sp.", "binomial", "latin name", "organism", "fossil name",
                "specimen name", "name"
            )

            // Identity
            SpecimenField.ELEMENT -> listOf(
                "element", "fossil element", "fossil type", "part", "body part",
                "anatomical element", "fossil part", "type", "piece"
            )
            SpecimenField.INVENTORY_ID -> listOf(
                "inventory id", "inv id", "catalog id", "cat id", "specimen id",
                "spec id", "acc. no.", "acc no", "accession number", "catalog number",
                "id", "identifier", "catalog no", "specimen number", "number"
            )
            SpecimenField.NOTES -> listOf(
                "notes", "note", "comments", "comment", "description",
                "remarks", "remark", "additional info", "details", "memo"
            )

            // Geological Time
            SpecimenField.ERA -> listOf("era", "geologic era", "geological era")
            SpecimenField.PERIOD -> listOf(
                "period", "geological period", "geologic period", "time period"
            )
            SpecimenField.EPOCH -> listOf("epoch", "geologic epoch", "geological epoch")
            SpecimenField.AGE -> listOf(
                "age", "stage", "geologic age", "geological age", "time", "geology"
            )

            // Location
            SpecimenField.LOCATION -> listOf(
                "location", "locality", "site", "place", "discovery site",
                "collection site", "found at", "where", "provenance", "origin",
                "site description"
            )
            SpecimenField.COUNTRY -> listOf("country", "nation", "state")
            SpecimenField.FORMATION -> listOf(
                "formation", "geological formation", "geologic formation",
                "fm", "rock formation", "strata", "stratum"
            )
            SpecimenField.LATITUDE -> listOf(
                "latitude", "lat", "coord lat", "gps lat", "y", "lat."
            )
            SpecimenField.LONGITUDE -> listOf(
                "longitude", "long", "lon", "lng", "coord long", "gps long",
                "gps lon", "x", "long.", "lon."
            )

            // Dimensions
            SpecimenField.WIDTH -> listOf(
                "width", "w", "wide", "breadth", "dimensions", "dimension", "size"
            )
            SpecimenField.HEIGHT -> listOf("height", "h", "tall", "depth", "d")
            SpecimenField.LENGTH -> listOf("length", "l", "long")
            SpecimenField.SIZE_UNIT -> listOf(
                "size unit", "unit", "measurement unit", "dimension unit",
                "units", "measure"
            )
            SpecimenField.WEIGHT -> listOf(
                "weight", "mass", "wt", "wt.", "grams", "kilograms"
            )
            SpecimenField.WEIGHT_UNIT -> listOf(
                "weight unit", "mass unit", "wt unit", "weight units"
            )

            // Acquisition
            SpecimenField.COLLECTION_DATE -> listOf(
                "collection date", "collected date", "found date", "discovery date",
                "date collected", "date found", "find date", "col date"
            )
            SpecimenField.ACQUISITION_DATE -> listOf(
                "acquisition date", "acquired date", "purchase date", "acq date",
                "date acquired", "obtained date", "date obtained"
            )
            SpecimenField.ACQUISITION_METHOD -> listOf(
                "acquisition method", "acq method", "how acquired", "obtained by",
                "acquisition", "source", "acquired from", "method",
                "found", "bought", "traded", "gift", "for sale", "for trade"
            )
            SpecimenField.CONDITION -> listOf(
                "condition", "preservation", "quality", "state", "grade", "rating"
            )

            // Financial
            SpecimenField.PRICE_PAID -> listOf(
                "price paid", "purchase price", "cost", "paid", "price",
                "bought for", "amount paid", "amount"
            )
            SpecimenField.PRICE_PAID_CURRENCY -> listOf(
                "price paid currency", "purchase currency", "paid currency",
                "currency paid", "cost currency"
            )
            SpecimenField.ESTIMATED_VALUE -> listOf(
                "estimated value", "value", "est value", "worth", "appraisal",
                "estimated price", "market value", "current value"
            )
            SpecimenField.ESTIMATED_VALUE_CURRENCY -> listOf(
                "estimated value currency", "value currency", "est currency",
                "appraisal currency"
            )

            // Metadata & Storage
            SpecimenField.STORAGE_ROOM -> listOf(
                "storage room", "room", "storage location", "stored in room", "location room"
            )
            SpecimenField.STORAGE_CABINET -> listOf(
                "storage cabinet", "cabinet", "drawer unit", "stored in cabinet"
            )
            SpecimenField.STORAGE_DRAWER -> listOf(
                "storage drawer", "drawer", "tray", "stored in drawer"
            )
            SpecimenField.TAG_NAMES -> listOf(
                "tags", "tag names", "labels", "keywords", "categories",
                "tag", "label"
            )
        }
    }

    /**
     * Updates a specific field mapping with new CSV columns
     */
    fun updateMapping(
        configuration: CsvMappingConfiguration,
        field: SpecimenField,
        newColumns: List<String>
    ): CsvMappingConfiguration {
        val updatedMappings = configuration.mappings.map { mapping ->
            if (mapping.specimenField == field) {
                mapping.copy(
                    csvColumns = newColumns,
                    isConfirmed = true,
                    confidence = if (newColumns.isNotEmpty()) 1.0 else 0.0
                )
            } else {
                mapping
            }
        }

        return configuration.copy(mappings = updatedMappings)
    }
}
