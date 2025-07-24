package com.dmdev.fossilvaultanda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dmdev.fossilvaultanda.authentication.ui.screens.AuthenticationScreen
import com.dmdev.fossilvaultanda.ui.screens.welcome.WelcomeScreen
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FossilVaultTheme {
                var showAuthScreen by remember { mutableStateOf(false) }
                
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
                            // TODO: Navigate to main app without account
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
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