package com.dmdev.fossilvaultanda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.dmdev.fossilvaultanda.ui.screens.specimen.ElementPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.specimen.LocationPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.specimen.PeriodPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.specimen.SimpleCurrencyPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.specimen.SimpleSizeUnitPickerScreen
import com.dmdev.fossilvaultanda.ui.screens.welcome.WelcomeScreen
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var authenticationManager: AuthenticationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FossilVaultTheme {
                MainContent(authenticationManager = authenticationManager)
            }
        }
    }
}

@Composable
fun MainContent(authenticationManager: AuthenticationManager) {
    val authState by authenticationManager.authenticationState.collectAsState()
    var showAuthScreen by remember { mutableStateOf(false) }
    var currentSpecimenId by remember { mutableStateOf<String?>(null) }
    var currentScreen by remember { mutableStateOf("home") } // home, settings, profile, editProfile, sizeUnitPicker, currencyPicker, addSpecimen, periodPicker, elementPicker, locationPicker
    var addSpecimenViewModel: AddSpecimenViewModel? by remember { mutableStateOf(null) }
    var selectedPeriod by remember { mutableStateOf<com.dmdev.fossilvaultanda.data.models.enums.Period?>(null) }
    var selectedElement by remember { mutableStateOf<com.dmdev.fossilvaultanda.data.models.enums.FossilElement?>(null) }
    var customElementText by remember { mutableStateOf("") }
    var currencyPickerContext by remember { mutableStateOf("") } // "price" or "value"
    val coroutineScope = rememberCoroutineScope()
    
    when (authState) {
        AuthenticationState.AUTHENTICATED, AuthenticationState.LOCAL_USER -> {
            // Show detail screen if a specimen is selected
            currentSpecimenId?.let { specimenId ->
                FossilDetailScreen(
                    specimenId = specimenId,
                    onNavigateBack = { currentSpecimenId = null },
                    onEditSpecimen = { /* TODO: Implement edit navigation */ },
                    onShareSpecimen = { /* TODO: Implement share functionality */ },
                    modifier = Modifier.fillMaxSize()
                )
            } ?: run {
                // Navigate between main screens
                when (currentScreen) {
                    "home" -> {
                        HomeScreen(
                            authenticationManager = authenticationManager,
                            onNavigateToSpecimen = { specimenId -> currentSpecimenId = specimenId },
                            onNavigateToSettings = { currentScreen = "settings" },
                            onAddSpecimen = { currentScreen = "addSpecimen" },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "settings" -> {
                        SettingsScreen(
                            onNavigateBack = { currentScreen = "home" },
                            onNavigateToProfile = { currentScreen = "profile" },
                            onNavigateToAuth = { showAuthScreen = true },
                            onNavigateToSizeUnitPicker = { currentScreen = "sizeUnitPicker" },
                            onNavigateToCurrencyPicker = { currentScreen = "currencyPicker" },
                            authenticationManager = authenticationManager,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "profile" -> {
                        ProfileScreen(
                            onNavigateBack = { currentScreen = "settings" },
                            onNavigateToEditProfile = { currentScreen = "editProfile" },
                            onNavigateToAuth = { showAuthScreen = true },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "editProfile" -> {
                        EditProfileScreen(
                            onNavigateBack = { currentScreen = "profile" },
                            onSave = { currentScreen = "profile" },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "sizeUnitPicker" -> {
                        if (addSpecimenViewModel != null) {
                            // Size unit picker for Add Specimen screen
                            SimpleSizeUnitPickerScreen(
                                onNavigateBack = { currentScreen = "addSpecimen" },
                                onSizeUnitSelected = { sizeUnit ->
                                    addSpecimenViewModel?.updateSizeUnit(sizeUnit)
                                    currentScreen = "addSpecimen"
                                },
                                selectedSizeUnit = addSpecimenViewModel?.formState?.value?.unit,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Original size unit picker for Settings
                            SizeUnitPickerScreen(
                                onNavigateBack = { currentScreen = "settings" },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    "currencyPicker" -> {
                        if (currencyPickerContext.isNotEmpty()) {
                            // Currency picker for Add Specimen screen - use a simple list for now
                            SimpleCurrencyPickerScreen(
                                onNavigateBack = { 
                                    currentScreen = "addSpecimen"
                                    currencyPickerContext = ""
                                },
                                onCurrencySelected = { currency ->
                                    when (currencyPickerContext) {
                                        "price" -> addSpecimenViewModel?.updatePricePaid(
                                            addSpecimenViewModel?.formState?.value?.pricePaid, 
                                            currency
                                        )
                                        "value" -> addSpecimenViewModel?.updateEstimatedValue(
                                            addSpecimenViewModel?.formState?.value?.estimatedValue, 
                                            currency
                                        )
                                    }
                                    currentScreen = "addSpecimen"
                                    currencyPickerContext = ""
                                },
                                selectedCurrency = when (currencyPickerContext) {
                                    "price" -> addSpecimenViewModel?.formState?.value?.pricePaidCurrency
                                    "value" -> addSpecimenViewModel?.formState?.value?.estimatedValueCurrency
                                    else -> null
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Original currency picker for Settings
                            CurrencyPickerScreen(
                                onNavigateBack = { currentScreen = "settings" },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    "addSpecimen" -> {
                        val viewModel: AddSpecimenViewModel = hiltViewModel()
                        addSpecimenViewModel = viewModel // Store the ViewModel reference
                        
                        AddSpecimenScreen(
                            onNavigateBack = { 
                                currentScreen = "home"
                                addSpecimenViewModel = null // Clear ViewModel when leaving
                            },
                            onNavigateToPeriodPicker = { 
                                selectedPeriod = viewModel.formState.value.period
                                currentScreen = "periodPicker" 
                            },
                            onNavigateToElementPicker = { 
                                selectedElement = viewModel.formState.value.element
                                customElementText = viewModel.formState.value.customElement
                                currentScreen = "elementPicker" 
                            },
                            onNavigateToSizeUnitPicker = { currentScreen = "sizeUnitPicker" },
                            onNavigateToCurrencyPicker = { context -> 
                                currencyPickerContext = context
                                currentScreen = "currencyPicker" 
                            },
                            onNavigateToLocationPicker = { currentScreen = "locationPicker" },
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "periodPicker" -> {
                        PeriodPickerScreen(
                            onNavigateBack = { currentScreen = "addSpecimen" },
                            onPeriodSelected = { period ->
                                addSpecimenViewModel?.updatePeriod(period)
                                selectedPeriod = period
                                currentScreen = "addSpecimen" // Navigate back immediately
                            },
                            selectedPeriod = selectedPeriod,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "elementPicker" -> {
                        ElementPickerScreen(
                            onNavigateBack = { currentScreen = "addSpecimen" },
                            onElementSelected = { element, customText ->
                                addSpecimenViewModel?.updateElement(element)
                                // TODO: Handle custom element text properly
                                selectedElement = element
                                customElementText = customText
                                currentScreen = "addSpecimen" // Navigate back immediately
                            },
                            selectedElement = selectedElement,
                            customElementText = customElementText,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    "locationPicker" -> {
                        LocationPickerScreen(
                            onNavigateBack = { currentScreen = "addSpecimen" },
                            onLocationSelected = { locationData ->
                                addSpecimenViewModel?.updateLocation(
                                    location = locationData.address,
                                    latitude = locationData.latitude,
                                    longitude = locationData.longitude
                                )
                                currentScreen = "addSpecimen"
                            },
                            initialLocation = addSpecimenViewModel?.formState?.value?.let { formState ->
                                if (formState.latitude != null && formState.longitude != null) {
                                    com.dmdev.fossilvaultanda.ui.screens.specimen.LocationData(
                                        latitude = formState.latitude,
                                        longitude = formState.longitude,
                                        address = formState.location
                                    )
                                } else null
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        AuthenticationState.UNAUTHENTICATED -> {
            if (showAuthScreen) {
                AuthenticationScreen(
                    onDismiss = { 
                        showAuthScreen = false
                        currentScreen = "home"
                    }
                )
            } else {
                WelcomeScreen(
                    onStartBuildingClick = {
                        showAuthScreen = true
                    },
                    onTryWithoutAccountClick = {
                        // Sign in as anonymous user
                        coroutineScope.launch {
                            try {
                                authenticationManager.anonymousSignIn()
                            } catch (e: Exception) {
                                // Handle error if needed
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        AuthenticationState.AUTHENTICATING -> {
            // Show loading or keep current screen
            if (showAuthScreen) {
                AuthenticationScreen(
                    onDismiss = { 
                        showAuthScreen = false
                        currentScreen = "home"
                    }
                )
            } else {
                WelcomeScreen(
                    onStartBuildingClick = {
                        showAuthScreen = true
                    },
                    onTryWithoutAccountClick = {
                        // Sign in as anonymous user
                        coroutineScope.launch {
                            try {
                                authenticationManager.anonymousSignIn()
                            } catch (e: Exception) {
                                // Handle error if needed
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    FossilVaultTheme {
        WelcomeScreen()
    }
}