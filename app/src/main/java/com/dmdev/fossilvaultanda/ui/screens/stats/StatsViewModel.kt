package com.dmdev.fossilvaultanda.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.data.models.Specimen
import com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
import com.fossilVault.geological.GeologicalPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

/**
 * UI State for the Stats screen
 */
data class StatsUiState(
    val filteredSpecimens: List<Specimen> = emptyList(),
    val periodDistribution: List<PeriodDistribution> = emptyList(),
    val availableCountries: List<String> = emptyList(),
    val filterState: StatsFilterState = StatsFilterState(),
    val totalCount: Int = 0,
    val mostCommonPeriod: GeologicalPeriod? = null
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: DatabaseManaging
) : ViewModel() {

    private val _filterState = MutableStateFlow(StatsFilterState())
    val filterState: StateFlow<StatsFilterState> = _filterState.asStateFlow()

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Combine specimens flow with filter state to produce filtered results
            combine(
                repository.specimens,
                _filterState
            ) { specimens, filters ->
                processSpecimens(specimens, filters)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    /**
     * Process specimens with current filters and calculate statistics
     */
    private fun processSpecimens(
        allSpecimens: List<Specimen>,
        filters: StatsFilterState
    ): StatsUiState {
        // Extract all unique countries from all specimens (for filter options)
        val availableCountries = allSpecimens
            .mapNotNull { it.country }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        // Apply filters
        val filtered = allSpecimens.filter { specimen ->
            matchesTimeFilter(specimen, filters.timeFilter) &&
                    matchesPeriodFilter(specimen, filters.selectedPeriods) &&
                    matchesCountryFilter(specimen, filters.selectedCountries)
        }

        // Calculate period distribution
        val periods = filtered.mapNotNull { it.geologicalTime.period }
        val distribution = PeriodDistribution.fromPeriods(periods)

        return StatsUiState(
            filteredSpecimens = filtered,
            periodDistribution = distribution,
            availableCountries = availableCountries,
            filterState = filters,
            totalCount = filtered.size,
            mostCommonPeriod = distribution.firstOrNull()?.period
        )
    }

    /**
     * Check if specimen matches the time filter
     */
    private fun matchesTimeFilter(specimen: Specimen, timeFilter: TimeFilter): Boolean {
        val daysToLookBack = timeFilter.daysToLookBack ?: return true

        val acquisitionDate = specimen.acquisitionDate ?: return false
        val cutoffDate = Clock.System.now().minus(daysToLookBack.days)

        return acquisitionDate >= cutoffDate
    }

    /**
     * Check if specimen matches the period filter
     */
    private fun matchesPeriodFilter(specimen: Specimen, selectedPeriods: Set<GeologicalPeriod>): Boolean {
        if (selectedPeriods.isEmpty()) return true
        val period = specimen.geologicalTime.period ?: return false
        return period in selectedPeriods
    }

    /**
     * Check if specimen matches the country filter
     */
    private fun matchesCountryFilter(specimen: Specimen, selectedCountries: Set<String>): Boolean {
        if (selectedCountries.isEmpty()) return true
        val country = specimen.country ?: return false
        return country in selectedCountries
    }

    // Filter update methods

    fun setTimeFilter(timeFilter: TimeFilter) {
        _filterState.update { it.copy(timeFilter = timeFilter) }
    }

    fun setPeriodFilter(periods: Set<GeologicalPeriod>) {
        _filterState.update { it.copy(selectedPeriods = periods) }
    }

    fun setCountryFilter(countries: Set<String>) {
        _filterState.update { it.copy(selectedCountries = countries) }
    }

    fun resetFilters() {
        _filterState.value = StatsFilterState()
    }
}
