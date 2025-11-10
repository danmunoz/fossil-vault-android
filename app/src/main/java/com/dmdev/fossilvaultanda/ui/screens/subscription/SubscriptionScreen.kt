package com.dmdev.fossilvaultanda.ui.screens.subscription

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmdev.fossilvaultanda.ui.screens.subscription.components.PlanComparisonCard
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparePlansScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PlanComparisonViewModel = hiltViewModel()
) {
    val plans by viewModel.plans.collectAsState()
    val showPaywallPlaceholder by viewModel.showPaywallPlaceholder.collectAsState()
    val context = LocalContext.current

    // Show placeholder toast when paywall should be displayed
    LaunchedEffect(showPaywallPlaceholder) {
        if (showPaywallPlaceholder) {
            Toast.makeText(
                context,
                "Subscription purchase coming soon!",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.onPaywallDismissed()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Compare Plans",
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
                .padding(paddingValues)
                .padding(horizontal = FossilVaultSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.md)
        ) {
            // Header section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = FossilVaultSpacing.sm)
                ) {
                    Text(
                        text = "Choose Your Plan",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(FossilVaultSpacing.xs))
                    Text(
                        text = "Upgrade to unlock more features and expand your fossil collection.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Plan cards
            items(plans) { plan ->
                PlanComparisonCard(
                    planData = plan,
                    formatStorageSize = viewModel::formatStorageSize,
                    onSeePricingClick = { viewModel.onSeePricingClicked(plan.tier) }
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(FossilVaultSpacing.md))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ComparePlansScreenPreview() {
    FossilVaultTheme {
        ComparePlansScreen()
    }
}
