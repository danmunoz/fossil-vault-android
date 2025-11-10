package com.dmdev.fossilvaultanda.ui.screens.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.ui.screens.stats.StatsFilterState
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing

/**
 * Filter bar component displaying Time, Period, and Country filter buttons
 */
@Composable
fun FilterBar(
    filterState: StatsFilterState,
    onTimeFilterClick: () -> Unit,
    onPeriodFilterClick: () -> Unit,
    onCountryFilterClick: () -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(FossilVaultSpacing.md)
        ) {
            // Header row with "Filters" title and "Reset Filters" button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (filterState.hasActiveFilters) {
                    TextButton(onClick = onResetFilters) {
                        Text("Reset Filters")
                    }
                }
            }

            // Filter chips - first row with time and period filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = FossilVaultSpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm)
            ) {
                FilterButton(
                    icon = Icons.Default.CalendarMonth,
                    label = filterState.timeFilterDisplayText,
                    isSelected = filterState.timeFilter != com.dmdev.fossilvaultanda.ui.screens.stats.TimeFilter.ALL_TIME,
                    onClick = onTimeFilterClick
                )

                FilterButton(
                    icon = Icons.Default.Schedule,
                    label = filterState.periodFilterDisplayText,
                    isSelected = filterState.selectedPeriods.isNotEmpty(),
                    onClick = onPeriodFilterClick
                )
            }

            // Filter chips - second row with country filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = FossilVaultSpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm)
            ) {
                FilterButton(
                    icon = Icons.Default.Public,
                    label = filterState.countryFilterDisplayText,
                    isSelected = filterState.selectedCountries.isNotEmpty(),
                    onClick = onCountryFilterClick
                )
            }
        }
    }
}

/**
 * Individual filter chip button
 */
@Composable
private fun FilterButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
