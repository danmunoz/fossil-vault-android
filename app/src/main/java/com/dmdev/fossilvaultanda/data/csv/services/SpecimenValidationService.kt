package com.dmdev.fossilvaultanda.data.csv.services

import com.dmdev.fossilvaultanda.data.csv.models.CsvMappingConfiguration
import com.dmdev.fossilvaultanda.data.csv.models.ImportedSpecimenDraft
import com.dmdev.fossilvaultanda.data.csv.models.SpecimenField
import com.dmdev.fossilvaultanda.data.csv.models.ValidationError
import com.dmdev.fossilvaultanda.data.csv.models.ValidationWarning
import com.dmdev.fossilvaultanda.data.models.enums.*
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for validating CSV data and creating specimen drafts
 * Performs comprehensive field validation with auto-correction support
 */
@Singleton
class SpecimenValidationService @Inject constructor() {

    companion object {
        private val DATE_FORMATS = listOf(
            // ISO 8601
            """^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}""".toRegex(),
            // yyyy-MM-dd
            """^\d{4}-\d{2}-\d{2}$""".toRegex(),
            // dd/MM/yyyy
            """^\d{2}/\d{2}/\d{4}$""".toRegex(),
            // MM/dd/yyyy
            """^\d{2}/\d{2}/\d{4}$""".toRegex(),
            // yyyy/MM/dd
            """^\d{4}/\d{2}/\d{2}$""".toRegex()
        )

        private const val MAX_REASONABLE_DIMENSION = 10000.0 // 10 meters in mm
        private const val MAX_REASONABLE_WEIGHT = 1000000.0 // 1 ton in grams
        private const val MAX_REASONABLE_PRICE = 10000000.0 // 10 million
    }

    /**
     * Validates all CSV rows and creates specimen drafts
     */
    fun validateAndCreateDrafts(
        configuration: CsvMappingConfiguration
    ): List<ImportedSpecimenDraft> {
        return configuration.csvResult.rows.mapIndexed { index, row ->
            val parsedData = parseRowData(row, configuration)
            val (errors, warnings) = validateParsedData(parsedData)

            ImportedSpecimenDraft(
                rowIndex = index,
                isSelected = errors.none { it.severity == ValidationError.Severity.BLOCKING },
                parsedData = parsedData,
                validationErrors = errors,
                validationWarnings = warnings
            )
        }
    }

    /**
     * Parses a single CSV row into a map of field values
     */
    private fun parseRowData(
        row: List<String>,
        configuration: CsvMappingConfiguration
    ): Map<SpecimenField, String> {
        val parsedData = mutableMapOf<SpecimenField, String>()

        for (mapping in configuration.mappings) {
            if (!mapping.isMapped()) continue

            val values = mutableListOf<String>()
            val columnData = mutableMapOf<String, String>()

            for (columnName in mapping.csvColumns) {
                val columnIndex = configuration.csvResult.headers.indexOf(columnName)
                if (columnIndex >= 0 && columnIndex < row.size) {
                    val value = row[columnIndex].trim()
                    if (value.isNotEmpty()) {
                        values.add(value)
                        columnData[columnName.lowercase()] = value
                    }
                }
            }

            if (values.isNotEmpty()) {
                // Special preprocessing for acquisition method (TriloBase boolean flags)
                if (mapping.specimenField == SpecimenField.ACQUISITION_METHOD) {
                    val method = parseBooleanAcquisitionMethod(columnData)
                    if (method != null) {
                        parsedData[mapping.specimenField] = method
                    } else {
                        // Standard text value
                        parsedData[mapping.specimenField] = values.joinToString(", ")
                    }
                } else {
                    // Multiple columns concatenated with ", "
                    parsedData[mapping.specimenField] = values.joinToString(", ")
                }
            }
        }

        return parsedData
    }

    /**
     * Parses TriloBase-style boolean acquisition flags
     * Checks for Found, Bought, Traded, Gift columns with 1/0 or true/false values
     */
    private fun parseBooleanAcquisitionMethod(columnData: Map<String, String>): String? {
        val found = columnData["found"]?.let { it == "1" || it.equals("true", ignoreCase = true) } ?: false
        val bought = columnData["bought"]?.let { it == "1" || it.equals("true", ignoreCase = true) } ?: false
        val traded = columnData["traded"]?.let { it == "1" || it.equals("true", ignoreCase = true) } ?: false
        val gift = columnData["gift"]?.let { it == "1" || it.equals("true", ignoreCase = true) } ?: false

        // Return the first true value found (priority order: found > gift > purchased > traded)
        return when {
            found -> "found"
            gift -> "gifted"
            bought -> "purchased"
            traded -> "traded"
            else -> null // No boolean flags detected, use standard parsing
        }
    }

