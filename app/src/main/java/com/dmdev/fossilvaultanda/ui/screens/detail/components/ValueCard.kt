package com.dmdev.fossilvaultanda.ui.screens.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
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
import com.dmdev.fossilvaultanda.data.models.Specimen
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun ValueCard(
    specimen: Specimen,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                contentDescription = "Value information"
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
                        text = "Value",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            HorizontalDivider()

            // Price paid
            specimen.pricePaid?.let { pricePaid ->
                val currency = specimen.pricePaidCurrency
                val formattedPrice = formatCurrency(pricePaid, currency)

                ListItem(
                    headlineContent = {
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "Price paid",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Price paid: $formattedPrice"
                    }
                )
            }

            // Estimated value
            specimen.estimatedValue?.let { estimatedValue ->
                val currency = specimen.estimatedValueCurrency
                val formattedValue = formatCurrency(estimatedValue, currency)

                // Calculate appreciation if both values exist
                val appreciationColor = if (specimen.pricePaid != null) {
                    when {
                        estimatedValue > specimen.pricePaid -> Color(0xFF4CAF50) // Green for appreciation
                        estimatedValue < specimen.pricePaid -> Color(0xFFF44336) // Red for depreciation
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    }
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                }

                ListItem(
                    headlineContent = {
                        Text(
                            text = formattedValue,
                            style = MaterialTheme.typography.bodyLarge,
                            color = appreciationColor
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "Estimated value",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Estimated value: $formattedValue"
                    }
                )
            }
        }
    }
}

private fun formatCurrency(
    amount: Double,
    currencyEnum: com.dmdev.fossilvaultanda.data.models.enums.Currency?
): String {
    return try {
        val currency = currencyEnum?.let { Currency.getInstance(it.currencyCode) }
        val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
        if (currency != null) {
            formatter.currency = currency
        }
        formatter.format(amount)
    } catch (e: Exception) {
        // Fallback formatting
        val symbol = currencyEnum?.symbol ?: "$"
        "$symbol${String.format("%.2f", amount)}"
    }
}