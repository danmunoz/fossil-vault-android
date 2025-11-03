package com.dmdev.fossilvaultanda.data.csv.repository

import android.net.Uri
import com.dmdev.fossilvaultanda.data.csv.models.*
import com.dmdev.fossilvaultanda.data.csv.services.CsvParsingService
import com.dmdev.fossilvaultanda.data.csv.services.FieldMappingService
import com.dmdev.fossilvaultanda.data.csv.services.SpecimenValidationService
import com.dmdev.fossilvaultanda.data.models.Specimen
import com.dmdev.fossilvaultanda.data.models.StorageMethod
import com.dmdev.fossilvaultanda.data.models.Taxonomy
import com.dmdev.fossilvaultanda.data.models.enums.*
import com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
import com.fossilVault.geological.GeologicalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for orchestrating CSV import operations
 * Handles parsing, validation, and batch import of specimens
 */
@Singleton
class CsvImportRepository @Inject constructor(
    private val csvParsingService: CsvParsingService,
    private val fieldMappingService: FieldMappingService,
    private val validationService: SpecimenValidationService,
    private val databaseManager: DatabaseManaging
) {

    companion object {
        private const val BATCH_SIZE = 10

        // Common date formats for CSV import (TriloBase, Excel, etc.)
        private val DATE_FORMATS = listOf(
            SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US),
            SimpleDateFormat("dd/MM/yyyy", Locale.US),
            SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US),
            SimpleDateFormat("MM/dd/yyyy", Locale.US),
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
            SimpleDateFormat("yyyy-MM-dd", Locale.US),
            SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US),
            SimpleDateFormat("dd.MM.yyyy", Locale.US),
            SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US),
            SimpleDateFormat("yyyy/MM/dd", Locale.US)
        )

        /**
         * Parses a date string with multiple format attempts
         * First tries ISO 8601, then common CSV date formats
         */
        private fun parseFlexibleDate(dateString: String): Instant? {
            // Try ISO 8601 first
            try {
                return Instant.parse(dateString)
            } catch (e: Exception) {
                // Ignore and try other formats
            }

            // Try common date formats
            for (format in DATE_FORMATS) {
                try {
                    val date = format.parse(dateString)
                    if (date != null) {
                        return Instant.fromEpochMilliseconds(date.time)
                    }
                } catch (e: Exception) {
                    // Try next format
                }
            }

            return null
        }

        /**
         * Normalizes numeric strings by replacing comma decimal separators with periods
         * Handles European number format (e.g., "2,5" → "2.5")
         */
        private fun normalizeNumeric(value: String): String {
            // Replace comma with period only if it appears to be a decimal separator
            // (single comma, followed by digits)
            return if (value.contains(',') && value.indexOf(',') == value.lastIndexOf(',')) {
                value.replace(',', '.')
            } else {
                value
            }
        }

        /**
         * Parses a combined dimension string (e.g., "2,5x1,8 cm" or "2.5x1.8x3.2")
         * Returns map of dimension values and optionally unit
         */
        private fun parseCombinedDimensions(value: String): Map<String, String> {
            val result = mutableMapOf<String, String>()

            // Extract unit if present (cm, mm, inch, etc.)
            val unitPattern = Regex("""(cm|mm|inch|in)\s*$""", RegexOption.IGNORE_CASE)
            val unitMatch = unitPattern.find(value)
            val unit = unitMatch?.value?.trim()?.lowercase()
            val numericPart = value.replace(unitPattern, "").trim()

            // Parse dimensions (format: "2,5x1,8" or "2.5x1.8x3.2")
            val dimensions = numericPart.split('x', 'X', '×', '*')
                .mapNotNull {
                    normalizeNumeric(it.trim()).toDoubleOrNull()
                }

            // Map to width, height, length
            if (dimensions.isNotEmpty()) {
                result["width"] = dimensions.getOrNull(0)?.toString() ?: ""
                result["height"] = dimensions.getOrNull(1)?.toString() ?: ""
                result["length"] = dimensions.getOrNull(2)?.toString() ?: ""

                // Map unit to standard format
                unit?.let {
                    result["size_unit"] = when (it) {
                        "cm" -> "cm"
                        "mm" -> "mm"
                        "inch", "in" -> "inch"
                        else -> "mm" // default
                    }
                }
            }

            return result
        }

        /**
         * Parses weight with unit in same field (e.g., "125 gr" or "1.5 kg")
         * Returns map with weight value and unit
         */
        private fun parseWeightWithUnit(value: String): Map<String, String> {
            val result = mutableMapOf<String, String>()

            // Extract unit (gr, g, kg, grams, etc.)
            val unitPattern = Regex("""(gr|g|kg|grams?|kilograms?)\s*$""", RegexOption.IGNORE_CASE)
            val unitMatch = unitPattern.find(value)
            val unit = unitMatch?.value?.trim()?.lowercase()

            // Extract numeric value
            val numericPart = value.replace(unitPattern, "").trim()
            val weight = normalizeNumeric(numericPart).toDoubleOrNull()

            weight?.let {
                result["weight"] = it.toString()
                result["weight_unit"] = when (unit) {
                    "gr", "g", "gram", "grams" -> "gr"
                    "kg", "kilogram", "kilograms" -> "kg"
                    else -> "gr" // default
                }
            }

            return result
        }
    }

    /**
     * Parses a CSV file and returns the result
     */
    suspend fun parseCSV(uri: Uri, fileName: String): Result<CsvParsingResult> {
        return try {
            val result = csvParsingService.parseCSV(uri, fileName)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates automatic field mappings from CSV headers
     */
    fun generateFieldMappings(csvResult: CsvParsingResult): CsvMappingConfiguration {
        return fieldMappingService.generateAutoMappings(csvResult)
    }

    /**
     * Updates a specific field mapping
     */
    fun updateFieldMapping(
        configuration: CsvMappingConfiguration,
        field: SpecimenField,
        columns: List<String>
    ): CsvMappingConfiguration {
        return fieldMappingService.updateMapping(configuration, field, columns)
    }

    /**
     * Validates CSV data and creates specimen drafts for preview
     */
    fun validateAndCreateDrafts(
        configuration: CsvMappingConfiguration
    ): List<ImportedSpecimenDraft> {
        return validationService.validateAndCreateDrafts(configuration)
    }

    /**
     * Imports selected specimens with progress tracking
     * Returns a Flow that emits progress updates
     */
    fun importSpecimens(
        drafts: List<ImportedSpecimenDraft>,
        userId: String
    ): Flow<ImportProgress> = flow {
        val selectedDrafts = drafts.filter { it.canBeImported() }
        val totalCount = selectedDrafts.size
        var importedCount = 0
        var failedCount = 0
        val importedIds = mutableListOf<String>()
        val warnings = mutableListOf<ImportWarning>()

        // Emit initial progress
        emit(
            ImportProgress(
                totalSpecimens = totalCount,
                importedCount = 0,
                failedCount = 0
            )
        )

        // Process in batches
        selectedDrafts.chunked(BATCH_SIZE).forEach { batch ->
            batch.forEach { draft ->
                try {
                    // Convert draft to Specimen
                    val specimen = draftToSpecimen(draft, userId)

                    // Check for duplicate inventory ID
                    if (specimen.inventoryId != null) {
                        val exists = databaseManager.checkInventoryIdExists(specimen.inventoryId)
                        if (exists) {
                            failedCount++
                            emit(
                                ImportProgress(
                                    totalSpecimens = totalCount,
                                    importedCount = importedCount,
                                    failedCount = failedCount,
                                    currentSpecimen = draft.getDisplayName(),
                                    error = "Duplicate inventory ID: ${specimen.inventoryId}"
                                )
                            )
                            return@forEach
                        }
                    }

                    // Save specimen
                    databaseManager.save(specimen)
                    importedIds.add(specimen.id)
                    importedCount++

                    // Add warnings for this specimen
                    draft.validationWarnings.forEach { warning ->
                        warnings.add(
                            ImportWarning(
                                rowNumber = draft.rowIndex + 1,
                                specimenName = draft.getDisplayName(),
                                field = warning.field,
                                message = warning.message,
                                originalValue = warning.originalValue,
                                correctedValue = warning.correctedValue
                            )
                        )
                    }

                    // Emit progress
                    emit(
                        ImportProgress(
                            totalSpecimens = totalCount,
                            importedCount = importedCount,
                            failedCount = failedCount,
                            currentSpecimen = draft.getDisplayName()
                        )
                    )
                } catch (e: Exception) {
                    failedCount++
                    emit(
                        ImportProgress(
                            totalSpecimens = totalCount,
                            importedCount = importedCount,
                            failedCount = failedCount,
                            currentSpecimen = draft.getDisplayName(),
                            error = e.message
                        )
                    )
                }
            }
        }

        // Emit completion
        emit(
            ImportProgress(
                totalSpecimens = totalCount,
                importedCount = importedCount,
                failedCount = failedCount,
                isCompleted = true
            )
        )
    }

    /**
     * Converts an ImportedSpecimenDraft to a Specimen
     */
    private fun draftToSpecimen(draft: ImportedSpecimenDraft, userId: String): Specimen {
        val data = draft.parsedData

        // Parse taxonomy
        val taxonomy = Taxonomy(
            kingdom = data[SpecimenField.KINGDOM],
            phylum = data[SpecimenField.PHYLUM],
            taxonomicClass = data[SpecimenField.CLASS],
            order = data[SpecimenField.ORDER],
            family = data[SpecimenField.FAMILY],
            genus = data[SpecimenField.GENUS],
            species = data[SpecimenField.SPECIES] ?: ""
        )

        // Parse geological time
        val geologicalTime = GeologicalTime(
            era = data[SpecimenField.ERA]?.let {
                com.fossilVault.geological.GeologicalEra.fromSerializedName(it)
            },
            period = data[SpecimenField.PERIOD]?.let {
                com.fossilVault.geological.GeologicalPeriod.fromSerializedName(it)
            },
            epoch = data[SpecimenField.EPOCH]?.let {
                com.fossilVault.geological.GeologicalEpoch.fromSerializedName(it)
            },
            age = data[SpecimenField.AGE]?.let {
                com.fossilVault.geological.GeologicalAge.fromSerializedName(it)
            }
        )

        // Parse element
        val element = data[SpecimenField.ELEMENT]?.let {
            FossilElement.fromSerializedName(it)
        } ?: FossilElement.OTHER

        // Parse dimensions with preprocessing for combined formats
        var width: Double? = null
        var height: Double? = null
        var length: Double? = null
        var sizeUnit = SizeUnit.MM

        // Try to parse individual dimension fields first
        data[SpecimenField.WIDTH]?.let {
            if (it.contains('x', ignoreCase = true) || it.contains('×')) {
                // Combined dimension format (e.g., "2,5x1,8 cm")
                val parsed = parseCombinedDimensions(it)
                width = parsed["width"]?.toDoubleOrNull()
                height = parsed["height"]?.toDoubleOrNull()
                length = parsed["length"]?.toDoubleOrNull()
                parsed["size_unit"]?.let { unit ->
                    sizeUnit = SizeUnit.fromSerializedName(unit)
                }
            } else {
                // Simple numeric value (possibly with comma decimal)
                width = normalizeNumeric(it).toDoubleOrNull()
            }
        }

        // Only parse individual height/length if not already parsed from combined format
        if (height == null) {
            data[SpecimenField.HEIGHT]?.let {
                height = normalizeNumeric(it).toDoubleOrNull()
            }
        }
        if (length == null) {
            data[SpecimenField.LENGTH]?.let {
                length = normalizeNumeric(it).toDoubleOrNull()
            }
        }

        // Parse size unit if provided separately
        data[SpecimenField.SIZE_UNIT]?.let {
            sizeUnit = SizeUnit.fromSerializedName(it)
        }

        // Parse weight with preprocessing for combined formats
        var weight: Double? = null
        var weightUnit = WeightUnit.GR

        data[SpecimenField.WEIGHT]?.let {
            if (it.matches(Regex(".*\\s*(gr|g|kg|grams?|kilograms?)\\s*$", RegexOption.IGNORE_CASE))) {
                // Weight with unit in same field (e.g., "125 gr")
                val parsed = parseWeightWithUnit(it)
                weight = parsed["weight"]?.toDoubleOrNull()
                parsed["weight_unit"]?.let { unit ->
                    weightUnit = WeightUnit.fromSerializedName(unit)
                }
            } else {
                // Simple numeric value (possibly with comma decimal)
                weight = normalizeNumeric(it).toDoubleOrNull()
            }
        }

        // Parse weight unit if provided separately
        data[SpecimenField.WEIGHT_UNIT]?.let {
            weightUnit = WeightUnit.fromSerializedName(it)
        }

        // Parse coordinates
        val latitude = data[SpecimenField.LATITUDE]?.toDoubleOrNull()
        val longitude = data[SpecimenField.LONGITUDE]?.toDoubleOrNull()

        // Parse dates with flexible format support
        val collectionDate = data[SpecimenField.COLLECTION_DATE]?.let {
            parseFlexibleDate(it)
        }

        val acquisitionDate = data[SpecimenField.ACQUISITION_DATE]?.let {
            parseFlexibleDate(it)
        }

        // Parse acquisition info
        val acquisitionMethod = data[SpecimenField.ACQUISITION_METHOD]?.let {
            AcquisitionMethod.fromSerializedName(it)
        }

        val condition = data[SpecimenField.CONDITION]?.let {
            Condition.fromSerializedName(it)
        }

        // Parse financial data
        val pricePaid = data[SpecimenField.PRICE_PAID]?.let {
            // Remove currency symbols
            it.replace(Regex("[^0-9.]"), "").toDoubleOrNull()
        }
        val pricePaidCurrency = data[SpecimenField.PRICE_PAID_CURRENCY]?.let {
            Currency.fromSerializedName(it)
        }

        val estimatedValue = data[SpecimenField.ESTIMATED_VALUE]?.let {
            // Remove currency symbols
            it.replace(Regex("[^0-9.]"), "").toDoubleOrNull()
        }
        val estimatedValueCurrency = data[SpecimenField.ESTIMATED_VALUE_CURRENCY]?.let {
            Currency.fromSerializedName(it)
        }

        // Parse storage
        val storage = if (
            data[SpecimenField.STORAGE_ROOM] != null ||
            data[SpecimenField.STORAGE_CABINET] != null ||
            data[SpecimenField.STORAGE_DRAWER] != null
        ) {
            StorageMethod(
                room = data[SpecimenField.STORAGE_ROOM],
                cabinet = data[SpecimenField.STORAGE_CABINET],
                drawer = data[SpecimenField.STORAGE_DRAWER]
            )
        } else {
            null
        }

        // Parse tags (comma or semicolon separated)
        val tagNames = data[SpecimenField.TAG_NAMES]?.split(Regex("[,;]"))
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        return Specimen(
            id = UUID.randomUUID().toString(),
            userId = userId,
            taxonomy = taxonomy,
            geologicalTime = geologicalTime,
            element = element,
            location = data[SpecimenField.LOCATION],
            country = data[SpecimenField.COUNTRY],
            formation = data[SpecimenField.FORMATION],
            latitude = latitude,
            longitude = longitude,
            width = width,
            height = height,
            length = length,
            unit = sizeUnit,
            weight = weight,
            weightUnit = weightUnit,
            collectionDate = collectionDate,
            acquisitionDate = acquisitionDate,
            acquisitionMethod = acquisitionMethod,
            condition = condition,
            inventoryId = data[SpecimenField.INVENTORY_ID],
            notes = data[SpecimenField.NOTES],
            storage = storage,
            pricePaid = pricePaid,
            pricePaidCurrency = pricePaidCurrency,
            estimatedValue = estimatedValue,
            estimatedValueCurrency = estimatedValueCurrency,
            tagNames = tagNames,
            // Excluded fields as per requirements: isFavorite, imageUrls, isPublic, shareUrl
            isFavorite = false,
            imageUrls = emptyList(),
            isPublic = false,
            shareUrl = null
        )
    }

    /**
     * Creates an import summary from the final progress
     */
    fun createImportSummary(
        fileName: String,
        finalProgress: ImportProgress,
        warnings: List<ImportWarning>,
        durationMs: Long
    ): ImportSummary {
        return ImportSummary(
            importId = UUID.randomUUID().toString(),
            fileName = fileName,
            totalProcessed = finalProgress.totalSpecimens,
            successfullyImported = finalProgress.importedCount,
            failed = finalProgress.failedCount,
            skipped = finalProgress.totalSpecimens - finalProgress.completedCount(),
            warnings = warnings,
            importDurationMs = durationMs
        )
    }
}