    /**
     * Validates parsed data and returns errors and warnings
     */
    private fun validateParsedData(
        parsedData: Map<SpecimenField, String>
    ): Pair<List<ValidationError>, List<ValidationWarning>> {
        val errors = mutableListOf<ValidationError>()
        val warnings = mutableListOf<ValidationWarning>()

        // Validate required fields
        if (parsedData[SpecimenField.SPECIES].isNullOrBlank()) {
            errors.add(
                ValidationError(
                    field = SpecimenField.SPECIES,
                    originalValue = "",
                    message = "Species is required and cannot be empty",
                    severity = ValidationError.Severity.BLOCKING
                )
            )
        }

        // Add general warnings for data format conversions
        addFormatConversionWarnings(parsedData, warnings)

        // Validate each field
        parsedData.forEach { (field, value) ->
            when (field) {
                // Coordinates
                SpecimenField.LATITUDE -> validateLatitude(value, errors, warnings)
                SpecimenField.LONGITUDE -> validateLongitude(value, errors, warnings)

                // Dimensions
                SpecimenField.WIDTH, SpecimenField.HEIGHT, SpecimenField.LENGTH ->
                    validateDimension(field, value, errors, warnings)
                SpecimenField.WEIGHT -> validateWeight(value, errors, warnings)
                SpecimenField.SIZE_UNIT -> validateSizeUnit(value, errors, warnings)
                SpecimenField.WEIGHT_UNIT -> validateWeightUnit(value, errors, warnings)

                // Dates
                SpecimenField.COLLECTION_DATE, SpecimenField.ACQUISITION_DATE ->
                    validateDate(field, value, errors, warnings)

                // Enums
                SpecimenField.ELEMENT -> validateFossilElement(value, errors, warnings)
                SpecimenField.ACQUISITION_METHOD -> validateAcquisitionMethod(value, errors, warnings)
                SpecimenField.CONDITION -> validateCondition(value, errors, warnings)

                // Currency
                SpecimenField.PRICE_PAID, SpecimenField.ESTIMATED_VALUE ->
                    validatePrice(field, value, errors, warnings)
                SpecimenField.PRICE_PAID_CURRENCY, SpecimenField.ESTIMATED_VALUE_CURRENCY ->
                    validateCurrency(field, value, errors, warnings)

                // Geological time (handled by geological package, just warn if unrecognized)
                SpecimenField.PERIOD -> validatePeriod(value, warnings)

                else -> {} // Other fields don't need special validation
            }
        }

        return Pair(errors, warnings)
    }

    // Validation functions for specific field types

    private fun validateLatitude(
        value: String,
        errors: MutableList<ValidationError>,
        warnings: MutableList<ValidationWarning>
    ) {
        val lat = value.toDoubleOrNull()
        if (lat == null) {
            errors.add(
                ValidationError(
                    SpecimenField.LATITUDE,
                    value,
                    "Invalid latitude format (must be a number)",
                    ValidationError.Severity.WARNING
                )
            )
        } else if (lat < -90 || lat > 90) {
            errors.add(
                ValidationError(
                    SpecimenField.LATITUDE,
                    value,
                    "Latitude must be between -90 and 90 degrees",
                    ValidationError.Severity.BLOCKING
                )
            )
        }
    }

    private fun validateLongitude(
        value: String,
        errors: MutableList<ValidationError>,
        warnings: MutableList<ValidationWarning>
    ) {
        val lng = value.toDoubleOrNull()
        if (lng == null) {
            errors.add(
                ValidationError(
                    SpecimenField.LONGITUDE,
                    value,
                    "Invalid longitude format (must be a number)",
                    ValidationError.Severity.WARNING
                )
            )
        } else if (lng < -180 || lng > 180) {
            errors.add(
                ValidationError(
                    SpecimenField.LONGITUDE,
                    value,
                    "Longitude must be between -180 and 180 degrees",
                    ValidationError.Severity.BLOCKING
                )
            )
        }
    }

    private fun validateDimension(
        field: SpecimenField,
        value: String,
        errors: MutableList<ValidationError>,
        warnings: MutableList<ValidationWarning>
    ) {
        val dimension = value.toDoubleOrNull()
        if (dimension == null) {
            errors.add(
                ValidationError(
                    field,
                    value,
                    "Invalid dimension format (must be a number)",
                    ValidationError.Severity.WARNING
                )
            )
        } else if (dimension < 0) {
            errors.add(
                ValidationError(
                    field,
                    value,
                    "Dimension cannot be negative",
                    ValidationError.Severity.BLOCKING
                )
            )
        } else if (dimension > MAX_REASONABLE_DIMENSION) {
            warnings.add(
                ValidationWarning(
                    field,
                    value,
                    "Unusually large dimension value (${dimension}mm = ${dimension / 1000}m)"
                )
            )
        }
    }

