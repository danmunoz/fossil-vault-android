package com.dmdev.fossilvaultanda.data.csv.models

/**
 * Summary of CSV import results
 */
data class ImportSummary(
    val importId: String,
    val fileName: String,
    val totalProcessed: Int,
    val successfullyImported: Int,
    val failed: Int,
    val skipped: Int,
    val warnings: List<ImportWarning>,
    val importDurationMs: Long,
    val importedSpecimenIds: List<String> = emptyList()
) {
    /**
     * Returns true if all selected specimens were imported successfully
     */
    fun isFullSuccess(): Boolean = failed == 0 && skipped == 0

    /**
     * Returns true if at least some specimens were imported
     */
    fun isPartialSuccess(): Boolean = successfullyImported > 0 && (failed > 0 || skipped > 0)

    /**
     * Returns the success rate as a percentage
     */
    fun getSuccessRate(): Float {
        if (totalProcessed == 0) return 0f
        return (successfullyImported.toFloat() / totalProcessed.toFloat()) * 100f
    }
}

/**
 * Warning for a specific specimen during import
 */
data class ImportWarning(
    val rowNumber: Int,
    val specimenName: String,
    val field: SpecimenField,
    val message: String,
    val originalValue: String,
    val correctedValue: String? = null
)

/**
 * Progress tracking during import
 */
data class ImportProgress(
    val totalSpecimens: Int,
    val importedCount: Int = 0,
    val failedCount: Int = 0,
    val currentSpecimen: String? = null,
    val isCompleted: Boolean = false,
    val isCancelled: Boolean = false,
    val error: String? = null
) {
    /**
     * Returns the number of specimens processed (imported + failed)
     */
    fun completedCount(): Int = importedCount + failedCount

    /**
     * Returns the progress percentage (0-100)
     */
    fun progressPercentage(): Float {
        if (totalSpecimens == 0) return 0f
        return (completedCount().toFloat() / totalSpecimens.toFloat()) * 100f
    }

    /**
     * Returns the number of specimens remaining
     */
    fun remainingCount(): Int = totalSpecimens - completedCount()
}
