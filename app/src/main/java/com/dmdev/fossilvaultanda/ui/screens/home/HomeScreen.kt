package com.dmdev.fossilvaultanda.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationManager
import com.dmdev.fossilvaultanda.data.models.enums.DisplayMode
import com.dmdev.fossilvaultanda.ui.screens.home.components.FilterSection
import com.dmdev.fossilvaultanda.ui.screens.home.components.HomeTopBar
import com.dmdev.fossilvaultanda.ui.screens.home.components.SearchAndFilterSection
import com.dmdev.fossilvaultanda.ui.screens.home.components.SpecimenGrid
import com.dmdev.fossilvaultanda.ui.screens.home.components.SpecimenList
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun HomeScreen(
    authenticationManager: AuthenticationManager? = null,
    onNavigateToSpecimen: (String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onAddSpecimen: () -> Unit = {},
    onNavigateToLimitReached: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val filteredSpecimens by viewModel.filteredSpecimens.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val displayMode by viewModel.displayMode.collectAsState()
    val hasAnySpecimens by viewModel.hasAnySpecimens.collectAsState()
    val isFiltered by viewModel.isFiltered.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToStats = onNavigateToStats
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        if (viewModel.canAddSpecimen()) {
                            onAddSpecimen()
                        } else {
                            onNavigateToLimitReached()
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add specimen"
                )
            }
        }
    ) { paddingValues ->
        val headerContent = @Composable {
            Column {
                // Search and period filter section
                SearchAndFilterSection(
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    selectedPeriod = selectedPeriod,
                    onPeriodChange = viewModel::updateSelectedPeriod,
                    resultCount = filteredSpecimens.size
                )
                
                // Sort and display mode controls
                FilterSection(
                    sortOption = sortOption,
                    onSortChange = viewModel::updateSortOption,
                    displayMode = displayMode,
                    onDisplayModeChange = viewModel::updateDisplayMode
                )
            }
        }
        
        // Specimen display based on mode
        when (displayMode) {
            DisplayMode.GRID -> {
                SpecimenGrid(
                    specimens = filteredSpecimens,
                    onSpecimenClick = { specimen -> onNavigateToSpecimen(specimen.id) },
                    onSpecimenAction = { specimen ->
                        // TODO: Show bottom sheet or context menu
                    },
                    headerContent = headerContent,
                    hasAnySpecimens = hasAnySpecimens,
                    isFiltered = isFiltered,
                    onAddSpecimen = onAddSpecimen,
                    onClearFilters = viewModel::clearFilters,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            DisplayMode.LIST -> {
                SpecimenList(
                    specimens = filteredSpecimens,
                    onSpecimenClick = { specimen -> onNavigateToSpecimen(specimen.id) },
                    onSpecimenAction = { specimen ->
                        // TODO: Show bottom sheet or context menu
                    },
                    headerContent = headerContent,
                    hasAnySpecimens = hasAnySpecimens,
                    isFiltered = isFiltered,
                    onAddSpecimen = onAddSpecimen,
                    onClearFilters = viewModel::clearFilters,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    FossilVaultTheme {
        HomeScreen()
    }
}