    private fun validateWeight(
        value: String,
        errors: MutableList<ValidationError>,
        warnings: MutableList<ValidationWarning>
    ) {
        val weight = value.toDoubleOrNull()
        if (weight == null) {
            errors.add(
                ValidationError(
                    SpecimenField.WEIGHT,
                    value,
                    "Invalid weight format (must be a number)",
                    ValidationError.Severity.WARNING
                )
            )
        } else if (weight < 0) {
            errors.add(
                ValidationError(
                    SpecimenField.WEIGHT,
                    value,
                    "Weight cannot be negative",
                    ValidationError.Severity.BLOCKING
                )
            )
        } else if (weight > MAX_REASONABLE_WEIGHT) {
            warnings.add(
                ValidationWarning(
                    SpecimenField.WEIGHT,
                    value,
                    "Unusually large weight value (${weight}g = ${weight / 1000}kg)"
                )
            )
        }
    }

    private fun validateSizeUnit(
        value: String,
        errors: MutableList<ValidationError>,
        warnings: MutableList<ValidationWarning>
    ) {
        val normalized = normalizeSizeUnit(value)
        val unit = SizeUnit.fromSerializedName(normalized)

        if (normalized != value) {
            warnings.add(
                ValidationWarning(
                    SpecimenField.SIZE_UNIT,
                    value,
                    "Size unit normalized",
                    correctedValue = unit.serializedName
                )
            )
        }
    }

    private fun validateWeightUnit(
        value: String,
        errors: MutableList<ValidationError>,
        warnings: MutableList<ValidationWarning>
    ) {
        val normalized = normalizeWeightUnit(value)
        val unit = WeightUnit.fromSerializedName(normalized)

        if (normalized != value) {
            warnings.add(
                ValidationWarning(
                    SpecimenField.WEIGHT_UNIT,
                    value,
                    "Weight unit normalized",
                    correctedValue = unit.serializedName
                )
            )
        }
    }

    private fun validateDate(
        field: SpecimenField,
        value: String,
        errors: MutableList<ValidationError>,
        warnings: MutableList<ValidationWarning>
    ) {
        try {
            // Try parsing as ISO 8601 instant
            Instant.parse(value)
        } catch (e: Exception) {
            // Check if it matches any known format
            val matchesKnownFormat = DATE_FORMATS.any { it.matches(value) }
            if (!matchesKnownFormat) {
                warnings.add(
                    ValidationWarning(
                        field,
                        value,
                        "Date format may not be recognized. Recommended format: yyyy-MM-dd"
                    )
                )
            }
        }
    }

    private fun validateFossilElement(
        value: String,
        errors: MutableList<ValidationError>,
        warnings: MutableList<ValidationWarning>
    ) {
        val element = FossilElement.fromSerializedName(value)
        if (element == FossilElement.OTHER && value.lowercase() != "other") {
            warnings.add(
                ValidationWarning(
                    SpecimenField.ELEMENT,
                    value,
                    "Unrecognized fossil element, will be set to 'Other'",
                    correctedValue = "Other"
                )
            )
        }
    }

    private fun validateAcquisitionMethod(
        value: String,
        errors: MutableList<ValidationError>,
        warnings: MutableList<ValidationWarning>
    ) {
        val normalized = normalizeAcquisitionMethod(value)
        if (normalized != value.lowercase()) {
            val method = AcquisitionMethod.fromSerializedName(normalized)
            warnings.add(
                ValidationWarning(
                    SpecimenField.ACQUISITION_METHOD,
                    value,
                    "Acquisition method normalized",
                    correctedValue = method.displayString
                )
            )
        }
    }

    private fun validateCondition(
        value: String,
        errors: MutableList<ValidationError>,
        warnings: MutableList<ValidationWarning>
    ) {
        // Condition is a sealed class that accepts custom values, so no validation needed
    }

    private fun validatePrice(
        field: SpecimenField,
        value: String,
        errors: MutableList<ValidationError>,
        warnings: MutableList<ValidationWarning>
    ) {
        // Remove currency symbols and whitespace
        val cleaned = value.replace(Regex("[^0-9.]"), "")
        val price = cleaned.toDoubleOrNull()

        if (price == null) {
            errors.add(
                ValidationError(
                    field,
                    value,
                    "Invalid price format (must be a number)",
                    ValidationError.Severity.WARNING
                )
            )
        } else if (price < 0) {
            warnings.add(
                ValidationWarning(
                    field,
                    value,
                    "Negative price value"
                )
            )
        } else if (price > MAX_REASONABLE_PRICE) {
            warnings.add(
                ValidationWarning(
                    field,
                    value,
                    "Unusually large price value: $${price}"
                )
            )
        } else if (cleaned != value) {
            warnings.add(
                ValidationWarning(
                    field,
                    value,
                    "Currency symbol removed from price",
                    correctedValue = cleaned
                )
            )
        }
    }

