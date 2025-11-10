package com.dmdev.fossilvaultanda.ui.screens.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.enums.Currency
import com.dmdev.fossilvaultanda.ui.screens.stats.CollectionHighlights
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Card component displaying collection highlights
 */
@Composable
fun CollectionHighlightsCard(
    highlights: CollectionHighlights?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FossilVaultSpacing.lg)
        ) {
            // Title
            Text(
                text = "FossilVault Highlights",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Subtitle
            Text(
                text = "A few interesting things we've noticed about your collection...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = FossilVaultSpacing.xs)
            )

            Spacer(modifier = Modifier.height(FossilVaultSpacing.lg))

            // Highlights list
            if (highlights == null) {
                Text(
                    text = "Add specimens to see highlights",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.md)
                ) {
                    // First acquired
                    highlights.firstAcquired?.let { item ->
                        HighlightRow(
                            icon = Icons.Default.Flag,
                            title = "First specimen acquired: ${item.speciesName}",
                            subtitle = "on ${formatDate(item.date)}"
                        )
                    }

                    // Most recent acquisition
                    highlights.mostRecentAcquisition?.let { item ->
                        HighlightRow(
                            icon = Icons.Default.CalendarToday,
                            title = "Most recent acquisition: ${item.speciesName}",
                            subtitle = "on ${formatDate(item.date)}"
                        )
                    }

                    // Most common period
                    highlights.mostCommonPeriod?.let { period ->
                        HighlightRow(
                            icon = Icons.Default.Layers,
                            title = "Most common period: ${period.periodName}",
                            subtitle = "(${period.count} specimen${if (period.count != 1) "s" else ""})"
                        )
                    }

                    // Location with most finds
                    highlights.topLocation?.let { location ->
                        HighlightRow(
                            icon = Icons.Default.Public,
                            title = "Location with most finds: ${location.country}",
                            subtitle = "(${location.count} fossil${if (location.count != 1) "s" else ""})"
                        )
                    }

                    // Most valuable specimen
                    highlights.mostValuable?.let { valuable ->
                        HighlightRow(
                            icon = Icons.Default.Diamond,
                            title = "Most valuable specimen: ${valuable.speciesName}",
                            subtitle = "- ${formatCurrency(valuable.value, valuable.currency)}"
                        )
                    }

                    // Total spent vs value (multiple currencies)
                    if (highlights.spentVsValue.isNotEmpty()) {
                        highlights.spentVsValue.entries.sortedByDescending { it.value.estimated }.forEach { (_, data) ->
                            HighlightRow(
                                icon = Icons.Default.AccountBalance,
                                title = "Total spent vs value:",
                                subtitle = "${formatCurrency(data.spent, data.currency)} spent\n${formatCurrency(data.estimated, data.currency)} estimated"
                            )
                        }
                    }

                    // Longest gap between fossils
                    highlights.longestGap?.let { days ->
                        HighlightRow(
                            icon = Icons.Default.Schedule,
                            title = "Longest gap between fossils: ${formatGap(days)}",
                            subtitle = null
                        )
                    }
                }
            }
        }
    }
}

/**
 * Highlight row with icon and text
 */
@Composable
private fun HighlightRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(FossilVaultSpacing.md))

        // Text content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Format an Instant to a localized date string
 */
private fun formatDate(instant: Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val javaDateTime = localDateTime.toJavaLocalDateTime()
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    return javaDateTime.format(formatter)
}

/**
 * Format currency value
 */
private fun formatCurrency(value: Double, currency: Currency): String {
    return try {
        val formatter = NumberFormat.getCurrencyInstance()
        formatter.currency = java.util.Currency.getInstance(currency.serializedName.uppercase())
        formatter.format(value)
    } catch (e: Exception) {
        // Fallback to simple formatting if currency is not recognized
        "${currency.symbol}${String.format("%.2f", value)}"
    }
}

/**
 * Format gap in days to readable format
 */
private fun formatGap(days: Int): String {
    return when {
        days < 30 -> "$days days"
        days < 365 -> {
            val months = days / 30
            "$months month${if (months != 1) "s" else ""}"
        }
        else -> {
            val years = days / 365
            val remainingMonths = (days % 365) / 30
            if (remainingMonths > 0) {
                "$years year${if (years != 1) "s" else ""}, $remainingMonths month${if (remainingMonths != 1) "s" else ""}"
            } else {
                "$years year${if (years != 1) "s" else ""}"
            }
        }
    }
}
