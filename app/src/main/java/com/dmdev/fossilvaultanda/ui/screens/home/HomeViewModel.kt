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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DatabaseManaging
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
    
    // Filtered and sorted specimens
    val filteredSpecimens: StateFlow<List<Specimen>> = combine(
        _specimens, searchQuery, selectedPeriod, sortOption
    ) { specimens, query, period, sort ->
        var filtered = specimens
        
        // Apply search filter
        if (query.isNotBlank()) {
            filtered = filtered.filter { specimen ->
                specimen.species.contains(query, ignoreCase = true) ||
                specimen.location?.contains(query, ignoreCase = true) == true ||
                specimen.formation?.contains(query, ignoreCase = true) == true
            }
        }
        
        // Apply period filter
        if (period != null) {
            filtered = filtered.filter { specimen ->
                val specimenPeriod = PeriodToGeologicalTimeMapper.mapGeologicalPeriodToPeriod(specimen.geologicalTime.period)
                specimenPeriod == period
            }
        }
        
        // Apply sorting
        when (sort) {
            SortOption.RECENT -> filtered.sortedByDescending { it.creationDate }
            SortOption.OLDEST -> filtered.sortedBy { it.creationDate }
            SortOption.NAME_A_Z -> filtered.sortedBy { it.species.lowercase() }
            SortOption.NAME_Z_A -> filtered.sortedByDescending { it.species.lowercase() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Actions
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
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
    
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedPeriod.value = null
    }
}