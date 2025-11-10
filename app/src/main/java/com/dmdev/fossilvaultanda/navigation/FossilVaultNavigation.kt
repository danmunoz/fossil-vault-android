package com.dmdev.fossilvaultanda.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationManager
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationState
import com.dmdev.fossilvaultanda.authentication.ui.screens.AuthenticationScreen
import com.dmdev.fossilvaultanda.ui.screens.detail.FossilDetailScreen
import com.dmdev.fossilvaultanda.ui.screens.home.HomeScreen
import com.dmdev.fossilvaultanda.ui.screens.profile.EditProfileScreen
import com.dmdev.fossilvaultanda.ui.screens.profile.ProfileScreen
import com.dmdev.fossilvaultanda.ui.screens.settings.CurrencyPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.settings.SettingsScreen
import com.dmdev.fossilvaultanda.ui.screens.settings.SizeUnitPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.specimen.AddSpecimenScreen
import com.dmdev.fossilvaultanda.ui.screens.specimen.AddSpecimenViewModel
import com.dmdev.fossilvaultanda.ui.screens.specimen.AdvancedGeologicalTimePickerScreen
import com.dmdev.fossilvaultanda.ui.screens.specimen.CountryPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.specimen.ElementPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.specimen.LocationPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.specimen.PeriodPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.specimen.SimpleCurrencyPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.specimen.SimpleSizeUnitPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.stats.StatsScreen
import com.dmdev.fossilvaultanda.ui.screens.subscription.ComparePlansScreen
import com.dmdev.fossilvaultanda.ui.screens.subscription.LimitReachedScreen
import com.dmdev.fossilvaultanda.ui.screens.subscription.SubscriptionStatusScreen
import com.dmdev.fossilvaultanda.ui.screens.support.AboutScreen
import com.dmdev.fossilvaultanda.ui.screens.support.FAQScreen
import com.dmdev.fossilvaultanda.ui.screens.welcome.WelcomeScreen
import kotlinx.coroutines.launch

