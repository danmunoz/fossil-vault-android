package com.dmdev.fossilvaultanda.data.csv.services

import android.content.Context
import android.net.Uri
import com.dmdev.fossilvaultanda.data.csv.models.CsvParsingResult
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStreamReader
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for parsing CSV files with automatic delimiter detection
 * Implements RFC 4180 compliant parsing using Apache Commons CSV
 */
@Singleton
class CsvParsingService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val SUPPORTED_DELIMITERS = listOf(',', ';', '\t', '|')
        private val SUPPORTED_ENCODINGS = listOf(
            Charsets.UTF_8,
            Charsets.UTF_16,
            Charset.forName("Windows-1252"),
            Charsets.ISO_8859_1
        )
        private const val SAMPLE_LINES_FOR_DETECTION = 10
    }

    /**
     * Parses a CSV file from a URI
     * @param uri The content URI of the CSV file
     * @param fileName The original file name
     * @return CsvParsingResult containing headers, rows, and metadata
     * @throws CsvParsingException if parsing fails
     */
    suspend fun parseCSV(uri: Uri, fileName: String): CsvParsingResult {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw CsvParsingException("Unable to open file")

            // Read bytes once (inputStream.reset() is not supported by all streams)
            val bytes = inputStream.use { it.readBytes() }

            // Try different encodings until one works
            var lastException: Exception? = null
            for (encoding in SUPPORTED_ENCODINGS) {
                try {
                    return parseWithEncoding(bytes, fileName, encoding)
                } catch (e: Exception) {
                    lastException = e
                }
            }

            throw CsvParsingException(
                "Failed to parse CSV with any supported encoding",
                lastException
            )
        } catch (e: Exception) {
            if (e is CsvParsingException) throw e
            throw CsvParsingException("Error reading file: ${e.message}", e)
        }
    }

    /**
     * Parses CSV content with a specific encoding
     */
    private fun parseWithEncoding(
        bytes: ByteArray,
        fileName: String,
        encoding: Charset
    ): CsvParsingResult {
        val content = String(bytes, encoding)

        // Detect delimiter by analyzing first few lines
        val delimiter = detectDelimiter(content)

        // Parse CSV with detected delimiter
        val csvFormat = CSVFormat.RFC4180.builder()
            .setDelimiter(delimiter)
            .setHeader()
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .setIgnoreEmptyLines(true)
            .build()

        val parser = CSVParser.parse(content, csvFormat)
        val headers = parser.headerNames
        val rows = mutableListOf<List<String>>()

        parser.forEach { record ->
            val row = record.toList()
            rows.add(row)
        }

        parser.close()

        return CsvParsingResult(
            headers = headers,
            rows = rows,
            fileName = fileName,
            detectedDelimiter = getDelimiterName(delimiter),
            totalRows = rows.size
        )
    }

    /**
     * Detects the most likely delimiter by analyzing the first few lines
     * Returns the delimiter with the most consistent column count
     */
    private fun detectDelimiter(content: String): Char {
        val lines = content.lines().take(SAMPLE_LINES_FOR_DETECTION)
        if (lines.isEmpty()) return ','

        val delimiterScores = SUPPORTED_DELIMITERS.associateWith { delimiter ->
            scoreDelimiter(lines, delimiter)
        }

        return delimiterScores.maxByOrNull { it.value }?.key ?: ','
    }

    /**
     * Scores a delimiter based on:
     * 1. Consistency of column count across lines
     * 2. Number of columns (more is better, but not single column)
     * 3. Presence in all lines
     */
    private fun scoreDelimiter(lines: List<String>, delimiter: Char): Double {
        val columnCounts = lines.map { line ->
            // Simple split (doesn't handle quotes, but good enough for detection)
            line.split(delimiter).size
        }

        if (columnCounts.isEmpty()) return 0.0

        val avgColumns = columnCounts.average()
        val variance = columnCounts.map { (it - avgColumns) * (it - avgColumns) }.average()
        val consistency = 1.0 / (1.0 + variance) // Higher consistency = lower variance

        // Penalize single-column results (probably wrong delimiter)
        val columnBonus = if (avgColumns > 1) avgColumns * 0.1 else -10.0

        return consistency * 10 + columnBonus
    }

    /**
     * Returns a human-readable name for the delimiter
     */
    private fun getDelimiterName(delimiter: Char): String {
        return when (delimiter) {
            ',' -> "Comma (,)"
            ';' -> "Semicolon (;)"
            '\t' -> "Tab"
            '|' -> "Pipe (|)"
            else -> delimiter.toString()
        }
    }

    /**
     * Validates that a file appears to be a valid CSV
     * @param uri The content URI of the file
     * @return true if the file appears to be a valid CSV
     */
    fun isValidCSV(uri: Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return false
            val firstLine = InputStreamReader(inputStream).use { reader ->
                reader.readLines().firstOrNull()
            }
            inputStream.close()

            // Check if first line contains at least one supported delimiter
            firstLine?.any { it in SUPPORTED_DELIMITERS } ?: false
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Exception thrown when CSV parsing fails
 */
class CsvParsingException(message: String, cause: Throwable? = null) : Exception(message, cause)
