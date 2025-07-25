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
                // Show home screen
                HomeScreen(
                    authenticationManager = authenticationManager,
                    onNavigateToSpecimen = { specimenId -> currentSpecimenId = specimenId },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        AuthenticationState.UNAUTHENTICATED -> {
            if (showAuthScreen) {
                AuthenticationScreen(
                    onDismiss = { showAuthScreen = false }
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
                    onDismiss = { showAuthScreen = false }
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