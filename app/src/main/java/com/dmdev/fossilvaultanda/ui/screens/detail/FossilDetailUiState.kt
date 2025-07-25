package com.dmdev.fossilvaultanda.ui.screens.detail

import com.dmdev.fossilvaultanda.data.models.Specimen

data class FossilDetailUiState(
    val isLoading: Boolean = false,
    val specimen: Specimen? = null,
    val error: String? = null,
    val currentImageIndex: Int = 0,
    val isImageFullScreen: Boolean = false,
    val showOverflowMenu: Boolean = false,
    val showBottomSheet: Boolean = false,
    val savedScrollPosition: Int = 0
) {
    val hasLocationData: Boolean
        get() = specimen?.let { 
            !it.location.isNullOrBlank() || 
            !it.formation.isNullOrBlank() || 
            it.latitude != null || 
            it.longitude != null ||
            it.collectionDate != null ||
            it.acquisitionDate != null
        } ?: false
    
    val hasPhysicalData: Boolean
        get() = specimen?.let {
            it.width != null || it.height != null || it.length != null
        } ?: false
    
    val hasValueData: Boolean
        get() = specimen?.let {
            it.pricePaid != null || 
            it.estimatedValue != null || 
            !it.inventoryId.isNullOrBlank() ||
            !it.notes.isNullOrBlank()
        } ?: false
    
    val hasCoordinates: Boolean
        get() = specimen?.let {
            it.latitude != null && it.longitude != null
        } ?: false
    
    val formattedCoordinates: String
        get() = specimen?.let { spec ->
            if (spec.latitude != null && spec.longitude != null) {
                "${String.format("%.6f", spec.latitude)}, ${String.format("%.6f", spec.longitude)}"
            } else ""
        } ?: ""
    
    val dimensionSummary: String
        get() = specimen?.let { spec ->
            val dimensions = listOfNotNull(
                spec.width?.let { "W: ${it}${spec.unit.symbol}" },
                spec.height?.let { "H: ${it}${spec.unit.symbol}" },
                spec.length?.let { "L: ${it}${spec.unit.symbol}" }
            )
            dimensions.joinToString(" × ")
        } ?: ""
}