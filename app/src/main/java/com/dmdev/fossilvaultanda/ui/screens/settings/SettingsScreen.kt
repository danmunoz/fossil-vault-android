package com.dmdev.fossilvaultanda.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationManager
import com.dmdev.fossilvaultanda.data.models.AppSettings
import com.dmdev.fossilvaultanda.ui.screens.settings.components.ConfigurationSection
import com.dmdev.fossilvaultanda.ui.screens.settings.components.ProfileSection
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {},
    onNavigateToSizeUnitPicker: () -> Unit = {},
    onNavigateToCurrencyPicker: () -> Unit = {},
    authenticationManager: AuthenticationManager? = null,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val profile by viewModel.userProfile.collectAsState()
    val authenticationState by viewModel.authenticationState.collectAsState()
    
    // Handle system back button
    BackHandler {
        onNavigateBack()
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = FossilVaultSpacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sectionVertical)
        ) {
            // Profile Section
            ProfileSection(
                profile = profile,
                authenticationState = authenticationState,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToAuth = onNavigateToAuth,
                onSignOut = viewModel::signOut,
                onDeleteAccount = viewModel::deleteAccount,
                modifier = Modifier.padding(top = FossilVaultSpacing.md)
            )
            
            // Configuration Section
            ConfigurationSection(
                appSettings = profile?.settings ?: AppSettings(),
                authenticationState = authenticationState,
                onDivideCarboniferousChanged = viewModel::updateDivideCarboniferous,
                onNavigateToSizeUnitPicker = onNavigateToSizeUnitPicker,
                onNavigateToCurrencyPicker = onNavigateToCurrencyPicker
            )
            
            // TODO: Add other sections (Data Management, Support & Info)
            // These will be implemented in future tasks
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    FossilVaultTheme {
        SettingsScreen()
    }
}