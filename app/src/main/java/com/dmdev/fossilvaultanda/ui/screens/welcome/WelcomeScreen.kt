package com.dmdev.fossilvaultanda.ui.screens.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.ui.screens.welcome.components.AnimatedLogo
import com.dmdev.fossilvaultanda.ui.screens.welcome.components.CTASection
import com.dmdev.fossilvaultanda.ui.screens.welcome.components.FeatureCardsSection
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun WelcomeScreen(
    onStartBuildingClick: () -> Unit = {},
    onTryWithoutAccountClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = FossilVaultSpacing.screenHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section with logo and branding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.xxl)
            ) {
                Spacer(modifier = Modifier.height(80.dp)) // Top padding as per spec
                
                AnimatedLogo()
                
                FeatureCardsSection()
            }
            
            // Bottom section with call-to-action
            Column {
                Spacer(modifier = Modifier.height(FossilVaultSpacing.xl))
                
                CTASection(
                    onStartBuildingClick = onStartBuildingClick,
                    onTryWithoutAccountClick = onTryWithoutAccountClick
                )
                
                Spacer(modifier = Modifier.height(FossilVaultSpacing.lg))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    FossilVaultTheme {
        WelcomeScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenDarkPreview() {
    FossilVaultTheme(darkTheme = true) {
        WelcomeScreen()
    }
}