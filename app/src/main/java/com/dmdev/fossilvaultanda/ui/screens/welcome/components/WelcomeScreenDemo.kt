package com.dmdev.fossilvaultanda.ui.screens.welcome.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.dmdev.fossilvaultanda.ui.screens.welcome.WelcomeScreen
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

class ThemePreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true)
}

@Preview(
    name = "Welcome Screen - Light",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun WelcomeScreenLightPreview() {
    FossilVaultTheme(darkTheme = false) {
        WelcomeScreen()
    }
}

@Preview(
    name = "Welcome Screen - Dark",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun WelcomeScreenDarkPreview() {
    FossilVaultTheme(darkTheme = true) {
        WelcomeScreen()
    }
}

@Preview(
    name = "Welcome Screen - Landscape",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=891dp,height=411dp,orientation=landscape"
)
@Composable
fun WelcomeScreenLandscapePreview() {
    FossilVaultTheme {
        WelcomeScreen()
    }
}

@Preview(
    name = "Welcome Screen - Small Phone",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=320dp,height=568dp"
)
@Composable
fun WelcomeScreenSmallPreview() {
    FossilVaultTheme {
        WelcomeScreen()
    }
}

@Preview(
    name = "Welcome Screen - Large Phone",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=428dp,height=926dp"
)
@Composable
fun WelcomeScreenLargePreview() {
    FossilVaultTheme {
        WelcomeScreen()
    }
}

@Preview(name = "Components Showcase", showBackground = true)
@Composable
fun ComponentsShowcasePreview() {
    FossilVaultTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(FossilVaultSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.xl)
            ) {
                Text(
                    text = "FossilVault Components",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.md)
                    ) {
                        AnimatedLogo()
                    }
                }
                
                FeatureCardsSection()
                
                CTASection()
            }
        }
    }
}