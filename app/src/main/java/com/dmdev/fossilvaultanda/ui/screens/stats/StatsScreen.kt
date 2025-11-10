package com.dmdev.fossilvaultanda.ui.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmdev.fossilvaultanda.ui.screens.stats.components.CountryDistributionChart
import com.dmdev.fossilvaultanda.ui.screens.stats.components.CountryFilterBottomSheet
import com.dmdev.fossilvaultanda.ui.screens.stats.components.FilterBar
import com.dmdev.fossilvaultanda.ui.screens.stats.components.PeriodDistributionChart
import com.dmdev.fossilvaultanda.ui.screens.stats.components.PeriodFilterBottomSheet
import com.dmdev.fossilvaultanda.ui.screens.stats.components.TimeFilterBottomSheet
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import kotlinx.coroutines.launch

/**
 * Stats screen displaying collection statistics and charts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    // Bottom sheet states
    var showTimeFilter by remember { mutableStateOf(false) }
    var showPeriodFilter by remember { mutableStateOf(false) }
    var showCountryFilter by remember { mutableStateOf(false) }

    val timeFilterSheetState = rememberModalBottomSheetState()
    val periodFilterSheetState = rememberModalBottomSheetState()
    val countryFilterSheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistics",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(FossilVaultSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.md)
        ) {
            // Filter bar
            FilterBar(
                filterState = filterState,
                onTimeFilterClick = { showTimeFilter = true },
                onPeriodFilterClick = { showPeriodFilter = true },
                onCountryFilterClick = { showCountryFilter = true },
                onResetFilters = { viewModel.resetFilters() }
            )

            // Period distribution chart
            PeriodDistributionChart(
                distribution = uiState.periodDistribution,
                totalCount = uiState.totalCount,
                mostCommonPeriod = uiState.mostCommonPeriod
            )

            // Country distribution chart
            CountryDistributionChart(
                distribution = uiState.countryDistribution,
                totalCount = uiState.countryDistribution.sumOf { it.count },
                topCountry = uiState.topCountry
            )
        }
    }

    // Time filter bottom sheet
    if (showTimeFilter) {
        TimeFilterBottomSheet(
            currentFilter = filterState.timeFilter,
            onDismiss = {
                scope.launch {
                    timeFilterSheetState.hide()
                }.invokeOnCompletion {
                    if (!timeFilterSheetState.isVisible) {
                        showTimeFilter = false
                    }
                }
            },
            onFilterSelected = { filter ->
                viewModel.setTimeFilter(filter)
            },
            sheetState = timeFilterSheetState
        )
    }

    // Period filter bottom sheet
    if (showPeriodFilter) {
        PeriodFilterBottomSheet(
            currentSelection = filterState.selectedPeriods,
            onDismiss = {
                scope.launch {
                    periodFilterSheetState.hide()
                }.invokeOnCompletion {
                    if (!periodFilterSheetState.isVisible) {
                        showPeriodFilter = false
                    }
                }
            },
            onApply = { periods ->
                viewModel.setPeriodFilter(periods)
            },
            sheetState = periodFilterSheetState
        )
    }

    // Country filter bottom sheet
    if (showCountryFilter) {
        CountryFilterBottomSheet(
            availableCountries = uiState.availableCountries,
            currentSelection = filterState.selectedCountries,
            onDismiss = {
                scope.launch {
                    countryFilterSheetState.hide()
                }.invokeOnCompletion {
                    if (!countryFilterSheetState.isVisible) {
                        showCountryFilter = false
                    }
                }
            },
            onApply = { countries ->
                viewModel.setCountryFilter(countries)
            },
            sheetState = countryFilterSheetState
        )
    }
}
