package com.dmdev.fossilvaultanda.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    onEditSpecimen: (String) -> Unit = {},
    onDuplicateSpecimen: (String) -> Unit = {},
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

    // State for dropdown menu and delete confirmation
    var menuExpandedSpecimenId by remember { mutableStateOf<String?>(null) }
    var specimenToDelete by remember { mutableStateOf<String?>(null) }
    
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
                        menuExpandedSpecimenId = specimen.id
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
                        menuExpandedSpecimenId = specimen.id
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

    // Dropdown menu for specimen actions
    if (menuExpandedSpecimenId != null) {
        SpecimenActionMenu(
            expanded = true,
            onDismiss = { menuExpandedSpecimenId = null },
            onEditClick = {
                onEditSpecimen(menuExpandedSpecimenId!!)
                menuExpandedSpecimenId = null
            },
            onDuplicateClick = {
                val specimenId = menuExpandedSpecimenId!!
                menuExpandedSpecimenId = null
                coroutineScope.launch {
                    if (viewModel.canAddSpecimen()) {
                        onDuplicateSpecimen(specimenId)
                    } else {
                        onNavigateToLimitReached()
                    }
                }
            },
            onDeleteClick = {
                specimenToDelete = menuExpandedSpecimenId
                menuExpandedSpecimenId = null
            }
        )
    }

    // Delete confirmation dialog
    if (specimenToDelete != null) {
        AlertDialog(
            onDismissRequest = { specimenToDelete = null },
            title = { Text("Delete Specimen") },
            text = { Text("Delete this specimen? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSpecimen(specimenToDelete!!)
                        specimenToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { specimenToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SpecimenActionMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("Edit specimen") },
            leadingIcon = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null
                )
            },
            onClick = onEditClick
        )

        DropdownMenuItem(
            text = { Text("Duplicate specimen") },
            leadingIcon = {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = null
                )
            },
            onClick = onDuplicateClick
        )

        DropdownMenuItem(
            text = { Text("Delete specimen") },
            leadingIcon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null
                )
            },
            onClick = onDeleteClick
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    FossilVaultTheme {
        HomeScreen()
    }
}