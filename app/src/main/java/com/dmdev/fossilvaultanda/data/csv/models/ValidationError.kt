package com.dmdev.fossilvaultanda.data.csv.models

/**
 * Validation error for a specific field in a CSV row
 */
data class ValidationError(
    val field: SpecimenField,
    val originalValue: String,
    val message: String,
    val severity: Severity = Severity.BLOCKING
) {
    enum class Severity {
        BLOCKING,  // Prevents import (e.g., invalid required field)
        WARNING    // Allows import with note (e.g., unusual value)
    }
}

/**
 * Validation warning with optional auto-correction
 */
data class ValidationWarning(
    val field: SpecimenField,
    val originalValue: String,
    val message: String,
    val correctedValue: String? = null
) {
    /**
     * Returns true if this warning includes an auto-correction
     */
    fun hasCorrectedValue(): Boolean = correctedValue != null
}