@Composable
fun FossilVaultNavigation(
    authenticationManager: AuthenticationManager,
    navController: NavHostController = rememberNavController()
) {
    val authState by authenticationManager.authenticationState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    // Determine start destination based on auth state
    val startDestination = when (authState) {
        AuthenticationState.AUTHENTICATED, AuthenticationState.LOCAL_USER -> FossilVaultRoute.Home
        AuthenticationState.UNAUTHENTICATED -> FossilVaultRoute.Welcome
        AuthenticationState.AUTHENTICATING -> FossilVaultRoute.Welcome
    }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Welcome & Authentication Flow
        composable<FossilVaultRoute.Welcome> {
            WelcomeScreen(
                onStartBuildingClick = {
                    navController.navigate(FossilVaultRoute.Authentication)
                },
                onTryWithoutAccountClick = {
                    coroutineScope.launch {
                        try {
                            authenticationManager.anonymousSignIn()
                            navController.navigate(FossilVaultRoute.Home) {
                                popUpTo(FossilVaultRoute.Welcome) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            // Handle error if needed
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        composable<FossilVaultRoute.Authentication> {
            AuthenticationScreen(
                onDismiss = {
                    if (authState == AuthenticationState.AUTHENTICATED || authState == AuthenticationState.LOCAL_USER) {
                        navController.navigate(FossilVaultRoute.Home) {
                            popUpTo(FossilVaultRoute.Welcome) { inclusive = true }
                        }
                    } else {
                        navController.navigateUp()
                    }
                }
            )
        }
        
        // Main App Flow
        composable<FossilVaultRoute.Home> {
            HomeScreen(
                authenticationManager = authenticationManager,
                onNavigateToSpecimen = { specimenId ->
                    navController.navigate(FossilVaultRoute.FossilDetail(specimenId))
                },
                onNavigateToSettings = {
                    navController.navigate(FossilVaultRoute.Settings)
                },
                onNavigateToStats = {
                    navController.navigate(FossilVaultRoute.Stats)
                },
                onNavigateToProfile = {
                    navController.navigate(FossilVaultRoute.Profile)
                },
                onSignOut = {
                    coroutineScope.launch {
                        authenticationManager.signOut()
                        navController.navigate(FossilVaultRoute.Welcome) {
                            popUpTo(FossilVaultRoute.Home) { inclusive = true }
                        }
                    }
                },
                onDeleteAccount = {
                    coroutineScope.launch {
                        authenticationManager.deleteAccount()
                        navController.navigate(FossilVaultRoute.Welcome) {
                            popUpTo(FossilVaultRoute.Home) { inclusive = true }
                        }
                    }
                },
                onAddSpecimen = {
                    navController.navigate(FossilVaultRoute.AddSpecimen())
                },
                onEditSpecimen = { specimenId ->
                    navController.navigate(FossilVaultRoute.AddSpecimen(specimenId = specimenId, isDuplicate = false))
                },
                onDuplicateSpecimen = { specimenId ->
                    navController.navigate(FossilVaultRoute.AddSpecimen(specimenId = specimenId, isDuplicate = true))
                },
                onNavigateToLimitReached = {
                    navController.navigate(FossilVaultRoute.LimitReached)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<FossilVaultRoute.Stats> {
            StatsScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<FossilVaultRoute.FossilDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<FossilVaultRoute.FossilDetail>()
            FossilDetailScreen(
                specimenId = route.specimenId,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onEditSpecimen = { specimenId ->
                    navController.navigate(FossilVaultRoute.AddSpecimen(specimenId))
                },
                onShareSpecimen = { /* TODO: Implement share functionality */ },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Settings Flow
        composable<FossilVaultRoute.Settings> {
            SettingsScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToProfile = {
                    navController.navigate(FossilVaultRoute.Profile)
                },
                onNavigateToAuth = {
                    navController.navigate(FossilVaultRoute.Authentication)
                },
                onNavigateToSizeUnitPicker = {
                    navController.navigate(FossilVaultRoute.SizeUnitPicker)
                },
                onNavigateToCurrencyPicker = {
                    navController.navigate(FossilVaultRoute.CurrencyPicker)
                },
                onNavigateToImportCsv = {
                    navController.navigate(FossilVaultRoute.ImportCsv)
                },
                onNavigateToFAQ = {
                    navController.navigate(FossilVaultRoute.FAQ)
                },
                onNavigateToAbout = {
                    navController.navigate(FossilVaultRoute.About)
                },
                authenticationManager = authenticationManager,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        composable<FossilVaultRoute.Profile> {
            ProfileScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToEditProfile = {
                    navController.navigate(FossilVaultRoute.EditProfile)
                },
                onNavigateToSubscription = {
                    navController.navigate(FossilVaultRoute.SubscriptionStatus)
                },
                onNavigateToAuth = {
                    navController.navigate(FossilVaultRoute.Authentication)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        composable<FossilVaultRoute.EditProfile> {
            EditProfileScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onSave = {
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        composable<FossilVaultRoute.SizeUnitPicker> {
            SizeUnitPickerScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        composable<FossilVaultRoute.CurrencyPicker> {
            CurrencyPickerScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Support & Info Flow
        composable<FossilVaultRoute.FAQ> {
            FAQScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<FossilVaultRoute.About> {
            AboutScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Subscription Flow
        composable<FossilVaultRoute.SubscriptionStatus> {
            SubscriptionStatusScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToComparePlans = {
                    navController.navigate(FossilVaultRoute.Subscription)
                }
            )
        }

        composable<FossilVaultRoute.Subscription> {
            ComparePlansScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<FossilVaultRoute.LimitReached> {
            LimitReachedScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // CSV Import Flow
        composable<FossilVaultRoute.ImportCsv> {
            com.dmdev.fossilvaultanda.ui.screens.csvimport.ImportScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onImportComplete = {
                    navController.navigate(FossilVaultRoute.Home) {
                        popUpTo(FossilVaultRoute.Home) { inclusive = false }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Add Specimen Flow
        composable<FossilVaultRoute.AddSpecimen> { backStackEntry ->
            val route = backStackEntry.toRoute<FossilVaultRoute.AddSpecimen>()
            val viewModel: AddSpecimenViewModel = hiltViewModel()

            // Initialize based on mode - only on first composition
            LaunchedEffect(route.specimenId, route.isDuplicate) {
                route.specimenId?.let { specimenId ->
                    if (route.isDuplicate) {
                        viewModel.initializeForDuplicate(specimenId)
                    } else {
                        viewModel.initializeForEdit(specimenId)
                    }
                }
            }
            
            AddSpecimenScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToPeriodPicker = {
                    navController.navigate(FossilVaultRoute.PeriodPicker)
                },
                onNavigateToAdvancedGeologicalTimePicker = {
                    navController.navigate(FossilVaultRoute.AdvancedGeologicalTimePicker)
                },
                onNavigateToElementPicker = {
                    navController.navigate(FossilVaultRoute.ElementPicker)
                },
                onNavigateToSizeUnitPicker = {
                    navController.navigate(FossilVaultRoute.SimpleSizeUnitPicker)
                },
                onNavigateToCurrencyPicker = { context ->
                    navController.navigate(FossilVaultRoute.SimpleCurrencyPicker(context))
                },
                onNavigateToLocationPicker = {
                    navController.navigate(FossilVaultRoute.LocationPicker)
                },
                onNavigateToCountryPicker = {
                    navController.navigate(FossilVaultRoute.CountryPicker)
                },
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        composable<FossilVaultRoute.PeriodPicker> {
            val parentEntry = remember(navController) {
                navController.currentBackStack.value.first { entry ->
                    entry.destination.route?.contains("AddSpecimen") == true
                }
            }
            val addSpecimenViewModel: AddSpecimenViewModel = hiltViewModel(parentEntry)
            
            PeriodPickerScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onPeriodSelected = { period ->
                    addSpecimenViewModel.updatePeriod(period)
                    navController.navigateUp()
                },
                selectedPeriod = addSpecimenViewModel.getCurrentSelectedPeriod(),
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<FossilVaultRoute.AdvancedGeologicalTimePicker> {
            val parentEntry = remember(navController) {
                navController.currentBackStack.value.first { entry ->
                    entry.destination.route?.contains("AddSpecimen") == true
                }
            }
            val addSpecimenViewModel: AddSpecimenViewModel = hiltViewModel(parentEntry)
            val formState = addSpecimenViewModel.formState.collectAsState().value

            AdvancedGeologicalTimePickerScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onGeologicalTimeSelected = { geologicalTime ->
                    addSpecimenViewModel.updateGeologicalTime(geologicalTime)
                    navController.navigateUp()
                },
                initialGeologicalTime = formState.geologicalTime,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<FossilVaultRoute.ElementPicker> {
            val parentEntry = remember(navController) {
                navController.currentBackStack.value.first { entry ->
                    entry.destination.route?.contains("AddSpecimen") == true
                }
            }
            val addSpecimenViewModel: AddSpecimenViewModel = hiltViewModel(parentEntry)
            val formState = addSpecimenViewModel.formState.collectAsState().value
            
            ElementPickerScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onElementSelected = { element, customText ->
                    addSpecimenViewModel.updateElement(element)
                    if (element == com.dmdev.fossilvaultanda.data.models.enums.FossilElement.OTHER) {
                        addSpecimenViewModel.updateCustomElement(customText)
                    }
                    navController.navigateUp()
                },
                selectedElement = formState.element,
                customElementText = formState.customElement,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        composable<FossilVaultRoute.LocationPicker> {
            LocationPickerScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onLocationSelected = { locationData ->
                    // TODO: Pass result back to AddSpecimen screen
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<FossilVaultRoute.CountryPicker> {
            val parentEntry = remember(navController) {
                navController.currentBackStack.value.first { entry ->
                    entry.destination.route?.contains("AddSpecimen") == true
                }
            }
            val addSpecimenViewModel: AddSpecimenViewModel = hiltViewModel(parentEntry)
            val formState = addSpecimenViewModel.formState.collectAsState().value

            CountryPickerScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onCountrySelected = { countryCode ->
                    addSpecimenViewModel.updateCountry(countryCode)
                    navController.navigateUp()
                },
                selectedCountry = formState.country,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<FossilVaultRoute.SimpleCurrencyPicker> { backStackEntry ->
            val route = backStackEntry.toRoute<FossilVaultRoute.SimpleCurrencyPicker>()
            SimpleCurrencyPickerScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onCurrencySelected = { currency ->
                    // TODO: Pass result back to AddSpecimen screen
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        composable<FossilVaultRoute.SimpleSizeUnitPicker> {
            SimpleSizeUnitPickerScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onSizeUnitSelected = { sizeUnit ->
                    // TODO: Pass result back to AddSpecimen screen
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}