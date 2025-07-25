package com.dmdev.fossilvaultanda.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmdev.fossilvaultanda.data.models.enums.Currency
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPickerScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val profile by viewModel.userProfile.collectAsState()
    val currentCurrency = profile?.settings?.defaultCurrency ?: Currency.getDeviceDefault()
    
    // Sort currencies: current selection first, then alphabetically by display name
    val sortedCurrencies = Currency.values().sortedWith { a, b ->
        when {
            a == currentCurrency -> -1
            b == currentCurrency -> 1
            else -> a.displayName.compareTo(b.displayName)
        }
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Default Currency",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.xs)
        ) {
            item {
                Text(
                    text = "Choose the default currency for specimen monetary values",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = FossilVaultSpacing.screenHorizontal)
                        .padding(vertical = FossilVaultSpacing.md)
                )
            }
            
            items(sortedCurrencies) { currency ->
                CurrencyItem(
                    currency = currency,
                    isSelected = currency == currentCurrency,
                    onSelected = { 
                        viewModel.updateDefaultCurrency(currency)
                        onNavigateBack()
                    }
                )
                
                if (currency != sortedCurrencies.last()) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(horizontal = FossilVaultSpacing.screenHorizontal)
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencyItem(
    currency: Currency,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelected() }
            .padding(horizontal = FossilVaultSpacing.screenHorizontal)
            .padding(vertical = FossilVaultSpacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.xs)
        ) {
            Text(
                text = currency.displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${currency.symbol} • ${currency.currencyCode}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CurrencyPickerScreenPreview() {
    FossilVaultTheme {
        CurrencyPickerScreen(
            onNavigateBack = {}
        )
    }
}