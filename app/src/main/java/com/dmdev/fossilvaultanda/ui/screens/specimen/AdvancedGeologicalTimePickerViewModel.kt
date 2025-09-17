package com.dmdev.fossilvaultanda.ui.screens.specimen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fossilVault.geological.GeologicalTime
import com.fossilVault.geological.GeologicalEra
import com.fossilVault.geological.GeologicalPeriod
import com.fossilVault.geological.GeologicalEpoch
import com.fossilVault.geological.GeologicalAge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GeologicalLevel {
    ERA, PERIOD, EPOCH, AGE
}

data class GeologicalTimePickerUiState(
    val selectedEra: GeologicalEra? = null,
    val selectedPeriod: GeologicalPeriod? = null,
    val selectedEpoch: GeologicalEpoch? = null,
    val selectedAge: GeologicalAge? = null,
    val expandedSections: Set<GeologicalLevel> = emptySet(),
    val availablePeriods: List<GeologicalPeriod> = GeologicalPeriod.values().toList(),
    val availableEpochs: List<GeologicalEpoch> = emptyList(),
    val availableAges: List<GeologicalAge> = emptyList(),
    val isLoading: Boolean = false
) {
    val currentGeologicalTime: GeologicalTime
        get() = GeologicalTime(
            era = selectedEra,
            period = selectedPeriod,
            epoch = selectedEpoch,
            age = selectedAge
        )

    val hasValidSelection: Boolean
        get() = selectedPeriod != null // At minimum, we need a period
}

@HiltViewModel
class AdvancedGeologicalTimePickerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(GeologicalTimePickerUiState())
    val uiState: StateFlow<GeologicalTimePickerUiState> = _uiState.asStateFlow()

    init {
        // Initialize with all eras expanded by default
        toggleSection(GeologicalLevel.ERA)
    }

    fun initializeWithGeologicalTime(geologicalTime: GeologicalTime?) {
        viewModelScope.launch {
            geologicalTime?.let { gt ->
                var newState = _uiState.value.copy(
                    selectedEra = gt.era,
                    selectedPeriod = gt.period,
                    selectedEpoch = gt.epoch,
                    selectedAge = gt.age
                )

                // Update available items based on selections
                newState = updateAvailableItems(newState)

                // Expand relevant sections if there are selections
                val expandedSections = mutableSetOf<GeologicalLevel>()
                if (gt.era != null) expandedSections.add(GeologicalLevel.ERA)
                if (gt.period != null) expandedSections.add(GeologicalLevel.PERIOD)
                if (gt.epoch != null) expandedSections.add(GeologicalLevel.EPOCH)
                if (gt.age != null) expandedSections.add(GeologicalLevel.AGE)

                newState = newState.copy(expandedSections = expandedSections)

                _uiState.value = newState
            }
        }
    }

    fun selectEra(era: GeologicalEra?) {
        viewModelScope.launch {
            var newState = _uiState.value.copy(
                selectedEra = era,
                selectedPeriod = null, // Clear lower levels
                selectedEpoch = null,
                selectedAge = null
            )

            newState = updateAvailableItems(newState)

            // Auto-expand period section when era is selected
            if (era != null) {
                newState = newState.copy(
                    expandedSections = newState.expandedSections + GeologicalLevel.PERIOD
                )
            }

            _uiState.value = newState
        }
    }

    fun selectPeriod(period: GeologicalPeriod?) {
        viewModelScope.launch {
            var newState = _uiState.value.copy(
                selectedPeriod = period,
                selectedEpoch = null, // Clear lower levels
                selectedAge = null
            )

            // Auto-select era if not already selected
            if (period != null && newState.selectedEra == null) {
                newState = newState.copy(selectedEra = period.era)
            }

            newState = updateAvailableItems(newState)

            // Auto-expand epoch section when period is selected
            if (period != null && newState.availableEpochs.isNotEmpty()) {
                newState = newState.copy(
                    expandedSections = newState.expandedSections + GeologicalLevel.EPOCH
                )
            }

            _uiState.value = newState
        }
    }

    fun selectEpoch(epoch: GeologicalEpoch?) {
        viewModelScope.launch {
            var newState = _uiState.value.copy(
                selectedEpoch = epoch,
                selectedAge = null // Clear lower level
            )

            // Auto-select parent hierarchy if not already selected
            if (epoch != null) {
                val parentPeriod = epoch.period
                val parentEra = parentPeriod.era

                if (newState.selectedPeriod == null) {
                    newState = newState.copy(selectedPeriod = parentPeriod)
                }
                if (newState.selectedEra == null) {
                    newState = newState.copy(selectedEra = parentEra)
                }
            }

            newState = updateAvailableItems(newState)

            // Auto-expand age section when epoch is selected
            if (epoch != null && newState.availableAges.isNotEmpty()) {
                newState = newState.copy(
                    expandedSections = newState.expandedSections + GeologicalLevel.AGE
                )
            }

            _uiState.value = newState
        }
    }

    fun selectAge(age: GeologicalAge?) {
        viewModelScope.launch {
            var newState = _uiState.value.copy(selectedAge = age)

            // Auto-select parent hierarchy if not already selected
            if (age != null) {
                val parentEpoch = age.getEpoch()
                val parentPeriod = parentEpoch.period
                val parentEra = parentPeriod.era

                if (newState.selectedEpoch == null) {
                    newState = newState.copy(selectedEpoch = parentEpoch)
                }
                if (newState.selectedPeriod == null) {
                    newState = newState.copy(selectedPeriod = parentPeriod)
                }
                if (newState.selectedEra == null) {
                    newState = newState.copy(selectedEra = parentEra)
                }
            }

            newState = updateAvailableItems(newState)
            _uiState.value = newState
        }
    }

    fun toggleSection(level: GeologicalLevel) {
        val currentExpanded = _uiState.value.expandedSections
        val newExpanded = if (level in currentExpanded) {
            currentExpanded - level
        } else {
            currentExpanded + level
        }

        _uiState.value = _uiState.value.copy(expandedSections = newExpanded)
    }

    fun clearSelection() {
        _uiState.value = GeologicalTimePickerUiState(
            expandedSections = setOf(GeologicalLevel.ERA)
        )
    }

    private fun updateAvailableItems(state: GeologicalTimePickerUiState): GeologicalTimePickerUiState {
        // Filter periods based on selected era
        val availablePeriods = if (state.selectedEra != null) {
            state.selectedEra.getPeriods()
        } else {
            GeologicalPeriod.values().toList()
        }

        // Filter epochs based on selected period
        val availableEpochs = if (state.selectedPeriod != null) {
            state.selectedPeriod.epochs
        } else {
            emptyList()
        }

        // Filter ages based on selected epoch
        val availableAges = if (state.selectedEpoch != null) {
            state.selectedEpoch.ages
        } else {
            emptyList()
        }

        return state.copy(
            availablePeriods = availablePeriods,
            availableEpochs = availableEpochs,
            availableAges = availableAges
        )
    }

    fun getCurrentSelection(): GeologicalTime {
        return _uiState.value.currentGeologicalTime
    }
}