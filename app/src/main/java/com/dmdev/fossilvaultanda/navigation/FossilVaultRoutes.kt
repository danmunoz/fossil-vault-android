package com.dmdev.fossilvaultanda.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for FossilVault app
 * Using Kotlin Serialization for type safety and parameter passing
 */
@Serializable
sealed class FossilVaultRoute {
    
    // Welcome & Authentication Flow
    @Serializable
    data object Welcome : FossilVaultRoute()
    
    @Serializable
    data object Authentication : FossilVaultRoute()
    
    // Main App Flow
    @Serializable
    data object Home : FossilVaultRoute()

    @Serializable
    data object Stats : FossilVaultRoute()

    @Serializable
    data class FossilDetail(val specimenId: String) : FossilVaultRoute()
    
    // Settings Flow
    @Serializable
    data object Settings : FossilVaultRoute()
    
    @Serializable
    data object Profile : FossilVaultRoute()
    
    @Serializable
    data object EditProfile : FossilVaultRoute()
    
    @Serializable
    data object SizeUnitPicker : FossilVaultRoute()
    
    @Serializable
    data object CurrencyPicker : FossilVaultRoute()

    // Support & Info Flow
    @Serializable
    data object FAQ : FossilVaultRoute()

    @Serializable
    data object About : FossilVaultRoute()

    // Subscription Flow
    @Serializable
    data object Subscription : FossilVaultRoute()

    @Serializable
    data object LimitReached : FossilVaultRoute()

    // CSV Import Flow
    @Serializable
    data object ImportCsv : FossilVaultRoute()

    // Add Specimen Flow
    @Serializable
    data class AddSpecimen(
        val specimenId: String? = null,
        val isDuplicate: Boolean = false
    ) : FossilVaultRoute()
    
    @Serializable
    data object PeriodPicker : FossilVaultRoute()

    @Serializable
    data object AdvancedGeologicalTimePicker : FossilVaultRoute()

    @Serializable
    data object ElementPicker : FossilVaultRoute()
    
    @Serializable
    data object LocationPicker : FossilVaultRoute()

    @Serializable
    data object CountryPicker : FossilVaultRoute()

    @Serializable
    data class SimpleCurrencyPicker(val context: String) : FossilVaultRoute()

    @Serializable
    data object SimpleSizeUnitPicker : FossilVaultRoute()
}

/**
 * Top-level navigation graphs for different app flows
 */
@Serializable
sealed class NavigationGraph {
    @Serializable
    data object Auth : NavigationGraph()
    
    @Serializable
    data object Main : NavigationGraph()
    
    @Serializable 
    data object Settings : NavigationGraph()
    
    @Serializable
    data object AddSpecimen : NavigationGraph()
}