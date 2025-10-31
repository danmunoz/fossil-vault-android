package com.dmdev.fossilvaultanda.ui.screens.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.Disposition
import com.dmdev.fossilvaultanda.data.models.OwnershipStatus
import com.dmdev.fossilvaultanda.data.models.Price
import com.dmdev.fossilvaultanda.data.models.Specimen
import java.text.NumberFormat
import java.util.Currency
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Card displaying archiving/disposition information for archived specimens.
 * Shows status, date, recipient, price, and notes for sold/traded/gifted/lost specimens.
 * Matches iOS DispositionCard implementation.
 */
@Composable
fun DispositionCard(
    specimen: Specimen,
    modifier: Modifier = Modifier
) {
    val disposition = specimen.disposition ?: return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                contentDescription = "Archiving information"
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Section header
            ListItem(
                headlineContent = {
                    Text(
                        text = "Archiving Information",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Default.Archive,
                        contentDescription = null,
                        tint = Color(0xFFFF9800) // Orange color matching iOS
                    )
                }
            )

            HorizontalDivider()

            // Status
            DispositionInfoRow(
                label = "Status",
                value = getStatusText(disposition.status),
                color = getStatusColor(disposition.status)
            )

            // Date
            disposition.date?.let { date ->
                DispositionInfoRow(
                    label = "Date",
                    value = formatDate(date),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Recipient
            disposition.recipient?.let { recipient ->
                DispositionInfoRow(
                    label = "Recipient",
                    value = recipient,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Price
            disposition.price?.let { price ->
                DispositionInfoRow(
                    label = "Price",
                    value = formatCurrency(price),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Notes (displayed in a separate section with different styling)
            disposition.notes?.let { notes ->
                if (notes.isNotBlank()) {
                    ListItem(
                        headlineContent = {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Notes",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Text(
                                        text = notes,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Notes: $notes"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DispositionInfoRow(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = color
            )
        },
        supportingContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        modifier = modifier.semantics {
            contentDescription = "$label: $value"
        }
    )
}

private fun getStatusText(status: OwnershipStatus): String {
    return when (status) {
        OwnershipStatus.SOLD -> "Sold"
        OwnershipStatus.TRADED -> "Traded"
        OwnershipStatus.GIFTED -> "Gifted"
        OwnershipStatus.LOST -> "Lost"
    }
}

@Composable
private fun getStatusColor(status: OwnershipStatus): Color {
    return when (status) {
        OwnershipStatus.SOLD -> Color(0xFF4CAF50) // Green
        OwnershipStatus.TRADED -> Color(0xFF2196F3) // Blue
        OwnershipStatus.GIFTED -> Color(0xFF9C27B0) // Purple
        OwnershipStatus.LOST -> Color(0xFFFF9800) // Orange
    }
}

private fun formatDate(instant: Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val javaDateTime = localDateTime.toJavaLocalDateTime()
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    return javaDateTime.format(formatter)
}

private fun formatCurrency(price: Price): String {
    return try {
        val formatter = NumberFormat.getCurrencyInstance()
        formatter.currency = Currency.getInstance(price.currency.serializedName.uppercase())
        formatter.format(price.amount)
    } catch (e: Exception) {
        // Fallback to simple formatting if currency is not recognized
        "${price.currency.symbol}${String.format("%.2f", price.amount)}"
    }
}
