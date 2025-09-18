package com.dmdev.fossilvaultanda.ui.screens.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.Specimen

@Composable
fun PhysicalPropertiesCard(
    specimen: Specimen,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                contentDescription = "Physical properties and measurements"
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
                        text = "Physical Properties",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Default.Straighten,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            
            HorizontalDivider()
            
            // Summary row if multiple dimensions exist
            val dimensions = listOfNotNull(
                specimen.length?.let { "L: ${it}${specimen.unit.symbol}" },
                specimen.width?.let { "W: ${it}${specimen.unit.symbol}" },
                specimen.height?.let { "H: ${it}${specimen.unit.symbol}" }
            )
            
            if (dimensions.size > 1) {
                ListItem(
                    headlineContent = { 
                        Text(
                            text = dimensions.joinToString(" × "),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    supportingContent = { 
                        Text(
                            text = "Overall dimensions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.Straighten, 
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Overall dimensions: ${dimensions.joinToString(" by ")}"
                    }
                )
            }
            
            // Individual measurements
            specimen.length?.let { length ->
                DimensionListItem(
                    label = "Length",
                    value = length,
                    unit = specimen.unit.symbol,
                    icon = Icons.Default.Height
                )
            }

            specimen.width?.let { width ->
                DimensionListItem(
                    label = "Width",
                    value = width,
                    unit = specimen.unit.symbol,
                    icon = Icons.Default.SwapHoriz
                )
            }

            specimen.height?.let { height ->
                DimensionListItem(
                    label = "Height",
                    value = height,
                    unit = specimen.unit.symbol,
                    icon = Icons.Default.SwapVert
                )
            }
        }
    }
}

@Composable
private fun DimensionListItem(
    label: String,
    value: Double,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { 
            Text(
                text = "$value$unit",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = { 
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        leadingContent = {
            Icon(
                icon, 
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        modifier = modifier.semantics {
            contentDescription = "$label: $value $unit"
        }
    )
}