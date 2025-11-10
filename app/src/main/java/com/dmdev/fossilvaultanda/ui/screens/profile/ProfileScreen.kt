package com.dmdev.fossilvaultanda.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.dmdev.fossilvaultanda.ui.screens.profile.components.ActionCardsSection
import com.dmdev.fossilvaultanda.ui.screens.profile.components.AccountManagementSection
import com.dmdev.fossilvaultanda.ui.screens.profile.components.ProfileHeaderSection
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.userProfile.collectAsState()
    val authenticationState by viewModel.authenticationState.collectAsState()
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
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
            Spacer(modifier = Modifier.height(FossilVaultSpacing.md))
            
            // Profile Header
            ProfileHeaderSection(
                profile = profile,
                authenticationState = authenticationState
            )
            
            // Action Cards
            ActionCardsSection(
                authenticationState = authenticationState,
                subscriptionBadge = profile?.subscriptionStatus?.tier?.displayName?.uppercase(),
                onNavigateToEditProfile = onNavigateToEditProfile,
                onSubscriptionClick = onNavigateToSubscription
            )
            
            // Account Management
            AccountManagementSection(
                authenticationState = authenticationState,
                onCreateAccountClick = onNavigateToAuth,
                onSignOutClick = viewModel::signOut,
                onDeleteAccountClick = viewModel::deleteAccount
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    FossilVaultTheme {
        ProfileScreen()
    }
}