package com.dmdev.fossilvaultanda.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationState
import com.dmdev.fossilvaultanda.data.models.AppSettings
import com.dmdev.fossilvaultanda.data.models.enums.Currency
import com.dmdev.fossilvaultanda.data.models.enums.SizeUnit
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun ConfigurationSection(
    appSettings: AppSettings,
    authenticationState: AuthenticationState,
    onDivideCarboniferousChanged: (Boolean) -> Unit,
    onNavigateToSizeUnitPicker: () -> Unit,
    onNavigateToCurrencyPicker: () -> Unit,
    onNavigateToImportCsv: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isAuthenticated = authenticationState == AuthenticationState.AUTHENTICATED || authenticationState == AuthenticationState.LOCAL_USER
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm)
    ) {
        // Section Header
        Text(
            text = "Configuration",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = FossilVaultSpacing.xs)
        )
        
        // Divide Carboniferous Period Toggle
        SettingToggleItem(
            title = "Divide Carboniferous Period",
            description = "Show Mississippian and Pennsylvanian as separate periods",
            checked = appSettings.divideCarboniferous,
            enabled = isAuthenticated,
            isLocked = !isAuthenticated,
            onCheckedChange = onDivideCarboniferousChanged
        )
        
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(vertical = FossilVaultSpacing.xs)
        )
        
        // Measurement Unit Setting
        SettingNavigationItem(
            title = "Measurement Unit",
            subtitle = "Default unit for specimen dimensions",
            value = if (isAuthenticated) appSettings.unit.displayName else null,
            enabled = isAuthenticated,
            isLocked = !isAuthenticated,
            onClick = onNavigateToSizeUnitPicker
        )
        
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(vertical = FossilVaultSpacing.xs)
        )
        
        // Default Currency Setting
        SettingNavigationItem(
            title = "Default Currency",
            subtitle = "Currency for monetary values",
            value = if (isAuthenticated) "${appSettings.defaultCurrency.symbol} ${appSettings.defaultCurrency.displayName}" else null,
            enabled = isAuthenticated,
            isLocked = !isAuthenticated,
            onClick = onNavigateToCurrencyPicker
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(vertical = FossilVaultSpacing.xs)
        )

        // Import from CSV
        SettingNavigationItem(
            title = "Import from CSV",
            subtitle = "Import specimens from a CSV file",
            enabled = isAuthenticated,
            isLocked = !isAuthenticated,
            onClick = onNavigateToImportCsv
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConfigurationSectionPreview() {
    FossilVaultTheme {
        Column(
            modifier = Modifier.padding(FossilVaultSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.lg)
        ) {
            // Authenticated state
            ConfigurationSection(
                appSettings = AppSettings(
                    unit = SizeUnit.MM,
                    divideCarboniferous = true,
                    defaultCurrency = Currency.USD
                ),
                authenticationState = AuthenticationState.AUTHENTICATED,
                onDivideCarboniferousChanged = {},
                onNavigateToSizeUnitPicker = {},
                onNavigateToCurrencyPicker = {}
            )
            
            HorizontalDivider()
            
            // Anonymous state
            ConfigurationSection(
                appSettings = AppSettings(),
                authenticationState = AuthenticationState.UNAUTHENTICATED,
                onDivideCarboniferousChanged = {},
                onNavigateToSizeUnitPicker = {},
                onNavigateToCurrencyPicker = {}
            )
        }
    }
}