    private fun validateCurrency(
        field: SpecimenField,
        value: String,
        errors: MutableList<ValidationError>,
        warnings: MutableList<ValidationWarning>
    ) {
        val normalized = normalizeCurrencyCode(value)
        val currency = Currency.fromSerializedName(normalized)

        if (normalized != value) {
            warnings.add(
                ValidationWarning(
                    field,
                    value,
                    "Currency code normalized",
                    correctedValue = currency.currencyCode
                )
            )
        }
    }

    private fun validatePeriod(
        value: String,
        warnings: MutableList<ValidationWarning>
    ) {
        // Just check if it's a recognized period, don't error
        // GeologicalPeriod doesn't have an "unknown" value, so just skip validation for now
        // The enum will handle invalid values by defaulting
    }

    /**
     * Adds warnings for common data format conversions
     */
    private fun addFormatConversionWarnings(
        parsedData: Map<SpecimenField, String>,
        warnings: MutableList<ValidationWarning>
    ) {
        // Warn about combined dimension format
        parsedData[SpecimenField.WIDTH]?.let { width ->
            if (width.contains('x', ignoreCase = true) || width.contains('×')) {
                warnings.add(
                    ValidationWarning(
                        SpecimenField.WIDTH,
                        width,
                        "Combined dimension format detected (e.g., '2,5x1,8 cm'). Values will be split into Width, Height, Length."
                    )
                )
            }
        }

        // Warn about comma decimal separator
        parsedData.forEach { (field, value) ->
            if (field in listOf(SpecimenField.WIDTH, SpecimenField.HEIGHT, SpecimenField.LENGTH, SpecimenField.WEIGHT)) {
                if (value.contains(',') && value.count { it == ',' } == 1) {
                    warnings.add(
                        ValidationWarning(
                            field,
                            value,
                            "European decimal format detected (comma). Will be converted to period decimal."
                        )
                    )
                }
            }
        }

        // Warn about weight with unit in same field
        parsedData[SpecimenField.WEIGHT]?.let { weight ->
            if (weight.matches(Regex(".*\\s*(gr|g|kg|grams?|kilograms?)\\s*$", RegexOption.IGNORE_CASE))) {
                warnings.add(
                    ValidationWarning(
                        SpecimenField.WEIGHT,
                        weight,
                        "Weight value includes unit. Unit will be extracted and stored separately."
                    )
                )
            }
        }

        // Warn about date format if not ISO 8601
        parsedData[SpecimenField.COLLECTION_DATE]?.let { date ->
            if (!date.matches(Regex("^\\d{4}-\\d{2}-\\d{2}.*"))) {
                warnings.add(
                    ValidationWarning(
                        SpecimenField.COLLECTION_DATE,
                        date,
                        "Non-standard date format. Will attempt to parse (recommended format: yyyy-MM-dd)."
                    )
                )
            }
        }

        parsedData[SpecimenField.ACQUISITION_DATE]?.let { date ->
            if (!date.matches(Regex("^\\d{4}-\\d{2}-\\d{2}.*"))) {
                warnings.add(
                    ValidationWarning(
                        SpecimenField.ACQUISITION_DATE,
                        date,
                        "Non-standard date format. Will attempt to parse (recommended format: yyyy-MM-dd)."
                    )
                )
            }
        }
    }

    // Normalization helpers

    private fun normalizeSizeUnit(value: String): String {
        return when (value.lowercase().trim()) {
            "millimeter", "millimeters", "mm" -> "mm"
            "centimeter", "centimeters", "cm" -> "cm"
            "inch", "inches", "in", "\"" -> "inch"
            else -> value
        }
    }

    private fun normalizeWeightUnit(value: String): String {
        return when (value.lowercase().trim()) {
            "gram", "grams", "g", "gr" -> "gr"
            "kilogram", "kilograms", "kg" -> "kg"
            else -> value
        }
    }

    private fun normalizeAcquisitionMethod(value: String): String {
        return when (value.lowercase().trim()) {
            "find", "found", "collected" -> "found"
            "gift", "gifted", "given" -> "gifted"
            "buy", "bought", "purchased", "purchase" -> "purchased"
            "trade", "traded", "exchange", "exchanged" -> "traded"
            else -> value.lowercase()
        }
    }

    private fun normalizeCurrencyCode(value: String): String {
        // Handle common currency symbols
        return when (value.trim()) {
            "$" -> "USD"
            "€" -> "EUR"
            "£" -> "GBP"
            "¥" -> "JPY"
            "₽" -> "RUB"
            "₹" -> "INR"
            "₩" -> "KRW"
            "₺" -> "TRY"
            "₪" -> "ILS"
            "฿" -> "THB"
            else -> value.uppercase()
        }
    }
}
