package com.dmdev.fossilvaultanda.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.data.models.Specimen
import com.dmdev.fossilvaultanda.data.models.enums.Currency
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
    val countryDistribution: List<CountryDistribution> = emptyList(),
    val availableCountries: List<String> = emptyList(),
    val filterState: StatsFilterState = StatsFilterState(),
    val totalCount: Int = 0,
    val mostCommonPeriod: GeologicalPeriod? = null,
    val topCountry: String? = null,
    val highlights: CollectionHighlights? = null
)

/**
 * Collection highlights showing interesting statistics about the collection
 */
data class CollectionHighlights(
    val firstAcquired: HighlightItem? = null,
    val mostRecentAcquisition: HighlightItem? = null,
    val mostCommonPeriod: PeriodHighlight? = null,
    val topLocation: LocationHighlight? = null,
    val mostValuable: ValueHighlight? = null,
    val spentVsValue: Map<Currency, SpentVsValueData> = emptyMap(),
    val longestGap: Int? = null
)

/**
 * Highlight item with species and date
 */
data class HighlightItem(
    val speciesName: String,
    val date: Instant
)

/**
 * Period highlight with period name and count
 */
data class PeriodHighlight(
    val periodName: String,
    val count: Int
)

/**
 * Location highlight with country and count
 */
data class LocationHighlight(
    val country: String,
    val count: Int
)

/**
 * Value highlight with species, value, and currency
 */
data class ValueHighlight(
    val speciesName: String,
    val value: Double,
    val currency: Currency
)

/**
 * Spent vs value data for a currency
 */
data class SpentVsValueData(
    val spent: Double,
    val estimated: Double,
    val currency: Currency
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
            // Combine specimens flow with filter state and profile to produce filtered results
            combine(
                repository.specimens,
                _filterState,
                repository.profile
            ) { specimens, filters, profile ->
                val defaultCurrency = profile?.settings?.defaultCurrency ?: Currency.USD
                processSpecimens(specimens, filters, defaultCurrency)
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
        filters: StatsFilterState,
        userCurrency: Currency
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
        val periodDistribution = PeriodDistribution.fromPeriods(periods)

        // Calculate country distribution
        val countries = filtered.mapNotNull { it.country }.filter { it.isNotBlank() }
        val countryDistribution = CountryDistribution.fromCountries(countries)

        // Calculate highlights
        val highlights = calculateHighlights(filtered, userCurrency)

        return StatsUiState(
            filteredSpecimens = filtered,
            periodDistribution = periodDistribution,
            countryDistribution = countryDistribution,
            availableCountries = availableCountries,
            filterState = filters,
            totalCount = filtered.size,
            mostCommonPeriod = periodDistribution.firstOrNull()?.period,
            topCountry = countryDistribution.firstOrNull()?.country,
            highlights = highlights
        )
    }

    /**
     * Calculate collection highlights from filtered specimens
     */
    private fun calculateHighlights(
        specimens: List<Specimen>,
        userCurrency: Currency
    ): CollectionHighlights? {
        // Need at least one specimen to generate highlights
        if (specimens.isEmpty()) return null

        // First acquired (earliest acquisition date)
        val firstAcquired = specimens
            .filter { it.acquisitionDate != null }
            .minByOrNull { it.acquisitionDate!! }
            ?.let { HighlightItem(it.taxonomy.getDisplayName(), it.acquisitionDate!!) }

        // Most recent acquisition (latest acquisition date)
        val mostRecentAcquisition = specimens
            .filter { it.acquisitionDate != null }
            .maxByOrNull { it.acquisitionDate!! }
            ?.let { HighlightItem(it.taxonomy.getDisplayName(), it.acquisitionDate!!) }

        // Most common period
        val periodCounts = specimens
            .mapNotNull { it.geologicalTime.period }
            .groupBy { it }
            .mapValues { it.value.size }
            .maxByOrNull { it.value }

        val mostCommonPeriod = periodCounts?.let {
            PeriodHighlight(it.key.displayName, it.value)
        }

        // Location with most finds (normalize country names)
        val locationCounts = specimens
            .filter { !it.country.isNullOrBlank() }
            .groupBy { it.country!!.trim().lowercase() }
            .mapValues { it.value.size }
            .maxByOrNull { it.value }

        val topLocation = locationCounts?.let { (normalizedCountry, count) ->
            // Get original country name from first specimen
            val displayCountry = specimens
                .first { it.country?.trim()?.lowercase() == normalizedCountry }
                .country!!
            LocationHighlight(displayCountry, count)
        }

        // Most valuable specimen (in user's preferred currency only)
        val mostValuable = specimens
            .filter { it.estimatedValue != null && it.estimatedValueCurrency == userCurrency }
            .maxByOrNull { it.estimatedValue!! }
            ?.let {
                ValueHighlight(
                    speciesName = it.taxonomy.getDisplayName(),
                    value = it.estimatedValue!!,
                    currency = it.estimatedValueCurrency!!
                )
            }

        // Total spent vs value (grouped by currency)
        val spentVsValue = specimens
            .groupBy { specimen ->
                // Group by the currency used (prefer estimatedValue currency, fall back to pricePaid currency)
                specimen.estimatedValueCurrency ?: specimen.pricePaidCurrency
            }
            .filterKeys { it != null }
            .mapKeys { it.key!! }
            .mapValues { (currency, specimensInCurrency) ->
                val spent = specimensInCurrency
                    .filter { it.pricePaid != null && it.pricePaidCurrency == currency }
                    .sumOf { it.pricePaid!! }

                val estimated = specimensInCurrency
                    .filter { it.estimatedValue != null && it.estimatedValueCurrency == currency }
                    .sumOf { it.estimatedValue!! }

                SpentVsValueData(spent, estimated, currency)
            }
            .filter { it.value.spent > 0.0 || it.value.estimated > 0.0 } // Only include currencies with actual values

        // Longest gap between acquisitions
        val sortedDates = specimens
            .mapNotNull { it.acquisitionDate }
            .sorted()

        val longestGap = if (sortedDates.size >= 2) {
            sortedDates.zipWithNext()
                .maxOfOrNull { (first, second) ->
                    (second - first).inWholeDays.toInt()
                }
        } else {
            null
        }

        return CollectionHighlights(
            firstAcquired = firstAcquired,
            mostRecentAcquisition = mostRecentAcquisition,
            mostCommonPeriod = mostCommonPeriod,
            topLocation = topLocation,
            mostValuable = mostValuable,
            spentVsValue = spentVsValue,
            longestGap = longestGap
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
