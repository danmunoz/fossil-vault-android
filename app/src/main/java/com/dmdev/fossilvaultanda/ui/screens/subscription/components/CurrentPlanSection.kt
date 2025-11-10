package com.dmdev.fossilvaultanda.ui.screens.subscription.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.enums.SubscriptionTier
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Section displaying the current subscription plan with icon, name, and benefits.
 */
@Composable
fun CurrentPlanSection(
    tier: SubscriptionTier,
    renewalDate: Instant?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Plan name
            Text(
                text = tier.displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle with renewal info
            Text(
                text = getSubtitleText(tier, renewalDate),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Plan Benefits
            Text(
                text = "Plan Benefits",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Benefits list
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                getPlanBenefits(tier).forEach { benefit ->
                    BenefitRow(benefit = benefit)
                }
            }
        }
    }
}

/**
 * Individual benefit row with checkmark icon.
 */
@Composable
private fun BenefitRow(
    benefit: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = benefit,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Get the icon for a subscription tier.
 */
private fun getPlanIcon(tier: SubscriptionTier): ImageVector {
    return when (tier) {
        SubscriptionTier.FREE -> Icons.Default.Person
        SubscriptionTier.PRO -> Icons.Default.Star
        SubscriptionTier.MAX -> Icons.Default.WorkspacePremium
    }
}

/**
 * Get the subtitle text for the plan section.
 */
private fun getSubtitleText(tier: SubscriptionTier, renewalDate: Instant?): String {
    return when {
        tier == SubscriptionTier.FREE -> "Free forever"
        renewalDate != null -> {
            val localDateTime = renewalDate.toLocalDateTime(TimeZone.currentSystemDefault())
            val javaLocalDateTime = localDateTime.toJavaLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
            "Renews on ${javaLocalDateTime.format(formatter)}"
        }
        else -> "Active subscription"
    }
}

/**
 * Get the list of benefits for a subscription tier.
 */
private fun getPlanBenefits(tier: SubscriptionTier): List<String> {
    return when (tier) {
        SubscriptionTier.FREE -> listOf(
            "Up to 15 specimens",
            "5 photos per specimen",
            "500 MB storage",
            "Basic collection management",
            "CSV export"
        )
        SubscriptionTier.PRO -> listOf(
            "Unlimited specimens",
            "10 photos per specimen",
            "5 GB storage",
            "Advanced search & filters",
            "Priority support",
            "CSV & ZIP export"
        )
        SubscriptionTier.MAX -> listOf(
            "Unlimited specimens",
            "20 photos per specimen",
            "15 GB storage",
            "Advanced search & filters",
            "Priority support",
            "CSV & ZIP export",
            "Exclusive features"
        )
    }
}
