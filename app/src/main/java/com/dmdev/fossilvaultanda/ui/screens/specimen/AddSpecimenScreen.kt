package com.dmdev.fossilvaultanda.ui.screens.specimen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmdev.fossilvaultanda.data.models.enums.Currency
import com.dmdev.fossilvaultanda.data.models.enums.FossilElement
import com.dmdev.fossilvaultanda.data.models.enums.Period
import com.dmdev.fossilvaultanda.data.models.enums.SizeUnit
import com.fossilVault.geological.GeologicalTime
import com.dmdev.fossilvaultanda.ui.screens.specimen.components.DatePickerField
import com.dmdev.fossilvaultanda.ui.screens.specimen.components.DimensionInputRow
import com.dmdev.fossilvaultanda.ui.screens.specimen.components.GeologicalTimeSelectionField
import com.dmdev.fossilvaultanda.ui.screens.specimen.components.ImagePickerManager
import com.dmdev.fossilvaultanda.ui.screens.specimen.components.PhotoGrid
import com.dmdev.fossilvaultanda.ui.screens.specimen.components.SectionHeader
import com.dmdev.fossilvaultanda.ui.screens.specimen.components.SelectionField
import com.dmdev.fossilvaultanda.ui.screens.specimen.components.ValidatedNumericField
import com.dmdev.fossilvaultanda.ui.screens.specimen.components.ValidatedTextField
import com.dmdev.fossilvaultanda.ui.theme.Dimensions
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpecimenScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPeriodPicker: () -> Unit,
    onNavigateToAdvancedGeologicalTimePicker: () -> Unit,
    onNavigateToElementPicker: () -> Unit,
    onNavigateToSizeUnitPicker: () -> Unit,
    onNavigateToCurrencyPicker: (String) -> Unit, // "price" or "value"
    onNavigateToLocationPicker: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddSpecimenViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val validationErrors by viewModel.validationErrors.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showEditConfirmationDialog by remember { mutableStateOf(false) }

    // Handle save success/error
    LaunchedEffect(uiState.saveSuccess, uiState.saveError) {
        if (uiState.saveSuccess) {
            onNavigateBack()
            viewModel.clearSaveState()
        }
        uiState.saveError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearSaveState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditMode) "Edit Specimen" else "Add New Specimen",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel"
                        )
                    }
                },
                actions = {
                    if (uiState.isSaving) {
                        Box(
                            modifier = Modifier.padding(end = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                        }
                    } else {
                        Button(
                            onClick = { 
                                if (uiState.isEditMode) {
                                    showEditConfirmationDialog = true
                                } else {
                                    viewModel.saveSpecimen()
                                }
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(if (uiState.isEditMode) "Update" else "Save")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensions.medium)
                .imePadding()
        ) {
            Spacer(modifier = Modifier.height(Dimensions.medium))

            // Photo Section
            PhotoSection(
                images = formState.imageUrls,
                onImagesChanged = viewModel::updateImages,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.large))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Dimensions.large))

            // Basic Information Section
            BasicInformationSection(
                species = formState.species,
                geologicalTime = formState.geologicalTime,
                element = formState.element,
                customElement = formState.customElement,
                onSpeciesChange = viewModel::updateSpecies,
                onGeologicalTimeClick = onNavigateToAdvancedGeologicalTimePicker,
                onElementClick = onNavigateToElementPicker,
                validationErrors = validationErrors,
                userProfile = userProfile,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.large))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Dimensions.large))

            // Location & Date Section
            LocationSection(
                location = formState.location,
                formation = formState.formation,
                latitude = formState.latitude,
                longitude = formState.longitude,
                collectionDate = formState.collectionDate,
                acquisitionDate = formState.acquisitionDate,
                onLocationChange = viewModel::updateLocation,
                onFormationChange = viewModel::updateFormation,
                onLocationPickerClick = onNavigateToLocationPicker,
                onCollectionDateChange = viewModel::updateCollectionDate,
                onAcquisitionDateChange = viewModel::updateAcquisitionDate,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.large))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Dimensions.large))

            // Dimensions Section
            DimensionsSection(
                width = formState.width,
                height = formState.height,
                length = formState.length,
                unit = formState.unit,
                onDimensionsChange = viewModel::updateDimensions,
                onUnitClick = onNavigateToSizeUnitPicker,
                validationErrors = validationErrors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.large))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Dimensions.large))

            // Value Information Section
            ValueSection(
                pricePaid = formState.pricePaid,
                pricePaidCurrency = formState.pricePaidCurrency,
                estimatedValue = formState.estimatedValue,
                estimatedValueCurrency = formState.estimatedValueCurrency,
                onPricePaidChange = { price -> viewModel.updatePricePaid(price, formState.pricePaidCurrency) },
                onEstimatedValueChange = { value -> viewModel.updateEstimatedValue(value, formState.estimatedValueCurrency) },
                onPriceCurrencyClick = { onNavigateToCurrencyPicker("price") },
                onValueCurrencyClick = { onNavigateToCurrencyPicker("value") },
                validationErrors = validationErrors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.large))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Dimensions.large))

            // Additional Details Section
            AdditionalDetailsSection(
                inventoryId = formState.inventoryId,
                notes = formState.notes,
                tags = formState.tagNames,
                isFavorite = formState.isFavorite,
                isPublic = formState.isPublic,
                onInventoryIdChange = viewModel::updateInventoryId,
                onNotesChange = viewModel::updateNotes,
                onTagsChange = viewModel::updateTags,
                onFavoriteChange = viewModel::updateIsFavorite,
                onPublicChange = viewModel::updateIsPublic,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimensions.xxLarge))
        }
    }
    
    // Edit Confirmation Dialog
    if (showEditConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showEditConfirmationDialog = false },
            title = { Text("Confirm Changes") },
            text = { Text("Do you want to save the changes to this specimen?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEditConfirmationDialog = false
                        viewModel.saveSpecimen()
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditConfirmationDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PhotoSection(
    images: List<com.dmdev.fossilvaultanda.data.models.StoredImage>,
    onImagesChanged: (List<com.dmdev.fossilvaultanda.data.models.StoredImage>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "Photos",
            icon = Icons.Default.Camera,
            iconColor = Color(0xFF2196F3) // Blue
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        // Photo grid with image picker
        ImagePickerManager(
            maxImages = 3,
            currentImages = images,
            onImagesSelected = onImagesChanged
        ) { openPicker ->
            PhotoGrid(
                images = images,
                maxImages = 3,
                onAddPhotos = openPicker,
                onRemoveImage = { imageToRemove ->
                    onImagesChanged(images.filter { it != imageToRemove })
                }
            )
        }
    }
}

@Composable
private fun BasicInformationSection(
    species: String,
    geologicalTime: GeologicalTime,
    element: FossilElement,
    customElement: String,
    onSpeciesChange: (String) -> Unit,
    onGeologicalTimeClick: () -> Unit,
    onElementClick: () -> Unit,
    validationErrors: Map<String, String>,
    userProfile: com.dmdev.fossilvaultanda.data.models.UserProfile?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "Basic Information",
            icon = Icons.Default.Info,
            iconColor = Color(0xFF4CAF50) // Green
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        ValidatedTextField(
            value = species,
            onValueChange = onSpeciesChange,
            label = "Species",
            placeholder = "e.g., Tyrannosaurus Rex",
            isRequired = true,
            errorMessage = validationErrors["species"],
            imeAction = ImeAction.Next
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        GeologicalTimeSelectionField(
            geologicalTime = geologicalTime,
            onClick = onGeologicalTimeClick,
            isRequired = true,
            errorMessage = validationErrors["geologicalTime"]
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        SelectionField(
            value = if (element != FossilElement.OTHER) element.displayString else customElement,
            label = "Anatomical Element",
            placeholder = "Select element",
            isRequired = true,
            errorMessage = validationErrors["element"],
            onClick = onElementClick
        )
    }
}

@Composable
private fun LocationSection(
    location: String,
    formation: String,
    latitude: Double?,
    longitude: Double?,
    collectionDate: kotlinx.datetime.Instant?,
    acquisitionDate: kotlinx.datetime.Instant?,
    onLocationChange: (String) -> Unit,
    onFormationChange: (String) -> Unit,
    onLocationPickerClick: () -> Unit,
    onCollectionDateChange: (kotlinx.datetime.Instant?) -> Unit,
    onAcquisitionDateChange: (kotlinx.datetime.Instant?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "Location & Date",
            icon = Icons.Default.LocationOn,
            iconColor = Color(0xFFFF9800) // Orange
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        ValidatedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = "Location",
            placeholder = "e.g., Hell Creek Formation, Montana",
            imeAction = ImeAction.Next
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        ValidatedTextField(
            value = formation,
            onValueChange = onFormationChange,
            label = "Geological Formation",
            placeholder = "e.g., Morrison Formation",
            imeAction = ImeAction.Next
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        // GPS Coordinates (simplified - would show actual coordinates if set)
        OutlinedButton(
            onClick = onLocationPickerClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (latitude != null && longitude != null) {
                    "GPS: ${String.format("%.6f", latitude)}, ${String.format("%.6f", longitude)}"
                } else {
                    "Set GPS Location"
                }
            )
        }
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        // Date pickers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DatePickerField(
                selectedDate = collectionDate,
                onDateSelected = onCollectionDateChange,
                label = "Collection Date",
                placeholder = "When was it found?",
                modifier = Modifier.weight(1f)
            )
            
            DatePickerField(
                selectedDate = acquisitionDate,
                onDateSelected = onAcquisitionDateChange,
                label = "Acquisition Date",
                placeholder = "When was it acquired?",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DimensionsSection(
    width: Double?,
    height: Double?,
    length: Double?,
    unit: SizeUnit,
    onDimensionsChange: (Double?, Double?, Double?) -> Unit,
    onUnitClick: () -> Unit,
    validationErrors: Map<String, String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "Dimensions",
            icon = Icons.Default.Straighten,
            iconColor = Color(0xFF9C27B0) // Purple
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        DimensionInputRow(
            width = width,
            height = height,
            length = length,
            unit = unit.symbol,
            onWidthChange = { onDimensionsChange(it, height, length) },
            onHeightChange = { onDimensionsChange(width, it, length) },
            onLengthChange = { onDimensionsChange(width, height, it) },
            widthError = validationErrors["width"],
            heightError = validationErrors["height"],
            lengthError = validationErrors["length"]
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        FilledTonalButton(
            onClick = onUnitClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Unit: ${unit.displayName}")
        }
    }
}

@Composable
private fun ValueSection(
    pricePaid: Double?,
    pricePaidCurrency: Currency?,
    estimatedValue: Double?,
    estimatedValueCurrency: Currency?,
    onPricePaidChange: (Double?) -> Unit,
    onEstimatedValueChange: (Double?) -> Unit,
    onPriceCurrencyClick: () -> Unit,
    onValueCurrencyClick: () -> Unit,
    validationErrors: Map<String, String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "Value Information",
            icon = Icons.Default.AttachMoney,
            iconColor = Color(0xFF9C27B0) // Purple
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        // Price Paid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            ValidatedNumericField(
                value = pricePaid,
                onValueChange = onPricePaidChange,
                label = "Price Paid",
                modifier = Modifier.weight(1f),
                errorMessage = validationErrors["pricePaid"]
            )
            
            FilledTonalButton(
                onClick = onPriceCurrencyClick,
                modifier = Modifier.width(80.dp)
            ) {
                Text(pricePaidCurrency?.symbol ?: "USD")
            }
        }
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        // Estimated Value
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            ValidatedNumericField(
                value = estimatedValue,
                onValueChange = onEstimatedValueChange,
                label = "Estimated Value",
                modifier = Modifier.weight(1f),
                errorMessage = validationErrors["estimatedValue"]
            )
            
            FilledTonalButton(
                onClick = onValueCurrencyClick,
                modifier = Modifier.width(80.dp)
            ) {
                Text(estimatedValueCurrency?.symbol ?: "USD")
            }
        }
    }
}

@Composable
private fun AdditionalDetailsSection(
    inventoryId: String,
    notes: String,
    tags: List<String>,
    isFavorite: Boolean,
    isPublic: Boolean,
    onInventoryIdChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onTagsChange: (List<String>) -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    onPublicChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "Additional Details",
            icon = Icons.Default.Description,
            iconColor = Color(0xFF607D8B) // Gray
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        ValidatedTextField(
            value = inventoryId,
            onValueChange = onInventoryIdChange,
            label = "Inventory ID",
            placeholder = "e.g., TREX-001"
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        ValidatedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = "Notes",
            placeholder = "Additional observations or details...",
            singleLine = false,
            maxLines = 4,
            imeAction = ImeAction.Default
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        // TODO: Implement tag management
        if (tags.isNotEmpty()) {
            Text(
                text = "Tags: ${tags.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // TODO: Add favorite and public toggles
    }
}

@Preview(showBackground = true)
@Composable
fun AddSpecimenScreenPreview() {
    FossilVaultTheme {
        AddSpecimenScreen(
            onNavigateBack = { },
            onNavigateToPeriodPicker = { },
            onNavigateToAdvancedGeologicalTimePicker = { },
            onNavigateToElementPicker = { },
            onNavigateToSizeUnitPicker = { },
            onNavigateToCurrencyPicker = { },
            onNavigateToLocationPicker = { }
        )
    }
}