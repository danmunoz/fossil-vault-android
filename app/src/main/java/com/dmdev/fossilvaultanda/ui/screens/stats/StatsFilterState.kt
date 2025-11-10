package com.dmdev.fossilvaultanda.ui.screens.stats

import com.fossilVault.geological.GeologicalPeriod

/**
 * Time filter options for statistics
 */
enum class TimeFilter(val displayName: String) {
    ALL_TIME("All Time"),
    LAST_YEAR("Last Year"),
    LAST_6_MONTHS("Last 6 Months"),
    LAST_3_MONTHS("Last 3 Months");

    /**
     * Returns the number of days to look back, or null for all time
     */
    val daysToLookBack: Int?
        get() = when (this) {
            ALL_TIME -> null
            LAST_YEAR -> 365
            LAST_6_MONTHS -> 180
            LAST_3_MONTHS -> 90
        }
}

/**
 * Filter state for statistics screen
 */
data class StatsFilterState(
    val timeFilter: TimeFilter = TimeFilter.ALL_TIME,
    val selectedPeriods: Set<GeologicalPeriod> = emptySet(),
    val selectedCountries: Set<String> = emptySet()
) {
    /**
     * Returns true if any filter is active (not in default "all" state)
     */
    val hasActiveFilters: Boolean
        get() = timeFilter != TimeFilter.ALL_TIME ||
                selectedPeriods.isNotEmpty() ||
                selectedCountries.isNotEmpty()

    /**
     * Returns a display string for the time filter button
     */
    val timeFilterDisplayText: String
        get() = timeFilter.displayName

    /**
     * Returns a display string for the period filter button
     */
    val periodFilterDisplayText: String
        get() = when {
            selectedPeriods.isEmpty() -> "All Periods"
            selectedPeriods.size == 1 -> selectedPeriods.first().displayName
            else -> "${selectedPeriods.size} Periods"
        }

    /**
     * Returns a display string for the country filter button
     */
    val countryFilterDisplayText: String
        get() = when {
            selectedCountries.isEmpty() -> "All Regions"
            selectedCountries.size == 1 -> selectedCountries.first()
            else -> "${selectedCountries.size} Regions"
        }

    /**
     * Resets all filters to default state
     */
    fun reset(): StatsFilterState = StatsFilterState()
}

/**
 * Data class representing the distribution of specimens across geological periods
 */
data class PeriodDistribution(
    val period: GeologicalPeriod,
    val count: Int,
    val percentage: Float
) {
    companion object {
        /**
         * Calculates period distribution from a list of periods
         */
        fun fromPeriods(periods: List<GeologicalPeriod>): List<PeriodDistribution> {
            if (periods.isEmpty()) return emptyList()

            val total = periods.size
            val grouped = periods.groupingBy { it }.eachCount()

            return grouped.map { (period, count) ->
                PeriodDistribution(
                    period = period,
                    count = count,
                    percentage = (count.toFloat() / total.toFloat()) * 100f
                )
            }.sortedByDescending { it.count }
        }
    }
}

/**
 * Data class representing the distribution of specimens across countries
 */
data class CountryDistribution(
    val country: String,
    val count: Int,
    val percentage: Float
) {
    companion object {
        /**
         * Calculates country distribution from a list of countries
         */
        fun fromCountries(countries: List<String>): List<CountryDistribution> {
            if (countries.isEmpty()) return emptyList()

            val total = countries.size
            val grouped = countries.groupingBy { it }.eachCount()

            return grouped.map { (country, count) ->
                CountryDistribution(
                    country = country,
                    count = count,
                    percentage = (count.toFloat() / total.toFloat()) * 100f
                )
            }.sortedByDescending { it.count }
        }
    }
}
