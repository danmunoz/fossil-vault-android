package com.dmdev.fossilvaultanda.ui.screens.specimen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.data.models.Specimen
import com.dmdev.fossilvaultanda.data.models.StoredImage
import com.dmdev.fossilvaultanda.data.models.UserProfile
import com.dmdev.fossilvaultanda.data.models.enums.Currency
import com.dmdev.fossilvaultanda.data.models.enums.FossilElement
import com.dmdev.fossilvaultanda.data.models.enums.Period
import com.dmdev.fossilvaultanda.data.models.enums.SizeUnit
import com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddSpecimenViewModel @Inject constructor(
    private val databaseManager: DatabaseManaging
) : ViewModel() {

    // Form state
    private val _formState = MutableStateFlow(SpecimenFormState())
    val formState: StateFlow<SpecimenFormState> = _formState.asStateFlow()

    // User profile for default settings
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    // UI state
    private val _uiState = MutableStateFlow(AddSpecimenUiState())
    val uiState: StateFlow<AddSpecimenUiState> = _uiState.asStateFlow()

    // Validation errors
    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    // Current editing specimen (for edit mode)
    private var editingSpecimen: Specimen? = null

    init {
        // Observe user profile for default settings
        viewModelScope.launch {
            databaseManager.profile.collect { profile ->
                _userProfile.value = profile
                // Set default values from user settings when profile loads
                profile?.let { setDefaultsFromProfile(it) }
            }
        }
    }

    fun initializeForEdit(specimen: Specimen) {
        editingSpecimen = specimen
        _formState.value = SpecimenFormState.fromSpecimen(specimen)
        _uiState.value = _uiState.value.copy(isEditMode = true)
    }

    fun updateSpecies(species: String) {
        _formState.value = _formState.value.copy(species = species)
        clearValidationError("species")
    }

    fun updatePeriod(period: Period) {
        _formState.value = _formState.value.copy(period = period)
        clearValidationError("period")
    }

    fun updateElement(element: FossilElement) {
        _formState.value = _formState.value.copy(element = element)
        clearValidationError("element")
    }

    fun updateLocation(location: String) {
        _formState.value = _formState.value.copy(location = location)
    }

    fun updateFormation(formation: String) {
        _formState.value = _formState.value.copy(formation = formation)
    }

    fun updateCoordinates(latitude: Double?, longitude: Double?) {
        _formState.value = _formState.value.copy(
            latitude = latitude,
            longitude = longitude
        )
    }

    fun updateLocation(location: String, latitude: Double?, longitude: Double?) {
        _formState.value = _formState.value.copy(
            location = location,
            latitude = latitude,
            longitude = longitude
        )
    }

    fun updateDimensions(width: Double?, height: Double?, length: Double?) {
        _formState.value = _formState.value.copy(
            width = width,
            height = height,
            length = length
        )
    }

    fun updateSizeUnit(unit: SizeUnit) {
        _formState.value = _formState.value.copy(unit = unit)
    }

    fun updateCollectionDate(date: Instant?) {
        _formState.value = _formState.value.copy(collectionDate = date)
    }

    fun updateAcquisitionDate(date: Instant?) {
        _formState.value = _formState.value.copy(acquisitionDate = date)
    }

    fun updatePricePaid(price: Double?, currency: Currency?) {
        _formState.value = _formState.value.copy(
            pricePaid = price,
            pricePaidCurrency = currency
        )
    }

    fun updateEstimatedValue(value: Double?, currency: Currency?) {
        _formState.value = _formState.value.copy(
            estimatedValue = value,
            estimatedValueCurrency = currency
        )
    }

    fun updateInventoryId(inventoryId: String) {
        _formState.value = _formState.value.copy(inventoryId = inventoryId)
    }

    fun updateNotes(notes: String) {
        _formState.value = _formState.value.copy(notes = notes)
    }

    fun updateTags(tags: List<String>) {
        _formState.value = _formState.value.copy(tagNames = tags)
    }

    fun updateImages(images: List<StoredImage>) {
        _formState.value = _formState.value.copy(imageUrls = images)
    }

    fun updateIsFavorite(isFavorite: Boolean) {
        _formState.value = _formState.value.copy(isFavorite = isFavorite)
    }

    fun updateIsPublic(isPublic: Boolean) {
        _formState.value = _formState.value.copy(isPublic = isPublic)
    }

    fun saveSpecimen() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, saveError = null)
            
            try {
                val currentProfile = _userProfile.value
                if (currentProfile == null) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        saveError = "User profile not loaded"
                    )
                    return@launch
                }

                // Validate form
                val validationResult = validateForm()
                if (validationResult.isNotEmpty()) {
                    _validationErrors.value = validationResult
                    _uiState.value = _uiState.value.copy(isSaving = false)
                    return@launch
                }

                // Create specimen from form state
                val specimen = createSpecimenFromForm(currentProfile.userId)
                
                // Save to repository
                if (editingSpecimen != null) {
                    databaseManager.update(specimen)
                } else {
                    databaseManager.save(specimen)
                }

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveError = e.message ?: "Failed to save specimen"
                )
            }
        }
    }

    private fun setDefaultsFromProfile(profile: UserProfile) {
        if (_formState.value == SpecimenFormState()) { // Only set defaults for new forms
            _formState.value = _formState.value.copy(
                unit = profile.settings.unit,
                pricePaidCurrency = profile.settings.defaultCurrency,
                estimatedValueCurrency = profile.settings.defaultCurrency
            )
        }
    }

    private fun validateForm(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val state = _formState.value

        if (state.species.isBlank()) {
            errors["species"] = "Species name is required"
        }

        if (state.period == Period.UNKNOWN) {
            errors["period"] = "Please select a geological period"
        }

        if (state.element == FossilElement.OTHER && state.customElement.isBlank()) {
            errors["element"] = "Please specify the fossil element"
        }

        // Validate coordinates if provided
        state.latitude?.let { lat ->
            if (lat < -90 || lat > 90) {
                errors["latitude"] = "Latitude must be between -90 and 90"
            }
        }

        state.longitude?.let { lng ->
            if (lng < -180 || lng > 180) {
                errors["longitude"] = "Longitude must be between -180 and 180"
            }
        }

        // Validate dimensions
        state.width?.let { if (it < 0) errors["width"] = "Width cannot be negative" }
        state.height?.let { if (it < 0) errors["height"] = "Height cannot be negative" }
        state.length?.let { if (it < 0) errors["length"] = "Length cannot be negative" }

        // Validate prices
        state.pricePaid?.let { if (it < 0) errors["pricePaid"] = "Price cannot be negative" }
        state.estimatedValue?.let { if (it < 0) errors["estimatedValue"] = "Value cannot be negative" }

        return errors
    }

    private fun createSpecimenFromForm(userId: String): Specimen {
        val state = _formState.value
        val specimenId = editingSpecimen?.id ?: UUID.randomUUID().toString()

        return Specimen(
            id = specimenId,
            userId = userId,
            species = state.species,
            period = state.period,
            element = if (state.element == FossilElement.OTHER && state.customElement.isNotBlank()) {
                FossilElement.OTHER // Note: In a real app, you might want to handle custom elements differently
            } else {
                state.element
            },
            location = state.location.takeIf { it.isNotBlank() },
            formation = state.formation.takeIf { it.isNotBlank() },
            latitude = state.latitude,
            longitude = state.longitude,
            width = state.width,
            height = state.height,
            length = state.length,
            unit = state.unit,
            collectionDate = state.collectionDate,
            acquisitionDate = state.acquisitionDate,
            inventoryId = state.inventoryId.takeIf { it.isNotBlank() },
            notes = state.notes.takeIf { it.isNotBlank() },
            imageUrls = state.imageUrls,
            isFavorite = state.isFavorite,
            tagNames = state.tagNames,
            isPublic = state.isPublic,
            pricePaid = state.pricePaid,
            pricePaidCurrency = state.pricePaidCurrency,
            estimatedValue = state.estimatedValue,
            estimatedValueCurrency = state.estimatedValueCurrency,
            creationDate = editingSpecimen?.creationDate ?: Clock.System.now()
        )
    }

    private fun clearValidationError(field: String) {
        val currentErrors = _validationErrors.value.toMutableMap()
        currentErrors.remove(field)
        _validationErrors.value = currentErrors
    }

    fun clearSaveState() {
        _uiState.value = _uiState.value.copy(
            saveSuccess = false,
            saveError = null
        )
    }
}

