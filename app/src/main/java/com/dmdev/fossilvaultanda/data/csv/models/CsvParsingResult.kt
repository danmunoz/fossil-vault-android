package com.dmdev.fossilvaultanda.data.csv.models

/**
 * Result of CSV parsing containing headers, rows, and metadata
 */
data class CsvParsingResult(
    val headers: List<String>,
    val rows: List<List<String>>,
    val fileName: String,
    val detectedDelimiter: String,
    val totalRows: Int
) {
    /**
     * Returns the first N rows for preview purposes
     */
    fun getSampleRows(count: Int = 5): List<List<String>> {
        return rows.take(count)
    }

    /**
     * Returns a specific column's values
     */
    fun getColumnValues(columnIndex: Int): List<String> {
        return rows.mapNotNull { row -> row.getOrNull(columnIndex) }
    }

    /**
     * Returns true if the CSV has a header row
     */
    fun hasHeaders(): Boolean = headers.isNotEmpty()
}
