package com.dmdev.fossilvaultanda.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.data.models.Specimen
import com.dmdev.fossilvaultanda.data.models.enums.DisplayMode
import com.dmdev.fossilvaultanda.data.models.enums.Period
import com.dmdev.fossilvaultanda.data.models.PeriodToGeologicalTimeMapper
import com.dmdev.fossilvaultanda.data.models.enums.SortOption
import com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
import com.dmdev.fossilvaultanda.data.repository.interfaces.SubscriptionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DatabaseManaging,
    private val subscriptionManager: SubscriptionManager
) : ViewModel() {
    
    // Search and filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    
    private val _selectedPeriod = MutableStateFlow<Period?>(null)
    val selectedPeriod = _selectedPeriod.asStateFlow()
    
    private val _sortOption = MutableStateFlow(SortOption.RECENT)
    val sortOption = _sortOption.asStateFlow()
    
    private val _displayMode = MutableStateFlow(DisplayMode.GRID)
    val displayMode = _displayMode.asStateFlow()
    
    // Base specimens from repository
    private val _specimens: StateFlow<List<Specimen>> = repository.specimens
        .onEach { specimens ->
            Log.d("HomeViewModel", "Specimens updated: ${specimens.size}")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Check if user has any specimens at all
    val hasAnySpecimens: StateFlow<Boolean> = _specimens
        .onEach { specimens ->
            Log.d("HomeViewModel", "hasAnySpecimens updated: ${specimens.isNotEmpty()}")
        }
        .map { specimens -> specimens.isNotEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // Check if any filters are currently active
    val isFiltered: StateFlow<Boolean> = combine(
        searchQuery, selectedPeriod
    ) { query, period ->
        query.isNotBlank() || period != null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Filtered and sorted specimens
    val filteredSpecimens: StateFlow<List<Specimen>> = combine(
        _specimens, searchQuery, selectedPeriod, sortOption
    ) { specimens, query, period, sort ->
        try {
            var filtered = specimens

            // Apply search filter
            if (query.isNotBlank()) {
                filtered = filtered.filter { specimen ->
                    try {
                        specimen.taxonomy.getDisplayName().contains(query, ignoreCase = true) ||
                        specimen.location?.contains(query, ignoreCase = true) == true ||
                        specimen.formation?.contains(query, ignoreCase = true) == true
                    } catch (e: Exception) {
                        Log.e("HomeViewModel", "Error filtering specimen ${specimen.id}: ${e.message}")
                        false
                    }
                }
            }

            // Apply period filter
            if (period != null) {
                filtered = filtered.filter { specimen ->
                    try {
                        val specimenPeriod = PeriodToGeologicalTimeMapper.mapGeologicalPeriodToPeriod(specimen.geologicalTime.period)
                        specimenPeriod == period
                    } catch (e: Exception) {
                        Log.e("HomeViewModel", "Error filtering by period for specimen ${specimen.id}: ${e.message}")
                        false
                    }
                }
            }

            // Apply sorting
            when (sort) {
                SortOption.RECENT -> filtered.sortedByDescending { it.creationDate }
                SortOption.OLDEST -> filtered.sortedBy { it.creationDate }
                SortOption.NAME_A_Z -> filtered.sortedBy { it.taxonomy.getDisplayName().lowercase() }
                SortOption.NAME_Z_A -> filtered.sortedByDescending { it.taxonomy.getDisplayName().lowercase() }
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error in filteredSpecimens combine: ${e.message}")
            emptyList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Actions
    fun updateSearchQuery(query: String) {
        try {
            _searchQuery.value = query.trim()
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error updating search query: ${e.message}")
        }
    }
    
    fun updateSelectedPeriod(period: Period?) {
        _selectedPeriod.value = period
    }
    
    fun updateSortOption(option: SortOption) {
        _sortOption.value = option
    }
    
    fun updateDisplayMode(mode: DisplayMode) {
        _displayMode.value = mode
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
    }

    suspend fun canAddSpecimen(): Boolean {
        return try {
            val currentCount = repository.getSpecimenCount()
            subscriptionManager.canAddSpecimen(currentCount)
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error checking specimen limit: ${e.message}")
            false
        }
    }
    
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedPeriod.value = null
    }
}