data class SpecimenFormState(
    // Basic Information (Required)
    val species: String = "",
    val period: Period = Period.UNKNOWN,
    val element: FossilElement = FossilElement.OTHER,
    val customElement: String = "", // For "Other" element type
    
    // Location Information
    val location: String = "",
    val formation: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    // Physical Measurements
    val width: Double? = null,
    val height: Double? = null,
    val length: Double? = null,
    val unit: SizeUnit = SizeUnit.MM,
    
    // Dates
    val collectionDate: Instant? = null,
    val acquisitionDate: Instant? = null,
    
    // Valuation
    val pricePaid: Double? = null,
    val pricePaidCurrency: Currency? = null,
    val estimatedValue: Double? = null,
    val estimatedValueCurrency: Currency? = null,
    
    // Additional Metadata
    val inventoryId: String = "",
    val notes: String = "",
    val tagNames: List<String> = emptyList(),
    
    // Media and Organization
    val imageUrls: List<StoredImage> = emptyList(),
    val isFavorite: Boolean = false,
    val isPublic: Boolean = false
) {
    companion object {
        fun fromSpecimen(specimen: Specimen): SpecimenFormState {
            return SpecimenFormState(
                species = specimen.species,
                period = specimen.period,
                element = specimen.element,
                customElement = "", // Would need custom element handling
                location = specimen.location ?: "",
                formation = specimen.formation ?: "",
                latitude = specimen.latitude,
                longitude = specimen.longitude,
                width = specimen.width,
                height = specimen.height,
                length = specimen.length,
                unit = specimen.unit,
                collectionDate = specimen.collectionDate,
                acquisitionDate = specimen.acquisitionDate,
                pricePaid = specimen.pricePaid,
                pricePaidCurrency = specimen.pricePaidCurrency,
                estimatedValue = specimen.estimatedValue,
                estimatedValueCurrency = specimen.estimatedValueCurrency,
                inventoryId = specimen.inventoryId ?: "",
                notes = specimen.notes ?: "",
                tagNames = specimen.tagNames,
                imageUrls = specimen.imageUrls,
                isFavorite = specimen.isFavorite,
                isPublic = specimen.isPublic
            )
        }
    }
}

data class AddSpecimenUiState(
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null
)