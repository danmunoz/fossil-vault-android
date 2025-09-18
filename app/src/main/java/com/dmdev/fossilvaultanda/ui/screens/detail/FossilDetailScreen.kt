package com.dmdev.fossilvaultanda.ui.screens.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmdev.fossilvaultanda.ui.screens.detail.components.AcquisitionCard
import com.dmdev.fossilvaultanda.ui.screens.detail.components.ImageGallery
import com.dmdev.fossilvaultanda.ui.screens.detail.components.InventoryCard
import com.dmdev.fossilvaultanda.ui.screens.detail.components.LocationDiscoveryCard
import com.dmdev.fossilvaultanda.ui.screens.detail.components.PhysicalPropertiesCard
import com.dmdev.fossilvaultanda.ui.screens.detail.components.SpeciesClassificationCard
import com.dmdev.fossilvaultanda.ui.screens.detail.components.ValueCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FossilDetailScreen(
    specimenId: String,
    onNavigateBack: () -> Unit,
    onEditSpecimen: (String) -> Unit = {},
    onShareSpecimen: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: FossilDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    
    // Load specimen on first composition
    LaunchedEffect(specimenId) {
        viewModel.loadSpecimen(specimenId)
    }
    
    // Restore scroll position
    LaunchedEffect(uiState.savedScrollPosition) {
        if (uiState.savedScrollPosition > 0) {
            listState.scrollToItem(uiState.savedScrollPosition)
        }
    }
    
    // Show error snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }
    
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Fossil Details",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // Save scroll position before navigating back
                            viewModel.saveScrollPosition(listState.firstVisibleItemIndex)
                            onNavigateBack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleOverflowMenu() }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                    
                    // Overflow menu
                    FossilDetailDropdownMenu(
                        expanded = uiState.showOverflowMenu,
                        onDismiss = { viewModel.setOverflowMenuVisible(false) },
                        onEditClick = {
                            viewModel.setOverflowMenuVisible(false)
                            uiState.specimen?.let { onEditSpecimen(it.id) }
                        },
                        onShareClick = {
                            viewModel.setOverflowMenuVisible(false)
                            uiState.specimen?.let { onShareSpecimen(it.id) }
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.semantics {
            contentDescription = "Fossil detail screen"
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.specimen != null -> {
                if (isTablet) {
                    // Tablet layout - two pane
                    TabletFossilDetailContent(
                        uiState = uiState,
                        viewModel = viewModel,
                        context = context,
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    // Phone layout - single column
                    PhoneFossilDetailContent(
                        uiState = uiState,
                        viewModel = viewModel,
                        listState = listState,
                        context = context,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Specimen not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoneFossilDetailContent(
    uiState: FossilDetailUiState,
    viewModel: FossilDetailViewModel,
    listState: androidx.compose.foundation.lazy.LazyListState,
    context: android.content.Context,
    modifier: Modifier = Modifier
) {
    val specimen = uiState.specimen ?: return
    
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        // Image gallery
        item {
            ImageGallery(
                images = specimen.imageUrls,
                currentIndex = uiState.currentImageIndex,
                onImageClick = { viewModel.toggleImageFullScreen() },
                onImageIndexChange = { viewModel.setCurrentImageIndex(it) }
            )
        }
        
        // Species & classification card
        item {
            SpeciesClassificationCard(
                specimen = specimen,
                onPeriodClick = { /* Handle period info */ },
                onTagClick = { /* Handle tag click */ }
            )
        }

        // Acquisition information card (conditional)
        if (uiState.hasAcquisitionData) {
            item {
                AcquisitionCard(specimen = specimen)
            }
        }

        // Location & discovery card (conditional)
        if (uiState.hasLocationData) {
            item {
                LocationDiscoveryCard(
                    specimen = specimen,
                    onMapClick = {
                        openCoordinatesInMaps(
                            context = context,
                            latitude = specimen.latitude,
                            longitude = specimen.longitude
                        )
                    }
                )
            }
        }
        
        // Physical properties card (conditional)
        if (uiState.hasPhysicalData) {
            item {
                PhysicalPropertiesCard(specimen = specimen)
            }
        }
        
        // Value card (conditional)
        if (uiState.hasValueData) {
            item {
                ValueCard(specimen = specimen)
            }
        }

        // Inventory card (always show as it includes creation date)
        item {
            InventoryCard(specimen = specimen)
        }
    }
}

@Composable
private fun TabletFossilDetailContent(
    uiState: FossilDetailUiState,
    viewModel: FossilDetailViewModel,
    context: android.content.Context,
    modifier: Modifier = Modifier
) {
    val specimen = uiState.specimen ?: return
    
    // TODO: Implement tablet two-pane layout
    // For now, use the same single-column layout
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            ImageGallery(
                images = specimen.imageUrls,
                currentIndex = uiState.currentImageIndex,
                onImageClick = { viewModel.toggleImageFullScreen() },
                onImageIndexChange = { viewModel.setCurrentImageIndex(it) }
            )
        }
        
        item {
            SpeciesClassificationCard(
                specimen = specimen,
                onPeriodClick = { /* Handle period info */ },
                onTagClick = { /* Handle tag click */ }
            )
        }

        if (uiState.hasAcquisitionData) {
            item {
                AcquisitionCard(specimen = specimen)
            }
        }

        if (uiState.hasLocationData) {
            item {
                LocationDiscoveryCard(
                    specimen = specimen,
                    onMapClick = {
                        openCoordinatesInMaps(
                            context = context,
                            latitude = specimen.latitude,
                            longitude = specimen.longitude
                        )
                    }
                )
            }
        }
        
        if (uiState.hasPhysicalData) {
            item {
                PhysicalPropertiesCard(specimen = specimen)
            }
        }
        
        if (uiState.hasValueData) {
            item {
                ValueCard(specimen = specimen)
            }
        }

        item {
            InventoryCard(specimen = specimen)
        }
    }
}

@Composable
private fun FossilDetailDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("Edit fossil") },
            leadingIcon = { 
                Icon(
                    Icons.Default.Edit, 
                    contentDescription = null
                )
            },
            onClick = onEditClick
        )
        
        DropdownMenuItem(
            text = { Text("Share") },
            leadingIcon = { 
                Icon(
                    Icons.Default.Share, 
                    contentDescription = null
                )
            },
            onClick = onShareClick
        )
    }
}

private fun openCoordinatesInMaps(
    context: android.content.Context,
    latitude: Double?,
    longitude: Double?
) {
    if (latitude != null && longitude != null) {
        val geoUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Fallback to any app that can handle geo URIs
            val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri)
            if (fallbackIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(fallbackIntent)
            }
        }
    }
}