package com.dmdev.fossilvaultanda.ui.screens.subscription

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmdev.fossilvaultanda.ui.screens.subscription.components.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Main subscription status screen showing current plan, usage stats, and management options.
 * Matches the iOS SubscriptionStatusView implementation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionStatusScreen(
    onNavigateBack: () -> Unit,
    onNavigateToComparePlans: () -> Unit,
    viewModel: SubscriptionStatusViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Show error snackbar if there's an error
    if (uiState.error != null) {
        LaunchedEffect(uiState.error) {
            // Error handling could show a snackbar
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Current Plan Section
                CurrentPlanSection(
                    tier = uiState.tier,
                    renewalDate = uiState.renewalDate
                )

                // Usage Stats Section
                Text(
                    text = "Usage Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                UsageProgressCard(
                    title = "Specimens",
                    current = uiState.currentSpecimenCount,
                    limit = uiState.specimenLimit
                )

                StorageUsageProgressCard(
                    current = uiState.currentStorageUsage,
                    limit = uiState.storageLimit
                )

                // Management Section
                Text(
                    text = "Manage Subscription",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Compare Plans card
                SubscriptionCard(
                    title = "Compare Plans",
                    subtitle = "See all available subscription tiers",
                    icon = Icons.AutoMirrored.Filled.CompareArrows,
                    onClick = onNavigateToComparePlans
                )

                // Next Billing Date card (only show if there's a renewal date)
                uiState.renewalDate?.let { date ->
                    SubscriptionCard(
                        title = "Next Billing Date",
                        subtitle = formatRenewalDate(date),
                        icon = Icons.Default.CalendarToday,
                        enabled = false
                    )
                }

                // Spacer at bottom for better scrolling
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Formats the renewal date for display.
 */
private fun formatRenewalDate(date: Instant): String {
    val localDateTime = date.toLocalDateTime(TimeZone.currentSystemDefault())
    val javaLocalDateTime = localDateTime.toJavaLocalDateTime()
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return javaLocalDateTime.format(formatter)
}
