package com.dmdev.fossilvaultanda.ui.screens.csvimport

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.data.csv.models.*
import com.dmdev.fossilvaultanda.data.csv.repository.CsvImportRepository
import com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing CSV import flow
 * Handles file selection → field mapping → preview → import
 */
@HiltViewModel
class ImportViewModel @Inject constructor(
    private val csvImportRepository: CsvImportRepository,
    private val databaseManager: DatabaseManaging
) : ViewModel() {

    // Current import state
    private val _uiState = MutableStateFlow<ImportUiState>(ImportUiState.FileSelection)
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()

    // CSV parsing result
    private var csvParsingResult: CsvParsingResult? = null

    // Field mapping configuration
    private val _mappingConfiguration = MutableStateFlow<CsvMappingConfiguration?>(null)
    val mappingConfiguration: StateFlow<CsvMappingConfiguration?> = _mappingConfiguration.asStateFlow()

    // Specimen drafts for preview
    private val _specimenDrafts = MutableStateFlow<List<ImportedSpecimenDraft>>(emptyList())
    val specimenDrafts: StateFlow<List<ImportedSpecimenDraft>> = _specimenDrafts.asStateFlow()

    // Import progress
    private val _importProgress = MutableStateFlow<ImportProgress?>(null)
    val importProgress: StateFlow<ImportProgress?> = _importProgress.asStateFlow()

    // Import summary
    private val _importSummary = MutableStateFlow<ImportSummary?>(null)
    val importSummary: StateFlow<ImportSummary?> = _importSummary.asStateFlow()

    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // User profile for getting user ID
    private val _userProfile = MutableStateFlow<com.dmdev.fossilvaultanda.data.models.UserProfile?>(null)

    init {
        // Observe user profile
        viewModelScope.launch {
            databaseManager.profile.collect { profile ->
                _userProfile.value = profile
            }
        }
    }

    /**
     * Step 1: Parse selected CSV file
     */
    fun parseCSVFile(uri: Uri, fileName: String) {
        viewModelScope.launch {
            _uiState.value = ImportUiState.Loading("Parsing CSV file...")

            csvImportRepository.parseCSV(uri, fileName).fold(
                onSuccess = { result ->
                    csvParsingResult = result
                    // Generate automatic field mappings
                    val mappings = csvImportRepository.generateFieldMappings(result)
                    _mappingConfiguration.value = mappings
                    _uiState.value = ImportUiState.FieldMapping
                },
                onFailure = { error ->
                    _errorMessage.value = "Failed to parse CSV: ${error.message}"
                    _uiState.value = ImportUiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    /**
     * Step 2: Update field mapping for a specific field
     */
    fun updateFieldMapping(field: SpecimenField, selectedColumns: List<String>) {
        val currentConfig = _mappingConfiguration.value ?: return
        val updatedConfig = csvImportRepository.updateFieldMapping(
            currentConfig,
            field,
            selectedColumns
        )
        _mappingConfiguration.value = updatedConfig
    }

    /**
     * Step 3: Proceed to preview after field mapping is complete
     */
    fun proceedToPreview() {
        val config = _mappingConfiguration.value ?: return

        if (!config.hasAllRequiredFieldsMapped()) {
            _errorMessage.value = "Please map all required fields before proceeding"
            return
        }

        _uiState.value = ImportUiState.Loading("Validating specimens...")

        viewModelScope.launch {
            try {
                val drafts = csvImportRepository.validateAndCreateDrafts(config)
                _specimenDrafts.value = drafts
                _uiState.value = ImportUiState.Preview
            } catch (e: Exception) {
                _errorMessage.value = "Failed to validate specimens: ${e.message}"
                _uiState.value = ImportUiState.Error(e.message ?: "Validation failed")
            }
        }
    }

    /**
     * Step 4: Toggle selection of a specimen draft
     */
    fun toggleSpecimenSelection(draftId: String) {
        _specimenDrafts.value = _specimenDrafts.value.map { draft ->
            if (draft.id == draftId && draft.canBeImported()) {
                draft.copy(isSelected = !draft.isSelected)
            } else {
                draft
            }
        }
    }

    /**
     * Select/deselect all specimens
     */
    fun toggleSelectAll(selectAll: Boolean) {
        _specimenDrafts.value = _specimenDrafts.value.map { draft ->
            if (draft.canBeImported()) {
                draft.copy(isSelected = selectAll)
            } else {
                draft
            }
        }
    }

    /**
     * Step 5: Execute import of selected specimens
     */
    fun executeImport() {
        viewModelScope.launch {
            // Get user ID from profile
            val userId = _userProfile.value?.userId
            if (userId == null) {
                _errorMessage.value = "User not authenticated"
                return@launch
            }

            val drafts = _specimenDrafts.value
            val selectedCount = drafts.count { it.canBeImported() && it.isSelected }

            if (selectedCount == 0) {
                _errorMessage.value = "No specimens selected for import"
                return@launch
            }

            _uiState.value = ImportUiState.Importing

            val startTime = System.currentTimeMillis()
            val warnings = mutableListOf<ImportWarning>()

            csvImportRepository.importSpecimens(drafts, userId).collect { progress ->
                _importProgress.value = progress

                if (progress.isCompleted) {
                    val duration = System.currentTimeMillis() - startTime
                    val summary = csvImportRepository.createImportSummary(
                        fileName = csvParsingResult?.fileName ?: "unknown.csv",
                        finalProgress = progress,
                        warnings = warnings,
                        durationMs = duration
                    )
                    _importSummary.value = summary
                    _uiState.value = ImportUiState.Summary
                }
            }
        }
    }

    /**
     * Resets the import flow to start over
     */
    fun resetImport() {
        _uiState.value = ImportUiState.FileSelection
        csvParsingResult = null
        _mappingConfiguration.value = null
        _specimenDrafts.value = emptyList()
        _importProgress.value = null
        _importSummary.value = null
        _errorMessage.value = null
    }

    /**
     * Go back to the previous step
     */
    fun goBack() {
        _uiState.value = when (_uiState.value) {
            is ImportUiState.FieldMapping -> ImportUiState.FileSelection
            is ImportUiState.Preview -> ImportUiState.FieldMapping
            is ImportUiState.Importing -> ImportUiState.Preview
            else -> _uiState.value
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Get statistics for preview screen
     */
    fun getPreviewStats(): PreviewStats {
        val drafts = _specimenDrafts.value
        return PreviewStats(
            total = drafts.size,
            selected = drafts.count { it.isSelected && it.canBeImported() },
            withErrors = drafts.count { it.hasErrors() },
            withWarnings = drafts.count { it.hasWarnings() && !it.hasErrors() }
        )
    }
}

/**
 * UI state for the import flow
 */
sealed class ImportUiState {
    object FileSelection : ImportUiState()
    data class Loading(val message: String) : ImportUiState()
    object FieldMapping : ImportUiState()
    object Preview : ImportUiState()
    object Importing : ImportUiState()
    object Summary : ImportUiState()
    data class Error(val message: String) : ImportUiState()
}

/**
 * Statistics for preview screen
 */
data class PreviewStats(
    val total: Int,
    val selected: Int,
    val withErrors: Int,
    val withWarnings: Int
)
