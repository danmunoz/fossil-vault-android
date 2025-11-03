package com.dmdev.fossilvaultanda.data.csv.models

import java.util.UUID

/**
 * Draft of a specimen parsed from CSV with validation status
 * Used in preview screen before actual import
 */
data class ImportedSpecimenDraft(
    val id: String = UUID.randomUUID().toString(),
    val rowIndex: Int,
    var isSelected: Boolean = true,
    val parsedData: Map<SpecimenField, String>,
    val validationErrors: List<ValidationError> = emptyList(),
    val validationWarnings: List<ValidationWarning> = emptyList()
) {
    /**
     * Returns true if this specimen has blocking validation errors
     */
    fun hasErrors(): Boolean {
        return validationErrors.any { it.severity == ValidationError.Severity.BLOCKING }
    }

    /**
     * Returns true if this specimen has warnings
     */
    fun hasWarnings(): Boolean = validationWarnings.isNotEmpty()

    /**
     * Returns true if this specimen can be imported
     * (selected and no blocking errors)
     */
    fun canBeImported(): Boolean = isSelected && !hasErrors()

    /**
     * Gets the species name from parsed data (for display)
     */
    fun getSpeciesName(): String? = parsedData[SpecimenField.SPECIES]

    /**
     * Gets the element from parsed data (for display)
     */
    fun getElement(): String? = parsedData[SpecimenField.ELEMENT]

    /**
     * Gets the period from parsed data (for display)
     */
    fun getPeriod(): String? = parsedData[SpecimenField.PERIOD]

    /**
     * Gets the location from parsed data (for display)
     */
    fun getLocation(): String? = parsedData[SpecimenField.LOCATION]

    /**
     * Returns a user-friendly display name for this specimen
     */
    fun getDisplayName(): String {
        return getSpeciesName() ?: "Row ${rowIndex + 1}"
    }
}
