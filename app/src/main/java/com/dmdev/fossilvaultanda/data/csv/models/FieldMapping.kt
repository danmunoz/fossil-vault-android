package com.dmdev.fossilvaultanda.data.csv.models

/**
 * Maps CSV columns to a specimen field
 * Supports multiple columns mapping to a single field (concatenated with ", ")
 */
data class FieldMapping(
    val specimenField: SpecimenField,
    val csvColumns: List<String> = emptyList(),
    val isConfirmed: Boolean = false,
    val confidence: Double = 0.0
) {
    /**
     * Returns true if this field has at least one CSV column mapped
     */
    fun isMapped(): Boolean = csvColumns.isNotEmpty()

    /**
     * Returns the confidence level as a category for UI display
     */
    fun getConfidenceLevel(): ConfidenceLevel {
        return when {
            confidence >= 0.9 -> ConfidenceLevel.HIGH
            confidence >= 0.7 -> ConfidenceLevel.MEDIUM
            confidence > 0.0 -> ConfidenceLevel.LOW
            else -> ConfidenceLevel.NONE
        }
    }
}

enum class ConfidenceLevel {
    NONE,    // No mapping
    LOW,     // < 0.7 (needs review)
    MEDIUM,  // 0.7 - 0.9 (probably correct)
    HIGH     // >= 0.9 (very confident)
}

/**
 * Complete mapping configuration for a CSV import
 */
data class CsvMappingConfiguration(
    val mappings: List<FieldMapping>,
    val csvResult: CsvParsingResult
) {
    /**
     * Returns the mapping for a specific field
     */
    fun getMappingFor(field: SpecimenField): FieldMapping? {
        return mappings.find { it.specimenField == field }
    }

    /**
     * Returns all mapped fields
     */
    fun getMappedFields(): List<SpecimenField> {
        return mappings.filter { it.isMapped() }.map { it.specimenField }
    }

    /**
     * Returns all unmapped required fields
     */
    fun getUnmappedRequiredFields(): List<SpecimenField> {
        val mappedFields = getMappedFields()
        return SpecimenField.requiredFields().filter { it !in mappedFields }
    }

    /**
     * Returns true if all required fields are mapped
     */
    fun hasAllRequiredFieldsMapped(): Boolean {
        return getUnmappedRequiredFields().isEmpty()
    }

    /**
     * Returns the percentage of total fields that are mapped
     */
    fun getMappingProgress(): Float {
        val totalFields = SpecimenField.entries.size
        val mappedCount = getMappedFields().size
        return mappedCount.toFloat() / totalFields.toFloat()
    }
}
