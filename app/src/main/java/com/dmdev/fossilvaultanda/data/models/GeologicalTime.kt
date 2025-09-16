package com.fossilVault.geological

data class GeologicalTime(
    val era: GeologicalEra? = null,
    val period: GeologicalPeriod? = null,
    val epoch: GeologicalEpoch? = null,
    val age: GeologicalAge? = null
) {
    val isValid: Boolean
        get() = period != null

    val isEmpty: Boolean
        get() = era == null && period == null && epoch == null && age == null

    val displayString: String
        get() {
            val components = mutableListOf<String>()
            
            age?.let { components.add(it.displayName) }
            epoch?.let { components.add(it.displayName) }
            period?.let { components.add(it.displayName) }
            era?.let { components.add(it.displayName) }
            
            return if (components.isEmpty()) "Unknown" else components.joinToString(", ")
        }

    val mostSpecificLevel: GeologicalTimeLevel?
        get() = when {
            age != null -> GeologicalTimeLevel.AGE
            epoch != null -> GeologicalTimeLevel.EPOCH
            period != null -> GeologicalTimeLevel.PERIOD
            era != null -> GeologicalTimeLevel.ERA
            else -> null
        }

    val timeRange: String?
        get() = when {
            age != null -> age.timeRange
            epoch != null -> epoch.timeRange
            period != null -> period.timeRange
            era != null -> era.timeRange
            else -> null
        }
}

enum class GeologicalTimeLevel {
    ERA,
    PERIOD,
    EPOCH,
    AGE
}
